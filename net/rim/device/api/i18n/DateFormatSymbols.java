package net.rim.device.api.i18n;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.i18n.DateTimeFormatOptions;

public final class DateFormatSymbols implements Persistable {
   private int _localecode;
   private String[] _ampm;
   private String[] _ampm_short;
   private String[] _dateFormats;
   private String _datetimeFormat;
   private String[] _months;
   private String[] _months_short;
   private String[] _timeFormats;
   private String[] _weekdays;
   private String[] _weekdays_short;
   private char _undefinedSymbol;
   private static final long SYMBOLS;
   private static DateFormatSymbols _default;

   private DateFormatSymbols() {
   }

   private DateFormatSymbols(Locale locale) {
      this._localecode = locale.getCode();
      ResourceBundleFamily family = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale");
      ResourceBundle resources = family.getBundle(locale);

      try {
         this.init(resources);
      } catch (MissingResourceException e) {
         this.init(family);
      }

      ObjectGroup.createGroupIgnoreTooBig(this);
   }

   private final void init(ResourceBundle resources) {
      this._ampm = resources.getStringArray(80);
      this._ampm_short = resources.getStringArray(81);
      if (this._ampm.length == 2 && this._ampm_short.length == 2) {
         this._dateFormats = resources.getStringArray(20);
         this._datetimeFormat = resources.getString(21);
         this._months = resources.getStringArray(30);
         this._months_short = resources.getStringArray(42);
         this._timeFormats = resources.getStringArray(10);
         this._weekdays = resources.getStringArray(66);
         this._weekdays_short = resources.getStringArray(73);
         this._undefinedSymbol = resources.getString(90).charAt(0);
      } else {
         throw new IllegalStateException("AM PM info bad");
      }
   }

   public final String[] getAmPmStrings() {
      return this._ampm;
   }

   public static final synchronized DateFormatSymbols getInstance() {
      settingChanged();
      return _default;
   }

   public static final synchronized DateFormatSymbols getInstance(Locale locale) {
      return getSymbols(locale.getCode());
   }

   public final String[] getMonths() {
      return this._months;
   }

   public final String getPattern(int style) {
      int dateIndex = 4 * DateTimeFormatOptions.getDateFormat() + (style >> 3 & 3);
      dateIndex = MathUtilities.clamp(0, dateIndex, this._dateFormats.length - 1);
      String datePattern = this._dateFormats[dateIndex];
      int timeIndex = 4 * DateTimeFormatOptions.getTimeFormat() + (style >> 0 & 3);
      timeIndex = MathUtilities.clamp(0, timeIndex, this._timeFormats.length - 1);
      String timePattern = this._timeFormats[timeIndex];
      if ((style & 32) != 0 && (style & 4) != 0) {
         return MessageFormat.format(this._datetimeFormat, new String[]{datePattern, timePattern});
      } else if ((style & 32) != 0) {
         return datePattern;
      } else if ((style & 4) != 0) {
         return timePattern;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final String[] getShortAmPmStrings() {
      return this._ampm_short;
   }

   public final String[] getShortMonths() {
      return this._months_short;
   }

   public final String[] getShortWeekdays() {
      return this._weekdays_short;
   }

   private static final DateFormatSymbols getSymbols(int localecode) {
      DateFormatSymbols symbols = null;
      PersistentObject persist = RIMPersistentStore.getPersistentObject(1125501472565950566L);
      synchronized (persist) {
         IntHashtable hashtable = (IntHashtable)persist.getContents();
         if (hashtable != null) {
            symbols = (DateFormatSymbols)hashtable.get(localecode);
         }

         if (symbols == null) {
            symbols = new DateFormatSymbols(Locale.get(localecode));
         }

         return symbols;
      }
   }

   public final String[] getWeekdays() {
      return this._weekdays;
   }

   public final char getUndefinedSymbol() {
      return this._undefinedSymbol;
   }

   public final void setSymbols(
      Locale locale,
      String[] ampm,
      String[] ampm_short,
      String[] dateFormats,
      String datetimeFormat,
      String[] months,
      String[] months_short,
      String[] timeFormats,
      String[] weekdays,
      String[] weekdays_short,
      char undefinedSymbol
   ) {
      DateFormatSymbols symbols = new DateFormatSymbols();
      symbols._localecode = locale.getCode();
      symbols._ampm = ampm;
      symbols._ampm_short = ampm_short;
      symbols._dateFormats = dateFormats;
      symbols._datetimeFormat = datetimeFormat;
      symbols._months = months;
      symbols._months_short = months_short;
      symbols._timeFormats = timeFormats;
      symbols._weekdays = weekdays;
      symbols._weekdays_short = weekdays_short;
      symbols._undefinedSymbol = undefinedSymbol;
      ObjectGroup.createGroupIgnoreTooBig(symbols);
      PersistentObject persist = RIMPersistentStore.getPersistentObject(1125501472565950566L);
      synchronized (persist) {
         IntHashtable hashtable = (IntHashtable)persist.getContents();
         if (hashtable == null) {
            hashtable = new IntHashtable();
            persist.setContents(hashtable, 51);
         }

         hashtable.put(locale.getCode(), symbols);
         persist.commit();
      }
   }

   static final void settingChanged() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      int systemLocalecode = Locale.getDefaultForSystem().getCode();
      DateFormatSymbols symbols = (DateFormatSymbols)applicationRegistry.get(1125501472565950566L);
      if (symbols == null || symbols._localecode != systemLocalecode) {
         DateFormatSymbols tmpSymbols = getSymbols(systemLocalecode);
         symbols = (DateFormatSymbols)applicationRegistry.getOrWaitFor(1125501472565950566L);
         if (symbols == null || symbols._localecode != systemLocalecode) {
            symbols = tmpSymbols;
            applicationRegistry.replace(1125501472565950566L, symbols);
         }
      }

      int appLocalecode = Locale.getDefault().getCode();
      if (appLocalecode != systemLocalecode) {
         if (_default != null && _default._localecode == appLocalecode) {
            symbols = _default;
         } else {
            symbols = getSymbols(appLocalecode);
         }
      }

      _default = symbols;
   }
}
