package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class StorageModeOptionsItem extends OptionsListItem {
   private String[] _questions;

   public StorageModeOptionsItem() {
      super(OptionsResources.getString(200));
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (this._questions == null) {
         this._questions = new Object[]{
            OptionsResources.getString(201), OptionsResources.getString(202), OptionsResources.getString(203), OptionsResources.getString(204)
         };
      }
   }

   @Override
   protected final void open() {
      UiApplication.getUiApplication().pushScreen((Screen)(new Object()));
      UiApplication.getUiApplication().invokeLater(new StorageModeOptionsItem$ConfirmAndShutdownRunnable(this));
   }
}
