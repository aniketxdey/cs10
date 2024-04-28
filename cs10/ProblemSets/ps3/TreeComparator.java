import java.util.*;

public class TreeComparator implements Comparator<BinaryTree<CodeTreeElement>> {

    public int compare(BinaryTree<CodeTreeElement> tree1, BinaryTree<CodeTreeElement> tree2) {
        // if tree1's char occurs less frequently than tree2's, return -1
        if (tree1.data.getFrequency() < tree2.data.getFrequency()) {
            return -1;
        }
        // if tree1's char occurs more frequently than tree2's, return 1
        else if (tree1.data.getFrequency() > tree2.data.getFrequency()) {
            return 1;
        }
        // otherwise they occur equally frequently, so return 0
        else {
            return 0;
        }
    }
}