package ch.heig.gre.groupQ;

import ch.heig.gre.maze.MazeBuilder;
import ch.heig.gre.maze.MazeGenerator;
import ch.heig.gre.maze.Progression;
import ch.heig.gre.util.ArrayUtil;

import java.util.*;

// TODO : classe à compléter et documenter
public final class DfsGenerator implements MazeGenerator {
  @Override
  public void generate(MazeBuilder builder, int from) {

    // Création de la pile pour DFS
    // Deque plus optimal que Stack : https://docs.oracle.com/javase/8/docs/api/java/util/Stack.html
    Deque<int[]> stack = new ArrayDeque<>();
    // Ajout du sommet de départ, avec 0 comme 1er voisin
    stack.push(new int[]{from, 0});

    // Mise à jour de l'interface graphique
    builder.progressions().setLabel(from, Progression.PROCESSING);

    // Map pour stocker les voisins de chaque sommet
    Map<Integer, int[]> neighborsMap = new HashMap<>();
    // On mélange une fois les voisisn
    int[] shuffledNeighbors = ArrayUtil.shuffle(builder.topology().neighbors(from));
    neighborsMap.put(from, shuffledNeighbors);

    // Tant que la pile n'est pas vide, on continue le DFS
    while (!stack.isEmpty()) {
      // On regarde le sommet sans le retirer de la pile
      int[] current = stack.peek();
      int currentId = current[0];
      int neighborId = current[1];

      // Voisins du sommet courant
      int[] neighbors = neighborsMap.get(currentId);

      // Pousser un nouveau sommet sur la pile
      boolean pushed = false;

      // Parcous des voisins jusqu'à trouver un sommet non visité
      while (neighborId < neighbors.length) {
        // Voisin de l'id courant
        int currentIdNeighbor = neighbors[neighborId];
        neighborId++;
        // Màj id de l'élément dans la pile
        current[1] = neighborId;

        // Si voisin pas encore traité
        if (builder.progressions().getLabel(currentIdNeighbor) == Progression.PENDING) {
          // On créer le passage entre les deux sommets
          builder.removeWall(currentId, currentIdNeighbor);
          // Mise à jour de l'interface graphique
          builder.progressions().setLabel(currentIdNeighbor, Progression.PROCESSING);
          // Comme le voisin n'a jamais été visité, sa liste de voisins n'est pas encore mélangée, on le fait maintenant
          neighborsMap.put(currentIdNeighbor, ArrayUtil.shuffle(builder.topology().neighbors(currentIdNeighbor)));

          // On ajoute le voisin à la pile avec 0 comme 1er voisin
          stack.push(new int[]{currentIdNeighbor, 0});
          // On indique qu'on a poussé un nouveau sommet
          pushed = true;
          break;
        }
        // Continue la boucle si en PROCESSING ou PROCESSED
      }

      // Si rien n'est poussé, on a plus de voisins
      if (!pushed) {
        // Mise à jour de l'interface graphique
        builder.progressions().setLabel(currentId, Progression.PROCESSED);
        // On retire le sommet de la pile
        stack.pop();
      }
    }
  }
}
