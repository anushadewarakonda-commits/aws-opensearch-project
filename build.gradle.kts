plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    gradlePluginPortal() // Add this to resolve plugins
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws:4.0.0")
    implementation("org.springframework.cloud:spring-cloud-starter-function-web")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.stripe:stripe-java:20.132.0")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("software.amazon.awssdk:dynamodb:2.20.20")

    // JUnit 5 (JUnit Jupiter) for unit testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    // Spring Boot Test for integration tests
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }

    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.mockito:mockito-junit-jupiter:3.12.4")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("") // Ensures the JAR is not suffixed with "-all"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Use JUnit 5 platform for tests
tasks.test {
    useJUnitPlatform()
}