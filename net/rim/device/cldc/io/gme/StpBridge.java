package net.rim.device.cldc.io.gme;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.cldc.io.stp.StpListener;
import net.rim.device.cldc.io.stp.StpUtil;
import net.rim.device.internal.system.USBPortInternal;

public final class StpBridge extends GmeRouterConnection implements ServiceRoutingListener, StpListener {
   private int _routingHandle = -1;
   private ServiceRouting _serviceRouting;
   private static String SCHEME = "stp:";
   private static final int CAPABILITIES;

   protected StpBridge(Transport transport) {
      super(transport, (DatagramConnectionBase)Connector.open(SCHEME));
      super._subConnection.setDatagramStatusListener(transport);
      this._serviceRouting = ServiceRouting.getInstance();
      this._routingHandle = this._serviceRouting
         .addInterface(new ServiceRoutingProperties(ServiceRoutingProperties.STP, 2, USBPortInternal.isSupported() ? 1250 : 12, 0, 1, 2));
      StpUtil.getInstance().addListener(this);
      this._serviceRouting.addListener(this);
   }

   @Override
   public final boolean isSendChoked() {
      return true;
   }

   @Override
   public final void send(DatagramBase dg, GMEDatagramInfo di, Datagram superDG) {
      String uid = null;
      GMEAddress gmeAddr = di.address;
      int numAddrs = gmeAddr.getNumTargets();
      if (numAddrs == 0) {
         throw new Object();
      }

      int i = 0;

      while (i < numAddrs) {
         GMETarget targ = gmeAddr.getTarget(i);
         switch (targ.type) {
            case 1:
            default:
               if (uid == null) {
                  uid = targ.address;
               } else if (!targ.address.equals(uid)) {
                  throw new Object();
               }
            case 0:
               i++;
               break;
            case 2:
               throw new Object();
         }
      }

      if (uid != null && this._serviceRouting.isServiceRoutable(uid, this._routingHandle)) {
         Transport.logDebugInfo(1415074675);
         super._transport.subSend(super._subConnection, dg, di.listener, di.transId, superDG);
         di.errorEventCode = -1;
         di.errorEventContext = null;
      } else {
         throw new Object();
      }
   }

   @Override
   public final void receivedFrom(GMEDatagramInfo dgInfo) {
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (!serviceState && StpUtil.getInstance().getServiceState(service)) {
         super._transport._serviceAuthentication.startService(service, StpUtil.getInstance().getCapabilities(service), SCHEME, this._routingHandle);
      }
   }

   @Override
   public final void stpServiceStateChanged(String service, int capabilities, boolean serviceState) {
      if (serviceState) {
         super._transport._serviceAuthentication.startService(service, capabilities, SCHEME, this._routingHandle);
      } else {
         super._transport._serviceAuthentication.stopService(service, this._routingHandle);
         ServiceRouting.getInstance().setServiceState(service, 0, this._routingHandle, false, false);
      }
   }
}
