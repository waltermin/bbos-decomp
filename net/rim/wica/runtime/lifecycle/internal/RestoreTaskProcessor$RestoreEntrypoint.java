package net.rim.wica.runtime.lifecycle.internal;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.wica.runtime.persistence.ApplicationSyncModel;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.ui.HomeScreenEntry;
import net.rim.wica.runtime.ui.HomeScreenUtilities;

class RestoreTaskProcessor$RestoreEntrypoint implements HomeScreenEntry {
   private ApplicationSyncModel _model;
   private static final Integer DEFAULT_POSITION = new Integer(60);
   private static final Bitmap ICON = Bitmap.getBitmapResource("default_icon.png");

   RestoreTaskProcessor$RestoreEntrypoint(ApplicationSyncModel model) {
      this._model = model;
   }

   @Override
   public Integer getEntryDefaultPosition() {
      return DEFAULT_POSITION;
   }

   @Override
   public Bitmap getEntryBitmap() {
      return ICON;
   }

   @Override
   public Bitmap getEntryBitmapFocus() {
      return ICON;
   }

   @Override
   public String getEntryDescription() {
      return this._model.getDescriptor().getName() + " (" + RuntimeResources.getString(118) + ")";
   }

   @Override
   public String getEntryId() {
      return HomeScreenUtilities.createEntryIdentifier(String.valueOf(this._model.getId()));
   }

   @Override
   public void run() {
      Dialog.alert(RuntimeResources.getString(111));
   }
}
