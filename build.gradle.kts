plugins {
    kotlin("jvm") version "1.9.22"
    id("application")
}

group = "com.bikeextreme"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.bikeextreme.MainKt")
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}