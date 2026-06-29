package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.ZoomBitmapField;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class SetAsHomescreenBackgroundMenuItem extends MenuItem {
   private FileItemField _item;
   private Field _field;

   public SetAsHomescreenBackgroundMenuItem(FileItemField item, Field field) {
      super(ExplorerResources.getResourceBundleFamily(), 8, 565264, Integer.MAX_VALUE);
      this._field = field;
      this._item = item;
   }

   @Override
   public final void run() {
      String[] params = null;
      if (this._field instanceof Object) {
         Manager mgr = (Manager)this._field;
         if (mgr.getFieldCount() == 1) {
            ZoomBitmapField zoomField = null;
            if (mgr.getField(0) instanceof Object) {
               zoomField = (ZoomBitmapField)mgr.getField(0);
            } else if (mgr.getField(0) instanceof Object) {
               mgr = (Manager)mgr.getField(0);
               if (mgr.getFieldCount() == 1 && mgr.getField(0) instanceof Object) {
                  zoomField = (ZoomBitmapField)mgr.getField(0);
               }
            }

            if (zoomField != null) {
               params = new Object[]{
                  Integer.toString(zoomField.getScale()),
                  Integer.toString(zoomField.getRotationValue()),
                  Integer.toString(zoomField.getImageX()),
                  Integer.toString(zoomField.getImageY())
               };
            }
         }
      }

      RibbonLauncher.getInstance().setBackgroundImage(this._item.getFullPath(), params);
   }
}
