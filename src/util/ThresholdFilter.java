package util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ThresholdFilter {
    private static final String outputFolder = "filtered/";
    // Prefixes
    private static final String wd = "http://www.wikidata.org/entity/";
    //private static final String wdt = "http://www.wikidata.org/prop/direct/";
    private static final int threshold = 100000;

    private static int getIdNumber(String entity) {
        assert entity.startsWith("<" + wd) && entity.endsWith(">");
        int index = entity.lastIndexOf("Q");
        if (index < 0)
            return 0;
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
            if (!subject.contains(wd) && !object.contains(wd)) {
                output.write(line);
                output.newLine();
                progress++;
                continue;
            }
            int subjectId = 0;
            if (subject.startsWith("<" + wd))
                subjectId = getIdNumber(subject);
            int objectId = 0;
            if (object.startsWith("<" + wd))
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
