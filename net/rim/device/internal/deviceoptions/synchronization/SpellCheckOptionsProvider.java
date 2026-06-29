package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;
import net.rim.tid.im.spellcheck.SpellCheckConstants;
import net.rim.tid.im.spellcheck.SpellCheckUtilities;

final class SpellCheckOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener, SpellCheckConstants {
   private static final int UID;
   private static final int OPTIONS_TAG;

   SpellCheckOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return 686875650;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      byte[] spellCheckProperties = SpellCheckUtilities.getIMProperties();
      if (spellCheckProperties != null) {
         ConverterUtilities.writeByteArray(buffer, 1, spellCheckProperties);
      }
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer, true);
            switch (tag) {
               case 1:
                  byte[] data = ConverterUtilities.readByteArray(buffer);
                  if (data == null) {
                     return;
                  }

                  byte[] spellCheckProperties = SpellCheckUtilities.getIMProperties();
                  if (spellCheckProperties == null) {
                     return;
                  }

                  if (spellCheckProperties.length > data.length) {
                     spellCheckProperties = new byte[spellCheckProperties.length];
                     System.arraycopy(data, 0, spellCheckProperties, 0, data.length);
                     data = spellCheckProperties;
                  }

                  SpellCheckUtilities.setIMProperties(data);
                  break;
               default:
                  ConverterUtilities.skipField(buffer);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8906172480279495146L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         8906172480279495146L,
         463674593572421888L,
         1246482708980695296L,
         -1080581586446909429L,
         -5171536448462041730L,
         2313449430721766284L,
         7084033152016666745L,
         432352552240071530L
      };
   }
}
