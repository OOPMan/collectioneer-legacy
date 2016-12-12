package com.github.oopman.collectioneer.database

import slick.jdbc.JdbcProfile

trait DriverComponent {
  val driver: JdbcProfile
}

