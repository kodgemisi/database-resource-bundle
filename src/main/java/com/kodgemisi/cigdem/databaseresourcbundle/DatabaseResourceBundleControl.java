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
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created on January, 2018
 *
 * @author destan
 */
@Slf4j
public class DatabaseResourceBundleControl extends ResourceBundle.Control {

	static final String FORMAT = "bundle.database";

	// java.util.ResourceBundle.Control.getTimeToLive's default is TTL_NO_EXPIRATION_CONTROL
	private static final long DEFAULT_TTL = TTL_NO_EXPIRATION_CONTROL; // should it be 3600000 (1 hour) ?

	private static final List<String> ACCEPTABLE_FORMATS = Collections.unmodifiableList(Arrays.asList(FORMAT));

	private final long ttl;

	private final BundleContentLoaderStrategy bundleContentLoaderStrategy;

	public DatabaseResourceBundleControl(BundleContentLoaderStrategy bundleContentLoaderStrategy) {
		this(bundleContentLoaderStrategy, DEFAULT_TTL);
	}

	/**
	 * @param bundleContentLoaderStrategy
	 * @param ttl                         can't be lesser than -2 ({@link java.util.ResourceBundle.Control#TTL_NO_EXPIRATION_CONTROL})
	 * @throws IllegalArgumentException when ttl is lesser than -2 ({@link java.util.ResourceBundle.Control#TTL_NO_EXPIRATION_CONTROL})
	 */
	public DatabaseResourceBundleControl(BundleContentLoaderStrategy bundleContentLoaderStrategy, long ttl) {

		// This control is copied from java.util.ResourceBundle.setExpirationTime in order to keep consistent API
		if (ttl >= TTL_NO_EXPIRATION_CONTROL) {
			this.ttl = ttl;
		}
		else {
			throw new IllegalArgumentException("Invalid Control: TTL=" + ttl);
		}

		this.bundleContentLoaderStrategy = bundleContentLoaderStrategy;
	}

	@Override
	public List<String> getFormats(@NonNull String baseName) {
		return ACCEPTABLE_FORMATS;
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) {

		if (!ACCEPTABLE_FORMATS.contains(format)) {
			// similar behavior with java.util.ResourceBundle.Control#newBundle
			throw new IllegalArgumentException("unknown format: " + format);
		}

		// See usage of reload of "if (reloadFlag) {" in java.util.ResourceBundle.Control.newBundle.
		// We need to disable cache similarly for db. However it seems not very safe.
		// Instead we should encourage user not to cache BundleEntity with JPA 2nd level cache.

		final String bundleName = super.toBundleName(baseName, locale);
		final Map<String, Object> lookup = loadFromDatabase(bundleName);
		return lookup.isEmpty() ? null : new DatabaseResourceBundle(lookup);
	}

	@Override
	public long getTimeToLive(@NonNull String baseName, @NonNull Locale locale) {
		return ttl;
	}

	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, @NonNull ResourceBundle bundle, long loadTime) {
		return this.bundleContentLoaderStrategy.needsReload(baseName, locale, format, bundle, loadTime);
	}

	/**
	 * @param bundleName
	 * @return never returns null.
	 */
	private Map<String, Object> loadFromDatabase(@NonNull String bundleName) {
		return this.bundleContentLoaderStrategy.loadFromDatabase(bundleName);
	}
}

// Implementation notes
// ====================
//
// FIXME: java.util.ResourceBundle.findBundleInCache uses setExpirationTime for non-expired bundles which resets the ttl for the bundle in cache which is wrong for us.
// because cacheKey.loadTime is not now for us, it's the last change time of our db table. However it works fine somehow!!
//
//
// In loadFromDatabase we could add useCache param and implement as follows but not sure if we need this:
//
// if(!useCache) {
//   typedQuery.setHint("eclipselink.cache-usage", "DoNotCheckCache");// typedQuery.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
//   typedQuery.setHint("org.hibernate.cacheable", true);
// }