# Implementation document
### Project structure
The project is mainly split into the GUI and pathing parts. 

The GUI part houses everything related to showing the progress of the pathing to the user, it mostly stays confined in its own part and doesn't fiddle that much with the pathing part. 

The pathing part has its own representation of the map that it's solving and will very frequently ask the GUI to draw its progress for the user. Pathing is run on its own thread such that the GUI can be updated while the algorithm is solving the map.

### Implemented time and space complexities
A\* mostly works as it should, so its time and space complexities are hopefully close to what they're supposed to. It uses a priority queue such that the nodes closest to the goal via Euclidean distance is handled first. 

JPS works the same way as A\*, and has similar complexities.

### Comparative performance
A\* works pretty fast, it can solve a very complex 1000x1000 labyrinth in a couple of seconds.
JPS usually takes about a tenth of the time A\* takes.

### Possible flaws and improvements
- UX is not very intuitive
- Much more

### Sources
For A\* I mostly got by with the help of its [Wikipedia article](https://en.wikipedia.org/wiki/A*_search_algorithm)

List for sources used for JPS development (probably not exhaustive):
- http://users.cecs.anu.edu.au/~dharabor/data/papers/harabor-grastien-aaai11.pdf
- https://www.gamedev.net/tutorials/programming/artificial-intelligence/jump-point-search-fast-a-pathfinding-for-uniform-cost-grids-r4220/
- https://www.utupub.fi/bitstream/handle/10024/148054/DI_tyo_Pertti_Ranttila_final.pdf
- https://zerowidth.com/2013/a-visual-explanation-of-jump-point-search.html