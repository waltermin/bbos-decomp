package net.rim.device.api.servicebook;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.datarecovery.DataRecoveryListener;
import net.rim.device.internal.system.DataServices;

final class ServiceRouting$RFServiceTracker implements DataRecoveryListener, GlobalEventListener {
   private boolean _isDataServiceEnabled;
   private boolean _isWaitingForHostRoutingInfo;
   private boolean _isWaitingForRelayCommunication;
   private boolean _freshnessSeal = true;
   private boolean _freshnessSealTwo = true;

   private ServiceRouting$RFServiceTracker() {
      ProtocolDaemon.getInstance().addGlobalEventListener(this);
      DataRecovery.getInstance().addListener(this);
      this._isWaitingForRelayCommunication = !DataRecovery.getInstance().isConnectionAvailable();
      this._isWaitingForHostRoutingInfo = !this.hasRoutingInfo();
      this._isDataServiceEnabled = DataServices.getInstance().isDataServicesEnabled(1);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid == -3864212166794284297L || guid == -2686242019764477137L || guid == -6531073315810526672L || guid == 2200641410611652722L)
         && HRUtils.getDefaultHRT() == object0) {
         boolean noActiveHRI = guid == -3864212166794284297L || guid == -2686242019764477137L;
         if (!this.isWaitingForRelayService() && (this._freshnessSealTwo || this._isWaitingForHostRoutingInfo != noActiveHRI)) {
            this._freshnessSealTwo = false;
            this._isWaitingForHostRoutingInfo = noActiveHRI;
            ServiceRouting sr = ServiceRouting.getInstance();
            if (this.isNetworkAvailable() && this.isDataServiceEnabled()) {
               sr.setRouteState(false, sr.getRouteHandle(ServiceRoutingProperties.MDP), !noActiveHRI, true);
               return;
            }

            sr.setRouteState(false, sr.getRouteHandle(ServiceRoutingProperties.MDP), false, true);
         }
      } else {
         if (guid == -3556743465989743742L && this._isDataServiceEnabled != DataServices.getInstance().isDataServicesEnabled(1)) {
            this._isDataServiceEnabled = !this._isDataServiceEnabled;
            ServiceRouting sr = ServiceRouting.getInstance();
            if (this.isNetworkAvailable() && this.hasRoutingInfo() && !this.isWaitingForRelayService()) {
               sr.setRouteState(false, sr.getRouteHandle(ServiceRoutingProperties.MDP), this._isDataServiceEnabled, true);
               return;
            }

            sr.setRouteState(false, sr.getRouteHandle(ServiceRoutingProperties.MDP), false, true);
         }
      }
   }

   @Override
   public final void dataRecoveryEventOccurred(int event, int linkType) {
      boolean currentState = this._isWaitingForRelayCommunication;
      switch (event) {
         case 0:
            return;
         case 1:
            this._isWaitingForRelayCommunication = false;
            break;
         case 2:
         case 3:
         default:
            this._isWaitingForRelayCommunication = true;
      }

      if (this._freshnessSeal || currentState != this._isWaitingForRelayCommunication) {
         this._freshnessSeal = false;
         ServiceRouting sr = ServiceRouting.getInstance();
         if (this.isNetworkAvailable() && this.hasRoutingInfo() && this.isDataServiceEnabled()) {
            sr.setRouteState(false, sr.getRouteHandle(ServiceRoutingProperties.MDP), !this._isWaitingForRelayCommunication, true);
            return;
         }

         sr.setRouteState(false, sr.getRouteHandle(ServiceRoutingProperties.MDP), false, true);
      }
   }

   private final boolean isRouteAvailable() {
      return this.isNetworkAvailable() && this.isDataServiceEnabled() && !this.isWaitingForRelayService() && this.hasRoutingInfo();
   }

   private final boolean hasRoutingInfo() {
      HostRoutingTable defHRT = HRUtils.getDefaultHRT();
      return defHRT != null && defHRT.getActiveHri() != null;
   }

   private final boolean isWaitingForRelayService() {
      return this._isWaitingForRelayCommunication;
   }

   private final boolean isDataServiceEnabled() {
      return this._isDataServiceEnabled;
   }

   private final boolean isNetworkAvailable() {
      return RadioInfo.getState() == 1 && (RadioInfo.getNetworkService() & 4) == 4;
   }

   ServiceRouting$RFServiceTracker(ServiceRouting$1 x0) {
      this();
   }
}
