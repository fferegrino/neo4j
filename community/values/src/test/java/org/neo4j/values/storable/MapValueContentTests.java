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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapValueContentTests
{
    @Test
    void shouldReturnMixed()
    {
        MapValueContent storable = MapValueContent.STORABLE;
        MapValueContent virtual = MapValueContent.VIRTUAL;
        MapValueContent actual1 = storable.Combine(MapValueContent.VIRTUAL);
        MapValueContent actual2 = virtual.Combine(MapValueContent.STORABLE);
        assertEquals(MapValueContent.MIXED, actual1);
        assertEquals(MapValueContent.MIXED, actual2);
    }

    @Test
    void shouldReturnNone()
    {
        MapValueContent empty = MapValueContent.EMPTY;
        MapValueContent actual = empty.Combine(MapValueContent.EMPTY);
        assertEquals(MapValueContent.EMPTY, actual);
    }

    @Test
    void shouldReturnSame()
    {
        MapValueContent storable = MapValueContent.STORABLE;
        MapValueContent actual = storable.Combine(MapValueContent.STORABLE);
        assertEquals(MapValueContent.STORABLE, actual);
        actual = storable.Combine(MapValueContent.EMPTY);
        assertEquals(MapValueContent.STORABLE, actual);
    }
}
