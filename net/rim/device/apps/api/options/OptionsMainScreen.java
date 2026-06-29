package net.rim.device.apps.api.options;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

public class OptionsMainScreen extends AppsMainScreen {
   private MainScreenOptionsListItem _optionsListItem;

   public OptionsMainScreen(MainScreenOptionsListItem optionsListItem, long style) {
      super(style);
      this._optionsListItem = optionsListItem;
      this.setDefaultClose(false);
   }

   @Override
   protected ContextObject getContext() {
      if (this._optionsListItem != null) {
         Object context = this._optionsListItem.getContext();
         if (context instanceof Object) {
            return (ContextObject)context;
         }
      }

      return null;
   }

   @Override
   protected boolean handleEndKey() {
      return this._optionsListItem != null ? this._optionsListItem.handleEndKey() : false;
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled && this._optionsListItem != null) {
         handled = this._optionsListItem.invokeOptionsAction(action);
      }

      return handled;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._optionsListItem != null) {
         this._optionsListItem.makeOptionsMenu(menu, instance);
      }
   }
}
