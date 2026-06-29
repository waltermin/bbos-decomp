package net.rim.device.api.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Graphics;

public class EmoticonStringPattern extends StringPattern {
   public static long SYSTEM_DEFAULT_KEY = 8885498469575442273L;

   public static EmoticonStringPattern getSystemDefault() {
      return (EmoticonStringPattern)ApplicationRegistry.getApplicationRegistry().get(SYSTEM_DEFAULT_KEY);
   }

   public int emoticonSize() {
      throw null;
   }

   public void drawEmoticon(Graphics _1, int _2, int _3, int _4) {
      throw null;
   }

   public String emoticonReplacementText(int _1) {
      throw null;
   }

   public String emoticonDescription(int _1) {
      throw null;
   }

   public int[][] emoticonScreenLayouts() {
      throw null;
   }
}
