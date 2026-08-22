plugins {
    kotlin("android")
    id("com.android.application")
}

val gdxVersion = "1.12.1"

android {
    namespace = "fazendinha.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "fazendinha.game"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs(arrayOf("../core/assets"))
            jniLibs.srcDirs(arrayOf("libs"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
        }
        debug {
            isMinifyEnabled = false
        }
    }
}

configurations {
    create("natives")
}

fun DependencyHandlerScope.natives(dependencyNotation: String) {
    val dep = project.dependencies.create(dependencyNotation)
    configurations.getByName("natives").dependencies.add(dep)
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}

tasks.register<Copy>("copyNatives") {
    from(configurations.getByName("natives"))
    into(project.file("libs"))
}

tasks.matching { it.name.contains("merge") && it.name.contains("JniLibFolders") }.configureEach {
    dependsOn("copyNatives")
}
