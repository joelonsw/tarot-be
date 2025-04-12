package services

import models.{ChatCompletionRequest, GroqModel}
import play.api.Logging

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex

@Singleton
class ResultService @Inject()(groqApiService: GroqApiService) extends Logging {

    private val thinkPattern: Regex = """<think>[\s\S]*?</think>""".r
    private val chineseCharPattern: Regex = """[\u4e00-\u9fff]+""".r

    def getTarotResult(tarotCards: String): Future[String] = {
        val request = ChatCompletionRequest.readTarot(tarotCards, GroqModel.Gemma2_9b_It)
        groqApiService.chatCompletion(request).map { response =>
            val content = response.choices.head.message.content
            cleanContent(content)
        }
    }

    /**
      * API 응답에서 불필요한 내용을 제거하는 메서드
      */
    private def cleanContent(content: String): String = {
        // <think> 태그와 내용 제거
        val withoutThink = thinkPattern.replaceAllIn(content, "")

        // 중국어 문자 제거
        val withoutChinese = chineseCharPattern.replaceAllIn(withoutThink, "")

        // 연속된 공백 및 빈 줄 정리
        val cleaned = withoutChinese
            .replaceAll("\\s+", " ")
            .replaceAll("\\n\\s*\\n", "\n\n")
            .trim

        cleaned
    }
}
