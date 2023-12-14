package util;

public class ResponseLatency {

  private long startTime;
  private RequestType requestType;
  private long latency;
  private int responseCode;

  public ResponseLatency(long startTime, RequestType requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public long getStartTime() {
    return startTime;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public long getLatency() {
    return latency;
  }

  public int getResponseCode() {
    return responseCode;
  }

  @Override
  public String toString() {
    return startTime + "," + requestType.toString() + "," + latency + "," + responseCode;
  }
}
