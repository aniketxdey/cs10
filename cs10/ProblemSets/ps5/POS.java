import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.*;
public class POS {
    HashMap<String, HashMap<String, Double>> transitions;
    HashMap<String, HashMap<String, Double>> observations;

    public static String viterbi(String input, HashMap<String, HashMap<String, Double>> transitions, HashMap<String, HashMap<String, Double>> observations) {
        String start = "#";
        String output = "";
        String lastTag = "";
        double highScore = Double.NEGATIVE_INFINITY;
        double obsScore = -100.0; //arbitrary value kept constant

        String[] wordArray = input.split(" ");
        ArrayList<HashMap<String, String>> backtracking = new ArrayList<>();
        Stack<String> printStrings = new Stack<>();
        Set<String> previousStates = new HashSet<>();
        previousStates.add(start);
        HashMap<String, Double> previousScores = new HashMap<>();
        previousScores.put(start, 0.0);

        for (int j = 0; j < wordArray.length; j++) {
            Set<String> nextStates = new HashSet<>();
            HashMap<String, Double> nextScores = new HashMap<>();
            HashMap<String, String> backPoint = new HashMap<>();

            // Iterate through each previous state
            for (String state : previousStates) {
                // Check if there are transitions from the current state
                if (transitions.containsKey(state) && !transitions.get(state).isEmpty()) {
                    // Iterate through each possible transition from the current state
                    for (String tr : transitions.get(state).keySet()) {
                        nextStates.add(tr);
                        double score = previousScores.get(state) + transitions.get(state).getOrDefault(tr, obsScore);
                        score += observations.getOrDefault(tr, new HashMap<>()).getOrDefault(wordArray[j], obsScore);

                        if (!nextScores.containsKey(tr) || score > nextScores.get(tr)) {
                            nextScores.put(tr, score);
                            backPoint.put(tr, state);
                        }
                    }
                }
            }
            // Update the previous scores and states for the next iteration
            previousScores = nextScores;
            previousStates = nextStates;
            // Add the back pointers for this word to the backtracking list
            backtracking.add(backPoint);
        }

        for (String state : previousScores.keySet()) {
            if (previousScores.get(state) > highScore) {
                highScore = previousScores.get(state);
                lastTag = state;
            }
        }

        // Add the last tag to the stack
        printStrings.push(lastTag);
        for (int i = wordArray.length - 1; i > 0; i--) {
            lastTag = backtracking.get(i).get(lastTag);
            printStrings.push(lastTag);
        }

        // Build the output string from the stack of tags
        while (!printStrings.isEmpty()) {
            output += printStrings.pop() + " ";
        }

        return output.trim();
    }

    public void train(String sentencesFilename,  String tagsFileName) throws Exception {
        BufferedReader sentences = new BufferedReader(new FileReader(sentencesFilename));
        BufferedReader tags = new BufferedReader(new FileReader(tagsFileName));

        // Initialize the transitions map where keys are states and values are maps of possible next states and their frequencies.
        transitions = new HashMap<>();
        // Initialize the observations map where keys are states and values are maps of observed words and their frequencies.
        observations = new HashMap<>();

        // Declare variables to hold the current line being read from each file.
        String sentenceLine;
        String tagLine;

        // Loop until there are lines in both the sentences and tags files.
        while ((sentenceLine = sentences.readLine()) != null && (tagLine = tags.readLine()) != null) {
            // Split the sentence into words.
            String[] sentenceWords = sentenceLine.split(" ");
            // Split the tag line into individual tags.
            String[] sentenceTags = tagLine.split(" ");

            // Initialize the previous tag as the start symbol.
            String prevTag = "#";
            // Iterate over each word and its corresponding tag.
            for (int i = 0; i < sentenceWords.length; i++) {
                // Convert the word to lowercase for normalization.
                String word = sentenceWords[i].toLowerCase();
                // Store the current tag.
                String tag = sentenceTags[i];

                // If the current tag is not in the observations map, add it with a new empty map.
                observations.putIfAbsent(tag, new HashMap<>());
                // Retrieve the map of words for the current tag.
                HashMap<String, Double> wordMap = observations.get(tag);
                // Increment the count for the current word in the map for the current tag.
                wordMap.put(word, wordMap.getOrDefault(word, 0.0) + 1);

                // If the previous tag is not in the transitions map, add it with a new empty map.
                transitions.putIfAbsent(prevTag, new HashMap<>());
                // Retrieve the map of next possible tags for the current tag.
                HashMap<String, Double> tagMap = transitions.get(prevTag);
                // Increment the count for the transition from the previous tag to the current tag.
                tagMap.put(tag, tagMap.getOrDefault(tag, 0.0) + 1);

                // Set the current tag as the previous tag for the next iteration of the loop.
                prevTag = tag;
            }

            // Uncomment the following line to handle transitions to an end state if needed.
            // transitions.get(prevTag).put("#", transitions.get(prevTag).getOrDefault("#", 0.0) + 1);
        }

        // Normalize the counts in the observations map to probabilities and convert them to log probabilities.
        for (Map.Entry<String, HashMap<String, Double>> entry : observations.entrySet()) {
            // Calculate the sum of all frequencies for the current tag.
            double sum = entry.getValue().values().stream().mapToDouble(f -> f).sum();
            // Iterate over each word observed with the current tag.
            for (Map.Entry<String, Double> wordEntry : entry.getValue().entrySet()) {
                // Convert the frequency to a log probability.
                wordEntry.setValue(Math.log(wordEntry.getValue() / sum));
            }
        }

        // Normalize the counts in the transitions map to probabilities and convert them to log probabilities.
        for (Map.Entry<String, HashMap<String, Double>> entry : transitions.entrySet()) {
            // Calculate the sum of all frequencies for the current tag.
            double sum = entry.getValue().values().stream().mapToDouble(f -> f).sum();
            // Iterate over each possible next tag for the current tag.
            for (Map.Entry<String, Double> tagEntry : entry.getValue().entrySet()) {
                // Convert the frequency to a log probability.
                tagEntry.setValue(Math.log(tagEntry.getValue() / sum));
            }
        }

        // Close the BufferedReader for the sentences file.
        sentences.close();
        // Close the BufferedReader for the tags file.
        tags.close();

    }
    public void consoleTest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter a sentence to be POS tagged:");
        System.out.print(">");
        String input = reader.readLine();
        String tagged = viterbi(input, transitions, observations);
        System.out.println("Tagged sentence: " + tagged);
    }

    public void fileTest(String sentencesFilename, String tagsFilename) throws IOException {
        BufferedReader sentenceReader = new BufferedReader(new FileReader(sentencesFilename));
        BufferedReader tagReader = new BufferedReader(new FileReader(tagsFilename));

        String sentence;
        String tagLine;
        int correctTags = 0;
        int totalTags = 0;

        // Loop until there are no more lines in both the sentences and tags files
        while ((sentence = sentenceReader.readLine()) != null && (tagLine = tagReader.readLine()) != null) {
            // Use the Viterbi method to predict the sequence of tags for the current sentence
            String predictedTags = viterbi(sentence, transitions, observations);
            String[] predictedTagsArray = predictedTags.split(" ");
            String[] actualTagsArray = tagLine.split(" ");

            // Iterate over the predicted tags and actual tags, counting matches and mismatches
            for (int i = 0; i < actualTagsArray.length; i++) {
                if (predictedTagsArray[i].equals(actualTagsArray[i])) {
                    correctTags++;
                }
                totalTags++;
            }
        }

        // Calculate and print the accuracy of the POS tagger
        double accuracy = (correctTags / (double) totalTags) * 100;
        System.out.println("Accuracy: " + accuracy + "%");
        System.out.println("Correct tags:" + correctTags);
        System.out.println("Total tags:" + totalTags);

        // Close the readers
        sentenceReader.close();
        tagReader.close();
    }


    public static void main(String[] args) {
        POS posTagger = new POS();
        try {
            posTagger.transitions = new HashMap<>();
            posTagger.observations = new HashMap<>();

            // Manually populate the transitions and observations maps based on the given data
            String[] sentences = {
                    "cat/N chase/V dog/N",
                    "cat/N watch/V chase/NP",
                    "chase/NP get/V watch/N",
                    "chase/NP watch/V dog/N and/CNJ cat/N",
                    "dog/N watch/V cat/N watch/V dog/N",
                    "cat/N watch/V watch/N and/CNJ chase/NP",
                    "dog/N watch/V and/CNJ chase/V chase/NP"
            };

            Map<String, Integer> tagCounts = new HashMap<>();
            String prevTag = "#"; // Start symbol
            for (String line : sentences) {
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    String[] wordTag = token.split("/");
                    String word = wordTag[0].toLowerCase();
                    String tag = wordTag[1];

                    // Update transitions
                    HashMap<String, Double> transitionMap = posTagger.transitions.getOrDefault(prevTag, new HashMap<>());
                    transitionMap.put(tag, transitionMap.getOrDefault(tag, 0.0) + 1);
                    posTagger.transitions.put(prevTag, transitionMap);

                    // Update observations
                    HashMap<String, Double> observationMap = posTagger.observations.getOrDefault(tag, new HashMap<>());
                    observationMap.put(word, observationMap.getOrDefault(word, 0.0) + 1);
                    posTagger.observations.put(tag, observationMap);

                    // Update tag counts for normalization later
                    tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);

                    prevTag = tag;
                }
                // Reset prevTag to start symbol after each sentence
                prevTag = "#";
            }

            // Convert counts to probabilities
            for (Map.Entry<String, HashMap<String, Double>> entry : posTagger.transitions.entrySet()) {
                double sum = entry.getValue().values().stream().mapToDouble(f -> f).sum();
                for (Map.Entry<String, Double> transition : entry.getValue().entrySet()) {
                    transition.setValue(transition.getValue() / sum);
                }
            }
            for (Map.Entry<String, HashMap<String, Double>> entry : posTagger.observations.entrySet()) {
                double tagCount = tagCounts.get(entry.getKey());
                for (Map.Entry<String, Double> observation : entry.getValue().entrySet()) {
                    observation.setValue(observation.getValue() / tagCount);
                }
            }

            // Test the viterbi function with simple sentences
            String testSentence1 = "cat watch dog"; //expected N V N
            System.out.println("Test Sentence 1:" + testSentence1);
            System.out.println("Test Sentence 1 Expected: N V N");
            String tagged1 = posTagger.viterbi(testSentence1, posTagger.transitions, posTagger.observations);
            System.out.println("Test 1 Results: " + tagged1);
            System.out.println("");

            String testSentence2 = "cat chase dog and watch dog"; //expected N V N CNJ V N
            System.out.println("Test Sentence 2:" + testSentence2);
            System.out.println("Test Sentence 1 Expected: N V N CNJ V N");
            String tagged2 = posTagger.viterbi(testSentence2, posTagger.transitions, posTagger.observations);
            System.out.println("Test 2 Results: " + tagged2);
            System.out.println("");

            String testSentence3 = "cat dog watch chase and"; //expected N N V V CNJ
            System.out.println("Test Sentence 3:" + testSentence3);
            System.out.println("Test Sentence 1 Expected: N N V V CNJ");
            String tagged3 = posTagger.viterbi(testSentence3, posTagger.transitions, posTagger.observations);
            System.out.println("Test 3 Results: " + tagged3);
            System.out.println("");


            //Train & testing the model with the simple datasets
            posTagger.train("ps5/simple-train-sentences.txt", "ps5/simple-train-tags.txt");
            System.out.println("Test Results for Simple Data:");
            posTagger.fileTest("ps5/simple-test-sentences.txt", "ps5/simple-test-tags.txt");
            System.out.println("");

            // Train & testing the model with Brown dataset
            posTagger.train("ps5/brown-train-sentences.txt", "ps5/brown-train-tags.txt");
            System.out.println("Test Results for Brown Data:");
            posTagger.fileTest("ps5/brown-test-sentences.txt", "ps5/brown-test-tags.txt");
            System.out.println("");

            // Command line input using model trained on Brown data
            for (int i = 0; i < Double.POSITIVE_INFINITY; i++) {
                posTagger.consoleTest();
            }



        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
