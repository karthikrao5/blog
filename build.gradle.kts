import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.1.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.moowork.node") version "1.3.1"
    kotlin("jvm") version "1.2.71"
    kotlin("plugin.spring") version "1.2.71"
    kotlin("plugin.jpa") version "1.2.71"
}

group = "org.krao"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

node {
    download = true
    // Set the work directory for unpacking node
    workDir = file("${project.buildDir}/frontend")

    // Set the work directory for NPM
    npmWorkDir = file("${project.buildDir}/frontend")
}

tasks {
    val appNpmInstall by registering(NpmTask::class) {
        println("Installs all dependencies from package.json")
        setNpmCommand("install")
    }

    val appNpmBuild by registering(NpmTask::class) {
        dependsOn(appNpmInstall)
        println("Builds production version of the webapp")
        setWorkingDir(file("${project.projectDir}/frontend"))
        setNpmCommand("build")
    }

    val copyWebApp by registering(Copy::class) {
        dependsOn(appNpmBuild)
		from("${project.projectDir}/frontend/build")
		into("${project.projectDir}/build/resources/main/static/.")
    }

    build {
        dependsOn(appNpmInstall, appNpmBuild, copyWebApp)
    }
}

springBoot {
    buildInfo {
        properties {
            artifact = "blog"
            version = "0.0.1"
            group = "org.krao"
            name = "Karthik's blog"
        }
    }
}