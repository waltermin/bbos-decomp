package net.rim.device.apps.internal.options.items;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.i18n.DateTimeFormatOptions;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.timesync.TimeSync;
import net.rim.vm.PersistentInteger;

final class DateTimeController implements FieldChangeListener, GlobalEventListener, RealtimeClockListener {
   private TimeService _timeService;
   private TimeSync _timeSync;
   private DateTimeController$UpdateTimeVerb _updateTimeVerb;
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
   private boolean _timeZoneGuessed = false;
   private int _tzGuessedID = PersistentInteger.getId(4039358504000129822L, 0);
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
   private Manager _content;
   private DateField _rawNetworkTimeField;
   private boolean _isWizard;
   private int _timeZoneLocale;
   private static final int NONE;
   private static final int DATE;
   private static final int TIME;
   private static final int OFF;
   private static final int BLACKBERRY;
   private static final int NETWORK;
   private static final long TIME_ZONE_BEST_GUESS;
   private static final short TIME_ZONE_CASABLANCA;

   public DateTimeController() {
      this(false);
   }

   public DateTimeController(boolean isWizard) {
      this._isWizard = isWizard;
   }

   protected final void populateMainScreen(MainScreen mainScreen, Manager content) {
      this._content = content;
      if (this._networkTimeSupport) {
         this._networkTimeValid = InternalServices.isNetworkTimeValid() && RadioInfo.getState() == 1;
      } else {
         this._networkTimeValid = false;
      }

      long currentTime = System.currentTimeMillis();
      this._dateField = (DateField)(new Object(OptionsResources.getString(501), currentTime, DateFormat.getInstance(40)));
      this._timeField = (DateField)(new Object(CommonResources.getString(2000), currentTime, DateFormat.getInstance(6)));
      this._timeZoneField = (ChoiceField)(new Object(CommonResources.getString(2013), this._shortTimeZoneNames));
      this._timeZoneField.setFont(mainScreen.getFontIfSet());
      this._timeFormatField = (ObjectChoiceField)(new Object(OptionsResources.getString(502), null));
      this._timeFormatField.setFont(mainScreen.getFontIfSet());
      this._timeZoneDescriptionField = (RichTextField)(new Object(null, 36028797019226112L));
      if (this._isWizard) {
         int fontSize = Ui.convertSize(7, 3, 0);
         this._timeZoneDescriptionField.setFont(content.getFont().derive(0, fontSize));
      }

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

      this._timeSyncSourceField = (ObjectChoiceField)(new Object(OptionsResources.getString(1448), sourceOptions, index));
      this.populateTimeFormats();
      int initialTZIndex = this._timeService.getTimeZoneIndex(this._timeService.getDefaultTimeZoneID());
      this._timeZoneField.setSelectedIndex(initialTZIndex);
      this._timeZoneDescriptionField.setText(this._longTimeZoneNames[initialTZIndex]);
      content.add(this._timeZoneField);
      content.add(this._timeZoneDescriptionField);
      content.add(this._timeField);
      content.add(this._timeFormatField);
      content.add(this._dateField);
      if (!this._isWizard) {
         content.add(this._timeSyncSourceField);
      }

      this._networkDateField = null;
      this._networkTimeField = null;
      if (this._networkTimeSupport && !this._isWizard) {
         content.add((Field)(new Object()));
         if (!this._networkTimeValid) {
            content.add((Field)(new Object(OptionsResources.getString(1807), 64)));
         } else {
            long networkTime = TimeSync.GetNetworkTime(currentTime);
            if (this._networkTimeZoneSupport) {
               networkTime = TimeSync.ApplyNetworkTimeZone(networkTime);
            }

            this._networkTimeField = new DateTimeController$NetworkTimeField(OptionsResources.getString(1804), networkTime, DateFormat.getInstance(6));
            this._networkDateField = new DateTimeController$NetworkTimeField(OptionsResources.getString(1805), networkTime, DateFormat.getInstance(40));
            if (this._networkTimeZoneSupport) {
               TimeZone GMT = TimeZone.getTimeZone(DateTimeUtilities.GMT);
               this._networkTimeField.setTimeZone(GMT);
               this._networkDateField.setTimeZone(GMT);
            }

            content.add(this._networkTimeField);
            content.add(this._networkDateField);
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

   private final void addRawNetworkTimeFields() {
      if (this._networkTimeSupport) {
         this._rawNetworkTimeField = new DateTimeController$NetworkTimeField(
            "Network UTC", TimeSync.GetNetworkTime(System.currentTimeMillis()), DateFormat.getInstance(6)
         );
         this._rawNetworkTimeField.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         this._content.add((Field)(new Object()));
         this._content.add(this._rawNetworkTimeField);
         if (this._networkTimeZoneSupport) {
            this._content
               .add(
                  (Field)(new Object(
                     ((StringBuffer)(new Object("Network TZ Offset:  ")))
                        .append(this.millisToHourString(InternalServices.getNetworkTimeZoneOffset()))
                        .append(" hours")
                        .toString()
                  ))
               );
            this._content
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

   protected final void updateTimeZoneNames() {
      this._shortTimeZoneNames = this._timeService.getTimeZoneNamesShort();
      this._longTimeZoneNames = this._timeService.getTimeZoneNamesLong();
      this._timeZoneLocale = Locale.getDefault().getCode();
   }

   protected final void initialize() {
      this._networkTimeZoneSupport = InternalServices.isNetworkTimeZoneSupported();
      this._networkTimeSupport = InternalServices.isNetworkTimeSupported();
      if (this._timeService == null) {
         this._timeService = TimeService.getTimeService();
      }

      this.updateTimeZoneNames();
      if (this._cal == null) {
         this._cal = Calendar.getInstance();
      }
   }

   protected final void beforeOpen() {
      if (this._timeSync == null) {
         this._timeSync = TimeSync.getInstance();
      }

      this._initialTimeSource = this._timeSync.getSource();
      this._updateTimeVerb = new DateTimeController$UpdateTimeVerb(this._timeSync);
      if (this._timeZoneLocale != Locale.getDefault().getCode()) {
         this.updateTimeZoneNames();
      }
   }

   protected final boolean save() {
      DateTimeFormatOptions.setTimeFormat(this._timeFormatField.getSelectedIndex());
      this._timeService.setDefaultTimeZone(this._timeService.getTimeZoneIDFromIndex(this._timeZoneField.getSelectedIndex()));
      if (this._timeZoneGuessed) {
         PersistentInteger.set(this._tzGuessedID, -1);
         this._timeZoneGuessed = false;
      }

      if (this._timeSyncSourceField.isDirty() && this._timeSync.getSource() != this._initialTimeSource && this._timeSync.getSource() != 0) {
         this._timeSync.synchronize(true);
         return true;
      } else {
         Calendar cal = this._cal;
         ((CalendarExtensions)cal).setTimeLong(this._dateField.getDate());
         cal.set(13, 0);
         cal.set(14, 0);
         DeviceInternal.setDateTime(((CalendarExtensions)cal).getTimeLong());
         return true;
      }
   }

   protected final boolean discard() {
      DateTimeFormatOptions.setTimeFormat(this._initialTimeFormat);
      this._timeSync.setSource(this._initialTimeSource);
      return true;
   }

   public final void bestGuessTimeZone() {
      int[] tzUIDs = this._timeService.getTimeZoneUIDs();
      if (PersistentInteger.get(this._tzGuessedID) == 0
         && tzUIDs[this._timeZoneField.getSelectedIndex()] == 90
         && InternalServices.isNetworkTimeZoneSupported()) {
         int gmtOffSet = InternalServices.getNetworkTimeZoneOffset();
         int dstOffSet = InternalServices.getNetworkDSTOffset();
         if (gmtOffSet < 0) {
            dstOffSet *= -1;
         }

         gmtOffSet += dstOffSet;
         gmtOffSet /= 60000;

         for (int i = 0; i <= tzUIDs.length - 1; i++) {
            if (this._timeService.getTimeZoneInfo(tzUIDs[i]).getGMTOffset() == gmtOffSet) {
               this._timeZoneGuessed = true;
               this._timeZoneField.setSelectedIndex(i);
               return;
            }
         }
      }
   }

   public final void beforeClose() {
      UiApplication.getUiApplication().removeGlobalEventListener(this);
      UiApplication.getUiApplication().removeRealtimeClockListener(this);
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
}
