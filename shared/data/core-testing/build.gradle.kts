plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.data.testing"
}

dependencies {
    implementation(projects.shared.data.core)
}
