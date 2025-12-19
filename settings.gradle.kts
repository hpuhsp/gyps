pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "Gyps"
include(":app")
include(":msc")
include(":base")
include(":swallow")
