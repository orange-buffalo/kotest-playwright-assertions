plugins {
    kotlin("jvm") version "2.4.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.28.2")
    implementation("com.squareup:kotlinpoet:2.4.0")
    implementation("io.ktor:ktor-client-core:3.6.0")
    implementation("io.ktor:ktor-client-cio:3.6.0")

    testImplementation("io.kotest:kotest-runner-junit5:6.2.5")
    testImplementation("io.kotest:kotest-assertions-core:6.2.5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")
}

tasks.test {
    useJUnitPlatform()
}
