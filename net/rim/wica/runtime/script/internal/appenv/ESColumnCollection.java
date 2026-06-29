package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.TableColumnModel;
import net.rim.wica.runtime.metadata.component.ui.control.TableModel;

final class ESColumnCollection extends RedirectedObject {
   private TableModel _model;

   ESColumnCollection(TableModel model) {
      this._model = model;
   }

   @Override
   public final long requestElementValue(long element) {
      UIComponent[] columns = this._model.getChildren();
      if (columns != null) {
         TableColumnModel column = (TableColumnModel)columns[Convert.toInt32(element)];
         return Value.makeObjectValue(new ESColumn(column));
      } else {
         return Value.DEFAULT;
      }
   }
}
