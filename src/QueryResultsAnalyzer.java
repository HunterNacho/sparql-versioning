import java.io.*;

public class QueryResultsAnalyzer {
    private static final String BASE_FOLDER = "/home/icuevas/scripts/queries/wrapped/results/";
    public static void main(String[] args) throws IOException {
        BufferedWriter table = new BufferedWriter(new FileWriter("results_table.csv"));
        StringBuilder allGraphs = new StringBuilder();
        allGraphs.append("o");
        for (int i = 0; i < Constants.GRAPHS.length; i++) {
            allGraphs.append(", ");
            allGraphs.append("filtered-");
            allGraphs.append(Constants.GRAPHS[i]);
        }
        table.write(allGraphs.toString());
        table.newLine();
        for (int i = 0; i < 385; i++) {
            StringBuilder tableRow = new StringBuilder();
            String queryId = String.format("q%03d", i);
            tableRow.append(queryId);
            QueryResultsHandler query;
            QueryResultsHandler previousQuery = null;
            boolean queryExists = true;
            for (int j = 0; j < Constants.GRAPHS.length; j++) {
                try {
                    query = new QueryResultsHandler(BASE_FOLDER + "filtered-" + Constants.GRAPHS[j] + "_" + queryId);
                }
                catch (FileNotFoundException e) {
                    queryExists = false;
                    break;
                }
                tableRow.append(", ");
                if (query.getResultsAmount() == 0)
                    tableRow.append(-1);
                else if (j == 0)
                    tableRow.append(query.getResultsAmount());
                else
                    tableRow.append(query.compareTo(previousQuery));
                previousQuery = query;
            }
            if (!queryExists)
                continue;
            table.write(tableRow.toString());
            table.newLine();
        }
        table.close();
    }
}