package com.mottc.coze.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/10
 * Time: 19:16
 */
@Entity
public class InviteMessage {
    @Id(autoincrement = true)
    private long id;
    private String from;
    private String reason;
    private String groupName;
    private String status;
    private int type;
    @Generated(hash = 179400718)
    public InviteMessage(long id, String from, String reason, String groupName,
            String status, int type) {
        this.id = id;
        this.from = from;
        this.reason = reason;
        this.groupName = groupName;
        this.status = status;
        this.type = type;
    }
    @Generated(hash = 1613074736)
    public InviteMessage() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getFrom() {
        return this.from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getGroupName() {
        return this.groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    
}
