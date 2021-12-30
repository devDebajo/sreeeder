package ru.debajo.reader.rss.data.dump

import be.ceau.opml.OpmlWriter
import be.ceau.opml.entity.Body
import be.ceau.opml.entity.Head
import be.ceau.opml.entity.Opml
import be.ceau.opml.entity.Outline
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.data.db.dao.ChannelsDao

class OpmlDumper(
    private val channelsDao: ChannelsDao
) {
    suspend fun dump(): String {
        val opml = Opml("1.0", createHead(), Body(createOutlines()))
        return OpmlWriter().write(opml)
    }

    private suspend fun createOutlines(): List<Outline> {
        val channels = channelsDao.getAllSubscribed()
        return channels.map { channel ->
            Outline(
                mapOf(
                    "type" to "rss",
                    "title" to channel.name,
                    "description" to channel.description,
                    "xmlUrl" to channel.url,
                ),
                emptyList()
            )
        }
    }

    private fun createHead(): Head {
        return Head(
            "Sreeeder OPML RSS feeds dump",
            DateTime.now(DateTimeZone.UTC).toString(),
            DateTime.now(DateTimeZone.UTC).toString(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}
