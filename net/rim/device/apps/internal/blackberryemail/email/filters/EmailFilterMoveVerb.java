package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class EmailFilterMoveVerb extends Verb {
   EmailFilterCollectionListField _listField;

   EmailFilterMoveVerb(EmailFilterCollectionListField listField) {
      super(629013);
      this._listField = listField;
   }

   @Override
   public String toString() {
      return EmailResources.getString(1009);
   }

   @Override
   public Object invoke(Object context) {
      this._listField.toggleMoveFilterState();
      return null;
   }
}
