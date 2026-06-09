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
data class TagAssociationEntity(
    @SerialName("tagId")
    @Serializable(with = KSerializerUUID::class)
    val tagId: UUID,

    @SerialName("associatedId")
    @Serializable(with = KSerializerUUID::class)
    val associatedId: UUID,

    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("lastSyncTime")
    @Serializable(with = KSerializerInstant::class)
    val lastSyncedTime: Instant,

    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isDeleted")
    val isDeleted: Boolean
)