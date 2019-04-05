import java.io.*;

public class QueryResultsCsvBuilder {
    public static void main(String[] args) throws IOException {
        File folder = new File(args[0]);
        String output = args[1];
        File[] files = folder.listFiles();
        assert files != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        writer.write("query,rows,time");
        writer.newLine();
        for (File file : files) {
            if (file.isDirectory() || !file.getName().contains(".rq")) {
                continue;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String stats = "";
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.contains("msec."))
                    stats = line;
            }
            int rows = 0;
            long time = 0;
            if (!stats.isEmpty()) {
                String[] split = stats.split("--");
                rows = Integer.parseInt(split[0].replace("Rows.", "").trim());
                time = Long.parseLong(split[1].replace("msec.", "").trim());
            }
            writer.write(file.getName() + "," + rows + "," + time);
            writer.newLine();
            reader.close();
        }
        writer.close();
    }
}
