package models.pizza

import scala.collection.mutable


case class Pizza(id: Int, name: String)

object PizzaRepo {
  val pizzas = new mutable.HashMap[Int, Pizza]
}