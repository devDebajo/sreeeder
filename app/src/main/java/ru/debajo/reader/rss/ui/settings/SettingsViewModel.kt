package ru.debajo.reader.rss.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.data.dump.FileSaver
import ru.debajo.reader.rss.data.dump.OpmlDumper
import ru.debajo.reader.rss.data.preferences.BackgroundUpdatesEnabledPreference
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesScheduler
import ru.debajo.reader.rss.domain.channel.SubscribeChannelsListUseCase
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.metrics.AnalyticsEnabledManager
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.arch.SingleLiveEvent
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
class SettingsViewModel(
    private val context: Context,
    private val analytics: Analytics,
    private val appThemeProvider: AppThemeProvider,
    private val backgroundUpdatesEnabledPreference: BackgroundUpdatesEnabledPreference,
    private val backgroundUpdatesScheduler: BackgroundUpdatesScheduler,
    private val analyticsEnabledManager: AnalyticsEnabledManager,
    private val fileSaver: FileSaver,
    private val opmlDumper: OpmlDumper,
    private val subscribeChannelsListUseCase: SubscribeChannelsListUseCase,
) : BaseViewModel() {

    private val stateMutable: MutableStateFlow<SettingsState> = MutableStateFlow(
        SettingsState(
            supportDynamicTheme = appThemeProvider.supportDynamicTheme(),
        )
    )
    val state: StateFlow<SettingsState> = stateMutable

    private val exportOpmlClickEventMutable: MutableLiveData<String> = SingleLiveEvent()
    private val importOpmlClickEventMutable: MutableLiveData<Unit> = SingleLiveEvent()
    private val snackBarMutable: MutableStateFlow<SnackbarState> = MutableStateFlow(SnackbarState("", false))

    val importOpmlClickEvent: LiveData<Unit> = importOpmlClickEventMutable
    val exportOpmlClickEvent: LiveData<String> = exportOpmlClickEventMutable
    val snackBar: StateFlow<SnackbarState> = snackBarMutable

    init {
        launch {
            appThemeProvider.currentAppThemeConfig.collect { config ->
                stateMutable.value = stateMutable.value.copy(
                    appTheme = config.theme,
                    isDynamicColor = config.dynamic,
                    backgroundUpdates = backgroundUpdatesEnabledPreference.get(),
                    analyticsEnabled = analyticsEnabledManager.isEnabled(),
                )
            }
        }
    }

    fun onExportOpmlClick() {
        exportOpmlClickEventMutable.value = fileSaver.createFileName("dump", "opml")
    }

    fun onImportOpmlClick() {
        importOpmlClickEventMutable.value = Unit
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        updateState {
            launch { analyticsEnabledManager.setEnabled(enabled) }
            copy(analyticsEnabled = enabled)
        }
    }

    fun toggleAnalyticsAlert(visible: Boolean) {
        updateState {
            copy(showAnalyticsAlertDialog = visible)
        }
    }

    fun toggleDynamicColor() {
        launch {
            appThemeProvider.update(!stateMutable.value.isDynamicColor)
        }
    }

    fun toggleThemeDropDown() {
        updateState { copy(dropDownExpanded = !dropDownExpanded) }
    }

    fun selectTheme(theme: AppTheme) {
        updateState { copy(dropDownExpanded = false) }
        launch { appThemeProvider.update(theme) }
    }

    fun toggleBackgroundUpdates() {
        updateState {
            val newBackgroundUpdates = !backgroundUpdates
            analytics.setBackgroundUpdatesToggleState(newBackgroundUpdates)

            launch(IO) {
                // TODO переделать (сокрыть backgroundUpdatesEnabledPreference в backgroundUpdatesScheduler)
                backgroundUpdatesEnabledPreference.set(newBackgroundUpdates)
                backgroundUpdatesScheduler.rescheduleOrCancel()
            }

            copy(backgroundUpdates = newBackgroundUpdates)
        }
    }

    fun writeOpmlDump(uri: Uri) {
        analytics.onExportOpml()
        launch(IO) {
            runCatching {
                val xml = opmlDumper.dump()
                fileSaver.writeFile(uri, xml)
            }
                .onSuccess { showSnackBar(R.string.settings_export_opml_success) }
                .onFailure {
                    Timber.tag("SettingsViewModel").e(it)
                    showSnackBar(R.string.settings_export_opml_fail)
                }
        }
    }

    fun readOpmlDump(uri: Uri) {
        if (state.value.importing) {
            return
        }
        analytics.onImportOpml()
        updateState { copy(importing = true) }
        launch(IO) {
            runCatching { importOpmlInternal(uri) }
                .onFailure {
                    Timber.tag("SettingsViewModel").e(it)
                    updateState { copy(importing = false) }
                    showSnackBar(R.string.settings_import_opml_fail)
                }
                .onSuccess { success ->
                    updateState { copy(importing = false) }
                    if (success) {
                        showSnackBar(R.string.settings_import_opml_success)
                    } else {
                        showSnackBar(R.string.settings_import_opml_fail)
                    }
                }
        }
    }

    private suspend fun importOpmlInternal(uri: Uri): Boolean {
        val fileRaw = fileSaver.readFileRaw(uri) ?: return false
        val channelsUrls = opmlDumper.parse(fileRaw).map { DomainChannelUrl(it) }
        subscribeChannelsListUseCase.subscribe(channelsUrls)
        return true
    }

    private suspend fun showSnackBar(messageRes: Int) {
        val message = context.getString(messageRes)
        val snackbar = SnackbarState(message)
        snackBarMutable.value = snackbar
        delay(2000)
        snackBarMutable.value = snackbar.copy(visible = false)
    }

    private inline fun updateState(block: SettingsState.() -> SettingsState) {
        stateMutable.value = stateMutable.value.block()
    }

    data class SnackbarState(val message: String, val visible: Boolean = true)
}
