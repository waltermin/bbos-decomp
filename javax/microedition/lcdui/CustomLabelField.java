package javax.microedition.lcdui;

import net.rim.device.api.ui.component.LabelField;

class CustomLabelField extends LabelField {
   private CustomField _customField;

   public CustomLabelField(String label, CustomField field) {
      super(label, 18014398509481984L);
      this._customField = field;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      return this._customField.keyDown(keycode, time);
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      return this._customField.keyUp(keycode, time);
   }

   @Override
   protected void drawFocus(net.rim.device.api.ui.Graphics graphics, boolean on) {
   }
}
