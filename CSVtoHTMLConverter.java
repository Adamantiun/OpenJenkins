import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CSVtoHTMLConverter {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the path to the CSV file as a parameter.");
            return;
        }

        String csvFile = args[0];
        String htmlFile = "report.html";
        String columnNames = "Timestamp,Elapsed Time,Label,Response Code,Response Message,Thread Name,Data Type,Success,Error Message,Bytes Received,Bytes Sent,Number of Groups,Number of Assertions,URL,Latency Time,Idle Time,Connect Time";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            FileWriter fw = new FileWriter(htmlFile);

            int totalTests = 0;
            int successfulTests = 0;
            long receivedBytesSum = 0;
            long sentBytesSum = 0;
            long idleTimeSum = 0;
            long connectTimeSum = 0;
            long latencyTimeSum = 0;

            // Calculate success rate and sum for specific columns
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split(",");
                if (columns.length > 7) {
                    totalTests++;
                    String success = columns[7].trim();
                    if (success.equalsIgnoreCase("true")) {
                        successfulTests++;
                    }
                    receivedBytesSum += Long.parseLong(columns[9].trim());
                    sentBytesSum += Long.parseLong(columns[10].trim());
                    idleTimeSum += Long.parseLong(columns[16].trim());
                    connectTimeSum += Long.parseLong(columns[15].trim());
                    latencyTimeSum += Long.parseLong(columns[14].trim());
                }
            }

            // Reset the buffered reader
            br.close();
            br = new BufferedReader(new FileReader(csvFile));

            // Generate HTML file
            fw.write("<html>\n<head>\n<title>CSV to HTML</title>\n<style>\ntable {\nborder-collapse: collapse;\nwidth: 100%;\n}\nth, td {\nborder: 1px solid black;\npadding: 8px;\ntext-align: left;\n}\n</style>\n</head>\n<body>\n");

            // Write the success rate in the header
            double successRate = Math.round((double) successfulTests / totalTests * 100);
            String headerStyle = "background-color: #f2f2f2; padding: 10px; text-align: center; font-size: 18px; height: 40px; line-height: 40px;";
            if (successRate > (double) 90){
                fw.write("<h2 style=\"" + headerStyle + " background-color: green; color: white;\">Success Rate: " + successRate + "%</h2>\n");
            } else if (successRate >= (double) 50){
                fw.write("<h2 style=\"" + headerStyle + " background-color: yellow; color: black;\">Success Rate: " + successRate + "%</h2>\n");
            } else
                fw.write("<h2 style=\"" + headerStyle + " background-color: red; color: white;\">Success Rate: " + successRate + "%</h2>\n");

            fw.write("<table>\n");

            // Write the header row
            fw.write("<tr>\n");
            String[] headers = columnNames.split(",");
            for (String header : headers) {
                fw.write("<th>" + header.trim() + "</th>\n");
            }
            fw.write("</tr>\n");

            // Read the remaining lines
            isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split(",");
                fw.write("<tr>\n");
                for (String column : columns) {
                    fw.write("<td>" + column.trim() + "</td>\n");
                }
                fw.write("</tr>\n");
            }

            // Calculate averages (rounded to the closest integer)
            long receivedBytesAvg = Math.round((double) receivedBytesSum / totalTests);
            long sentBytesAvg = Math.round((double) sentBytesSum / totalTests);
            long idleTimeAvg = Math.round((double) idleTimeSum / totalTests);
            long connectTimeAvg = Math.round((double) connectTimeSum / totalTests);
            long latencyTimeAvg = Math.round((double) latencyTimeSum / totalTests);

            // Write the average row
            fw.write("<tr>\n");
            for (int i = 0; i < headers.length; i++) {
                if (i == 0){
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">Average</td>\n");
                } else if (i == 9) {
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">" + receivedBytesAvg + "</td>\n");
                } else if (i == 10) {
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">" + sentBytesAvg + "</td>\n");
                } else if (i == 16) {
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">" + idleTimeAvg + "</td>\n");
                } else if (i == 15) {
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">" + connectTimeAvg + "</td>\n");
                } else if (i == 14) {
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">" + latencyTimeAvg + "</td>\n");
                } else {
                    fw.write("<td style=\"border: 1px solid black; padding: 8px; text-align: left; font-weight: bold;\">&nbsp;</td>\n");
                }
            }
            fw.write("</tr>\n");

            fw.write("</table>\n</body>\n</html>");

            br.close();
            fw.close();

            System.out.println("HTML file generated successfully: " + htmlFile);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
