-dontwarn java.beans.**
-dontwarn javax.script.**
-dontwarn javax.servlet.**
-dontwarn org.apache.**
-dontwarn coil.**
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn com.google.errorprone.**
-dontwarn org.slf4j.impl.**

# Ktor - abaikan class yang tidak ditemukan R8 saat minifikasi release
-dontwarn io.ktor.client.network.sockets.**
-dontwarn io.ktor.client.plugins.HttpTimeout
-dontwarn io.ktor.client.plugins.HttpTimeout$**
-dontwarn io.ktor.client.plugins.observer.**
-dontwarn io.ktor.util.KtorDsl
-dontwarn io.ktor.utils.io.CoroutinesKt
-dontwarn io.ktor.utils.io.core.Input

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>