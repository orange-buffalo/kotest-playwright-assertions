plugins {
    kotlin("jvm") version "2.4.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.28.2")
    implementation("com.squareup:kotlinpoet:2.3.0")
    implementation("io.ktor:ktor-client-core:3.5.1")
    implementation("io.ktor:ktor-client-cio:3.5.1")

    testImplementation("io.kotest:kotest-runner-junit5:6.2.1")
    testImplementation("io.kotest:kotest-assertions-core:6.2.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.1")
}

tasks.test {
    useJUnitPlatform()
}
