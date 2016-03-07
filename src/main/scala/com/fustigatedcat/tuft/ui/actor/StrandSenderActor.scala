package com.fustigatedcat.tuft.ui.actor

import akka.actor.Actor
import com.fustigatedcat.tuft.ui.actor.message.{PostToUserAffined, PostStrand}
import com.fustigatedcat.tuft.ui.model.{Tuft, SquerylMode, User, Strand}
import org.slf4j.LoggerFactory
import SquerylMode._
import org.squeryl.Session

class StrandSenderActor extends Actor {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def receive: Receive = {
    case PostStrand(id) => inTransaction {
      Session.currentSession.setLogger(str => logger.debug(str))
      Strand.getById(id) match {
        case Some(strand) => {
          logger.debug(s"Found strand $id")
          TuftActorSystem.userAffinedSenders ! PostToUserAffined(strand)
        }
        case _ => logger.error(s"Failed to get strand $id")
      }
    }
    case msg => logger.error(s"Invalid message $msg")
  }

}
