package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;

final class GPSComponentFactory implements Factory, RadioStatusListener, GPSListener, TestPoint {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   int _iconIndex;

   final int iconIndex() {
      if (!showIcon()) {
         return -1;
      } else {
         int gpsMode = GPS.getMode();
         if (gpsMode == 1) {
            return 0;
         } else {
            return gpsMode == 2 ? 1 : -1;
         }
      }
   }

   final void init() {
      if (GPS.isSupported()) {
         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
         repos.addFactory("GPSMode", this);
         Application app = Application.getApplication();
         GPS.addListener(app, this);
         app.addRadioListener(this);
      }

      this.updateRadioStatus();
   }

   @Override
   public final Object createInstance(Object initialData) {
      GPSComponent rc = new GPSComponent(this);
      this._helper.addComponentForUpdate(rc);
      return rc;
   }

   @Override
   public final synchronized void gpsModeChangeComplete(boolean success, int mode) {
      this.updateRadioStatus();
   }

   @Override
   public final void gpsLocationUpdated(int error, int type, int mode) {
   }

   @Override
   public final void gpsResponseGetLPS(int result) {
   }

   @Override
   public final void gpsResponseSetLPS(int result) {
   }

   @Override
   public final void gpsResponseEnablePIN(int result) {
   }

   @Override
   public final void gpsResponseChangePIN(int result) {
   }

   @Override
   public final void gpsPDEChangeComplete(boolean success, int ip, int port) {
   }

   @Override
   public final void gpsCredentialChangeComplete(boolean success, int clientId) {
   }

   @Override
   public final void gpsEphemerisDataRequired(int format) {
   }

   @Override
   public final void gpsLocationAidingRequest() {
   }

   @Override
   public final void signalLevel(int level) {
      this.updateRadioStatus();
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateRadioStatus();
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
      this.updateRadioStatus();
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateRadioStatus();
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Integer) {
         this._iconIndex = (Integer)value - 1;
      }

      this._helper.doUpdates();
   }

   static final boolean showIcon() {
      if (!GPS.isSupportedOnCurrentNetwork()) {
         return false;
      } else if (RadioInfo.getNetworkType() == 5 && GPS.isSupported()) {
         return true;
      } else {
         return RadioInfo.getNetworkType() != 4 ? false : RadioInfo.getState() != 0 && RadioInfo.getSignalLevel() != -256;
      }
   }

   private final void updateRadioStatus() {
      int iconIndex = this.iconIndex();
      if (this._iconIndex != iconIndex) {
         this._iconIndex = iconIndex;
         this._helper.doUpdates();
      }
   }
}
