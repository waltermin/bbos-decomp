package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.WeakReference;

final class EmailFilterEditScreen extends EditorUsingRIMModelFactory implements CollectionListener {
   Verb _saveVerb;
   Verb _storeAction;
   private String _userId;
   private EmailFilterCollectionImpl _emailFilterCollection;
   private WeakReference _weakReference;

   EmailFilterEditScreen(Verb storeAction, String userId) {
      super(new Object(0, 33), null, EmailFilter.getCollectionId(), 10000);
      this._storeAction = storeAction;
      this._saveVerb = new EmailFilterEditScreen$EmailFilterSaveVerb(this);
      this._weakReference = (WeakReference)(new Object(this));
      this._userId = userId;
      this._emailFilterCollection = EmailFilterCollectionImpl.getInstance(this._userId);
      this._emailFilterCollection.addCollectionListener(this._weakReference);
   }

   @Override
   public final void setModel(Object model) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void reset(Collection collection) {
      this.exitLater();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.exitLater();
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.exitLater();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (!this.isDisplayed()) {
         this._emailFilterCollection.removeCollectionListener(this._weakReference);
      } else {
         if (oldElement instanceof EmailFilterModelImpl) {
            EmailFilterModelImpl model = (EmailFilterModelImpl)this.getModel();
            EmailFilterModelImpl changedModel = (EmailFilterModelImpl)oldElement;
            if (model.getUID() == changedModel.getUID()) {
               this.exitLater();
            }
         }
      }
   }

   private final void exitLater() {
      this._emailFilterCollection.removeCollectionListener(this._weakReference);
      if (this.isDisplayed()) {
         this.getApplication().invokeLater(new EmailFilterEditScreen$1(this));
      }
   }

   private final void forceExit() {
      Dialog.alert(EmailResources.getString(1005));
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._saveVerb);
      if (menu.getDefaultVerb() == null) {
         if (this.isDirty()) {
            menu.setDefault(this._saveVerb);
            return;
         }

         menu.setDefault(super._leaveScreenVerb);
      }
   }

   @Override
   public final boolean onSave() {
      this._saveVerb.invoke(null);
      return false;
   }

   static final int access$100(EmailFilterEditScreen x0) {
      return x0.validateFields();
   }
}
