
import java.io.*;

/** BitInputStream contains methods to read one bit from a file at a time.
 * @author Stuart Hansen
 * @version February 24, 2008
 */
public class BitInputStream {

    private BufferedInputStream in;
    private int byt;
    private int bitMask;

    /** Constructs a BitInputStream
     * @param fileName is the name of the file
     */
    public BitInputStream(String fileName) {
        try {
            in = new BufferedInputStream(
                    new FileInputStream(
                    new File(fileName)));

            byt = in.read();
            bitMask = 0x80;
        } catch (Exception e) {
            System.err.println("Problem opening bitStream");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /** readBit() returns a 0 or 1.
     *  -1 is returned at the end of file.<br><br>
     *	 There is a quirk that padded 0s are returned from
     *	 the last byte, if it is not full.
     */
    public int readBit() {
        try {
            if (bitMask == 0) {
                bitMask = 0x80;
                byt = in.read();
                if (byt == -1) {
                    return -1;
                }
            }
        } catch (Exception e) {
            System.err.println("Problem reading from BitStream");
            System.err.println(e.getMessage());
        }
        int result = ((bitMask & byt) != 0) ? 1 : 0;
        bitMask >>>= 1;
        return result;
    }

    /** nextBit() is an alias for readBit() */
    public int nextBit() {
        return readBit();
    }

    /**
     * A small test main
     * @param args
     */
    public static void main(String[] args) {
        BitInputStream fin = new BitInputStream("mcgee.txt");
        int next = fin.nextBit();
        int count =0;
        while (next != -1) {
            System.out.print(next + " ");
            next = fin.nextBit();
            count++;
            if (count%8 == 0)
                System.out.println();
        }
    }
}
