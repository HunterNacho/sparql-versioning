import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ignacio on 02-12-2018.
 */
public class FindMaxEntityId {

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
        if (args.length < 1) {
            System.out.println("Usage: FindMaxEntityId <file>");
            System.exit(0);
            return;
        }
        BufferedReader file = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0]))));
        String line;
        long progress = 0;
        int maxId = -1;
        String maxLine = "";
        for (line = file.readLine(); line != null; line = file.readLine()) {
            if (line.isEmpty()) {
                progress++;
                continue;
            }
            line = line.trim();
            String[] triple = line.split(" ");
            String subject = triple[0];
            String object = triple[2];
            int subjectId = getIdNumber(subject);
            int objectId = -1;
            if (object.startsWith("<"))
                objectId = getIdNumber(object);
            if (subjectId > maxId) {
                maxId = subjectId;
                maxLine = line;
            }
            if (objectId > maxId) {
                maxId = objectId;
                maxLine = line;
            }
            progress++;
        }
        file.close();
        System.out.println("Max Q id is " + maxId);
        System.out.println(maxLine);
    }
}
