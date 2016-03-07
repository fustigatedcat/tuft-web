package com.fustigatedcat.tuft.ui.rest

import com.fustigatedcat.tuft.ui.model.{User, LoggedInUser}
import net.liftweb.http.{NotFoundResponse, LiftResponse}
import net.liftweb.http.rest.RestHelper
import org.slf4j.LoggerFactory
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonDSL._

object UserService extends RestHelper {

  val logger = LoggerFactory.getLogger(this.getClass)

  def getUserConnections : LiftResponse = LoggedInUser.get match {
    case Some(me) => User.getConnectionsForUser(me.id).map(user => {
      ("id" -> user.id) ~ ("name" -> s"${user.firstName} ${user.lastName}")
    }) : JValue
    case _ => NotFoundResponse()
  }

  serve("api" / "users" prefix {
    case "me" :: "connections" :: Nil JsonGet _ => getUserConnections()
  })

}
