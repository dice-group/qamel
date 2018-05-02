package org.aksw.qamel.data2rdf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
@WebAppConfiguration
public class ControllerTest {

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
		                              .build();
	}
//TODO write tests for the other controller endpoints once the API is stable
	@Test
	public void disambiguation() throws Exception {

		this.mockMvc.perform(post("/disambiguation").content("{\"input\": \"The philosopher and mathematician <entity>Leibniz</entity> was born in <entity>Leipzig</entity> in 1646.\",\"type\": \"text\",\"lang\":\"en\"}")
								.contentType(MediaType.APPLICATION_JSON))
		                                            

		 .andDo(print())
         .andExpect(status().isOk())
         .andExpect(content().json("{"
         		+ "			\"output\": ["
         		+ "				{"
         		+ "					\"namedEntity\": \"Leibniz\","
         		+ "					\"start\": 34,"
         		+ "					\"end\": 41,"
         		+ "					\"offset\": 7,"
         		+ "					\"disambiguatedURL\": \"http://dbpedia.org/resource/Gottfried_Wilhelm_Leibniz\""
         		+ "				},"
         		+ "				{"
         		+ "					\"namedEntity\": \"Leipzig\","
         		+ "					\"start\": 54,"
         		+ "					\"end\": 61,"
         		+ "					\"offset\": 7,"
         		+ "					\"disambiguatedURL\": \"http://dbpedia.org/resource/Leipzig\""
         		+ "				}"
         		+ "			]"
         		+ "		}"));
        


}
}