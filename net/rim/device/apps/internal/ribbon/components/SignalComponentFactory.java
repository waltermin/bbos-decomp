package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.internal.system.RadioInternal;

final class SignalComponentFactory implements Factory, RadioStatusListener {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   private int _lastLevel = 0;
   boolean _numericDisplay;
   StringBuffer _valueString = (StringBuffer)(new Object(8));
   static final int RADIO_OFF_LEVEL;
   static final int RADIO_NO_COVERAGE_LEVEL;
   static final int RADIO_SOS_COVERAGE_LEVEL;
   static final int NUMBER_OF_LEVELS;

   public SignalComponentFactory() {
   }

   final int getLevel() {
      return this._lastLevel;
   }

   private final int signalToLevel(int signal, int activeNetwork) {
      if (this._numericDisplay) {
         return signal;
      }

      if (RadioInfo.getState() == 0) {
         return 0;
      }

      if ((activeNetwork & 2) != 0) {
         if (signal == -256) {
            return 1;
         } else if (signal > -83) {
            return 7;
         } else if (signal > -90) {
            return 6;
         } else if (signal > -98) {
            return 5;
         } else if (signal > -105) {
            return 4;
         } else {
            return signal > -109 ? 3 : 2;
         }
      } else if ((activeNetwork & 8) != 0) {
         if (signal == -256) {
            return 1;
         } else {
            int sqeLevel = IDENInfo.getSQELevel();
            if (sqeLevel > 30 && signal > -88) {
               return 7;
            } else if (sqeLevel > 27 && signal > -91) {
               return 6;
            } else if (sqeLevel > 24 && signal > -94) {
               return 5;
            } else if (sqeLevel > 21 && signal > -100) {
               return 4;
            } else {
               return sqeLevel > 19 && signal > -103 ? 3 : 2;
            }
         }
      } else if ((activeNetwork & 1) != 0) {
         if ((RadioInternal.getActiveRadios() & 1) == 0 && (RadioInfo.getNetworkService() & 16384) == 0 && GAN.isGANAllowed()) {
            return 0;
         } else if (signal == -256) {
            return 1;
         } else if (signal >= -77) {
            return 7;
         } else if (signal >= -86) {
            return 6;
         } else if (signal >= -92) {
            return 5;
         } else {
            return signal >= -101 ? 4 : 3;
         }
      } else if (signal == -256) {
         return 1;
      } else if (signal >= -50) {
         return 7;
      } else if (signal >= -60) {
         return 6;
      } else if (signal >= -70) {
         return 5;
      } else {
         return signal >= -80 ? 4 : 3;
      }
   }

   final void init() {
      Application app = Application.getApplication();
      app.addRadioListener(this);
      this.updateSignalLevel();
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("SignalLevel", this);
   }

   final void updateSignalLevel() {
      this.updateSignalLevel(RadioInfo.getActiveWAFs());
   }

   private final void updateSignalLevel(int activeNetwork) {
      int newLevel = this.signalToLevel(RadioInfo.getSignalLevel(), activeNetwork);
      if ((activeNetwork & 1) != 0) {
         boolean emergencyOnly = (RadioInfo.getNetworkService() & 1) != 0;
         if (emergencyOnly && !this._numericDisplay) {
            byte[] suppressSOS = Branding.getData(17);
            if (suppressSOS != null && suppressSOS.length > 0 && suppressSOS[0] != 0) {
               newLevel = 2;
            } else {
               newLevel = 8;
            }
         }
      }

      if (newLevel != this._lastLevel) {
         if (this._numericDisplay) {
            this._valueString.setLength(0);
            this._valueString.append(newLevel);
         }

         this._lastLevel = newLevel;
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
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateSignalLevel();
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateSignalLevel();
   }

   @Override
   public final void radioTurnedOff() {
      this.updateSignalLevel();
   }

   @Override
   public final void signalLevel(int level) {
      int activeNetwork = RadioInfo.getActiveWAFs();
      int newLevel = this.signalToLevel(level, activeNetwork);
      if (newLevel != this._lastLevel) {
         this.updateSignalLevel(activeNetwork);
      }
   }

   @Override
   public final Object createInstance(Object initialData) {
      SignalLevelField sf = new SignalLevelField(this);
      this._helper.addComponentForUpdate(sf);
      return sf;
   }
}
