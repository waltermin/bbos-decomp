package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.GANServiceZoneInfo;
import net.rim.device.api.system.GANStatusListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.internal.system.DataServices;

final class RoamingComponentFactory implements Factory, GANStatusListener, RadioStatusListener, TestPoint {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   private int _iconIndex;
   private boolean _hasUMA;
   private int _level;
   public static final int ROAMING_INDEX;
   public static final int ROAMING_OFF_CAMPUS_INDEX;
   public static final int ROAMING_NO_DATA_INDEX;
   public static final int ROAMING_NO_DATA_OFF_CAMPUS_INDEX;
   public static final int HOME_ZONE_INDEX;
   public static final int UMA_FCZ_INDEX;

   public final int getIconIndex(boolean fczAlways) {
      return fczAlways && this._hasUMA ? 5 : this._iconIndex;
   }

   final void init() {
      this.updateStatus();
      Application app = Application.getApplication();
      app.addRadioListener(this);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("Roaming", this);
   }

   final void updateStatus() {
      this.updateStatus(RadioInfo.getNetworkService());
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateStatus();
      this._helper.doUpdates();
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateStatus();
      this._helper.doUpdates();
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      this.updateStatus();
      this._helper.doUpdates();
   }

   @Override
   public final void radioTurnedOff() {
      this.updateStatus();
      this._helper.doUpdates();
   }

   @Override
   public final void signalLevel(int level) {
      if (level == -256 ^ this._level == -256) {
         this._level = level;
         this.updateStatus();
         this._helper.doUpdates();
      }
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void ganEventOccurred(int event, int status, int errorCause) {
      if (event == 5) {
         this.updateStatus(16384 | RadioInfo.getNetworkService());
         this._helper.doUpdates();
      }
   }

   @Override
   public final Object createInstance(Object initialData) {
      RoamingComponent rc = new RoamingComponent(this);
      this._helper.addComponentForUpdate(rc);
      return rc;
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         this._iconIndex = value - 1;
      }

      this._helper.doUpdates();
   }

   private final void updateStatus(int networkService) {
      this._hasUMA = false;
      this._iconIndex = -1;
      int dsMode = DataServices.getInstance().getMode();
      boolean dataServicesBlocked = dsMode != 0 && dsMode != 1;
      if ((networkService & 8) != 0) {
         if ((networkService & 512) == 0) {
            this._iconIndex = dataServicesBlocked ? 2 : 0;
         }
      } else if ((networkService & 16) != 0) {
         this._iconIndex = dataServicesBlocked ? 3 : 1;
      } else if ((networkService & 64) != 0) {
         this._iconIndex = 4;
      }

      if ((networkService & 16384) != 0) {
         this._hasUMA = true;
         GANServiceZoneInfo info = GAN.getGANServiceZoneInfo();
         if (info != null && info._serviceZoneIndicator == 1) {
            this._iconIndex = 5;
         }
      }
   }
}
