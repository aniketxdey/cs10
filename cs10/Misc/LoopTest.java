public class LoopTest {
    void function(){
        for(int count = 0; count > -2; count--) {
            System.out.print(count + " ");
        }
    }

    public static void main(String[] args) {
        LoopTest l = new LoopTest();
        l.function();
    }
}
