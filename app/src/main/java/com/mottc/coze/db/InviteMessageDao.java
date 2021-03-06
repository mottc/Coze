package com.mottc.coze.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.mottc.coze.bean.InviteMessage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "INVITE_MESSAGE".
*/
public class InviteMessageDao extends AbstractDao<InviteMessage, Long> {

    public static final String TABLENAME = "INVITE_MESSAGE";

    /**
     * Properties of entity InviteMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, String.class, "time", false, "TIME");
        public final static Property From = new Property(2, String.class, "from", false, "FROM");
        public final static Property Reason = new Property(3, String.class, "reason", false, "REASON");
        public final static Property GroupName = new Property(4, String.class, "groupName", false, "GROUP_NAME");
        public final static Property GroupId = new Property(5, String.class, "groupId", false, "GROUP_ID");
        public final static Property Type = new Property(6, int.class, "type", false, "TYPE");
        public final static Property Status = new Property(7, String.class, "status", false, "STATUS");
    }


    public InviteMessageDao(DaoConfig config) {
        super(config);
    }
    
    public InviteMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"INVITE_MESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME\" TEXT UNIQUE ," + // 1: time
                "\"FROM\" TEXT," + // 2: from
                "\"REASON\" TEXT," + // 3: reason
                "\"GROUP_NAME\" TEXT," + // 4: groupName
                "\"GROUP_ID\" TEXT," + // 5: groupId
                "\"TYPE\" INTEGER NOT NULL ," + // 6: type
                "\"STATUS\" TEXT);"); // 7: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"INVITE_MESSAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, InviteMessage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(2, time);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(3, from);
        }
 
        String reason = entity.getReason();
        if (reason != null) {
            stmt.bindString(4, reason);
        }
 
        String groupName = entity.getGroupName();
        if (groupName != null) {
            stmt.bindString(5, groupName);
        }
 
        String groupId = entity.getGroupId();
        if (groupId != null) {
            stmt.bindString(6, groupId);
        }
        stmt.bindLong(7, entity.getType());
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(8, status);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, InviteMessage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(2, time);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(3, from);
        }
 
        String reason = entity.getReason();
        if (reason != null) {
            stmt.bindString(4, reason);
        }
 
        String groupName = entity.getGroupName();
        if (groupName != null) {
            stmt.bindString(5, groupName);
        }
 
        String groupId = entity.getGroupId();
        if (groupId != null) {
            stmt.bindString(6, groupId);
        }
        stmt.bindLong(7, entity.getType());
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(8, status);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public InviteMessage readEntity(Cursor cursor, int offset) {
        InviteMessage entity = new InviteMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // time
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // from
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // reason
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // groupName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // groupId
            cursor.getInt(offset + 6), // type
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // status
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, InviteMessage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFrom(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setReason(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setGroupName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setGroupId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setType(cursor.getInt(offset + 6));
        entity.setStatus(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(InviteMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(InviteMessage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(InviteMessage entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
