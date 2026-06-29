package net.rim.device.cldc.util;

public interface CalendarExtensions {
   int ERA = 0;
   int BC = 0;
   int AD = 1;
   int DAY_OF_YEAR = 6;
   int DST_OFFSET = 16;

   void add(int var1, int var2);

   void roll(int var1, int var2);

   int getActualMaximum(int var1);

   int getActualMinimum(int var1);

   void setTimeLong(long var1);

   long getTimeLong();
}
