buildscript {
    val kotlinVersion by extra("1.9.22")
    val agpVersion by extra("8.2.2")

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    version = "1.0.0"
}
