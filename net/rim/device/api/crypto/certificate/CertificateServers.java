package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public final class CertificateServers implements Persistable {
   private Vector _ldapServers = new Vector();
   private Vector _ocspServers = new Vector();
   private Vector _crlServers = new Vector();
   private Vector _deletedServers = new Vector();
   public static final int LDAP_SERVER = 1;
   public static final int OCSP_SERVER = 2;
   public static final int CRL_SERVER = 3;
   public static final int DELETED_SERVER = 4;
   private static final long CERTIFICATE_OPTIONS = -3117817938665553109L;
   private static final long CERTIFICATE_COLLECTION = -7396257705664757140L;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(-3117817938665553109L);
   private static CertificateOptionsSyncCollection _collection;

   private CertificateServers() {
   }

   public static final synchronized CertificateServers getInstance() {
      return (CertificateServers)_persist.getContents();
   }

   public final void addCollectionListener(CollectionListener listener) {
      _collection.addCollectionListener(listener);
   }

   public final void removeCollectionListener(CollectionListener listener) {
      _collection.removeCollectionListener(listener);
   }

   public final CertificateServerInfo addServer(CertificateServerInfo server) {
      if (server == null) {
         throw new IllegalArgumentException();
      }

      this.getServerVector(server.getType()).addElement(server);
      _persist.commit();
      _collection.fireElementAdded(server);
      return server;
   }

   public final CertificateServerInfo addServer(String server, int type, String friendlyName) {
      return this.addServer(server, type, friendlyName, null, 0);
   }

   public final CertificateServerInfo addServer(String server, int type, String friendlyName, String baseQuery, int port) {
      return this.addServer(server, type, friendlyName, baseQuery, port, 0);
   }

   public final CertificateServerInfo addServer(String server, int type, String friendlyName, String baseQuery, int port, int authType) {
      return this.addServer(server, type, friendlyName, baseQuery, port, authType, 0);
   }

   public final CertificateServerInfo addServer(String server, int type, String friendlyName, String baseQuery, int port, int authType, int connectionType) {
      return this.addServer(new CertificateServerInfo(server, type, authType, friendlyName, baseQuery, port, connectionType));
   }

   public final boolean contains(CertificateServerInfo server) {
      if (server == null) {
         throw new IllegalArgumentException();
      } else {
         return this.getServerVector(server.getType()).contains(server);
      }
   }

   public final CertificateServerInfo elementAt(int type, int index) {
      return (CertificateServerInfo)this.getServerVector(type).elementAt(index);
   }

   public final boolean removeServer(CertificateServerInfo server) {
      if (server != null && server.getType() != 4) {
         boolean removed = this.removeServerFromVector(server);
         if (removed) {
            CertificateServerInfo serverInfo = server.clone();
            serverInfo.markDeleted();
            this._deletedServers.addElement(serverInfo);
            _collection.fireElementUpdated(server, serverInfo);
         }

         _persist.commit();
         return removed;
      } else {
         throw new IllegalArgumentException();
      }
   }

   final boolean internalRemoveServer(CertificateServerInfo server) {
      if (server == null) {
         throw new IllegalArgumentException();
      }

      boolean removed = this.removeServerFromVector(server);
      _persist.commit();
      _collection.fireElementRemoved(server);
      return removed;
   }

   private final boolean removeServerFromVector(CertificateServerInfo server) {
      return this.getServerVector(server.getType()).removeElement(server);
   }

   public final SyncObject[] getSyncObjects() {
      SyncObject[] array = new SyncObject[0];
      int arraySize = 0;
      Enumeration enumeration = null;

      for (int i = 1; i <= 4; i++) {
         for (Enumeration var5 = this.getServers(i); var5.hasMoreElements(); array[arraySize - 1] = (SyncObject)var5.nextElement()) {
            Array.resize(array, ++arraySize);
         }
      }

      return array;
   }

   public final SyncObject getSyncObject(int uid) {
      for (int i = 1; i <= 4; i++) {
         Enumeration enumeration = this.getServers(i);

         while (enumeration.hasMoreElements()) {
            SyncObject element = (SyncObject)enumeration.nextElement();
            if (element.getUID() == uid) {
               return element;
            }
         }
      }

      return null;
   }

   public final void removeAllElements() {
      this._ldapServers.removeAllElements();
      this._ocspServers.removeAllElements();
      this._crlServers.removeAllElements();
      _persist.commit();
   }

   final void internalRemoveAllElements() {
      this._deletedServers.removeAllElements();
      this.removeAllElements();
   }

   public final int getServerSize(int type) {
      return this.getServerVector(type).size();
   }

   public final Enumeration getServers(int type) {
      return this.getServerVector(type).elements();
   }

   private final Vector getServerVector(int type) {
      switch (type) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            return this._ldapServers;
         case 2:
            return this._ocspServers;
         case 3:
            return this._crlServers;
         case 4:
            return this._deletedServers;
      }
   }

   public static final CertificateServerInfo[] selectBestServers(String[] emailAddresses) {
      CertificateServers allServers = getInstance();
      if (allServers.getServerSize(1) != 0 && emailAddresses != null) {
         CertificateServerInfo[] bestServers = new CertificateServerInfo[0];

         for (String currentEmailAddress : emailAddresses) {
            addBestServers(currentEmailAddress, allServers, bestServers);
         }

         return bestServers.length > 0 ? bestServers : null;
      } else {
         return null;
      }
   }

   public final CertificateServerInfo[] getServerInfo(int type) {
      Enumeration servers = this.getServers(type);
      CertificateServerInfo[] serverInfo = new CertificateServerInfo[0];

      while (servers.hasMoreElements()) {
         Arrays.add(serverInfo, servers.nextElement());
      }

      return serverInfo;
   }

   private static final void addBestServers(String emailAddress, CertificateServers allServers, CertificateServerInfo[] bestServers) {
      String emailAddressFragment = emailAddress;

      boolean goodServerFound;
      do {
         emailAddressFragment = getServerFragment(emailAddressFragment);
         if (emailAddressFragment == null) {
            return;
         }

         goodServerFound = false;
         Enumeration enumeration = allServers.getServers(1);

         while (enumeration.hasMoreElements()) {
            CertificateServerInfo currentServer = (CertificateServerInfo)enumeration.nextElement();
            if (currentServer.getServer().indexOf(emailAddressFragment) >= 0 && !Arrays.contains(bestServers, currentServer)) {
               Arrays.add(bestServers, currentServer);
               goodServerFound = true;
            }
         }
      } while (!goodServerFound);
   }

   private static final String getServerFragment(String emailAddress) {
      int emailServerIndex = emailAddress.indexOf(64);
      if (emailServerIndex == -1) {
         int index = emailAddress.indexOf(46);
         return index != -1 && index != emailAddress.lastIndexOf(46) ? emailAddress.substring(index + 1) : null;
      } else {
         return emailAddress.substring(emailServerIndex + 1);
      }
   }

   static {
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new CertificateServers(), 4801362);
            _persist.commit();
         }
      }

      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _collection = (CertificateOptionsSyncCollection)registry.getOrWaitFor(-7396257705664757140L);
      if (_collection == null) {
         _collection = new CertificateOptionsSyncCollection();
         SyncManager.getInstance().enableSynchronization(_collection);
         registry.put(-7396257705664757140L, _collection);
      }
   }
}
