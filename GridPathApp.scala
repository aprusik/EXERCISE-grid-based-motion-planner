import scala.io.StdIn
import stateSearch._

final class GridPathApp() {
  val cols: Int = StdIn.readLine().toInt
  val rows: Int = StdIn.readLine().toInt

  val world = new GridWorld(rows, cols)

  private val robot = world.make()

  def printWorld(): Unit = world.print()
}

object GridPathApp {
  def main(args: Array[String]): Unit = {
    if (args.length < 1 || args.length > 2)
      println("usage: run.sh <algorithm> < <VacuumWorld>") // Incorrect usage
    else {

      // read world
      val app = new GridPathApp()
//      app.printWorld()

      val v = app.robot
      var result: Option[Robot] = Option.empty

      val alg: Option[StateSearch[Robot]] = args(0) match {
        case "depth-first" =>
          Some(new DepthFirst[Robot](v, Robot.expand, Robot.testGoal))
        case "depth-first-id" =>
          Some(new DepthFirstId[Robot](v, Robot.expand, Robot.testGoal))
        case "uniform-cost" =>
          Some(new UniformCost[Robot](v, Robot.expand, Robot.testGoal))
        case "a-star" =>
          if (args.length != 2) {
            println("usage: run.sh a-star <h0, h1, OR h2> < <VacuumWorld>")
            Option.empty
          }
          else args(1) match {
            case "h0" =>
              Some(new AStar[Robot](
                v, Robot.expand, Robot.testGoal, Robot.h0))
            case "h1" =>
              Some(new AStar[Robot](
                v, Robot.expand, Robot.testGoal, Robot.h1))
            case "h2" =>
              Some(new AStar[Robot](
                v, Robot.expand, Robot.testGoal, Robot.h2))
            case _ => Option.empty
          }
        case _ => Option.empty
      }
      if (alg.isEmpty)
        println("usage: run.sh <algorithm type> < <VacuumWorld>") // Incorrect usage
      else result = alg.get.find()

      if (result.isDefined) result.get.printHist()
      else println("Unable to find solution.")
    }
  }
}