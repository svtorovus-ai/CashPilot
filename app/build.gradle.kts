plugins { id("com.android.application") }

android {
    namespace = "ua.cashpilot"
    compileSdk = 36

    defaultConfig {
        applicationId = "ua.cashpilot"
        minSdk = 29
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"
    }

    signingConfigs {
        create("release") {
            val storePath = System.getenv("CASH_PILOT_KEYSTORE_PATH")
            val storePassword = System.getenv("CASH_PILOT_KEYSTORE_PASSWORD")
            val alias = System.getenv("CASH_PILOT_KEY_ALIAS")
            val keyPassword = System.getenv("CASH_PILOT_KEY_PASSWORD")
            if (!storePath.isNullOrBlank() && !storePassword.isNullOrBlank() &&
                !alias.isNullOrBlank() && !keyPassword.isNullOrBlank()) {
                storeFile = file(storePath)
                this.storePassword = storePassword
                keyAlias = alias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies { implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core:1.15.0")
    implementation(libs.constraintlayout)
    implementation(libs.google.material)
}
