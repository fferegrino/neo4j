/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
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
package org.neo4j.values;

import java.util.Arrays;

import static java.lang.String.format;

final class DirectIntArray extends DirectIntegralArray
{
    private final int[] value;

    DirectIntArray( int[] value )
    {
        assert value != null;
        this.value = value;
    }

    @Override
    public int length()
    {
        return value.length;
    }

    @Override
    public long longValue( int index )
    {
        return value[index];
    }

    @Override
    public boolean equals( byte[] x )
    {
        return PrimitiveArrayValues.equals( x, value );
    }

    @Override
    public boolean equals( short[] x )
    {
        return PrimitiveArrayValues.equals( x, value );
    }

    @Override
    public boolean equals( int[] x )
    {
        return Arrays.equals( value, x );
    }

    @Override
    public boolean equals( long[] x )
    {
        return PrimitiveArrayValues.equals( value, x );
    }

    @Override
    public boolean equals( float[] x )
    {
        return PrimitiveArrayValues.equals( value, x );
    }

    @Override
    public boolean equals( double[] x )
    {
        return PrimitiveArrayValues.equals( value, x );
    }

    @Override
    public void writeTo( ValueWriter writer )
    {
        PrimitiveArrayWriting.writeTo( writer, value );
    }

    @Override
    public Object asPublic()
    {
        return value.clone();
    }

    @Override
    public String toString()
    {
        return format( "IntArray(%s)", Arrays.toString( value ) );
    }
}
