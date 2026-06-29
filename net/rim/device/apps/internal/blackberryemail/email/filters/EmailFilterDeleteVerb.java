package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.i18n.CommonResource;

final class EmailFilterDeleteVerb extends Verb {
   private EmailFilterCollectionListField _listField;
   private String _userId;
   private static EmailFilterDeleteVerb _theVerb = new EmailFilterDeleteVerb();

   private EmailFilterDeleteVerb() {
      super(629014);
   }

   static final EmailFilterDeleteVerb getInstance(EmailFilterCollectionListField listField, String userId) {
      _theVerb._listField = listField;
      _theVerb._userId = userId;
      return _theVerb;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(17);
   }

   @Override
   public final Object invoke(Object parameter) {
      this._listField.setAlerted(false);
      Object obj = this._listField.getSelectedElement();
      if (obj != null) {
         EmailFilterModelImplClone m = (EmailFilterModelImplClone)obj;
         String promptmessage = EmailResources.getString(192);
         promptmessage = promptmessage.concat(((StringBuffer)(new Object(" '"))).append(m.getName()).append("'?").toString());
         int retVal = Dialog.ask(2, promptmessage, -1);
         if (retVal == 3) {
            this._listField.delete(this._listField.getSelectedIndex());
            EmailFilter.getCollection(this._userId).remove(m._org);
         }
      }

      this._listField.setAlerted(true);
      return null;
   }
}
