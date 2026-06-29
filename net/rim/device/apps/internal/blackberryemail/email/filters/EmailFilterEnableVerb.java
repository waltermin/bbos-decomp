package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class EmailFilterEnableVerb extends Verb {
   private EmailFilterCollectionListField _listField;
   private static EmailFilterEnableVerb _theVerb = new EmailFilterEnableVerb();

   EmailFilterEnableVerb() {
      super(30270);
   }

   static final EmailFilterEnableVerb getInstance(EmailFilterCollectionListField listField) {
      _theVerb._listField = listField;
      return _theVerb;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(900);
   }

   @Override
   public final Object invoke(Object context) {
      Object obj = this._listField.getSelectedElement();
      if (obj != null) {
         EmailFilterModelImplClone m = (EmailFilterModelImplClone)obj;
         m.toggleStatus();
         m._hasChanged = true;
         this._listField.invalidate(this._listField.getSelectedIndex());
         this._listField.setMuddy(true);
      }

      return null;
   }
}
