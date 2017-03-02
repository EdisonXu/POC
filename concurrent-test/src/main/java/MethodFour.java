import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Edison Xu on 2017/3/2.
 */
public class MethodFour{


    public Runnable newThreadOne() {
        final String[] inputArr = Helper.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;
            private CyclicBarrier barrier;
            public void run() {
                for (int i = 0, j=0; i < arr.length; i=i+2,j++) {
                    try {
                        barrier = new CyclicBarrier(1, newThreadTwo(j));
                        Helper.print(arr[i],arr[i+1]);
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Runnable newThreadTwo(int seq) {
        final String[] inputArr = Helper.buildCharArr(26);
        return new Runnable() {

            private String[] arr = inputArr;
            public void run() {
                Helper.print(arr[seq]);
            }
        };
    }


    public static void main(String args[]){
        MethodFour four = new MethodFour();
        Helper.instance.run(four.newThreadOne());
        Helper.instance.shutdown();
    }
}
