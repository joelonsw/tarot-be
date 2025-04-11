package controllers

import services.ResultService

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ResultController @Inject()(resultService: ResultService) extends TarotBaseController {

    def getTarotResult() = Action.async { implicit request =>
        resultService.getTarotResult().map { result =>
            Ok(result)
        }
    }
}
