package com.ivy.data.db.entity

import androidx.annotation.Keep
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

// TODO: Drop this database table
@Keep
@Serializable
@Deprecated("Legacy table. Must be dropped")
data class UserEntity(
    @SerialName("email")
    val email: String,
    @SerialName("authProviderType")
    val authProviderType: String,
    @SerialName("firstName")
    var firstName: String,
    @SerialName("lastName")
    val lastName: String?,
    @SerialName("profilePicture")
    val profilePicture: String?,
    @SerialName("color")
    val color: Int,

    @SerialName("testUser")
    val testUser: Boolean = false,

    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    var id: UUID
)
