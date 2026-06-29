package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.vm.PersistentInteger;

public class DateTimeFormatOptions {
   public static final int TIME_FORMAT_UNSET;
   public static final int TIME_FORMAT_12_HOUR;
   public static final int TIME_FORMAT_24_HOUR;
   private static final long TIME_FORMAT_GUID;
   private static int _timeFormatId = PersistentInteger.getId(-7914069170068447103L, -1);
   private static DateTimeFormatOptions$TimeFormatOptions _instance;
   private static int _timeFormatApplication = -1;

   public static int getDateFormat() {
      return 0;
   }

   public static int getTimeFormat() {
      if (_instance.timeFormatUserSetting != -1) {
         return _instance.timeFormatUserSetting;
      } else {
         return Locale.getDefault() != Locale.getDefaultForSystem() ? _timeFormatApplication : _instance.timeFormatSystem;
      }
   }

   public static String[] getTimeFormats() {
      ResourceBundle resourceBundle = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale");
      return resourceBundle.getStringArray(9);
   }

   public static void onAppLocaleChange() {
      if (_instance.timeFormatUserSetting == -1 && Locale.getDefault() != Locale.getDefaultForSystem()) {
         ResourceBundle bundle = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale");
         String defaultTimeFormat = bundle.getString(12);
         _timeFormatApplication = Integer.parseInt(defaultTimeFormat);
      }
   }

   public static void onSystemLocaleChange() {
      int format = PersistentInteger.get(_timeFormatId);
      if (format == -1) {
         ResourceBundle bundle = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale");
         String defaultTimeFormat = bundle.getString(12);
         _instance.timeFormatSystem = Integer.parseInt(defaultTimeFormat);
      }
   }

   public static void setTimeFormat(int timeFormat) {
      int curTimrFormat = getTimeFormat();
      if (timeFormat != curTimrFormat) {
         switch (timeFormat) {
            case -2:
            default:
               throw new IllegalArgumentException();
            case -1:
            case 0:
            case 1:
               _instance.timeFormatUserSetting = timeFormat;
               PersistentInteger.set(_timeFormatId, timeFormat);
               onSystemLocaleChange();
               RIMGlobalMessagePoster.postGlobalEvent(7207871974803693937L);
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (DateTimeFormatOptions$TimeFormatOptions)ar.getOrWaitFor(-7914069170068447103L);
      if (_instance == null) {
         _instance = new DateTimeFormatOptions$TimeFormatOptions();
         _instance.timeFormatSystem = PersistentInteger.get(_timeFormatId);
         _instance.timeFormatUserSetting = -1;
         ar.put(-7914069170068447103L, _instance);
      }

      onSystemLocaleChange();
   }
}
