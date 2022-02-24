
public class Message {

    private int messageType;
    private String username;
    private String message;
    public Message(){};


    // Setters & Getters
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
