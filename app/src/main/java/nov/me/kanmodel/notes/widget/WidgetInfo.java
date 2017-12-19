package nov.me.kanmodel.notes.widget;

import nov.me.kanmodel.notes.Note;

/**
 * WidgetInfo记录widget信息
 * Created by KanModel on 2017/12/2.
 */

public class WidgetInfo {
    private long time;
    private int appWidgetID;
    private Note note;

    WidgetInfo(long time, int appWidgetID) {
        this(time, appWidgetID, null);
    }

    public WidgetInfo(long time, int appWidgetID, Note note) {
        this.time = time;
        this.appWidgetID = appWidgetID;
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAppWidgetID() {
        return appWidgetID;
    }

    public void setAppWidgetID(int appWidgetID) {
        this.appWidgetID = appWidgetID;
    }
}
