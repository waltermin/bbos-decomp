package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.AutoTextEditField;

public class LimitedScrollingMultilineEditField extends AutoTextEditField {
   public LimitedScrollingMultilineEditField(String label, String initialText) {
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      int moveAmount = super.moveFocus(amount, status, time);
      return moveAmount < 0 ? 0 : moveAmount;
   }
}
