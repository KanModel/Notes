package nov.me.kanmodel.notes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        TextView disTV;
        ViewHolder(View itemView) {
            super(itemView);
            noteView = itemView;
            titleET = itemView.findViewById(R.id.r_title);
            contentET = itemView.findViewById(R.id.r_content);
            timeTV = itemView.findViewById(R.id.r_time);
            disTV = itemView.findViewById(R.id.r_dis);
        }

    }
    NoteAdapter(List<Note> notes) {
        this.notes = notes;
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
        holder.titleET.setText(note.getTitle());
        holder.contentET.setText(note.getContent());
        holder.titleET.setTextSize(titleFontSize);
        holder.contentET.setTextSize(contentFontSize);
        TextView timeTV = holder.timeTV;
        timeTV.setTextSize(timeFontSize);
        long time = note.getTime(), lastChangedTime = note.getLastChangedTime();
        if (time == lastChangedTime) {
            timeTV.setText(TimeAid.stampToDate(time));
        } else {
            timeTV.setText(TimeAid.stampToDate(time) + " - 最后更改于" + TimeAid.stampToDate(lastChangedTime));
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
     * @param note 新Note
     * @param position 添加位置
     */
    void addData(Note note, int position) {
        notes.add(position, note);
        notifyItemInserted(position);
    }

    /**
     * 添加新便签到第一个位置
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
    }

    public static void setTimeFontSize(int timeFontSize) {
        NoteAdapter.timeFontSize = timeFontSize;
    }

    public static void setContentFontSize(int contentFontSize) {
        NoteAdapter.contentFontSize = contentFontSize;
    }
}
