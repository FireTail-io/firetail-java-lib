plugins {
    `java-library`
    // application
    `maven-publish`
    signing
    // Not possible to set the version for a plugin from a variable.
    kotlin("plugin.spring") version "1.8.21"
    kotlin("jvm") version "1.8.21"
    id("io.spring.dependency-management") version "1.1.2"
}

buildscript {
    val kotlinVersion = "1.8.21"
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("org.jmailen.gradle:kotlinter-gradle:3.14.0")
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    mavenCentral()
}

group = "com.github.firetail-io"
version = "0.0.1-SNAPSHOT"
description = "firetail-java-lib"

// java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencies {
    // Dependencies are transitively imported from spring-boot-dependencies
    implementation(
        platform("org.springframework.boot:spring-boot-dependencies:3.1.5"),
    )
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("org.slf4j:slf4j-api")
    api("ch.qos.logback:logback-classic")
    compileOnly("org.yaml:snakeyaml:2.2")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-webmvc")
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // javax vs. jakarta - Spring boot 3x uses jakarta. Need a version of this for < 3x for
    // older SB apps
    compileOnly("jakarta.annotation:jakarta.annotation-api")
    compileOnly("jakarta.servlet:jakarta.servlet-api:5.0.0")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.0.4")
    testImplementation("jakarta.servlet:jakarta.servlet-api")
    // end javax vs. jakarta
    testImplementation("org.springframework:spring-webmvc")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
}

signing {
    setRequired {
        // signing is only required if the artifacts are to be published
        gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
    }
    if (project.hasProperty("signJar") && project.property("signJar") == "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(configurations["archives"])
    }
}

publishing {
    publications {
        create<MavenPublication>("jar") {
            artifact(tasks.named("jar"))
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test { // See 5️⃣
    useJUnitPlatform() // JUnitPlatform for tests. See 6️⃣
}
