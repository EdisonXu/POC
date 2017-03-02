import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by Edison Xu on 2017/3/2.
 */
public class MethodSix {

    private final PipedInputStream inputStream1;
    private final PipedOutputStream outputStream1;
    private final PipedInputStream inputStream2;
    private final PipedOutputStream outputStream2;
    private final byte[] MSG;

    public MethodSix() {
        inputStream1 = new PipedInputStream();
        outputStream1 = new PipedOutputStream();
        inputStream2 = new PipedInputStream();
        outputStream2 = new PipedOutputStream();
        MSG = "Go".getBytes();

        try {
            inputStream1.connect(outputStream2);
            inputStream2.connect(outputStream1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException {
        inputStream1.close();
        inputStream2.close();
        outputStream1.close();
        outputStream2.close();
    }

    public Runnable newThreadOne() {
        final String[] inputArr = Helper.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;
            private PipedInputStream in = inputStream1;
            private PipedOutputStream out = outputStream1;

            public void run() {
                for (int i = 0; i < arr.length; i=i+2) {
                    Helper.print(arr[i], arr[i + 1]);
                    try {
                        out.write(MSG);
                        byte[] inArr = new byte[2];
                        in.read(inArr);
                        while(true){
                            if("Go".equals(new String(inArr)))
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public Runnable newThreadTwo() {
        final String[] inputArr = Helper.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;
            private PipedInputStream in = inputStream2;
            private PipedOutputStream out = outputStream2;

            public void run() {
                for (int i = 0; i < arr.length; i++) {
                    try {
                        byte[] inArr = new byte[2];
                        in.read(inArr);
                        while(true){
                            if("Go".equals(new String(inArr)))
                                break;
                        }
                        Helper.print(arr[i]);
                        out.write(MSG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public static void main(String args[]) throws IOException {
        MethodSix six = new MethodSix();
        Helper.instance.run(six.newThreadOne());
        Helper.instance.run(six.newThreadTwo());
        Helper.instance.shutdown();
        six.shutdown();
    }
}
