package services

import javax.inject._
import scala.concurrent.Future

@Singleton
class ResultService {

    def getTarotResult(): Future[String] = {
        Future.successful("Reading Result")
    }

}
