## Project specification
This project will be documented in English

I am studying for a bachelor's in computer science

### Outline
This program will find try to find an optimal path from a given point to the goal as specified in the input and hopefully display its progress in real time. The data structures picked were simply the ones required by the pathfinding algorithms that will be implemented and those required for displaying them.

### Data structures used
- arrays for representing maps, general data storage and whatnot
- a priority queue for certain pathfinding algorithms
- other structures required for the implementation of certain algorithms

### Algorithms used
- Dijkstra and/or A*
- Jump Point Search
- (maybe) IDA*

### Program input
The program will take a lossless image whose pixels will represent whether a certain node is traversable or not.

### Expected complexity
The memory complexity will always be at least O(n) where n is the number of pixels in the input. Auxiliary space used will vary based on the algorithm(s) selected for pathfinding, with JPS probably taking up less space compared to Dijkstra or A*

