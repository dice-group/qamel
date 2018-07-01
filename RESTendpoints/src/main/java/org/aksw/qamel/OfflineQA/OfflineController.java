package org.aksw.qamel.OfflineQA;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.naming.Context;
import javax.servlet.http.HttpServletResponse;
import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qamel.OfflineQA.App;
import org.aksw.qamel.OfflineQA.QAResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.jsonldjava.utils.JsonUtils;


@RestController
@EnableAutoConfiguration
public class OfflineController {
	private Logger log = LoggerFactory.getLogger(OfflineController.class);
	protected App system;
	private static final Context Context = null;

	public OfflineController() {

		system = new App(Context);
	}

	@RequestMapping(value = "/offline", method = RequestMethod.POST)
	public String askOffline(@RequestParam Map<String, String> params, final HttpServletResponse response) throws ExecutionException, RuntimeException, IOException, ParseException {
		String question = params.get("query");
		String lang = params.get("lang");
		log.debug("Received question = " + question);
		log.debug("Language of question = " + lang);
		Question q = new Question();
		q.getLanguageToQuestion().put(lang,question);
		QAResult[] answer = system.answerQuestion(question);
		log.info("Got: "+JsonUtils.toPrettyString(answer));
		return JsonUtils.toPrettyString(answer);
	}

}