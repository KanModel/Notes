package nov.me.kanmodel.notes;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by kgdwhsk on 2017/11/26.
 */
//todo 删除功能
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = "ViewHolder";

    private static List<Note> notes;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View noteView;
        EditText titleET;
        EditText contentET;
        TextView timeTV;

        public ViewHolder(View itemView) {
            super(itemView);
            noteView = itemView;
            titleET = itemView.findViewById(R.id.r_title);
            contentET = itemView.findViewById(R.id.r_content);
            timeTV = itemView.findViewById(R.id.r_time);
        }
    }

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Note note = notes.get(position);
//                Toast.makeText(view.getContext(), "Content:" + note.getContent() + "\nTitle:" +
//                        note.getTitle() + "\nTime:" + note.getLogTime(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: Content:" + note.getContent() + "\nTitle:" +
                        note.getTitle() + "\nTime:" + note.getLogTime() + "\nPos:" + position);
                Intent intent = new Intent("nov.me.kanmodel.notes.EditActivity");
                intent.putExtra("pos", position);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("content", note.getContent());
                intent.putExtra("time", Aid.stampToDate(note.getTime()));
                intent.putExtra("timeLong", note.getTime());
//                startActivity(intent);
                view.getContext().startActivity(intent);
            }
        });
//        holder.timeTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "timeTV", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onClick: time");
//            }
//        });
//        holder.contentET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "contentET", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onClick: content");
//            }
//        });
//        holder.titleET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "titleET", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onClick: titleET");
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.titleET.setText(note.getTitle());
        holder.contentET.setText(note.getContent());
//        holder.timeTV.setText(note.getLogTime());
        holder.timeTV.setText(Aid.stampToDate(String.valueOf(note.getTime())));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    void addData(Note note, int position) {
        notes.add(position, note);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, notes.size());
    }

    void addData(Note note) {
        addData(note, 0);
    }

    void removeData(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notes.size());
    }

    void refreshData(int position) {
        notifyItemRangeChanged(position, notes.size());
    }

    void refreshAllData() {
        refreshAllData(notes.size());
    }

    void refreshAllData(int size) {
        notifyItemRangeChanged(0, size);
    }

    public static List<Note> getNotes() {
        return notes;
    }
}
