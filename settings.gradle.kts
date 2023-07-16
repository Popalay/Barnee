dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
rootProject.name = "Barnee"

include(":app")
include(":shared")
include(":ui")
