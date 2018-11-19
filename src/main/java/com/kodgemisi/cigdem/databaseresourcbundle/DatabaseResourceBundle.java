/*
 * Copyright © 2018 Kod Gemisi Ltd.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is “Incompatible With Secondary Licenses”, as defined by
 * the Mozilla Public License, v. 2.0.
 */

package com.kodgemisi.cigdem.databaseresourcbundle;

import sun.util.ResourceBundleEnumeration;

import javax.sql.DataSource;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Represents a {@code ResourceBundle} which is backed by (in other words stored in) a database.
 * <p>
 * Created on May, 2018
 *
 * @author destan
 * @see com.kodgemisi.cigdem.databaseresourcbundle.BundleContentLoaderStrategy
 */
public class DatabaseResourceBundle extends ResourceBundle {

	private final Map<String, Object> lookup;

	public DatabaseResourceBundle(Map<String, Object> lookup) {
		this.lookup = lookup;
	}

	public static ResourceBundle getBundle(String baseName, DataSource dataSource) {
		return DatabaseResourceBundle.getBundle(baseName, Locale.getDefault(), dataSource);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale, DataSource dataSource) {
		return new DatabaseResourceBundleControl(new DefaultBundleContentLoaderStrategy(dataSource)).newBundle(baseName, locale,
																											   DatabaseResourceBundleControl.FORMAT,
																											   DatabaseResourceBundle.class.getClassLoader(),
																											   true);
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}
		return lookup.get(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		ResourceBundle parent = this.parent;
		return new ResourceBundleEnumeration(lookup.keySet(), parent != null ? parent.getKeys() : null);
	}

}

/*
 * Implementation Notes
 * ====================
 * We could extend ListResourceBundle and only override {@link java.util.ListResourceBundle#getContents} method for this class' implementation.
 * In {@link java.util.ListResourceBundle#getContents} we could load contents from database.
 * <p>
 * However in {@link java.util.PropertyResourceBundle}'s implementation, it's contents are loaded in its constructor. We choose to mimic PropertyResourceBundle's
 * behavior when implementing DatabaseResourceBundle so its contents are given to its constructor.
 * <p>
 * In that way, we can abstract content loading logic away into {@link java.util.ResourceBundle.Control} implementation just like in PropertyResourceBundle's implementation.
 */

