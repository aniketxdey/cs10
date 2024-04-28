
import java.util.*;

public class graphLibrary {

    /**
     * Performs a breadth-first search on the graph starting from the source vertex and constructs a path tree.
     * @param g the graph to search
     * @param source the source vertex
     * @return a graph representing the path tree
     */
    public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {
        // Initializations
        Graph<V, E> tree = new AdjacencyMapGraph<>();
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        // Initialize BFS from the source vertex
        visited.add(source);
        queue.add(source);
        tree.insertVertex(source);

        // BFS loop
        while (!queue.isEmpty()) {
            V u = queue.remove();
            for (V v : g.outNeighbors(u)) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                    tree.insertVertex(v);
                    tree.insertDirected(u, v, g.getLabel(u, v)); // Insert edge pointing from child to parent
                }
            }
        }

        return tree;
    }

    /**
     * Constructs a path from the given vertex to the root of the BFS tree.
     * @param tree the BFS tree
     * @param v the vertex to find the path for
     * @return a list of vertices representing the path
     */
    public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {
        List<V> path = new ArrayList<>();
        while (v != null) {
            path.add(v);
            Iterator<V> it = tree.inNeighbors(v).iterator();
            v = it.hasNext() ? it.next() : null;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Determines which vertices are in the original graph but not in the subgraph.
     * @param graph the original graph
     * @param subgraph the subgraph
     * @return a set of vertices that are in the graph but not the subgraph
     */
    public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {
        Set<V> missing = new HashSet<>();
        for (V v : graph.vertices()) {
            if (!subgraph.hasVertex(v)) {
                missing.add(v);
            }
        }
        return missing;
    }

    /**
     * Calculates the average separation from the root in the BFS tree.
     * @param tree the BFS tree
     * @param root the root vertex
     * @return the average separation
     */
    public static <V, E> double averageSeparation(Graph<V, E> tree, V root) {
        Map<V, Integer> levelMap = new HashMap<>();
        calculateLevels(tree, root, 0, levelMap);

        double totalSeparation = 0;
        for (int level : levelMap.values()) {
            totalSeparation += level;
        }

        return totalSeparation / tree.numVertices();
    }

    /**
     * Recursively calculates the levels of each vertex in the BFS tree.
     * @param tree the BFS tree
     * @param vertex the current vertex
     * @param level the current level
     * @param levelMap a map to store the levels of vertices
     */
    private static <V, E> void calculateLevels(Graph<V, E> tree, V vertex, int level, Map<V, Integer> levelMap) {
        levelMap.put(vertex, level);
        for (V v : tree.outNeighbors(vertex)) {
            if (!levelMap.containsKey(v)) {
                calculateLevels(tree, v, level + 1, levelMap);
            }
        }
    }
    public static void main(String[] args) {
        AdjacencyMapGraph<String, Set<String>> graph = new AdjacencyMapGraph<>();

        //Test graphLib methods
        graph.insertVertex("Kevin Bacon");
        graph.insertVertex("Alice");
        graph.insertVertex("Bob");
        graph.insertVertex("Charlie");
        graph.insertVertex("Dartmouth");
        graph.insertVertex("Nobody");
        graph.insertVertex("Nobody's Friend");
        // Add more vertices as needed

        // Add edges
        Set<String> edge1 = new HashSet<>();
        edge1.add("A Movie");
        edge1.add("E Movie");
        graph.insertDirected("Kevin Bacon", "Alice", edge1);
        graph.insertDirected("Alice", "Kevin Bacon", edge1);

        Set<String> edge2 = new HashSet<>();
        edge2.add("A Movie");
        graph.insertDirected("Kevin Bacon", "Bob", edge2);
        graph.insertDirected("Bob", "Kevin Bacon", edge2);

        Set<String> edge3 = new HashSet<>();
        edge3.add("A Movie");
        graph.insertDirected("Alice", "Bob", edge3);
        graph.insertDirected("Bob", "Alice", edge3);

        Set<String> edge4 = new HashSet<>();
        edge4.add("D Movie");
        graph.insertDirected("Alice", "Charlie", edge4);
        graph.insertDirected("Charlie", "Alice", edge4);

        Set<String> edge5 = new HashSet<>();
        edge5.add("C Movie");
        graph.insertDirected("Bob", "Charlie", edge5);
        graph.insertDirected("Charlie", "Bob", edge5);

        Set<String> edge6 = new HashSet<>();
        edge6.add("B Movie");
        graph.insertDirected("Charlie", "Dartmouth", edge6);
        graph.insertDirected("Dartmouth", "Charlie", edge6);


        Set<String> edge7 = new HashSet<>();
        edge7.add("F Movie");
        graph.insertDirected("Nobody", "Nobody's Friend", edge7);
        graph.insertDirected("Nobody's Friend", "Nobody", edge7);

        // Perform BFS & test methods
        Graph<String, Set<String>> bfsTree = graphLibrary.bfs(graph, "Kevin Bacon");
        System.out.println(graphLibrary.getPath(bfsTree, "Charlie"));
        System.out.println(averageSeparation(bfsTree, "Kevin Bacon"));
        System.out.println(bfsTree.numVertices());

        // Print the BFS tree
        System.out.println("BFS Tree:");
        for (String vertex : bfsTree.vertices()) {
            System.out.print(vertex + " -> ");
            for (String parent : bfsTree.inNeighbors(vertex)) {
                System.out.print(parent + " ");
            }
            System.out.println();
        }

        bfsTree = graphLibrary.bfs(graph, "Alice");

        // Print the BFS tree
        System.out.println("BFS Tree:");
        for (String vertex : bfsTree.vertices()) {
            System.out.print(vertex + " -> ");
            for (String parent : bfsTree.inNeighbors(vertex)) {
                System.out.print(parent + " ");
            }
            System.out.println();
        }
    }

}
