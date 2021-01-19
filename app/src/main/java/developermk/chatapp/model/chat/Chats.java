package developermk.chatapp.model.chat;

public class Chats {
    private String dateTime;
    private String textMessage;
    private String url;
    private String type;
    private String sender;
    private String receiver;
    private String lastMessage;
    private boolean isseen;

    public Chats() {
    }

    public Chats(String dateTime, String textMessage, String url,String type, String sender, String receiver,String lastMessage,boolean isseen) {
        this.dateTime = dateTime;
        this.textMessage = textMessage;
        this.url = url;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.lastMessage = lastMessage;
        this.isseen = isseen;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
