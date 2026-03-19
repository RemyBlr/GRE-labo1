/**
 * Class : GRE
 * File : BfsSolver.java
 * Project name : LABO - Labyrinthes et explorations de graphes
 * Date : 19.03.2026
 * Project members :
 * - Florian Duruz, Rémy Bleuer
 *
 * Description:
 * Implémentation du solveur de labyrinthe utilisant l'algorithme BFS (Breadth-First Search).
 * Le BFS garantit de trouver le plus court chemin dans un graphe qui ne contient pas de poids.
 * L'algorithme compte également le nombre de chemins optimaux possibles pour atteindre la destination.
 */
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

  /**
   * Exécute l'algoritme BFS (Breadth-First Search) pour trouver le chemin le plus court.
   *
   * 1. Initialisation de la source avec une distance de 0.
   * 2. Exploration des sommets niveau par niveau à partir de la source.
   * 3. Pour chaque sommet traité:
   *    - Si voisin pas encore été visité, le marque comme visité et l'ajoute à la file.
   *    - Si voisin au même niveau, incrémente le nombre de chemins optimaux.
   * 4. Arrêt dès que la destination est trouvée.
   * 5. Reconstruit le chemin en remontant via les parents depuis la destination.
   *
   * @param graph le graphe à explorer
   * @param source le sommet de départ
   * @param destination le sommet d'arrivée
   * @param distances un label pour enregistrer les distances de chaque sommet
   * @return un résultat qui contient le chemin optimal et ses métadonnées
   */
  private Result bfsOne(Graph graph, int source, int destination, VertexLabelling<Integer> distances)
  {
    // Nombre total de sommets du graphe
    int verticesCount = graph.nbVertices();

    // Tableau d'état pour chaque sommet
    Progression[] state = new Progression[verticesCount];
    Arrays.fill(state, Progression.PENDING);
    // Distance depuis la source, -1 => non visité
    int[] dist = new int[verticesCount];
    // Sommet parent dans le chemin, -1 => aucun parent
    int[] parent = new int[verticesCount];
    // Nbr de chemins optimaux pour atteindre ce sommet
    long[] optimalPathCount = new long[verticesCount];
    Arrays.fill(parent, -1);

    // Initialisation de la source
    state[source] = Progression.PROCESSING;
    dist[source] = 0;
    optimalPathCount[source] = 1;
    distances.setLabel(source, 0);

    // File d'attente pour le BFS (FIFO)
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(source);

    // Explore tant qu'il y a des sommets à traiter
    while (!queue.isEmpty()) {
      // Extrait le prochain sommet de la file (le plus ancien, car FIFO)
      int current = queue.poll();
      state[current] = Progression.PROCESSED;

      //On s'arrête quand on traîte la sortie(et donc tout les chemins optimaux possibles sont trouvés)
      if(current == destination) break;

      // Explore tous les voisins du sommet courant
      for (int neighbor : graph.neighbors(current)) {
        // CAS 1 : Le voisin n'a pas encore été visité
        if (state[neighbor] == Progression.PENDING) {
          state[neighbor] = Progression.PROCESSING;
          dist[neighbor] = dist[current] + 1;
          parent[neighbor] = current;
          optimalPathCount[neighbor] = optimalPathCount[current];
          distances.setLabel(neighbor, dist[neighbor]);
          queue.add(neighbor);
        }
        // CAS 2 : Le voisin est au même niveau BFS que le sommet courant
        else if (state[neighbor] == Progression.PROCESSING && dist[neighbor] == dist[current] + 1) {
          // Il exite un autre chemin optimal pour atteindre le voisin
          optimalPathCount[neighbor] += optimalPathCount[current];
        }
      }
    }

    // Reconstruction du chemin en remontant depuis la destination vers la source via les parents
    List<Integer> path = new ArrayList<>();
    for (int currentVertex = destination; currentVertex != -1; currentVertex = parent[currentVertex]) {
      path.add(currentVertex);
    }
    // Inverse le chemin pour avoir l'ordre source -> destination
    Collections.reverse(path);

    // Métadonnées du résultat
    Metadata metadata = new Metadata();
    metadata.put(Keys.LENGTH, dist[destination]);
    metadata.put(Keys.NB_OPTIMAL_PATHS, optimalPathCount[destination]);

    return new Result(path, metadata);
  }
}
