package net.rim.device.apps.internal.alarm;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.GlobalEventListener;

final class Alarm$1 implements GlobalEventListener {
   private final Alarm this$0;

   Alarm$1(Alarm _1) {
      this.this$0 = _1;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 7207871974803693937L) {
         this.this$0._alarmTime.setFormat(DateFormat.getInstance(6));
      }
   }
}
