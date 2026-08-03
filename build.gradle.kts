plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.ktfmt) apply false
}

subprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")
    configure<com.ncorti.ktfmt.gradle.KtfmtExtension> { kotlinLangStyle() }
}

tasks.register<Delete>("clean") { delete(rootProject.layout.buildDirectory) }
