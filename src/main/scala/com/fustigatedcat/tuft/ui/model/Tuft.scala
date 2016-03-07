package com.fustigatedcat.tuft.ui.model

import java.sql.Timestamp

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

import SquerylMode._

object Tuft {

  def getTuftsForPoster(posterId : Long) : List[Tuft] = {
    join(TuftDB.tuft,TuftDB.tuftAffinedUserMap)((t,taum) =>
      where(taum.affinedUserId === posterId and t.userId <> posterId)
      select t
      on(taum.tuftId === t.id)
    ).toList
  }

}

case class Tuft( @Column("tuft_id") id : Long,
                 @Column("user_id") userId : Long,
                 @Column("name") name : String,
                 @Column("created") created : Timestamp) extends KeyedEntity[Long] {

  lazy val user = TuftDB.userToTuft.right(this)

  lazy val strands = TuftDB.tuftToStrands.left(this)

  lazy val affinedUsers = TuftDB.tuftAffinedUsers.left(this)

}
