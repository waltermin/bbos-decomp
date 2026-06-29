package net.rim.device.cldc.io.devicehttp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.io.ConnectionCloseListener;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.io.http.AuthScheme;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Process;
import net.rim.vm.WeakReference;

public final class HttpConnectionManager implements GlobalEventListener, ConnectionCloseListener {
   private IntHashtable _processTable;
   private Object _cleanupSync;
   private boolean _cleanupRunning;
   private static final int TWO_MINUTES = 120000;
   private static final int FIFTEEN_MINUTES = 900000;
   private static final long APP_REGISTRY_KEY = -1270498631466223615L;

   public final synchronized void setMaxNumberConnections(int value) {
      int pid = Process.currentProcess().getProcessId();
      HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
      if (pc == null) {
         pc = new HttpConnectionManager$ProcessCollection(null);
         this._processTable.put(pid, pc);
      }

      pc._maxNumberConnections = value;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void closeConnections() {
      int pid = Process.currentProcess().getProcessId();
      HttpConnectionManager$ProcessCollection collection = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
      if (collection != null) {
         Enumeration enumeration = collection._connections.elements();

         while (enumeration.hasMoreElements()) {
            HttpConnectionManager$Connection connection = (HttpConnectionManager$Connection)enumeration.nextElement();
            boolean var7 = false /* VF: Semaphore variable */;

            try {
               var7 = true;
               this.closeConnection(collection, connection);
               var7 = false;
            } finally {
               if (var7) {
                  collection._connections.removeValue(connection);
                  continue;
               }
            }
         }
      }
   }

   public final ClientProtocol getConnection(String host, int mode, boolean timeouts, URL url, boolean explicitProxy, boolean useHttp11, ServiceRecord record) {
      int pid = Process.currentProcess().getProcessId();
      SocketConnection socketConnection = null;
      InputStream in = null;
      OutputStream out = null;
      synchronized (this) {
         HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
         if (pc == null) {
            pc = new HttpConnectionManager$ProcessCollection(null);
            this._processTable.put(pid, pc);
         }

         int freeConnections = pc._connections.size();
         int count = freeConnections;
         int size = pc._inUseConnections.size();

         for (int i = 0; i < size; i++) {
            WeakReference wr = (WeakReference)pc._inUseConnections.elementAt(i);
            if (wr.get() == null) {
               pc._inUseConnections.removeElementAt(i);
               i--;
               size--;
            } else {
               count++;
            }
         }

         long waitTime = 5000;

         while (true) {
            Enumeration enumeration = pc._connections.elements(host);
            if (enumeration.hasMoreElements()) {
               EventLogger.logEvent(711994783830004691L, 1214473827, 5);
               HttpConnectionManager$Connection connection = (HttpConnectionManager$Connection)enumeration.nextElement();
               socketConnection = connection._socketConnection;
               in = connection._inputStream;
               out = connection._outputStream;
               if (((ConnectionCloseProvider)socketConnection).isConnectionEstablished()) {
                  ((ConnectionCloseProvider)socketConnection).setConnectionCloseListener(null);
                  pc._connections.removeValue(host, connection);
                  break;
               }

               EventLogger.logEvent(711994783830004691L, 1214473060, 5);
               this.closeConnection(pc, connection);
               socketConnection = null;
               in = null;
               out = null;
               freeConnections--;
               count--;
            } else {
               if (waitTime > 0 && count >= pc._maxNumberConnections) {
                  if (freeConnections < pc._maxNumberConnections) {
                     EventLogger.logEvent(711994783830004691L, 1214478177, 5);

                     try {
                        long timeBefore = System.currentTimeMillis();
                        this.wait(waitTime);
                        waitTime -= System.currentTimeMillis() - timeBefore;
                        continue;
                     } finally {
                        continue;
                     }
                  }

                  EventLogger.logEvent(711994783830004691L, 1214474348, 5);
                  HttpConnectionManager$Connection oldestConnection = null;
                  Enumeration connectionEnum = pc._connections.elements();

                  while (connectionEnum.hasMoreElements()) {
                     HttpConnectionManager$Connection connection = (HttpConnectionManager$Connection)connectionEnum.nextElement();
                     if (oldestConnection == null || connection._allocationTime < oldestConnection._allocationTime) {
                        oldestConnection = connection;
                     }
                  }

                  if (oldestConnection != null) {
                     this.closeConnection(pc, oldestConnection);
                  }
                  break;
               }

               EventLogger.logEvent(711994783830004691L, 1214476147, 5);
               break;
            }
         }
      }

      if (socketConnection == null) {
         EventLogger.logEvent(711994783830004691L, 1214476144, 5);
         socketConnection = (SocketConnection)Connector.open(host, mode, timeouts);
         in = socketConnection.openInputStream();
         out = socketConnection.openOutputStream();
      } else {
         EventLogger.logEvent(711994783830004691L, 1214475634, 5);
      }

      synchronized (this) {
         HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
         if (pc == null) {
            pc = new HttpConnectionManager$ProcessCollection(null);
            this._processTable.put(pid, pc);
         }

         pc._inUseConnections.addElement(new WeakReference(socketConnection));
      }

      try {
         return socketConnection instanceof SecureConnection
            ? new net.rim.device.cldc.io.devicehttps.ClientProtocol(
               host, url, (SecureConnection)socketConnection, in, out, explicitProxy, useHttp11, mode, timeouts, true, record, true
            )
            : new ClientProtocol(host, url, socketConnection, in, out, explicitProxy, useHttp11, mode, timeouts, true, record, true);
      } finally {
         ;
      }
   }

   public final synchronized boolean addConnection(String host, SocketConnection socket, InputStream in, OutputStream out) {
      int pid = Process.currentProcess().getProcessId();
      HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
      if (pc == null) {
         pc = new HttpConnectionManager$ProcessCollection(null);
         this._processTable.put(pid, pc);
      }

      int count = 0;
      int size = pc._inUseConnections.size();

      for (int i = 0; i < size; i++) {
         WeakReference wr = (WeakReference)pc._inUseConnections.elementAt(i);
         Object obj = wr.get();
         if (obj != null && obj != socket) {
            count++;
         } else {
            pc._inUseConnections.removeElementAt(i);
            i--;
            size--;
         }
      }

      if (count + pc._connections.size() > pc._maxNumberConnections) {
         return false;
      }

      if (!(socket instanceof ConnectionCloseProvider)) {
         return false;
      }

      if (!((ConnectionCloseProvider)socket).connectionStatusAvailable()) {
         return false;
      }

      ((ConnectionCloseProvider)socket).setConnectionCloseListener(this);
      pc._connections.add(host, new HttpConnectionManager$Connection(socket, in, out));
      this.ensureCleanupIsRunning();
      this.notify();
      return true;
   }

   public final synchronized AuthScheme getAuthScheme(String host) {
      int pid = Process.currentProcess().getProcessId();
      HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
      if (pc == null) {
         pc = new HttpConnectionManager$ProcessCollection(null);
         this._processTable.put(pid, pc);
      }

      return (AuthScheme)pc._authSchemes.get(host);
   }

   public final synchronized void addAuthScheme(String host, AuthScheme authScheme) {
      if (authScheme != null) {
         int pid = Process.currentProcess().getProcessId();
         HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
         if (pc == null) {
            pc = new HttpConnectionManager$ProcessCollection(null);
            this._processTable.put(pid, pc);
         }

         pc._authSchemes.put(host, authScheme);
         pc._authTime = System.currentTimeMillis();
      }
   }

   public final synchronized void removeActiveConnection(SocketConnection socket) {
      int pid = Process.currentProcess().getProcessId();
      HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pid);
      if (pc == null) {
         pc = new HttpConnectionManager$ProcessCollection(null);
         this._processTable.put(pid, pc);
      }

      int size = pc._inUseConnections.size();

      for (int i = 0; i < size; i++) {
         WeakReference wr = (WeakReference)pc._inUseConnections.elementAt(i);
         Object obj = wr.get();
         if (obj == null || obj == socket) {
            pc._inUseConnections.removeElementAt(i);
            i--;
            size--;
         }
      }

      this.ensureCleanupIsRunning();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -1270659756336956134L && object0 instanceof int[]) {
         boolean found = false;
         int[] pids = (int[])object0;

         for (int i = 0; i < pids.length; i++) {
            HttpConnectionManager$ProcessCollection pc = (HttpConnectionManager$ProcessCollection)this._processTable.get(pids[i]);
            if (pc != null) {
               pc._alive = false;
               found = true;
            }
         }

         if (found) {
            synchronized (this._cleanupSync) {
               this.ensureCleanupIsRunning();
               this._cleanupSync.notify();
               return;
            }
         }
      }
   }

   @Override
   public final void connectionClosed(ConnectionCloseProvider socketConnection) {
      synchronized (this._cleanupSync) {
         this._cleanupSync.notify();
      }
   }

   public static final HttpConnectionManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      HttpConnectionManager mgr = (HttpConnectionManager)ar.getOrWaitFor(-1270498631466223615L);
      if (mgr == null) {
         mgr = new HttpConnectionManager();
         ar.put(-1270498631466223615L, mgr);
      }

      return mgr;
   }

   private HttpConnectionManager() {
      EventLogger.register(711994783830004691L, "net.rim.http", 2);
      this._processTable = new IntHashtable();
      this._cleanupSync = new Object();
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private final void closeConnection(HttpConnectionManager$ProcessCollection param1, HttpConnectionManager$Connection param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 2
      // 01: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$Connection._inputStream Ljava/io/InputStream;
      // 04: ifnull 12
      // 07: aload 2
      // 08: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$Connection._inputStream Ljava/io/InputStream;
      // 0b: invokevirtual java/io/InputStream.close ()V
      // 0e: goto 12
      // 11: astore 3
      // 12: aload 2
      // 13: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$Connection._outputStream Ljava/io/OutputStream;
      // 16: ifnull 24
      // 19: aload 2
      // 1a: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$Connection._outputStream Ljava/io/OutputStream;
      // 1d: invokevirtual java/io/OutputStream.close ()V
      // 20: goto 24
      // 23: astore 3
      // 24: aload 2
      // 25: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$Connection._socketConnection Ljavax/microedition/io/SocketConnection;
      // 28: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2d: aload 1
      // 2e: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$ProcessCollection._connections Lnet/rim/device/api/util/MultiMap;
      // 31: aload 2
      // 32: invokevirtual net/rim/device/api/util/MultiMap.removeValue (Ljava/lang/Object;)Z
      // 35: pop
      // 36: return
      // 37: astore 3
      // 38: aload 1
      // 39: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$ProcessCollection._connections Lnet/rim/device/api/util/MultiMap;
      // 3c: aload 2
      // 3d: invokevirtual net/rim/device/api/util/MultiMap.removeValue (Ljava/lang/Object;)Z
      // 40: pop
      // 41: return
      // 42: astore 4
      // 44: aload 1
      // 45: getfield net/rim/device/cldc/io/devicehttp/HttpConnectionManager$ProcessCollection._connections Lnet/rim/device/api/util/MultiMap;
      // 48: aload 2
      // 49: invokevirtual net/rim/device/api/util/MultiMap.removeValue (Ljava/lang/Object;)Z
      // 4c: pop
      // 4d: aload 4
      // 4f: athrow
      // try (3 -> 6): 7 null
      // try (11 -> 14): 15 null
      // try (0 -> 19): 25 null
      // try (0 -> 19): 32 null
      // try (25 -> 26): 32 null
      // try (32 -> 33): 32 null
   }

   private final void ensureCleanupIsRunning() {
      synchronized (this._cleanupSync) {
         if (!this._cleanupRunning) {
            this._cleanupRunning = true;
            Proxy.getInstance().startThread(new HttpConnectionManager$CleanupThread(this, null));
         }
      }
   }
}
