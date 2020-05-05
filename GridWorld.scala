import scala.collection.mutable
import scala.io.StdIn

class GridWorld(rows: Int, cols: Int) {
  val maxRows: Int = rows - 1
  val maxCols: Int = cols - 1
  var robot: Robot = _ // forward declare robot variable

  // world array
  val world: Array[Array[Char]] = Array.ofDim[Char](rows, cols)

  // populate world and make robot
  def make(): Robot = {
    var dirt: mutable.Set[Int] = mutable.Set() // keep track of goal locations

    // populate world array
    var r = 0
    while (r < rows) {
      val row = StdIn.readLine()
      var c = 0
      for (col <- row) {
        if (col == '@') { // robot start pos
          if (robot != null) // already have a start pos
            throw new IllegalStateException(
              "Multiple robot start positions found")
          robot = new Robot(r, c, this)
          world(r)(c) = '_' // add empty space under robot
        } else world(r)(c) = col // add entry into world

        if (col == '*') dirt += r * (maxCols+1) + c // add goal to set

        c += 1
      }
      r += 1
    }

    if (robot != null) { robot.goal = dirt; robot }
    else throw new IllegalStateException("No robot start position found")
  }

  // print initial world and size
  def print(): Unit = {
    if (robot == null) println("Cannot print world: world not made yet!")
    else {

      // Print world size
      println("size: " + cols + "x" + rows)

      // print world
      for (r <- 0 until rows) {
        var row = ""
        for (c <- 0 until cols)
          if (robot.currRow == r && robot.currCol == c) row += "@" + " " // check for robot
          else if (world(r)(c) == '*' &&
            !robot.goal(r * (maxCols+1) + c)) row += "_" + " " // check if goal visited
          else row += world(r)(c) + " "
        println(row)
      }
    }
  }

  // returns the contents of the selected cell
  def apply(row: Int, col: Int): Char = world(row)(col)
}
