package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;
import net.rim.device.internal.deviceoptions.SMSOptions;

final class SMSOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID;
   private static final int ROUTE_TAG;
   private static final int DELIVERY_REPORTS_TAG;
   private static final int STORE_ON_SIM_TAG;
   private static final int NUM_PREVIOUS_ITEMS_TAG;
   private static final int DISABLE_AUTOTEXT_TAG;
   private static final int ENABLE_CELL_BROADCAST_TAG;

   SMSOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return 287956570;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      ConverterUtilities.convertInt(buffer, 1, SMSOptions.getRoute(), 1);
      ConverterUtilities.convertInt(buffer, 2, SMSOptions.getDeliveryReports() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 3, SMSOptions.getStoreOnSIM() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 4, SMSOptions.getNumPreviousItems(), 4);
      ConverterUtilities.convertInt(buffer, 5, SMSOptions.getDisableAutoText() ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 6, SMSOptions.getEnableCellBroadcast() ? 1 : 0, 1);
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         while (buffer.available() > 0) {
            switch (ConverterUtilities.getType(buffer)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  SMSOptions.setRoute(ConverterUtilities.readInt(buffer));
                  break;
               case 2:
                  SMSOptions.setDeliveryReports(ConverterUtilities.readInt(buffer) != 0);
                  break;
               case 3:
                  SMSOptions.setStoreOnSIM(ConverterUtilities.readInt(buffer) != 0);
                  break;
               case 4:
                  SMSOptions.setNumPreviousItems(ConverterUtilities.readInt(buffer));
                  break;
               case 5:
                  SMSOptions.setDisableAutoText(ConverterUtilities.readInt(buffer) != 0);
                  break;
               case 6:
                  SMSOptions.setEnableCellBroadcast(ConverterUtilities.readInt(buffer) != 0);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6063360555319689575L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         6063360555319689575L,
         -3455386805510078464L,
         7207871974803693937L,
         3596208183088439728L,
         -3455386801215111168L,
         -7464003439710973532L,
         -8040378802380461050L,
         -1438311245835636745L
      };
   }
}
