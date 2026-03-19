/**
 * File : DfsGenerator.java
 * Project name : LABO - Labyrinthes et explorations de graphes
 * Project members :
 * - Florian Duruz, Rémy Bleuer
 */
package ch.heig.gre.groupQ;

import ch.heig.gre.maze.MazeBuilder;
import ch.heig.gre.maze.MazeGenerator;
import ch.heig.gre.maze.Progression;
import ch.heig.gre.util.ArrayUtil;

import java.util.Arrays;
import java.util.Stack;

public final class DfsGenerator implements MazeGenerator {
  @Override
  public void generate(MazeBuilder builder, int from) {
    int n = builder.topology().nbVertices();
    Progression[] progressions = new Progression[n];
    Arrays.fill(progressions, Progression.PENDING);
    dfs(builder, progressions, from);
  }

  private void dfs(MazeBuilder builder, Progression[] progressions, int from) {
    Stack<Integer> stack = new Stack<>();

    progressions[from] = Progression.PROCESSING;
    builder.progressions().setLabel(from, Progression.PROCESSING);
    stack.push(from);

    int[][] neighbors = new int[builder.topology().nbVertices()][];

    while (!stack.isEmpty()) {
      int current = stack.peek();//on regarde en haut de la pile (sans retirer)

      //on ne veut shuffle que une fois les voisins
      if(neighbors[current] == null)
        neighbors[current] = ArrayUtil.shuffle(builder.topology().neighbors(current));

      int neighbor = randomPendingNeighbor(progressions, neighbors[current]);

      if (neighbor == -1) {// Tous les voisins de current ont été visité => finalise
        stack.pop();//ici on retire de la pile!
        progressions[current] = Progression.PROCESSED;
        builder.progressions().setLabel(current, Progression.PROCESSED);
      }
      else {// casse le mur vers le voisin + explore
        builder.removeWall(current, neighbor);
        progressions[neighbor] = Progression.PROCESSING;
        builder.progressions().setLabel(neighbor, Progression.PROCESSING);
        stack.push(neighbor);//on ajoute le voisin en haut de la pile(sans retirer le précédent)
      }
    }
  }

  /**
   * Choisi le premier voisin valide (état PENDING) dans un tableau déjà "shuffle"
   * @param progressions Tableau contenant les états des sommets du labyrinthe
   * @param neighbors Tableau de voisin déjà shuffle
   * @return index d'un voisin si trouvé sinon -1
   */
  private int randomPendingNeighbor(Progression[] progressions, int[] neighbors) {
    for (int neighbor : neighbors) {
      if (progressions[neighbor] == Progression.PENDING) return neighbor;
    }
    return -1;
  }
}
