package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingModelImpl;
import net.rim.device.internal.i18n.CommonResource;

final class EmailFilterList$EmailFilterSaveVerb extends Verb {
   boolean _closeScreen;
   private final EmailFilterList this$0;

   public EmailFilterList$EmailFilterSaveVerb(EmailFilterList _1) {
      super(332288);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(18);
   }

   public final void setCloseScreen(boolean closeScreen) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object parameter) {
      for (int i = this.this$0._service.getSize() - 1; i >= 0; i--) {
         EmailFilterList$ServiceInfo info = (EmailFilterList$ServiceInfo)this.this$0._service.getChoice(i);
         if (info != null && info._listField != null) {
            info._listField.setAlerted(false);
         }
      }

      for (int var6 = this.this$0._service.getSize() - 1; var6 >= 0; var6--) {
         EmailFilterList$ServiceInfo info = (EmailFilterList$ServiceInfo)this.this$0._service.getChoice(var6);
         if (info != null && info.isValid()) {
            EmailSettingModelImpl cachedModel = (EmailSettingModelImpl)info._forward.getCookie();
            if (info._forward.getSelectedIndex() != cachedModel.getFilterDefaultAction()) {
               EmailSettingModelImpl model = new EmailSettingModelImpl(cachedModel);
               model.setFilterDefaultAction(info._forward.getSelectedIndex());
               model.setId(info._userId);
               model.updateConfig();
            }

            info._listField.saveChanges();
            this.this$0._mainScreen.setDirty(false);
            EmailFilterCollectionImpl.getInstance(info._userId).removeCollectionListener(this.this$0._weakReference);
         }
      }

      if (this._closeScreen) {
         ExitVerb.createCloseVerb(0, null).invoke(null);
      }

      return null;
   }
}
