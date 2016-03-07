package com.fustigatedcat.tuft.ui.actor

import akka.actor.{Props, ActorSystem}
import akka.routing.RoundRobinPool

object TuftActorSystem {

  val system = ActorSystem("tuft-actor-system")

  val strandSenders = system.actorOf(RoundRobinPool(nrOfInstances = 10).props(Props[StrandSenderActor]))

  val userAffinedSenders = system.actorOf(RoundRobinPool(nrOfInstances = 10).props(Props[UserAffinedActor]))

}
