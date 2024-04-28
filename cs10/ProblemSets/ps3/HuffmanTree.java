import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

public class HuffmanTree implements Huffman {

    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(pathName)); //store input in Buffered reader file
        try {
            Map<Character, Long> frequencyTable = new HashMap<Character, Long>(); //create new map to hold characters and frequencies
            int i;
            while ((i = input.read()) >= 0) {  //go through input 1 by 1
                char c = (char) i; //cast int i to char
                if (frequencyTable.containsKey(c)) { //if value in table, increase frequency
                    Long frequencyValue = frequencyTable.get(c);
                    frequencyValue++;
                    frequencyTable.put(c, frequencyValue);
                } else { //if not in table, add character/frequency combo
                    frequencyTable.put(c, 1L);
                }
            }
            return frequencyTable;
        } finally {
            input.close(); //close input file
        }
    }

    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        //create single trees and add all to the priority queue
        Comparator<BinaryTree<CodeTreeElement>> comparator = new TreeComparator(); //use Tree Comparator
        PriorityQueue<BinaryTree<CodeTreeElement>> queue = new PriorityQueue<BinaryTree<CodeTreeElement>>(frequencies.size() + 1, comparator);
        Set<Character> charList = frequencies.keySet(); //create list of characters to go through
        for (char ch: charList) { //iterate through the list
            CodeTreeElement tree = new CodeTreeElement(frequencies.get(ch), ch); //store frequencies and characters in CodeTreeElement
            BinaryTree<CodeTreeElement> elementTree = new BinaryTree<CodeTreeElement>(tree); //pass in sinhular CodeTreeElement object tree
            queue.add(elementTree); //add the tree to the queue for priorities
        }

        // boundary case #1 - empty file
        if (queue.isEmpty()) {
            return new BinaryTree<CodeTreeElement>(new CodeTreeElement(0L, '0'));  // If no elements, just initialize empty  tree

        }
        //boundary case #2 - single character
        if (queue.size() == 1) {
            BinaryTree<CodeTreeElement> child = queue.element(); // If one element, tree with just that element
            return new BinaryTree<CodeTreeElement>(null, child, new BinaryTree<CodeTreeElement>(new CodeTreeElement(0L, '0')));

        }

        //create a single tree
        while (queue.size() > 1) {

            BinaryTree<CodeTreeElement> tree1 = queue.remove();// Remove the two lowest-frequency trees
            BinaryTree<CodeTreeElement> tree2 = queue.remove();
            CodeTreeElement jointTree = new CodeTreeElement(tree1.getData().getFrequency() + tree2.getData().getFrequency(), null);
            BinaryTree<CodeTreeElement> parentTree = new BinaryTree<CodeTreeElement>(jointTree, tree1, tree2); // Create a parent tree, attach two children
            queue.add(parentTree); // Add the new tree back into the queue
        }
        return queue.remove();
    }

    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> codeMap =  new HashMap<Character, String>();  //create hashmap for the tree
        String code = "";
        computeHelper(codeTree, code, codeMap); //call helper function to edit codeMap
        return codeMap; //return codeMap of characters and their respective codes
    }

    public static void computeHelper(BinaryTree<CodeTreeElement> tree, String code, Map<Character, String> codeMap) {
        if (tree.isLeaf()) { //check if it is a leaf, if so, return and go to next recursion
            codeMap.put(tree.data.getChar(), code);
            return;
        }
        if (tree.hasLeft()) {
            // Add a 0 to the bit sequence and recurse
            computeHelper(tree.getLeft(), code + "0", codeMap);
        }
        if (tree.hasRight()) {
            // Add a 1 to the bit sequence and recurse
            computeHelper(tree.getRight(), code + "1", codeMap);
        }
    }

    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException{
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName); //create BufferedBitWriter object for writing out
        BufferedReader input = new BufferedReader(new FileReader(pathName)); //process input
        String test = "";
        try {
            int i;
            while ((i = input.read()) != -1) {
                char c = (char) i;
                String code = codeMap.get(c);
                // Write each bit in the code to the new file
                for (char bit: code.toCharArray()) {
                    bitOutput.writeBit(bit == '1');
                    test += bit;
                }
            }
        }
        finally {
            bitOutput.close(); //close files for output and input
            input.close();
        }
    }

    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        BufferedBitReader bitInput = new BufferedBitReader(compressedPathName); //create BufferedBitReader object for writing out
        BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName));
        try {
            BinaryTree<CodeTreeElement> tree = codeTree; //pass in Code Tree to BinaryTree value to analyze
            while (bitInput.hasNext()) {
                boolean bit = bitInput.readBit();
                if (bit) {
                    tree = tree.getRight(); //1 is right, 0 is left (true and false respectively as well)
                }
                else {
                    tree = tree.getLeft();
                }
                if (tree.isLeaf()) {
                    output.write(tree.getData().getChar()); //if the tree is a leaf, write the character
                    tree = codeTree;
                }
            }
        }
        finally {
            bitInput.close();
            output.close();
        }
    }

    public static void main(String[] args) {
        try {
            //test 1 - hello
            HuffmanTree a = new HuffmanTree();
            Map<Character, Long> frequencyTable1 = a.countFrequencies("input/test1");
            BinaryTree<CodeTreeElement> huffmanTree1  = a.makeCodeTree(frequencyTable1);
            Map<Character, String> mapCodes1 = a.computeCodes(huffmanTree1);
            a.compressFile(mapCodes1, "input/test1", "input/test1_compressed.txt");
            a.decompressFile("input/test1_compressed.txt", "test1_decompressed.txt", huffmanTree1);

            //test 2 - empty file
            HuffmanTree b = new HuffmanTree();
            Map<Character, Long> frequencyTable2 = b.countFrequencies("input/test2");
            BinaryTree<CodeTreeElement> huffmanTree2  = b.makeCodeTree(frequencyTable2);
            Map<Character, String> mapCodes2 = b.computeCodes(huffmanTree2);
            b.compressFile(mapCodes2, "input/test2", "input/test2_compressed.txt");
            b.decompressFile("input/test2_compressed.txt", "test2_decompressed.txt", huffmanTree2);

            //test 3 - single character
            HuffmanTree c = new HuffmanTree();
            Map<Character, Long> frequencyTable3 = c.countFrequencies("input/test3");
            BinaryTree<CodeTreeElement> huffmanTree3  = c.makeCodeTree(frequencyTable3);
            Map<Character, String> mapCodes3 = c.computeCodes(huffmanTree3);
            c.compressFile(mapCodes3, "input/test3", "input/test3_compressed.txt");
            c.decompressFile("input/test3_compressed.txt", "test3_decompressed.txt", huffmanTree3);

            //test 4 - peter piper
            HuffmanTree d = new HuffmanTree();
            Map<Character, Long> frequencyTable4 = d.countFrequencies("input/test4");
            BinaryTree<CodeTreeElement> huffmanTree4  = d.makeCodeTree(frequencyTable4);
            Map<Character, String> mapCodes4 = d.computeCodes(huffmanTree4);
            d.compressFile(mapCodes4, "input/test4", "input/test4_compressed.txt");
            d.decompressFile("input/test4_compressed.txt", "test4_decompressed.txt", huffmanTree4);

            //US Constitution
            HuffmanTree e = new HuffmanTree();
            Map<Character, Long> frequencyTable5 = e.countFrequencies("input/USConstitution.txt");
            BinaryTree<CodeTreeElement> huffmanTree5  = e.makeCodeTree(frequencyTable5);
            Map<Character, String> mapCodes5 = e.computeCodes(huffmanTree5);
            e.compressFile(mapCodes5, "input/USConstitution.txt", "input/USConstitution_compressed.txt");
            e.decompressFile("input/USConstitution_compressed.txt", "USConstitution_decompressed.txt", huffmanTree5);

            //WarAndPeace
            HuffmanTree f = new HuffmanTree();
            Map<Character, Long> frequencyTable6 = f.countFrequencies("input/WarAndPeace.txt");
            BinaryTree<CodeTreeElement> huffmanTree6  = f.makeCodeTree(frequencyTable6);
            Map<Character, String> mapCodes6 = f.computeCodes(huffmanTree6);
            f.compressFile(mapCodes6, "input/WarAndPeace.txt", "input/WarAndPeace_compressed.txt");
            f.decompressFile("input/WarAndPeace_compressed.txt", "WarAndPeace_decompressed.txt", huffmanTree6);


        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}


