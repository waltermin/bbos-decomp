package net.rim.device.apps.internal.options.items;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.i18n.DateTimeFormatOptions;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.timesync.TimeSync;

public final class DateTimeOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener, GlobalEventListener, RealtimeClockListener {
   private TimeService _timeService;
   private TimeSync _timeSync;
   private DateTimeOptionsItem$UpdateTimeVerb _updateTimeVerb;
   private String[] _shortTimeZoneNames;
   private String[] _longTimeZoneNames;
   private String[] _timeFormats;
   private int _initialTimeFormat;
   private int _initialTimeSource;
   private int _currentYear;
   private int _currentMonth;
   private int _currentDay;
   private int _currentHour;
   private int _currentMinute;
   private DateField _dateField;
   private DateField _timeField;
   private ChoiceField _timeZoneField;
   private RichTextField _timeZoneDescriptionField;
   private ObjectChoiceField _timeFormatField;
   private ObjectChoiceField _timeSyncSourceField;
   private Calendar _cal;
   private DateField _networkDateField;
   private DateField _networkTimeField;
   private boolean _networkTimeZoneSupport;
   private boolean _networkTimeSupport;
   private boolean _networkTimeValid;
   private MainScreen _mainScreen;
   private DateField _rawNetworkTimeField;
   private CheckboxField _autoAdjustForDSTField;
   private static final int NONE = 0;
   private static final int DATE = 1;
   private static final int TIME = 2;
   private static final int OFF = 0;
   private static final int BLACKBERRY = 1;
   private static final int NETWORK = 2;

   public DateTimeOptionsItem() {
      super(OptionsResources.getString(500));
      ContextObject.put(super._context, 244, "date_and_time");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._mainScreen = mainScreen;
      if (this._networkTimeSupport) {
         this._networkTimeValid = InternalServices.isNetworkTimeValid() && RadioInfo.getState() == 1;
      } else {
         this._networkTimeValid = false;
      }

      long currentTime = System.currentTimeMillis();
      this._dateField = (DateField)(new Object(OptionsResources.getString(501), currentTime, DateFormat.getInstance(40)));
      this._timeField = (DateField)(new Object(CommonResources.getString(2000), currentTime, DateFormat.getInstance(6)));
      this._timeZoneField = (ChoiceField)(new Object(CommonResources.getString(2013), this._shortTimeZoneNames, 0, 134217728));
      this._timeFormatField = (ObjectChoiceField)(new Object(OptionsResources.getString(502), null, 0, 134217728));
      this._timeZoneDescriptionField = (RichTextField)(new Object(null, 36028797019226112L));
      int index;
      switch (this._timeSync.getSource()) {
         case 1:
            index = 0;
            break;
         case 2:
            index = 1;
            break;
         case 3:
         default:
            index = 2;
      }

      String[] sourceOptions = OptionsResources.getStringArray(1943);
      if ((!this._networkTimeSupport || !InternalServices.isNetworkTimeValid()) && sourceOptions.length > 2) {
         String[] tempArray = new Object[sourceOptions.length - 1];
         System.arraycopy(sourceOptions, 0, tempArray, 0, sourceOptions.length - 1);
         sourceOptions = tempArray;
      }

      if (index >= sourceOptions.length) {
         index = sourceOptions.length - 1;
         this.setTimeSyncSource(index);
      }

      this._timeSyncSourceField = (ObjectChoiceField)(new Object(OptionsResources.getString(1448), sourceOptions, index, 134217728));
      this.populateTimeFormats();
      int initialTZIndex = this._timeService.getTimeZoneIndex(this._timeService.getDefaultTimeZoneID());
      this._timeZoneField.setSelectedIndex(initialTZIndex);
      this._timeZoneDescriptionField.setText(this._longTimeZoneNames[initialTZIndex]);
      this._mainScreen.add(this._timeZoneField);
      this._mainScreen.add(this._timeZoneDescriptionField);
      this._mainScreen.add(this._timeField);
      this._mainScreen.add(this._timeFormatField);
      this._mainScreen.add(this._dateField);
      this._mainScreen.add(this._timeSyncSourceField);
      this._networkDateField = null;
      this._networkTimeField = null;
      if (this._networkTimeSupport) {
         this._mainScreen.add((Field)(new Object()));
         if (!this._networkTimeValid) {
            this._mainScreen.add((Field)(new Object(OptionsResources.getString(1807), 64)));
         } else {
            long networkTime = TimeSync.GetNetworkTime(currentTime);
            if (this._networkTimeZoneSupport) {
               networkTime = TimeSync.ApplyNetworkTimeZone(networkTime);
            }

            this._networkTimeField = new DateTimeOptionsItem$NetworkTimeField(OptionsResources.getString(1804), networkTime, DateFormat.getInstance(6));
            this._networkDateField = new DateTimeOptionsItem$NetworkTimeField(OptionsResources.getString(1805), networkTime, DateFormat.getInstance(40));
            if (this._networkTimeZoneSupport) {
               TimeZone GMT = TimeZone.getTimeZone(DateTimeUtilities.GMT);
               this._networkTimeField.setTimeZone(GMT);
               this._networkDateField.setTimeZone(GMT);
            }

            this._mainScreen.add(this._networkTimeField);
            this._mainScreen.add(this._networkDateField);
            if (this._rawNetworkTimeField != null) {
               this.addRawNetworkTimeFields();
            }
         }
      }

      this._timeZoneField.setChangeListener(this);
      this._timeFormatField.setChangeListener(this);
      this._timeField.setChangeListener(this);
      this._dateField.setChangeListener(this);
      this._timeSyncSourceField.setChangeListener(this);
      this.update(0, 0, 0, 3);
      UiApplication.getUiApplication().addGlobalEventListener(this);
      UiApplication.getUiApplication().addRealtimeClockListener(this);
   }

   private final String millisToHourString(long millis) {
      return String.valueOf(millis / 1000 / 60 / 60);
   }

   private final String millisToMinuteString(long millis) {
      return String.valueOf(millis / 1000 / 60);
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1094996820:
            this.addAutomaticClockAdjustmentForDSTField();
            return true;
         case 1280265295:
            if (this._rawNetworkTimeField == null) {
               this.addRawNetworkTimeFields();
            }

            return true;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }

   private final void addAutomaticClockAdjustmentForDSTField() {
      this._autoAdjustForDSTField = (CheckboxField)(new Object(OptionsResources.getString(2004), TimeService.getTimeService().automaticClockAdjustmentForDST()));
      this._mainScreen.add((Field)(new Object()));
      this._mainScreen.add(this._autoAdjustForDSTField);
   }

   private final void addRawNetworkTimeFields() {
      if (this._networkTimeSupport) {
         this._rawNetworkTimeField = new DateTimeOptionsItem$NetworkTimeField(
            "Network UTC", TimeSync.GetNetworkTime(System.currentTimeMillis()), DateFormat.getInstance(6)
         );
         this._rawNetworkTimeField.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         this._mainScreen.add((Field)(new Object()));
         this._mainScreen.add(this._rawNetworkTimeField);
         if (this._networkTimeZoneSupport) {
            this._mainScreen
               .add(
                  (Field)(new Object(
                     ((StringBuffer)(new Object("Network TZ Offset:  ")))
                        .append(this.millisToHourString(InternalServices.getNetworkTimeZoneOffset()))
                        .append(" hours")
                        .toString()
                  ))
               );
            this._mainScreen
               .add(
                  (Field)(new Object(
                     ((StringBuffer)(new Object("Network DST Offset: ")))
                        .append(this.millisToMinuteString(InternalServices.getNetworkDSTOffset()))
                        .append(" minutes")
                        .toString()
                  ))
               );
         }
      }
   }

   private final void populateTimeFormats() {
      this._timeFormats = DateTimeFormatOptions.getTimeFormats();
      FieldChangeListener oldListener = this._timeFormatField.getChangeListener();
      this._timeFormatField.setChangeListener(null);
      this._timeFormatField.setChoices(this._timeFormats);
      this._initialTimeFormat = DateTimeFormatOptions.getTimeFormat();
      if (this._initialTimeFormat >= this._timeFormats.length) {
         this._initialTimeFormat = 0;
         DateTimeFormatOptions.setTimeFormat(0);
      }

      this._timeFormatField.setSelectedIndex(this._initialTimeFormat);
      this._timeFormatField.setChangeListener(oldListener);
   }

   @Override
   protected final void initialize() {
      super.initialize();
      this._networkTimeZoneSupport = InternalServices.isNetworkTimeZoneSupported();
      this._networkTimeSupport = InternalServices.isNetworkTimeSupported();
      if (this._timeService == null) {
         this._timeService = TimeService.getTimeService();
      }

      if (this._shortTimeZoneNames == null) {
         this._shortTimeZoneNames = this._timeService.getTimeZoneNamesShort();
      }

      if (this._longTimeZoneNames == null) {
         this._longTimeZoneNames = this._timeService.getTimeZoneNamesLong();
      }

      if (this._cal == null) {
         this._cal = Calendar.getInstance();
      }
   }

   @Override
   protected final void open() {
      if (this._timeSync == null) {
         this._timeSync = TimeSync.getInstance();
      }

      this._initialTimeSource = this._timeSync.getSource();
      this._updateTimeVerb = new DateTimeOptionsItem$UpdateTimeVerb(this._timeSync);
      super.open();
   }

   @Override
   protected final void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(4115625230054951442L);
      Verb[] factoryVerbs = verbRepository.getVerbs(null);
      if (factoryVerbs != null && factoryVerbs.length > 0) {
         verbToMenu.addVerbs(factoryVerbs);
      }
   }

   @Override
   protected final boolean save() {
      DateTimeFormatOptions.setTimeFormat(this._timeFormatField.getSelectedIndex());
      this._timeService.setDefaultTimeZone(this._timeService.getTimeZoneIDFromIndex(this._timeZoneField.getSelectedIndex()));
      if (this._timeSyncSourceField.isDirty() && this._timeSync.getSource() != this._initialTimeSource && this._timeSync.getSource() != 0) {
         this._timeSync.synchronize(true);
      } else if (this._dateField.isDirty() || this._timeField.isDirty()) {
         Calendar cal = this._cal;
         ((CalendarExtensions)cal).setTimeLong(this._timeField.getDate());
         cal.set(13, 0);
         cal.set(14, 0);
         DeviceInternal.setDateTime(((CalendarExtensions)cal).getTimeLong());
      }

      if (this._autoAdjustForDSTField != null && this._autoAdjustForDSTField.isDirty()) {
         TimeService.getTimeService().setAutomaticClockAdjustmentForDST(this._autoAdjustForDSTField.getChecked());
      }

      return super.save();
   }

   @Override
   protected final boolean discard() {
      DateTimeFormatOptions.setTimeFormat(this._initialTimeFormat);
      this._timeSync.setSource(this._initialTimeSource);
      return super.discard();
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean close = super.confirm(verb, context);
      if (close) {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
         UiApplication.getUiApplication().removeRealtimeClockListener(this);
      }

      return close;
   }

   private final void update(long newDateAndTime, int update, int restore, int preserve) {
      boolean dateDirty = this._dateField.isDirty();
      boolean timeDirty = this._timeField.isDirty();
      if ((update & 1) != 0) {
         this._dateField.setDate(newDateAndTime);
      }

      if ((update & 2) != 0) {
         this._timeField.setDate(newDateAndTime);
      }

      Calendar cal = this._cal;
      if (restore == 1) {
         ((CalendarExtensions)cal).setTimeLong(this._timeField.getDate());
         cal.set(1, this._currentYear);
         cal.set(2, this._currentMonth);
         cal.set(5, this._currentDay);
         this._dateField.setDate(((CalendarExtensions)cal).getTimeLong());
      }

      if (restore == 2) {
         ((CalendarExtensions)cal).setTimeLong(this._dateField.getDate());
         cal.set(11, this._currentHour);
         cal.set(12, this._currentMinute);
         cal.set(13, 0);
         cal.set(14, 0);
         this._timeField.setDate(((CalendarExtensions)cal).getTimeLong());
      }

      if (restore == 3) {
         cal.set(1, this._currentYear);
         cal.set(2, this._currentMonth);
         cal.set(5, this._currentDay);
         cal.set(11, this._currentHour);
         cal.set(12, this._currentMinute);
         cal.set(13, 0);
         cal.set(14, 0);
         this._dateField.setDate(((CalendarExtensions)cal).getTimeLong());
         this._timeField.setDate(((CalendarExtensions)cal).getTimeLong());
      }

      if (preserve != 0) {
         if ((preserve & 1) != 0) {
            ((CalendarExtensions)cal).setTimeLong(this._timeField.getDate());
            this._currentYear = cal.get(1);
            this._currentMonth = cal.get(2);
            this._currentDay = cal.get(5);
         }

         if ((preserve & 2) != 0) {
            ((CalendarExtensions)cal).setTimeLong(this._dateField.getDate());
            this._currentHour = cal.get(11);
            this._currentMinute = cal.get(12);
         }
      }

      this._dateField.setDirty(dateDirty);
      this._timeField.setDirty(timeDirty);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._timeZoneField) {
         int selectedTimeZoneIndex = this._timeZoneField.getSelectedIndex();
         TimeZone tz = this._timeService.getTimeZone(this._timeService.getTimeZoneIDFromIndex(selectedTimeZoneIndex));
         this._timeZoneDescriptionField.setText(this._longTimeZoneNames[selectedTimeZoneIndex]);
         this._timeField.setTimeZone(tz);
         this._dateField.setTimeZone(tz);
         this._cal.setTimeZone(tz);
         if (!this._networkTimeZoneSupport) {
            if (this._networkDateField != null) {
               this._networkDateField.setTimeZone(tz);
            }

            if (this._networkTimeField != null) {
               this._networkTimeField.setTimeZone(tz);
            }
         }

         if (!this._timeField.isDirty() && !this._dateField.isDirty()) {
            this.update(0, 0, 0, 3);
         } else {
            this.update(0, 0, 3, 0);
         }
      } else {
         if (field == this._timeFormatField) {
            DateTimeFormatOptions.setTimeFormat(this._timeFormatField.getSelectedIndex());
            this._timeField.setFormat(DateFormat.getInstance(6));
            if (this._networkTimeField != null) {
               this._networkTimeField.setFormat(DateFormat.getInstance(6));
               return;
            }
         } else if (field == this._timeField) {
            if ((context & -2147483648) == 0) {
               this.update(this._timeField.getDate(), 1, 1, 2);
               return;
            }
         } else if (field == this._dateField) {
            if ((context & -2147483648) == 0) {
               this.update(this._dateField.getDate(), 2, 2, 1);
               return;
            }
         } else if (field == this._timeSyncSourceField) {
            this.setTimeSyncSource(this._timeSyncSourceField.getSelectedIndex());
         }
      }
   }

   private final void setTimeSyncSource(int index) {
      int currentSource;
      switch (index) {
         case 0:
            currentSource = 0;
            break;
         case 1:
            currentSource = 2;
            break;
         case 2:
         default:
            currentSource = 3;
      }

      this._timeSync.setSource(currentSource);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this.populateTimeFormats();
         this._timeField.setFormat(DateFormat.getInstance(6));
      }
   }

   @Override
   public final void clockUpdated() {
      long currentTime = System.currentTimeMillis();
      if (this._updateTimeVerb._updateTimeRequested || !this._timeField.isDirty() && !this._dateField.isDirty()) {
         if (this._updateTimeVerb._updateTimeRequested) {
            this._dateField.setDirty(false);
            this._timeField.setDirty(false);
            this._timeSyncSourceField.setDirty(false);
            this._updateTimeVerb._updateTimeRequested = false;
         }

         this.update(currentTime, 3, 0, 3);
      }

      if (this._networkTimeValid) {
         long networkTime = TimeSync.GetNetworkTime(currentTime);
         if (this._rawNetworkTimeField != null) {
            this._rawNetworkTimeField.setDate(networkTime);
         }

         if (this._networkTimeZoneSupport) {
            networkTime = TimeSync.ApplyNetworkTimeZone(networkTime);
         }

         if (this._networkDateField != null) {
            this._networkDateField.setDate(networkTime);
         }

         if (this._networkTimeField != null) {
            this._networkTimeField.setDate(networkTime);
         }
      }
   }

   @Override
   protected final void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      super.populateMenuVerbs(verbToMenu, instance);
      if (this._timeSync.isAvailable()) {
         verbToMenu.addVerb(this._updateTimeVerb);
      }
   }
}
