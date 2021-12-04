package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.ChannelSubscriptionsDao
import ru.debajo.reader.rss.data.db.model.DbChannelSubscription
import ru.debajo.reader.rss.data.db.model.DbDateTime
import timber.log.Timber

class ChannelsSubscriptionsRepository(
    private val dao: ChannelSubscriptionsDao
) {
    suspend fun toggle(url: String) {
        withContext(IO) {
            val isSubscribed = isSubscribed(url).first()
            if (isSubscribed) {
                dao.remove(url)
            } else {
                dao.add(DbChannelSubscription(url, DbDateTime.now()))
            }
        }
    }

    fun isSubscribed(url: String): Flow<Boolean> {
        return dao.observeByUrl(url).map { it.isNotEmpty() }
    }

    fun loadData(dataLoader: suspend () -> Int): Flow<State> {
        return flow {
            emit(State.Loading)

            try {
                emit(State.Data(dataLoader()))
            } catch (_: Throwable) {
                emit(State.Error)
            }
        }
    }

    sealed interface State {
        object Loading : State
        object Error : State
        class Data(val yopta: Int) : State
    }


    fun observe(): Flow<List<String>> {
        return dao.observeSubscriptions()
            .map { list ->
                val urls = list.map { it.url }
                urls.forEach { Timber.tag("Subscription").d(it) }
                urls
            }
            .flowOn(IO)
    }
}