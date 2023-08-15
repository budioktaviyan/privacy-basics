plugins {
  id("com.android.application")
  kotlin("android")
  id("com.google.devtools.ksp")
}

android {
  namespace = "id.android.basics.security"
  compileSdk = 34
  buildToolsVersion = "34.0.0"
  ndkVersion = "25.2.9519653"

  defaultConfig {
    applicationId = "id.android.basics.security"
    minSdk = 23
    targetSdk = 34
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables.useSupportLibrary = true

    javaCompileOptions.annotationProcessorOptions {
      arguments.plus(mapOf("room.schemaLocation" to "$projectDir/schemas"))
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs.plus(listOf("-opt-in=kotlin.RequiresOptIn", "-Xjvm-default=enable"))
  }
  buildFeatures.compose = true
  composeOptions.kotlinCompilerExtensionVersion = "1.4.8"
  packaging {
    resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
  }
}

dependencies {
  implementation("androidx.activity:activity-compose:1.7.2")
  implementation("androidx.activity:activity-ktx:1.7.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
  implementation("androidx.compose.ui:ui:1.5.0")
  implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
  implementation("androidx.compose.material:material:1.5.0")
  implementation("androidx.compose.material:material-icons-extended:1.5.0")
  implementation("androidx.compose.material3:material3:1.1.1")
  implementation("androidx.navigation:navigation-compose:2.7.0")
  implementation("androidx.camera:camera-lifecycle:1.2.3")
  implementation("androidx.camera:camera-camera2:1.2.3")
  implementation("androidx.camera:camera-view:1.2.3")
  implementation("androidx.core:core-ktx:1.10.1")
  implementation("com.google.android.gms:play-services-location:21.0.1")
  implementation("io.coil-kt:coil-compose:2.4.0")
  implementation("androidx.room:room-runtime:2.5.2")
  implementation("androidx.room:room-ktx:2.5.2")

  ksp("androidx.room:room-compiler:2.5.2")

  testImplementation("junit:junit:4.13.2")

  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0")
}