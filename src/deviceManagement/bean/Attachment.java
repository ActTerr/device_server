package deviceManagement.bean;

import java.util.Date;

public class Attachment {
    long Nid;
    long Aid;
    Date date;
    String name;

    public Attachment(long nid, long aid, Date date, String name) {
        Nid = nid;
        Aid = aid;
        this.date = date;
        this.name = name;
    }

    public Attachment() {
    }

    public long getNid() {
        return Nid;
    }

    public void setNid(long nid) {
        Nid = nid;
    }

    public long getAid() {
        return Aid;
    }

    public void setAid(long aid) {
        Aid = aid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}