package net.rim.device.cldc.io.ippp;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Datagram;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.ServiceBookNotFoundException;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.vm.WeakReference;

public class SocketTransportBase extends IPPPTransportBase implements GlobalEventListener {
   private Hashtable _connections = (Hashtable)(new Object());
   private Cache _cache;
   private IntHashtable _notifierConnections;
   private IntHashtable _establishedConnections;
   private int _nextConnectionId;
   private static final int EVENT_LOG_NO_UID;
   private static final int MAXIMUM_CACHE_SIZE;
   private static final int MAXIMUM_QUEUE_SIZE;
   private static final int MAXIMUM_PENDING_SIZE;
   private static final long IPPP_TIMEOUT;
   private static final int IPPP_DATA_SIZE;
   private static final String STRING_ConnectionUID;
   private static final String STRING_ConnectionType;
   public static final int IPPP_TYPE_CORPORATE;
   public static final int IPPP_TYPE_PUBLIC;
   public static final int IPPP_TYPE_PROVISIONING;

   public SocketTransportBase() {
      for (ServiceRecord rec : ServiceBook.getSB().findRecordsByCid("IPPP")) {
         try {
            this._connections.put(StringUtilities.toLowerCase(rec.getUid(), 1701707776), new SocketTransportBase$SocketTransportBaseConnection(this, rec));
         } finally {
            continue;
         }
      }

      this._notifierConnections = (IntHashtable)(new Object());
      this._cache = new Cache(this.getCacheSize(null), this.getPendingSize(null));
      this._establishedConnections = (IntHashtable)(new Object());
      this._nextConnectionId = RandomSource.getInt() & 2147483647;
      ProtocolDaemon.getInstance().addGlobalEventListener(this);
   }

   public static String findAcceptableConnectionUid(URLParameters params) {
      if (params != null) {
         if (params.containParameter("ConnectionUID")) {
            return params.getValue("ConnectionUID");
         }

         if (params.containParameter("ConnectionType")) {
            String connectionType = params.getValue("ConnectionType");
            if (StringUtilities.strEqualIgnoreCase("mds-public", connectionType, 1701707776)) {
               return getFirstUidByIpppType(1);
            }

            if (StringUtilities.strEqualIgnoreCase("mds-corporate", connectionType, 1701707776)) {
               return getFirstUidByIpppType(0);
            }

            if (StringUtilities.strEqualIgnoreCase("provisioning", connectionType, 1701707776)) {
               return getFirstUidByIpppType(2);
            }
         }
      }

      return getFirstUidByIpppType(0);
   }

   public static String getFirstUidByIpppType(int type) {
      try {
         Enumeration elements = ((SocketTransportBase)TransportRegistry.get("net.rim.device.cldc.io.ippp.Transport"))._connections.elements();

         while (elements.hasMoreElements()) {
            SocketTransportBase$SocketTransportBaseConnection conn = (SocketTransportBase$SocketTransportBaseConnection)elements.nextElement();
            if (type == conn.getIpppType()) {
               return conn._uid;
            }
         }
      } finally {
         return null;
      }

      return null;
   }

   public static int getTypeByUid(String uid) {
      try {
         Enumeration elements = ((SocketTransportBase)TransportRegistry.get("net.rim.device.cldc.io.ippp.Transport"))._connections.elements();

         while (elements.hasMoreElements()) {
            SocketTransportBase$SocketTransportBaseConnection conn = (SocketTransportBase$SocketTransportBaseConnection)elements.nextElement();
            if (StringUtilities.strEqualIgnoreCase(uid, conn._uid, 1701707776)) {
               return conn.getIpppType();
            }
         }
      } finally {
         return -1;
      }

      return -1;
   }

   @Override
   public Datagram newDatagram(byte[] buf, int offset, int length, String addr) {
      return new SocketDatagram(buf, offset, length, addr);
   }

   public boolean registerNotifierConnection(short port, DatagramListener listener) {
      WeakReferenceUtilities.purge(this._notifierConnections);
      WeakReference weak = (WeakReference)this._notifierConnections.get(port);
      if (weak != null && weak.get() != null) {
         return false;
      }

      weak = (WeakReference)(new Object(listener));
      this._notifierConnections.put(port, weak);
      return true;
   }

   public void deregisterNotifierConnection(short port) {
      this._notifierConnections.remove(port);
      if (this._notifierConnections.size() == 0) {
         this._cache.clear();
      } else {
         this._cache.remove(port);
      }
   }

   public boolean registerEstablishedConnection(int connectionIdentifier, DatagramListener listener) {
      WeakReferenceUtilities.purge(this._establishedConnections);
      WeakReference weak = (WeakReference)this._establishedConnections.get(connectionIdentifier);
      if (weak != null && weak.get() != null) {
         return false;
      }

      weak = (WeakReference)(new Object(listener));
      this._establishedConnections.put(connectionIdentifier, weak);
      this._cache.remove(connectionIdentifier);
      return true;
   }

   public void deregisterEstablishedConnection(int connectionIdentifier) {
      WeakReference weak = (WeakReference)this._establishedConnections.remove(connectionIdentifier);
      if (weak != null) {
         Object object = weak.get();
         if (object != null) {
            synchronized (object) {
               object.notifyAll();
               return;
            }
         }
      }
   }

   @Override
   protected boolean passUpDatagram(Datagram datagram) {
      boolean foundListener = false;
      SocketDatagram socketDatagram = (SocketDatagram)datagram;
      String uid = socketDatagram.getGroupUID();
      if (uid == null) {
         EventLogger.logEvent(6406224406390975741L, 86347381, 0);
         return false;
      }

      uid = StringUtilities.toLowerCase(uid, 1701707776);
      if (this._connections.get(uid) == null) {
         EventLogger.logEvent(6406224406390975741L, ((StringBuffer)(new Object("RXnf "))).append(uid).toString().getBytes(), 0);
         return false;
      }

      Queue queue = this._cache.get(socketDatagram.getConnectionID());
      int connectionId = socketDatagram.getConnectionID();
      WeakReference weak = (WeakReference)this._establishedConnections.get(connectionId);
      if (weak != null) {
         DatagramListener listener = (DatagramListener)weak.get();
         if (listener != null) {
            foundListener = true;
            listener.dataReceived(socketDatagram);
         } else {
            this._establishedConnections.remove(connectionId);
         }
      }

      if (!foundListener && socketDatagram.isServerConnection()) {
         if (queue == null) {
            queue = new Queue(socketDatagram.getConnectionID(), this.getQueueSize(null));
            queue.setPort(socketDatagram.getPort());
            if (!this._cache.put(queue)) {
               return false;
            }
         }

         queue.put(socketDatagram.getSequence(), socketDatagram);
         if (socketDatagram.isConnectRequest()) {
            short port = socketDatagram.getPort();
            if (!this._cache.update(queue, port)) {
               return false;
            }

            weak = (WeakReference)this._notifierConnections.get(port);
            if (weak != null) {
               DatagramListener listener = (DatagramListener)weak.get();
               if (listener != null) {
                  listener.connectRequestReceived(socketDatagram);
                  foundListener = true;
               } else {
                  this._notifierConnections.remove(port);
               }
            }

            if (!foundListener) {
               this._cache.remove(socketDatagram.getConnectionID());
            }
         }
      }

      return foundListener;
   }

   public int getNextConnectionId() {
      if (++this._nextConnectionId < 0) {
         this._nextConnectionId = 0;
      }

      return this._nextConnectionId;
   }

   public boolean isConnectionCached(short port) {
      return this._cache.contains(port);
   }

   public int getConnectionID(short port) {
      return this._cache.getConnectionID(port);
   }

   public Cache getCache() {
      return this._cache;
   }

   public Queue removeQueue(int connectionID) {
      return this._cache.remove(connectionID);
   }

   public int getCacheSize(String uid) {
      try {
         SocketTransportBase$SocketTransportBaseConnection c = this.getConnection(uid);
         return c.getCacheSize();
      } finally {
         ;
      }
   }

   public int getQueueSize(String uid) {
      try {
         SocketTransportBase$SocketTransportBaseConnection c = this.getConnection(uid);
         return c.getQueueSize();
      } finally {
         ;
      }
   }

   public int getPendingSize(String uid) {
      try {
         SocketTransportBase$SocketTransportBaseConnection c = this.getConnection(uid);
         return c.getPendingSize();
      } finally {
         ;
      }
   }

   public long getTimeout(String uid) {
      try {
         SocketTransportBase$SocketTransportBaseConnection c = this.getConnection(uid);
         return c.getTimeout();
      } finally {
         return 130000;
      }
   }

   public int getDataSize(String uid) {
      try {
         SocketTransportBase$SocketTransportBaseConnection c = this.getConnection(uid);
         return c.getDataSize();
      } finally {
         ;
      }
   }

   @Override
   public synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      super.eventOccurred(guid, data0, data1, object0, object1);
      if (guid == -4220058463650496006L) {
         try {
            ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
            if (sr != null && sr.getType() == 0 && StringUtilities.strEqualIgnoreCase("IPPP", sr.getCid(), 1701707776)) {
               String uid = StringUtilities.toLowerCase(sr.getUid(), 1701707776);
               SocketTransportBase$SocketTransportBaseConnection temp = (SocketTransportBase$SocketTransportBaseConnection)this._connections.get(uid);
               if (temp != null) {
                  this.clearAllConnections();
               }

               this._connections.put(uid, new SocketTransportBase$SocketTransportBaseConnection(this, sr));
            }
         } finally {
            return;
         }
      } else if (guid == 2522898683889177438L) {
         Enumeration elements = this._connections.elements();

         while (elements.hasMoreElements()) {
            SocketTransportBase$SocketTransportBaseConnection conn = (SocketTransportBase$SocketTransportBaseConnection)elements.nextElement();
            if (conn._applicationData != null && data0 == conn._applicationData.getServiceRecordId()) {
               this.clearAllConnections();
               this._connections.remove(StringUtilities.toLowerCase(conn._uid, 1701707776));
               return;
            }
         }
      } else if (guid == 8288627527798139133L) {
         label150:
         try {
            ServiceBook serviceBook = ServiceBook.getSB();
            ServiceRecord sr = serviceBook.getRecordById(data0);
            if (sr != null && StringUtilities.strEqualIgnoreCase("IPPP", sr.getCid(), 1701707776)) {
               if (sr.getType() != 0) {
                  if (sr.getType() == 2) {
                     String uid = StringUtilities.toLowerCase(sr.getUid(), 1701707776);
                     SocketTransportBase$SocketTransportBaseConnection temp = (SocketTransportBase$SocketTransportBaseConnection)this._connections.get(uid);
                     if (temp != null) {
                        this.clearAllConnections();
                        this._connections.remove(uid);
                     }
                  }
               } else {
                  String uid = StringUtilities.toLowerCase(sr.getUid(), 1701707776);
                  SocketTransportBase$SocketTransportBaseConnection temp = (SocketTransportBase$SocketTransportBaseConnection)this._connections.get(uid);
                  if (temp != null) {
                     temp.update();
                  } else {
                     this._connections.put(uid, new SocketTransportBase$SocketTransportBaseConnection(this, sr));
                  }

                  if (object1 instanceof ServiceRecord) {
                     String originalUid = ((ServiceRecord)object1).getUid();
                     if (originalUid != null) {
                        originalUid = StringUtilities.toLowerCase(originalUid, 1701707776);
                        if (serviceBook.getRecordByUidAndCid(originalUid, "IPPP") == null && this._connections.get(uid) != null) {
                           this.clearAllConnections();
                           this._connections.remove(originalUid);
                        }
                     }
                  }
               }
            }
         } finally {
            ;
         }
      }
   }

   private SocketTransportBase$SocketTransportBaseConnection getConnection(String uid) {
      if (uid == null) {
         ServiceRecord[] elements = ServiceBook.getSB().findRecordsByCid("IPPP");
         if (elements.length > 0) {
            uid = elements[0].getUid();
         }
      }

      if (uid == null) {
         throw new ServiceBookNotFoundException("IPPP");
      } else {
         SocketTransportBase$SocketTransportBaseConnection connection = (SocketTransportBase$SocketTransportBaseConnection)this._connections
            .get(StringUtilities.toLowerCase(uid, 1701707776));
         if (connection == null) {
            throw new ServiceBookNotFoundException("IPPP");
         } else {
            return connection;
         }
      }
   }

   private void clearAllConnections() {
      IntEnumeration enumeration = this._establishedConnections.keys();

      while (enumeration.hasMoreElements()) {
         int key = enumeration.nextElement();
         WeakReference weak = (WeakReference)this._establishedConnections.remove(key);
         if (weak != null) {
            Object object = weak.get();
            if (object != null) {
               synchronized (object) {
                  object.notifyAll();
               }
            }
         }
      }
   }
}
