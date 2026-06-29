package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.component.ButtonField;

public class QmButton extends ButtonField {
   public QmButton(String label, long flags) {
      super(label, flags);
   }

   public void invoke() {
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         this.invoke();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.invoke();
      return true;
   }
}
