import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IntervalGraphBuilder {
    // Filenames should have this format: "wikidata-YYYYMMDD-truthy-BETA.nt.gz"
    private final static String TAIL = "-truthy-BETA.nt.gz";
    private static final HashMap<Interval, BufferedWriter> writerHashMap = new HashMap<>();
    private static final int LENGTH = Constants.GRAPHS.length;
    private static BufferedReader[] files = new BufferedReader[LENGTH];

    public static void main(String[] args) throws IOException {
        long initialTime = System.currentTimeMillis();
        BufferedWriter graphInfo = new BufferedWriter(new FileWriter(Constants.DATA_FOLDER + "intervals/graph_data.nt"));
        String[] lines = new String[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            files[i] = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
                    Constants.DATA_FOLDER + "wikidata-" + Constants.GRAPHS[i] + TAIL
            ))));
            for (int j = i; j < LENGTH; j++) {
                String interval = Constants.GRAPHS[i] + "-" + Constants.GRAPHS[j];
                writerHashMap.put(new Interval(i, j),
                        new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
                        Constants.DATA_FOLDER + "intervals/" + interval + ".nt.gz"
                )))));
                String start = "<http://wikidata.org/intervals/" +
                        interval +
                        "> <http://wikidata.org/intervals/start>  \"" +
                        Constants.GRAPHS[i] +
                        "\" .";
                String end = "<http://wikidata.org/intervals/" +
                        interval +
                        "> <http://wikidata.org/intervals/end> \"" +
                        Constants.GRAPHS[j] +
                        "\" .";
                graphInfo.write(start);
                graphInfo.newLine();
                graphInfo.write(end);
                graphInfo.newLine();
            }
            String line = files[i].readLine();
            while (line != null && line.trim().isEmpty())
                line = files[i].readLine();
            if (line != null)
                line = line.trim();
            lines[i] = line;
        }
        long processed = 0;
        while (!allNull(lines)) {
            if ((processed % 1000000) == 0)
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
                if (lines[i] == null) {
                    matches[i] = false;
                    continue;
                }
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
                if (!matches[i])
                    continue;
                String line = files[i].readLine();
                while (line != null && line.trim().isEmpty())
                    line = files[i].readLine();
                if (line != null)
                    line = line.trim();
                lines[i] = line;
            }
        }
        for (BufferedReader file : files)
            file.close();
        for (BufferedWriter writer : writerHashMap.values())
            writer.close();
        graphInfo.close();
        long endTime = System.currentTimeMillis();
        BufferedWriter timeWriter = new BufferedWriter(new FileWriter("interval-time-stats"));
        timeWriter.write("Interval building took " + (endTime - initialTime) + " ms");
        timeWriter.newLine();
        timeWriter.close();
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
                if (i + 1 == matches.length)
                    intervals.add(new Interval(start, end));
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
        @Override
        public int hashCode() {
            int result = 5;
            result = 37 * result + start;
            result = 37 * result + end;
            return result;
        }
        @Override
        public boolean equals(Object other) {
            return other != null && other instanceof Interval
                    && start == ((Interval) other).start && end == ((Interval) other).end;
        }
    }

}
