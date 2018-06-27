package org.aksw.qamel.TeBaQA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aksw.qamel.OfflineQA.Answer;
import org.aksw.qamel.OfflineQA.QAResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.lang.Nullable;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeBaQA{

	private static final String URI = "http://185.2.103.92:8081/tebaqa/qa";
	static String question= "What is capital of France";
	public List<String> Answer = new ArrayList();



	public  List answerQuestion(String question) throws IOException {
		Answer.clear();
		System.out.println("QUESTION: "+question);
		QAResult[] answers = null;
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
					.url(URI)
					.post(requestBody)
					.build();
			System.out.println("REQUEST: "+request);
			Response response = client.newCall(request).execute();
			JSONObject resultObject = new JSONObject(response.body().string());
			JSONObject answersObject = new JSONObject(
					resultObject.getJSONArray("questions")
					.getJSONObject(0).getJSONObject("question")
					.getString("answers"));
			JSONArray bindings = answersObject.getJSONObject("results")
					.getJSONArray("bindings");
			answers = new QAResult[bindings.length() + 2];
			System.out.println("Answer: "+answers);

			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				answers[i + 1] = createQAResult(question,
						binding.getJSONObject("x").getString("type"),
						binding.getJSONObject("x").optString("datatype"),

						binding.getJSONObject("x").getString("value"));
				// Log.d("QA", "Result: " + binding.toString());
				System.out.println("RESULT: "+ binding.toString());

				Answer.add(binding.toString());

			}

		} catch (JSONException e) {

		}
		return Answer;

	}


	public QAResult createQAResult(String question, String type, @Nullable String dataType,
			String data) {
		switch (type) {
		case "uri":
			return new QAResult(question, data);
		case "typed-literal":
			switch (dataType) {
			case "http://www.w3.org/2001/XMLSchema#date":
				return new QAResult(question, data);
			}
		}
		return new QAResult(question, data);
	}
}








