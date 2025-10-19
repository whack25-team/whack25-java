# WHack2025 Project - Hot N Cold

Robots start at a given house and have a destination house to reach in the shortest amount of time.

Robots are designed to travel as follows:
- Avoid traffic jams
- If multiple routes are available of similar length, evenly distribue traffic down the routes
- Avoid deadlocks where possible
- Travel on the right side of the road

# Situations
- Robots may get stuck in traffic jams
- The world can be modified to make it so that there is no available path to the destination
- Blockages may occur randomly on roads, denoted by red cells
- Some robots/cars may intentionally and temporarily cause a blockage that forces other robots to explicitly avoid the area
- Terrain is generated at random

# Constraints
- Robots cannot travel off-road
- No two robots can occupy the same tile (note, this is technically adjustable in the code)

# Adjustable parameters
- Time taken to traverse edges
- Probability of a robot spawning at a home in a given tick
- Probability of a robot causing a blockage when stuck in a traffic jam in a given tick
- Width and height of the grid
- Density of the roads on the grid
- Density of houses on the roads
- If stuck at a cell unable to move, how many ticks to wait until the next check (this reduces lag)
- Probability of a blockage spawning on a given road tile in a given tick

# Controls

Many speeds can be adjusted with constants at the top of files.

Click a cell to mark it as permanently blocked.

Hit `S` to toggle robot spawning.

# More

The numbers in the top left are the attempted robot spawns and the number of successful robot arrivals (at their destination). Note that the first number if _attempted_ - if a robot is spawned and in doing so causes an immediate conflict, it is despawned.

# Generation
 - starts with a grid
 - draws lines  on the grid to form a connected graph until coverage quota met
    - done by starting new lines from existing paths, always gap between lines except for junctions
 - forms junctions on the grid
 - splits each current cell into a 2x2, needed for  2-way street set up, then applies edges between them
 - spawns houses randomly based on a probability on each valid space