import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ThresholdFilter {
    private static final String outputFolder = "filtered/";

    private static int getIdNumber(String entity) {
        assert entity.startsWith("<") && entity.endsWith(">");
        int index = entity.lastIndexOf("Q");
        if (index < 0)
            return index;
        entity = entity.substring(index + 1, entity.length() - 1);
        int number;
        try {
            number = Integer.parseInt(entity);
        }
        catch (NumberFormatException e) {
            number = -1;
        }
        return number;
    }

    public static void main(String[] args) throws IOException{
        if(args.length < 2) {
            System.out.println("Usage: ThresholdFilter <file> <threshold>");
            System.exit(0);
            return;
        }
        String filename =  args[0];
        long threshold = Long.parseLong(args[1]);
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
        int progress = 0;
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
            int subjectId = getIdNumber(subject);
            int objectId = 0;
            if (object.startsWith("<"))
                objectId = getIdNumber(object);
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
