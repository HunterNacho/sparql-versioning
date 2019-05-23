import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ResultsDiffGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("usage: ResultsDiffGenerator <input_1> <input_2> <output>");
            return;
        }
        QueryResultsHandler input1 = new QueryResultsHandler(args[0]);
        QueryResultsHandler input2 = new QueryResultsHandler(args[1]);
        BufferedWriter output = new BufferedWriter(new FileWriter(args[2]));
        ArrayList<String> diff = input1.minus(input2);
        for (String result : diff) {
            output.write(result);
            output.newLine();
        }
        output.close();
    }
}
