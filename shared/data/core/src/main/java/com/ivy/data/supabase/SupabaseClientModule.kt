package com.ivy.data.supabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.supabase.SupabaseClient
import io.supabase.auth.Auth
import io.supabase.createSupabaseClient
import io.supabase.postgrest.Postgrest
import javax.inject.Singleton

/**
 * TODO: Replace these with your actual Supabase project credentials.
 * For production, move these to local.properties or environment variables.
 */
private const val SUPABASE_URL = "https://kortbujyuafwdiqxsiok.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtvcnRidWp5dWFmd2RpcXhzaW9rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzkwODE2MjksImV4cCI6MjA5NDY1NzYyOX0.gceG3ophu4c4UVFbfFnUMgJVqTI_UbVde5tYPXv8UBQ"

@Module
@InstallIn(SingletonComponent::class)
object SupabaseClientModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}
