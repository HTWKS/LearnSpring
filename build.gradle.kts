import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.apollographql.apollo3") version "3.8.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "ThoughtWorks"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.2")
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

apollo {
    service("GithubGraphql") {
        packageName.set("thoughtworks.main.graphql")
    }
}