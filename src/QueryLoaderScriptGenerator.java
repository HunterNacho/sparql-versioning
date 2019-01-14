import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class QueryLoaderScriptGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: QueryLoaderScriptGenerator <script_folder>");
            System.exit(0);
            return;
        }
        File queryFolder = new File(args[0]);
        if (!queryFolder.isDirectory()) {
            System.out.println("Argument must be a folder");
            System.exit(0);
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(queryFolder.getAbsolutePath() + "/load-queries"));
        File[] files = queryFolder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() || !file.getName().contains(".rq"))
                continue;
            writer.write(
                    "isql-vt 1111 dba dba exec=\"load " + file.getAbsolutePath() +
                            ";\" > " + queryFolder.getAbsolutePath() + "/results/" + file.getName()
            );
            writer.newLine();
        }
        writer.close();
    }
}
