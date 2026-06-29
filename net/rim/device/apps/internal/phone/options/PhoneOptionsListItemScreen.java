package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.options.OptionsMainScreen;

class PhoneOptionsListItemScreen extends OptionsMainScreen implements GlobalEventListener {
   private Application _app = Application.getApplication();
   private MainScreenOptionsListItem _optionsItem;

   PhoneOptionsListItemScreen(MainScreenOptionsListItem optionsItem) {
      super(optionsItem, 0);
      this._optionsItem = optionsItem;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Application.getApplication().addGlobalEventListener(this);
      } else {
         Application.getApplication().removeGlobalEventListener(this);
      }
   }

   protected void optionsUpdated() {
      this.deleteAll();
      if (this._optionsItem instanceof VoiceOptionsListItem) {
         ((VoiceOptionsListItem)this._optionsItem).optionsUpdated();
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid == -2282475915901395486L || guid == -3666745774872801074L) && this._app != null) {
         this._app.invokeLater(new PhoneOptionsListItemScreen$1(this));
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      if (this.getLeafFieldWithFocus() instanceof Object) {
         switch (action) {
            case 1:
               return true;
         }
      }

      return super.invokeAction(action);
   }
}
