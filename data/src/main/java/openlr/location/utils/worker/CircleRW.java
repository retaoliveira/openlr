/**
 * Licensed to the TomTom International B.V. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  TomTom International B.V.
 * licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * <p>
 * Copyright (C) 2009-2019 TomTom International B.V.
 * <p>
 * TomTom (Legal Department)
 * Email: legal@tomtom.com
 * <p>
 * TomTom (Technical contact)
 * Email: openlr@tomtom.com
 * <p>
 * Address: TomTom International B.V., Oosterdoksstraat 114, 1011DK Amsterdam,
 * the Netherlands
 * <p>
 * Copyright (C) 2009-2019 TomTom International B.V.
 * <p>
 * TomTom (Legal Department)
 * Email: legal@tomtom.com
 * <p>
 * TomTom (Technical contact)
 * Email: openlr@tomtom.com
 * <p>
 * Address: TomTom International B.V., Oosterdoksstraat 114, 1011DK Amsterdam,
 * the Netherlands
 */
/**
 *  Copyright (C) 2009-2019 TomTom International B.V.
 *
 *   TomTom (Legal Department)
 *   Email: legal@tomtom.com
 *
 *   TomTom (Technical contact)
 *   Email: openlr@tomtom.com
 *
 *   Address: TomTom International B.V., Oosterdoksstraat 114, 1011DK Amsterdam,
 *   the Netherlands
 */
package openlr.location.utils.worker;

import openlr.location.Location;
import openlr.location.LocationFactory;
import openlr.location.utils.LocationDataConstants;
import openlr.location.utils.LocationDataException;
import openlr.map.InvalidMapDataException;
import openlr.map.MapDatabase;

/**
 * Location reader and writer.
 *
 * <p>
 * OpenLR is a trade mark of TomTom International B.V.
 * <p>
 * email: software@openlr.org
 *
 * @author TomTom International B.V.
 */
public class CircleRW extends AbstractRW {

    /** The Constant CIRCLE_INDEX. */
    private static final int CIRCLE_LON_INDEX = 0;

    /** The Constant CIRCLE_LAT_INDEX. */
    private static final int CIRCLE_LAT_INDEX = 1;

    /** The Constant CIRCLE_RADIUS_INDEX. */
    private static final int CIRCLE_RADIUS_INDEX = 2;

    /**
     * {@inheritDoc}
     */
    @Override
    public final String createLocationString(
            final Location location) {
        double lon = location.getCenterPoint().getLongitudeDeg();
        double lat = location.getCenterPoint().getLatitudeDeg();
        long radius = location.getRadius();
        StringBuilder sb = new StringBuilder();
        sb.append(LocationDataConstants.CIRCLE_MARKER).append(
                LocationDataConstants.PART_DELIMITER);
        sb.append(location.getID());
        sb.append(LocationDataConstants.PART_DELIMITER);
        sb.append(lon);
        sb.append(LocationDataConstants.FEATURE_DELIMITER);
        sb.append(lat);
        sb.append(LocationDataConstants.FEATURE_DELIMITER);
        sb.append(radius);

        sb.append("\n");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Location readLocationString(final String id,
                                             final String[] features, final MapDatabase mdb)
            throws LocationDataException {

        if (features.length < CIRCLE_RADIUS_INDEX + 1) {
            throw new LocationDataException(id + ": Not enough coordinates for circle");
        }
        try {
            double lon = Double.parseDouble(features[CIRCLE_LON_INDEX]);
            double lat = Double.parseDouble(features[CIRCLE_LAT_INDEX]);
            int radius = Integer.parseInt(features[CIRCLE_RADIUS_INDEX]);

            return LocationFactory.createCircleLocation(id, lon, lat, radius);
        } catch (InvalidMapDataException e) {
            throw new LocationDataException(id + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new LocationDataException(id + ": Wrong coordinate format", e);
        }
    }

}
