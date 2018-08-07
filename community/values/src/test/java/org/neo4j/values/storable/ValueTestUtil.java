package org.neo4j.values.storable;

import org.neo4j.values.AnyValue;
import org.neo4j.values.virtual.VirtualValueTestUtil;

public class ValueTestUtil
{
    public static Value map( Object... keyOrVal )
    {
        assert keyOrVal.length % 2 == 0;
        String[] keys = new String[keyOrVal.length / 2];
        AnyValue[] values = new AnyValue[keyOrVal.length / 2];
        for ( int i = 0; i < keyOrVal.length; i += 2 )
        {
            keys[i / 2] = (String) keyOrVal[i];
            values[i / 2] = VirtualValueTestUtil.toAnyValue( keyOrVal[i + 1] );
        }
        return Values.map( keys, values );
    }
}
