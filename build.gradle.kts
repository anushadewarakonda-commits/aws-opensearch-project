plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.11"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(platform("software.amazon.awssdk:bom:2.34.7"))
    implementation("software.amazon.awssdk:s3")
    implementation("com.amazonaws:aws-lambda-java-events:3.16.1")
    implementation("software.amazon.awssdk:dynamodb")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}