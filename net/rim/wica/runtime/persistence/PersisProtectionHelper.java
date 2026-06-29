package net.rim.wica.runtime.persistence;

import net.rim.device.api.system.PersistentContent;

final class PersisProtectionHelper {
   static final boolean needCoding() {
      return PersistentContent.isEncryptionEnabled();
   }

   static final Object encode(Object value) {
      if (!needCoding()) {
         return value;
      }

      if (value instanceof String) {
         return PersistentContent.encode((String)value, false, true);
      }

      if (value instanceof String[]) {
         String[] valueStrings = (String[])value;
         int len = valueStrings.length;
         Object[] tempEncoded = new Object[len];

         for (int i = 0; i < len; i++) {
            tempEncoded[i] = PersistentContent.encode(valueStrings[i], false, true);
         }

         return tempEncoded;
      } else {
         if (!(value instanceof Object[])) {
            return value;
         }

         Object[] tempObjs = (Object[])value;
         int len = tempObjs.length;
         Object[] tempEncoded = new Object[len];

         for (int i = 0; i < len; i++) {
            tempEncoded[i] = encode(tempObjs[i]);
         }

         return tempEncoded;
      }
   }

   static final Object decode(Object encodedValue) {
      if (!(encodedValue instanceof Object[])) {
         return encodedValue != null
               && !(encodedValue instanceof long[])
               && !(encodedValue instanceof double[])
               && !(encodedValue instanceof int[])
               && !(encodedValue instanceof byte[])
               && !(encodedValue instanceof boolean[])
            ? PersistentContent.decodeString(encodedValue)
            : encodedValue;
      }

      Object[] tempEncoded = (Object[])encodedValue;
      int len = tempEncoded.length;
      Object[] tempValues = new Object[len];

      for (int i = 0; i < len; i++) {
         tempValues[i] = decode(tempEncoded[i]);
      }

      return tempValues;
   }

   static final Object reEncode(Object encodedValue) {
      if (!(encodedValue instanceof Object[])) {
         if (encodedValue == null
            || encodedValue instanceof long[]
            || encodedValue instanceof double[]
            || encodedValue instanceof int[]
            || encodedValue instanceof byte[]
            || encodedValue instanceof boolean[]) {
            return encodedValue;
         } else {
            return PersistentContent.checkEncoding(encodedValue, false, true) ? encodedValue : PersistentContent.reEncode(encodedValue, false, true);
         }
      } else {
         Object[] tempEncoded = (Object[])encodedValue;
         int len = tempEncoded.length;
         Object[] tempValues = new Object[len];

         for (int i = 0; i < len; i++) {
            tempValues[i] = reEncode(tempEncoded[i]);
         }

         return tempValues;
      }
   }
}
