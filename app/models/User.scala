package models

import com.mohiva.play.silhouette.api.{LoginInfo, Identity}


case class User(loginInfo : LoginInfo, name : String) extends Identity
