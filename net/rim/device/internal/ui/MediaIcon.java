package net.rim.device.internal.ui;

public class MediaIcon {
   public static final char PLAY = '\u0000';
   public static final char PAUSE = '\u0001';
   public static final char REVERSE = '\u0002';
   public static final char FORWARD = '\u0003';
   public static final char SKIP_REVERSE = '\u0004';
   public static final char SKIP_FORWARD = '\u0005';
   public static final char STOP = '\u0006';
   public static final char EJECT = '\u0007';
   public static final char RECORD = '\b';
   public static final char VOLUME = '\t';
   private static final int ICON_COUNT = 10;
   public static final IconCollection COLLECTION = IconCollection.get("net_rim_Media", 10);

   private MediaIcon() {
   }
}
