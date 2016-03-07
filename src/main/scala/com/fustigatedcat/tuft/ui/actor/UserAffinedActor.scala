package com.fustigatedcat.tuft.ui.actor

import akka.actor.Actor
import com.fustigatedcat.tuft.ui.actor.message.PostToUserAffined
import com.fustigatedcat.tuft.ui.model.SquerylMode._
import com.fustigatedcat.tuft.ui.model.Tuft
import org.slf4j.LoggerFactory

class UserAffinedActor extends Actor {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def receive: Receive = {
    case PostToUserAffined(strand) => inTransaction {
      logger.debug(s"Handling strand ${strand.id}")
      Tuft.getTuftsForPoster(strand.posterId).foreach(tuft => {
        println(s"Posting to ${tuft.id}")
        strand.tufts.associate(tuft)
      })
    }
  }

}
