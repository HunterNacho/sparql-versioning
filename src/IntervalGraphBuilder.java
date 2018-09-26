import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IntervalGraphBuilder {
    private static final String BASE_FOLDER = "/home/icuevas/data/filtered/";
    // Filenames should have this format: "wikidata-YYYYMMDD-truthy-BETA.nt.gz"
    private final static String TAIL = "-truthy-BETA.nt.gz";
    private static final HashMap<Interval, BufferedWriter> writerHashMap = new HashMap<>();
    private static final int LENGTH = Constants.GRAPHS.length;
    private static BufferedReader[] files = new BufferedReader[LENGTH];

    public static void main(String[] args) throws IOException {
        BufferedWriter graphInfo = new BufferedWriter(new FileWriter(BASE_FOLDER + "intervals/graph_data.nt"));
        String[] lines = new String[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            files[i] = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
                    BASE_FOLDER + "wikidata-" + Constants.GRAPHS[i] + TAIL
            ))));
            for (int j = i; j < LENGTH; j++) {
                writerHashMap.put(new Interval(i, j),
                        new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
                        BASE_FOLDER + "intervals/" + Constants.GRAPHS[i] + "-" + Constants.GRAPHS[j] + ".nt.gz"
                )))));
                String start = "<http://wikidata.org/intervals/" +
                        Constants.GRAPHS[i] +
                        "-" +
                        Constants.GRAPHS[j] +
                        "> <http://wikidata.org/intervals/start>  \"" +
                        Constants.GRAPHS[i] +
                        "\" .";
                String end = "<http://wikidata.org/intervals/" +
                        Constants.GRAPHS[i] +
                        "-" +
                        Constants.GRAPHS[j] +
                        "> <http://wikidata.org/intervals/end> \"" +
                        Constants.GRAPHS[j] +
                        "\" .";
                graphInfo.write(start);
                graphInfo.newLine();
                graphInfo.write(end);
                graphInfo.newLine();
            }
            lines[i] = files[i].readLine();
        }
        int processed = 0;
        while (!allNull(lines)) {
            if ((processed % 10000000) == 0)
                System.out.println("Processed " + processed + " lines");
            String min = null;
            for (int i = 0; i < LENGTH; i++) {
                if (lines[i] == null)
                    continue;
                if (min == null || lines[i].compareTo(min) < 0)
                    min = lines[i];
            }
            boolean[] matches = new boolean[LENGTH];
            for (int i = 0; i < LENGTH; i++) {
                if (lines[i] == null)
                    continue;
                matches[i] = lines[i].equals(min);
            }
            ArrayList<Interval> intervals = getIntervals(matches);
            for (Interval interval : intervals) {
                BufferedWriter writer = writerHashMap.get(interval);
                assert min != null;
                writer.write(min);
                writer.newLine();
            }
            processed++;
            for (int i = 0; i < LENGTH; i++) {
                if (lines[i] == null || !matches[i])
                    continue;
                lines[i] = files[i].readLine();
            }
        }
        for (BufferedReader file : files)
            file.close();
        for (BufferedWriter writer : writerHashMap.values())
            writer.close();
        graphInfo.close();
    }

    private static ArrayList<Interval> getIntervals(boolean[] matches) {
        ArrayList<Interval> intervals = new ArrayList<>();
        int start = -1;
        int end = -1;
        for (int i = 0; i < matches.length; i++) {
            if (matches[i]) {
                if (start < 0) {
                    start = i;
                }
                end = i;
            }
            else {
                if (start >= 0) {
                    intervals.add(new Interval(start, end));
                    start = -1;
                    end = -1;
                }
            }
        }
        return intervals;
    }

    private static boolean allNull(Object[] objects) {
        for (Object object : objects) {
            if (object != null)
                return false;
        }
        return true;
    }

    private static class Interval {
        private int start;
        private int end;
        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
        public String getStart() {
            return Constants.GRAPHS[start];
        }
        public String getEnd() {
            return Constants.GRAPHS[end];
        }
        @Override
        public int hashCode() {
            return 17 * start + 31 * end;
        }
        @Override
        public boolean equals(Object other) {
            return other != null && other instanceof Interval
                    && start == ((Interval) other).start && end == ((Interval) other).end;
        }
    }

}
