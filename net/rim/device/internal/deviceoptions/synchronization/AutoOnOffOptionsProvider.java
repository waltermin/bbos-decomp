package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.AutoOnOff;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;

final class AutoOnOffOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID = -1243804307;
   private static final int WEEKDAY_ON_TAG = 1;
   private static final int WEEKDAY_OFF_TAG = 2;
   private static final int WEEKDAY_ENABLE_TAG = 3;
   private static final int WEEKEND_ON_TAG = 4;
   private static final int WEEKEND_OFF_TAG = 5;
   private static final int WEEKEND_ENABLE_TAG = 6;

   AutoOnOffOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return -1243804307;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      ConverterUtilities.convertInt(buffer, 1, AutoOnOff.getWeekdayOnTime(), 4);
      ConverterUtilities.convertInt(buffer, 2, AutoOnOff.getWeekdayOffTime(), 4);
      if (AutoOnOff.isWeekdayAutoOnOffEnabled()) {
         ConverterUtilities.writeEmptyField(buffer, 3);
      }

      ConverterUtilities.convertInt(buffer, 4, AutoOnOff.getWeekendOnTime(), 4);
      ConverterUtilities.convertInt(buffer, 5, AutoOnOff.getWeekendOffTime(), 4);
      if (AutoOnOff.isWeekendAutoOnOffEnabled()) {
         ConverterUtilities.writeEmptyField(buffer, 6);
      }
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         boolean gotweekdayon = false;
         boolean gotweekdayoff = false;
         boolean gotweekendon = false;
         boolean gotweekendoff = false;
         int weekdayon = 0;
         int weekdayoff = 0;
         int weekendon = 0;
         int weekendoff = 0;
         boolean weekdayenable = false;
         boolean weekendenable = false;

         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer);
            switch (tag) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  gotweekdayon = true;
                  weekdayon = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  gotweekdayoff = true;
                  weekdayoff = ConverterUtilities.readInt(buffer);
                  break;
               case 3:
                  weekdayenable = true;
                  ConverterUtilities.skipField(buffer);
                  break;
               case 4:
                  gotweekendon = true;
                  weekendon = ConverterUtilities.readInt(buffer);
                  break;
               case 5:
                  gotweekendoff = true;
                  weekendoff = ConverterUtilities.readInt(buffer);
                  break;
               case 6:
                  weekendenable = true;
                  ConverterUtilities.skipField(buffer);
            }
         }

         if (gotweekdayon) {
            AutoOnOff.setWeekdayOnTime(weekdayon);
         }

         if (gotweekdayoff) {
            AutoOnOff.setWeekdayOffTime(weekdayoff);
         }

         AutoOnOff.enableWeekdayAutoOnOff(weekdayenable);
         if (gotweekendon) {
            AutoOnOff.setWeekendOnTime(weekendon);
         }

         if (gotweekendoff) {
            AutoOnOff.setWeekendOffTime(weekendoff);
         }

         AutoOnOff.enableWeekendAutoOnOff(weekendenable);
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -2918606221006897090L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         -2918606221006897090L,
         -3455386809805045760L,
         -3297167379286550693L,
         -3455386809805045760L,
         6063360555319689575L,
         -3455386805510078464L,
         7207871974803693937L,
         3596208183088439728L
      };
   }
}
