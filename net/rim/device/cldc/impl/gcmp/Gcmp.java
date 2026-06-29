package net.rim.device.cldc.impl.gcmp;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.SmsAddress;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.nativebase.NativeListener;
import net.rim.device.cldc.io.udp.UdpInternalAddress;
import net.rim.device.internal.system.NvStore;

public final class Gcmp implements GcmpEvents, NativeListener {
   private DatagramConnection _udpConn;
   private GcmpSession[] _sessions;
   public static final long ID = -1673931206114386243L;
   private static final String STR = "net.rim.gcmp";

   public static final Gcmp getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      Gcmp gcmp = (Gcmp)applicationRegistry.getOrWaitFor(-1673931206114386243L);
      if (gcmp == null) {
         gcmp = new Gcmp();
         applicationRegistry.put(-1673931206114386243L, gcmp);
      }

      return gcmp;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Gcmp() {
      if (NvStore.readData(36) == null) {
         NvStore.writeData(36, !DeviceInfo.isSimulator() ? RandomSource.getBytes(32) : new byte[32]);
      }

      ProtocolDaemon daemon = ProtocolDaemon.getInstance();
      this._sessions = new GcmpSession[0];
      DatagramConnection smsConn = null;

      try {
         String address = UdpInternalAddress.makeAddress(true, null, -1, -1, null, 0, 0, 4, true);
         this._udpConn = (DatagramConnection)Connector.open(address);
         ((DatagramConnectionBase)this._udpConn).setFlag(1024, true);
         if (RadioInfo.areWAFsSupported(11)) {
            int[] ports = new int[]{65536};
            address = SmsAddress.makeAddress(true, null, ports);
            smsConn = (DatagramConnection)Connector.open(address);
         }
      } catch (Throwable var6) {
         throw new RuntimeException(e.getMessage());
      }

      Thread udpReceiveThread = new GcmpReceiveThread(this._udpConn, false);
      daemon.startThread(udpReceiveThread);
      if (smsConn != null) {
         Thread smsReceiveThread = new GcmpReceiveThread(smsConn, true);
         daemon.startThread(smsReceiveThread);
      }

      ((DatagramConnectionBase)this._udpConn).setup(334258761, this);
      EventLogger.register(-1673931206114386243L, "net.rim.gcmp", 2);
      EventLogger.logEvent(-1673931206114386243L, 1195592046, 0);
   }

   public final int register(DatagramAddressBase addressBase, String name) {
      EventLogger.logEvent(-1673931206114386243L, 1195593330, 0);
      if (!(addressBase instanceof UdpAddress)) {
         return -1;
      }

      synchronized (this._sessions) {
         return this.getSession((UdpAddress)addressBase, name);
      }
   }

   final GcmpSession makeSession(UdpAddress addressBase) {
      synchronized (this._sessions) {
         if (addressBase != null && addressBase.getIpAddressInt() != -1 && addressBase.getApn() != null) {
            int id = this.getSession(addressBase, "Relay");
            if (id != -1) {
               return this._sessions[id];
            }
         } else if (this._sessions.length >= 1) {
            return this._sessions[0];
         }

         return null;
      }
   }

   private final int getSession(UdpAddress addressBase, String name) {
      int id = this.findSession(addressBase);
      if (id != -1) {
         return id;
      }

      Arrays.add(this._sessions, new GcmpSession(this._udpConn, addressBase, name));
      return this._sessions.length - 1;
   }

   private final int findSession(UdpAddress addressBase) {
      for (int id = this._sessions.length - 1; id >= 0; id--) {
         if (this._sessions[id].isAddressed(addressBase)) {
            return id;
         }
      }

      return -1;
   }

   public final void deregister(int id) {
      EventLogger.logEvent(-1673931206114386243L, 1195593316, 0);
      synchronized (this._sessions) {
         if (id >= 0 && id < this._sessions.length) {
            this._sessions[id].deactivate();
            Arrays.removeAt(this._sessions, id);
         }
      }
   }

   public final byte[] getSecurePinIpKeyData() {
      byte[] key = NvStore.readData(36);
      byte[] data = new byte[1 + key.length];
      data[0] = 2;
      System.arraycopy(key, 0, data, 1, key.length);
      return data;
   }

   public final void pingNow(int id) {
      synchronized (this._sessions) {
         if (id < this._sessions.length) {
            if (id >= 0) {
               this._sessions[id].kick(0, 0, (byte)0, null);
            } else {
               int length = this._sessions.length;

               for (int i = 0; i < length; i++) {
                  this._sessions[i].kick(0, 0, (byte)0, null);
               }
            }
         }
      }
   }

   @Override
   public final void datagramStatus(DatagramAddressBase addressBase, int status) {
      synchronized (this._sessions) {
         int id = this.findSession((UdpAddress)addressBase);
         if (id != -1) {
            this._sessions[id].packetSent();
         }
      }
   }
}
