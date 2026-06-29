package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class EmailFilterNewVerb extends Verb {
   private EmailFilterCollectionListField _listField;
   private String _userId;
   private static EmailFilterNewVerb _theVerb = new EmailFilterNewVerb();

   private EmailFilterNewVerb() {
      super(629008);
   }

   static final EmailFilterNewVerb getInstance(EmailFilterCollectionListField listField, String userId) {
      _theVerb._listField = listField;
      _theVerb._userId = userId;
      return _theVerb;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(10023);
   }

   @Override
   public final Object invoke(Object context) {
      EmailFilterModelImpl filterModel = null;
      if (!(context instanceof EmailFilterModelImpl)) {
         filterModel = (EmailFilterModelImpl)FactoryUtil.createInstance(-1388842558271364146L, context);
      } else {
         filterModel = (EmailFilterModelImpl)context;
      }

      if (ObjectGroup.isInGroup(filterModel)) {
         filterModel = (EmailFilterModelImpl)ObjectGroup.expandGroup(filterModel);
      }

      filterModel._userId = _theVerb._userId;
      EmailFilterEditScreen filterEditScreen = new EmailFilterEditScreen(AddStoreAction.getInstance(this._listField, this._userId), this._userId);
      filterEditScreen.setModel(filterModel);
      this._listField.setAlerted(false);
      filterEditScreen.go();
      this._listField.setAlerted(true);
      return filterModel;
   }
}
