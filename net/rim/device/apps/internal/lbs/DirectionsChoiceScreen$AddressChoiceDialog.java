package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.i18n.CommonResource;

final class DirectionsChoiceScreen$AddressChoiceDialog extends Dialog {
   private ButtonField _cancel;

   DirectionsChoiceScreen$AddressChoiceDialog(String message, Object[] choices, int[] values, int defaultChoice) {
      super(message, null, null, 0, null, 0);
      ButtonField workAddress = new ButtonField((String)choices[0], 12884901888L);
      ButtonField homeAddress = new ButtonField((String)choices[1], 12884901888L);
      this._cancel = new ButtonField(CommonResource.getString(10005), 12884901888L);
      this.add(workAddress);
      this.add(homeAddress);
      this.add(this._cancel);
      this._cancel.setChangeListener(this);
      workAddress.setChangeListener(this);
      homeAddress.setChangeListener(this);
      workAddress.setFocus();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._cancel) {
         this.cancel();
      } else {
         super.fieldChanged(field, context);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.cancel();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
