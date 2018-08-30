import java.io.*;

public class QueryResultsAnalyzer {
    private static final String baseFolder = "/home/icuevas/scripts/queries/wrapped/results/";
    private static final String[] graphs = new String[]{
            "filtered-20170830",
            "filtered-20170907",
            "filtered-20170913",
            "filtered-20170920",
            "filtered-20170927"
    };
    public static void main(String[] args) throws IOException {
        BufferedWriter table = new BufferedWriter(new FileWriter("results_table.csv"));
        String allGraphs = graphs.toString();
        allGraphs = allGraphs.substring(1, allGraphs.length() - 1);
        table.write("o, " + allGraphs);
        table.newLine();
        for (int i = 0; i < 385; i++) {
            StringBuilder tableRow = new StringBuilder();
            String queryId = String.format("q%03d", i);
            tableRow.append(queryId);
            QueryResultsHandler query;
            QueryResultsHandler previousQuery;
            boolean queryExists = true;
            for (int j = 0; j < graphs.length; j++) {
                try {
                    query = new QueryResultsHandler(baseFolder + graphs[j] + "_" + queryId);
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
    }
}