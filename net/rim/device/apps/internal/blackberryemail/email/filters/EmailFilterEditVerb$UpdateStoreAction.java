package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.apps.api.framework.verb.Verb;

final class EmailFilterEditVerb$UpdateStoreAction extends Verb {
   private String _userId;
   private EmailFilterCollectionListField _listField;

   EmailFilterEditVerb$UpdateStoreAction(String userId, EmailFilterCollectionListField listField) {
      super(0);
      this._userId = userId;
      this._listField = listField;
   }

   @Override
   public final Object invoke(Object parameter) {
      this._listField.setAlerted(false);
      if (!(parameter instanceof EmailFilterModelImplClone)) {
         return parameter;
      }

      EmailFilterCollectionImpl filterCollection = EmailFilter.getCollection(this._userId);
      EmailFilterModelImplClone model = (EmailFilterModelImplClone)parameter;
      model._fields.trimToSize();
      EmailFilterModelImplClone clone = (EmailFilterModelImplClone)this._listField.getSelectedElement();
      EmailFilterModelImpl newModel = new EmailFilterModelImpl(model);
      EmailFilterModelImplClone newClone = EmailFilter.clone(newModel);
      newModel._order = model._org._order;
      newModel._enabled = model._org._enabled;
      int index = -1;
      int size = filterCollection.size();

      for (int counter = 0; counter < size; counter++) {
         int uid = ((EmailFilterModelImpl)filterCollection.getAt(counter)).getUID();
         if (uid == newModel.getUID()) {
            index = counter;
            break;
         }
      }

      filterCollection.update(filterCollection.getAt(index), newModel);
      this._listField.update(clone, newClone);
      this._listField.setAlerted(true);
      return parameter;
   }
}
