package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.text.IPTextFilter;
import net.rim.device.api.util.CharacterUtilities;

public class IPEditField extends EditField {
   private boolean keyRepeatProcessed;
   private int lastKeyPosition;

   public IPEditField(String label, String initialValue) {
      super(label, initialValue);
      this.setFilter(new IPTextFilter());
   }

   public IPEditField(String label, String initialValue, int maxNumChars) {
      super(label, initialValue, maxNumChars, 0);
      this.setFilter(new IPTextFilter());
   }

   public IPEditField(String label, String initialValue, int maxNumChars, int filterFlags) {
      super(label, initialValue, maxNumChars, 0);
      this.setFilter(new IPTextFilter(filterFlags));
   }

   @Override
   protected boolean insert(char key, int status) {
      if (Character.isUpperCase(key)) {
         key = CharacterUtilities.toLowerCase(key, 1701707776);
      }

      if (key == ' ' || key == '.' || key == ':') {
         int portCharactersUsed = 0;
         int pointCharactersUsed = 0;
         int lettersUsed = 0;
         String text = this.getText();

         for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '.') {
               pointCharactersUsed++;
            }

            if (ch == ':') {
               portCharactersUsed++;
            }

            if (Character.isLowerCase(ch)) {
               lettersUsed++;
            }
         }

         if (key == ' ') {
            if (pointCharactersUsed == 3 && lettersUsed == 0) {
               key = ':';
            } else {
               key = '.';
            }
         }

         if (key == ':' && portCharactersUsed == 2) {
            return false;
         }
      }

      return super.insert(key, status);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      this.keyRepeatProcessed = false;
      this.lastKeyPosition = this.getCursorPosition();
      return super.keyDown(keycode, time);
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      boolean handled = false;
      if (!this.keyRepeatProcessed) {
         char altedKeyChar = Keypad.getAltedChar(Keypad.map(keycode));
         if (Character.isDigit(altedKeyChar)) {
            if (this.lastKeyPosition != this.getCursorPosition()) {
               this.backspace();
            }

            this.insert(altedKeyChar, 0);
            this.keyRepeatProcessed = true;
            handled = true;
         }
      }

      if (!handled) {
         handled = super.keyRepeat(keycode, time);
      }

      return handled;
   }

   public static final int parseIpAddr(String str) {
      int ipAddr = 0;
      int index = 0;
      int nextIndex = str.indexOf(46);
      int val = 0;
      boolean badNumber = false;

      int i;
      for (i = 0; index < str.length(); i++) {
         nextIndex = str.indexOf(46, index);
         if (nextIndex == -1) {
            nextIndex = str.length();
         }

         try {
            val = Integer.parseInt(str.substring(index, nextIndex));
         } catch (NumberFormatException e) {
            badNumber = true;
         }

         if (val < 0 || val > 255 || badNumber) {
            throw new IllegalArgumentException("Invalid IP Address. An octet must be in range 0-255");
         }

         ipAddr <<= 8;
         ipAddr |= val;
         index = nextIndex + 1;
      }

      if (i != 4) {
         throw new IllegalArgumentException("Invalid IP Address. Must be ###.###.###.###");
      } else {
         return ipAddr;
      }
   }

   public static final void appendIpAddr(StringBuffer strBuf, int ip) {
      for (int shift = 24; shift >= 0; shift -= 8) {
         int temp = ip >>> shift & 0xFF;
         strBuf.append(temp);
         if (shift != 0) {
            strBuf.append('.');
         }
      }
   }

   public static final void appendIpAddr(StringBuffer strBuf, byte[] ip) {
      if (ip == null) {
         strBuf.append("0.0.0.0");
      } else {
         for (int i = 0; i < 4; i++) {
            strBuf.append(ip[i] & 255);
            if (i != 3) {
               strBuf.append('.');
            }
         }
      }
   }
}
