package net.rim.device.cldc.io.sync;

import java.io.IOException;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.CyclicQueue;
import net.rim.device.api.util.IntLongHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.gme.GMEAddress;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.cldc.io.gme.GMETarget;
import net.rim.device.cldc.io.sync.command.Status;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.synchronization.ota.OTASyncDaemon;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;
import net.rim.vm.WeakReference;

public final class Transport extends DatagramTransportBase implements ServiceRoutingListener2, DatagramStatusListener, Runnable {
   private ServiceRouting _serviceRouting;
   private ReusableObjectPool _syncDatagramsPool;
   private LongHashtable _connections = new LongHashtable();
   private IntLongHashtable _transactionIdToSid = new IntLongHashtable();
   private CyclicQueue _receivedGMEDatagrams = new CyclicQueue();

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final int send(Datagram aDatagram, boolean checkInSyncDatagram) {
      SyncDatagram xSyncDatagram = (SyncDatagram)aDatagram;
      boolean var13 = false /* VF: Semaphore variable */;

      int var10;
      try {
         var13 = true;
         GMEDatagram xGMEDatagram = (GMEDatagram)super._subConnection.newDatagram(super._subConnection.getNominalLength());
         long sid = xSyncDatagram.getSid();
         String xServiceUid = this.getServiceUidMappedFor(sid);
         if (xServiceUid == null) {
            Logger.logTransportationDropDatagram("Tx", xSyncDatagram);
            throw new IOException();
         }

         xGMEDatagram.setAddress("sync/" + xServiceUid);
         int xSyncDatagramSize = xGMEDatagram.getPosition();
         xSyncDatagram.writeTo(xGMEDatagram);
         xGMEDatagram.setLength(xGMEDatagram.getPosition());
         xSyncDatagramSize = xGMEDatagram.getLength() - xSyncDatagramSize;
         Logger.logTransportationRxTx("Tx", xSyncDatagramSize, xSyncDatagram);
         xGMEDatagram.setFlag(16, false);
         int xTransactionId = super._subConnection.allocateDatagramId(xGMEDatagram);
         EventLogger.logEvent(2424575107343457299L, 1415082868, xGMEDatagram.getDatagramId(), 10, 0);
         xGMEDatagram.setDatagramStatusListener(this);
         this.addSendRequest(super._subConnection, xGMEDatagram);
         this._transactionIdToSid.put(xTransactionId, sid);
         var10 = xTransactionId;
         var13 = false;
      } finally {
         if (var13) {
            if (checkInSyncDatagram) {
               this._syncDatagramsPool.checkIn(xSyncDatagram);
            }
         }
      }

      if (checkInSyncDatagram) {
         this._syncDatagramsPool.checkIn(xSyncDatagram);
      }

      return var10;
   }

   final void deRegister(SyncConnection aSyncConnection) {
      synchronized (this._connections) {
         long sid = aSyncConnection.getSid();
         if (this._connections.containsKey(sid)) {
            this._connections.remove(sid);
         }
      }
   }

   final void register(SyncConnection aSyncConnection) {
      long sid = aSyncConnection.getSid();
      synchronized (this._connections) {
         WeakReference xWref = (WeakReference)this._connections.get(sid);
         if (xWref != null) {
            Object xObject = xWref.get();
            if (xObject != null) {
               throw new IOException();
            }

            xWref = new WeakReference(aSyncConnection);
            this._connections.put(sid, xWref);
         } else {
            this._connections.put(sid, new WeakReference(aSyncConnection));
         }
      }
   }

   public final boolean isSerialTransportEnabledFor(long sid) {
      String xServiceUid = this.getServiceUidMappedFor(sid);
      int handle = this._serviceRouting.getRouteHandle(2);
      return handle != -1 ? this._serviceRouting.isServiceRoutable(xServiceUid, handle) : false;
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
      this.serviceRoutingStateChanged(service, true);
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      this.serviceRoutingStateChanged(null, routeState);
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      synchronized (this._connections) {
         Enumeration xConnectionsList = this._connections.elements();

         while (xConnectionsList.hasMoreElements()) {
            WeakReference xWref = (WeakReference)xConnectionsList.nextElement();
            SyncConnection xSyncConnection = (SyncConnection)xWref.get();
            if (xSyncConnection != null) {
               if (this.isSerialTransportEnabledFor(xSyncConnection.getSid())) {
                  xSyncConnection.notifyListener(17, null, 0);
               } else {
                  xSyncConnection.notifyListener(18, null, 0);
               }
            }
         }
      }
   }

   @Override
   public final void updateDatagramStatus(int dgId, int code, Object context) {
      long sid = this._transactionIdToSid.get(dgId);
      if (sid != -1) {
         WeakReference xWref;
         synchronized (this._connections) {
            xWref = (WeakReference)this._connections.get(sid);
         }

         if (xWref != null) {
            SyncConnection xSyncConnection = (SyncConnection)xWref.get();
            if (xSyncConnection != null) {
               if (code == 0) {
                  xSyncConnection.notifyListener(2, null, dgId);
                  return;
               }

               if ((code & 128) == 128) {
                  xSyncConnection.notifyListener(3, null, dgId);
               }
            }
         }
      }
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
      // 03: bipush 1
      // 04: invokevirtual java/lang/Thread.setPriority (I)V
      // 07: aconst_null
      // 08: astore 1
      // 09: aload 0
      // 0a: getfield net/rim/device/cldc/io/sync/Transport._receivedGMEDatagrams Lnet/rim/device/api/util/CyclicQueue;
      // 0d: dup
      // 0e: astore 2
      // 0f: monitorenter
      // 10: aload 0
      // 11: getfield net/rim/device/cldc/io/sync/Transport._receivedGMEDatagrams Lnet/rim/device/api/util/CyclicQueue;
      // 14: invokevirtual net/rim/device/api/util/CyclicQueue.isEmpty ()Z
      // 17: ifeq 24
      // 1a: aload 0
      // 1b: getfield net/rim/device/cldc/io/sync/Transport._receivedGMEDatagrams Lnet/rim/device/api/util/CyclicQueue;
      // 1e: invokevirtual java/lang/Object.wait ()V
      // 21: goto 10
      // 24: aload 0
      // 25: getfield net/rim/device/cldc/io/sync/Transport._receivedGMEDatagrams Lnet/rim/device/api/util/CyclicQueue;
      // 28: invokevirtual net/rim/device/api/util/CyclicQueue.dequeue ()Ljava/lang/Object;
      // 2b: checkcast net/rim/device/cldc/io/gme/GMEDatagram
      // 2e: astore 1
      // 2f: aload 2
      // 30: monitorexit
      // 31: goto 39
      // 34: astore 3
      // 35: aload 2
      // 36: monitorexit
      // 37: aload 3
      // 38: athrow
      // 39: aload 1
      // 3a: ifnull 42
      // 3d: aload 0
      // 3e: aload 1
      // 3f: invokespecial net/rim/device/cldc/io/sync/Transport.processGMEDatagram (Lnet/rim/device/cldc/io/gme/GMEDatagram;)V
      // 42: aload 1
      // 43: ifnull 07
      // 46: aload 1
      // 47: invokevirtual net/rim/device/api/util/DataBuffer.zero ()V
      // 4a: goto 07
      // 4d: astore 2
      // 4e: goto 07
      // 51: astore 2
      // 52: aload 1
      // 53: ifnull 07
      // 56: aload 1
      // 57: invokevirtual net/rim/device/api/util/DataBuffer.zero ()V
      // 5a: goto 07
      // 5d: astore 2
      // 5e: goto 07
      // 61: astore 2
      // 62: aload 1
      // 63: ifnull 07
      // 66: aload 1
      // 67: invokevirtual net/rim/device/api/util/DataBuffer.zero ()V
      // 6a: goto 07
      // 6d: astore 2
      // 6e: goto 07
      // 71: astore 4
      // 73: aload 1
      // 74: ifnull 80
      // 77: aload 1
      // 78: invokevirtual net/rim/device/api/util/DataBuffer.zero ()V
      // 7b: goto 80
      // 7e: astore 5
      // 80: aload 4
      // 82: athrow
      // try (10 -> 25): 26 null
      // try (26 -> 29): 26 null
      // try (38 -> 40): 41 null
      // try (5 -> 36): 43 null
      // try (46 -> 48): 49 null
      // try (5 -> 36): 51 null
      // try (54 -> 56): 57 null
      // try (5 -> 36): 59 null
      // try (43 -> 44): 59 null
      // try (51 -> 52): 59 null
      // try (62 -> 64): 65 null
      // try (59 -> 60): 59 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processGMEDatagram(GMEDatagram aGMEDatagram) {
      GMEAddress aGMEAddress = new GMEAddress(aGMEDatagram.getAddress());
      GMETarget aGMETarget = aGMEAddress.getSrc();
      String xServiceUid = aGMETarget.address;
      SyncDatagram xSyncDatagram = null;
      boolean var21 = false /* VF: Semaphore variable */;

      label160: {
         label161: {
            try {
               label142:
               try {
                  var21 = true;
                  int t = aGMEDatagram.readUnsignedByte();
                  int xSyncDatagramSize = aGMEDatagram.available();
                  long sid = this.getSidOf(xServiceUid);
                  String userId = OTASyncDaemon.getSingletonInstance().getUserIdForSid(sid);
                  xSyncDatagram = this.checkOutSyncDatagram();
                  if (t > 32) {
                     xSyncDatagram.setProtocolVersion(32);
                     xSyncDatagram.readFrom(aGMEDatagram);
                     xSyncDatagram.resetVerficationBits();
                     xSyncDatagram.setUserId(userId);
                     Status xStatus = new Status();
                     xStatus.setSessionErrorCode(401);
                     xSyncDatagram.addCommand(xStatus);
                     this.send(xSyncDatagram, false);
                     var21 = false;
                  } else {
                     xSyncDatagram.setProtocolVersion(t);
                     xSyncDatagram.readFrom(aGMEDatagram);
                     xSyncDatagram.setTransactionId(aGMEDatagram.getTransactionId());
                     xSyncDatagram.setUserId(userId);
                     if (userId != null) {
                        WeakReference xWref;
                        synchronized (this._connections) {
                           xWref = (WeakReference)this._connections.get(sid);
                        }

                        if (xWref != null) {
                           SyncConnection xSyncConnection = (SyncConnection)xWref.get();
                           if (xSyncConnection != null) {
                              Logger.logTransportationRxTx("Rx", xSyncDatagramSize, xSyncDatagram);
                              xSyncConnection.notifyListener(1, xSyncDatagram, xSyncDatagram.getTransactionId());
                              var21 = false;
                              break label160;
                           }
                        } else {
                           synchronized (this._connections) {
                              this._connections.remove(sid);
                           }
                        }
                     }

                     Logger.logTransportationDropDatagram("Rx", xSyncDatagram);
                     var21 = false;
                  }
                  break label161;
               } catch (Throwable var28) {
                  Logger.logErrorMessage(t);
                  var21 = false;
                  break label142;
               }
            } finally {
               if (var21) {
                  if (xSyncDatagram != null) {
                     this._syncDatagramsPool.checkIn(xSyncDatagram);
                  }
               }
            }

            if (xSyncDatagram != null) {
               this._syncDatagramsPool.checkIn(xSyncDatagram);
               return;
            }

            return;
         }

         if (xSyncDatagram != null) {
            this._syncDatagramsPool.checkIn(xSyncDatagram);
            return;
         }

         return;
      }

      if (xSyncDatagram != null) {
         this._syncDatagramsPool.checkIn(xSyncDatagram);
      }
   }

   @Override
   public final void init() {
      StringBuffer sb = new StringBuffer();
      sb.append("gme:").append("sync");
      super.init((DatagramConnection)Connector.open(sb.toString()));
      ProtocolDaemon.getInstance().submitRunnable(this);
   }

   @Override
   public final int getMaximumLength() {
      return -1;
   }

   private final long getSidOf(String aServiceUid) {
      ServiceRecord xServiceRecord = ServiceBook.getSB().getRecordByUidAndCid(aServiceUid, "sync");
      return ServiceIdentifier.createSid(xServiceRecord);
   }

   private final String getServiceUidMappedFor(long sid) {
      ServiceRecord xServiceRecord = OTASyncDaemon.getSingletonInstance().getServiceRecordForSid(sid);
      return xServiceRecord != null ? xServiceRecord.getUid() : null;
   }

   @Override
   public final void send(Datagram aDatagram) {
      this.send(aDatagram, false);
   }

   public Transport() {
      this._serviceRouting = ServiceRouting.getInstance();
      this._serviceRouting.addListener(this);
      this._syncDatagramsPool = ReusableObjectPool.getSingletonInstance(7926551755126522851L);
   }

   private final SyncDatagram checkOutSyncDatagram() {
      SyncDatagram xSyncDatagram = (SyncDatagram)this._syncDatagramsPool.checkOut();
      if (xSyncDatagram == null) {
         xSyncDatagram = new SyncDatagram();
      }

      return xSyncDatagram;
   }

   @Override
   protected final void processReceivedDatagram(Datagram aDatagram) {
      GMEDatagram gmeDatagram = null;
      if (!(aDatagram instanceof GMEDatagram)) {
         Firewall.getInstance().incrementBlockedCount((byte)-4);
      } else {
         gmeDatagram = (GMEDatagram)aDatagram;
         if ((gmeDatagram.isFromPeer() || !gmeDatagram.wasDatagramSecure()) && !DeviceInfo.isSimulator()) {
            Firewall.getInstance().incrementBlockedCount((byte)-4);
         } else {
            synchronized (this._receivedGMEDatagrams) {
               EventLogger.logEvent(2424575107343457299L, 1381516132, gmeDatagram.getTransactionId(), 10, 0);
               this._receivedGMEDatagrams.enqueue(gmeDatagram);
               this._receivedGMEDatagrams.notify();
            }
         }
      }
   }
}
