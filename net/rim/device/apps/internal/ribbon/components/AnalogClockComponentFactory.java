package net.rim.device.apps.internal.ribbon.components;

import java.util.Calendar;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.cldc.util.CalendarExtensions;

final class AnalogClockComponentFactory implements Factory, GlobalEventListener, RealtimeClockListener, SystemListener2, Runnable {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   private Calendar _cachedCal;
   private CalendarExtensions _cachedCalEx;
   char _hour;
   char _minute;
   char _second;
   int _timerid = -1;
   boolean _trackSeconds;
   Application _application;

   final void init() {
      this._application = Application.getApplication();
      this._application.addRealtimeClockListener(this);
      this._application.addGlobalEventListener(this);
      this._application.addSystemListener(this);
      this.updateFormats();
      this.clockUpdated();
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("AnalogClock", this);
   }

   final void trackSeconds(boolean trackSeconds) {
      this._trackSeconds = trackSeconds;
      if (this._trackSeconds && this._timerid == -1 && Backlight.isEnabled()) {
         this._timerid = this._application.invokeLater(this, 1000, true);
         this.update();
      } else {
         if (!this._trackSeconds && this._timerid != -1) {
            this._application.cancelInvokeLater(this._timerid);
            this._timerid = -1;
         }
      }
   }

   public final synchronized void update() {
      this._cachedCalEx.setTimeLong(System.currentTimeMillis());
      int minute = this._cachedCal.get(12);
      this._hour = (char)(48 + this._cachedCal.get(10) * 5 + minute / 12);
      this._minute = (char)(48 + minute);
      this._second = (char)(48 + this._cachedCal.get(13));
      this._helper.doUpdates();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 3596208183088439728L && guid != 8877632280522743328L && guid != 7207871974803693937L) {
         if (guid == -7464003439710973532L) {
            this.updateFormats();
         }
      } else {
         this.updateFormats();
      }
   }

   @Override
   public final synchronized void clockUpdated() {
      this.update();
   }

   @Override
   public final void run() {
      this._second++;
      if (this._second == 'l') {
         this._second = '0';
      }

      this._helper.doUpdates();
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
      this.clockUpdated();
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void fastReset() {
   }

   @Override
   public final void backlightStateChange(boolean on) {
      if (this._trackSeconds) {
         if (on && this._timerid == -1) {
            this._timerid = this._application.invokeLater(this, 1000, true);
            this.update();
         } else {
            if (!on && this._timerid != -1) {
               this._application.cancelInvokeLater(this._timerid);
               this._timerid = -1;
            }
         }
      }
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final Object createInstance(Object initialData) {
      AnalogClockComponentFactory$AnalogClock analogClock = new AnalogClockComponentFactory$AnalogClock(this);
      this._helper.addComponentForUpdate(analogClock);
      return analogClock;
   }

   private final void updateFormats() {
      this._cachedCal = Calendar.getInstance();
      this._cachedCalEx = (CalendarExtensions)this._cachedCal;
      this.update();
   }
}
