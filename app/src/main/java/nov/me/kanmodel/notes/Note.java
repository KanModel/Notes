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

    /**
     * @param title 便签标题
     * @param content 便签内容
     * @param logTime  便签时间
     * @param time 时间戳
     */
    Note(String title, String content, String logTime, long time){
        this.title = title;
        this.content = content;
        this.logTime = logTime;
        this.time = time;
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
}
