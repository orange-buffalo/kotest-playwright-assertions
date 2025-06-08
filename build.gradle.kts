plugins {
    kotlin("jvm") version "2.1.10"
    id("me.qoomon.git-versioning") version "6.4.4"
}

group = "io.orange-buffalo"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.13.0")
}

gitVersioning.apply {
    refs {
        branch("main") {
            version = "\${describe.tag.version}-SNAPSHOT"
        }
        branch(".+") {
            version = "\${ref}-SNAPSHOT"
        }
        tag("(?<version>.*)") {
            version = "\${ref.version}"
        }
    }
    rev {
        version = "\${commit}"
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
