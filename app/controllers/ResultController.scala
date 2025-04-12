package controllers

import services.ResultService

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ResultController @Inject()(resultService: ResultService) extends TarotBaseController {

    def getTarotResult(selectedCards: String) = Action.async { implicit request =>
        val tarotCards = selectedCards.split(",").toList
        if (tarotCards.size == 3) {
            resultService.getTarotResult(selectedCards).map { result =>
                println(result)
                Ok(result)
            }
        } else {
            Future.successful(BadRequest("3장의 카드를 선택해주세요."))
        }
    }
}
