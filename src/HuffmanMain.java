public class HuffmanMain {
    public static void main(String[] args) throws Exception {
        Huffman.encode("mcgee.txt", "code.txt", "compressed.txt");
        Huffman.decode("compressed.txt", "code.txt", "decompressed.txt");

    }
}
