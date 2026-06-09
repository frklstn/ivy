package com.ivy.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val supabaseClient: SupabaseClient,
) {

    val authState: kotlinx.coroutines.flow.Flow<AuthState> = supabaseClient.auth.sessionStatus.map { status ->
        if (status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated) {
            AuthState.Authenticated(status.session.accessToken)
        } else {
            AuthState.Unauthenticated
        }
    }

    val currentUser: UserInfo?
        get() = supabaseClient.auth.currentUserOrNull()

    val isAuthenticated: Boolean
        get() = supabaseClient.auth.currentSessionOrNull() != null

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        supabaseClient.auth.signOut()
    }
}

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data class Authenticated(val accessToken: String) : AuthState
}
