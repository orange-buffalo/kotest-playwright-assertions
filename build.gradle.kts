plugins {
    kotlin("jvm") version "2.1.10"
    id("me.qoomon.git-versioning") version "6.4.4"
}

group = "io.orange-buffalo"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val playwrightVersion = "1.52.0"

dependencies {
    api("com.microsoft.playwright:playwright:$playwrightVersion")
    implementation("io.kotest:kotest-assertions-core:5.9.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-framework-engine:5.9.1")
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

val generateLocatorAssertions = tasks.register<io.orangebuffalo.kotestplaywrightassertions.generator.GenerateLocatorAssertionsTask>("generateLocatorAssertions") {
    targetPlaywrightVersion.set(playwrightVersion)
    outputDirectory.set(layout.buildDirectory.dir("generated/source/kotlin"))
}

sourceSets {
    main {
        kotlin {
            srcDir(generateLocatorAssertions.map { it.outputDirectory })
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateLocatorAssertions)
}

kotlin {
    jvmToolchain(17)
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
