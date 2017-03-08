package org.aksw.ldac.input.sparql2nl;

import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;

public class ElementVerbalizationVisitor extends ElementVisitorBase {
	Logger log = LoggerFactory.getLogger(ElementVerbalizationVisitor.class);
	private String naturalLanguangeQuery;
	private DBPedia dbpedia;

	public ElementVerbalizationVisitor() {
		dbpedia = new DBPedia();
	}

	@Override
	public void visit(ElementPathBlock el) {
		ListIterator<TriplePath> it = el.getPattern().iterator();
		while (it.hasNext()) {
			final TriplePath tp = it.next();
			naturalLanguangeQuery += verbalizeSubject(tp) + " ";
			naturalLanguangeQuery += verbalizePredicate(tp) + " ";
			naturalLanguangeQuery += verbalizeObject(tp) + " ";

			log.debug("triplePath" + tp);
		}
	}

	private String verbalizeSubject(TriplePath tp) {
		if (!tp.getSubject().isVariable()) {
			String uri = tp.getSubject().getURI();
			return getLabelFromDBpedia(uri);
		} else
			return new String();
	}

	private String verbalizePredicate(TriplePath tp) {
		if (!tp.getPredicate().isVariable()) {
			String uri = tp.getPredicate().getURI();
			return getLabelFromDBpedia(uri);
		} else
			return new String();

	}

	private String verbalizeObject(TriplePath tp) {
		if (tp.getObject().isURI()) {
			String uri = tp.getObject().getURI();
			return getLabelFromDBpedia(uri);
		} else
			return new String();

	}

	private String getLabelFromDBpedia(String uri) {
		ResultSet res = dbpedia.askDbpedia("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + "SELECT ?label " + "WHERE {<" + uri + "> rdfs:label ?label . FILTER(langMatches(lang(?label), \"EN\"))" + "}");
		return res.next().getLiteral("label").getString();
	}

	public String getNaturalLanguangeQuery() {
		return naturalLanguangeQuery.trim();
	}

	public void setNaturalLanguangeQuery(String naturalLanguangeQuery) {
		this.naturalLanguangeQuery = naturalLanguangeQuery;
	}

}
