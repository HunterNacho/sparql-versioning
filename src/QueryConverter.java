import java.io.*;

public class QueryConverter {
    private static final String BASE_FOLDER = "/home/icuevas/scripts/queries/wrapped/";
    public static void main (String[] args) throws IOException{
        if (args.length < 1) {
            System.err.println("Password not provided");
            System.exit(1);
            return;
        }
        String password =  args[0];
        String scriptHeader = "isql-vt 1111 dba " + password + " exec=\"load ";
        String scriptTail = ";\" > results/";
        BufferedWriter script = new BufferedWriter(new FileWriter(BASE_FOLDER + "exec-all-queries"));
        for (int i = 0; i < 385; i++) {
            if ((i % 50) == 0)
                System.out.println("Processed " + i + " files");
            String queryId = "q" + String.format("%03d", i);
            String filename = BASE_FOLDER + queryId + ".sql";
            QueryHandler query;
            try {
                query = new QueryHandler(filename);
            }
            catch (FileNotFoundException e) {
                System.err.println("Skipping file " + queryId + ".sql");
                continue;
            }
            for (int j = 0; j < 5; j++) {
                String currentGraph = "filtered-" + Constants.GRAPHS[j];
                String output = BASE_FOLDER + currentGraph+ "/" + queryId + ".sql";
                script.write(scriptHeader + output + scriptTail + currentGraph + "_" + queryId);
                script.newLine();
                query.insertGraphAndWrite("http://wikidata.org/" + currentGraph, output);
            }
        }
        script.close();
    }
}
