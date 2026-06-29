package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;
import net.rim.device.internal.deviceoptions.Owner;

final class OwnerInfoOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID;
   private static final int NAME_TAG;
   private static final int INFO_TAG;

   OwnerInfoOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return -1113958365;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      ConverterUtilities.writeStringSmart(buffer, 1, Owner.getOwnerName());
      ConverterUtilities.writeStringSmart(buffer, 2, Owner.getOwnerInfo());
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         String ownerName = null;
         String ownerInfo = null;

         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer, true);
            switch (tag) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  ownerName = ConverterUtilities.readString(buffer);
                  break;
               case 2:
                  ownerInfo = ConverterUtilities.readString(buffer);
            }
         }

         if (ownerName != null) {
            Owner.setOwnerName(ownerName);
         }

         if (ownerInfo != null) {
            Owner.setOwnerInfo(ownerInfo);
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3297167379286550693L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         -3297167379286550693L,
         -3455386809805045760L,
         6063360555319689575L,
         -3455386805510078464L,
         7207871974803693937L,
         3596208183088439728L,
         -3455386801215111168L,
         -7464003439710973532L
      };
   }
}
