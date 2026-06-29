package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.i18n.CommonResource;

final class EmailFilterEditScreen$EmailFilterSaveVerb extends Verb {
   private final EmailFilterEditScreen this$0;

   public EmailFilterEditScreen$EmailFilterSaveVerb(EmailFilterEditScreen _1) {
      super(629016);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(18);
   }

   @Override
   public final Object invoke(Object context) {
      int id = EmailFilterEditScreen.access$100(this.this$0);
      if (id != -1) {
         if (id == 0) {
            Status.show(EmailResources.getString(182));
            return null;
         } else {
            Status.show(EmailResources.getString(1006));
            return null;
         }
      } else {
         EmailFilterCollectionImpl.getInstance(this.this$0._userId).removeCollectionListener(this.this$0._weakReference);
         EmailFilterModelImpl model = (EmailFilterModelImpl)this.this$0.getModel();
         if (this.duplicateFilterNamesFound(model)) {
            Status.show(EmailResources.getString(113));
            return null;
         } else {
            this.this$0._storeAction.invoke(model);
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            return model;
         }
      }
   }

   private final boolean duplicateFilterNamesFound(EmailFilterModelImpl model) {
      String title = model.getName();
      int uid = model.getUID();
      int size = this.this$0._emailFilterCollection.getSize(null);
      if (title == null) {
         return false;
      }

      for (int i = 0; i < size; i++) {
         Object obj = this.this$0._emailFilterCollection.getAt(i);
         if (obj instanceof EmailFilterModelImpl) {
            EmailFilterModelImpl filterModel = (EmailFilterModelImpl)obj;
            if (filterModel.getUID() != uid && title.equalsIgnoreCase(filterModel.getName())) {
               return true;
            }
         }
      }

      return false;
   }
}
