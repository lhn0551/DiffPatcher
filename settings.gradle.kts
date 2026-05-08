pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/google") {
            name = "AliyunGoogle"
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://maven.aliyun.com/repository/central") {
            name = "AliyunMavenCentral"
        }
        maven("https://maven.aliyun.com/repository/gradle-plugin") {
            name = "AliyunGradlePluginPortal"
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/google") {
            name = "AliyunGoogle"
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://maven.aliyun.com/repository/central") {
            name = "AliyunMavenCentral"
        }
    }
}

rootProject.name = "Diff-update-sdk"
