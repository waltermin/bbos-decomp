package net.rim.device.cldc.impl.hrt;

final class HRTRequestThread$TriggerEvent {
   public int event;
   public long context;
   public Object obj;

   public HRTRequestThread$TriggerEvent(int e, long c, Object o) {
      this.event = e;
      this.context = c;
      this.obj = o;
   }
}
