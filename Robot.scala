import scala.collection.mutable

class Robot(var currRow: Int, var currCol: Int, world: GridWorld,
            var goal: mutable.Set[Int] = mutable.Set(),
            var history: List[Char] = Nil, var moveCount: Int = 0) {
  var id = 0
  val maxCols: Int = world.maxCols
  def index: Int = currRow * (maxCols+1) + currCol

  def copy(): Robot = {
    val newVac = new Robot(currRow, currCol, world, goal.clone(), history, moveCount)
    newVac
  }

  // method adapted from https://alvinalexander.com/scala/how-to-define-equals-hashcode-methods-in-scala-object-equality
  override def equals(that: Any): Boolean = {
    that match {
      case that: Robot => that.isInstanceOf[Robot] &&
        this.hashCode() == that.hashCode()
      case _ => false
    }
  }

  override def hashCode: Int =
    (goal.hashCode().toString + currRow + currCol).toLong.hashCode()

  def v(): Boolean = {
    if (world(currRow, currCol) == '*' && goal.contains(index)) {
      goal -= index
      history = 'V' +: history
      true
    } else false
  }
  def u(): Boolean = move(0, -1, 'N')
  def d(): Boolean = move(0, 1, 'S')
  def l(): Boolean = move(-1, 0, 'W')
  def r(): Boolean = move(1, 0, 'E')

  def move(x: Int, y: Int, dir: Char): Boolean = {
    val row = currRow + y
    val col = currCol + x
    if (!(row > world.maxRows || col > world.maxCols || row < 0 || col < 0) && // not out of bounds
      world(row, col) != '#') { // not wall
      currRow = row
      currCol = col
      history = dir +: history
      moveCount += 1
      true
    } else false
  }

  def printHist(): Unit = {
    for (dir <- history.reverse) println(dir)
    println(id + 1 + " nodes generated")
    println(Robot.expansions + " nodes expanded")
//    print(history.size + " steps taken")
  }
}

object Robot {
  var globalId = 0
  var expansions = 0
  def coords(index: Int, maxCols: Int): (Int, Int) =
    (index / (maxCols+1), index % (maxCols+1))
  def distance(i1: Int, i2: Int, maxCols: Int): Int = {
    val y1 = coords(i1, maxCols)._1
    val x1 = coords(i1, maxCols)._2
    val y2 = coords(i2, maxCols)._1
    val x2 = coords(i2, maxCols)._2
    val y = y1 - y2
    val x = x1 - x2
    math.sqrt(y*y + x*x).toInt
  }
  def testGoal(v: Robot): Boolean = v.goal.isEmpty

  def h0(v: Robot): Double = 0

  def h1(v: Robot): Double = {
    val distances = v.goal.map(d => distance(v.index, d, v.maxCols))
//    distances.sum / distances.size
    distances.max
  }

  def h2(v: Robot): Double = {
    var rem = v.goal
    if (rem.nonEmpty) {
      var point = rem.minBy(x => distance(v.index, x, v.maxCols)) //find closest point
      var dist = distance(v.index, point, v.maxCols)
      rem = rem - point
      while (rem.nonEmpty) {
        val oldPoint = point
        point = rem.minBy(x => distance(point, x, v.maxCols)) //find closest to last point
        dist += distance(oldPoint, point, v.maxCols)  //add distance to total distance
        rem = rem - point //remove point from list
      }
      dist //return sum of distances
    } else 0
  }

  def expand(vac: Robot): List[Robot] = {
    expansions += 1
    var children: List[Robot] = Nil

    def createChild(action: Robot => Boolean): Unit = {
      val child = vac.copy()
      if (action(child)) {
        globalId += 1
        child.id = globalId
        children = child +: children
      }
    }
    createChild(v)
    createChild(u)
    createChild(d)
    createChild(l)
    createChild(r)
    children
  }

  def v(v: Robot): Boolean = v.v()
  def u(v: Robot): Boolean = v.u()
  def d(v: Robot): Boolean = v.d()
  def l(v: Robot): Boolean = v.l()
  def r(v: Robot): Boolean = v.r()
}
