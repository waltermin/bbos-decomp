package net.rim.device.cldc.util;

public interface CalendarExtensions {
   int ERA;
   int BC;
   int AD;
   int DAY_OF_YEAR;
   int DST_OFFSET;

   void add(int var1, int var2);

   void roll(int var1, int var2);

   int getActualMaximum(int var1);

   int getActualMinimum(int var1);

   void setTimeLong(long var1);

   long getTimeLong();
}
