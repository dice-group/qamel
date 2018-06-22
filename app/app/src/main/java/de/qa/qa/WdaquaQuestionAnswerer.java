package de.qa.qa;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.qa.qa.result.DateResult;
import de.qa.qa.result.FooterResult;
import de.qa.qa.result.HeaderResult;
import de.qa.qa.result.QAResult;
import de.qa.qa.result.TextResult;
import de.qa.qa.result.UriResult;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WdaquaQuestionAnswerer implements QuestionAnswerer{
    private static final String TAG = null;
    Button online_test;
    String data = "";

   private static final String WDAQUA_URI = "http://185.2.103.92:8081/tebaqa/qa";

    @Override
    public QAResult[] answerQuestion(String question) {
        Log.d("QA", "Answering question using " + getClass().getSimpleName());
        QAResult[] answers = new QAResult[0];
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("query", question)
                    .build();
            Request request = new Request.Builder()
                    .url(WDAQUA_URI)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject resultObject = new JSONObject(response.body().string());
            JSONObject answersObject = new JSONObject(
                    resultObject.getJSONArray("questions")
                            .getJSONObject(0).getJSONObject("question")
                            .getString("answers"));
            JSONArray bindings = answersObject.getJSONObject("results")
                    .getJSONArray("bindings");
            answers = new QAResult[bindings.length() + 2];
            answers[0] = new HeaderResult(question);
            for (int i = 0; i < bindings.length(); i++) {
                JSONObject binding = bindings.getJSONObject(i);
                answers[i + 1] = createQAResult(question,
                        binding.getJSONObject("x").getString("type"),
                        binding.getJSONObject("x").optString("datatype"),
                        //Nah
                        binding.getJSONObject("x").getString("value"));
                Log.d("QA", "Result: " + binding.toString());
            }
            answers[answers.length - 1] = new FooterResult(question);
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), Log.getStackTraceString(e));
            answers = new QAResult[]{
                    new HeaderResult(question),
                    new TextResult(question, "An error occurred"),
                    new FooterResult(question)
            };
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), Log.getStackTraceString(e));
            answers = new QAResult[]{
                    new HeaderResult(question),
                    new TextResult(question, "Couldn't connect to Internet"),
                    new FooterResult(question)
            };
        }
        if (answers.length == 0) {
            answers = new QAResult[]{
                    new HeaderResult(question),
                    new TextResult(question, "Can't answer this question."),
                    new FooterResult(question)
            };
        }
        return answers;
    }

    private QAResult createQAResult(String question, String type, @Nullable String dataType,
                                    String data) {
        switch (type) {
            case "uri":
                return new UriResult(question, data);
            case "typed-literal":
                switch (dataType) {
                    case "http://www.w3.org/2001/XMLSchema#date":
                        return new DateResult(question, data);
                }
        }
        return new QAResult(question, data);
    }
}
