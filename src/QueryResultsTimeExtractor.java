import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class QueryResultsTimeExtractor {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: QueryResultsTimeExtractor <input_folder>");
        }
        File folder = new File(args[0]);
        File[] queryFolders = folder.listFiles();
        assert queryFolders != null;
        Arrays.sort(queryFolders);
        StringBuilder queries = new StringBuilder();
        StringBuilder times = new StringBuilder();
        for (File queryFolder : queryFolders) {
            if (!queryFolder.isDirectory() || !queryFolder.getName().startsWith("q") || queryFolder.getName().endsWith("prev")) {
                continue;
            }
            String queryName = queryFolder.getName();
            boolean splitDiff = false;
            if (queryName.endsWith("curr")) {
                queryName = queryName.replace("_curr", "");
                splitDiff = true;
            }
            queries.append(queryName);
            queries.append(", ");
            ArrayList<Long> execTimes = new ArrayList<>();
            File[] files = queryFolder.listFiles();
            assert files != null;
            Arrays.sort(files);
            for (File file : files) {
                long time = 300000;
                BufferedReader reader = new BufferedReader(new FileReader(file));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (line.contains("msec.")) {
                        String[] split = line.split("--");
                        time = Long.parseLong(split[1].replace("msec.", "").trim());
                        break;
                    }
                }
                execTimes.add(time);
                reader.close();
            }
            long avgTime = 0;
            for (Long time : execTimes)
                avgTime = avgTime + time;
            avgTime = avgTime/execTimes.size();
            if (splitDiff) {
                File prevQueryFolder = new File(queryFolder.getAbsolutePath().replace("curr", "prev"));
                ArrayList<Long> prevTimes = new ArrayList<>();
                File[] prevFiles = prevQueryFolder.listFiles();
                assert prevFiles != null;
                Arrays.sort(files);
                for (File file : prevFiles) {
                    long time = 300000;
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (line.contains("msec.")) {
                            String[] split = line.split("--");
                            time = Long.parseLong(split[1].replace("msec.", "").trim());
                            break;
                        }
                    }
                    prevTimes.add(time);
                    reader.close();
                }
                long prevAvgTime = 0;
                for (Long time : prevTimes)
                    prevAvgTime = prevAvgTime + time;
                prevAvgTime = prevAvgTime/prevTimes.size();
                avgTime = avgTime + prevAvgTime;
            }
            times.append(avgTime);
            times.append(", ");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(folder.getAbsolutePath() + "/query-times"));
        writer.write(queries.toString());
        writer.newLine();
        writer.write(times.toString());
        writer.newLine();
        writer.close();
    }
}
