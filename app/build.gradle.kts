
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow") version "5.0.0"
    application
}

dependencies {
    implementation(project(":command-side"))
    implementation(project(":query-side"))
}

application {
    mainClassName = "com.alo.cqrs.orders.AppKt"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}
