
package de.qa.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.qa.R;
import de.qa.misc.Utils;
import de.qa.qa.OfflineQuestionAnswerer;
import de.qa.qa.QuestionAnswerer;
import de.qa.qa.WdaquaQuestionAnswerer;
import de.qa.qa.result.FooterResult;
import de.qa.qa.result.HeaderResult;
import de.qa.qa.result.QAResult;
import de.qa.qa.result.TextResult;
import de.qa.qa.result.UriResult;
import de.qa.view.adapter.QAAdapter;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class QAFragment extends Fragment implements OnClickListener,
        QAAdapter.OnItemClickListener, AdapterView.OnItemClickListener{
    private static final String TAG = QAFragment.class.getSimpleName();

    private View mRootView;
    private ImageView mQaButton;
    private EditText mQuestionInput;
    private RecyclerView mResultsRecycler;
    private ArrayList<QAResult> mAnswers = new ArrayList<>();

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private ListView mlist;
    private ImageButton mbtSpeak;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_qa, container, false);
        mQaButton = mRootView.findViewById(R.id.qa_btn);
        mQuestionInput = mRootView.findViewById(R.id.question_input);
        mQaButton.setOnClickListener(this);
        mResultsRecycler = mRootView.findViewById(R.id.results_recycler);

        mlist = mRootView.findViewById(R.id.list);
        mbtSpeak = mRootView.findViewById(R.id.btSpeak);
        mbtSpeak.setOnClickListener(this);
        mlist.setOnItemClickListener(this);
        checkVoiceRecognition();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        mResultsRecycler.setLayoutManager(llm);
        QAAdapter adapter = new QAAdapter(mAnswers);
        adapter.setOnItemClickListener(this);
        mResultsRecycler.setAdapter(adapter);
        return mRootView;
    }

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            //mbtSpeak.setText("Voice recognizer not present");

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if(resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    if (textMatchList.get(0).contains("search")) {

                        String searchQuery = textMatchList.get(0);
                        searchQuery = searchQuery.replace("search","");
                        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                        search.putExtra(SearchManager.QUERY, searchQuery);
                        startActivity(search);

                        Log.e(TAG, "text that matches"+textMatchList);
                    } else {
                        // populate the Matches

                        mlist.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, textMatchList));


                        Log.e(TAG, "text that matches in else"+textMatchList);
                    }

                }
                //Result code for various error.
            }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }



/*
     * Helper method to show the toast message
     **/

    void showToastMessage(String message){

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
            view.setVisibility(INVISIBLE);
            mRootView.findViewById(R.id.progress).setVisibility(VISIBLE);
        }
        if (view == mbtSpeak) {
            mlist.setAdapter(null);
            mlist.setVisibility(VISIBLE);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            // Specify the calling package to identify your application
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                    .getPackage().getName());

            // Display an hint to the user about what he should say.
            //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, metTextHint.getText().toString());


            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

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
        mQaButton.setVisibility(VISIBLE);
        mRootView.findViewById(R.id.progress).setVisibility(INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e(TAG,"item sleected position"+i);
        mQuestionInput.setText(adapterView.getItemAtPosition(i).toString());
        mlist.setVisibility(GONE);
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