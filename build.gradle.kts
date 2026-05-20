plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.bikeextreme"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    testLogging {
        events("passed", "skipped", "failed")
    }
}

application {
    mainClass.set("com.bikeextreme.MainKt")
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}