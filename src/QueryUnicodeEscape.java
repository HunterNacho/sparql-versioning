import java.io.*;

public class QueryUnicodeEscape {
    public static void main(String[] args) throws IOException{
        File folder = new File(args[0]);
        assert folder.isDirectory();
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() || !file.getName().contains(".rq"))
                continue;
            BufferedWriter writer = new BufferedWriter(new FileWriter("clean/" + file.getName()));
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                writer.write(line.replace("\\u", "\\\\u"));
                writer.newLine();
            }
            writer.close();
            reader.close();
        }
    }
}
