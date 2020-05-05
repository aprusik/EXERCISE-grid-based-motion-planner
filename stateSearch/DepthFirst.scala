package stateSearch

import scala.annotation.tailrec

class DepthFirst[A](start: A, expand: A => List[A], test: A => Boolean,
                    protected val limit: Int = 100000) extends StateSearch[A] {

  private val depthLimit = limit
  private final var open = List(DFSNode(start, 0, Option.empty))

  @tailrec
  protected final override def search(): Option[DFSNode] = {
    if (open.isEmpty) throw new IllegalStateException("Empty open list")
    var node = pop()
    if (test(node.data)) Some(node)
    else {
      handleDepth(node) match {
        case Some(value) =>
          node = value
          val children = expand(node.data)
          for (child <- children) {
            val newChild = DFSNode(child, node.depth + 1, Some(node))
            if (!checkDuplicate(newChild))
              open = newChild +: open
          }
          search()
        case None => Option.empty
      }
    }
  }

  protected final def pop(): DFSNode = {
    val head = open.head
    open = open.tail
    head
  }

  protected def handleDepth(node: DFSNode): Option[DFSNode] = {
    if (node.depth > depthLimit) {
      val end = endBranch(depthLimit)
      if (end.isEmpty) Option.empty
      else Some(end.get)
    } else Some(node)
  }

  @tailrec
  protected final def endBranch(limit: Int): Option[DFSNode] = {
    if (open.isEmpty) Option.empty
    else {
      val head = pop()
      if (head.depth < limit) Some(head)
      else endBranch(limit)
    }
  }

  protected final def checkDuplicate(check: DFSNode): Boolean = {
    @tailrec
    def helper(node: DFSNode): Boolean = {
      if (node.parent.isEmpty) false
      else if (check.data == node.parent.get.data) true
      else helper(node.parent.get)
    }
    helper(check)
  }

  protected final case class DFSNode(data: A, depth: Int,
                             parent: Option[DFSNode]) extends Node
}