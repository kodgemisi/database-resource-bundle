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

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * This class loads the content of a {@link com.kodgemisi.cigdem.databaseresourcbundle.DatabaseResourceBundle} from {@link #DEFAULT_TABLE_NAME}
 * by using DEFAULT_LOAD_QUERY via {@link java.sql.PreparedStatement}
 * </p>
 *
 * <p>
 * You can override {@link #DEFAULT_LOAD_QUERY} and {@link #DEFAULT_NEEDS_RELOAD_QUERY} by using constructor
 * {@link com.kodgemisi.cigdem.databaseresourcbundle.DefaultBundleContentLoaderStrategy#DefaultBundleContentLoaderStrategy(javax.sql.DataSource, String, String)}.
 * See each constructor's javadoc for more information.
 * </p>
 * <p>
 * Created on June, 2018
 *
 * @author destan
 */
@Slf4j
public class DefaultBundleContentLoaderStrategy implements BundleContentLoaderStrategy {

	private static final String DEFAULT_LOAD_QUERY =
			"SELECT DISTINCT b.key, b.value FROM " + DEFAULT_TABLE_NAME + " b WHERE name = ? AND language = ? AND country = ? AND variant = ? ;";

	private static final String DEFAULT_NEEDS_RELOAD_QUERY = "SELECT MAX(last_modified) FROM " + DEFAULT_TABLE_NAME + " b WHERE name = ? ;";

	private final DataSource dataSource;

	private final String loadQuery;

	private final String needsReloadQuery;

	/**
	 * This constructor used default values for databse operations.
	 *
	 * @param dataSource datasource on which the prepared statements will be run.
	 * @see #DEFAULT_TABLE_NAME
	 * @see #DEFAULT_LOAD_QUERY
	 * @see #DEFAULT_NEEDS_RELOAD_QUERY
	 */
	public DefaultBundleContentLoaderStrategy(DataSource dataSource) {
		this(dataSource, DEFAULT_LOAD_QUERY, DEFAULT_NEEDS_RELOAD_QUERY);
	}

	/**
	 * By using this constructor you can override the table name which is used in {@link #DEFAULT_LOAD_QUERY} and {@link #DEFAULT_NEEDS_RELOAD_QUERY}.
	 *
	 * @param dataSource datasource on which the prepared statements will be run.
	 * @param tableName  the table name which will override {@link #DEFAULT_TABLE_NAME}.
	 * @see #DEFAULT_TABLE_NAME
	 */
	public DefaultBundleContentLoaderStrategy(DataSource dataSource, String tableName) {
		this(dataSource, DEFAULT_LOAD_QUERY.replace(DEFAULT_TABLE_NAME, tableName),
			 DEFAULT_NEEDS_RELOAD_QUERY.replace(DEFAULT_TABLE_NAME, tableName));
	}

	/**
	 * By using this constructor you can override default queries {@link #DEFAULT_LOAD_QUERY} and {@link #DEFAULT_NEEDS_RELOAD_QUERY}.
	 *
	 * @param dataSource       datasource on which the prepared statements will be run.
	 * @param loadQuery        the query which will override {@link #DEFAULT_LOAD_QUERY}. This query is used to load bundle content from the database.
	 * @param needsReloadQuery the query which will override {@link #DEFAULT_NEEDS_RELOAD_QUERY}. This query is used to check if database content is changed since last load.
	 * @see #DEFAULT_LOAD_QUERY
	 * @see #DEFAULT_NEEDS_RELOAD_QUERY
	 */
	public DefaultBundleContentLoaderStrategy(DataSource dataSource, String loadQuery, String needsReloadQuery) {
		this.dataSource = dataSource;
		this.loadQuery = loadQuery;
		this.needsReloadQuery = needsReloadQuery;

		if (log.isDebugEnabled()) {
			log.debug("DefaultBundleContentLoaderStrategy is initialized with following 2 queries:");
			log.debug("loadQuery {}", loadQuery);
			log.debug("needsReloadQuery {}", needsReloadQuery);
		}
	}

	@Override
	public Map<String, Object> loadFromDatabase(String bundleName) {
		final BundleMetaData bundleMetaData = new BundleMetaData(bundleName);

		try (final Connection connection = dataSource.getConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(loadQuery);) {

			preparedStatement.setString(1, bundleMetaData.basename);
			preparedStatement.setString(2, bundleMetaData.language);
			preparedStatement.setString(3, bundleMetaData.country);
			preparedStatement.setString(4, bundleMetaData.variant);

			if (log.isTraceEnabled()) {
				log.trace("Loading content for {} with query {}", bundleName, preparedStatement);
			}

			try (final ResultSet resultSet = preparedStatement.executeQuery()) {
				final Map<String, Object> resultMap = new HashMap<>();

				while (resultSet.next()) {
					final String key = resultSet.getString("key");
					final String value = resultSet.getString("value");
					resultMap.put(key, value);
				}

				return resultMap;
			}
		}
		catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

		return Collections.emptyMap();
	}

	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ResourceBundle bundle, long loadTime) {
		try (final Connection connection = dataSource.getConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(needsReloadQuery)) {

			preparedStatement.setString(1, baseName);

			if (log.isTraceEnabled()) {
				log.trace("Checking if reload needed content for {} {} with query {}", baseName, locale, preparedStatement);
			}

			try (final ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					final long lastModifiedTime = resultSet.getLong(1);
					return lastModifiedTime > loadTime;
				}
			}
		}
		catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		return true;
	}

}
