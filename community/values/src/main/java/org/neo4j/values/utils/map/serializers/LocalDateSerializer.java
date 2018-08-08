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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateSerializer extends StdSerializer<LocalDate>
{
    public static final String TYPE_NAME = "LocalDate";

    public LocalDateSerializer()
    {
        super( LocalDate.class );
    }

    @Override
    public void serialize( LocalDate localDate, JsonGenerator jsonGenerator,
                           SerializerProvider serializerProvider ) throws IOException
    {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(CustomMapSerializer.TYPE_FIELD_NAME, TYPE_NAME);
        jsonGenerator.writeNumberField("_year", localDate.getYear());
        jsonGenerator.writeNumberField("_month", localDate.getMonthValue());
        jsonGenerator.writeNumberField("_day", localDate.getDayOfMonth());
        jsonGenerator.writeEndObject();
    }
}
