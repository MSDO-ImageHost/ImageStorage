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
    implementation("com.h2database", "h2", "1.4.200")
    implementation("org.jetbrains.exposed", "exposed-core", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.25.1")
    implementation("com.beust:klaxon:5.4")
    implementation("org.slf4j", "slf4j-simple", "2.0.0-alpha1")
    implementation("org.xerial:sqlite-jdbc:3.21.0.1")
    implementation("mysql:mysql-connector-java:8.0.21")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    setEnvironment("DB_USER" to "guest", "DB_PASSWORD" to "pass", "DB_URI" to "jdbc:sqlite:test.sqlite", "DB_DRIVER" to "org.sqlite.JDBC")
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

