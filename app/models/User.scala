package models

import com.mohiva.play.silhouette.api.Identity


case class User(providerKey: String, name: String) extends Identity
