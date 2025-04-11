package controllers

import play.api.mvc._

import javax.inject._


@Singleton
class HomeController @Inject() extends TarotBaseController {

    def index() = Action { implicit request: Request[AnyContent] =>
        Ok("tarot-be")
    }
}
