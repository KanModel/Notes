package nov.me.kanmodel.notes;

/**
 * 存储每个便签内容的数据类
 * Created by KanModel on 2017/11/26.
 */

public class Note {
    private String title;
    private String content;
    private String logTime;
    private long time;

    private long lastChangedTime;

    /**
     * @param title 便签标题
     * @param content 便签内容
     * @param logTime  便签时间
     * @param time 时间戳
     */
    Note(String title, String content, String logTime, long time, long lastChangedTime){
        this.title = title;
        this.content = content;
        this.logTime = logTime;
        this.time = time;
        this.lastChangedTime = lastChangedTime;
    }

    public String getTitle() {
        return title;
    }

    String getContent() {
        return content;
    }

    String getLogTime() {
        return logTime;
    }

    public long getTime() {
        return time;
    }

    void setContent(String content) {
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
}
