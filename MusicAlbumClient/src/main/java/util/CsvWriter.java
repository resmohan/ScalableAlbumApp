package util;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {

  private final String serverName;
  private static final String FILE_PATH = "output"+File.separator;
  private static final String LATENCY_FILE_SUFFIX = "LatencyInfo.csv";
  private static final String GRAPH_FILE_SUFFIX = "GraphInfo.csv";
  public CsvWriter(String serverName) {
    this.serverName = serverName;
  }
  public void writeLatencyDetails(List<ResponseLatency> responseLatencyList){
    try {
      FileWriter outputFile = new FileWriter(FILE_PATH + serverName + LATENCY_FILE_SUFFIX);
      CSVWriter csvWriter = new CSVWriter(outputFile);
      csvWriter.writeNext(new String[]{"Start Time","Request Type"," Latency","Response Code"});
      for(ResponseLatency responseLatency: responseLatencyList){
        csvWriter.writeNext(new String[]{responseLatency.toString()});
      }
      csvWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public void writeGraphData(List<Integer> requestCountList){
    try {
      FileWriter outputFile = new FileWriter(FILE_PATH + serverName + GRAPH_FILE_SUFFIX);
      CSVWriter csvWriter = new CSVWriter(outputFile);
      csvWriter.writeNext(new String[]{"Time in seconds","Number of requests processed"});
      for(int i=0;i<requestCountList.size();i++){
        csvWriter.writeNext(new String[]{String.valueOf(i+1), String.valueOf(requestCountList.get(i))});
      }
      csvWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
