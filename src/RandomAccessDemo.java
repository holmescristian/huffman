import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by hansen on 2/13/2017.
 */
public class RandomAccessDemo {
    public static void main(String[] args) throws Exception {
        RandomAccessFile fin = new RandomAccessFile(new File("mcgee.txt"), "r");
        int b =(int) fin.read();
        while (b != -1) {
            System.out.println((char)b + "\t" + b);
            b = fin.read();
        }

    }
}
