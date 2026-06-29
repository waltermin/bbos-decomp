package net.rim.device.apps.internal.addressbook.eventmodel;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.addressbook.EventModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.cldc.util.CalendarExtensions;

public class EventModelFactory extends RIMModelFactory {
   private int _type;

   EventModelFactory(int type) {
      this._type = type;
   }

   @Override
   public Object createInstance(Object initialData) {
      EventModel model = null;
      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         label35:
         try {
            model = new EventModelImpl(null);
            model.setEventDate(stringToLong(syncBuffer.getString(this._type, true)));
         } finally {
            break label35;
         }
      } else {
         if (initialData instanceof ContextObject) {
            ContextObject.put(initialData, -4054673099568009991L, new Integer(this._type));
         }

         model = new EventModelImpl(initialData);
      }

      model.setEventType(this._type);
      return model;
   }

   @Override
   public int getMinimumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MIN_VALUE;
   }

   @Override
   public int getMaximumCount(Object context) {
      return ContextObject.getFlag(context, 11) ? 1 : Integer.MAX_VALUE;
   }

   @Override
   public boolean recognize(Object object) {
      if (!(object instanceof EventModelImpl)) {
         if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
            if (syncBuffer != null) {
               if (syncBuffer.getFieldType(true) == this._type) {
                  return true;
               }

               return false;
            }
         }

         return false;
      } else {
         return ((EventModelImpl)object).getEventType() == this._type;
      }
   }

   public static String longToString(long date) {
      StringBuffer sb = new StringBuffer(10);
      if (date != -1) {
         Calendar cal = Calendar.getInstance();
         cal.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         ((CalendarExtensions)cal).setTimeLong(date);
         int value = cal.get(5);
         if (value < 10) {
            sb.append('0');
         }

         sb.append(value);
         sb.append('/');
         value = cal.get(2) + 1;
         if (value < 10) {
            sb.append('0');
         }

         sb.append(value);
         sb.append('/');
         sb.append(cal.get(1));
      }

      return sb.toString();
   }

   public static long stringToLong(String date) {
      if (date != null) {
         int length = date.length();
         if (length > 0 && length <= 10) {
            int[] fields = new int[7];
            int firstDelimiter = date.indexOf(47);
            int lastDelimiter = date.lastIndexOf(47);
            if (firstDelimiter != lastDelimiter && firstDelimiter > 0 && lastDelimiter < length - 1) {
               String token = date.substring(0, firstDelimiter);
               fields[2] = Integer.parseInt(token);
               token = date.substring(firstDelimiter + 1, lastDelimiter);
               fields[1] = Integer.parseInt(token) - 1;
               token = date.substring(lastDelimiter + 1, length);
               fields[0] = Integer.parseInt(token);
               Calendar cal = Calendar.getInstance();
               cal.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
               DateTimeUtilities.setCalendarFields(cal, fields);
               return ((CalendarExtensions)cal).getTimeLong();
            }
         }
      }

      return -1;
   }
}
