package net.rim.device.apps.api.transmission.rim.otasync;

import net.rim.device.api.ui.component.ObjectChoiceField;

class ObjectChoiceFieldWithId extends ObjectChoiceField {
   int _id;

   public ObjectChoiceFieldWithId(int id, String label, Object[] choices, int defaultIndex) {
      super(label, choices, defaultIndex);
      this._id = id;
   }
}
