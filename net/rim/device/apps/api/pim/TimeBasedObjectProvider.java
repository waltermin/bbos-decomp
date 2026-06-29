package net.rim.device.apps.api.pim;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.util.SimpleSortingVector;

public interface TimeBasedObjectProvider {
   void getElementsVisibleDuring(long var1, long var3, TimeZone var5, SimpleSortingVector var6);

   void getElementsVisibleDuring(long var1, long var3, TimeZone var5, SimpleSortingVector var6, boolean var7);

   long[] getElementsStartingAround(long var1, int var3, int var4, TimeZone var5, Vector var6);

   String getProviderName();

   long getProviderID();

   int getEventCountBeforeTime(long var1, TimeZone var3);

   int getEventCountAfterTime(long var1, TimeZone var3);

   int size();
}
