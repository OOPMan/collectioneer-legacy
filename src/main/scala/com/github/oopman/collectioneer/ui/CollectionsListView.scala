package com.github.oopman.collectioneer.ui


import com.github.oopman.collectioneer.model.Collection

import scalafx.geometry.Insets
import scalafx.scene.control.{ListCell, ListView}
import scalafx.scene.layout.HBox
import scalafx.scene.text.Text

/**
  * Created by adamj on 2016/12/19.
  */
class CollectionsListView extends ListView[Collection] {

  /**
    * A custom cell factory is used to render the Collections
    */
  cellFactory = { _ =>
    new ListCell[Collection] {
      item.onChange { (_, _, newValue) =>
        graphic = new HBox {
          padding = Insets(5, 10, 5, 10)
          children = Seq(
            // TODO: Use proper controls
            new Text { text = newValue.name },
            new Text { text = if(newValue.active) "X" else "O" }
          )
        }

      }
    }
  }
}
