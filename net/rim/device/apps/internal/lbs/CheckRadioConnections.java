package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.CoverageStatusListener;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.system.DataServices;

public final class CheckRadioConnections implements CoverageStatusListener {
   private boolean _radioTurnedOn = false;
   private boolean _outOfCoverage = false;
   private boolean _dataServicesEnabled = false;
   private String _radioErrorMsg;
   private String _coverageErrorMsg;
   private CheckRadioConnections$Callback _callback;
   private Request _curRequest;
   private static boolean _mapReqRadioChecked = false;

   public CheckRadioConnections(CheckRadioConnections$Callback callback) {
      this._callback = callback;
      this._curRequest = null;
   }

   public final void checkRadioConnection(Request request) {
      this._curRequest = request;
      this._radioTurnedOn = false;
      if (request.getCommand() != 1 || !_mapReqRadioChecked) {
         if (request.getCommand() == 1) {
            _mapReqRadioChecked = true;
         }

         if (RadioInfo.getActiveWAFs() == 0) {
            String netType = RadioInfo.areWAFsSupported(4) ? "" : LBSResources.getString(335);
            if ((DeviceInfo.getBatteryStatus() & 16384) != 0) {
               this._radioErrorMsg = MessageFormat.format(LBSResources.getString(333), new String[]{netType});
               UiApplication.getUiApplication().invokeLater(new CheckRadioConnections$MessageRunnable(this, this._radioErrorMsg, 0));
            } else {
               this._radioErrorMsg = MessageFormat.format(LBSResources.getString(332), new String[]{netType});
               UiApplication.getUiApplication().invokeLater(new CheckRadioConnections$MessageRunnable(this, this._radioErrorMsg, 1));
            }
         } else {
            this.checkCoverage(false, request);
         }
      }
   }

   public final void checkCoverage(boolean requestComplete, Request request) {
      this._outOfCoverage = false;
      this._dataServicesEnabled = false;
      if (!DataServices.getInstance().isDataServicesEnabled()) {
         this._coverageErrorMsg = LBSResources.getString(482);
         if (!requestComplete) {
            UiApplication.getUiApplication().invokeLater(new CheckRadioConnections$MessageRunnable(this, this._coverageErrorMsg, 2));
            return;
         }
      } else if (this.isOutofCoverage()) {
         CoverageInfo.addListener(this);
         String netType = (RadioInfo.getActiveWAFs() & 4) == 4 ? LBSResources.getString(336) : LBSResources.getString(335);
         this._coverageErrorMsg = MessageFormat.format(LBSResources.getString(334), new String[]{netType});
         if (!requestComplete) {
            UiApplication.getUiApplication().invokeLater(new CheckRadioConnections$MessageRunnable(this, this._coverageErrorMsg, 0));
         }

         this._outOfCoverage = true;
      }
   }

   public final boolean isOutofCoverage() {
      return !CoverageInfo.isCoverageSufficient(2) && !CoverageInfo.isCoverageSufficient(4) && !CoverageInfo.isCoverageSufficient(1);
   }

   @Override
   public final void coverageStatusChanged(int newCoverage) {
      if (!this.isOutofCoverage() && this._curRequest != null && this._curRequest.getCommand() == 1) {
         this._outOfCoverage = false;
         _mapReqRadioChecked = false;
         this._callback.radioChecked(true, true, this._outOfCoverage);
      }
   }
}
