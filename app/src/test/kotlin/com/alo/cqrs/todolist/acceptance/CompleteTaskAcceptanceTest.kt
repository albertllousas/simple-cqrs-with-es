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

class CompleteTaskAcceptanceTest {
    private lateinit var server: ApplicationEngine

    private val appPort = RandomPort.get()

    @BeforeEach
    fun `set up`() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = appPort
        server = embeddedServer(
            factory = Netty,
            port = appPort,
            module = Application::module
        ).start()
        Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0) })
    }

    @Test
    fun `should complete a task to an existent todo list and read the projection details`() {
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
            .given()
            .contentType(ContentType.JSON)
            .body(
                """
					{
                        "name": "my task"
					}
				"""
            )
            .`when`()
            .port(appPort)
            .post("/todo-lists/$id/tasks")
            .then()
            .assertThat()
            .statusCode(202)

        val taskId:String  = RestAssured
            .`when`()
            .get("/todo-lists/$id/details")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .path("tasks[0].id")

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .port(appPort)
            .put("/todo-lists/$id/tasks/$taskId/completed")
            .then()
            .assertThat()
            .statusCode(202)

        Thread.sleep(500) //TODO: add awaitability and remove sleep

        RestAssured
            .`when`()
            .get("/todo-lists/$id/details")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .also {
                assertThatJson(it.body.asString())
                    .isEqualTo(
                        """
					{
                        "id":"$id",
                        "name":"my todo list",
                        "status":"DONE",
                        "tasks":[
                            {
                                "id": "$taskId",
                                "name": "my task",
                                "status":"DONE"
                            }
                        ]
                    }
					"""
                    )
            }

    }


}
