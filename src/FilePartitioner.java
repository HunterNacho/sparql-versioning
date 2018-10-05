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
        BufferedWriter loadFile = new BufferedWriter(new FileWriter("load-parts"));
        loadFile.write("DELETE FROM DB.DBA.load_list;");
        loadFile.newLine();
        String line;
        while ((line = reader.readLine()) != null) {
            if ((totalLines % 1000000) == 0)
                System.out.println("Processed " + totalLines + " lines");
            if (currentLines == partitionLines) {
                loadFile.write("ld_add('" + getPartName(outputFilename, fileIndex) +
                        "', 'http://wikidata.org/intervals/20170830-20170927');");
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
        loadFile.write("rdf_loader_run();");
        loadFile.newLine();
        loadFile.close();
    }

    private static BufferedWriter openFile(String filename, int partNumber) throws IOException{
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
                getPartName(filename, partNumber)))));
    }

    private static String getPartName(String baseFilename, int partNumber) {
        return baseFilename + "-part-" + partNumber + ".nt.gz";
    }
}
