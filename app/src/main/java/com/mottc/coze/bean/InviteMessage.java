package com.mottc.coze.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/17
 * Time: 16:10
 */
@Entity
public class InviteMessage {
    //不能用int
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String time;
    private String from;
    private String reason;
    private String groupName;
    private int type;
    private String status;
    @Generated(hash = 1509908040)
    public InviteMessage(Long id, String time, String from, String reason,
            String groupName, int type, String status) {
        this.id = id;
        this.time = time;
        this.from = from;
        this.reason = reason;
        this.groupName = groupName;
        this.type = type;
        this.status = status;
    }
    @Generated(hash = 1613074736)
    public InviteMessage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
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
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
