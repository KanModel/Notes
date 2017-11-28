package nov.me.kanmodel.notes;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> notes;

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

    public NoteAdapter(List<Note> notes){
        this.notes = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //todo 编辑功能
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Note note = notes.get(position);
                Toast.makeText(view.getContext(), "Content:" + note.getContent() + "\nTitle:" +
                        note.getTitle() + "\nTime:" + note.getLogTime(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("nov.me.kanmodel.notes.EditActivity");
                intent.putExtra("title", note.getTitle());
                intent.putExtra("content", note.getContent());
                intent.putExtra("time", Aid.stampToDate(note.getTime()));
//                startActivity(intent);
                view.getContext().startActivity(intent);
            }
        });
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

    public void addData(Note note, int position){
        notes.add(position, note);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, notes.size());
    }

    public void removeData(int position){
        notes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notes.size());
    }
}
