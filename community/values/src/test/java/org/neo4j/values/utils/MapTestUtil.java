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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Class that provides utilities to work with java.util.Map
 */
public class MapTestUtil
{
    public static boolean customComparison( Map<String, Object> first, Map<String, Object> second )
    {
        boolean equal = first.equals(second);
        if ( equal )
        {
            return equal;
        }

        Set<String> firstKeys = first.keySet();
        Set<String> secondKeys = second.keySet();

        if ( !firstKeys.equals(secondKeys) )
        {
            return false;
        }

        for ( String keyInFirst : firstKeys )
        {
            Object firstObject = first.get(keyInFirst);
            Object secondObject = second.get(keyInFirst);

            equal = firstObject.equals(secondObject);
            if ( equal )
            {
                continue;
            }
            else
            {
                Class<?> firstClass = firstObject.getClass();
                equal = firstClass.equals(secondObject.getClass());
                if ( equal ) // Same type
                {
                    if ( firstClass.isArray() )
                    {
                        Class<?> arrayType = firstClass.getComponentType();
                        if ( arrayType == int.class )
                        {
                            equal =  Arrays.equals((int[])firstObject, (int[])secondObject);
                        }
                        else if ( arrayType == float.class )
                        {
                            equal =  Arrays.equals((float[])firstObject, (float[])secondObject);
                        }
                        else if ( arrayType == double.class )
                        {
                            equal = Arrays.equals((float[])firstObject, (float[])secondObject);
                        }
                        else if ( arrayType == char.class )
                        {
                            equal =  Arrays.equals((char[])firstObject, (char[])secondObject);
                        }
                        else if ( arrayType == byte.class )
                        {
                            equal =  Arrays.equals((byte[])firstObject, (byte[])secondObject);
                        }
                        else if ( arrayType == boolean.class )
                        {
                            equal =  Arrays.equals((boolean[])firstObject, (boolean[])secondObject);
                        }
                        else if ( arrayType == short.class )
                        {
                            equal = Arrays.equals((short[]) firstObject, (short[])secondObject);
                        }
                        else
                        {
                            equal = Arrays.equals((Object[]) firstObject, (Object[])secondObject);
                        }
                    }
                    else if ( firstObject instanceof Map )
                    {
                        equal = customComparison((Map<String, Object>) firstObject, (Map<String, Object>) secondObject);
                    }
                }
            }
            if ( !equal )
            {
                return false;
            }
        }
        return true;
    }
}
