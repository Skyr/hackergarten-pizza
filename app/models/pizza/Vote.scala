package models.pizza

import scala.collection.mutable


case class Vote(pizzaId: Int, userProviderKey: String, vote: Int)

object VoteRepo {
  val votes = new mutable.MutableList[Vote]()
}
