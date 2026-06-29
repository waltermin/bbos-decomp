package net.rim.device.internal.i18n;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

final class UnicodeServiceUtilities$DefaultUnicodeServiceProvider implements UnicodeServiceProvider {
   private byte[] _defaultSupportedEncodings;
   private static String[] _definedSerializationEncodingsNames = new String[]{"UTF-8\r", "UTF-16BE\r"};
   private static byte[] _definedSerializationEncodingsBytes = new byte[]{0, 1};

   private UnicodeServiceUtilities$DefaultUnicodeServiceProvider() {
   }

   @Override
   public final synchronized byte[] getSupportedEncodings() {
      if (this._defaultSupportedEncodings == null || this._defaultSupportedEncodings.length == 0) {
         TextProcessingRegistry tr = TextProcessingRegistry.getInstance();
         if (tr != null) {
            String[] deviceEncodings = tr.getSupported(0);
            byte encodingByte = -1;
            if (deviceEncodings != null && deviceEncodings.length > 0) {
               byte[] newSupportedEncodingsBytes = new byte[deviceEncodings.length];
               int count = 0;

               for (int i = 0; i < deviceEncodings.length; i++) {
                  if ((encodingByte = this.getEncoding(deviceEncodings[i])) != -1) {
                     newSupportedEncodingsBytes[count] = encodingByte;
                     count++;
                  }
               }

               if (count > 0) {
                  Array.resize(newSupportedEncodingsBytes, count);
                  this._defaultSupportedEncodings = newSupportedEncodingsBytes;
               }
            }
         }

         if (this._defaultSupportedEncodings == null) {
            this._defaultSupportedEncodings = new byte[]{-1};
         }
      }

      return this._defaultSupportedEncodings;
   }

   @Override
   public final byte resolveEncoding(byte[] clientServiceEncodings, byte[] hostServiceEncodings) {
      if (hostServiceEncodings != null && hostServiceEncodings.length != 0 && clientServiceEncodings != null && clientServiceEncodings.length != 0) {
         byte prefEnc = UnicodeServiceUtilities.getPreferredEncoding();
         byte curEnc = -1;
         if (this._defaultSupportedEncodings == null) {
            this._defaultSupportedEncodings = this.getSupportedEncodings();
            if (this._defaultSupportedEncodings == null) {
               return -1;
            }
         }

         for (int i = 0; i < clientServiceEncodings.length; i++) {
            for (int j = 0; j < hostServiceEncodings.length; j++) {
               if (hostServiceEncodings[j] == clientServiceEncodings[i]) {
                  int k = 0;

                  while (k < this._defaultSupportedEncodings.length && this._defaultSupportedEncodings[k] != clientServiceEncodings[i]) {
                     k++;
                  }

                  if (k < this._defaultSupportedEncodings.length) {
                     if (curEnc == -1) {
                        curEnc = clientServiceEncodings[i];
                     }

                     if (prefEnc == clientServiceEncodings[i] && prefEnc != -1) {
                        return prefEnc;
                     }
                  }
               }
            }
         }

         return curEnc;
      } else {
         return -1;
      }
   }

   @Override
   public final String getEncoding(byte encodingType) {
      for (int i = 0; i < _definedSerializationEncodingsBytes.length; i++) {
         if (encodingType == _definedSerializationEncodingsBytes[i]) {
            return _definedSerializationEncodingsNames[i];
         }
      }

      return "";
   }

   @Override
   public final byte getEncoding(String encodingType) {
      if (encodingType != null) {
         for (int i = 0; i < _definedSerializationEncodingsBytes.length; i++) {
            if (StringUtilities.startsWithIgnoreCase(_definedSerializationEncodingsNames[i], encodingType, 1701707776)) {
               return _definedSerializationEncodingsBytes[i];
            }
         }
      }

      return -1;
   }

   UnicodeServiceUtilities$DefaultUnicodeServiceProvider(UnicodeServiceUtilities$1 x0) {
      this();
   }
}
