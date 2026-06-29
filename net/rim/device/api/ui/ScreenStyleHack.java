package net.rim.device.api.ui;

public final class ScreenStyleHack {
   private ScreenStyleHack() {
   }

   public static final void setStyleSystem(Field f, long on, long off) {
      f.setStyleSystem(on, off);
   }
}
