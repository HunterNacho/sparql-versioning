import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DumpCleaner {
    public static void main (String[] args) throws IOException {
        if (args.length < 1)
            return;
        BufferedReader input = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0]))));
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("clean/" + args[0]))));
        String line;
        while ((line = input.readLine()) != null) {
            if (line.contains("#wktLiteral"))
                line = line.replaceAll("#wktLiteral", "#wktliteral");
            output.write(line);
            output.newLine();
        }
        input.close();
        output.close();
    }
}
