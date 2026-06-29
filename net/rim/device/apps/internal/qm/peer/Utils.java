package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

final class Utils {
   public static final int FIELD_ELEMENT_BIT_LENGTH = 160;
   public static final int FIELD_ELEMENT_BYTE_LENGTH = 20;
   public static final int PRIVATE_KEY_BYTE_LENGTH = 21;
   public static final int COMPRESSED_POINT_BYTE_LENGTH = 21;
   public static final int PUBLIC_KEY_BYTE_LENGTH = 21;
   public static final byte HIGH_BYTE_MASK = 0;
   public static final byte LOW_BYTE_MASK = 2;
   private static StringBuffer _buffer = (StringBuffer)(new Object());
   private static byte[] _codes = new byte[256];
   private static final String _alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
   private static final String TOSTRIP = "\n\n";
   private static final String TOSTRIP_GROUPWISE = (String)(new Object(new byte[]{32, 10, 32, 10}));

   private Utils() {
   }

   public static final byte[] getMD5Hash(String str) {
      Digest md5 = (Digest)(new Object());
      md5.update(str.getBytes());
      return md5.getDigest();
   }

   public static final String byteArrayToHex(byte[] array) {
      _buffer.setLength(0);

      for (int i = 0; i < array.length; i++) {
         int nybble = array[i] >> 4 & 15;
         _buffer.append(nybble >= 10 ? (char)(nybble - 10 + 97) : (char)(nybble + 48));
         nybble = array[i] & 15;
         _buffer.append(nybble >= 10 ? (char)(nybble - 10 + 97) : (char)(nybble + 48));
      }

      return _buffer.toString();
   }

   static final void add(int[] array, int element) {
      int length = array.length;
      Array.resize(array, length + 1);
      array[length] = element;
   }

   static final void removeAt(int[] array, int index) {
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   public static final char[] encodeBase64(byte[] toencode, int offset, int length) {
      int encodedLength = (length + 2) / 3 * 4;
      char[] encoded = new char[encodedLength];
      int i = offset;

      for (int index = 0; i < length; index += 4) {
         boolean quad = false;
         boolean trip = false;
         int val = 255 & toencode[i];
         val <<= 8;
         if (i + 1 < length) {
            val |= 255 & toencode[i + 1];
            trip = true;
         }

         val <<= 8;
         if (i + 2 < length) {
            val |= 255 & toencode[i + 2];
            quad = true;
         }

         encoded[index + 3] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(quad ? val & 63 : 64);
         val >>= 6;
         encoded[index + 2] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(trip ? val & 63 : 64);
         val >>= 6;
         encoded[index + 1] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(val & 63);
         val >>= 6;
         encoded[index] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(val & 63);
         i += 3;
      }

      return encoded;
   }

   public static final byte[] decodeBase64(byte[] encoded, int offset, int length) {
      int tempLen = length;

      for (int ix = offset; ix < offset + length; ix++) {
         if (encoded[ix] > 255 || _codes[encoded[ix]] < 0) {
            tempLen--;
         }
      }

      int len = tempLen / 4 * 3;
      if (tempLen % 4 == 3) {
         len += 2;
      }

      if (tempLen % 4 == 2) {
         len++;
      }

      byte[] out = new byte[len];
      int shift = 0;
      int accum = 0;
      int index = 0;

      for (int ix = offset; ix < offset + length; ix++) {
         int value = encoded[ix] > 255 ? -1 : _codes[encoded[ix]];
         if (value >= 0) {
            accum <<= 6;
            shift += 6;
            accum |= value;
            if (shift >= 8) {
               shift -= 8;
               out[index++] = (byte)(accum >> shift & 0xFF);
            }
         }
      }

      return out;
   }

   static final String stripNonBase64Chars(String text) {
      if (text != null) {
         int len = text.length();
         _buffer.setLength(0);

         for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(c) >= 0) {
               _buffer.append(c);
            }
         }

         return _buffer.toString();
      } else {
         return null;
      }
   }

   private static final int getStripStringIndex(String src, int fromIndex) {
      int index = src.indexOf("\n\n", fromIndex);
      return index == -1 ? src.indexOf(TOSTRIP_GROUPWISE, fromIndex) : index;
   }

   static final String stripLastTwoParagraphs(String text) {
      try {
         text = text.trim();
         int first = getStripStringIndex(text, 0);
         int second = first != -1 ? getStripStringIndex(text, first + 1) : -1;
         int next = -1;

         do {
            next = second != -1 ? getStripStringIndex(text, second + 1) : -1;
            if (next != -1) {
               first = second;
               second = next;
            }
         } while (next != -1);

         if (first != -1 && second != -1) {
            text = text.substring(0, first);
         }

         return text;
      } finally {
         ;
      }
   }

   public static final String resolveName(String toResolve) {
      _buffer.setLength(0);
      if (toResolve != null) {
         Object lookup = AddressBookServices.getAddressBook().reverseLookup(toResolve);
         if (lookup instanceof Object) {
            AddressCardModel card = (AddressCardModel)lookup;
            PersonNameModel person = card.getName();
            if (person != null) {
               String name = person.getFirstName();
               if (name != null) {
                  _buffer.append(name);
               }

               name = person.getLastName();
               if (name != null) {
                  if (_buffer.length() > 0) {
                     _buffer.append(' ');
                  }

                  _buffer.append(name);
               }
            }
         }
      }

      return _buffer.length() > 0 ? _buffer.toString() : toResolve;
   }

   public static final Verb[] getVerbs(String toResolve) {
      Verb[] verbs = null;
      if (toResolve != null) {
         verbs = new Object[0];
         Object lookup = AddressBookServices.getAddressBook().reverseLookup(toResolve);
         if (lookup instanceof Object) {
            ((VerbProvider)lookup).getVerbs(null, verbs);
         }
      }

      return verbs;
   }

   public static final AddressCardModel getAddressCard(String toResolve) {
      AddressCardModel result = null;
      if (toResolve != null) {
         Object lookup = AddressBookServices.getAddressBook().reverseLookup(toResolve);
         if (lookup instanceof Object) {
            result = (AddressCardModel)lookup;
         }
      }

      return result;
   }

   public static final boolean isValidString(String s) {
      return s != null && s.length() > 0;
   }

   static {
      Arrays.fill(_codes, (byte)-1);

      for (int i = 65; i <= 90; i++) {
         _codes[i] = (byte)(i - 65);
      }

      for (int i = 97; i <= 122; i++) {
         _codes[i] = (byte)(26 + i - 97);
      }

      for (int i = 48; i <= 57; i++) {
         _codes[i] = (byte)(52 + i - 48);
      }

      _codes[43] = 62;
      _codes[47] = 63;
   }
}
