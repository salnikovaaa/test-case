package dev.zio.quickstart.other

import scala.util.control.Breaks.{break, breakable}

object AdditionalFunc {
  def findIntoFile (line1: String, line2: String, path: String) : Boolean = {
    val file = scala.io.Source.fromFile(path).getLines()
    var res = false;
    breakable{for (x <- file if (x.toString.equals(line1.toString) == true || x.toString.equals(line2.toString) == true)) {
      res = true
      break
    }}
    return res;
  }

}
