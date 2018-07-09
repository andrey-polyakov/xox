XOX

# How to run
Just call main method of T3controller. If there is t3.properties in the classpath properties will ne read, otherwise default values will apply.
In order to specify folder containing the file set -DconfigFolder=folder

# Overview
There is a number of moving parts in this engine. On the higher level there is a controller, view and the game itself.
Game enforces that nothing goes against the rules, governs turn order and such. Also, there is an important part called
strategy which leverages internal data structures of the game (covered below).

## Game engine at a lower level
Game assumes cells of the board form shapes. For example, those could be diagonals, vertical
and horizontal lines. THe very same cells may belong to multiple shapes. Potentially, other shapes may be easily accommodated.

<br><br>
Each individual strip is handled by so called aggregator which in turn internally operates 
a fragmented data structured named PartitionedIsland. This underlying structure represents
partitions of the given shape occupied by the players of the game. Nodes correspond 
board cell which when belong to the same user form partitions. Partitions processed as one unit 
regardless of how many cells (wrapped in nodes resembling double-linked list) it contains. This along with various short 
cuts allows Log(n) traversal of the partitions in which we are interested at higher level. Finally, this approach divides complex
task into smaller sub-tasks.


# Strategy
Strategy is a concept that leverages two classes of aggregator output, namely, winning coordinates and best move.
<br>
<br>
Regarding winning coordinates, basic strategy implemented in this project uses those to win should there be such an 
opportunity or to prevent opponents from winning the following turn. In case of multiple threats it picks the one
providing opportunity to multiple opponents.
##Prioritized moves
In less exciting scenario, the strategy favours shapes already started by the computer and devalues shapes which will
never lead to victory. For instance, among the following combination the first one is best as it progressed more than the others 
and has enough slots for hypothetical victory.
<br>
Based on several factors each cell is rated and the best rated cell is selected by the computer.
<br>
OOO___ - best, minimum possible number of empty cells combined with cells of the player<br>
OO____ - second best because it is shorter<br>
O___X_ - spoiled, not winnable<br>
XXX___ - just as bas as the one above because there is no cells of our player<br>
 
## Room for improvement

Implement the following strategies:

- If, on the next move, my opponent can set up a fork, block that possibility by moving into the square that is common to his two winning combinations.

- If I can make a move that will set up a winning combination for myself, do it. But ensure that this move does not force the opponent into establishing a fork.

Refine layers and interfaces. Improve test coverage of higher level classes.

# Other notes
Typically, I use either groovy or Lombok to auto-generate boilerplate code such as getter & setters.
