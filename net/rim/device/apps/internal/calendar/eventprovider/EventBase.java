package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.BareEvent;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;

class EventBase implements BareEvent, PersistableRIMModel {
   protected static final ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
   protected static final char LOCATION_START_TOKEN;
   protected static final char LOCATION_END_TOKEN;
   protected static final char SPACE_SEPARATOR;
   protected static final char HYPHEN_SEPARATOR;
   protected static Object[] _sharedObjectArray = new Object[0];
   protected static String[] _timeZoneDescriptions;
   protected static DateFormat _mediumDateFormat = DateFormat.getInstance(48);

   protected static void appendTimeZoneDescription(TimeZone tz, StringBuffer b) {
      if (_timeZoneDescriptions == null) {
         _timeZoneDescriptions = TimeService.getTimeService().getTimeZoneNamesLong();
      }

      int tzIndex = TimeService.getTimeService().getTimeZoneIndex(tz.getID());
      b.append(' ');
      b.append(_timeZoneDescriptions[tzIndex]);
   }

   protected void checkForVerbOverrides(Object context, Verb[] defaultVerbs) {
   }

   protected Verb getVerbs(Object context, Verb[] verbs, Verb deleteVerb, Verb editVerb) {
      int defaultVerbIndex = 0;
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb[] defaultVerbs = new Object[2];
      defaultVerbs[1] = deleteVerb;
      this.checkForVerbOverrides(context, defaultVerbs);
      if (!ContextObject.getFlag(context, 86)) {
         defaultVerbs[1] = null;
      }

      int max = defaultVerbs.length;
      Array.resize(verbs, max + 1);
      int verbIndex = 0;
      verbs[verbIndex++] = editVerb;

      for (int i = 0; i < max; i++) {
         if (defaultVerbs[i] != null) {
            verbs[verbIndex++] = defaultVerbs[i];
         }
      }

      Array.resize(verbs, verbIndex);
      boolean reminder = false;
      if (context != null && ((ContextObject)context).getFlag(36)) {
         reminder = true;
      }

      if (!reminder) {
         Verb[] temp = new Object[0];
         EventUtilities.getExternalCalendarActionVerbs(temp);
         if (temp != null && temp.length > 0) {
            int oldLength = verbs.length;
            Array.resize(verbs, verbs.length + temp.length);

            for (int i = oldLength; i < verbs.length; i++) {
               verbs[i] = temp[i - oldLength];
            }
         }

         String mimeType = "text/x-vcalendar";
         Verb[] vcalVerbs = MIMEContentVerbRepository.getVerbs(mimeType);
         if (vcalVerbs != null && vcalVerbs.length > 0) {
            int oldLength = verbs.length;
            Array.resize(verbs, verbs.length + vcalVerbs.length);

            for (int i = oldLength; i < verbs.length; i++) {
               verbs[i] = vcalVerbs[i - oldLength];
               Verb var10000 = verbs[i];
               if (verbs[i] instanceof Object) {
                  DefaultProvider dp = (DefaultProvider)var10000;
                  Object result = dp.getDefault(this, null);
                  if (verbs[i] == result) {
                     defaultVerbIndex = i;
                  }
               }
            }
         }
      }

      return verbs[defaultVerbIndex];
   }

   @Override
   public boolean isAllDay() {
      throw null;
   }

   @Override
   public long getDuration(TimeZone _1) {
      throw null;
   }

   @Override
   public long getStart(TimeZone _1) {
      throw null;
   }

   @Override
   public byte getProperties() {
      throw null;
   }

   @Override
   public int getIconsForField(long _1, IconCollection[] _3, int[][][] _4) {
      throw null;
   }

   @Override
   public String getStringForField(long _1, long _3) {
      throw null;
   }

   @Override
   public String getStringForField(long _1) {
      throw null;
   }
}
