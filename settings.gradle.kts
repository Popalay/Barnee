dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
rootProject.name = "Barnee"

include(":app")
include(":shared")

enableFeaturePreview("VERSION_CATALOGS")