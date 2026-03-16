plugins {
    kotlin("jvm") version "2.3.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.28.0")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("io.ktor:ktor-client-core:3.4.1")
    implementation("io.ktor:ktor-client-cio:3.4.1")

    testImplementation("io.kotest:kotest-runner-junit5:6.1.7")
    testImplementation("io.kotest:kotest-assertions-core:6.1.7")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.3")
}

tasks.test {
    useJUnitPlatform()
}
