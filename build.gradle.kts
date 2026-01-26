import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "2.3.0"
    // version pinned as newer has major JGit changed which is not compatible with jreleaser
    id("com.github.jmongard.git-semver-plugin") version "0.13.0"
    id("maven-publish")
    id("signing")
    id("org.jreleaser") version "1.21.0"
}

repositories {
    mavenCentral()
}

group = "io.orange-buffalo"

semver {
    // tags managed by jreleaser
    createReleaseTag = false
}
version = semver.version

val playwrightVersion = "1.57.0"

dependencies {
    api("com.microsoft.playwright:playwright:$playwrightVersion")
    api("io.kotest:kotest-assertions-core:6.1.1")

    testImplementation("io.kotest:kotest-runner-junit5:6.1.1")
    testImplementation("io.kotest:kotest-framework-engine:6.1.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

tasks.test {
    useJUnitPlatform()
}

val generateLocatorAssertions =
    tasks.register<io.orangebuffalo.kotestplaywrightassertions.generator.GenerateLocatorAssertionsTask>("generateLocatorAssertions") {
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

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    // javadoc jar is necessary for publishing to Maven Central,
    // but we don't generate it as it is a Kotlin project
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("Kotest assertions for Playwright")
                url.set("https://github.com/orange-buffalo/kotest-playwright-assertions")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("orange-buffalo")
                        name.set("Bogdan Ilchyshyn")
                        email.set("orange-buffalo@users.noreply.github.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/orange-buffalo/kotest-playwright-assertions.git")
                    developerConnection.set("scm:git:ssh://github.com/orange-buffalo/kotest-playwright-assertions.git")
                    url.set("https://github.com/orange-buffalo/kotest-playwright-assertions")
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

jreleaser {
    release {
        github {
            val shouldSkipGitHubActions = project.version.get().endsWith("-SNAPSHOT")
            skipTag = shouldSkipGitHubActions
            skipRelease = shouldSkipGitHubActions

            uploadAssets = Active.NEVER
            prerelease {
                enabled = true
            }
            changelog {
                formatted = Active.ALWAYS
                preset = "conventional-commits"
                skipMergeCommits = true
                hide {
                    uncategorized = true
                    contributor("[bot]")
                    contributor("orange-buffalo")
                    contributor("GitHub")
                }
            }
        }
    }
    signing {
        active = Active.ALWAYS
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                create("release-deploy") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    javadocJar = false
                }
            }
            nexus2 {
                create("snapshot-deploy") {
                    active = Active.SNAPSHOT
                    url = "https://central.sonatype.com/repository/maven-snapshots/"
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    applyMavenCentralRules = true
                    snapshotSupported = true
                    closeRepository = true
                    releaseRepository = true
                    stagingRepository("build/staging-deploy")
                    javadocJar = false
                }
            }
        }
    }
}
