
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
}

android {
    namespace = "xyz.akimlc.themetool"
    compileSdk = 35

    defaultConfig {
        applicationId = "xyz.akimlc.themetool"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "2.0.0"
        versionCode = getGitCommitCount()
    }

    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath!=null) {
        signingConfigs {
            register("github") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    } else {
        signingConfigs {
            register("release") {
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig =
                signingConfigs.getByName(if (keystorePath!=null) "github" else "release")
        }
        debug {
            if (keystorePath!=null) signingConfig = signingConfigs.getByName("github")
        }
    }

    android.applicationVariants.all {
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = "v" + config.versionName
                val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                val createTime = LocalDateTime.now().format(formatter)
                this.outputFileName =
                    "ThemeTool_${versionName}(${versionCode})_${createTime}_$name.apk"
            }
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
}
fun getGitCommitCount(): Int {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", "HEAD"))
        val count = process.inputStream.bufferedReader().readText().trim()
        count.toInt()
    } catch (e: Exception) {
        1
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.miuix)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.kotlinx.coroutines.android)

    //UMeng
    implementation("com.umeng.umsdk:common:9.4.7")
    implementation("com.umeng.umsdk:asms:1.4.0")

    //ROOM
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    //MMKV
    implementation("com.tencent:mmkv:2.2.2")

    implementation("com.squareup.retrofit2:retrofit:2.12.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.12.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.8.3")
}
