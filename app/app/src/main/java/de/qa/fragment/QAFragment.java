package de.qa.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.qa.R;
import de.qa.misc.Utils;
import de.qa.qa.OfflineQuestionAnswerer;
import de.qa.qa.QuestionAnswerer;
import de.qa.qa.WdaquaQuestionAnswerer;
import de.qa.qa.result.TextResult;
import de.qa.qa.result.FooterResult;
import de.qa.qa.result.HeaderResult;
import de.qa.qa.result.QAResult;
import de.qa.qa.result.UriResult;
import de.qa.view.adapter.QAAdapter;

public class QAFragment extends Fragment implements View.OnClickListener,
        QAAdapter.OnItemClickListener {
    private static final String TAG = QAFragment.class.getSimpleName();

    private View mRootView;
    private ImageView mQaButton;
    private EditText mQuestionInput;
    private RecyclerView mResultsRecycler;
    private ArrayList<QAResult> mAnswers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_qa, container, false);
        mQaButton = mRootView.findViewById(R.id.qa_btn);
        mQuestionInput = mRootView.findViewById(R.id.question_input);
        mQaButton.setOnClickListener(this);
        mResultsRecycler = mRootView.findViewById(R.id.results_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        mResultsRecycler.setLayoutManager(llm);
        QAAdapter adapter = new QAAdapter(mAnswers);
        adapter.setOnItemClickListener(this);
        mResultsRecycler.setAdapter(adapter);
        return mRootView;
    }

    @Override
    public void onClick(View view) {
        if (view == mQaButton) {
            String question = mQuestionInput.getText().toString();
            AnswerQuestionTask task = new AnswerQuestionTask();
            task.fragment = new WeakReference<>(this);
            task.execute(question);
            mQuestionInput.setText("");
            mResultsRecycler.smoothScrollToPosition(0);
            view.setVisibility(View.INVISIBLE);
            mRootView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        if (mAnswers.get(position) instanceof UriResult) {
            ViewCompat.setTransitionName(view.findViewById(R.id.item_qa_text), "title");
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(view.findViewById(R.id.item_qa_text), "title")
                    .addToBackStack(null)
                    .replace(android.R.id.content,
                            UriDetailFragment.newInstance(getContext(),
                                    (UriResult) mAnswers.get(position)))
                    .commit();
        }
    }

    /**
     * Display a question with all its answers on the screen.
     *
     * @param results the results to display
     */
    private void publishAnswers(QAResult[] results) {
        for (QAResult result : results) {
            mAnswers.add(0, result);
            mResultsRecycler.getAdapter().notifyItemInserted(1);
        }
        mResultsRecycler.smoothScrollToPosition(0);
        mQaButton.setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
    }

    private static class AnswerQuestionTask extends AsyncTask<String, Object, QAResult[]> {

        WeakReference<QAFragment> fragment;

        @Override
        protected QAResult[] doInBackground(String... strings) {
            String question = strings[0];

            Context context = null;
            if (fragment.get() != null) context = fragment.get().getActivity();
            if (context == null) return new QAResult[]{
                    new HeaderResult(question),
                    new TextResult(question, "An error occurred"),
                    new FooterResult(question)
            };
            QuestionAnswerer answerer;
            if (Utils.isOffline(context)) {
                answerer = new OfflineQuestionAnswerer(context);
            } else {
                answerer = new WdaquaQuestionAnswerer();
            }
            return answerer.answerQuestion(question);
        }

        @Override
        protected void onPostExecute(QAResult[] results) {
            super.onPostExecute(results);
            if (fragment != null) {
                fragment.get().publishAnswers(results);
            }
        }
    }


}
