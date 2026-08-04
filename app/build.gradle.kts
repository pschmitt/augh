import java.time.Instant

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose.compiler)
}

fun buildConfigString(value: String): String =
    "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

val buildCommit =
    providers
        .gradleProperty("aughhhhCommit")
        .orElse(providers.environmentVariable("GITHUB_SHA"))
        .orElse("unknown")
        .get()
val buildDate =
    providers
        .gradleProperty("aughhhhBuildDate")
        .orElse(providers.environmentVariable("BUILD_DATE"))
        .orElse(Instant.now().toString())
        .get()

val configuredVersionCode =
    providers
        .gradleProperty("versionCode")
        .orElse("1")
        .map { value ->
            value.toIntOrNull()?.takeIf { it > 0 }
                ?: error("versionCode must be a positive integer")
        }
        .get()
val configuredVersionName =
    providers.gradleProperty("versionName").orElse("1.0.0").get().also { name ->
        require(name.isNotBlank()) { "versionName must not be blank" }
    }

android {
    namespace = "dev.pschmitt.aughhhh"
    compileSdk = 36
    buildToolsVersion = "36.1.0"

    defaultConfig {
        applicationId = "dev.pschmitt.aughhhh"
        minSdk = 26
        targetSdk = 36
        versionCode = configuredVersionCode
        versionName = configuredVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BUILD_COMMIT", buildConfigString(buildCommit))
        buildConfigField("String", "BUILD_DATE", buildConfigString(buildDate))
    }

    buildTypes {
        named("debug") { applicationIdSuffix = ".debug" }
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    signingConfigs {
        named("debug") {
            System.getenv("CI_KEYSTORE_PATH")?.let { path ->
                storeFile = file(path)
                storePassword = System.getenv("CI_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("CI_KEY_ALIAS")
                keyPassword = System.getenv("CI_KEY_PASSWORD")
            }
        }
    }

    buildTypes.named("release") { signingConfig = signingConfigs.getByName("debug") }

    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.splashscreen)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.fastlane.screengrab)
    testImplementation(libs.junit)
}
