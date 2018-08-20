package util;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class DiffCalc {
    private static final String outputFolder = "diffs/";
    public static void main(String[] args) throws IOException{
        if (args.length < 2) {
            System.out.println("Two files must be specified");
            System.exit(0);
            return;
        }
        BufferedReader file1;
        BufferedReader file2;
        try {
            file1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0]))));
            file2 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[1]))));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
            return;
        }
        // Filenames should have this format: "wikidata-YYYYMMDD-truthy-BETA.nt.sorted.gz"
        String date1 = args[0].substring(9,17);
        String date2 = args[0].substring(9,17);
        BufferedWriter deltaAdd = new BufferedWriter(new FileWriter(outputFolder + "delta+_" + date1 + "__" + date2 + ".nt.gz"));
        BufferedWriter deltaSub = new BufferedWriter(new FileWriter(outputFolder + "delta-_" + date1 + "__" + date2 + ".nt.gz"));
        String line1 = file1.readLine();
        String line2 = file2.readLine();
        while (line1!= null && line2!= null) {
            if (line1.isEmpty()) {
                line1 = file1.readLine();
                continue;
            }
            if (line2.isEmpty()) {
                line2 = file2.readLine();
                continue;
            }
            line1 = line1.trim();
            line2 = line2.trim();
            if (line1.equals(line2)) {
                line1 = file1.readLine();
                line2 = file2.readLine();
                continue;
            }
            switch (Integer.signum(line1.compareTo(line2))) {
                case -1:
                    // Line 1 < Line 2
                    deltaSub.write(line1);
                    deltaSub.newLine();
                    line1 = file1.readLine();
                    break;
                case 1:
                    // Line 1 > Line 2
                    deltaAdd.write(line2);
                    deltaAdd.newLine();
                    line2 = file2.readLine();
                    break;
                case 0:
                    // Line 1 = Line 2
                default:
                    line1 = file1.readLine();
                    line2 = file2.readLine();
            }
        }
        while (line2 != null) {
            if (line2.isEmpty()) {
                line2 = file2.readLine();
                continue;
            }
            deltaAdd.write(line2);
            deltaAdd.newLine();
            line2 = file2.readLine();
        }
        while (line1 != null) {
            if (line1.isEmpty()) {
                line1 = file1.readLine();
                continue;
            }
            deltaSub.write(line1);
            deltaSub.newLine();
            line1 = file1.readLine();
        }
    }
}
