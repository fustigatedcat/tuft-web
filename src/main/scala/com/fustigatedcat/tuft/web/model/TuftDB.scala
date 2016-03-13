package com.fustigatedcat.tuft.web.model

import org.squeryl.Schema

import SquerylMode._

object TuftDB extends Schema {

  val user = table[User]

  val userConnection = table[UserConnection]

  val userRequestsSent = oneToManyRelation(user, userConnection).via((u, uc) => u.id === uc.requestor_user_id)

  val userRequestsReceived = oneToManyRelation(user, userConnection).via((u, uc) => u.id === uc.requestee_user_id)

}
