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
        for (line = file.readLine(); line != null; line = file.readLine()) {
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
            if (subjectId > maxId)
                maxId = subjectId;
            if (objectId > maxId)
                maxId = objectId;
            progress++;
        }
        file.close();
        System.out.println("Max Q id is " + maxId);
    }
}
