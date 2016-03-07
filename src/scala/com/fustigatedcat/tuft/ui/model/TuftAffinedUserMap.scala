package com.fustigatedcat.tuft.ui.model

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.squeryl.dsl.CompositeKey2
import SquerylMode._

case class TuftAffinedUserMap( @Column(name = "tuft_id") tuftId : Long,
                               @Column(name = "affined_user_id") affinedUserId : Long) extends KeyedEntity[CompositeKey2[Long, Long]]{

  override def id = compositeKey(tuftId, affinedUserId)

}
