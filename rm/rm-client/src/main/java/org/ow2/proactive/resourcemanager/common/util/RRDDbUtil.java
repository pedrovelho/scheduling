/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.resourcemanager.common.util;

public class RRDDbUtil {

    /**
     * @param zone on the possible zone: a, m, h, H, d, w, M, y.
     * @return number of seconds according to the existing zones
     */
    public static long secondsInZone(char zone) {
        switch (zone) {
            default:
            case 'a': // 1 minute
                return 60;
            case 'n': // 5 minutes
                return 60 * 5;
            case 'm': // 10 minutes
                return 60 * 10;
            case 't': // 30 minutes
                return 60 * 30;
            case 'h': // 1 hour
                return 60 * 60;
            case 'j': // 2 hours
                return 60 * 60 * 2;
            case 'k': // 4 hours
                return 60 * 60 * 4;
            case 'H': // 8 hours
                return 60 * 60 * 8;
            case 'd': // 1 day
                return 60 * 60 * 24;
            case 'w': // 1 week
                return 60 * 60 * 24 * 7;
            case 'M': // 1 month
                return 60 * 60 * 24 * 28;
            case 'y': // 1 year
                return 60 * 60 * 24 * 365;
        }
    }
}
