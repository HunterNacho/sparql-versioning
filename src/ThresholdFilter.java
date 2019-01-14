import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ThresholdFilter {

    public static void main(String[] args) throws IOException{
        if(args.length < 3) {
            System.out.println("Usage: ThresholdFilter <file> <threshold> <output-folder>");
            System.exit(0);
            return;
        }
        String filename =  args[0];
        long threshold = Long.parseLong(args[1]);
        String outputFolder = args[2];
        BufferedReader file;
        try {
            file = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            System.exit(1);
            return;
        }
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFolder + filename))));
        String line;
        long progress = 0;
        while ((line = file.readLine()) != null) {
            if ((progress % 1000000) == 0) {
                System.out.println("Read " + progress + " lines");
            }
            if (line.isEmpty()) {
                progress++;
                continue;
            }
            line = line.trim();
            String[] triple = line.split(" ");
            String subject = triple[0];
            String object = triple[2];
            long subjectId = Constants.getIdNumber(subject);
            long objectId = 0;
            if (object.startsWith("<"))
                objectId = Constants.getIdNumber(object);
            if ((subjectId < threshold) && (objectId < threshold)) {
                output.write(line);
                output.newLine();
            }
            progress++;
        }
        output.close();
        file.close();
    }
}
