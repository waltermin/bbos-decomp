package javax.microedition.lcdui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.cldc.util.CalendarExtensions;

public class DateField extends Item {
   private net.rim.device.api.ui.component.DateField _field;
   private int _mode;
   private Calendar _calendar;
   private static final long MILLIS_IN_A_DAY;
   public static final int DATE;
   public static final int TIME;
   public static final int DATE_TIME;

   private int toRimMode(int mode) {
      switch (mode) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            return 16;
         case 2:
            return 32;
         case 3:
            return 48;
      }
   }

   public DateField(String label, int mode) {
      this(label, mode, null);
   }

   public DateField(String label, int mode, TimeZone timeZone) {
      synchronized (Application.getEventLock()) {
         this._field = new net.rim.device.api.ui.component.DateField(label, 0, this.toRimMode(mode));
         this._field.setCookie(this);
         this.setInputMode(mode);
         if (timeZone != null) {
            this._field.setTimeZone(timeZone);
         }

         this._calendar = Calendar.getInstance(timeZone);
         this._field.setDate(null);
         this.setPeer(this._field);
      }
   }

   public Date getDate() {
      synchronized (Application.getEventLock()) {
         long millis = this._field.getDate();
         if (millis == Long.MIN_VALUE) {
            return null;
         }

         switch (this._mode) {
            case 0:
               break;
            case 1:
            default: {
               CalendarExtensions cal = (CalendarExtensions)this._calendar;
               this._calendar.setTimeZone(this._field.getTimeZone());
               cal.setTimeLong(millis);
               this._calendar.set(11, 0);
               this._calendar.set(12, 0);
               this._calendar.set(13, 0);
               this._calendar.set(14, 0);
               millis = cal.getTimeLong();
               break;
            }
            case 2:
               if (millis < 0 || millis >= 86400000) {
                  return null;
               }
            case 3: {
               CalendarExtensions cal = (CalendarExtensions)this._calendar;
               this._calendar.setTimeZone(this._field.getTimeZone());
               cal.setTimeLong(millis);
               this._calendar.set(13, 0);
               this._calendar.set(14, 0);
               millis = cal.getTimeLong();
            }
         }

         return new Date(millis);
      }
   }

   public void setDate(Date date) {
      synchronized (Application.getEventLock()) {
         if (date != null && this._mode == 2 && date.getTime() >= 86400000) {
            this._field.setDate(null);
         } else {
            this._field.setDate(date);
         }
      }
   }

   public int getInputMode() {
      synchronized (Application.getEventLock()) {
         return this._mode;
      }
   }

   public void setInputMode(int mode) {
      synchronized (Application.getEventLock()) {
         switch (mode) {
            case 0:
               throw new IllegalArgumentException();
            case 1:
            default:
               this._field.setFormat(DateFormat.getInstance(48));
               break;
            case 2:
               this._field.setFormat(DateFormat.getInstance(6));
               break;
            case 3:
               this._field.setFormat(DateFormat.getInstance(54));
         }

         this._mode = mode;
      }
   }

   @Override
   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         this._field.setLabel(label);
      }
   }

   @Override
   public String getLabel() {
      synchronized (Application.getEventLock()) {
         return this._field.getLabel();
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      this._field.setChangeListener(null);
      this._field.setChangeListener(changeListener);
      return this._field;
   }
}
