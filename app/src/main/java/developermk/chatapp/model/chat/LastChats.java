package developermk.chatapp.model.chat;

public class LastChats {
    private String dateTime;
    private String textMessage;
    private String receiver;
    private String sender;
    private String lastMessage;
    private String type;
    private String url;
    private boolean isseen;

    public LastChats() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public LastChats(String dateTime, String textMessage, String receiver, String sender, String lastMessage, String type, String url, boolean isseen) {
        this.dateTime = dateTime;
        this.textMessage = textMessage;
        this.receiver = receiver;
        this.sender = sender;
        this.lastMessage = lastMessage;
        this.type = type;
        this.url = url;
        this.isseen=isseen;
    }
}