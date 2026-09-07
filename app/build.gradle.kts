plugins { id("com.android.application") }

android { namespace = "ua.cashpilot"; compileSdk = 36
    defaultConfig { applicationId = "ua.cashpilot"; minSdk = 29; targetSdk = 36; versionCode = 2; versionName = "1.1" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}

dependencies { implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.constraintlayout)
    implementation(libs.google.material)
}
