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
package org.neo4j.values.utils.map.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.neo4j.values.storable.DurationValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

public class CustomMapSerializer extends ObjectMapper
{
    public static final String TYPE_FIELD_NAME = "__t__";

    public CustomMapSerializer()
    {
        SimpleModule module = new SimpleModule( "CustomMapSerializer" );

        module.addSerializer( LocalDate.class, new LocalDateSerializer() );
        module.addSerializer( LocalDateTime.class, new LocalDateTimeSerializer() );
        module.addSerializer( LocalTime.class, new LocalTimeSerializer() );
        module.addSerializer( OffsetTime.class, new OffsetTimeSerializer() );
        module.addSerializer( ZonedDateTime.class, new ZonedDateTimeSerializer() );
        module.addSerializer( DurationValue.class, new DurationValueSerializer() );

        this.registerModule( module );
    }
}
