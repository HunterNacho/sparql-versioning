import java.io.*;

public class QueryConverter {
    private static final String baseFolder = "/home/icuevas/scripts/queries/wrapped/";
    private static final String[] graphs = new String[]{
            "filtered-20170830",
            "filtered-20170907",
            "filtered-20170913",
            "filtered-20170920",
            "filtered-20170927"
    };
    public static void main (String[] args) throws IOException{
        if (args.length < 1) {
            System.err.println("Password not provided");
            System.exit(1);
            return;
        }
        String password =  args[0];
        String scriptHeader = "isql-vt 1111 dba " + password + " exec=\"load ";
        String scriptTail = ";\" > results/";
        BufferedWriter script = new BufferedWriter(new FileWriter(baseFolder + "exec-all-queries"));
        for (int i = 0; i < 385; i++) {
            if ((i % 50) == 0)
                System.out.println("Proccessed " + i + " files");
            String queryId = "q" + String.format("%03d", i);
            String filename = baseFolder + queryId + ".sql";
            QueryHandler query;
            try {
                query = new QueryHandler(filename);
            }
            catch (FileNotFoundException e) {
                System.err.println("Skipping file " + queryId + ".sql");
                continue;
            }
            for (int j = 0; j < 5; j++) {
                String currentGraph = graphs[j];
                String output = baseFolder + currentGraph+ "/" + queryId + ".sql";
                script.write(scriptHeader + output + scriptTail + currentGraph + "_" + queryId);
                script.newLine();
                query.insertGraphAndWrite("http://wikidata.org/" + currentGraph, output);
            }
        }
        script.close();
    }
}
