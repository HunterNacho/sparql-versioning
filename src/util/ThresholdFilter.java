package util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ThresholdFilter {
    private static final String outputFolder = "filtered/";
    private static final int threshold = 100000;
    private static final Pattern pattern = Pattern.compile("Q\\d+>");

    private static int getIdNumber(String entity) {
        assert entity.startsWith("<") && entity.endsWith(">");
        Matcher matcher = pattern.matcher(entity);
        if (!matcher.matches())
            return -1;
        int index = entity.lastIndexOf("Q");
        entity = entity.substring(index + 1, entity.length() - 1);
        return Integer.parseInt(entity);
    }

    public static void main(String[] args) throws IOException{
        if(args.length < 1) {
            System.out.println("Filename not specified");
            System.exit(0);
            return;
        }
        String filename =  args[0];
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
