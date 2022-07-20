package cl.architeq.acc.util;

public class MessageDetails {

    private String dateTime;
    private String message;
    private String details;

    public MessageDetails() {
        //
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "MessageDetails{" +
                "dateTime='" + dateTime + '\'' +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }


}
