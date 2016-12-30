package com.github.oopman.collectioneer.model

import slick.jdbc.JdbcProfile

trait DriverComponent {
  val driver: JdbcProfile
}

