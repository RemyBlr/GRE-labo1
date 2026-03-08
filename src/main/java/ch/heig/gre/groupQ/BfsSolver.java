package ch.heig.gre.groupQ;

import ch.heig.gre.graph.Graph;
import ch.heig.gre.graph.GridGraph2D;
import ch.heig.gre.graph.PositiveWeightFunction;
import ch.heig.gre.graph.VertexLabelling;
import ch.heig.gre.maze.MazeSolver;

import java.util.ArrayList;
import java.util.List;

// TODO : compléter cette classe et documenter. Voir documentation de l'interface MazeSolver et des classes associées
//  pour la description des paramètres.
public final class BfsSolver implements MazeSolver {
  @Override
  public Result solve(GridGraph2D grid, PositiveWeightFunction weights, int source, int destination, VertexLabelling<Integer> distances) {
    // Ne pas modifier
    return solve((Graph) grid, source, destination, distances);
  }

  public Result solve(Graph graph, int source, int destination, VertexLabelling<Integer> distances) {
    // Mise à jour de l'interface graphique :
    // distances.setLabel(..., ...);

    // Création des métadonnées à retourner avec le résultat
    // Metadata metadata = new Metadata();
    // metadata.put(Keys.LENGTH, ...);
    // metadata.put(Keys.NB_OPTIMAL_PATHS, ...);
    //List<Integer> path = new ArrayList<Integer>();
    //for (int i = 0; i < 25; i++)
    //{
    //  path.add(i);
    //}
    //return new Result(path, null);
    return null;
  }
}
