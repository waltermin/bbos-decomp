package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderGlobalEventListener;

final class LocalizationOptionsProvider extends OptionsProviderBase implements OptionsProviderGlobalEventListener {
   private static final int UID;
   private static final int LOCALE_CODE_TAG;
   private static final int LOCALE_VARIANT_TAG;
   private static final int INPUT_LOCALE_CODE_TAG;
   private static final int SYSTEM_NAME_ORDER;
   private static final int INPUT_LOCALE_VARIANT_TAG;

   LocalizationOptionsProvider(OptionsProviderChangeListener listener) {
      super(listener);
   }

   @Override
   public final int getUID() {
      return -1399197799;
   }

   @Override
   public final void getOptionsData(DataBuffer buffer) {
      Locale locale = Locale.getDefaultForSystem();
      Locale inputLocale = Locale.getDefaultInputForSystem();
      ConverterUtilities.convertInt(buffer, 1, locale.getCode(), 4);
      ConverterUtilities.writeStringSmart(buffer, 2, locale.getVariant());
      ConverterUtilities.convertInt(buffer, 5, inputLocale.getCode(), 4);
      ConverterUtilities.writeStringSmart(buffer, 7, inputLocale.getVariant());
      ConverterUtilities.convertInt(buffer, 6, Locale.getSystemNameOrder(), 4);
   }

   @Override
   public final void setOptionsData(DataBuffer buffer) {
      int localeCode = 0;
      int inputLocaleCode = 0;
      int restoredNameOrder = -1;
      String localeVariant = null;
      String inputLocaleVariant = null;

      label133:
      try {
         while (buffer.available() > 0) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case 0:
               case 3:
               case 4:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  localeCode = ConverterUtilities.readInt(buffer);
                  break;
               case 2:
                  localeVariant = ConverterUtilities.readString(buffer);
                  break;
               case 5:
                  inputLocaleCode = ConverterUtilities.readInt(buffer);
                  break;
               case 6:
                  restoredNameOrder = ConverterUtilities.readInt(buffer);
                  break;
               case 7:
                  inputLocaleVariant = ConverterUtilities.readString(buffer);
            }
         }
      } finally {
         break label133;
      }

      if (restoredNameOrder != -1 && restoredNameOrder != Locale.getSystemNameOrder()) {
         Locale.setNameOrder(restoredNameOrder);
      }

      if (localeCode != 0) {
         Locale locale = Locale.get(localeCode, localeVariant);

         label123:
         try {
            Locale.setDefaultForSystem(locale);
         } finally {
            break label123;
         }
      }

      if (inputLocaleCode != 0) {
         Locale inputLocale = Locale.get(inputLocaleCode, inputLocaleVariant);

         try {
            Locale.setDefaultInputForSystem(inputLocale);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L || guid == -8040378802380461050L || guid == -1438311245835636745L) {
         this.optionsProviderChanged();
      }
   }

   @Override
   public final long[] getGlobalEventUids() {
      return new long[]{
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
         -5171536448462041730L,
         2313449430721766284L,
         7084033152016666745L,
         432352552240071530L,
         7278498720816365926L,
         -3357076846604974592L,
         7310873840697547124L,
         8009097783552271733L,
         576545729631112972L,
         6089471461968214310L,
         1956822834377108850L,
         513721691243808545L
      };
   }
}
