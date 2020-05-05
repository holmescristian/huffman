/*
* Author: Cristian Holmes
* Date: 2/27/18
* Description: Will take a text file and encode or decode using a Huffman Tree
*
* */
import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Huffman {
    //Used to correlate ascii values to their code
    private static String[] codeArray = new String[256];

    /**
     * Encode a file using a huffman tree and output it to a new file as well as recording the codes to a file
     * @param originalFilename  Name of the original file to be encoded
     * @param codeFilename      Name of the file to output the codes
     * @param compressedFilename Name of the file to output compressed
     * @throws Exception
     */
    public static void encode(String originalFilename, String codeFilename, String compressedFilename) throws Exception{
        //Records the frequencies of each character in the text file
        TreeMap<Character,Integer> freq = new TreeMap<>();

        //Used to read the file in one character at a time
        RandomAccessFile fin = new RandomAccessFile(new File(originalFilename), "r");

        //Will contain the int value of each character
        int b = fin.read();

        //Read one character until the end of the file is reached
        while (b != -1) {
            //Only Increment if the leaf is already there
            if(freq.get( (char) b ) != null)
                freq.put( (char) b, freq.get((char)b) + 1 );
            //Otherwise make the new leaf
            else
                freq.put( (char) b, 1);
            b = fin.read();
        }

        //Used to sort the treemap
        PriorityQueue<HuffmanNode> sort = new PriorityQueue<>(freq.size(), new HuffmanCompare());

        //Check for every characters frequency
        for(int i = 0; i <= 255; i++ ){
            //If the leaf exists for that character put it in the Queue
            if(freq.get( (char) i ) != null){
                //Make a node and add it to the Queue
                HuffmanNode temp = new HuffmanNode();
                temp.freq = freq.get((char) i);
                temp.c = (char)i;
                sort.add(temp);
            }
        }

        //The root of the whole Huffman Tree
        HuffmanNode root = null;

        //Keep compressing until the tree is only one node
        while(sort.size() > 1){
            //Grab the two smallest nodes
            HuffmanNode node1 = sort.poll();
            HuffmanNode node2 = sort.poll();
            HuffmanNode total = new HuffmanNode();

            //Combine the two smallest nodes with the combined frequency
            total.left = node1;
            total.right = node2;
            total.freq = node1.freq + node2.freq;

            //Make the new node the root of the whole tree
            root = total;

            //Add it back to the queue to be resorted
            sort.add(root);
        }

        //Used to write the strings into a file
        BufferedWriter bfr = new BufferedWriter(new FileWriter(codeFilename));

        writeCode(bfr, root, "");
        //Close the file
        bfr.close();
        //Read in one character at a time
        fin = new RandomAccessFile(new File(originalFilename), "r");
        b = fin.read();
        //Used to write code as bits into a file
        BitOutputStream bitOut = new BitOutputStream(compressedFilename);
        //Replace every character with the code
        while (b != -1) {
            bitOut.writeString(codeArray[b]);
            b = fin.read();
        }
        //Close the file
        bitOut.close();

    }

    /**
     * Encode a file using a huffman tree and output it to a new file as well as recording the codes to a file
     * @param compressedFilename  Name of the original file to be decompressed
     * @param codeFilename      Name of the file to input the codes
     * @param decompressedFilename Name of the file to output decoded text
     * @throws Exception If the file doesn't exist or
     */
    public static void decode(String compressedFilename, String codeFilename, String decompressedFilename) throws Exception {

        //Used to sort the reconstruction of the new tree
        PriorityQueue<HuffmanNode> sort = new PriorityQueue<>(256, new HuffmanCompare());

        //Used to read in the codes
        BufferedReader codeFile = new BufferedReader(new FileReader(codeFilename));
        //Holds one line of the file
        String singleLine;
        //Splits the line into several smaller strings
        String [] lineSplit;
        //Going to hold the frequency of each character and the character that it represents
        int [][] split = new int[256][2];

        //Remake the Huffman Tree to find the code
        //Read the frequency and and character for the array
        while( (singleLine = codeFile.readLine()) != null){
            //Split the line into each number
            lineSplit = singleLine.split(" ");
            //Record the ascii value and the frequency in the array
            int cVal = Integer.parseInt(lineSplit[0]);
            int freq = Integer.parseInt(lineSplit[1]);
            split[cVal][0] = cVal;
            split[cVal][1] = freq;
        }

        //Put the nodes into a priority queue for sorting
        for(int i = 0; i < 256; i++){
            //If that character happens at least once put it into the queue
            if(split[i][1] != 0) {
                HuffmanNode temp = new HuffmanNode();
                temp.c = (char) split[i][0];
                temp.freq = split[i][1];
                sort.add(temp);
            }
        }

        //The root of the whole Huffman Tree
        HuffmanNode root = null;

        //Compress the tree until only one node remains
        while(sort.size() > 1){
            //Take the two smallest nodes
            HuffmanNode node1 = sort.poll();
            HuffmanNode node2 = sort.poll();
            HuffmanNode total = new HuffmanNode();

            //Combine the two nodes into one
            total.left = node1;
            total.right = node2;
            total.freq = node1.freq + node2.freq;

            //Make it into the new root
            root = total;

            //Add the new node to the queue
            sort.add(root);
        }

        //Used to write the decompressed file
        BufferedWriter fout = new BufferedWriter(new FileWriter(decompressedFilename));
        //Used to traverse the tree
        HuffmanNode traverse = root;
        //Reading bit by bit
        BitInputStream bitIn = new BitInputStream(compressedFilename);
        int bit = bitIn.nextBit();

        //Read in bits until the end of the file
        while (bit != -1) {
            //If a leaf is reached record the character into the file and go back to the head
            if (traverse.right == null && traverse.left == null){
                fout.write(traverse.c);
                traverse = root;
            }

            //If the bit is 1 go right otherwise left for 1
            if(bit == 1){
                traverse = traverse.right;
            }
            else if (bit == 0){
                traverse = traverse.left;
            }
            bit = bitIn.nextBit();
        }

        //Close the file
        fout.close();

    }

    /**
     *  Finds and writes all of the codes into a file as well as an array for the decoding
     * @param bfr Buffered writer for writing the codes to a file
     * @param root The root node of the tree
     * @param code The code made by traversing the tree
     * @throws Exception
     */
    private static void writeCode(BufferedWriter bfr, HuffmanNode root, String code) throws Exception{
        //If a leaf is reached record the code int the file and in the code array
        if(root.left == null && root.right == null){
            bfr.write((int) root.c + " " + root.freq + " " + code + '\n');
            codeArray[(int) root.c] = code;
        }
        //If there is only a left node traverse only left
        else if (root.left != null && root.right == null){
            writeCode(bfr, root.left, code + '0');
        }
        //If there is only a right node traverse only right
        else if(root.right != null && root.left == null){
            writeCode(bfr, root.right, code + '1');
        }
        //Otherwise traverse both the left and the right
        else{
            writeCode(bfr, root.right, code + '1');
            writeCode(bfr, root.left, code + '0');
        }
    }
}

/**
 * Used to make the huffman tree. The nodes the tree is made of
 */
class HuffmanNode {
    //The character
    char c;
    //The frequency of this character
    int freq;

    HuffmanNode left;
    HuffmanNode right;
}

/**
 * Comparator for huffman nodes, needed to make a priority queue
 */
class HuffmanCompare implements Comparator<HuffmanNode>{
    @Override
    public int compare(HuffmanNode o1, HuffmanNode o2) {
        if(o1.freq - o2.freq > 0)
            return 1;
        else if(o2.freq - o1.freq > 0)
            return - 1;
        else
            return 0;
    }
}
