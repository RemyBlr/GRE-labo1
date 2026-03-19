/**
 * Class : GRE
 * File : DfsGenerator.java
 * Project name : LABO - Labyrinthes et explorations de graphes
 * Date : 19.03.2026
 * Project members :
 * - Florian Duruz, Rémy Bleuer
 *
 * Description:
 * Implémentation du générateur de labyrinthe utilisant l'algorithme DFS (Depth-First Search).
 * Le DFS génère un labyrinthe en explorant les sommets en profondeur.
 * L'ordre d'exploration des voisins est aléatoire, ce qui garantit un labyrinthe différent à chaque génération.
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

  /**
   * Exécute l'algorithme DFS (Depth-First Search) itératif pour générer un labyrinthe.
   *
   * 1. Initialisation du sommet de départ, marqué comme PROCESSING et empilé.
   * 2. Exploration en profondeur à partir du sommet courant (sommet au sommet de la pile).
   * 3. Pour chaque sommet traité :
   *    - Si un voisin PENDING existe, casse le mur vers ce voisin et l'empile.
   *    - Sinon, tous les voisins ont été visités : dépile et marque le sommet comme PROCESSED.
   * 4. Les voisins sont mélangés aléatoirement à la première visite pour garantir un labyrinthe aléatoire.
   * 5. Arrêt lorsque la pile est vide (tous les sommets accessibles ont été visités).
   *
   * @param builder      le constructeur du labyrinthe (gère les murs et les progressions)
   * @param progressions tableau d'état pour chaque sommet du graphe
   * @param from         le sommet de départ
   */
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
