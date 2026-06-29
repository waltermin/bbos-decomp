package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.component.RadioButtonField;

public final class QmRadioButton extends RadioButtonField {
   public QmRadioButton(String label) {
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.keyChar('\n', status, time);
      return true;
   }
}
