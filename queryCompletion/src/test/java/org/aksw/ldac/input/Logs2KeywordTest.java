package org.aksw.ldac.input;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.Test;

public class Logs2KeywordTest {
	@Test
	public void testExtractQuery() throws MalformedURLException, URISyntaxException {
		String log = "2013-11-19-01-search-frontend 2013-11-27 05:54:44,486 ips=10.16.93.0 pid=1789 tid=24029d9e-d503-4c0c-8a73-2900b699152d [INFO ] <http-8080-12> NEW TRACE: GET http://www.bluekiwi.de/web/search?it=liniertes+papier+2+klasse&utm_medium=cpc&utm_source=google&utm_campaign=cpc_Google-KWs_1-14&koid=7553338414&pk_kwd=liniertes%20papier%202%20klasse&pk_mt=b // remote_addr=37.24.174.25 x-forwarded-for=null referer=http://www.google.com/uds/afs?q=lineatur%20klasse%202&client=pub-5954065646542570&channel=1038908302&hl=de&oe=UTF-8&ie=UTF-8&r=s&fexp=21404%2C7000108&jsei=4&format=p4&ad=n0&nocache=6801385531680812&num=0&output=uds_ads_only&v=3&allwcallad=1&adext=ctc1&rurl=http%3A%2F%2Fwww.auspreiser.de%2Fpreise%2F%3Fq%3DLineatur%2BKlasse%2B2&referer=http%3A%2F%2Fwww.google.com%2Fcse%3Fq%3DLineatur%2520deutsch%25202.%2520klasse%26client%3Dgoogle-coop%26hl%3Dde%26r%3Dm%26cx%3D01100272707440... ";
		String query = "liniertes papier 2 klasse";

		Logs2Keywords l2k = new Logs2Keywords();
		String test = l2k.transformLogToQuery(log);
		assertTrue(test.equals(query));
	}
}
