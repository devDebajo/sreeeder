package ru.debajo.reader.rss.ui.main.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.main.model.ChromeTabsParams
import timber.log.Timber

object ChromeTabsLauncher {

    fun launch(context: Context, data: ChromeTabsParams) {
        val defaultColors = CustomTabColorSchemeParams.Builder()
            .apply {
                if (data.toolbarColor != null) {
                    setToolbarColor(data.toolbarColor)
                }
            }
            .build()
        val intent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(defaultColors)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setInstantAppsEnabled(true)
            .setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
            .setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .build()

        runCatching { intent.launchUrl(context, Uri.parse(data.url)) }
            .onFailure {
                Timber.tag("ChromeTabsLauncher").e(it)
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW).setData(Uri.parse(data.url)),
                        context.getString(R.string.open_in)
                    )
                )
            }
    }
}
