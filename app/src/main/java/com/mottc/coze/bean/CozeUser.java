package com.mottc.coze.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 15:25
 */

@Entity
public class CozeUser {
    //不能用int
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String userName;
    private String nickName;
    private String avatar;
    @Generated(hash = 1481640100)
    public CozeUser(Long id, String userName, String nickName, String avatar) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.avatar = avatar;
    }
    @Generated(hash = 769123555)
    public CozeUser() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getNickName() {
        return this.nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


}
