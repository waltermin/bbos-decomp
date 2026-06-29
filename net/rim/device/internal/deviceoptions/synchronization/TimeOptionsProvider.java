package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;
import net.rim.device.internal.i18n.DateTimeFormatOptions;
import net.rim.device.internal.timesync.TimeSync;

final class TimeOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID;
   private static final int TIMEFORMAT1224_TAG;
   private static final int TIMEZONEID_TAG;
   private static final int AUTO_TIME_SET;

   TimeOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return -447168820;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      ConverterUtilities.convertInt(buffer, 1, DateTimeFormatOptions.getTimeFormat(), 4);
      TimeService ts = TimeService.getTimeService();
      int tzindex = ts.getSerialSyncID(ts.getDefaultTimeZoneID());
      ConverterUtilities.convertInt(buffer, 2, tzindex, 4);
      ConverterUtilities.convertInt(buffer, 3, TimeSync.getInstance().getSource(), 1);
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         boolean gottimezoneid = false;
         boolean gottimeformat = false;
         int timezoneid = 0;
         int timeformat = 0;

         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer);
            switch (tag) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  gottimeformat = true;
                  timeformat = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  gottimezoneid = true;
                  timezoneid = ConverterUtilities.readInt(buffer);
                  break;
               case 3:
                  TimeSync.getInstance().setSource(ConverterUtilities.readInt(buffer));
            }
         }

         if (gottimezoneid) {
            TimeService ts = TimeService.getTimeService();
            ts.setDefaultTimeZone(ts.getTimeZoneIDFromSerialSyncID(timezoneid));
         }

         if (gottimeformat) {
            DateTimeFormatOptions.setTimeFormat(timeformat);
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 7207871974803693937L || guid == 3596208183088439728L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         7207871974803693937L,
         3596208183088439728L,
         -3455386801215111168L,
         -7464003439710973532L,
         -8040378802380461050L,
         -1438311245835636745L,
         -3455386809805045760L,
         9206737719270818227L,
         -3455386809805045760L,
         -4394903006263251010L,
         -3455386809805045760L,
         8906172480279495146L,
         463674593572421888L,
         1246482708980695296L,
         -1080581586446909429L,
         -5171536448462041730L
      };
   }
}
