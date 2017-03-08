package org.aksw.ldac.input.sparql2nl;

import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;

public class ElementBindingCounterVisitor extends ElementVisitorBase {
	Logger log = LoggerFactory.getLogger(ElementBindingCounterVisitor.class);
	private int counter;

	public ElementBindingCounterVisitor() {
		counter = 0;
	}

	@Override
	public void visit(ElementPathBlock el) {
		ListIterator<TriplePath> it = el.getPattern().iterator();
		while (it.hasNext()) {
			final TriplePath tp = it.next();
			if (tp.getSubject().isURI())
				counter++;
			if (tp.getPredicate().isURI())
				counter++;
			if (!tp.getObject().isVariable())
				counter++;
			log.debug("triplePath" + tp);
		}
	}

	public int getNumberOfBoundVariables() {

		return counter;
	}

}
