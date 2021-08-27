plugins {
    kotlin("multiplatform") version "1.5.21"
    id("maven-publish")

}

group = "github.nwn"
version = "1.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    jvm {
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {

        }
    }
    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/${properties["githubUsername"]}/kotlin-graph")
                credentials {

                    username = project.findProperty("githubUsername").toString()
                    password = project.findProperty("githubToken").toString()
                }
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["kotlin"])
            }

        }
    }
}

