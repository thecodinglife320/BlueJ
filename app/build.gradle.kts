plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.kotlin.android)
   id ("androidx.navigation.safeargs.kotlin")
}

android {
   namespace = "com.project.ad.bluej"
   compileSdk = 35

   defaultConfig {
      applicationId = "com.project.ad.bluej"
      minSdk = 26
      targetSdk = 34
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
         )
      }
   }
   buildFeatures {
      viewBinding = true
      dataBinding = true
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
   }
   kotlinOptions {
      jvmTarget = "11"
   }
}

dependencies {

   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.appcompat)
   implementation(libs.material)
   implementation(libs.androidx.activity)
   implementation(libs.androidx.constraintlayout)
   implementation(libs.androidx.navigation.fragment)
   implementation(libs.androidx.navigation.ui.ktx)
   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.espresso.core)
   // Moshi
   implementation(libs.moshi.kotlin)

   // Retrofit with Moshi Converter
   implementation(libs.converter.moshi)

   implementation(libs.glide) // Use the latest version
   annotationProcessor(libs.compiler)

   implementation(libs.androidx.lifecycle.viewmodel.ktx)
   implementation(libs.androidx.lifecycle.livedata.ktx)

   implementation(libs.androidx.paging.runtime)
}