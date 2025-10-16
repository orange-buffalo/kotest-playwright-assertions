plugins {
    kotlin("jvm") version "2.2.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.27.1")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("io.ktor:ktor-client-core:3.3.1")
    implementation("io.ktor:ktor-client-cio:3.3.1")

    testImplementation("io.kotest:kotest-runner-junit5:6.0.4")
    testImplementation("io.kotest:kotest-assertions-core:6.0.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0")
}

tasks.test {
    useJUnitPlatform()
}
