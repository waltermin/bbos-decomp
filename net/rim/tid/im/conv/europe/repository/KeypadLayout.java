package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Keypad;
import net.rim.tid.im.layout.SLKeyLayout;

public class KeypadLayout {
   private static String[] _adjacentKeys;
   private static char[] _sharedKeys;

   private static char[] initSharedKeysReducedDefault() {
      char[] sharedKeys = new char[256];
      initSharedKey(sharedKeys, 'a', 's');
      initSharedKey(sharedKeys, 'b', 'n');
      initSharedKey(sharedKeys, 'c', 'v');
      initSharedKey(sharedKeys, 'd', 'f');
      initSharedKey(sharedKeys, 'e', 'r');
      initSharedKey(sharedKeys, 'f', 'd');
      initSharedKey(sharedKeys, 'g', 'h');
      initSharedKey(sharedKeys, 'h', 'g');
      initSharedKey(sharedKeys, 'i', 'u');
      initSharedKey(sharedKeys, 'j', 'k');
      initSharedKey(sharedKeys, 'k', 'j');
      initSharedKey(sharedKeys, 'n', 'b');
      initSharedKey(sharedKeys, 'o', 'p');
      initSharedKey(sharedKeys, 'p', 'o');
      initSharedKey(sharedKeys, 'q', 'w');
      initSharedKey(sharedKeys, 'r', 'e');
      initSharedKey(sharedKeys, 's', 'a');
      initSharedKey(sharedKeys, 't', 'y');
      initSharedKey(sharedKeys, 'u', 'i');
      initSharedKey(sharedKeys, 'v', 'c');
      initSharedKey(sharedKeys, 'w', 'q');
      initSharedKey(sharedKeys, 'x', 'z');
      initSharedKey(sharedKeys, 'y', 't');
      initSharedKey(sharedKeys, 'z', 'x');
      initSharedKey(sharedKeys, 'A', 'S');
      initSharedKey(sharedKeys, 'B', 'N');
      initSharedKey(sharedKeys, 'C', 'V');
      initSharedKey(sharedKeys, 'D', 'F');
      initSharedKey(sharedKeys, 'E', 'R');
      initSharedKey(sharedKeys, 'F', 'D');
      initSharedKey(sharedKeys, 'G', 'H');
      initSharedKey(sharedKeys, 'H', 'G');
      initSharedKey(sharedKeys, 'I', 'U');
      initSharedKey(sharedKeys, 'J', 'K');
      initSharedKey(sharedKeys, 'K', 'J');
      initSharedKey(sharedKeys, 'N', 'B');
      initSharedKey(sharedKeys, 'O', 'P');
      initSharedKey(sharedKeys, 'P', 'O');
      initSharedKey(sharedKeys, 'Q', 'W');
      initSharedKey(sharedKeys, 'R', 'E');
      initSharedKey(sharedKeys, 'S', 'A');
      initSharedKey(sharedKeys, 'T', 'Y');
      initSharedKey(sharedKeys, 'U', 'I');
      initSharedKey(sharedKeys, 'V', 'C');
      initSharedKey(sharedKeys, 'W', 'Q');
      initSharedKey(sharedKeys, 'X', 'Z');
      initSharedKey(sharedKeys, 'Y', 'T');
      initSharedKey(sharedKeys, 'Z', 'X');
      return sharedKeys;
   }

   private static void initSharedKey(char[] map, char ch1, char ch2) {
      map[ch1] = ch2;
   }

   private static String[] initDefault() {
      String[] adjacentKeys = new Object[256];
      initAdjacentChars(adjacentKeys, 'a', "qwsz");
      initAdjacentChars(adjacentKeys, 'b', "vghjn");
      initAdjacentChars(adjacentKeys, 'c', "xdfgv");
      initAdjacentChars(adjacentKeys, 'd', "wersfzxc");
      initAdjacentChars(adjacentKeys, 'e', "wsdfr");
      initAdjacentChars(adjacentKeys, 'f', "dertgvcx");
      initAdjacentChars(adjacentKeys, 'g', "rtyfhcvb");
      initAdjacentChars(adjacentKeys, 'h', "tyugjvbn");
      initAdjacentChars(adjacentKeys, 'i', "uojkl");
      initAdjacentChars(adjacentKeys, 'j', "yuihkbnm");
      initAdjacentChars(adjacentKeys, 'k', "uiojlbnm");
      initAdjacentChars(adjacentKeys, 'l', "iopkm");
      initAdjacentChars(adjacentKeys, 'm', "jkln");
      initAdjacentChars(adjacentKeys, 'n', "hjkbm");
      initAdjacentChars(adjacentKeys, 'o', "ipkl");
      initAdjacentChars(adjacentKeys, 'p', "ol");
      initAdjacentChars(adjacentKeys, 'q', "was");
      initAdjacentChars(adjacentKeys, 'r', "etdfg");
      initAdjacentChars(adjacentKeys, 's', "qweadzx");
      initAdjacentChars(adjacentKeys, 't', "ryfgh");
      initAdjacentChars(adjacentKeys, 'u', "yihjk");
      initAdjacentChars(adjacentKeys, 'v', "fghcb");
      initAdjacentChars(adjacentKeys, 'w', "qeasd");
      initAdjacentChars(adjacentKeys, 'x', "sdfzc");
      initAdjacentChars(adjacentKeys, 'y', "tughj");
      initAdjacentChars(adjacentKeys, 'z', "asdx");
      return adjacentKeys;
   }

   private static String[] initQwerty(String[] defaults) {
      return defaults;
   }

   private static String[] initAzerty(String[] defaults) {
      String[] adjacentKeys = new Object[256];
      System.arraycopy(defaults, 0, adjacentKeys, 0, 256);
      initAdjacentChars(adjacentKeys, 'a', "zqs");
      initAdjacentChars(adjacentKeys, 'e', "zsdfr");
      initAdjacentChars(adjacentKeys, 'q', "awsz");
      initAdjacentChars(adjacentKeys, 'w', "qsdx");
      initAdjacentChars(adjacentKeys, 'x', "sdfwc");
      initAdjacentChars(adjacentKeys, 'z', "qeasd");
      return adjacentKeys;
   }

   private static String[] initQwertz(String[] defaults) {
      String[] adjacentKeys = new Object[256];
      System.arraycopy(defaults, 0, adjacentKeys, 0, 256);
      initAdjacentChars(adjacentKeys, 'a', "qwsy");
      initAdjacentChars(adjacentKeys, 'd', "wersfyxc");
      initAdjacentChars(adjacentKeys, 'g', "rtzfhcvb");
      initAdjacentChars(adjacentKeys, 'h', "tzugjvbn");
      initAdjacentChars(adjacentKeys, 'j', "zuihkbnm");
      initAdjacentChars(adjacentKeys, 's', "qweadyx");
      initAdjacentChars(adjacentKeys, 't', "rzfgh");
      initAdjacentChars(adjacentKeys, 'u', "zihjk");
      initAdjacentChars(adjacentKeys, 'x', "sdfyc");
      initAdjacentChars(adjacentKeys, 'y', "asdx");
      initAdjacentChars(adjacentKeys, 'z', "tughj");
      return adjacentKeys;
   }

   private static void initAdjacentChars(String[] keys, char ch, String str) {
      keys[ch] = str;
   }

   public static boolean isAdjacent(char ch1, char ch2) {
      if (_adjacentKeys == null) {
         return false;
      } else {
         int chi = ch1;
         if (chi < 256 && chi > 0) {
            String adjacentCharacters = _adjacentKeys[chi];
            return adjacentCharacters != null ? adjacentCharacters.indexOf(ch2) != -1 : false;
         } else {
            return false;
         }
      }
   }

   public static String getAdjacentCharacters(char ch) {
      if (_adjacentKeys == null) {
         return null;
      }

      int chi = ch;
      return chi < 256 && chi > 0 ? _adjacentKeys[chi] : null;
   }

   public static boolean isShared(char ch1, char ch2) {
      if (_sharedKeys == null) {
         return false;
      }

      int chi = ch1;
      return chi < 256 && chi > 0 ? _sharedKeys[chi] == ch2 : false;
   }

   public static boolean supportsSharedCharacters() {
      return _sharedKeys != null;
   }

   public static char getSharedCharacter(char ch) {
      if (_sharedKeys == null) {
         return '\u0000';
      }

      int chi = ch;
      if (chi < 256 && chi > 0) {
         return _sharedKeys[chi];
      }

      SLKeyLayout layout = Keypad.getLayout();
      int code = layout.getOriginalKeyCode(ch, 0);
      if (code == 0) {
         return '\u0000';
      }

      StringBuffer chars = layout.getKeyChars(code, 0);
      if (chars.length() == 1) {
         return '\u0000';
      }

      char firstCh = chars.charAt(0);
      return firstCh == ch ? chars.charAt(1) : firstCh;
   }

   static {
      switch (Keypad.getHardwareLayout()) {
         case 1364341300:
         case 1364346180:
            _sharedKeys = initSharedKeysReducedDefault();
            return;
         default:
            switch (Locale.getDefaultForKeyboard().getCode()) {
               case 1684340736:
                  _adjacentKeys = initQwertz(initDefault());
                  return;
               case 1718747136:
                  _adjacentKeys = initAzerty(initDefault());
                  return;
               default:
                  _adjacentKeys = initQwerty(initDefault());
            }
      }
   }
}
