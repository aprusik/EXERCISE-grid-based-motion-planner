# Grid-Based Motion Planner Exercise
Plans a route through an arbitrary grid with blocked, empty, start and goal cells so that each goal is visited.

## Input Syntax
1. Line with number of collumns in grid (`col`)
2. Line with number of rows in grid (`row`)
3. `row` rows, each `col` characters long consisting of the following characters:
   * `_` for empty cells
   * `#` for blocked cells
   * `*` for goal cells
   * `@` for the start cell (only one cell can be a start position)
   
### Example Input
```
4
4
___*
_#_*
__##
_@_*
```

## Output
A series of cardinal direction instructions (`N`, `S`, `E`, `W`) planning a path through the grid so that each goal cell is visited, starting at the starte cell. When a goal cell is visited, `V` is displayed instead of a directional instruction to indicate the goal has been reached.

Each instruction is presented on its own line in order from top to bottom.

The instructions are followed by the number of search nodes generated and expanded in the process of the calculation.

### Example Output
```
E
E
V
W
W
W
N
N
N
E
E
E
V
S
V
114 nodes generated
57 nodes expanded
```

## Build/Run Instructions

### Requirements
* Scala

### Usage
The program accepts 1-2 command line arguments:
1. algorithm: can be `depth-first`, `depth-first-id`, `uniform-cost`, or `a-star`
2. heuristic: if algorithm is `a-star`, which heuristic to use; can be `h0`, `h1`, or `h2`

The Grid is then accepted from standard input.

### Windows (PowerShell)
* Build with: `scalac stateSearch/*.scala`, and `scalac *.scala`
* Run with: `scala GridPathApp <algorithm> <heuristic>`
  * Recommended: pipe in the contents of a plaintext file with the grid like so: `Get-Content <grid-file> | scala GridPathApp <algorithm> <heuristic>`
  
### MacOS / Linux Shell
Use included build and run scripts.
You can pipe the contents of a plaintext file into the run script and it should send it to the program's standard input to run.
  
Let me know if you have any problems building or running the program.
  
## Algorithms and Discussion

### Implementation & Class Descriptions

* **GridPathApp**: main execution class which parses the inputs and creates the initial GridWorld

* **GridWorld**: parses input to create world, holds static information associated with the world (i.e. obsticals, empty spaces, goals, start locations, etc.); also creates the "robot", which is the traversal entity.

* **Robot**: Holds dynamic information about the "robot"’s location, it’s movement history (Scala List for easy popping), the goals remaining (a mutable set of INTs which represent the index of the goal in the array because a goal cannot be in the same place twice).  There is also an ID which serves for easy node identification and a count of the nodes generated.

  * **Vacuum hash function**: there is a hash function on the vacuum that simply strings the goal set hash and location together to guarantee uniqueness, then hashes that.
  
### Algorithms
GridPathApp parses the given argument to either “depth-first” or “uniform-cost”, then creates and runs the associated algorithm class.  Both algorithms are implemented using a tail-recursive structure as Scala is a recursion-optimized language. Nodes for the algorithms are implemented via the use of a case class which packages the node information for easy access and usage. There is a lot of duplicated code between the two of these classes which should probably be consolidated into a generic State Search class which they both inherit.

#### Depth First Search Algorithm (DFSStateSearch.scala)
https://en.wikipedia.org/wiki/Depth-first_search

There is a depth limit set to 1,000,000 as to not allow the algorithm to run infinitely but allow for very large worlds.  This can be modified by changing the value of the DEPTH_LIMIT constant. A weak duplicate checking method is also implemented for cycle checking which recursively traverses the parent nodes.

This algorithm is **NOT** optimal, it only returns the first solution found. It uses very little space, but is slow. If the correct path requires more than the depth bound of steps, it will not be found.

#### Depth First Search-Iterative Deepening Algorithm (DepthFirstID.scala)
https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search

This algorithm is implemented by utilizing the standard Depth-First Search algorithm implemented above, with a new optional parameter of maxDepth. If no solution is found at a depth of 0 (the root), the maxDepth is increased and the search is run again until one is found.

This algorithm is slow, but uses little space and unlike normal Depth First Search, it finds an optimal solution.

#### Uniform Cost Algorithm (UCStateSearch.scala)
A precursor to [Dijkstra's Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm).

There is a cycle checking method that tests a HashSet of generated nodes to ensure no duplicates are expanded. A HashSet was used because each node state only needs to exist once within it; it uses the hashing algorithm implemented in the Robot class.

This algorithm is slow, and uses a lot of space, but is guarenteed to find and optimal solution (least number of steps).

#### A* (AStar.scala)
https://en.wikipedia.org/wiki/A*_search_algorithm

The most sophisticated of the search algorithms here; this algorithm is implemented by building upon a modified version of the UniformCost.scala class. The modification adds a newNode function which is used in the search function to determine the node weight.  newNode is overridden in AStar.scala to include a heuristic value which is calculated via a method passed in during construction.

Its time and space complexity vary with the particular heuristic used, but will generally be at least as good as Uniform Cost. If the heuristic is admissable (the three included are), it is guarenteed to find an optimal solution.

##### Heuristic 0 (h0)
This heuristic simply returns 0, resulting in an algorithm that runs identically to Uniform-Cost.

##### Heuristic 1 (h1)
This heuristic returns the average of all distances to the goals from the robot. This heuristic is admissible because the maximum average distance would occur when one node is left, leaving the Euclidian distance as a lower bound to the remaining movement over walls and the grid.

##### Heuristic 2 (h2)
This heuristic plots the optimal Euclidian distance between all nodes assuming there are no walls, and the robot can travel straight from one node to another. This is accomplished by finding the distance from the robot to the nearest node, then the distance from that node to the next, and so on, returning the total distance. This is admissible because the robot must travel through each node, and the optimal Euclidian path through each node can be calculated in the above way because if a node other than the nearest is chosen in the chain, the total distance will be greater. The robot cannot travel on Euclidian paths; it must make its way on a grid, which uses more or the same movement when rounded as the Euclidian path, and it also may have to find its way around walls, ensuring that h2 is a lower bound to the remaining distance.
