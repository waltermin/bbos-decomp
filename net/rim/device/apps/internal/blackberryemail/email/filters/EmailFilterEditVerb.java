package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class EmailFilterEditVerb extends Verb {
   private EmailFilterModelImplClone _record;
   private String _userId;
   private EmailFilterCollectionListField _listField;
   private static EmailFilterEditVerb _theVerb = new EmailFilterEditVerb();

   @Override
   public final String toString() {
      return CommonResource.getString(16);
   }

   private EmailFilterEditVerb() {
      super(629012);
   }

   static final EmailFilterEditVerb getInstance(EmailFilterModelImplClone record, String userId, EmailFilterCollectionListField listField) {
      _theVerb._record = record;
      _theVerb._userId = userId;
      _theVerb._listField = listField;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object context) {
      EmailFilterEditScreen filterEditScreen = new EmailFilterEditScreen(new EmailFilterEditVerb$UpdateStoreAction(this._userId, this._listField), this._userId);
      if (ObjectGroup.isInGroup(this._record)) {
         this._record = (EmailFilterModelImplClone)ObjectGroup.expandGroup(this._record);
      }

      this._listField.setAlerted(false);
      EmailFilterModelImplClone clone = EmailFilter.clone(this._record);
      filterEditScreen.setModel(clone);
      filterEditScreen.go();
      this._listField.setAlerted(true);
      ContextObject invokeContextObject = new ContextObject(39);
      invokeContextObject.setFlag(40);
      return invokeContextObject;
   }
}
