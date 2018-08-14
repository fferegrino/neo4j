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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.values.storable.DurationValue;
import org.neo4j.values.utils.map.serializers.CustomMapSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class MapValueUtil
{
    static CustomMapSerializer MAPPER = new CustomMapSerializer();

    static
    {
        MAPPER.enable( DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY );
    }

    /**
     * Turn a string representation of a Map<String, Object> into a Map<String, AnyValue>.
     * @param mapRepresentation
     * @return
     */
    public static Map<String, Object> parseMap( String mapRepresentation )
    {
        try
        {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) MAPPER.readValue( mapRepresentation, HashMap.class );
            replaceArrays( hashMap );
            return hashMap;
        }
        catch ( IOException e )
        {
            // TODO: Indicate error while parsing
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Using ObjectMapper arrays are being received as Object[] containing... Object, the purpose of this method is to
     * cast each array into the original values it contains, i.e. if the data type of the elementis in the array is
     * Integer, it will get transformed to an array of type Integer[]
     * @param map
     */
    private static void replaceArrays( HashMap<String, Object> map )
    {
        for ( String key : map.keySet() )
        {
            Object value = map.get(key);
            if ( value instanceof Object[] )
            {
                Object[] objects = (Object[]) value;
                if ( objects.length > 0 )
                {
                    Object first =  objects[0];
                    if ( first instanceof String )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, String[].class ) );
                    }
                    else if ( first instanceof Byte )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Byte[].class ) );
                    }
                    else if ( first instanceof Long )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Long[].class ) );
                    }
                    else if ( first instanceof Integer )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Integer[].class ) );
                    }
                    else if ( first instanceof Double )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Double[].class ) );
                    }
                    else if ( first instanceof Float )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, String[].class ) );
                    }
                    else if ( first instanceof Boolean )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Boolean[].class ) );
                    }
                    else if ( first instanceof Character )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Character[].class ) );
                    }
                    else if ( first instanceof Short )
                    {
                        map.put( key, Arrays.copyOf( objects, objects.length, Short[].class ) );
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                                format( "[%s:%s] is not a supported value for arrays inside maps", first, first.getClass().getName() ) );

                    }
                }
                else
                {
                    // I've seen that empty arrays are treated as empty string arrays
                    map.put( key, new String[0] );
                }
            }
            else if ( value instanceof HashMap<?,?> )
            {
                HashMap<String, Object> innerMap = (HashMap<String, Object>) value;
                Set<String> keys = innerMap.keySet();

                if ( keys.equals(LOCALDATE_SET) )
                {
                    int year = (int) innerMap.get("_year");
                    int monthValue = (int) innerMap.get("_month");
                    int dayOfMonth = (int) innerMap.get("_day");
                    map.put( key, LocalDate.of( year, monthValue, dayOfMonth ) );
                }
                else if ( keys.equals(DURATION_SET) )
                {
                    //"months", "days", "seconds", "nanos", "units", "naN", "sequenceValue"

                    Object monthsRef = innerMap.get("_months");
                    long months = monthsRef instanceof Long ? (long) monthsRef : Long.valueOf( (int)monthsRef );

                    Object daysRef = innerMap.get("_days");
                    long days = daysRef instanceof Long ? (long) daysRef : Long.valueOf( (int)daysRef );

                    Object secondsRef = innerMap.get("_seconds");
                    long seconds = secondsRef instanceof Long ? (long) secondsRef : Long.valueOf( (int)secondsRef );

                    int nanos = (int) innerMap.get("_nanos");

                    DurationValue duration = DurationValue.duration(months,days,seconds,nanos);
                    map.put( key, duration );
                }
                else if ( keys.equals(LOCALDATETIME_SET) )
                {
                    int year = (int) innerMap.get("_year");
                    int monthValue = (int) innerMap.get("_month");
                    int dayOfMonth = (int) innerMap.get("_day");
                    int hour = (int) innerMap.get("_hour");
                    int minute = (int) innerMap.get("_minute");
                    int second = (int) innerMap.get("_second");
                    int nano = (int) innerMap.get("_nano");
                    map.put( key, LocalDateTime.of( year, monthValue, dayOfMonth, hour, minute, second, nano ) );
                }
                else if ( keys.equals(LOCALTIME_SET) )
                {
                    int hour = (int) innerMap.get("_hour");
                    int minute = (int) innerMap.get("_minute");
                    int second = (int) innerMap.get("_second");
                    int nano = (int) innerMap.get("_nano");
                    map.put( key, LocalTime.of( hour, minute, second, nano ) );
                }
                else if ( keys.equals(OFFSETTIME_SET) )
                {
                    int hour = (int) innerMap.get("_hour");
                    int minute = (int) innerMap.get("_minute");
                    int second = (int) innerMap.get("_second");
                    int nano = (int) innerMap.get("_nano");
                    String offsetId = (String) innerMap.get("_offset");
                    map.put( key, OffsetTime.of( hour, minute, second, nano, ZoneOffset.of( offsetId ) ) );
                }
                else if ( keys.equals(ZONEDDATETIME_SET) )
                {
                    int year = (int) innerMap.get("_year");
                    int monthValue = (int) innerMap.get("_month");
                    int dayOfMonth = (int) innerMap.get("_day");
                    int hour = (int) innerMap.get("_hour");
                    int minute = (int) innerMap.get("_minute");
                    int second = (int) innerMap.get("_second");
                    int nano = (int) innerMap.get("_nano");
                    String offsetId = (String) innerMap.get("_offset");
                    map.put( key, ZonedDateTime.of( year, monthValue, dayOfMonth, hour, minute, second, nano, ZoneId.of( offsetId ) ) );
                }
                else
                {
                    replaceArrays(innerMap);
                }
            }
        }
    }

    private static final String[] LOCALDATE_KEYS = new String[] { CustomMapSerializer.TYPE_FIELD_NAME, "_year", "_month", "_day" };
    private static final Set<String> LOCALDATE_SET = new HashSet<>( Arrays.asList( LOCALDATE_KEYS ) );

    //private static final String[] DURATION_KEYS = new String[] { "seconds", "nano", "units", "zero", "negative" };
    private static final String[] DURATION_KEYS = new String[] {  CustomMapSerializer.TYPE_FIELD_NAME, "_months", "_days", "_seconds", "_nanos" };
    private static final Set<String> DURATION_SET = new HashSet<>( Arrays.asList( DURATION_KEYS ) );

    private static final String[] LOCALDATETIME_KEYS = new String[] { CustomMapSerializer.TYPE_FIELD_NAME, "_year", "_month", "_day",
            "_hour", "_minute", "_second", "_nano" };
    private static final Set<String> LOCALDATETIME_SET = new HashSet<>( Arrays.asList( LOCALDATETIME_KEYS ) );

    private static final String[] LOCALTIME_KEYS = new String[] { CustomMapSerializer.TYPE_FIELD_NAME, "_hour", "_minute", "_second", "_nano" };
    private static final Set<String> LOCALTIME_SET = new HashSet<>( Arrays.asList( LOCALTIME_KEYS ) );

    private static final String[] OFFSETTIME_KEYS = new String[] { CustomMapSerializer.TYPE_FIELD_NAME, "_offset",
            "_hour", "_minute", "_second", "_nano"};
    private static final Set<String> OFFSETTIME_SET = new HashSet<>( Arrays.asList( OFFSETTIME_KEYS ) );

    private static final String[] ZONEDDATETIME_KEYS = new String[] { CustomMapSerializer.TYPE_FIELD_NAME, "_year", "_month", "_day", "_offset",
            "_hour", "_minute", "_second", "_nano"};
    private static final Set<String> ZONEDDATETIME_SET = new HashSet<>( Arrays.asList( ZONEDDATETIME_KEYS ) );

    /**
     * Serialize a Map into a string
     * @param map
     * @return
     */
    public static String stringifyMap( Map<String, Object> map )
    {
        String mapRepresentation = null;
        try
        {
            mapRepresentation = MAPPER.writeValueAsString( map );
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalArgumentException( "There was an error converting your map to a string", e );
        }
        return mapRepresentation;
    }
}
