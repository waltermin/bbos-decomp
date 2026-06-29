package net.rim.device.api.io;

import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.CyclicQueue;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

final class DatagramReceiveThread extends Thread implements ConnectionListener {
   private Hashtable _connections = new Hashtable();
   private CyclicQueue _queue = new CyclicQueue(32);
   private static final long ID;

   public static final DatagramReceiveThread getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DatagramReceiveThread ret = (DatagramReceiveThread)ar.getOrWaitFor(-4931091334314128113L);
      if (ret == null) {
         ret = new DatagramReceiveThread();
         ProtocolDaemon.getInstance().startThread(ret);
         ar.put(-4931091334314128113L, ret);
      }

      return ret;
   }

   public final void addConnection(DatagramConnectionBase connection, DatagramTransportBase transport) {
      this._connections.put(connection, transport);
      connection.setConnectionListener(this);
   }

   @Override
   public final void dataAvailable(Connection connection) {
      synchronized (this._queue) {
         this._queue.enqueue(connection);
         this._queue.notify();
      }
   }

   @Override
   public final void run() {
      while (true) {
         try {
            DatagramConnectionBase connection;
            synchronized (this._queue) {
               if (this._queue.isEmpty()) {
                  this._queue.wait();
               }

               connection = (DatagramConnectionBase)this._queue.dequeue();
            }

            Datagram datagram = connection.newDatagram(0);
            connection.receive(datagram);
            ((DatagramTransportBase)this._connections.get(connection)).superProcessReceivedDatagram(datagram);
         } catch (IOException var11) {
         } catch (Throwable var12) {
         } finally {
            DatagramConnectionBase connection = null;
            Datagram datagram = null;
         }
      }
   }
}
