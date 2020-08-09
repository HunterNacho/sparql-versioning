import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class QueryLoaderScriptGenerator {
    public static void main(String[] args) throws IOException {
        final int NUMBER_OF_TRIALS = 3;
        if (args.length < 2) {
            System.out.println("Usage: QueryLoaderScriptGenerator <script_folder> <output_folder> [manual-diff]");
            System.exit(0);
            return;
        }
        File queryFolder = new File(args[0]);
        if (!queryFolder.isDirectory()) {
            System.out.println("Argument must be a folder");
            System.exit(0);
            return;
        }
        String outputFolder = args[1];
        if (!outputFolder.endsWith("/"))
            outputFolder = outputFolder.concat("/");
        BufferedWriter writer = new BufferedWriter(new FileWriter(queryFolder.getAbsolutePath() + "/load-queries"));
        File[] files = queryFolder.listFiles();
        assert files != null;
        Arrays.sort(files);
        for (File file : files) {
            if (file.isDirectory() || !file.getName().contains(".rq"))
                continue;
            String filename = file.getName().replace(".rq", "");
            writer.write("mkdir " + outputFolder + filename);
            writer.newLine();
            for (int i = 0; i < NUMBER_OF_TRIALS ; i++) {
                writer.write(
                        "isql-vt 1111 dba dba " + file.getAbsolutePath() +
                                " > " + outputFolder + filename + "/" + i
                );
                writer.newLine();
            }
        }
        writer.close();
    }
}
