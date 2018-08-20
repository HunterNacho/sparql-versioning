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
        assert entity.startsWith("<" + wd + "Q") && entity.endsWith(">");
        entity = entity.substring(wd.length() + 2, entity.length() - 1);
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
        while ((line = file.readLine()) != null) {
            if (line.isEmpty())
                continue;
            line = line.trim();
            String[] triple = line.split(" ");
            String subject = triple[0];
            String object = triple[2];
            if (!subject.contains(wd)) {
                output.write(line);
                output.newLine();
                continue;
            }
            int subjectId = getIdNumber(subject);
            int objectId = 0;
            if (object.startsWith("<" + wd))
                objectId = getIdNumber(object);
            if ((subjectId < threshold) && (objectId < threshold)) {
                output.write(line);
                output.newLine();
            }
        }
    }
}