package net.rim.device.cldc.io.gme;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.cldc.io.srp.SrpListener;
import net.rim.device.cldc.io.srp.SrpUtils;
import net.rim.device.internal.system.DataServices;

public final class SrpBridge extends GmeRouterConnection implements ServiceRoutingListener, SrpListener {
   private int _routingHandle = -1;
   private ServiceRouting _serviceRouting;
   private DataServices _dataServices;
   private String _connectionParameters;
   private int _linkType = -1;
   private int _connectionType = -1;
   private static String SNTP = "_GME_SNTP_";
   private static String SCHEME = "srp:";
   private static final int CAPABILITIES = 3;

   protected SrpBridge(Transport transport, boolean wifiLink) {
      super(
         transport,
         (DatagramConnectionBase)Connector.open(
            ((StringBuffer)(new Object())).append(SCHEME).append("//;connectiontype=router;interface=").append(wifiLink ? "wifi" : "cellular").toString()
         )
      );
      super._subConnection.setDatagramStatusListener(transport);
      this._dataServices = DataServices.getInstance();
      this._serviceRouting = ServiceRouting.getInstance();
      this._connectionParameters = ((StringBuffer)(new Object(";connectiontype=router;interface="))).append(wifiLink ? "wifi" : "cellular").toString();
      if (wifiLink) {
         this._routingHandle = this._serviceRouting.addInterface(new ServiceRoutingProperties(ServiceRoutingProperties.SRP_WI_FI, 3, 1146, 1, 2, 3));
         this._linkType = 0;
      } else {
         this._routingHandle = this._serviceRouting.addInterface(new ServiceRoutingProperties(ServiceRoutingProperties.SRP_RF, 1, 6, 2, 2, 3));
         this._linkType = 1;
      }

      this._connectionType = 0;
      SrpUtils.getInstance().addListener(this);
      this._serviceRouting.addListener(this);
   }

   @Override
   public final boolean isSendChoked() {
      return true;
   }

   @Override
   public final void send(DatagramBase dg, GMEDatagramInfo di, Datagram superDG) {
      if (!this._dataServices.isDataServicesEnabled(this._linkType == 0 ? 2 : 1, 2)) {
         throw new Object();
      }

      String uid = null;
      GMEAddress gmeAddr = di.address;
      int numAddrs = gmeAddr.getNumTargets();
      boolean pickAny = false;
      if (numAddrs == 0) {
         if (di.cmdByte != 10) {
            throw new Object();
         }

         uid = SNTP;
         pickAny = true;
      } else {
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
      }

      if (uid != null && this._serviceRouting.isServiceRoutable(!pickAny ? uid : null, this._routingHandle)) {
         dg.setAddressBase(super._subConnection.newDatagramAddressBase(uid, false));
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
   public final int getLinkType() {
      return this._linkType;
   }

   @Override
   public final int getConnectionType() {
      return this._connectionType;
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (!serviceState && SrpUtils.getInstance().getServiceState(service)) {
         super._transport
            ._serviceAuthentication
            .startService(
               service,
               SrpUtils.getInstance().getCapabilities(service),
               ((StringBuffer)(new Object())).append(SCHEME).append("//").append(service).append(this._connectionParameters).toString(),
               this._routingHandle
            );
      }
   }

   @Override
   public final void srpServiceStateChanged(String service, int capabilities, boolean serviceState) {
      if (serviceState) {
         super._transport
            ._serviceAuthentication
            .startService(
               service,
               capabilities,
               ((StringBuffer)(new Object())).append(SCHEME).append("//").append(service).append(this._connectionParameters).toString(),
               this._routingHandle
            );
      } else {
         super._transport._serviceAuthentication.stopService(service, this._routingHandle);
         this._serviceRouting.setServiceState(service, 0, this._routingHandle, false, false);
      }
   }

   @Override
   public final void srpRouteStateChanged(int linkType, int connectionType, boolean routeState) {
      if (linkType == this.getLinkType() && connectionType == this.getConnectionType()) {
         this._serviceRouting.setRouteState(this._routingHandle, routeState, false);
      }
   }
}
