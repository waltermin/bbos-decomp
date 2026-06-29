package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.internal.io.file.FileUtilities;

public final class ExploreScreen extends AppsMainScreen implements ExploreCallback {
   private FooterField _pathSizeField;
   private LabelField _pathField;
   private ExploreManager _manager;
   private VerticalFieldManager _vfm;
   private boolean _statusOn;

   public ExploreScreen(Object ctx) {
      super(562949953421312L);
      ContextObject context = ContextObject.castOrCreate(ctx);
      context.setFlag(45);
      this.setContext(context);
      this._pathField = (LabelField)(new Object(null, 1152921504606847168L));
      this._pathSizeField = new FooterField();
      this._vfm = (VerticalFieldManager)(new Object());
      this._vfm.add(this._pathField);
      this.setTitle(this._vfm);
      this._pathField.setEditable(false);
      this._manager = new ExploreManager(this, ctx, false, 3459063580983296000L);
      this.addFocusChangeListener(this._manager);
      this.add(this._manager);
      this.setHelp(32247);
   }

   @Override
   protected final void makeMenu(Menu contextMenu, int instance) {
      super.makeMenu(contextMenu, instance);
      Field field = this.getLeafFieldWithFocus();
      if (field == this._pathField) {
         this._manager.makeMenuForPath(contextMenu, instance);
      }
   }

   @Override
   public final void pathSet(Object path) {
      this._pathField.setText(FileUtilities.getDisplayName(path.toString()));
   }

   @Override
   public final void statusOn() {
      if (!this._statusOn) {
         this._vfm.add(this._pathSizeField);
         this._statusOn = true;
      }
   }

   @Override
   public final void statusOff() {
      if (this._statusOn) {
         this._vfm.delete(this._pathSizeField);
         this._statusOn = false;
      }
   }

   @Override
   public final void currentItemChanged(Field field, FileItemField item) {
      this._pathSizeField.setItem(item);
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      FileSelectionFilter filter = this._manager.getFilter();
      switch (backdoorCode) {
         case 1212761157:
            filter.setHideFiltered(true);
            break;
         case 1397247831:
            filter.setHideFiltered(false);
            break;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }

      this._manager.refreshView();
      return true;
   }

   @Override
   public final boolean onClose() {
      if (super.onClose()) {
         this._manager.cleanup();
         return true;
      } else {
         return false;
      }
   }
}
