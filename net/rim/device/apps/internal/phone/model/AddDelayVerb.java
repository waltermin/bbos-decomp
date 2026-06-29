package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class AddDelayVerb extends Verb {
   private int _type;
   private EditField _editField;
   static final int STOP = 0;
   static final int PAUSE = 1;

   public AddDelayVerb(EditField editField, int type) {
      super(16864261);
      this._editField = editField;
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      char ch = (char)(this._type == 0 ? 33 : 44);
      this._editField.insert("" + ch);
      this._editField.setDirty(true);
      return null;
   }

   @Override
   public final String toString() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         default:
            return PhoneResources.getString(446);
         case 1:
            return PhoneResources.getString(445);
      }
   }
}
