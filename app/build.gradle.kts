plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    android {
        namespace = "com.miapp.xanogamesstore"
        compileSdk = 34

        defaultConfig {
            applicationId = "com.miapp.xanogamesstore"
            minSdk = 24
            targetSdk = 34
            versionCode = 1
            versionName = "1.0.0"

            vectorDrawables { useSupportLibrary = true }


            // === XANO - NUEVOS ENDPOINTS ===
            buildConfigField(
                "String",
                "XANO_ORIGIN",
                "\"https://x8ki-letl-twmt.n7.xano.io\""
            )
            buildConfigField(
                "String",
                "XANO_BASE_AUTH",
                "\"https://x8ki-letl-twmt.n7.xano.io/api:ObzeKtl9/\""
            )
            buildConfigField(
                "String",
                "XANO_BASE_SHOP",
                "\"https://x8ki-letl-twmt.n7.xano.io/api:c_UHqNA3/\""
            )
            buildConfigField(
                "String",
                "XANO_BASE_UPLOAD",
                "\"https://x8ki-letl-twmt.n7.xano.io/api:-ukB1aW3/\""
            )
        }


        buildTypes {
            debug {
                isMinifyEnabled = false
                isDebuggable = true   // <- importante
            }
            release {
                isMinifyEnabled = true
                isDebuggable = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        buildFeatures {
            viewBinding = true
            buildConfig = true
        }


        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        kotlinOptions { jvmTarget = "17" }
    }

    dependencies {
        // Shimmer effect
        implementation("com.facebook.shimmer:shimmer:0.5.0")

        // Retrofit + Gson
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:okhttp:4.11.0")
        implementation("com.github.bumptech.glide:glide:4.16.0")

        // OkHttp
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

        // Coroutines + lifecycleScope
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

        // UI base
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.2.0")

        // Fragments y RecyclerView
        implementation("androidx.fragment:fragment-ktx:1.8.3")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
    }
}
dependencies {
    implementation(libs.androidx.swiperefreshlayout)
}
