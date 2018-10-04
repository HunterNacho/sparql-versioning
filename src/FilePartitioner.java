import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FilePartitioner {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: FilePartitioner <filename> <lines-per-partition>");
        }
        String inputFilename = args[0];
        int partitionLines = Integer.parseInt(args[1]);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputFilename))));
        String outputFilename = inputFilename;
        if (inputFilename.indexOf('.') > -1)
            outputFilename = inputFilename.substring(0, inputFilename.indexOf('.'));
        int currentLines = 0;
        int totalLines = 0;
        int fileIndex = 1;
        BufferedWriter writer = openFile(outputFilename, fileIndex);
        String line;
        while ((line = reader.readLine()) != null) {
            if ((totalLines % 1000000) == 0)
                System.out.println("Processed " + totalLines + " lines");
            if (currentLines == partitionLines) {
                writer.close();
                fileIndex++;
                writer = openFile(outputFilename, fileIndex);
                currentLines = 0;
            }
            writer.write(line);
            writer.newLine();
            currentLines++;
            totalLines++;
        }
    }

    private static BufferedWriter openFile(String filename, int partNumber) throws IOException{
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
                filename + "-part-" + partNumber + ".nt.gz"))));
    }
}
