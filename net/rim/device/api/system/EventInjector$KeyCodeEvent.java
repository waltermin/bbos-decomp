package net.rim.device.api.system;

import net.rim.device.api.ui.KeypadUtil;

public class EventInjector$KeyCodeEvent extends EventInjector$Event {
   public static final int KEY_DOWN;
   public static final int KEY_REPEAT;
   public static final int KEY_UP;

   public EventInjector$KeyCodeEvent(int event, char keyName, int status, int time) {
      super(2, event, 0, charToScancode(keyName, status), time, null, null);
      if (!this.isVaildStatus(status)) {
         throw new IllegalArgumentException();
      }
   }

   public int getKeyCode() {
      int scanCode = super._msg.getData0();
      return this.extractKeyCode(scanCode);
   }

   public void setKeyCode(char keyName, int status) {
      if (!this.isVaildStatus(status)) {
         throw new IllegalArgumentException();
      }

      super._msg.setData0(charToScancode(keyName, status));
   }

   @Override
   public void setStatus(int status) {
      if (!this.isVaildStatus(status)) {
         throw new IllegalArgumentException();
      }

      int keyCode = this.getKeyCode();
      int scanCode = keyCode << 16;
      scanCode |= status;
      super._msg.setData0(scanCode);
   }

   @Override
   public int getStatus() {
      int scanCode = super._msg.getData0();
      return this.extractStatus(scanCode);
   }

   public int getTime() {
      return super._msg.getData1();
   }

   public void setTime(int time) {
      super._msg.setData1(time);
   }

   private static int charToScancode(char c, int status) {
      if (c >= 128 && c <= 159) {
         return KeypadUtil.getKeyCode(c, 0, 0);
      }

      if (c >= 'a' && c <= 'z') {
         c = (char)(c - ' ');
      }

      int scancode = c << 16;
      return scancode | status;
   }

   private int extractKeyCode(int scanCode) {
      return scanCode >> 16;
   }

   private int extractStatus(int scanCode) {
      int lowMask = 65535;
      return lowMask & scanCode;
   }

   private boolean isVaildStatus(int status) {
      return (status & -65536) == 0;
   }
}
