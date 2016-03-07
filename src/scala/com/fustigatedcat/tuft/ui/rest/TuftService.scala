package com.fustigatedcat.tuft.ui.rest

import java.sql.Timestamp
import java.util.Calendar

import com.fustigatedcat.tuft.ui.model.{User, Tuft, LoggedInUser}
import net.liftweb.http.{OkResponse, BadResponse, LiftResponse, NotFoundResponse}
import net.liftweb.http.rest.RestHelper
import com.fustigatedcat.tuft.ui.model.SquerylMode._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonDSL._

object TuftService extends RestHelper {

  def getTuftList : LiftResponse = LoggedInUser.get match {
    case Some(user) => user.tufts.map(t => ("id" -> t.id) ~ ("name" -> t.name)) : JValue
    case _ => NotFoundResponse()
  }

  def createTuft(tuft : JValue) : LiftResponse = LoggedInUser.get match {
    case Some(user) => (tuft \ "name").extractOpt[String] match {
      case Some(name) => {
        user.tufts.associate(Tuft (0, user.id, name, new Timestamp(Calendar.getInstance().getTimeInMillis)))
        OkResponse()
      }
      case _ => BadResponse()
    }
    case _ => NotFoundResponse()
  }

  def getTuft(id : Long) : LiftResponse = LoggedInUser.get match {
    case Some(user) => user.tufts.where(t => t.id === id).headOption match {
      case Some(tuft) => ("id" -> tuft.id) ~
        ("name" -> tuft.name) ~
        ("affinedUsers" -> tuft.affinedUsers.map(u =>
          ("id" -> u.id) ~ ("name" -> s"${u.firstName} ${u.lastName}")
        )) : JValue
      case _ => NotFoundResponse()
    }
    case _ => NotFoundResponse()
  }

  def getStrandsForTuft(id : Long) : LiftResponse = LoggedInUser.get match {
    case Some(user) => user.tufts.find(_.id == id) match {
      case Some(tuft) => tuft.strands.map(s => ("posterId" -> s.posterId) ~ ("message" -> s.message) ~ ("posted" -> s.posted.getTime)) : JValue
      case _ => NotFoundResponse()
    }
    case _ => NotFoundResponse()
  }

  def affineUserToTuft(id : Long, userId : Long) : LiftResponse = LoggedInUser.get match {
    case Some(me) => User.getById(userId) match {
      case Some(them) if User.areUsersConnected_?(me.id, userId) => me.tufts.where(t => t.id === id).headOption match {
        case Some(tuft) if tuft.affinedUsers.where(u => u.id === userId).headOption.isDefined => OkResponse()
        case Some(tuft) => {
          tuft.affinedUsers.associate(them)
          "success" -> "ok" : JValue
        }
        case _ => NotFoundResponse()
      }
      case _ => BadResponse()
    }
    case _ => NotFoundResponse()
  }

  def unaffineUserFromTuft(id : Long, userId : Long) : LiftResponse = LoggedInUser.get match {
    case Some(me) => me.tufts.where(t => t.id === id).headOption match {
      case Some(tuft) => tuft.affinedUsers.where(u => u.id === userId).headOption match {
        case Some(them) => {
          tuft.affinedUsers.dissociate(them)
          "success" -> "ok" : JValue
        }
        case _ => "success" -> "ok" : JValue
      }
      case _ => NotFoundResponse()
    }
    case _ => NotFoundResponse()
  }

  serve("api" / "tufts" prefix {
    case Nil JsonGet _ => getTuftList
    case Nil JsonPost req => createTuft(req._1)
    case id :: Nil JsonGet _ => getTuft(id.toLong)
    case id :: "strands" :: Nil JsonGet _ => getStrandsForTuft(id.toLong)
    case id :: "affined-users" :: Nil JsonPut req => affineUserToTuft(id.toLong, (req._1 \ "userId").extractOrElse[Long](0L))
    case id :: "affined-users" :: them :: Nil JsonDelete _ => unaffineUserFromTuft(id.toLong, them.toLong)
  })

}
