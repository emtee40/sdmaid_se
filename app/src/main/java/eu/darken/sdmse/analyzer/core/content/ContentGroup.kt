package eu.darken.sdmse.analyzer.core.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

interface ContentGroup {
    val id: Id
    val contents: Collection<ContentItem>

    val groupSize: Long
        get() = contents.sumOf { it.size }

    @Parcelize
    data class Id(val value: String = UUID.randomUUID().toString()) : Parcelable
}
