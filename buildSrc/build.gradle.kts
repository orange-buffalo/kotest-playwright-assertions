plugins {
    kotlin("jvm") version "2.2.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.27.0")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("io.ktor:ktor-client-core:3.2.3")
    implementation("io.ktor:ktor-client-cio:3.2.3")

    testImplementation("io.kotest:kotest-runner-junit5:6.0.0")
    testImplementation("io.kotest:kotest-assertions-core:6.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.4")
}

tasks.test {
    useJUnitPlatform()
}
