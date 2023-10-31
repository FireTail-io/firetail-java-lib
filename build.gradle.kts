plugins {
    `java-library`
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
    implementation(
        platform("org.springframework.boot:spring-boot-dependencies:2.7.17"),
    )
    api("org.yaml:snakeyaml:2.2")
    // Dependencies are transitively imported from spring-boot-dependencies
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("commons-io:commons-io:2.7")
    api("net.logstash.logback:logstash-logback-encoder:7.4")
    api("javax.annotation:javax.annotation-api:1.3.2")
    api("org.slf4j:slf4j-api")
    api("ch.qos.logback:logback-classic")
    compileOnly("javax.servlet:javax.servlet-api")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-webmvc")
    testImplementation(kotlin("test"))
    testImplementation("javax.servlet:javax.servlet-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
