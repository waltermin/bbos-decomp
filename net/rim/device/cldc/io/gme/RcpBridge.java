package net.rim.device.cldc.io.gme;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.cldc.io.srp.SrpListener;
import net.rim.device.cldc.io.srp.SrpUtils;
import net.rim.device.internal.system.DataServices;

public final class RcpBridge extends GmeRouterConnection implements SrpListener {
   private DataServices _dataServices;
   private ServiceRouting _serviceRouting;
   private int _routingHandle = -1;
   private int _linkType = -1;
   private int _connectionType = -1;
   private static String SCHEME = "srp:";
   private static String EMPTY = "";
   private static final int CAPABILITIES = 63;

   protected RcpBridge(Transport transport, boolean wifiLink) {
      super(
         transport,
         (DatagramConnectionBase)Connector.open(
            ((StringBuffer)(new Object())).append(SCHEME).append("//;connectiontype=relay;interface=").append(wifiLink ? "wifi" : "cellular").toString()
         )
      );
      this._dataServices = DataServices.getInstance();
      this._serviceRouting = ServiceRouting.getInstance();
      super._subConnection.setDatagramStatusListener(transport);
      if (wifiLink) {
         this._routingHandle = this._serviceRouting.addInterface(new ServiceRoutingProperties(ServiceRoutingProperties.RCP_WI_FI, 3, 1146, 1, 2, 63));
         this._linkType = 0;
      } else {
         this._routingHandle = this._serviceRouting.addInterface(new ServiceRoutingProperties(ServiceRoutingProperties.RCP_RF, 1, 6, 2, 2, 63));
         this._linkType = 1;
      }

      this._connectionType = 1;
      SrpUtils.getInstance().addListener(this);
   }

   @Override
   public final boolean isSendChoked() {
      return true;
   }

   private final void findRoutingInformation(HostRoutingTable hrt, GMEDatagramInfo di) {
   }

   @Override
   public final void send(DatagramBase dg, GMEDatagramInfo di, Datagram superDG) {
      if (!this._dataServices.isDataServicesEnabled(this._linkType == 0 ? 2 : 1, 1)) {
         throw new Object();
      }

      if (!this._serviceRouting.isServiceRoutable(null, this._routingHandle)) {
         throw new Object();
      }

      dg.setAddressBase(super._subConnection.newDatagramAddressBase(EMPTY, false));
      Transport.logDebugInfo(1415074675);
      super._transport.subSend(super._subConnection, dg, di.listener, di.transId, superDG);
      di.errorEventCode = -1;
      di.errorEventContext = null;
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
   public final void srpServiceStateChanged(String service, int capabilities, boolean serviceState) {
   }

   @Override
   public final void srpRouteStateChanged(int linkType, int connectionType, boolean routeState) {
      if (linkType == this.getLinkType() && connectionType == this.getConnectionType()) {
         this._serviceRouting.setRouteState(this._routingHandle, routeState, false);
      }
   }
}
