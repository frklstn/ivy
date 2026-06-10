plugins {
    id("ivy.feature")
    id("ivy.integration.testing")
}

android {
    namespace = "com.ivy.data"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val supabaseUrl: String = (project.findProperty("SUPABASE_URL") ?: "").toString()
        val supabaseKey: String = (project.findProperty("SUPABASE_ANON_KEY") ?: "").toString()

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseKey\"")
    }
}

dependencies {
    implementation(projects.shared.base)
    api(projects.shared.data.model)

    api(libs.datastore)
    implementation(libs.bundles.ktor)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.kt)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)

    testImplementation(projects.shared.data.modelTesting)
    androidTestImplementation(libs.bundles.integration.testing)
}
