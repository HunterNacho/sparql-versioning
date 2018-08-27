import java.io.*;
import java.util.ArrayList;

class QueryHandler {
    private ArrayList<String> lines = new ArrayList<>();
    private int openingBraceLineIndex;
    private int openingBraceCharIndex;
    private int closingBraceLineIndex;
    private int closingBraceCharIndex;
    QueryHandler(String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = input.readLine()) != null) {
            line = line.trim();
            lines.add(line);
        }
        input.close();
        findOpeningBraceIndexes();
        findClosingBraceIndexes();
    }

    private void findOpeningBraceIndexes() {
        boolean selectFound = false;
        int whereLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!selectFound && line.toUpperCase().contains("SELECT")) {
                selectFound = true;
            }
            if ((whereLine < 0) && line.toUpperCase().contains("WHERE")) {
                whereLine = i;
            }
            if (selectFound && line.contains("{")) {
                if ((i == whereLine) && (line.lastIndexOf("{") < line.toUpperCase().indexOf("WHERE")))
                    continue;
                openingBraceLineIndex = i;
                openingBraceCharIndex = line.indexOf("{", (i == whereLine) ? line.toUpperCase().indexOf("WHERE") : 0);
                return;
            }
        }
    }

    private void findClosingBraceIndexes() {
        int braces = 1;
        for (int i = openingBraceLineIndex; i < lines.size(); i++) {
            String toCheck = lines.get(i);
            if (i == openingBraceLineIndex) {
                toCheck = toCheck.substring(openingBraceCharIndex + 1);
            }
            for (int j = 0; j < toCheck.length(); j++) {
                switch (toCheck.charAt(j)) {
                    case '{':
                        braces++;
                        break;
                    case '}':
                        braces--;
                        break;
                    default:
                        break;
                }
                if (braces == 0) {
                    closingBraceLineIndex = i;
                    closingBraceCharIndex = j;
                    return;
                }
            }
        }
    }

    void insertGraphAndWrite(String graphName, String outputFilename) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(outputFilename));
        int i;
        for (i = 0; i < lines.size(); i++) {
            if ((i == openingBraceLineIndex) && (i == closingBraceLineIndex)) {
                String line = lines.get(i);
                output.write(
                        line.substring(0, openingBraceCharIndex + 1)
                        + " GRAPH <" + graphName + "> {"
                        + line.substring(openingBraceCharIndex + 1, closingBraceCharIndex)
                        + "}" + line.substring(closingBraceCharIndex)
                );
                output.newLine();
                continue;
            }
            if (i == openingBraceLineIndex) {
                String line = lines.get(i);
                output.write(
                        line.substring(0, openingBraceCharIndex + 1)
                        + " GRAPH <" + graphName + "> {"
                        + line.substring(openingBraceCharIndex + 1)
                );
                output.newLine();
                continue;
            }
            if (i == closingBraceLineIndex) {
                String line = lines.get(i);
                output.write(line.substring(0, closingBraceCharIndex) + "}" + line.substring(closingBraceCharIndex));
                output.newLine();
                continue;
            }
            output.write(lines.get(i));
            output.newLine();
        }
        output.close();
    }
}
