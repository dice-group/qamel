package org.aksw.qamel.TeBaQA;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.aksw.qa.commons.datastructure.Question;
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
public class TeBaQAController {
	private Logger log = LoggerFactory.getLogger(TeBaQAController.class);
	protected TeBaQA tebaqa;

	public TeBaQAController() {

		tebaqa = new TeBaQA();
	}

	@RequestMapping(value = "/tebaqa", method = RequestMethod.POST)
	public String askTeBaQA(@RequestParam Map<String, String> params, final HttpServletResponse response) throws ExecutionException, RuntimeException, IOException, ParseException {
		String question = params.get("query");
		String value=params.get("value");
		Question q = new Question();
		List<String> answer = tebaqa.answerQuestion(question);
		q.getLanguageToQuestion().put(question, value);
		log.info("Got: "+JsonUtils.toPrettyString(answer));
		return JsonUtils.toPrettyString(answer);
	}

}