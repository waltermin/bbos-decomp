package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.WeakReference;

public final class GTPatriciaTreeHelper {
   private static WeakReference _tmpBufferWR = (WeakReference)(new Object(null));
   private static char[] _characterMap = new char[26];
   private static final int[] _bitCountMasks = new int[]{
      -1431655766,
      -858993460,
      -252645136,
      -16711936,
      -65536,
      -804651007,
      16777215,
      1866989824,
      727916,
      1987005697,
      16831589,
      -1972564893,
      207814912,
      1851099757,
      1956816537,
      712179968,
      712179968,
      -1975817147,
      16806977,
      -2104615050
   };

   public static final int getStringBit(String str, int offset, int length, int id, int bitNum) {
      int charNum = bitNum / 17;
      if (charNum >= length) {
         if (id == -1) {
            return -1;
         } else {
            bitNum -= 17 * length;
            if (bitNum != 0 && bitNum <= 32) {
               bitNum = 32 - bitNum;
               return id >> bitNum & 1;
            } else {
               return 0;
            }
         }
      } else {
         bitNum %= 17;
         if (bitNum == 0) {
            return 1;
         }

         bitNum = 16 - bitNum;
         char ch = str.charAt(offset + charNum);
         return ch >> bitNum & 1;
      }
   }

   public static final int compareStringBits(String lookupStr, int lookupOffset, int lookupLength, int lookupId, String str, int offset, int length, int id) {
      if (lookupId == id) {
         return 0;
      }

      int bitNum;
      for (bitNum = 1; lookupLength != 0; bitNum += 17) {
         if (length == 0) {
            return bitNum;
         }

         char lookupCh = lookupStr.charAt(lookupOffset);
         char ch = str.charAt(offset);
         if (lookupCh != ch) {
            return bitDifference(bitNum, lookupCh, ch, 16);
         }

         lookupOffset++;
         offset++;
         lookupLength--;
         length--;
      }

      return length == 0 ? bitDifference(bitNum, lookupId, id, 32) : -bitNum;
   }

   public static final int bitDifference(int bitNum, int lookup, int value, int numBits) {
      if (lookup == value) {
         return 0;
      }

      value ^= lookup;
      bitNum += numBits;
      int i = _bitCountMasks.length;

      for (int skip = 16; --i >= 0; skip >>= 1) {
         int masked = value & _bitCountMasks[i];
         if (masked != 0) {
            bitNum -= skip;
            value = masked;
         }
      }

      return (lookup & value) == 0 ? -bitNum : bitNum;
   }

   static final int getUIDFromID(int id) {
      return id >> 16 & 65535;
   }

   static final int getOffsetFromID(int id) {
      return id & 65535;
   }

   static final int createID(int uid, int offset) {
      return (uid & 65535) << 16 | offset & 65535;
   }

   public static final Object generateKeywords(String source) {
      Object keywords = null;
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         if (source != null) {
            StringBuffer buffer = getStringBuffer();
            SLKeyLayout layout = QmUtil.isReducedKeyboard() ? Keypad.getLayout() : null;
            synchronized (buffer) {
               buffer.append(source);

               for (int i = buffer.length() - 1; i >= 0; i--) {
                  char c = buffer.charAt(i);
                  c = Character.toLowerCase(CharacterUtilities.getOriginal(c));
                  if (layout != null && c >= 'a' && c <= 'z') {
                     if (_characterMap[c - 'a'] != 0) {
                        c = _characterMap[c - 'a'];
                     } else {
                        synchronized (layout) {
                           StringBuffer layoutBuffer = layout.getComplementaryChars(c, 0);
                           if (layoutBuffer != null && layoutBuffer.length() > 0) {
                              c = layoutBuffer.charAt(0);
                              _characterMap[c - 'a'] = c;

                              for (int j = 1; j < layoutBuffer.length(); j++) {
                                 _characterMap[layoutBuffer.charAt(j) - 'a'] = c;
                              }
                           } else {
                              _characterMap[c - 'a'] = c;
                           }
                        }
                     }
                  }

                  buffer.setCharAt(i, c);
               }

               source = buffer.toString();
               buffer.setLength(0);
            }

            return PersistentContent.encode(source, true, true);
         }

         keywords = "";
      }

      return keywords;
   }

   private static final synchronized StringBuffer getStringBuffer() {
      StringBuffer stringBuffer = (StringBuffer)_tmpBufferWR.get();
      if (stringBuffer == null) {
         stringBuffer = (StringBuffer)(new Object());
         _tmpBufferWR.set(stringBuffer);
      }

      stringBuffer.setLength(0);
      return stringBuffer;
   }
}
