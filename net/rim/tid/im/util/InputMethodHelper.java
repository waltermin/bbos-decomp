package net.rim.tid.im.util;

import net.rim.tid.awt.event.KeyEvent;

public class InputMethodHelper {
   public static boolean isIgnorableFunctionalKeyEvent(KeyEvent event) {
      switch (event.getKeyCode()) {
         case 17:
         case 18:
         case 19:
         case 273:
         case 4098:
            return true;
         default:
            return false;
      }
   }
}
