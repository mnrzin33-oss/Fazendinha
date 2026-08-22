plugins {
    kotlin("android")
    id("com.android.application")
}

val gdxVersion = "1.12.1"

android {
    namespace = "com.stardew.game"
    compileSdk = 34
    ndkVersion = "25.2.9519653"

    defaultConfig {
        applicationId = "com.stardew.game"
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
}

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}

fun DependencyHandlerScope.natives(dependencyNotation: String) {
    val dependency = project.dependencies.create(dependencyNotation)
    val natives = configurations.getByName("natives")
    natives.dependencies.add(dependency)
}

configurations {
    create("natives")
}

tasks.register<Copy>("copyNatives") {
    from(configurations.getByName("natives"))
    into(project.file("libs"))
}
