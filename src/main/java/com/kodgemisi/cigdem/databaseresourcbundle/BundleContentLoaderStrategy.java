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

import lombok.NonNull;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Strategy for loading contents of {@link com.kodgemisi.cigdem.databaseresourcbundle.DatabaseResourceBundle} from database.
 *
 * Created on June, 2018
 *
 * @author destan
 * @see com.kodgemisi.cigdem.databaseresourcbundle.DefaultBundleContentLoaderStrategy
 */
public interface BundleContentLoaderStrategy {

	/**
	 * Default table name against which all the queries will be made by default.
	 */
	String DEFAULT_TABLE_NAME = "Bundle";

	/**
	 * <p>
	 * Loads all the content of the given resource bundle. When loading the content the bundle's name, language, country and variant should all be taken into consideration.
	 * </p>
	 *
	 * @param bundleName the full name of the resource bundle. For example {@code ButtonLabel_fr_CA_UNIX}.
	 * @return Map of keys and values of the resource bundle
	 */
	Map<String, Object> loadFromDatabase(@NonNull String bundleName);

	/**
	 * <p>Checks if there is any row in database table which is inserted or updated after {@code loadTime}.</p>
	 *
	 * <p>This method is called from {@link DatabaseResourceBundleControl#needsReload}</p>
	 *
	 * @param baseName
	 * @param locale
	 * @param format
	 * @param bundle
	 * @param loadTime
	 * @return true if the contents should be reloaded from the database (there is any row in database table which is inserted or updated after {@code loadTime}), false otherwise.
	 */
	boolean needsReload(String baseName, Locale locale, String format, @NonNull ResourceBundle bundle, long loadTime);

	/**
	 * This class is intended to be used in implementations of {@link com.kodgemisi.cigdem.databaseresourcbundle.BundleContentLoaderStrategy} interface to
	 * easily extract parts of bundle name and to be used in related queries.
	 */
	class BundleMetaData {

		public final String basename;

		public final String language;

		public final String country;

		public final String variant;

		public BundleMetaData(String bundleName) {
			// bundleName is like ButtonLabel_fr_CA_UNIX
			final String[] params = bundleName.split("_");

			this.basename = params[0];
			this.language = params.length >= 2 ? params[1] : "";
			this.country = params.length >= 3 ? params[2] : "";
			this.variant = params.length >= 4 ? params[3] : "";
		}
	}
}
