package com.fustigatedcat.tuft.ui.model

import SquerylMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.squeryl.dsl.CompositeKey2

case class UserConnection( @Column("requestor_id") requestorId : Long,
                           @Column("requestee_id") requesteeId : Long,
                           @Column("acknowledged") acknowledged : Boolean) extends KeyedEntity[CompositeKey2[Long,Long]] {

  override def id = compositeKey(requestorId, requesteeId)

}
