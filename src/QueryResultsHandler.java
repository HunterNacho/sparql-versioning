import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

class QueryResultsHandler {
    private ArrayList<String> results = new ArrayList<>();
    QueryResultsHandler(String filename) throws IOException {
        BufferedReader file;
        file = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = file.readLine()) != null) {
            if (!line.contains("http"))
                continue;
            results.add(line.trim());
        }
        Collections.sort(results);
        file.close();
    }
    int getResultsAmount() {
        return results.size();
    }

    int compareTo(QueryResultsHandler other) {
        int i = 0;
        int j = 0;
        int diffs = 0;
        while ((i < results.size()) && (j < other.results.size())) {
            switch (Integer.signum(results.get(i).compareTo(other.results.get(j)))) {
                case -1:
                    i++;
                    diffs++;
                    continue;
                case 1:
                    j++;
                    diffs++;
                    continue;
                case 0:
                    i++;
                    j++;
                default:
            }
        }
        diffs += results.size() - i;
        diffs += other.results.size() - j;
        return diffs;
    }

    ArrayList<String> minus(QueryResultsHandler other) {
        ArrayList<String> delta = new ArrayList<>();
        int i = 0;
        int j = 0;
        while ((i < results.size()) && (j < other.results.size())) {
            switch (Integer.signum(results.get(i).compareTo(other.results.get(j)))) {
                case -1:
                    delta.add(results.get(i));
                    i++;
                    continue;
                case 1:
                    j++;
                    continue;
                case 0:
                default:
                    i++;
                    j++;
            }
        }
        while (i < results.size()) {
            delta.add(results.get(i));
            i++;
        }
        return delta;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: QueryResultsHandler <input> <output>");
            return;
        }
        QueryResultsHandler handler = new QueryResultsHandler(args[0]);
        BufferedWriter output = new BufferedWriter(new FileWriter(args[1]));
        for (String line : handler.results) {
            output.write(line);
            output.newLine();
        }
        output.close();
    }
}
