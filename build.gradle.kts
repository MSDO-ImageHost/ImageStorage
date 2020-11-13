import org.gradle.jvm.tasks.Jar

plugins {
    java
    kotlin("jvm") version "1.3.72"
}

group = "dk.sdu.imagehost"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.rabbitmq", "amqp-client", "5.10.0")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jetbrains.exposed", "exposed-core", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-jodatime", "0.25.1")
    api("org.xerial:sqlite-jdbc:3.21.0.1")
    api("mysql:mysql-connector-java:8.0.21")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    this.setProperty("archiveFileName", "ImageStorage.jar")
    manifest {
        attributes["Implementation-Title"] = "Imagehost Imagestorage"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Main-Class"] = "dk.sdu.imagehost.imagestorage.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    "build" {
        dependsOn(fatJar)
    }
}

