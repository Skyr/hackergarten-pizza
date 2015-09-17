package models.pizza

import scala.collection.mutable


case class Pizza(id: Int, name: String)

object PizzaRepo {
  val pizzas = new mutable.HashMap[Int, Pizza]
  pizzas.put(0, Pizza(0, "Pizza Speciale"))
  pizzas.put(1, Pizza(1, "Pizza Funghi"))
}