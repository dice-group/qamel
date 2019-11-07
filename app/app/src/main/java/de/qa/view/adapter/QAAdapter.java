package de.qa.view.adapter;

import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import de.qa.R;
import de.qa.qa.result.FooterResult;
import de.qa.qa.result.HeaderResult;
import de.qa.qa.result.QAResult;
import de.qa.qa.result.UriResult;

public class QAAdapter extends RecyclerView.Adapter<QAAdapter.QaViewHolder> implements View.OnClickListener {

    private ArrayList<QAResult> mDataset;

    private OnItemClickListener mListener;
    public QAAdapter(ArrayList<QAResult> dataset) {
        mDataset = dataset;
    }

    @Override
    public QaViewHolder onCreateViewHolder(ViewGroup parent, int layoutRes) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent,
                false);
        return new QaViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position) instanceof HeaderResult) return R.layout.item_qa_header;
        if (mDataset.get(position) instanceof FooterResult) return R.layout.item_qa_footer;
        return R.layout.item_qa_body;
    }
    @Override
    public void onBindViewHolder(QaViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).toString());
        holder.cardView.setTag(position);
        if (mDataset.get(position) instanceof UriResult) {
            holder.cardView.setClickable(true);
            holder.cardView.setOnClickListener(this);
        } else {
            holder.cardView.setClickable(false);
            holder.cardView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) mListener.onItemClick((Integer) view.getTag(), view);
        }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class QaViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView pictureView, audioOutput;
        CardView cardView;

        QaViewHolder(final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_qa_text);
            pictureView = itemView.findViewById(R.id.item_qa_picture);
            cardView = itemView.findViewById(R.id.card_view);
            audioOutput = itemView.findViewById(R.id.audio_output);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
}
