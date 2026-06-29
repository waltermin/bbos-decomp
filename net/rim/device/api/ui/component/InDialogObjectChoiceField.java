package net.rim.device.api.ui.component;

public final class InDialogObjectChoiceField extends ObjectChoiceField {
   public InDialogObjectChoiceField(String label, Object[] choices) {
   }

   public InDialogObjectChoiceField(String label, Object[] choices, int initialIndex) {
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.changeOptionDialog();
      return true;
   }
}
