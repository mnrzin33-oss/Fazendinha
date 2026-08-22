buildscript {
    val kotlinVersion by extra("1.9.22")

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    version = "1.0.0"
}
