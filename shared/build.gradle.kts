import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
//    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)

    kotlin("plugin.serialization") version "2.1.20"
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    /*iosX64()
    iosArm64()
    iosSimulatorArm64()*/

/*
    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
*/

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines.core)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.json)

        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation("androidx.fragment:fragment-compose:1.8.6")
            implementation(libs.ktor.client.okhttp)
            implementation("androidx.media3:media3-exoplayer:1.6.0")
            implementation("androidx.media3:media3-exoplayer-hls:1.6.0")
            implementation("androidx.media3:media3-ui:1.6.0")
            implementation("androidx.media3:media3-ui-compose:1.6.0")
            implementation(libs.androidx.lifecycle.viewmodel.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.mayankmkh.kmpsample"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

