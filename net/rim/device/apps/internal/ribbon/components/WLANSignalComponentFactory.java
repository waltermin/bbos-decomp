package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANNetInfo;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

final class WLANSignalComponentFactory implements Factory, RadioStatusListener {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   private int _lastLevel = 0;
   boolean _numericDisplay;
   StringBuffer _valueString = (StringBuffer)(new Object(8));
   static final int RADIO_OFF_LEVEL = 0;
   static final int RADIO_NO_COVERAGE_LEVEL = 1;
   static final int RADIO_WEAK_LEVEL = 2;
   static final int RADIO_MEDIUM_LEVEL = 3;
   static final int RADIO_HIGH_LEVEL = 4;
   static final int NUMBER_OF_LEVELS = 5;

   final void updateSignalLevel() {
      int newLevel = this.signalToLevel(RadioInfo.getSignalLevel(4));
      if (newLevel != this._lastLevel) {
         if (this._numericDisplay) {
            this._valueString.setLength(0);
            this._valueString.append(newLevel);
         }

         this._lastLevel = newLevel;
         this._helper.doUpdates();
      }
   }

   final int getLevel(Object context) {
      if (!(context instanceof Object)) {
         return this._lastLevel;
      }

      WLANNetInfo netInfo = (WLANNetInfo)context;
      return this.signalToLevel(netInfo._signalLevel);
   }

   final void init() {
      if (WLAN.isSupported()) {
         Application app = Application.getApplication();
         app.addRadioListener(4, this);
         this.updateSignalLevel();
         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
         repos.addFactory("WLANSignalLevel", this);
      }
   }

   @Override
   public final Object createInstance(Object initialData) {
      WLANSignalLevelField wsf = new WLANSignalLevelField(this);
      this._helper.addComponentForUpdate(wsf);
      return wsf;
   }

   @Override
   public final void signalLevel(int level) {
      int newLevel = this.signalToLevel(level);
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
   public final void networkServiceChange(int networkId, int service) {
      this.updateSignalLevel();
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
   public final void baseStationChange() {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   private final int signalToLevel(int signal) {
      if (this._numericDisplay) {
         return signal;
      } else if (!WLAN.isRadioOn()) {
         return 0;
      } else if (signal == -256) {
         return 1;
      } else if (signal >= -60) {
         return 4;
      } else {
         return signal >= -80 ? 3 : 2;
      }
   }
}
