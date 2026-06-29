package java.util;

public class Calendar {
   protected int[] fields;
   protected boolean[] isSet;
   protected long time;
   private TimeZone _zone;
   public static final int YEAR;
   public static final int MONTH;
   public static final int DATE;
   public static final int DAY_OF_MONTH;
   public static final int DAY_OF_WEEK;
   public static final int AM_PM;
   public static final int HOUR;
   public static final int HOUR_OF_DAY;
   public static final int MINUTE;
   public static final int SECOND;
   public static final int MILLISECOND;
   public static final int SUNDAY;
   public static final int MONDAY;
   public static final int TUESDAY;
   public static final int WEDNESDAY;
   public static final int THURSDAY;
   public static final int FRIDAY;
   public static final int SATURDAY;
   public static final int JANUARY;
   public static final int FEBRUARY;
   public static final int MARCH;
   public static final int APRIL;
   public static final int MAY;
   public static final int JUNE;
   public static final int JULY;
   public static final int AUGUST;
   public static final int SEPTEMBER;
   public static final int OCTOBER;
   public static final int NOVEMBER;
   public static final int DECEMBER;
   public static final int AM;
   public static final int PM;
   private static final int FIELD_COUNT;

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
