import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class FileParser {
    private static final String[] headingsDefault = new String[]{"PT", "AU", "AF", "BA", "BE", "BF",
            "TI", "SO", "SE", "BN", "CT", "CY", "CL", "SP", "HO", "AB", "RI", "OI", "SN", "EI",
            "PD", "PY", "VL", "AR", "IS", "SI", "BP", "EP", "D2", "DI", "PN", "UT", "PM", "ER"};
    private static final String inputDefault = "results.txt";
    private static final String outputDefault = "results.csv";
    private static final String DELIMITER = ",";

    public static void main(String[] args) throws IOException {
        final FileParser solution = new FileParser();
        solution.generateOutput();
    }

    /**
     * Asks the user for information about the file input and output using console
     * Then produces a file
     *
     * @throws IOException
     */
    private void generateOutput() throws IOException {
        final Scanner in = new Scanner(System.in);
        final Writer out = new PrintWriter(new OutputStreamWriter(System.out));

        final String readFileName =
                getResponseFromUser(in, out, "\nInput file name: ", inputDefault);
        BufferedReader resultsReader = new BufferedReader(new FileReader(readFileName));

        final String writeFileName =
                getResponseFromUser(in, out, "\nOutput file name: ", outputDefault);
        BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(writeFileName));

        final String[] headers = getHeaders(in, out);

        produceFile(headers, resultsReader, resultsWriter);
        out.close();
        in.close();
    }

    /**
     * Either reads the readers from a csv file or uses the default
     *
     * @param in  Input for console
     * @param out Output for console
     * @return The headers to be used
     * @throws IOException
     */
    private String[] getHeaders(final Scanner in, final Writer out) throws IOException {
        final String[] headers;
        if (getResponseFromUser(in, out, "\nWould you like to use default headers? (y/n) ", "y")
                .equals("y")) {
            headers = headingsDefault;
        } else {
            String headingsFileName = getResponseFromUser(in, out, "\nHeadings file name: ", "");
            BufferedReader headingReader = new BufferedReader(new FileReader(headingsFileName));
            headers = headingReader.readLine().split(",");
            headingReader.close();
        }
        return headers;
    }

    /**
     * Writes a message to the user and returns the result or default
     *
     * @param in           Input for console
     * @param out          Output for console
     * @param textToPrint  String to write to console
     * @param defaultValue The default to return if no input
     * @return The response or default
     * @throws IOException
     */
    private String getResponseFromUser(final Scanner in, final Writer out,
                                       final String textToPrint, final String defaultValue) throws
            IOException {
        out.write(textToPrint);
        out.flush();
        String fileName = in.nextLine();
        if (fileName.length() <= 0) {
            fileName = defaultValue;
        }
        return fileName;
    }

    /**
     * Produce csv file of results
     *
     * @param headings      the headers for the excel file
     * @param resultsReader BufferedReader for result file
     * @param resultsWriter BufferedWriter for output file
     */
    private static void produceFile(final String[] headings,
                                    final BufferedReader resultsReader,
                                    final BufferedWriter resultsWriter) {
        String line;
        try {
            writeHeaders(resultsWriter, headings);
            do {
                String previousColumn = "";
                LinkedHashMap<String, StringBuilder> entries = generateNewMap(headings);
                while ((line = resultsReader.readLine()) != null && line.length() >= 3) {
                    previousColumn = addEntry(entries, line, previousColumn);
                }
                writeEntries(entries, resultsWriter);
            } while (resultsReader.readLine() != null);

            resultsReader.close();
            resultsWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write column headers
     *
     * @param resultsWriter BufferedWriter for output file
     * @param headers       the headers for the excel file
     * @throws IOException
     */
    private static void writeHeaders(final BufferedWriter resultsWriter, final String[]
            headers) throws IOException {
        for (final String heading : headers) {
            resultsWriter.write(heading);
            resultsWriter.write(DELIMITER);
        }
        resultsWriter.newLine();
    }

    /**
     * Write out entries with delimiters
     *
     * @param entries        map of values
     * @param bufferedWriter BufferedWriter for output file
     * @throws IOException
     */
    private static void writeEntries(final LinkedHashMap<String, StringBuilder> entries, final
    BufferedWriter bufferedWriter) throws IOException {
        for (String header : entries.keySet()) {
            bufferedWriter.write("\"");
            bufferedWriter.write(entries.get(header).toString());
            bufferedWriter.write("\"");
            bufferedWriter.write(DELIMITER);
        }
        bufferedWriter.newLine();
    }

    /**
     * Add file line to correct entry
     *
     * @param entries        Column values
     * @param line           File line
     * @param previousColumn Column of previous entry
     * @return this column value
     * @throws Exception
     */
    private static String addEntry(final Map<String, StringBuilder> entries, final String line,
                                   final String previousColumn) throws Exception {
        String thisCol = line.substring(0, 2);
        String col = thisCol.equals("  ") ? previousColumn : thisCol;
        if (!entries.containsKey(col)) {
            throw new Exception("The column " + col + " does not exist.");
        }
        StringBuilder b = entries.get(col);
        if (thisCol.equals("  ")) {
            b.append(". ");
        }
        getFormattedString(line, b);
        return col;
    }

    /**
     * Format string by escaping " and then saving to map
     *
     * @param line line from file
     * @param b    StringBuilder for entry
     */
    private static void getFormattedString(final String line, StringBuilder b) {
        String[] split = line.substring(3).split("\"");
        for (int i = 0; i < split.length - 1; i++) {
            b.append(split[i]);
            b.append("\"\"");
        }
        b.append(split[split.length - 1]);
    }

    /**
     * Make hashmap for headers
     *
     * @param headings the given excel headers
     * @return generated map
     */
    private static LinkedHashMap<String, StringBuilder> generateNewMap(final String[] headings) {
        LinkedHashMap<String, StringBuilder> headingMap = new LinkedHashMap<>();
        for (String head : headings) {
            headingMap.put(head, new StringBuilder());
        }
        return headingMap;
    }

}
