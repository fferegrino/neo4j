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
package org.neo4j.cypher.internal.runtime.interpreted

import org.opencypher.v9_0.util.CypherTypeException
import org.opencypher.v9_0.util.test_helpers.CypherFunSuite
import org.neo4j.values.storable.Values._
import org.neo4j.values.storable.MapValueBuilder
import org.neo4j.values.virtual.VirtualValues.list

import scala.Array._

class MakeValuesNeoSafeTest extends CypherFunSuite {

  test("string collection turns into string array") {
    makeValueNeoSafe(list(stringValue("a"), stringValue("b"))) should equal(stringArray("a", "b"))
  }

  test("empty collection in is empty array") {
    makeValueNeoSafe(list()) should equal(stringArray())
  }

  test("retains type of primitive arrays") {
    Seq(longArray(emptyLongArray), shortArray(emptyShortArray), byteArray(emptyByteArray), intArray(emptyIntArray),
        doubleArray(emptyDoubleArray), floatArray(emptyFloatArray), booleanArray(emptyBooleanArray)).foreach { array =>

      makeValueNeoSafe(array) should equal(array)
    }
  }

  test("string arrays work") {
    val array = Array[String]()

    makeValueNeoSafe(stringArray()) should equal(stringArray())
  }

  test("mixed types are not ok") {
    intercept[CypherTypeException](makeValueNeoSafe(list(stringValue("a"), intValue(12), booleanValue(false))))
  }

  test("takes map with list as content") {
    val mapBuilder = new MapValueBuilder
    mapBuilder.add("l", list(longValue(1), longValue(2)))

    val containsVirtual = mapBuilder.build()

    val mapBuilderStorable = new MapValueBuilder()
    mapBuilderStorable.add("l", longArray(Array(1,2) ))
    val containsStorable = mapBuilderStorable.build()

    makeValueNeoSafe(containsVirtual) should equal(containsStorable)
  }

  test("takes nested map as content") {
    // Virtual
    val innerBuilderVirtual = new MapValueBuilder
    innerBuilderVirtual.add("v1", intValue(10))
    innerBuilderVirtual.add("l", list(longValue(1), longValue(2)))
    val innerMapVirtual = innerBuilderVirtual.build()

    val outerBuilderVirtual = new MapValueBuilder
    outerBuilderVirtual.add("inner", innerMapVirtual)
    val outerMapVirtual = outerBuilderVirtual.build()

    // Storables
    val innerBuilderStorable = new MapValueBuilder
    innerBuilderStorable.add("v1", intValue(10))
    innerBuilderStorable.add("l", longArray(Array(1,2) ))
    val innerMapStorable = innerBuilderStorable.build()

    val outerBuilderStorable = new MapValueBuilder()
    outerBuilderStorable.add("inner", innerMapStorable)
    val outerMapStorable = outerBuilderStorable.build()

    makeValueNeoSafe(outerMapVirtual) should equal(outerMapStorable)
  }
}
