package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;

class InputHandlingScreen extends AppsMainScreen implements Confirmation {
   private Verb _saveVerb;
   protected Verb _closeVerb = (Verb)(new Object(0, this));
   protected ContextObject _context;
   protected Object _originalSource;
   private Field _title;

   InputHandlingScreen(ContextObject context) {
      super(0);
      this._context = context;
   }

   protected void setSaveVerb(Profile profile) {
      this._saveVerb = new InputHandlingScreen$SaveVerb(this, profile);
   }

   protected void setTitle(RIMModel model, Object context) {
      if (context == null) {
         context = this._context;
      }

      Field field = null;
      if (model instanceof Object) {
         field = ((FieldProvider)model).getField(context);
      }

      if (field != null) {
         this.setTitle(field);
         this._title = field;
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._saveVerb != null) {
         menu.add(this._saveVerb);
         if (this.isMuddy()) {
            menu.setDefault(this._saveVerb);
         }
      }
   }

   @Override
   protected boolean keyChar(char keyChar, int status, int time) {
      if (keyChar == 27) {
         this._closeVerb.invoke(this._context);
         return true;
      } else {
         return super.keyChar(keyChar, status, time);
      }
   }

   @Override
   public boolean confirm(Verb aVerb, Object contextObject) {
      boolean result = true;
      if (this.isDirty() && this._saveVerb != null) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case -1:
               result = false;
               break;
            case 1:
               this._saveVerb.invoke(contextObject);
               return false;
         }
      }

      return result;
   }

   private boolean proceedWithSave() {
      try {
         if (this._originalSource == null) {
            return true;
         }

         NotificationsManager.getSourceId(this._originalSource);
         return true;
      } finally {
         ResourceBundle profileResources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         Status.show(profileResources.getString(254));
         return false;
      }
   }

   private void save(Profile profile, Object context) {
      if (this.proceedWithSave()) {
         boolean result = this.saveField(this._title);

         for (int i = this.getFieldCount() - 1; i >= 0; i--) {
            result &= this.saveField(this.getField(i));
         }

         if (context != profile && result) {
            Profiles.getInstance().commitChanges(profile, true);
            UiApplication.getUiApplication().popScreen(this);
            return;
         }
      } else {
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   private boolean saveField(Field field) {
      if (field == null) {
         return true;
      }

      Object cookie = field.getCookie();
      return !(cookie instanceof Object) ? true : ((FieldProvider)cookie).grabDataFromField(field, this._context);
   }
}
