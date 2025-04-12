package services

import akka.actor.ActorSystem
import models.{ChatCompletionRequest, ChatCompletionResponse}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}
import play.api.libs.ws.{WSClientConfig, WSResponse}
import play.api.{Configuration, Logging}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

@Singleton
class GroqApiService @Inject()(configuration: Configuration,
                               lifecycle: ApplicationLifecycle) extends Logging {

    private val host = configuration.get[String]("groq.endpoint")
    private val apiKey = configuration.get[String]("groq.apikey")

    private val requestTimeout = 3000
    private val connectionTtl = 3000
    private val connectionIdleTimeout = 3000
    private val maxConnectionPerServer = 16
    private val retryCount = 1
    private val connectionTimeout = 3000

    implicit private val system: ActorSystem = ActorSystem()

    private val config = AhcWSClientConfig(
        maxConnectionLifetime = connectionTtl.millis,
        idleConnectionInPoolTimeout = connectionIdleTimeout.millis,
        maxConnectionsTotal = maxConnectionPerServer,
        maxRequestRetry = retryCount,
        wsClientConfig = WSClientConfig(connectionTimeout = connectionTimeout.millis))

    val client = AhcWSClient(config)

    def closeWSClient(): Unit = {
        logger.info("closeWSClient.close")
        client.close()
    }

    lifecycle.addStopHook { () => Future.successful(closeWSClient()) }

    def defaultHeaders = Map("Content-Type" -> "application/json", "Authorization" -> s"Bearer $apiKey")

    def chatCompletion(request: ChatCompletionRequest): Future[ChatCompletionResponse] = {
        client
            .url(s"$host/openai/v1/chat/completions")
            .addHttpHeaders(defaultHeaders.toSeq: _*)
            .post(Json.toJson(request))
            .map { response =>
                handleResponse(response)
            }
    }

    private def handleResponse(response: WSResponse): ChatCompletionResponse = {
        response.status match {
            case 200 => Json.parse(response.body).as[ChatCompletionResponse]
            case _ => throw new RuntimeException(s"API 요청 실패: ${response.status} ${response.body}")
        }
    }
}
