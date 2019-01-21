import java.io.*;

public class QueryResultsCsvBuilder {
    public static void main(String[] args) throws IOException {
        File folder = new File(args[0]);
        File[] files = folder.listFiles();
        assert files != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter("results.csv"));
        for (File file : files) {
            if (file.isDirectory() || !file.getName().contains(".rq")) {
                continue;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String stats = "N/A";
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.contains("msec."))
                    stats = line;
            }
            writer.write(file.getName() + ", " + stats);
            writer.newLine();
            reader.close();
        }
        writer.close();
    }
}
