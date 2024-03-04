import java.io.Serializable;

public class Response implements Serializable {
  private String status;
  private String message;


  public Response(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }


  @Override
  public String toString() {
    return "Response{" + "status='" + status + '\'' + ", message='" + message + '\'' + '}';
  }
}

