package net.rim.device.internal.ui.autotext;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.i18n.LocaleUtil;

final class AutoTextCollection$AutoTextSyncConverter implements SyncConverter {
   private static final int AT_FIELD_ORIGINAL_TEXT = 1;
   private static final int AT_FIELD_REPLACEMENT_TEXT = 2;
   private static final int AT_FIELD_LOCALE_NUMBER = 4;
   private static final int AT_FIELD_CASE_FLAGS = 5;
   private static final int AT_FIELD_EXTENDED_LOCALE_NUMBER = 6;

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof AutoTextEntry)) {
         return false;
      }

      AutoTextEntry entry = (AutoTextEntry)object;
      ConverterUtilities.writeStringSmart(buffer, 1, entry.getFindString());
      ConverterUtilities.writeStringSmart(buffer, 2, entry.getReplaceString());
      int localeCode = entry.getLocaleCode();
      int oldLocaleCode = LocaleUtil.convertJavaLocaleToCppLocale(localeCode);
      ConverterUtilities.convertInt(buffer, 4, oldLocaleCode, 4);
      ConverterUtilities.convertInt(buffer, 5, entry.getCase(), 4);
      ConverterUtilities.convertInt(buffer, 6, localeCode, 4);
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      String originalText = null;
      String replacementText = null;
      int localeCode = 0;
      int caseFlags = 0;
      int position = dataBuffer.getPosition();
      boolean caseFlagsMissing = true;
      if (version == 0 || version >= 2 && version <= 3) {
         try {
            if (ConverterUtilities.findType(dataBuffer, 1, true)) {
               originalText = ConverterUtilities.readString(dataBuffer, true);
            }

            dataBuffer.setPosition(position);
            if (ConverterUtilities.findType(dataBuffer, 2, true)) {
               replacementText = ConverterUtilities.readString(dataBuffer, true);
            }

            dataBuffer.setPosition(position);
            if (ConverterUtilities.findType(dataBuffer, 4)) {
               localeCode = ConverterUtilities.readInt(dataBuffer);
               if (version == 2) {
                  localeCode = LocaleUtil.convertCppLocaleToJavaLocale(localeCode);
               }
            }

            dataBuffer.setPosition(position);
            if (ConverterUtilities.findType(dataBuffer, 6)) {
               localeCode = ConverterUtilities.readInt(dataBuffer);
            }

            dataBuffer.setPosition(position);
            if (ConverterUtilities.findType(dataBuffer, 5)) {
               caseFlags = ConverterUtilities.readInt(dataBuffer);
               caseFlagsMissing = false;
            }

            if (originalText != null) {
               if (caseFlagsMissing
                  && (
                     originalText.equals("i")
                        || originalText.equals("id")
                        || originalText.equals("il")
                        || originalText.equals("im")
                        || originalText.equals("ive")
                        || originalText.equals("www")
                  )) {
                  caseFlags = 1;
               }

               if (localeCode != 0) {
                  try {
                     Locale.get(localeCode);
                  } finally {
                     ;
                  }
               }

               return new AutoTextEntry(originalText, replacementText, caseFlags, localeCode, uid);
            }
         } finally {
            return null;
         }

         return null;
      } else {
         return null;
      }
   }
}
