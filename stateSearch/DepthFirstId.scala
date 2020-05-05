package stateSearch

import scala.annotation.tailrec

class DepthFirstId[A](start: A, expand: A => List[A], test: A => Boolean)
  extends StateSearch[A] {

  var depthLimit = 0

  @tailrec
  protected final override def search(): Option[DFSINode] = {
    val df = new DepthFirst[A](start, expand, test, depthLimit)
    depthLimit += 1
    val dfr = df.find()
    if (dfr.isDefined) Some(DFSINode(dfr.get, depthLimit))
    else search()
  }

  protected final case class DFSINode(data: A, depth: Int) extends Node
}