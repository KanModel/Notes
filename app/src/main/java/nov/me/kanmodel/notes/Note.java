package nov.me.kanmodel.notes;

/**
 * Created by kgdwhsk on 2017/11/26.
 */

public class Note {
    private String title;
    private String content;
    private String logTime;
    private long time;

    public Note(String title, String content, String logTime){
        this(title, content, logTime, 0);
    }

    public Note(String title, String content, String logTime, long time){
        this.title = title;
        this.content = content;
        this.logTime = logTime;
        this.time = time;
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
}
