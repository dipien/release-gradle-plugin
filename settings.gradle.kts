plugins {
    id("com.gradle.enterprise").version("3.3.4")
}

include(":release-gradle-plugin")

apply(from = java.io.File(settingsDir, "buildCacheSettings.gradle"))
