import java.util.*;

/**
 * Library for graph analysis
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * 
 */
public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 * @param g		graph to walk on
	 * @param start	initial vertex (assumed to be in graph)
	 * @param steps	max number of steps
	 * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 * 			    null if start isn't in graph
	 */
	public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, int steps) {
		List<V> path = new ArrayList<>();
		if (!g.hasVertex(start)) return null;

		path.add(start);
		V current = start;
		for (int i = 0; i < steps; i++) {
			Iterable<V> neighbors = g.outNeighbors(current);
			List<V> neighborsList = new ArrayList<>();
			for (V neighbor : neighbors) {
				neighborsList.add(neighbor);
			}

			if (neighborsList.isEmpty()) {
				break; // No out-edges
			}

			current = neighborsList.get(new Random().nextInt(neighborsList.size()));
			path.add(current);
		}
		return path;
	}
	
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		List<V> vertices = new ArrayList<>();
		for (V item : g.vertices()) {
			vertices.add(item);
		}
		vertices.sort(Comparator.comparingInt(g::inDegree).reversed());
		return vertices;
	}
}
