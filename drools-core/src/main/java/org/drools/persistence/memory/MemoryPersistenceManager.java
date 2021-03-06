package org.drools.persistence.memory;

import java.net.Inet4Address;
import java.util.Random;

import javax.transaction.xa.Xid;

import org.drools.persistence.ByteArraySnapshotter;
import org.drools.persistence.DroolsXid;
import org.drools.persistence.PersistenceManager;
import org.drools.persistence.Transaction;

public class MemoryPersistenceManager
    implements
    ByteArraySnapshotter,
    PersistenceManager {
    ByteArraySnapshotter  snapshotter;
    byte[]                lastSave;
    MemoryXaResource      xaResource;

    public MemoryPersistenceManager(ByteArraySnapshotter snapshotter) {
        this.snapshotter = snapshotter;
    }

    public MemoryXaResource getXAResource() {
        if ( xaResource == null ) {
            xaResource = new MemoryXaResource( this );
        }
        return xaResource;
    }

    public Transaction getTransaction() {
        return new Transaction( getUniqueXID(),
                                getXAResource() );
    }

    public void save() {
        if ( xaResource != null && xaResource.isInTransaction() ) {
            throw new RuntimeException( "You cannot call a persistence save point while a transaction is open" );
        }
        lastSave = getSnapshot();
    }

    public void load() {
        if ( xaResource != null && xaResource.isInTransaction() ) {
            throw new RuntimeException( "You cannot call a persistence save point while a transaction is open" );
        }
        loadSnapshot( lastSave );
    }

    public boolean isInTransaction() {
        return false;
    }

    public void setLastSave(byte[] lastSave) {
        this.lastSave = lastSave;
    }

    public byte[] getLastSave() {
        return lastSave;
    }

    public byte[] getSnapshot() {
        return snapshotter.getSnapshot();
    }

    public void loadSnapshot(byte[] bytes) {
        this.snapshotter.loadSnapshot( bytes );
    }

    byte[]      localIP     = null;
    private int txnUniqueID = 0;
    private int tid         = 1;

    private Xid getUniqueXID() {
        Random rnd = new Random( System.currentTimeMillis() );
        txnUniqueID++;
        int txnUID = txnUniqueID;
        int tidID = tid;
        int randID = rnd.nextInt();
        byte[] gtrid = new byte[64];
        byte[] bqual = new byte[64];

        if ( null == localIP ) {
            try {
                localIP = Inet4Address.getLocalHost().getAddress();
            } catch ( Exception ex ) {
                localIP = new byte[]{0x01, 0x02, 0x03, 0x04};
            }
        }

        System.arraycopy( localIP,
                          0,
                          gtrid,
                          0,
                          4 );
        System.arraycopy( localIP,
                          0,
                          bqual,
                          0,
                          4 );

        // Bytes 4 -> 7 - unique transaction id (unique to our class instance).          
        // Bytes 8 ->11 - thread id (unique to our thread).
        // Bytes 12->15 - random number generated using seed from current time in milliseconds.
        for ( int i = 0; i <= 3; i++ ) {
            gtrid[i + 4] = (byte) (txnUID % 0x100);
            bqual[i + 4] = (byte) (txnUID % 0x100);
            txnUID >>= 8;
            gtrid[i + 8] = (byte) (tidID % 0x100);
            bqual[i + 8] = (byte) (tidID % 0x100);
            tidID >>= 8;
            gtrid[i + 12] = (byte) (randID % 0x100);
            bqual[i + 12] = (byte) (randID % 0x100);
            randID >>= 8;
        }

        return new DroolsXid( 0x1234,
                              gtrid,
                              bqual );

    }
}
