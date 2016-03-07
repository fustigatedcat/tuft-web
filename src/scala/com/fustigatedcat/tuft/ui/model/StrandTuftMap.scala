package com.fustigatedcat.tuft.ui.model

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.squeryl.dsl.CompositeKey2
import SquerylMode._

case class StrandTuftMap( @Column("strand_id") strandId : Long,
                          @Column("tuft_id") tuftId : Long) extends KeyedEntity[CompositeKey2[Long, Long]] {

  override def id = compositeKey(strandId, tuftId)

}
