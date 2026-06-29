package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;

final class FontOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID;
   private static final int SIZE_TAG;
   private static final int STYLE_TAG;
   private static final int FAMILY_TAG;
   private static final int ANTIALIAS_TAG;
   private static final int POST430_TAG;

   FontOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return -1789532810;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      Font f = Font.getDefault();
      int height = Font.getDefaultHeight(4194307);
      int style = f.getStyle();
      int antialiasing = f.getAntialiasMode();
      ConverterUtilities.convertInt(buffer, 5, 1, 4);
      ConverterUtilities.convertInt(buffer, 1, height, 4);
      ConverterUtilities.convertInt(buffer, 2, style, 4);
      ConverterUtilities.convertInt(buffer, 4, antialiasing, 4);
      ConverterUtilities.writeStringSmart(buffer, 3, f.getFontFamily().getName());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void setOptionsData(DataBuffer buffer) {
      try {
         boolean post430 = false;
         boolean gotsize = false;
         boolean gotstyle = false;
         int size = 0;
         int style = 0;
         int antialias = 1;
         String family = null;

         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer, true);
            switch (tag) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  gotsize = true;
                  size = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  gotstyle = true;
                  style = ConverterUtilities.readInt(buffer);
                  break;
               case 3:
                  family = ConverterUtilities.readString(buffer);
                  break;
               case 4:
                  antialias = ConverterUtilities.readInt(buffer);
                  break;
               case 5:
                  ConverterUtilities.readInt(buffer);
                  post430 = true;
            }
         }

         if (post430 && gotsize && gotstyle && family != null) {
            boolean var18 = false /* VF: Semaphore variable */;

            try {
               boolean var22 = false /* VF: Semaphore variable */;

               FontFamily var26;
               label115:
               try {
                  var22 = true;
                  var18 = true;
                  var26 = FontFamily.forName(family);
                  var22 = false;
               } finally {
                  if (var22) {
                     var26 = FontFamily.forName(FontFamily.FAMILY_SYSTEM);
                     break label115;
                  }
               }

               if (var26 != null) {
                  int units = size < 400 ? 3 : 4194307;
                  Font font = var26.getFont(style, size, units, antialias, 0);
                  Font.setDefaultFontForSystem(font);
                  var18 = false;
               } else {
                  var18 = false;
               }
            } finally {
               if (var18) {
                  return;
               }
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
         -4394903006263251010L,
         -3455386809805045760L,
         8906172480279495146L,
         463674593572421888L,
         1246482708980695296L,
         -1080581586446909429L,
         -5171536448462041730L,
         2313449430721766284L
      };
   }
}
