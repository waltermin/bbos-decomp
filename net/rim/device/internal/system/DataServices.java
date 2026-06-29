package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.PersistentInteger;

public final class DataServices implements RadioStatusListener, SystemListener2 {
   private int _mode;
   private int _modeWiFiRouter;
   private int _modeWiFiRelay;
   private boolean _roaming;
   public static final long GUID;
   public static final int LINK_TYPE_RF;
   public static final int LINK_TYPE_WIFI;
   public static final int LINK_TYPE_SERIAL;
   public static final int CONNECTION_TYPE_ANY;
   public static final int CONNECTION_TYPE_RELAY;
   public static final int CONNECTION_TYPE_ROUTER;
   public static final int MODE_UNSUPPORTED;
   public static final int MODE_OFF;
   public static final int MODE_ON;
   public static final int MODE_ROAM;
   public static final long MODE_RF_ID;
   public static final long MODE_WIFI_ROUTER_ID;
   public static final long MODE_WIFI_RELAY_ID;
   private static final int PERSIST_ID_MODE_RF = PersistentInteger.getId(-3556743465989743742L, RadioInternal.getDataServiceMode());
   private static final int PERSIST_ID_MODE_WIFI_ROUTER = PersistentInteger.getId(8849509471475052206L, 2);
   private static final int PERSIST_ID_MODE_WIFI_RELAY = PersistentInteger.getId(-3494193735767159191L, 2);
   private static DataServices _instance;

   public static final DataServices getInstance() {
      if (_instance != null) {
         return _instance;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (DataServices)ar.getOrWaitFor(-3556743465989743742L);
      if (_instance == null) {
         _instance = new DataServices();
         ar.put(-3556743465989743742L, _instance);
      }

      return _instance;
   }

   private DataServices() {
      EventLogger.register(-3556743465989743742L, "net.rim.DataServices", 2);
      this._mode = PersistentInteger.get(PERSIST_ID_MODE_RF);
      RadioInternal.setDataServiceMode(this._mode);
      this._roaming = (RadioInfo.getNetworkService() & 24) != 0;
      EventLogger.logEvent(-3556743465989743742L, 1229864240 + this._mode);
      this._modeWiFiRouter = PersistentInteger.get(PERSIST_ID_MODE_WIFI_ROUTER);
      EventLogger.logEvent(-3556743465989743742L, 1229864240 + this._modeWiFiRouter);
      this._modeWiFiRelay = PersistentInteger.get(PERSIST_ID_MODE_WIFI_RELAY);
      EventLogger.logEvent(-3556743465989743742L, 1229864240 + this._modeWiFiRelay);
      Proxy proxy = Proxy.getInstance();
      proxy.addRadioListener(this);
      proxy.addSystemListener(this);
   }

   public final boolean isDataServicesEnabled() {
      return this.isDataServicesEnabled(1, 1);
   }

   public final boolean isDataServicesEnabled(int linkType) {
      return this.isDataServicesEnabled(linkType, 0);
   }

   public final boolean isDataServicesEnabled(int linkType, int connectionType) {
      synchronized (this) {
         switch (linkType) {
            case 0:
               throw new IllegalArgumentException();
            case 1:
            default:
               switch (connectionType) {
                  case -1:
                     return false;
                  case 0:
                  case 1:
                  case 2:
                  default:
                     return this._mode == 1 || this._mode == 0 || this._mode == 3 && !this._roaming;
               }
            case 2:
               switch (connectionType) {
                  case -1:
                     return false;
                  case 0:
                     return this._mode != 2 && (this._modeWiFiRelay == 1 || this._modeWiFiRouter == 1);
                  case 1:
                  default:
                     return this._mode != 2 && this._modeWiFiRelay == 1;
                  case 2:
                     return this._mode != 2 && this._modeWiFiRouter == 1;
               }
            case 3:
               switch (connectionType) {
                  case 0:
                  case 2:
                     return true;
                  default:
                     return false;
               }
         }
      }
   }

   public final int getMode() {
      return this.getMode(1, 1);
   }

   public final int getMode(int linkType) {
      return this.getMode(linkType, 0);
   }

   public final int getMode(int linkType, int connectionType) {
      switch (linkType) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            switch (connectionType) {
               case -1:
                  return 2;
               case 0:
               case 1:
               case 2:
               default:
                  return this._mode;
            }
         case 2:
            switch (connectionType) {
               case -1:
                  return 2;
               case 0:
                  if (this._modeWiFiRelay != 1 && this._modeWiFiRouter != 1) {
                     return 2;
                  }

                  return 1;
               case 1:
               default:
                  return this._modeWiFiRelay;
               case 2:
                  return this._modeWiFiRouter;
            }
         case 3:
            switch (connectionType) {
               case 0:
               case 2:
                  return 1;
               default:
                  return 2;
            }
      }
   }

   public final void setMode(int mode) {
      this.setMode(mode, 1, 1);
   }

   public final void setMode(int mode, int linkType, int connectionType) {
      switch (linkType) {
         case 0:
            return;
         case 1:
         default:
            this.setModeRF(mode, 1);
            return;
         case 2:
            this.setModeWiFi(mode, connectionType);
      }
   }

   private final void setModeRF(int mode, int connectionType) {
      synchronized (this) {
         if (this._mode == mode) {
            return;
         }

         this._mode = mode;
      }

      EventLogger.logEvent(-3556743465989743742L, 1296252208 + mode);
      PersistentInteger.set(PERSIST_ID_MODE_RF, mode);
      RadioInternal.setDataServiceMode(mode);
      RIMGlobalMessagePoster.postGlobalEvent(-3556743465989743742L, 1, mode);
   }

   private final void setModeWiFi(int mode, int connectionType) {
      switch (connectionType) {
         case 0:
            return;
         case 1:
            synchronized (this) {
               if (this._modeWiFiRelay == mode) {
                  return;
               }

               this._modeWiFiRelay = mode;
            }

            EventLogger.logEvent(-3556743465989743742L, 1464089904 + mode);
            PersistentInteger.set(PERSIST_ID_MODE_WIFI_RELAY, mode);
            break;
         case 2:
         default:
            synchronized (this) {
               if (this._modeWiFiRouter == mode) {
                  return;
               }

               this._modeWiFiRouter = mode;
            }

            EventLogger.logEvent(-3556743465989743742L, 1162100016 + mode);
            PersistentInteger.set(PERSIST_ID_MODE_WIFI_ROUTER, mode);
      }

      RIMGlobalMessagePoster.postGlobalEvent(-3556743465989743742L, 2, mode);
   }

   public final boolean getRoaming() {
      return this._roaming;
   }

   private final void setRoaming(int service) {
      boolean roaming = (service & 24) != 0;
      synchronized (this) {
         if (this._roaming == roaming) {
            return;
         }

         this._roaming = roaming;
      }

      EventLogger.logEvent(-3556743465989743742L, 1380793648 + (roaming ? 1 : 0));
      RIMGlobalMessagePoster.postGlobalEvent(-3556743465989743742L);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.setRoaming(service);
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.setRoaming(service);
   }

   @Override
   public final void radioTurnedOff() {
      this.setRoaming(0);
   }

   @Override
   public final void fastReset() {
      RadioInternal.setDataServiceMode(this._mode);
      this.setRoaming(RadioInfo.getNetworkService());
   }

   @Override
   public final void powerUp() {
      this.setRoaming(RadioInfo.getNetworkService());
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
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
   public final void powerOff() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }
}
