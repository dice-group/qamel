/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.sail.solr.config;

import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.sail.config.SailFactory;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.lucene.LuceneSail;
import org.eclipse.rdf4j.sail.lucene.config.AbstractLuceneSailConfig;
import org.eclipse.rdf4j.sail.solr.SolrIndex;

/**
 * A {@link SailFactory} that creates {@link LuceneSail}s based on RDF configuration data.
 */
public class SolrSailFactory implements SailFactory {

	/**
	 * The type of repositories that are created by this factory.
	 * 
	 * @see SailFactory#getSailType()
	 */
	public static final String SAIL_TYPE = "openrdf:LuceneSail/Solr";

	/**
	 * Returns the Sail's type: <tt>openrdf:LuceneSail/Solr</tt>.
	 */
	@Override
	public String getSailType() {
		return SAIL_TYPE;
	}

	@Override
	public SailImplConfig getConfig() {
		return new SolrSailConfig();
	}

	@Override
	public Sail getSail(SailImplConfig config)
		throws SailConfigException
	{
		if (!SAIL_TYPE.equals(config.getType())) {
			throw new SailConfigException("Invalid Sail type: " + config.getType());
		}

		LuceneSail luceneSail = new LuceneSail();
		luceneSail.setParameter(LuceneSail.INDEX_CLASS_KEY, SolrIndex.class.getName());

		if (config instanceof AbstractLuceneSailConfig) {
			AbstractLuceneSailConfig luceneConfig = (AbstractLuceneSailConfig)config;
			luceneSail.setParameter(LuceneSail.LUCENE_DIR_KEY, luceneConfig.getIndexDir());
			for (String key : luceneConfig.getParameterNames()) {
				luceneSail.setParameter(key, luceneConfig.getParameter(key));
			}
		}

		return luceneSail;
	}
}
