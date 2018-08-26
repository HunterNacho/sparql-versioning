import java.io.*;

public class QueryCleaner {
    private static final String inputFolder = "clean/";
    private static final String outputFolder = "wrapped/";
    public static void main(String[] args) throws IOException{
        for (int i = 0; i < 385; i++) {
            String filename;
            if (i < 10) {
                filename = "q00" + i;
            }
            else if (i < 100) {
                filename = "q0" + i;
            }
            else {
                filename = "q" + i;
            }
            BufferedReader input;
            try {
                input = new BufferedReader(new FileReader(inputFolder + filename + ".rq"));
            }
            catch (FileNotFoundException e) {
                System.err.println("Skipping file " + filename + ".rq");
                continue;
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(outputFolder + filename + ".sql"));
            output.write("SPARQL");
            output.newLine();
            String line;
            while ((line = input.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")){
                    continue;
                }
                line = line.trim();
                if (!line.contains("#>") && line.contains("#")) {
                    line = line.split("#")[0];
                }
                output.write(line);
                output.newLine();
            }
            input.close();
            output.newLine();
            output.write(";");
            output.newLine();
            output.close();
            if ((i % 100) == 0) {
                System.out.println(i + " files processed");
            }
        }
        System.out.println("Done");
    }
}
