package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;

final class AddStoreAction extends Verb {
   private EmailFilterCollectionListField _listField;
   private String _userId;
   private static AddStoreAction _theVerb = new AddStoreAction();

   AddStoreAction() {
      super(0);
   }

   static final AddStoreAction getInstance(EmailFilterCollectionListField listField, String userId) {
      _theVerb._listField = listField;
      _theVerb._userId = userId;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (parameter != null) {
         if (!ObjectGroup.isInGroup(parameter) && parameter instanceof EmailFolder) {
            EmailFilterModelImpl model = (EmailFolder)parameter;
            model._fields.trimToSize();
            model.setOrder(EmailFilter.getCollection(this._userId).size());
            this._listField.add(EmailFilter.clone(model));
            this._listField.setSelectedIndex(this._listField.getSize() - 1);
         }

         EmailFilter.getCollection(this._userId).add(parameter);
      }

      return parameter;
   }
}
