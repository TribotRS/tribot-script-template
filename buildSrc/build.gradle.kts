import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.lingala.zip4j:zip4j:2.6.4")
    implementation("com.google.code.gson:gson:2.8.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("TribotPlugin") {
            id = "org.tribot.tribot-gradle-plugin"
            implementationClass = "org.tribot.gradle.plugin.TribotPlugin"
        }
    }
}