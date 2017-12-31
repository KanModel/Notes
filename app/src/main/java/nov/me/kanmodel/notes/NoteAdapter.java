package nov.me.kanmodel.notes;

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

import nov.me.kanmodel.notes.utils.TimeAid;
import nov.me.kanmodel.notes.utils.dbAid;
import nov.me.kanmodel.notes.widget.NoteAppWidget;

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
        TextView dstTV;

        ViewHolder(View itemView) {
            super(itemView);
            noteView = itemView;
            titleET = itemView.findViewById(R.id.r_title);
            contentET = itemView.findViewById(R.id.r_content);
            timeTV = itemView.findViewById(R.id.r_time);
            dstTV = itemView.findViewById(R.id.r_dis);
        }

    }

    NoteAdapter(List<Note> noteList) {
        notes = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Note note = notes.get(position);
        if (note.getTitle().equals("")) {
            holder.titleET.setVisibility(View.GONE);
        } else {
            holder.titleET.setText(note.getTitle());
            holder.titleET.setVisibility(View.VISIBLE);
        }
        holder.titleET.setTextSize(titleFontSize);
        if (note.getContent().equals("")) {
            holder.contentET.setVisibility(View.GONE);
        } else {
            holder.contentET.setText(note.getContent());
            holder.contentET.setVisibility(View.VISIBLE);
        }
        holder.contentET.setTextSize(contentFontSize);
        TextView timeTV = holder.timeTV;
        timeTV.setTextSize(timeFontSize);
        long time = note.getTime(), lastChangedTime = note.getLastChangedTime();
        if (time == lastChangedTime) {
            timeTV.setText(TimeAid.stampToDate(time));
        } else {
            timeTV.setText(TimeAid.stampToDate(time) + " - 最后更改于" + TimeAid.stampToDate(lastChangedTime));
        }
        TextView dstTV = holder.dstTV;
        long dstTime = dbAid.querySQLNotice(MainActivity.getDbHelper(), time);
        Log.d(TAG, "onBindViewHolder: dstTime:" + dstTime + " ,diff :" + (dstTime - TimeAid.getNowTime()));
        if (dstTime > 0 && (dstTime - TimeAid.getNowTime()) > 0) {
            dstTV.setVisibility(View.VISIBLE);
//            dstTV.setText();
            long day = TimeAid.getDiffDay(dstTime);
            long hour = TimeAid.getDiffHour(dstTime);
            long minute = TimeAid.getDiffMinutes(dstTime);
            if (day > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + day + " 天");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                spannableString.setSpan(sizeSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                dstTV.setText(spannableString);
            } else if (hour > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + hour + " 小时");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                spannableString.setSpan(sizeSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
        } else {
            dstTV.setVisibility(View.GONE);
        }
//        holder.noteView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = holder.getAdapterPosition();
////                Note note = notes.get(position);
////                Log.d(TAG, "onClick: Content:" + note.getContent() + "\nTitle:" +
////                        note.getTitle() + "\nTime:" + note.getLogTime() + "\nPos:" + position);
////                Intent intent = new Intent("nov.me.kanmodel.notes.EditActivity");
////                intent.putExtra("pos", position);
////                intent.putExtra("title", note.getTitle());
////                intent.putExtra("content", note.getContent());
////                intent.putExtra("time", dbAid.stampToDate(note.getTime()));
////                intent.putExtra("timeLong", note.getTime());
////                view.getContext().startActivity(intent);
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * @param note     新Note
     * @param position 添加位置
     */
    void addData(Note note, int position) {
        notes.add(position, note);
        notifyItemInserted(position);
    }

    /**
     * 添加新便签到第一个位置
     *
     * @param note 新Note
     */
    void addData(Note note) {
        addData(note, 0);
    }

    void removeData(int position) {
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
    void refreshAllData() {
        refreshAllData(notes.size());
    }

    /**
     * 刷新RecyclerView
     */
    void refreshAllDataForce() {
        notes = dbAid.initNotes(dbAid.getDbHelper(MainActivity.getContext()));
        refreshAllData();
    }

    /**
     * 刷新RecyclerView
     *
     * @param size Note集合长度
     */
    void refreshAllData(int size) {
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
