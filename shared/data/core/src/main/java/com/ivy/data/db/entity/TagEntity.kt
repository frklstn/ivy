package com.ivy.data.db.entity

import androidx.annotation.Keep
import com.ivy.base.kotlinxserilzation.KSerializerInstant
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Keep
@Serializable
data class TagEntity(
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID,

    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String?,

    @SerialName("color")
    val color: Int,
    @SerialName("icon")
    val icon: String?,
    @SerialName("orderNum")
    val orderNum: Double,

    @SerialName("creationTime")
    @Serializable(with = KSerializerInstant::class)
    val dateTime: Instant,

    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isDeleted")
    val isDeleted: Boolean,

    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("lastSyncTime")
    @Serializable(with = KSerializerInstant::class)
    val lastSyncedTime: Instant
)
