pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}
rootProject.name = "NoteDelight"

include(":shared")
include(":shared-compose-ui")
include(":shared-jvm-util")
include(":shared-android-util")
include(":shared-android-test-util")
//include(":android-old-app")
include(":android-compose-app")
include(":desktop-compose-app")
include(":ios-compose-kit")
