package nov.me.kanmodel.notes.model;

/**
 * 存储每个便签内容的数据类
 * Created by KanModel on 2017/11/26.
 */

public class Note {
    private String title;
    private String content;
    private String logTime;
    private long time;
    private long dstTime;

    private long lastChangedTime;

    /**
     * @param title   便签标题
     * @param content 便签内容
     * @param logTime 便签时间
     * @param time    时间戳
     */
    public Note(String title, String content, String logTime, long time, long lastChangedTime) {
        this.title = title;
        this.content = content;
        this.logTime = logTime;
        this.time = time;
        this.lastChangedTime = lastChangedTime;
    }

    public Note(String title, String content, String logTime, long time, long lastChangedTime, long dstTime) {
        this(title, content, logTime, time, lastChangedTime);
        this.dstTime = dstTime;
    }

    public Note(String title, String content, long time) {
        this(title, content, "", time, 0);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLogTime() {
        return logTime;
    }

    public long getTime() {
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastChangedTime() {
        return lastChangedTime;
    }

    public void setLastChangedTime(long lastChangedTime) {
        this.lastChangedTime = lastChangedTime;
    }

    public long getDstTime() {
        return dstTime;
    }
}
