tasks.wrapper {
    gradleVersion = "7.4"
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    kotlin("jvm") version "1.6.10"
    application
    java
}

group = "at.tugraz"
version = "0.1"

repositories {
    mavenCentral()
}

val dllDirectory = "build/dll"

dependencies {
    // rocket league bot framework
    implementation("org.rlbot.commons:framework:2.1.0")
    runtimeOnly(files(dllDirectory))

    // enables our application to listen to global key events for testing
    implementation("com.github.kwhat:jnativehook:2.2.2")

    // logging and testing
    implementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation(kotlin("test"))
}

tasks.create("createDllDirectory") {
    mkdir(dllDirectory)
}

tasks.withType<JavaExec> {
    dependsOn("createDllDirectory")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    applicationDistribution.exclude(dllDirectory)
    applicationDefaultJvmArgs = listOf("-Djna.library.path=$dllDirectory")
    mainClass.set("at.tugraz.user_interfaces_ss22.MainKt")
}
