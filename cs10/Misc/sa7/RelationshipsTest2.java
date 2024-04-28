public class RelationshipsTest2 {
    public static void main(String [] args) {
        Graph<String, String> relationships = new AdjacencyMapGraph<String, String>();

        String A = "A", B = "B", C = "C", D = "D", E = "E";
        relationships.insertVertex(A);
        relationships.insertVertex(B);
        relationships.insertVertex(C);
        relationships.insertVertex(D);
        relationships.insertVertex(E);

        relationships.insertDirected(A, B, "");
        relationships.insertDirected(A, C, "");
        relationships.insertDirected(A, D, "");
        relationships.insertDirected(A, E, "");
        relationships.insertDirected(B, A, "");
        relationships.insertDirected(B, C, "");
        relationships.insertDirected(C, A, "");
        relationships.insertDirected(C, B, "");
        relationships.insertDirected(C, D, "");
        relationships.insertDirected(E, B, "");
        relationships.insertDirected(E, C, "");

        System.out.println("Random walk from A for 4 steps: " + GraphLib.randomWalk(relationships, A, 4));
        System.out.println("Vertices sorted by in-degree: " + GraphLib.verticesByInDegree(relationships));
    }
}