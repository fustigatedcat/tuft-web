package com.fustigatedcat.tuft.ui.model

import java.sql.Timestamp

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

import SquerylMode._

object Strand {

  def getById(id : Long) : Option[Strand] = {
    from(TuftDB.strand)(s => where(s.id === id) select s).headOption
  }

}

case class Strand( @Column("strand_id") id : Long,
                   @Column("poster_id") posterId : Long,
                   @Column("posted") posted : Timestamp,
                   @Column("message") message : String) extends KeyedEntity[Long] {

  lazy val poster = TuftDB.posterToSentStrands.right(this)

  lazy val tufts = TuftDB.tuftToStrands.right(this)

}
