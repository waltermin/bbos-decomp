package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.MenuItemModel;
import net.rim.wica.runtime.metadata.component.ui.MenuModel;
import net.rim.wica.runtime.script.internal.WicaAppContext;

final class ESMenu extends RedirectedObject {
   private MenuModel _model;
   private WicaAppContext _context;

   ESMenu(MenuModel model, WicaAppContext context) {
      super("MDSMenuElement", context.getMenuPrototype());
      this._context = context;
      this._model = model;
   }

   @Override
   public final long requestFieldValue(String name) {
      long tmp = this.noRedirectGetField(name);
      if (tmp == Value.UNDEFINED) {
         long value = Value.DEFAULT;
         MenuItemModel menuItem = this._model.getMenuItem(name);
         if (menuItem != null) {
            value = Value.makeObjectValue(new ESMenuItem(menuItem, this._context));
            this.addField(name, 28, value);
         }

         return value;
      } else {
         return tmp;
      }
   }
}
