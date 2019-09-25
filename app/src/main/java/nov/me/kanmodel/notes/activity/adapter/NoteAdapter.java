package nov.me.kanmodel.notes.activity.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.activity.MainActivity;
import nov.me.kanmodel.notes.model.Note;
import nov.me.kanmodel.notes.utils.DBAid;
import nov.me.kanmodel.notes.utils.TimeAid;

/**
 * 重写RecyclerView类
 * Created by KanModel on 2017/11/26.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static int titleFontSize;
    private static int timeFontSize;

    public static int getTitleFontSize() {
        return titleFontSize;
    }

    public static int getTimeFontSize() {
        return timeFontSize;
    }

    public static int getContentFontSize() {
        return contentFontSize;
    }

    private static int contentFontSize;

    private static final String TAG = "ViewHolder";

    private static List<Note> notes = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {

        View noteView;

        EditText titleET;
        EditText contentET;
        TextView timeTV;
        TextView targetTV;
        TextView dstTV;

        ViewHolder(View itemView) {
            super(itemView);
            noteView = itemView;
            titleET = itemView.findViewById(R.id.r_title);
            contentET = itemView.findViewById(R.id.r_content);
            timeTV = itemView.findViewById(R.id.r_time);
            targetTV = itemView.findViewById(R.id.r_target_time);
            dstTV = itemView.findViewById(R.id.r_dis);
        }

    }

    public NoteAdapter(List<Note> noteList) {
        notes = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Note note = notes.get(position);
        //标题初始化
        if (note.getTitle().equals("")) {
            holder.titleET.setVisibility(View.GONE);
        } else {
            holder.titleET.setText(note.getTitle());
            holder.titleET.setVisibility(View.VISIBLE);
        }
        //内容初始化
        holder.titleET.setTextSize(titleFontSize);
        if (note.getContent().equals("")) {
            holder.contentET.setVisibility(View.GONE);
        } else {
            holder.contentET.setText(note.getContent());
            holder.contentET.setVisibility(View.VISIBLE);
        }
        holder.contentET.setTextSize(contentFontSize);
        //时间初始化
        TextView timeTV = holder.timeTV;
        timeTV.setTextSize(timeFontSize);
        long time = note.getTime(), lastChangedTime = note.getLastChangedTime();
        if (time == lastChangedTime) {
            timeTV.setText(TimeAid.INSTANCE.stampToDate(time));
        } else {
            timeTV.setText(String.format("%s - 修改于%s", TimeAid.INSTANCE.stampToDate(time), TimeAid.INSTANCE.stampToDate(lastChangedTime)));
        }
        //目标时间及倒计时初始化
        TextView dstTV = holder.dstTV, tartTV = holder.targetTV;
        long dstTime = DBAid.querySQLNotice(MainActivity.getDbHelper(), time);
        Log.d(TAG, "onBindViewHolder: dstTime:" + dstTime + " ,diff :" + (dstTime - TimeAid.INSTANCE.getNowTime()));
        if (dstTime > 0 && (dstTime - TimeAid.INSTANCE.getNowTime()) > 0) {
            dstTV.setVisibility(View.VISIBLE);
            long day = TimeAid.INSTANCE.getDiffDay(dstTime);
            long hour = TimeAid.INSTANCE.getDiffHour(dstTime);
            long minute = TimeAid.INSTANCE.getDiffMinutes(dstTime);
            if (day > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + day + " 天 " + hour + " 小时 " + minute + " 分钟");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                int lengthOfDay = String.valueOf(day).length();
                spannableString.setSpan(sizeSpan, 3, 3 + lengthOfDay, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, 3 + lengthOfDay, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                dstTV.setText(spannableString);
            } else if (hour > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + hour + " 小时 " + minute + " 分钟");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                int lengthOfHour = String.valueOf(hour).length();
                spannableString.setSpan(sizeSpan, 3, 3 + lengthOfHour, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, 3 + lengthOfHour, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                dstTV.setText(spannableString);
            } else if (minute > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + minute + " 分钟");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                spannableString.setSpan(sizeSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                dstTV.setText(spannableString);
            } else {
                dstTV.setVisibility(View.GONE);
            }
            tartTV.setVisibility(View.VISIBLE);
            tartTV.setText(String.format("距离 %s 还", TimeAid.INSTANCE.stampToDate(dstTime)));
        } else {
            dstTV.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * @param note     新Note
     * @param position 添加位置
     */
    public void addData(Note note, int position) {
        notes.add(position, note);
        notifyItemInserted(position);
    }

    /**
     * 添加新便签到第一个位置
     *
     * @param note 新Note
     */
    public void addData(Note note) {
        addData(note, 0);
    }

    public void removeData(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notes.size());
    }

    public void refreshData(int position) {
        notifyItemRangeChanged(position, notes.size());
    }

    /**
     * 刷新RecyclerView
     */
    public void refreshAllData() {
        refreshAllData(notes.size());
    }

    /**
     * 刷新RecyclerView
     */
    public void refreshAllDataForce() {
        notes = DBAid.findAllNote(DBAid.getDbHelper(MainActivity.getContext()));
        refreshAllData();
    }

    /**
     * 刷新RecyclerView
     *
     * @param size Note集合长度
     */
    public void refreshAllData(int size) {
        notifyItemRangeChanged(0, size);
    }

    /**
     * @return Note集合
     */
    public static List<Note> getNotes() {
        return notes;
    }

    public static void setTitleFontSize(int titleFontSize) {
        NoteAdapter.titleFontSize = titleFontSize;
//        MainActivity.getNoteAdapter().refreshAllData();
    }

    public static void setTimeFontSize(int timeFontSize) {
        NoteAdapter.timeFontSize = timeFontSize;
//        MainActivity.getNoteAdapter().refreshAllData();
    }

    public static void setContentFontSize(int contentFontSize) {
        NoteAdapter.contentFontSize = contentFontSize;
//        MainActivity.getNoteAdapter().refreshAllData();
    }
}
