
plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"
}

allprojects {

    repositories {
        jcenter()
    }

}

val ktorVersion = "1.3.2"
val arrowVersion = "0.10.4"

subprojects {
    apply(plugin = "java")
    dependencies {
        val implementation by configurations
        val testImplementation by configurations
        // Align versions of all Kotlin components
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

        // Use the Kotlin JDK 8 standard library.
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("io.ktor:ktor-server-core:$ktorVersion")
        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        implementation("io.ktor:ktor-jackson:$ktorVersion")
        implementation("ch.qos.logback:logback-classic:1.2.3")
        implementation("io.arrow-kt:arrow-core:$arrowVersion")

        testImplementation("org.assertj:assertj-core:3.15.0")
        testImplementation("io.mockk:mockk:1.9.3")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
        testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
        testImplementation("io.rest-assured:rest-assured:3.3.0") {
            exclude(group = "com.sun.xml.bind", module = "jaxb-osgi")
        }
        testImplementation("com.github.javafaker:javafaker:1.0.1")
        testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.17.0")
    }

    tasks.apply {
        test {
            useJUnitPlatform()
        }
    }
}
