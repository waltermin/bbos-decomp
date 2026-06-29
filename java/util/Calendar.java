package java.util;

public class Calendar {
   protected int[] fields;
   protected boolean[] isSet;
   protected long time;
   private TimeZone _zone;
   public static final int YEAR = 1;
   public static final int MONTH = 2;
   public static final int DATE = 5;
   public static final int DAY_OF_MONTH = 5;
   public static final int DAY_OF_WEEK = 7;
   public static final int AM_PM = 9;
   public static final int HOUR = 10;
   public static final int HOUR_OF_DAY = 11;
   public static final int MINUTE = 12;
   public static final int SECOND = 13;
   public static final int MILLISECOND = 14;
   public static final int SUNDAY = 1;
   public static final int MONDAY = 2;
   public static final int TUESDAY = 3;
   public static final int WEDNESDAY = 4;
   public static final int THURSDAY = 5;
   public static final int FRIDAY = 6;
   public static final int SATURDAY = 7;
   public static final int JANUARY = 0;
   public static final int FEBRUARY = 1;
   public static final int MARCH = 2;
   public static final int APRIL = 3;
   public static final int MAY = 4;
   public static final int JUNE = 5;
   public static final int JULY = 6;
   public static final int AUGUST = 7;
   public static final int SEPTEMBER = 8;
   public static final int OCTOBER = 9;
   public static final int NOVEMBER = 10;
   public static final int DECEMBER = 11;
   public static final int AM = 0;
   public static final int PM = 1;
   private static final int FIELD_COUNT = 15;

   protected Calendar() {
      this.setTimeZone(TimeZone.getDefault());
      this.setTimeInMillis(System.currentTimeMillis());
      if (!(this instanceof net.rim.device.cldc.util.GregorianCalendar)) {
         this.fields = new int[15];
         this.isSet = new boolean[15];
      }
   }

   public boolean before(Object when) {
      return when instanceof Calendar && ((Calendar)when).getTimeInMillis() > this.getTimeInMillis();
   }

   public boolean after(Object when) {
      return when instanceof Calendar && ((Calendar)when).getTimeInMillis() < this.getTimeInMillis();
   }

   public static Calendar getInstance() {
      return new net.rim.device.cldc.util.GregorianCalendar();
   }

   public static Calendar getInstance(TimeZone zone) {
      Calendar cal = new net.rim.device.cldc.util.GregorianCalendar();
      cal.setTimeZone(zone);
      return cal;
   }

   public final Date getTime() {
      return new Date(this.getTimeInMillis());
   }

   protected long getTimeInMillis() {
      return this.time;
   }

   public final int get(int field) {
      try {
         return ((net.rim.device.cldc.util.GregorianCalendar)this).internalGet(field, true);
      } catch (ClassCastException e) {
         return this.fields[field];
      }
   }

   public final void set(int field, int value) {
      try {
         ((net.rim.device.cldc.util.GregorianCalendar)this).internalSet(field, value);
      } catch (ClassCastException e) {
         this.fields[field] = value;
         this.isSet[field] = true;
      }
   }

   public final void setTime(Date date) {
      this.setTimeInMillis(date.getTime());
   }

   protected void setTimeInMillis(long millis) {
      this.time = millis;
   }

   public void setTimeZone(TimeZone value) {
      this._zone = value;
   }

   public TimeZone getTimeZone() {
      return this._zone;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Calendar)) {
         return false;
      }

      Calendar that = (Calendar)obj;
      return this.getTimeInMillis() == that.getTimeInMillis() && this._zone.equals(that._zone);
   }

   @Override
   public int hashCode() {
      return this._zone.hashCode();
   }

   protected void computeFields() {
      throw null;
   }

   protected void computeTime() {
      throw null;
   }
}
