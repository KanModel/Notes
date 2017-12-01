package nov.me.kanmodel.notes;

/**
 * WidgetInfo记录widget信息
 * Created by KanModel on 2017/12/2.
 */

public class WidgetInfo {
    private long time;
    private int appWidgetID;

    WidgetInfo(long time, int appWidgetID) {
        this.time = time;
        this.appWidgetID = appWidgetID;
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
