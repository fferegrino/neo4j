/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.values.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.values.AnyValue;
import org.neo4j.values.storable.Values;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapValueUtil
{

    /**
     * Turn a string representation of a Map<String, Object> into a Map<String, AnyValue>.
     * @param mapRepresentation
     * @return
     */
    public static Map<String, Object> parseMap( String mapRepresentation )
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) mapper.readValue(mapRepresentation, HashMap.class);
            return hashMap;
        }
        catch ( IOException e )
        {
            // TODO: do something as the transformation failed
        }
        return null;
    }

    /**
     * Serialize a Map into a string
     * @param map
     * @return
     */
    public static String stringifyMap( Map<String, Object> map )
    {
        ObjectMapper mapper = new ObjectMapper();
        // Using a HashMap as backing storage...
        String mapRepresentation = null;
        try
        {
            mapRepresentation = mapper.writeValueAsString( map );
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalArgumentException( "There was an error converting your map to a string", e );
        }
        return mapRepresentation;
    }
}
