package java.util;

public class Date {
   private long millis;

   public Date() {
      this(System.currentTimeMillis());
   }

   public Date(long date) {
      this.millis = date;
   }

   public long getTime() {
      return this.millis;
   }

   public void setTime(long time) {
      this.millis = time;
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof Date && ((Date)obj).getTime() == this.getTime();
   }

   @Override
   public int hashCode() {
      long ht = this.getTime();
      return (int)ht ^ (int)(ht >> 32);
   }

   @Override
   public String toString() {
      Calendar c = Calendar.getInstance();
      c.setTime(this);
      return net.rim.device.cldc.util.GregorianCalendar.toString(c);
   }
}
