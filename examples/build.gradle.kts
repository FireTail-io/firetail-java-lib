plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.6.10"
}

group = "com.github.firetail-io"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    mavenCentral()
}

dependencies {
    implementation(
        platform("org.springframework.boot:spring-boot-dependencies:3.1.5"),
    )
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("net.logstash.logback:logstash-logback-encoder:7.4")
    api("org.slf4j:slf4j-api")
    api("ch.qos.logback:logback-classic")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.github.firetail-io:firetail-java-lib:$version")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args("--spring.profiles.active=local")
}
