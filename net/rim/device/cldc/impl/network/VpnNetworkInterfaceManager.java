package net.rim.device.cldc.impl.network;

import net.rim.device.api.network.NetworkInterfaceFactory;
import net.rim.device.api.network.NetworkInterfaceListener;
import net.rim.device.api.network.NetworkInterfaceManager;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.internal.crypto.vpn.VPNListener;
import net.rim.device.internal.io.tunnel.TunnelWorker;
import net.rim.device.internal.proxy.Proxy;

final class VpnNetworkInterfaceManager extends NetworkInterfaceManager implements NetworkInterfaceListener, VPNListener {
   private NetworkInterfaceManager _subManager = NetworkInterfaceFactory.getInstance().getManager("wlan");
   private boolean _subStatus;
   private boolean _interfaceStatus;
   private byte[] _ipAddress;
   private static final long GUID;
   protected static final String NAME;
   private static final int STALE_SUB_STATE;
   private static final int STALE_INTERFACE_STATE;
   private static final int NO_IP_ADDRESS;
   private static final int STALE_INTERFACE_INFO;
   private static final int EXTRA_INTERFACE_INFO;

   public static final void init() {
      NetworkInterfaceFactory.getInstance().registerManager(new VpnNetworkInterfaceManager());
   }

   private VpnNetworkInterfaceManager() {
      super("vpn", 6319177317455077160L, null);
      boolean inBootUp = this.systemBootingUp();
      this._subStatus = this._subManager.getStatus();
      this._ipAddress = this.verifyIpAddress(inBootUp ? null : this.getIpAddress());
      this._interfaceStatus = this._ipAddress != null;
      if (!inBootUp) {
         this.updateStatus(true);
      }

      this._subManager.addListener(this);
      Proxy.getInstance().addRadioListener(this);
   }

   @Override
   public final void networkInterfaceStatusChanged(boolean status, Object context, NetworkInterfaceManager manager) {
      if (this._subStatus != status) {
         this._subStatus = status;
         this.updateStatus(false);
      } else {
         EventLogger.logEvent(6319177317455077160L, 1400132418, 3);
      }
   }

   @Override
   public final void networkInterfaceEvent(int event, Object context, NetworkInterfaceManager manager) {
   }

   private final void interfaceSuccess() {
      this._ipAddress = this.verifyIpAddress(this.getIpAddress());
      if (this._ipAddress == null) {
         EventLogger.logEvent(6319177317455077160L, 1315916112, 3);
      }

      if (!this._interfaceStatus) {
         this._interfaceStatus = true;
         this.updateStatus(true);
      } else {
         EventLogger.logEvent(6319177317455077160L, 1400129862, 3);
      }
   }

   private final void interfaceFail() {
      this._ipAddress = null;
      if (this._interfaceStatus) {
         this._interfaceStatus = false;
         this.updateStatus(true);
      } else {
         EventLogger.logEvent(6319177317455077160L, 1400129862, 3);
      }
   }

   @Override
   public final void vpnStatusChanged(int eventCode, int statusCode, int handle, int data) {
      switch (eventCode) {
         case 8960:
            if (statusCode == 0) {
               this.interfaceSuccess();
               return;
            }
            break;
         case 8962:
            if (statusCode != -25) {
               return;
            }
         case 8961:
         case 8966:
            this.interfaceFail();
      }
   }

   private final void updateStatus(boolean redirect) {
      boolean status = this._subStatus && this._interfaceStatus;
      if (super._status != status) {
         super._status = status;
         this.invokeListeners(status, null, redirect);
      }
   }

   private final boolean systemBootingUp() {
      ApplicationManager mgr = ApplicationManager.getApplicationManager();
      return mgr != null && mgr.inStartup();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] getIpAddress() {
      byte[] addr = null;
      Tunnel t = null;
      boolean var8 = false /* VF: Semaphore variable */;
      boolean var11 = false /* VF: Semaphore variable */;

      label91: {
         try {
            label85:
            try {
               var11 = true;
               var8 = true;
               int e = RadioInfo.getAccessPointNumber(WLAN.WLAN_PSEUDO_APN);
               if (!RadioInfo.isPDPContextActive(e)) {
                  TunnelWorker tw = (TunnelWorker)(new Object());
                  t = tw.open((TunnelConfig)(new Object(WLAN.WLAN_PSEUDO_APN, "net.rim.vpniface", null, null, null, tw)));
               }

               addr = RadioInfo.getIPAddress(e);
               var8 = false;
               var11 = false;
               break label91;
            } finally {
               if (var11) {
                  addr = null;
                  var8 = false;
                  break label85;
               }
            }
         } finally {
            if (var8) {
               if (t != null) {
                  t.close();
                  Tunnel var14 = null;
               }

               return addr;
            }
         }

         if (t != null) {
            t.close();
            Tunnel var15 = null;
         }

         return addr;
      }

      if (t != null) {
         t.close();
         Tunnel var16 = null;
      }

      return addr;
   }

   private final byte[] verifyIpAddress(byte[] ipAddress) {
      return ipAddress != null && ipAddress[0] != 0 && ipAddress[1] != 0 && ipAddress[2] != 0 && ipAddress[3] != 0 ? ipAddress : null;
   }
}
