package com.fustigatedcat.tuft.ui.rest

import java.sql.Timestamp
import java.util.Calendar

import com.fustigatedcat.tuft.ui.actor.TuftActorSystem
import com.fustigatedcat.tuft.ui.actor.message.PostStrand
import com.fustigatedcat.tuft.ui.model.{Strand, LoggedInUser}
import net.liftweb.http.{AcceptedResponse, BadResponse, NotFoundResponse, LiftResponse}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.{JObject, JValue}
import com.fustigatedcat.tuft.ui.model.SquerylMode._
import net.liftweb.json.JsonDSL._

object StrandService extends RestHelper {

  def postStrand(strand : JValue) : LiftResponse = LoggedInUser.get match {
    case Some(user) => (strand \ "message").extractOpt[String] match {
      case Some(message) => {
        val newStrand = user.sentStrands.associate(
          Strand(
            0,
            user.id,
            new Timestamp(Calendar.getInstance().getTimeInMillis),
            message
          )
        )
        TuftActorSystem.strandSenders ! PostStrand(newStrand.id)
        AcceptedResponse()
      }
      case _ => BadResponse()
    }
    case _ => NotFoundResponse()
  }

  def getPostedStrands : LiftResponse = LoggedInUser.get match {
    case Some(user) => user.sentStrands.map(s =>
      ("message" -> s.message) ~ ("posted" -> s.posted.getTime)
    ) : JValue
    case _ => NotFoundResponse()
  }

  serve("api" / "strands" prefix {
    case Nil JsonPost req => postStrand(req._1)
    case "posted" :: Nil JsonGet _ => getPostedStrands()
  })

}
