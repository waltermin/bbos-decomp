package net.rim.device.api.system;

public class EventInjector$KeyEvent extends EventInjector$Event {
   public static final int KEY_DOWN = 513;
   public static final int KEY_REPEAT = 514;
   public static final int KEY_UP = 515;

   public EventInjector$KeyEvent(int event, char c, int status, int time) {
      super(2, event, c, status, time, null, null);
   }

   public void setChar(char c) {
      super._msg.setSubMessage(c);
   }

   public char getChar() {
      return (char)super._msg.getSubMessage();
   }

   public void setTime(int time) {
      super._msg.setData1(time);
   }

   public int getTime() {
      return super._msg.getData1();
   }
}
