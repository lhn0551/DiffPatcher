plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

group = "com.github.lhn0551"
            version = "v1.0.0"

base {
    archivesName.set("diff-update-sdk")
}

android {
    namespace = "com.youhaoka.diffupdate"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.lhn0551"
            artifactId = "DiffPatcher"
version = "v1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
