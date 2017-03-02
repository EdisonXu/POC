import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Edison Xu on 2017/3/2.
 */
public class MethodSeven {

    private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public Runnable newThreadOne() {
        final String[] inputArr = Helper.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;
            public void run() {
                for (int i = 0; i < arr.length; i=i+2) {
                    Helper.print(arr[i], arr[i + 1]);
                    queue.offer("TwoToGo");
                    while(!"OneToGo".equals(queue.peek())){}
                    queue.poll();
                }
            }
        };
    }

    public Runnable newThreadTwo() {
        final String[] inputArr = Helper.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (int i = 0; i < arr.length; i++) {
                    while(!"TwoToGo".equals(queue.peek())){}
                    queue.poll();
                    Helper.print(arr[i]);
                    queue.offer("OneToGo");
                }
            }
        };
    }

    public static void main(String args[]){
        long start = System.currentTimeMillis();
        MethodSeven seven = new MethodSeven();
        Helper.instance.run(seven.newThreadOne());
        Helper.instance.run(seven.newThreadTwo());
        Helper.instance.shutdown();
        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - start));
    }
}
