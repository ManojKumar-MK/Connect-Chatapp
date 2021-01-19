package developermk.chatapp.model;

public class Chatlist {
    private String userID;
    private String userName;
    private String description;
    private String date;
    private String urlProfile;
    private String phone;
    private String lastMsg;

    public Chatlist() {
    }

    public Chatlist(String userID, String userName, String description, String date, String urlProfile,String phone,String lastMsg) {
        this.userID = userID;
        this.userName = userName;
        this.description = description;
        this.date = date;
        this.urlProfile = urlProfile;
        this.phone = phone;
        this.lastMsg=lastMsg;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
