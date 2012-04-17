package views.html

import helper.FieldConstructor

/**
 * Author: chris
 * Created: 4/17/12
 */

object MyBootstrapHelpers {
  implicit val myFields = FieldConstructor(bootstrapHoriz.f)
}
