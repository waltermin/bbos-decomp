package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.MenuItemModel;
import net.rim.wica.runtime.script.internal.WicaAppContext;

final class ESMenuItem extends RedirectedObject {
   private MenuItemModel _model;
   private static final String VisibleProperty;

   ESMenuItem(MenuItemModel model, WicaAppContext context) {
      super("MDSMenuItemElement", context.getMenuItemPrototype());
      this._model = model;
   }

   @Override
   public final long requestFieldValue(String name) {
      long objId = Value.DEFAULT;
      if (name == "visible") {
         objId = Value.makeBooleanValue(this._model.isVisible());
      }

      return objId;
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      if (name == "visible") {
         this._model.setVisible(Convert.toBoolean(value));
         return false;
      } else {
         return true;
      }
   }
}
