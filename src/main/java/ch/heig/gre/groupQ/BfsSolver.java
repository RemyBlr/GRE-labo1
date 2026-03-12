package ch.heig.gre.groupQ;

import ch.heig.gre.Keys;
import ch.heig.gre.graph.Graph;
import ch.heig.gre.graph.GridGraph2D;
import ch.heig.gre.graph.PositiveWeightFunction;
import ch.heig.gre.graph.VertexLabelling;
import ch.heig.gre.maze.MazeSolver;
import ch.heig.gre.maze.Metadata;
import ch.heig.gre.maze.Progression;

import java.util.*;

public final class BfsSolver implements MazeSolver {
  @Override
  public Result solve(GridGraph2D grid, PositiveWeightFunction weights, int source, int destination, VertexLabelling<Integer> distances) {
    // Ne pas modifier
    return solve((Graph) grid, source, destination, distances);
  }

  public Result solve(Graph graph, int source, int destination, VertexLabelling<Integer> distances) {
    return bfsOne(graph, source, destination, distances);
  }

  private Result bfsOne(Graph graph, int source, int destination, VertexLabelling<Integer> distances)
  {
    int verticesCount = graph.nbVertices();
    Progression[] state = new Progression[verticesCount];
    Arrays.fill(state, Progression.PENDING);

    int[] dist = new int[verticesCount];
    int[] parent = new int[verticesCount];
    long[] optimalPathCount = new long[verticesCount];
    Arrays.fill(parent, -1);

    state[source] = Progression.PROCESSING;
    dist[source] = 0;
    optimalPathCount[source] = 1;
    distances.setLabel(source, 0);

    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(source);

    while (!queue.isEmpty()) {
      int current = queue.poll();
      state[current] = Progression.PROCESSED;

      for (int neighbor : graph.neighbors(current)) {
        if (state[neighbor] == Progression.PENDING) {
          state[neighbor] = Progression.PROCESSING;
          dist[neighbor] = dist[current] + 1;
          parent[neighbor] = current;
          optimalPathCount[neighbor] = optimalPathCount[current];
          distances.setLabel(neighbor, dist[neighbor]);
          queue.add(neighbor);
        } else if (state[neighbor] == Progression.PROCESSING && dist[neighbor] == dist[current] + 1) {
          //si on vérifie une voisin qui à deja été calculer, on regarde si si la distance en fait
          //un autre chemin optimal possible donc si la différence de distance est aussi de 1
          optimalPathCount[neighbor] += optimalPathCount[current];
        }
      }
    }

    List<Integer> path = new ArrayList<>();
    for (int cur = destination; cur != -1; cur = parent[cur]) {
      path.add(cur);
    }
    Collections.reverse(path);

    Metadata metadata = new Metadata();
    metadata.put(Keys.LENGTH, dist[destination]);
    metadata.put(Keys.NB_OPTIMAL_PATHS, optimalPathCount[destination]);

    return new Result(path, metadata);
  }
}
