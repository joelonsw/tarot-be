package models

import models.GroqModel.GroqModel
import play.api.libs.json._

case class ChatMessage(role: String, content: String)

object ChatMessage {
    implicit val format: Format[ChatMessage] = Json.format[ChatMessage]
}

/**
  * Groq API에서 제공하는 모델명을 관리하는 열거형
  * 2025년 4월 12일 기준 모델 목록
  */
object GroqModel extends Enumeration {
    type GroqModel = Value

    // Production 모델
    val DistilWhisperLargeV3En = Value("distil-whisper-large-v3-en")
    val Gemma2_9b_It = Value("gemma2-9b-it")
    val Llama_3_3_70b_Versatile = Value("llama-3.3-70b-versatile")
    val Llama_3_1_8b_Instant = Value("llama-3.1-8b-instant")
    val LlamaGuard_3_8b = Value("llama-guard-3-8b")
    val Llama3_70b_8192 = Value("llama3-70b-8192")
    val Llama3_8b_8192 = Value("llama3-8b-8192")
    val WhisperLargeV3 = Value("whisper-large-v3")
    val WhisperLargeV3Turbo = Value("whisper-large-v3-turbo")

    // Preview 모델
    val Llama_4_Scout_17b_16e_Instruct = Value("meta-llama/llama-4-scout-17b-16e-instruct")
    val Llama_4_Maverick_17b_128e_Instruct = Value("meta-llama/llama-4-maverick-17b-128e-instruct")
    val PlayaiTts = Value("playai-tts")
    val PlayaiTtsArabic = Value("playai-tts-arabic")
    val QwenQwq32b = Value("qwen-qwq-32b")
    val MistralSaba24b = Value("mistral-saba-24b")
    val Qwen2_5_Coder_32b = Value("qwen-2.5-coder-32b")
    val Qwen2_5_32b = Value("qwen-2.5-32b")
    val DeepseekR1DistillQwen32b = Value("deepseek-r1-distill-qwen-32b")
    val DeepseekR1DistillLlama70b = Value("deepseek-r1-distill-llama-70b")
    val Llama_3_3_70b_Specdec = Value("llama-3.3-70b-specdec")
    val Llama_3_2_1b_Preview = Value("llama-3.2-1b-preview")
    val Llama_3_2_3b_Preview = Value("llama-3.2-3b-preview")
    val Llama_3_2_11b_Vision_Preview = Value("llama-3.2-11b-vision-preview")
    val Llama_3_2_90b_Vision_Preview = Value("llama-3.2-90b-vision-preview")
    val Allam_2_7b = Value("allam-2-7b")
}

case class ChatCompletionRequest(
                                    model: String,
                                    messages: Seq[ChatMessage],
                                    temperature: Option[Double] = None,
                                    top_p: Option[Double] = None,
                                    max_tokens: Option[Int] = None,
                                    stream: Option[Boolean] = None,
                                    stop: Option[Seq[String]] = None
                                )

object ChatCompletionRequest {
    implicit val format: Format[ChatCompletionRequest] = Json.format[ChatCompletionRequest]

    val systemPrompt = """당신은 타로 카드를 해석하는 전문가입니다.
                         |한국어로만 답변하며, 중국어나 다른 언어를 섞지 마세요.
                         |HTML 태그나 <think>와 같은 태그를 사용하지 마세요.
                         |오직 타로 카드의 의미와 해석만 제공하세요.""".stripMargin


    def readTarot(tarotCards: String, groqModel: GroqModel) = {
        ChatCompletionRequest(
            model = groqModel.toString,
            messages = Seq(
                ChatMessage("system", systemPrompt),
                ChatMessage("user", s"${tarotCards}에 대한 3 카드 타로의 제너럴 리딩을 해주세요. 리딩 결과에 지지가 되는 메시지를 담아주세요. 모든 답변은 한국어로 작성해주세요. 답변에는 지지하는 내용을 담는다는 말을 하지 마세요.")
            ),
            temperature = Some(1.1)
        )
    }
}

case class ChatCompletionChoice(
                                   index: Int,
                                   message: ChatMessage,
                                   finish_reason: String
                               )

object ChatCompletionChoice {
    implicit val format: Format[ChatCompletionChoice] = Json.format[ChatCompletionChoice]
}

case class ChatCompletionUsage(
                                  prompt_tokens: Int,
                                  completion_tokens: Int,
                                  total_tokens: Int,
                                  total_time: Double
                              )

object ChatCompletionUsage {
    implicit val format: Format[ChatCompletionUsage] = Json.format[ChatCompletionUsage]
}

case class ChatCompletionResponse(
                                     id: String,
                                     `object`: String,
                                     created: Long,
                                     model: String,
                                     choices: Seq[ChatCompletionChoice],
                                     usage: ChatCompletionUsage,
                                     system_fingerprint: String
                                 )

object ChatCompletionResponse {
    implicit val format: Format[ChatCompletionResponse] = Json.format[ChatCompletionResponse]
}
