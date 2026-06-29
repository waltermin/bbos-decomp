package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

public class SMILEvents {
   public static final String BEGIN;
   public static final String END;
   public static final String REPEAT;
   public static final String ANIMEND;
   public static final String FOCUSIN;
   public static final String FOCUSOUT;
   public static final String ACTIVATE;
   public static final int BEGIN_ID;
   public static final int END_ID;
   public static final int REPEAT_ID;
   public static final int ANIMEND_ID;
   public static final int FOCUSIN_ID;
   public static final int FOCUSOUT_ID;
   public static final int ACTIVATE_ID;
   public static final int IMPLICIT_END_OF_MEDIA_ID;
   public static final int UNDEFINED_ID;
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
