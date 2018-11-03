import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FilePartitioner {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: FilePartitioner <filename> <lines-per-partition> <target-graph>");
            System.exit(0);
            return;
        }
        String inputFilename = args[0];
        long partitionLines = Long.parseLong(args[1]);
        String targetGraph = args[2];
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputFilename))));
        String outputFilename = inputFilename;
        if (inputFilename.indexOf('.') > -1)
            outputFilename = inputFilename.substring(0, inputFilename.indexOf('.'));
        String outputFolder = outputFilename + "-partition/";
        outputFilename = outputFolder + outputFilename;
        long currentLines = 0;
        long totalLines = 0;
        long fileIndex = 1;
        BufferedWriter writer = openFile(outputFilename, fileIndex);
        BufferedWriter loadFile = new BufferedWriter(new FileWriter(outputFolder + "load-parts"));
        String line;
        while ((line = reader.readLine()) != null) {
            if ((totalLines % 1000000) == 0)
                System.out.println("Processed " + totalLines + " lines");
            if (currentLines == partitionLines) {
                loadFile.write("ld_add('" + getPartName(outputFilename, fileIndex) +
                        "', '"+ targetGraph + "');");
                loadFile.newLine();
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
        reader.close();
        loadFile.close();
    }

    private static BufferedWriter openFile(String filename, long partNumber) throws IOException{
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
                getPartName(filename, partNumber)))));
    }

    private static String getPartName(String baseFilename, long partNumber) {
        return baseFilename + "-part-" + partNumber + ".nt.gz";
    }
}
