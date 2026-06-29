package net.rim.device.cldc.io.udp;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.IOPortAlreadyBoundException;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.cldc.io.nativebase.NativeConnectionBase;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.internal.io.PortAssigner;
import net.rim.device.internal.io.PortAssigner$PortAssignedConnectionString;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.io.tunnel.TunnelWorker;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public final class Protocol extends NativeConnectionBase {
   private boolean _promiscuousMode;
   private boolean _promiscuousApnMode;
   private byte _isMidlet;
   private int _localPort = -1;
   private String _apnName;
   private Tunnel _tunnel;
   private static final byte IS_MIDLET_UNDETERMINED = 0;
   private static final byte IS_MIDLET_TRUE = 1;
   private static final byte IS_MIDLET_FALSE = 2;
   private static PortAssigner _hpa = PortAssigner.getInstance(17);
   private static String SERVER_CHECK_STRING = "//:";
   private static String SLASH_SLASH = "//";
   private static String SLASH = "/";
   private static String UDP_TYPE = "|UDP";
   private static String UDP_TUNNEL = "net.rim.udp";

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      boolean serverConnection = false;
      String localName = name;
      boolean prMode = UdpInternalAddress.isPromiscuousMode(name);
      boolean noTunnelRequired = UdpInternalAddress.noTunnelRequired(name);
      boolean retryOnNoContext = UdpInternalAddress.retriesOnNoContextRequested(name);
      String[] apnSettings = null;
      if (name.indexOf(SERVER_CHECK_STRING) != -1 || name.equals(SLASH_SLASH)) {
         serverConnection = true;

         String c;
         try {
            UdpAddress tempAddr = new UdpAddress(name);
            c = tempAddr.getType() != -1
               ? tempAddr.getAddress()
               : UdpAddress.makeAddress(
                  false,
                  tempAddr.getIpAddress(),
                  tempAddr.getDestPort(),
                  tempAddr.getSrcPort(),
                  tempAddr.getApn(),
                  1,
                  tempAddr.getApnUsername(),
                  tempAddr.getApnPassword()
               );
         } catch (Exception e) {
            c = UDP_TYPE;
         }

         name = c;
      }

      if (prMode || retryOnNoContext) {
         try {
            ControlledAccess.assertRRISignatures(true);
            if (retryOnNoContext) {
               this.setFlag(1024, true);
            }

            if (prMode) {
               this._promiscuousMode = true;
               noTunnelRequired = false;
            }
         } catch (ControlledAccessException cae) {
            EventLogger.logEvent(-832245984976358184L, 1229874030, 2);
            throw new IOException(cae.getMessage());
         }
      }

      if (!this._promiscuousMode) {
         apnSettings = UdpAddress.retrieveApnSettings(name);
         if (apnSettings == null || apnSettings[0] == null) {
            apnSettings = new String[]{
               TunnelCredentialsProvider.getInstance().getApn(),
               TunnelCredentialsProvider.getInstance().getApnUsername(),
               TunnelCredentialsProvider.getInstance().getApnPassword()
            };
         }

         if (UdpInternalAddress.wifiRequested(name)) {
            apnSettings = new String[]{WLAN.WLAN_PSEUDO_APN, null, null};
         }

         if (!noTunnelRequired && apnSettings != null) {
            TunnelWorker tw = new TunnelWorker();

            try {
               this._tunnel = tw.open(new TunnelConfig(apnSettings[0], UDP_TUNNEL, null, apnSettings[1], apnSettings[2], tw));
            } catch (IllegalArgumentException var19) {
            }
         }
      }

      name = UdpAddress.resolveAddress(name);
      Connection c = super.openPrim(name, mode, timeouts);
      if (this._promiscuousMode) {
         ((UdpAddress)super._receiveFilter).setDestPort(-1);
         this._localPort = -1;
         return c;
      }

      synchronized (_hpa) {
         if ((this._apnName = ((UdpAddress)super._receiveFilter).getApn()) == null && apnSettings != null) {
            this._apnName = apnSettings[0];
         }

         PortAssigner$PortAssignedConnectionString url = _hpa.checkPorts(localName);
         int port = -1;
         if (!serverConnection) {
            port = url.getLocalPortAssigned() ? url.getLocalPort() : _hpa.getUnusedPort(this._apnName);
         } else if (url.getPortAssigned()) {
            port = url.getPort();
            this._promiscuousApnMode = noTunnelRequired;
         } else {
            port = _hpa.getUnusedPort(this._apnName);
         }

         if (port != -1) {
            try {
               if (!this._promiscuousApnMode) {
                  _hpa.registerConnection(port, c, this._apnName);
               } else {
                  _hpa.registerConnection(port, c);
               }
            } catch (IOPortAlreadyBoundException ie) {
               try {
                  c.close();
               } catch (IOException var17) {
               }

               throw ie;
            }

            this._localPort = port;
         }
      }

      if (!this._promiscuousApnMode && apnSettings != null) {
         if (((UdpAddress)super._receiveFilter).getApn() == null) {
            ((UdpAddress)super._receiveFilter).setApn(apnSettings[0]);
            ((UdpAddress)super._receiveFilter).setApnUsername(apnSettings[1]);
            ((UdpAddress)super._receiveFilter).setApnPassword(apnSettings[2]);
         }

         if (super._addressBase != null && ((UdpAddress)super._addressBase).getApn() == null) {
            ((UdpAddress)super._addressBase).setApn(apnSettings[0]);
            ((UdpAddress)super._addressBase).setApnUsername(apnSettings[1]);
            ((UdpAddress)super._addressBase).setApnPassword(apnSettings[2]);
         }
      }

      if (serverConnection) {
         super._receiveFilter.swap();
      }

      ((UdpAddress)super._receiveFilter).setDestPort(this._localPort);
      return c;
   }

   @Override
   public final void close() {
      super.close();
      if (this._localPort != -1 && _hpa != null) {
         synchronized (_hpa) {
            if (this._promiscuousApnMode) {
               _hpa.deregisterConnection(this._localPort, this);
            } else {
               _hpa.deregisterConnection(this._localPort, this, this._apnName);
            }
         }
      }

      if (this._tunnel != null) {
         this._tunnel.close();
      }
   }

   @Override
   public final int getLocalPort() {
      this.checkForClosed();
      return ((UdpAddress)super._receiveFilter).getDestPort();
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int size) {
      if (this._isMidlet == 0) {
         this._isMidlet = (byte)(CodeModuleManager.isMidlet(TraceBack.getCallingModule(0)) ? 1 : 2);
      }

      return this._isMidlet == 1 ? this.newMidletDatagram(buf, size, null) : super.newDatagram(buf, size);
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int size, String addr) {
      if (this._isMidlet == 0) {
         this._isMidlet = (byte)(CodeModuleManager.isMidlet(TraceBack.getCallingModule(0)) ? 1 : 2);
      }

      return this._isMidlet == 1 ? this.newMidletDatagram(buf, size, addr) : super.newDatagram(buf, size, addr);
   }

   @Override
   public final Datagram newDatagram(int size) {
      if (this._isMidlet == 0) {
         this._isMidlet = (byte)(CodeModuleManager.isMidlet(TraceBack.getCallingModule(0)) ? 1 : 2);
      }

      return this._isMidlet == 1 ? this.newMidletDatagram(null, size, null) : super.newDatagram(size);
   }

   @Override
   public final Datagram newDatagram(int size, String addr) {
      if (this._isMidlet == 0) {
         this._isMidlet = (byte)(CodeModuleManager.isMidlet(TraceBack.getCallingModule(0)) ? 1 : 2);
      }

      return this._isMidlet == 1 ? this.newMidletDatagram(null, size, addr) : super.newDatagram(size, addr);
   }

   public final Datagram newMidletDatagram(byte[] buf, int size, String addr) {
      this.checkForClosed();
      addr = this.midletSpecificWork(addr);
      Datagram datagram = ((Transport)super._transport).newMidletDatagram(buf, 0, size, addr);
      if (buf == null && size > 0) {
         datagram.setLength(size);
      }

      return datagram;
   }

   @Override
   protected final boolean checkNetwork() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return false;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            return true;
      }
   }

   @Override
   public final int getMaximumLength() {
      this.checkForClosed();
      int length = super._transport.getMaximumLength();
      switch (((UdpAddress)super._addressBase).getType()) {
         case 2:
         case 4:
            length -= GpakUtil.getHeaderSize();
         default:
            return length;
      }
   }

   @Override
   public final int getNominalLength() {
      this.checkForClosed();
      return this.getMaximumLength();
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase addressBase) {
      return ((UdpInternalAddress)super._receiveFilter).equals(addressBase, this._promiscuousMode);
   }

   @Override
   public final void receive(Datagram datagram) {
      super.receive(datagram);
      int type = ((UdpAddress)super._receiveFilter).getType();
      if (type != -1 && (type & 6) != 0) {
         if (datagram instanceof DatagramBase) {
            UdpInternalAddress addressBase = new UdpInternalAddress(((DatagramBase)datagram).getAddressBase());
            int packetType = GpakUtil.decode(datagram, addressBase);
            addressBase.setType(packetType);
            ((DatagramBase)datagram).setAddressBase(addressBase);
            return;
         }

         UdpInternalAddress addressBase = new UdpInternalAddress(datagram.getAddress());
         int packetType = GpakUtil.decode(datagram);
         addressBase.setType(packetType);
         datagram.setAddress(addressBase.getAddress());
      }
   }

   @Override
   public final byte[] setup(int callType, Object context) {
      switch (callType) {
         case -157135626:
            return super.setup(callType, context);
         case -157135625:
         default:
            if (!this._promiscuousMode) {
               try {
                  ControlledAccess.assertRRISignatures(true);
               } catch (ControlledAccessException cae) {
                  return null;
               }

               this._promiscuousMode = true;
               ((UdpAddress)super._receiveFilter).setDestPort(-1);
               if (this._localPort != -1 && _hpa != null) {
                  synchronized (_hpa) {
                     _hpa.deregisterConnection(this._localPort, this, this._apnName);
                  }
               }

               this._localPort = -1;
               if (this._tunnel != null) {
                  this._tunnel.close();
                  return null;
               }
            }

            return null;
      }
   }

   private final String midletSpecificWork(String address) {
      int mh = Process.currentProcess().getModuleHandle();
      if (CodeModuleManager.isMidlet(mh) && address != null) {
         int index = address.indexOf(SLASH_SLASH);
         if (index > 0) {
            address = address.substring(index);
         }

         if (super._addressBase instanceof UdpAddress) {
            UdpAddress uab = (UdpAddress)super._addressBase;
            if (uab.getSrcPort() == -1) {
               address = address + ';' + this._localPort;
            }

            String apn = uab.getApn();
            if (apn == null || apn.length() == 0) {
               apn = this._apnName;
            }

            if (apn != null && apn.length() > 0) {
               address = address + SLASH + apn;
            }
         }
      }

      return address;
   }

   @Override
   public final int getProperties(String name) {
      int properties = 2;
      if (UdpInternalAddress.wifiRequested(name)) {
         properties |= 16;
      }

      return properties;
   }
}
