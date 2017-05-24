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

import java.util.concurrent.Callable;

abstract class LazyIntegralArray<T> extends LazyArray<T> implements ValueGroup.VIntegerArray
{
    LazyIntegralArray( Callable<? extends T> producer )
    {
        super( producer );
    }

    @Override
    public boolean equals( char[] x )
    {
        return false;
    }

    @Override
    public boolean equals( String[] x )
    {
        return false;
    }

    @Override
    public boolean equals( boolean[] x )
    {
        return false;
    }

    @Override
    public int compareTo( ValueGroup.VIntegerArray other )
    {
        return NumberValues.compareIntegerArrays( this, other );
    }

    @Override
    public int compareTo( ValueGroup.VFloatingPointArray other )
    {
        return NumberValues.compareIntegerVsFloatArrays( this, other );
    }
}
