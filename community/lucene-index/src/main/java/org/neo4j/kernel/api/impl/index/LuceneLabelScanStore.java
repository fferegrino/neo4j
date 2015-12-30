/*
 * Copyright (c) 2002-2016 "Neo Technology,"
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
package org.neo4j.kernel.api.impl.index;

import org.apache.lucene.store.LockObtainFailedException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.kernel.api.labelscan.LabelScanStore;
import org.neo4j.kernel.api.labelscan.LabelScanWriter;
import org.neo4j.kernel.impl.api.scan.LabelScanStoreProvider.FullStoreChangeStream;
import org.neo4j.kernel.impl.store.UnderlyingStorageException;
import org.neo4j.logging.Log;
import org.neo4j.logging.LogProvider;
import org.neo4j.storageengine.api.schema.LabelScanReader;

public class LuceneLabelScanStore implements LabelScanStore
{
    public static final String INDEX_IDENTIFIER = "labelStore";

    private final LuceneLabelScanIndex luceneIndex;
    // We get in a full store stream here in case we need to fully rebuild the store if it's missing or corrupted.
    private final FullStoreChangeStream fullStoreStream;
    private final Log log;
    private final Monitor monitor;
    private boolean needsRebuild;
    private final Lock lock = new ReentrantLock( true );

    public interface Monitor
    {
        Monitor EMPTY = new Monitor()
        {
            @Override
            public void init()
            {
            }

            @Override
            public void noIndex()
            {
            }

            @Override
            public void lockedIndex( LockObtainFailedException e )
            {
            }

            @Override
            public void corruptIndex( IOException e )
            {
            }

            @Override
            public void rebuilding()
            {
            }

            @Override
            public void rebuilt( long roughNodeCount )
            {
            }
        };

        void init();

        void noIndex();

        void lockedIndex( LockObtainFailedException e );

        void corruptIndex( IOException e );

        void rebuilding();

        void rebuilt( long roughNodeCount );
    }

    public LuceneLabelScanStore( LuceneLabelScanIndex luceneIndex, FullStoreChangeStream fullStoreStream,
            LogProvider logProvider, Monitor monitor )
    {
        this.luceneIndex = luceneIndex;
        this.fullStoreStream = fullStoreStream;
        this.log = logProvider.getLog( getClass() );
        this.monitor = monitor;
    }

    @Override
    public void force()
    {
        try
        {
            luceneIndex.flush();
        }
        catch ( IOException e )
        {
            throw new UnderlyingStorageException( e );
        }
    }

    @Override
    public LabelScanReader newReader()
    {
        return luceneIndex.getLabelScanReader();
    }

    @Override
    public ResourceIterator<File> snapshotStoreFiles() throws IOException
    {
        return luceneIndex.snapshot();
    }

    @Override
    public void init() throws IOException
    {
        monitor.init();
        try
        {
            if ( !luceneIndex.exists() )
            {
                log.info( "No lucene scan store index found, this might just be first use. Preparing to rebuild." );
                monitor.noIndex();

                luceneIndex.create();
                needsRebuild = true;
            }
            else if ( !luceneIndex.isValid() )
            {
                // todo: rebuild instead of failing? failing is here now only because there is a test expecting failure...
//                monitor.corruptIndex(  );
//                luceneIndex.create();
//                needsRebuild = true;

                log.warn( "Lucene scan store index could not be read. Preparing to rebuild." );

                throw new IOException( "Label scan store could not be read, and needs to be rebuilt. " +
                                       "To trigger a rebuild, ensure the database is stopped, delete the files in '" +
                                       luceneIndex + "', and then start the database again." );
            }

            // todo: test this strange open-close thingy
            luceneIndex.open();
        }
        catch ( LockObtainFailedException e )
        {
            luceneIndex.close();
            log.error( "Index is locked by another process or database", e );
            monitor.lockedIndex( e );
            throw e;
        }
    }

    @Override
    public void start() throws IOException
    {
        if ( needsRebuild )
        {   // we saw in init() that we need to rebuild the index, so do it here after the
            // neostore has been properly started.
            monitor.rebuilding();
            log.info( "Rebuilding lucene scan store, this may take a while" );
            long numberOfNodes = rebuild();
            monitor.rebuilt( numberOfNodes );
            log.info( "Lucene scan store rebuilt (roughly " + numberOfNodes + " nodes)" );
            needsRebuild = false;
        }
    }

    private long rebuild() throws IOException
    {
        try ( LabelScanWriter writer = newWriter() )
        {
            return fullStoreStream.applyTo( writer );
        }
    }

    @Override
    public void stop()
    {   // Not needed
    }

    @Override
    public void shutdown() throws IOException
    {
        luceneIndex.close();
    }

    @Override
    public LabelScanWriter newWriter()
    {
        // Only a single writer is allowed at any point in time. For that this lock is used and passed
        // onto the writer to release in its close()
        lock.lock();
        return luceneIndex.getLabelScanWriter(lock);
    }
}
