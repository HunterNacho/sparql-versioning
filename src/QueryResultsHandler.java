import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
}
