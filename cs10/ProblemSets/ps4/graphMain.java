import java.util.ArrayList;
import java.util.Map;

public class graphMain {


    public static void main(String [] args) throws Exception {

        Graph<String, String> actors = new AdjacencyMapGraph<String, String>();

        // Test the code with sample files from bacon.zip with lesser data for actors, movies and the tuple

        Map<String,String> actorMap=fileReader.loadActors("ps4/actorsTest.txt");
        Map<String,String> movieMap=fileReader.loadActors("ps4/moviesTest.txt");
        Map<String, ArrayList<String>> movieActorsMap=fileReader.loadMovieActors("ps4/movie-actorsTest.txt");

        //The larger data files with actors, movies and the movie-actor combined dataset, uncomment to run with the larger dataset

//        Map<String,String> actorMap=fileReader.loadActors("ps4/actors.txt");
//        Map<String,String> movieMap=fileReader.loadActors("ps4/movies.txt");
//        Map<String, ArrayList<String>> movieActorsMap=fileReader.loadMovieActors("ps4/movie-actors.txt");

        //Loops and inserts all the actors in a vertex
        for(String id:actorMap.keySet()) {
            actors.insertVertex(actorMap.get(id));

        }

        //Adds the vertices of the graph as movies
        for(String movie: movieActorsMap.keySet()) {

            ArrayList<String> actorList = movieActorsMap.get(movie);
            for(String actor:actorList) {
                for(int i = 0;i < actorList.size();i++) {
                    actors.insertDirected(actorMap.get(actor),actorMap.get((actorList.get(i))),movieMap.get(movie));
                }
            }

        }

        //Playing the game
        playGame.getlist(actors);

    }
}