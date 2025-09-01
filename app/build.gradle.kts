plugins {
            alias(libs.plugins.android.application)
            id("com.google.gms.google-services")
        }

        android {
            namespace = "com.example.fashionstore"
            compileSdk = 35

            defaultConfig {
                applicationId = "com.example.fashionstore"
                minSdk = 24
                targetSdk = 35
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

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }

        dependencies {
            implementation(libs.appcompat)
            implementation(libs.material)
            implementation(libs.activity)
            implementation(libs.constraintlayout)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.database)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            testImplementation(libs.junit)
            androidTestImplementation(libs.ext.junit)
            androidTestImplementation(libs.espresso.core)

            // Firebase BOM
            implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
            implementation("com.google.firebase:firebase-analytics")
            // Remove this if NOT using Firebase Storage
            // implementation("com.google.firebase:firebase-storage")

            // Glide for image loading
            implementation("com.github.bumptech.glide:glide:4.16.0")
            annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

            // Cloudinary
            implementation("com.cloudinary:cloudinary-android:2.3.1")
        }