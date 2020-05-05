/** This class implements a bitwise output stream in Java
 * @author Stuart Hansen
 * @version February 24, 2008
 */
import java.io.*;

public class BitOutputStream {

    BufferedOutputStream out;
    int byt;
    int offset;

    /** constructs a BitOutStream.
     * Program exits if there is a problem
     * opening the file.
     */
    public BitOutputStream(String fileName) {
        try {
            out = new BufferedOutputStream(
                    new FileOutputStream(
                    new File(fileName)));

            offset = 8;
        } catch (Exception e) {
            System.err.println("Problem opening BitOutputStream");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /** Writes one bit to the file */
    public void writeBit(int bit) {
        byt <<= 1;
        byt += bit;
        offset -= 1;

        try {
            if (offset == 0) {
                offset = 8;
                out.write(byt);
                byt = 0;
            }
        } catch (Exception e) {
            System.err.println("Problem writing to BitOutputStream");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Write the bits contained in a String to the file.  The String is
     * interpreted as "1" is a 1 bit.  Any other character is a 0 bit.
     * */
    public void writeString(String bits) {
        for (int i = 0; i < bits.length(); i++) {
            int bit = (bits.charAt(i) == '1') ? 1 : 0;
            writeBit(bit);
        }
    }

    /** Closes the file.  Since the file must contain a even number of bytes,
    the final byte is padded with 0s. */
    public void close() throws IOException {
        while (offset != 8) {
            writeBit(0);
        }

        out.close();
    }

    /** A small test main */
    public static void main(String[] args) throws Exception {
        BitOutputStream bs = new BitOutputStream("Junk.bits");

        for (char ch = 'a'; ch <= 'z'; ch++) {
            for (int i = 8; i > 0; i--) {
                int res = getBit(ch, i);
                bs.writeBit(res);
            }
        }
        bs.close();

    }

    /** A helper method to help with the testing */
    public static int getBit(char ch, int bitPos) {
        int b = ch << (32 - bitPos);
        b >>>= (31);
        return (int) b;
    }
}
