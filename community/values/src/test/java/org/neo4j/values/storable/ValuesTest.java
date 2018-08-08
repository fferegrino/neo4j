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
package org.neo4j.values.storable;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.neo4j.values.utils.MapValueUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.neo4j.values.storable.Values.booleanArray;
import static org.neo4j.values.storable.Values.booleanValue;
import static org.neo4j.values.storable.Values.byteArray;
import static org.neo4j.values.storable.Values.byteValue;
import static org.neo4j.values.storable.Values.charArray;
import static org.neo4j.values.storable.Values.charValue;
import static org.neo4j.values.storable.Values.doubleArray;
import static org.neo4j.values.storable.Values.doubleValue;
import static org.neo4j.values.storable.Values.floatArray;
import static org.neo4j.values.storable.Values.floatValue;
import static org.neo4j.values.storable.Values.intArray;
import static org.neo4j.values.storable.Values.intValue;
import static org.neo4j.values.storable.Values.longArray;
import static org.neo4j.values.storable.Values.longValue;
import static org.neo4j.values.storable.Values.shortArray;
import static org.neo4j.values.storable.Values.shortValue;
import static org.neo4j.values.storable.Values.stringArray;
import static org.neo4j.values.storable.Values.stringValue;
import static org.neo4j.values.utils.AnyValueTestUtil.assertEqual;

class ValuesTest
{
    @Test
    void shouldParseMapWithArrays()
    {
        MapValueBuilder mv = new MapValueBuilder();
        mv.add("ints", intArray(new int[] { 1, 2, 3}));
        mv.add("bools", booleanArray(new boolean [] { true, false, true, false, false }));
        MapValue expectedMap = mv.build();

        MapValue actualMap = Values.mapValue( "{\"ints\":[1,2,3], \"bools\":[ true, false, true, false, false ]}}" );

        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldParseMapWithDatesTypes()
    {
        LocalDate localDateExpected = LocalDate.of(2018, 1, 1);
        OffsetTime offsetTimeExpected = OffsetTime.of(12, 50, 35, 0, ZoneOffset.ofHours(1));
        LocalTime localTimeExpected = LocalTime.of(12, 50, 35, 0);
        ZonedDateTime zonedDateTimeExpected = ZonedDateTime.of(2018, 1, 1, 21, 40, 32, 0, ZoneOffset.ofHours(1));
        LocalDateTime localDateTimeExpected = LocalDateTime.of(2015, 7, 4, 19, 32, 24);

        MapValueBuilder mv = new MapValueBuilder();
        mv.add("localdate", Values.of(localDateExpected));
        mv.add("localtime", Values.of(localTimeExpected));
        mv.add("localdatetime", Values.of(localDateTimeExpected));
        mv.add("offsettime", Values.of(offsetTimeExpected));
        mv.add("zoneddatetime", Values.of(zonedDateTimeExpected));

        MapValue expectedMap = mv.build();

        String stringifiedMap = MapValueUtil.stringifyMap(expectedMap.asObjectCopy());
        MapValue actualMap = Values.mapValue(stringifiedMap);

        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldParseMapWithDatesTypesNow()
    {
        MapValueBuilder mv = new MapValueBuilder();
        mv.add("localdate", Values.of(LocalDate.now()));
        mv.add("localtime", Values.of(LocalTime.now()));
        mv.add("localdatetime", Values.of(LocalDateTime.now()));
        mv.add("offsettime", Values.of(OffsetTime.now()));
        mv.add("zoneddatetime", Values.of(ZonedDateTime.now()));

        MapValue expectedMap = mv.build();

        String stringifiedMap = MapValueUtil.stringifyMap(expectedMap.asObjectCopy());
        MapValue actualMap = Values.mapValue(stringifiedMap);

        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldParseMapWithJustDurations()
    {
        MapValueBuilder mv = new MapValueBuilder();
        Duration duration = Duration.ofSeconds(45).plusHours(45).plusDays(400);
        mv.add("duration", Values.of(duration));
        mv.add("zero", DurationValue.ZERO);
        mv.add("thousand", DurationValue.duration(Period.of(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE)));

        MapValue expectedMap = mv.build();

        String stringifiedMap = MapValueUtil.stringifyMap(expectedMap.asObjectCopy());
        MapValue actualMap = Values.mapValue(stringifiedMap);

        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldParseMapWithArraysNested()
    {
        MapValueBuilder mvInner = new MapValueBuilder();
        mvInner.add("ints", intArray(new int[] { 1, 2, 3}));
        mvInner.add("bools", booleanArray(new boolean [] { true, false, true, false, false }));
        MapValueBuilder mvOuter  = new MapValueBuilder();
        mvOuter.add("map", mvInner.build());
        MapValue expectedMap = mvOuter.build();

        MapValue actualMap = Values.mapValue("{\"map\": " +
                "{\"ints\":[1,2,3], \"bools\":[ true, false, true, false, false ]}}" +
                " }");

        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldParseMapWithEmptyArray()
    {
        MapValueBuilder mvOuter  = new MapValueBuilder();
        mvOuter.add("empty", stringArray());
        MapValue expectedMap = mvOuter.build();

        MapValue actualMap = Values.mapValue("{\"empty\": [] }");

        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldBeEqualToItself()
    {
        assertEqual( booleanValue( false ), booleanValue( false ) );
        assertEqual( byteValue( (byte) 0 ), byteValue( (byte) 0 ) );
        assertEqual( shortValue( (short) 0 ), shortValue( (short) 0 ) );
        assertEqual( intValue( 0 ), intValue( 0 ) );
        assertEqual( longValue( 0 ), longValue( 0 ) );
        assertEqual( floatValue( 0.0f ), floatValue( 0.0f ) );
        assertEqual( doubleValue( 0.0 ), doubleValue( 0.0 ) );
        assertEqual( stringValue( "" ), stringValue( "" ) );

        assertEqual( booleanValue( true ), booleanValue( true ) );
        assertEqual( byteValue( (byte) 1 ), byteValue( (byte) 1 ) );
        assertEqual( shortValue( (short) 1 ), shortValue( (short) 1 ) );
        assertEqual( intValue( 1 ), intValue( 1 ) );
        assertEqual( longValue( 1 ), longValue( 1 ) );
        assertEqual( floatValue( 1.0f ), floatValue( 1.0f ) );
        assertEqual( doubleValue( 1.0 ), doubleValue( 1.0 ) );
        assertEqual( charValue( 'x' ), charValue( 'x' ) );
        assertEqual( stringValue( "hi" ), stringValue( "hi" ) );

        assertEqual( booleanArray( new boolean[]{} ), booleanArray( new boolean[]{} ) );
        assertEqual( byteArray( new byte[]{} ), byteArray( new byte[]{} ) );
        assertEqual( shortArray( new short[]{} ), shortArray( new short[]{} ) );
        assertEqual( intArray( new int[]{} ), intArray( new int[]{} ) );
        assertEqual( longArray( new long[]{} ), longArray( new long[]{} ) );
        assertEqual( floatArray( new float[]{} ), floatArray( new float[]{} ) );
        assertEqual( doubleArray( new double[]{} ), doubleArray( new double[]{} ) );
        assertEqual( charArray( new char[]{} ), charArray( new char[]{} ) );
        assertEqual( stringArray(), stringArray() );

        assertEqual( booleanArray( new boolean[]{true} ), booleanArray( new boolean[]{true} ) );
        assertEqual( byteArray( new byte[]{1} ), byteArray( new byte[]{1} ) );
        assertEqual( shortArray( new short[]{1} ), shortArray( new short[]{1} ) );
        assertEqual( intArray( new int[]{1} ), intArray( new int[]{1} ) );
        assertEqual( longArray( new long[]{1} ), longArray( new long[]{1} ) );
        assertEqual( floatArray( new float[]{1.0f} ), floatArray( new float[]{1.0f} ) );
        assertEqual( doubleArray( new double[]{1.0} ), doubleArray( new double[]{1.0} ) );
        assertEqual( charArray( new char[]{'x'} ), charArray( new char[]{'x'} ) );
        assertEqual( stringArray( "hi" ), stringArray( "hi" ) );
    }

    @Test
    void pointValueShouldRequireConsistentInput()
    {
        assertThrows( IllegalArgumentException.class, () -> Values.pointValue( CoordinateReferenceSystem.Cartesian, 1, 2, 3 ) );
        assertThrows( IllegalArgumentException.class, () -> Values.pointValue( CoordinateReferenceSystem.Cartesian_3D, 1, 2 ) );
        assertThrows( IllegalArgumentException.class, () -> Values.pointValue( CoordinateReferenceSystem.WGS84, 1, 2, 3 ) );
        assertThrows( IllegalArgumentException.class, () -> Values.pointValue( CoordinateReferenceSystem.WGS84_3D, 1, 2 ) );
    }
}
