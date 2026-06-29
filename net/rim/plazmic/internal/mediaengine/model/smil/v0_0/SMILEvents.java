package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

public class SMILEvents {
   public static final String BEGIN = "begin";
   public static final String END = "end";
   public static final String REPEAT = "repeat";
   public static final String ANIMEND = "animend";
   public static final String FOCUSIN = "focusin";
   public static final String FOCUSOUT = "focusout";
   public static final String ACTIVATE = "activate";
   public static final int BEGIN_ID = 1;
   public static final int END_ID = 2;
   public static final int REPEAT_ID = 3;
   public static final int ANIMEND_ID = 4;
   public static final int FOCUSIN_ID = 11;
   public static final int FOCUSOUT_ID = 12;
   public static final int ACTIVATE_ID = 13;
   public static final int IMPLICIT_END_OF_MEDIA_ID = 23;
   public static final int UNDEFINED_ID = -1;
   private static final String[] NAMES = new String[]{"begin", "end", "repeat", "animend", "focusin", "focusout", "activate"};
   private static final int[] IDS = new int[]{
      1,
      2,
      3,
      4,
      11,
      12,
      13,
      -805044223,
      2,
      -804651007,
      51,
      1870004480,
      16803179,
      1701539702,
      725324,
      12956929,
      67123971,
      -1485792700,
      107957625,
      106628608,
      5066562,
      1694657542,
      134219776,
      1165246502,
      134253174,
      134251046,
      119610152,
      1948780544
   };

   public static int getIntId(String name) {
      for (int i = 0; i < NAMES.length; i++) {
         if (NAMES[i].equals(name)) {
            return IDS[i];
         }
      }

      return -1;
   }
}
