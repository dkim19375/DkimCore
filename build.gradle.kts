import me.dkim19375.dkimgradle.data.pom.DeveloperData
import me.dkim19375.dkimgradle.data.pom.LicenseData
import me.dkim19375.dkimgradle.data.pom.SCMData
import me.dkim19375.dkimgradle.enums.mavenAll
import me.dkim19375.dkimgradle.util.addKotlinKDocSourcesJars
import me.dkim19375.dkimgradle.util.setupJava
import me.dkim19375.dkimgradle.util.setupPublishing

plugins {
    signing
    `java-library`
    `maven-publish`
    alias(libs.plugins.dkim.gradle)
    alias(libs.plugins.dokkatoo)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.licenser)
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.spotless)
}

group = "me.dkim19375"
version = "1.5.2"

setupJava(javaVersion = JavaVersion.VERSION_11)

repositories {
    mavenCentral()
    mavenAll()
}

dependencies {
    compileOnly(libs.kaml)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.triumph.config)

    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core) {
        exclude(group = "org.jetbrains.kotlin")
    }
    api(libs.kotlinx.coroutines.reactor) {
        exclude(group = "org.jetbrains.kotlin")
        exclude(module = "kotlinx-coroutines-core")
    }

    // optional dependencies
    compileOnly(libs.commons.io)
    compileOnly(libs.commons.lang3)
    compileOnly(libs.commons.text)
    compileOnly(libs.gson)

    // testing libs
    testImplementation(libs.commons.io)
    testImplementation(libs.commons.lang3)
    testImplementation(libs.gson)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kaml)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.triumph.config)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockito.kotlin)
}

private object LibInfo {
    const val ARTIFACT_ID = "dkimcore"
    const val DESCRIPTION = "A library for some utilities made in kotlin!"
    const val VCS_USERNAME = "dkim19375"
    const val VCS_REPOSITORY = "DkimCore"
    const val VCS = "github.com/$VCS_USERNAME/$VCS_REPOSITORY"
}

val artifacts = addKotlinKDocSourcesJars()

setupPublishing(
    groupId = "io.github.dkim19375",
    artifactId = LibInfo.ARTIFACT_ID,
    description = LibInfo.DESCRIPTION,
    url = "https://${LibInfo.VCS}",
    licenses = listOf(LicenseData.MIT),
    developers = listOf(
        DeveloperData(
            id = "dkim19375",
            roles = listOf("developer"),
            timezone = "America/New_York",
            url = "https://github.com/dkim19375",
        )
    ),
    scm = SCMData.generateGit(
        username = LibInfo.VCS_USERNAME,
        repository = LibInfo.VCS_REPOSITORY,
        developerSSH = true,
    ),
    publicationName = "mavenKotlin",
    verifyMavenCentral = true,
    artifacts = artifacts.javadocJarTasks.values.map(TaskProvider<Jar>::get) + artifacts.sourcesJarTask.get(),
    setupNexusPublishing = System.getenv("GITHUB_ACTIONS") != "true",
)