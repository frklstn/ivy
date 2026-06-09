package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.UserEntity
import java.util.*

@Deprecated("No longer needed, must be removed.")
interface UserDao {
    suspend fun save(user: UserEntity)
    suspend fun findById(userId: UUID): UserEntity?
    suspend fun deleteAll()
}
