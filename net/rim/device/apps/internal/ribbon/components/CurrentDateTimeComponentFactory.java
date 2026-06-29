package net.rim.device.apps.internal.ribbon.components;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.cldc.util.CalendarExtensions;

final class CurrentDateTimeComponentFactory implements Factory, GlobalEventListener, RealtimeClockListener, SystemListener {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   private GlobalListenerFactoryHelper _globalHelper = (GlobalListenerFactoryHelper)(new Object());
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private StringBuffer _lastDate = (StringBuffer)(new Object());
   private SimpleDateFormat _dateFormat;
   private StringBuffer _lastTime = (StringBuffer)(new Object());
   private StringBuffer _lastTimeNoAmPm = (StringBuffer)(new Object());
   private StringBuffer _lastAmPm = (StringBuffer)(new Object());
   private SimpleDateFormat _timeFormat;
   private SimpleDateFormat _timeFormatNoAmPm;
   private SimpleDateFormat _formatAmPm;
   private StringBuffer _lastDateTime = (StringBuffer)(new Object());
   private Calendar _cachedCal;
   private CalendarExtensions _cachedCalEx;
   static final int INVALID_TYPE = 0;
   static final int DATE_TIME_TYPE = 1;
   static final int DATE_TYPE = 2;
   static final int TIME_TYPE = 3;

   final void init() {
      Application app = Application.getApplication();
      app.addRealtimeClockListener(this);
      app.addGlobalEventListener(this);
      app.addSystemListener(this);
      this.updateFormats();
      this.clockUpdated();
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("CurrentDateTime", this);
   }

   final StringBuffer getBuffer(int type) {
      switch (type) {
         case 0:
            return null;
         case 1:
         default:
            return this._lastDateTime;
         case 2:
            return this._lastDate;
         case 3:
            return this._lastTime;
      }
   }

   final StringBuffer getTimeNoAmPmBuffer() {
      return this._lastTimeNoAmPm;
   }

   final StringBuffer getAmPmBuffer() {
      return this._lastAmPm;
   }

   private final void updateFormats() {
      this._dateFormat = (SimpleDateFormat)(new Object(this._rbf.getString(11)));
      this._timeFormat = (SimpleDateFormat)(new Object(6));
      String timeFormatPattern = this._timeFormat.getPattern();
      int amPmIndex = timeFormatPattern.indexOf(97);

      while (amPmIndex > 0 && timeFormatPattern.charAt(amPmIndex - 1) == ' ') {
         amPmIndex--;
      }

      if (amPmIndex == -1) {
         this._timeFormatNoAmPm = this._timeFormat;
         this._formatAmPm = null;
      } else {
         this._timeFormatNoAmPm = (SimpleDateFormat)(new Object(timeFormatPattern.substring(0, amPmIndex)));
         this._formatAmPm = (SimpleDateFormat)(new Object(timeFormatPattern.substring(amPmIndex)));
      }

      DateFormat.getInstance(54);
      this._cachedCal = Calendar.getInstance();
      this._cachedCalEx = (CalendarExtensions)this._cachedCal;
      this.clockUpdated();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3596208183088439728L || guid == 8877632280522743328L || guid == 7207871974803693937L) {
         this.updateFormats();
      } else if (guid == -7464003439710973532L) {
         this.updateFormats();
      }

      this._globalHelper.eventOccurred(guid, data0, data1, object0, object1);
   }

   @Override
   public final void clockUpdated() {
      this._cachedCalEx.setTimeLong(System.currentTimeMillis());
      synchronized (this._lastTime) {
         this._lastTime.setLength(0);
         this._timeFormat.format(this._cachedCal, this._lastTime, null);
         this._lastTimeNoAmPm.setLength(0);
         this._timeFormatNoAmPm.format(this._cachedCal, this._lastTimeNoAmPm, null);
         this._lastAmPm.setLength(0);
         if (this._formatAmPm != null) {
            this._formatAmPm.format(this._cachedCal, this._lastAmPm, null);
         }
      }

      StringBuffer lastDate = this._lastDate;
      synchronized (lastDate) {
         lastDate.setLength(0);
         this._dateFormat.format(this._cachedCal, lastDate, null);

         for (int lv = lastDate.length() - 1; lv >= 0; lv--) {
            lastDate.setCharAt(lv, Character.toUpperCase(lastDate.charAt(lv)));
         }
      }

      synchronized (this._lastDateTime) {
         this._lastDateTime.setLength(0);
         this._timeFormat.format(this._cachedCal, this._lastDateTime, null);
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
   public final Object createInstance(Object initialData) {
      CurrentDateTimeField dateTime = new CurrentDateTimeField(this);
      this._helper.addComponentForUpdate(dateTime);
      this._globalHelper.addComponentForUpdate(dateTime);
      return dateTime;
   }
}
