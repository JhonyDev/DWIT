package com.app.dwit.models;


public class FriendlyMessage extends Super {

    private String messageId;
    private String text;
    private String fromUser;
    private String fromUserName;
    private String photoUrl;
    private String toUser;
    private String conversationId;
    private String fromUserProfilePic;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String messageId, String text, String fromUser, String fromUserName,
                           String photoUrl, String toUser,
                           String fromUserProfilePic, String conversationId) {
        this.messageId = messageId;
        this.text = text;
        this.fromUser = fromUser;
        this.fromUserName = fromUserName;
        this.photoUrl = photoUrl;
        this.toUser = toUser;
        this.conversationId = conversationId;
        this.fromUserProfilePic = fromUserProfilePic;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFromUserProfilePic() {
        return fromUserProfilePic;
    }

    public void setFromUserProfilePic(String fromUserProfilePic) {
        this.fromUserProfilePic = fromUserProfilePic;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
