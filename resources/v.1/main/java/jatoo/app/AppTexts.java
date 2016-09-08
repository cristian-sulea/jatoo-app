/*
 * Copyright (C) 2014 Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.app;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;

/**
 * This class allows you to use locale specific texts.
 * 
 * @author Cristian Sulea ( http://cristian.sulea.net )
 * @version 2.1, May 21, 2014
 */
public class AppTexts {

	private Class<?> clazz;
	private Log logger;
	private ResourceBundle resourceBundle;

	private AppTexts fallback;

	public AppTexts(Class<?> clazz, Log fallback) {
		this(clazz, fallback, null);
	}

	public AppTexts(Class<?> clazz, Log logger, AppTexts fallback) {

		this.clazz = clazz;
		this.logger = logger;
		this.fallback = fallback;

		try {
			resourceBundle = ResourceBundle.getBundle(clazz.getPackage().getName() + ".texts");
		}

		catch (Throwable e) {
			resourceBundle = null;
			logger.error("no texts for class: " + clazz.getName(), e);
		}
	}

	public String getText(String key) {

		if (resourceBundle == null) {
			logger.error("no texts for class: " + clazz.getName() + " returning the key: #" + key);
			return "#" + key;
		}

		try {
			return resourceBundle.getString(key);
		}

		catch (MissingResourceException e1) {

			if (fallback != null) {
				return fallback.getText(key);
			}

			else {
				logger.error("no key #" + key);
				return "#" + key;
			}
		}
	}

}
