package com.alo.cqrs.todolist.acceptance

import com.alo.cqrs.todolist.module
import io.ktor.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.restassured.RestAssured
import io.restassured.http.ContentType
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateTodoListAcceptanceTest {
    private lateinit var server: ApplicationEngine

    private val appPort = 8080

    @BeforeEach
    fun `set up`() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 8080
        server = embeddedServer(
            factory = Netty,
            port = appPort,
            module = Application::module
        ).start()
        Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0) })
    }

    @Test
    fun `should create a todo list with a command and read the projection details`() {
        val id = UUID.randomUUID()

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(
                """
					{
						"id": "$id",
                        "name": "my todo list"
					}
				"""
            )
            .`when`()
            .port(appPort)
            .post("/todo-lists")
            .then()
            .assertThat()
            .statusCode(202)

        RestAssured
            .`when`()
            .get("/todo-lists/$id/details")
            .then()
            .assertThat()
            .statusCode(200)

            .extract()
            .response()
            .also {
                assertThatJson(it.body.asString()).isEqualTo(
                    """
					{
                        "id":"$id",
                        "name":"my todo list",
                        "status":"TODO",
                        "tasks":[]
                    }
					"""
                )
            }

    }

}
