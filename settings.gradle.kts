pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}
rootProject.name = "NoteDelight"

include(":shared-android-test-util", ":shared", ":androidApp")
