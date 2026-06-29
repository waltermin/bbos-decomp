package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class MoveOverrideVerb extends Verb {
   private OverrideListField _overrideListField;

   MoveOverrideVerb(OverrideListField overrideListField) {
      super(626741);
      this._overrideListField = overrideListField;
   }

   @Override
   public String toString() {
      return CommonResources.getString(9120);
   }

   @Override
   public Object invoke(Object context) {
      this._overrideListField.toggleMoveOverrideState();
      return null;
   }
}
