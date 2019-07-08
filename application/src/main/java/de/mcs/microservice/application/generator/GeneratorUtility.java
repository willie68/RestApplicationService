/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: GeneratorUtility.java
 * EMail: W.Klaas@gmx.de
 * Created: 05.01.2018 wklaa_000
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package de.mcs.microservice.application.generator;

/**
 * @author wklaa_000
 *
 */
public class GeneratorUtility {

  public String firstToUpperCase(String value) {
    if (value != null) {
      if (value.length() >= 2) {
        return String.format("%s%s", value.substring(0, 1).toUpperCase(), value.substring(1));
      } else {
        return value.toUpperCase();
      }
    }
    return null;
  }
}
