package net.rim.device.cldc.io.gme;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class GmeRouter implements ServiceRoutingListener {
   private GmeRouterConnection[] _connections = new GmeRouterConnection[0];
   private GmeRouterConnection _currentConnection;
   private Datagram _currentDatagram;
   private static final long GUID = -18860188569269429L;

   protected final void addConnection(GmeRouterConnection connection) {
      Arrays.add(this._connections, connection);
   }

   protected final boolean isSendChoked() {
      for (int i = this._connections.length - 1; i >= 0; i--) {
         if (!this._connections[i].isSendChoked()) {
            return false;
         }
      }

      return true;
   }

   protected final void send(DatagramBase param1, GMEDatagramInfo param2, Datagram param3) throws MissingRoutingInformationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceId ()I
      // 03: bipush -1
      // 05: if_icmpne 0b
      // 08: goto f1
      // 0b: aload 0
      // 0c: getfield net/rim/device/cldc/io/gme/GmeRouter._connections [Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 0f: arraylength
      // 10: bipush 1
      // 11: isub
      // 12: istore 4
      // 14: iload 4
      // 16: ifge 1c
      // 19: goto f1
      // 1c: aload 0
      // 1d: aload 0
      // 1e: astore 5
      // 20: monitorenter
      // 21: aload 0
      // 22: aload 0
      // 23: getfield net/rim/device/cldc/io/gme/GmeRouter._connections [Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 26: iload 4
      // 28: aaload
      // 29: putfield net/rim/device/cldc/io/gme/GmeRouter._currentConnection Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 2c: aload 0
      // 2d: aload 1
      // 2e: putfield net/rim/device/cldc/io/gme/GmeRouter._currentDatagram Ljavax/microedition/io/Datagram;
      // 31: aload 5
      // 33: monitorexit
      // 34: goto 3f
      // 37: astore 6
      // 39: aload 5
      // 3b: monitorexit
      // 3c: aload 6
      // 3e: athrow
      // 3f: aload 0
      // 40: getfield net/rim/device/cldc/io/gme/GmeRouter._connections [Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 43: iload 4
      // 45: aaload
      // 46: aload 1
      // 47: aload 2
      // 48: aload 3
      // 49: invokevirtual net/rim/device/cldc/io/gme/GmeRouterConnection.send (Lnet/rim/device/api/io/DatagramBase;Lnet/rim/device/cldc/io/gme/GMEDatagramInfo;Ljavax/microedition/io/Datagram;)V
      // 4c: aload 0
      // 4d: aload 0
      // 4e: astore 5
      // 50: monitorenter
      // 51: aload 0
      // 52: aconst_null
      // 53: putfield net/rim/device/cldc/io/gme/GmeRouter._currentConnection Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 56: aload 0
      // 57: aconst_null
      // 58: putfield net/rim/device/cldc/io/gme/GmeRouter._currentDatagram Ljavax/microedition/io/Datagram;
      // 5b: aload 5
      // 5d: monitorexit
      // 5e: return
      // 5f: astore 7
      // 61: aload 5
      // 63: monitorexit
      // 64: aload 7
      // 66: athrow
      // 67: astore 5
      // 69: aload 0
      // 6a: aload 0
      // 6b: astore 6
      // 6d: monitorenter
      // 6e: aload 0
      // 6f: getfield net/rim/device/cldc/io/gme/GmeRouter._currentConnection Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 72: ifnull 78
      // 75: aload 5
      // 77: athrow
      // 78: aload 6
      // 7a: monitorexit
      // 7b: goto 86
      // 7e: astore 8
      // 80: aload 6
      // 82: monitorexit
      // 83: aload 8
      // 85: athrow
      // 86: aload 0
      // 87: getfield net/rim/device/cldc/io/gme/GmeRouter._connections [Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 8a: arraylength
      // 8b: istore 4
      // 8d: aload 0
      // 8e: aload 0
      // 8f: astore 5
      // 91: monitorenter
      // 92: aload 0
      // 93: aconst_null
      // 94: putfield net/rim/device/cldc/io/gme/GmeRouter._currentConnection Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // 97: aload 0
      // 98: aconst_null
      // 99: putfield net/rim/device/cldc/io/gme/GmeRouter._currentDatagram Ljavax/microedition/io/Datagram;
      // 9c: aload 5
      // 9e: monitorexit
      // 9f: goto eb
      // a2: astore 9
      // a4: aload 5
      // a6: monitorexit
      // a7: aload 9
      // a9: athrow
      // aa: astore 5
      // ac: aload 0
      // ad: aload 0
      // ae: astore 5
      // b0: monitorenter
      // b1: aload 0
      // b2: aconst_null
      // b3: putfield net/rim/device/cldc/io/gme/GmeRouter._currentConnection Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // b6: aload 0
      // b7: aconst_null
      // b8: putfield net/rim/device/cldc/io/gme/GmeRouter._currentDatagram Ljavax/microedition/io/Datagram;
      // bb: aload 5
      // bd: monitorexit
      // be: goto eb
      // c1: astore 10
      // c3: aload 5
      // c5: monitorexit
      // c6: aload 10
      // c8: athrow
      // c9: astore 11
      // cb: aload 0
      // cc: aload 0
      // cd: astore 12
      // cf: monitorenter
      // d0: aload 0
      // d1: aconst_null
      // d2: putfield net/rim/device/cldc/io/gme/GmeRouter._currentConnection Lnet/rim/device/cldc/io/gme/GmeRouterConnection;
      // d5: aload 0
      // d6: aconst_null
      // d7: putfield net/rim/device/cldc/io/gme/GmeRouter._currentDatagram Ljavax/microedition/io/Datagram;
      // da: aload 12
      // dc: monitorexit
      // dd: goto e8
      // e0: astore 13
      // e2: aload 12
      // e4: monitorexit
      // e5: aload 13
      // e7: athrow
      // e8: aload 11
      // ea: athrow
      // eb: iinc 4 -1
      // ee: goto 14
      // f1: new net/rim/device/cldc/io/gme/MissingRoutingInformationException
      // f4: dup
      // f5: invokespecial net/rim/device/cldc/io/gme/MissingRoutingInformationException.<init> ()V
      // f8: athrow
      // try (17 -> 28): 29 null
      // try (29 -> 32): 29 null
      // try (46 -> 54): 55 null
      // try (55 -> 58): 55 null
      // try (34 -> 42): 60 null
      // try (65 -> 72): 73 null
      // try (73 -> 76): 73 null
      // try (86 -> 94): 95 null
      // try (95 -> 98): 95 null
      // try (34 -> 42): 100 null
      // try (105 -> 113): 114 null
      // try (114 -> 117): 114 null
      // try (34 -> 42): 119 null
      // try (60 -> 82): 119 null
      // try (100 -> 101): 119 null
      // try (124 -> 132): 133 null
      // try (133 -> 136): 133 null
      // try (119 -> 120): 119 null
   }

   protected final void cancel(Datagram datagram) {
      for (int i = this._connections.length - 1; i >= 0; i--) {
         this._connections[i].cancel(datagram);
      }
   }

   protected final void datagramProcessed(int datagramId) {
      for (int i = this._connections.length - 1; i >= 0; i--) {
         this._connections[i]._subConnection.datagramProcessed(datagramId);
      }
   }

   protected final int getMaximumLength() {
      int ret = Integer.MAX_VALUE;

      for (int i = this._connections.length - 1; i >= 0; i--) {
         int temp = this._connections[i]._subConnection.getMaximumLength();
         if (temp < ret) {
            ret = temp;
         }
      }

      return ret;
   }

   protected final int getNominalLength() {
      int ret = Integer.MAX_VALUE;

      for (int i = this._connections.length - 1; i >= 0; i--) {
         int temp = this._connections[i]._subConnection.getNominalLength();
         if (temp < ret) {
            ret = temp;
         }
      }

      return ret;
   }

   protected final byte[] setup(int callType, Object context) {
      for (int i = this._connections.length - 1; i >= 0; i--) {
         byte[] ret = this._connections[i]._subConnection.setup(callType, context);
         if (ret != null) {
            return ret;
         }
      }

      return null;
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (serviceState) {
         GmeRouterConnection connection;
         Datagram datagram;
         synchronized (this) {
            if (this._currentConnection == null) {
               return;
            }

            connection = this._currentConnection;
            datagram = this._currentDatagram;
            this._currentConnection = null;
            this._currentDatagram = null;
         }

         try {
            connection.cancel(datagram);
         } finally {
            return;
         }
      }
   }

   private GmeRouter() {
      ServiceRouting.getInstance().addListener(this);
   }

   public static final GmeRouter getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      GmeRouter gmeRouter = (GmeRouter)ar.getOrWaitFor(-18860188569269429L);
      if (gmeRouter == null) {
         gmeRouter = new GmeRouter();
         ar.put(-18860188569269429L, gmeRouter);
      }

      return gmeRouter;
   }
}
