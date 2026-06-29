package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.TableColumnModel;
import net.rim.wica.runtime.metadata.component.ui.control.TableModel;
import net.rim.wica.runtime.script.internal.WicaAppContext;

final class ESTableElement extends ESControl {
   private static final String ColumnsProperty;
   private static final String SelectedColumnIndexProperty;
   private static final String SelectedRowIndexProperty;

   ESTableElement(TableModel model, WicaAppContext context) {
      super("TableElement", model, context, context.getTablePrototype());
   }

   @Override
   public final long requestFieldValue(String name) {
      TableModel model = (TableModel)this.getModel();
      if (name == "selectedRowIndex") {
         return Value.makeIntegerValue(model.getSelectedRowIndex());
      }

      if (name == "selectedColumnIndex") {
         return Value.makeIntegerValue(model.getSelectedColumnIndex());
      }

      if (name == "columns") {
         return Value.makeObjectValue(new ESColumnCollection(model));
      }

      UIComponent columnModel = model.getComponent(name);
      return columnModel instanceof TableColumnModel ? Value.makeObjectValue(new ESColumn((TableColumnModel)columnModel)) : super.requestFieldValue(name);
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      TableModel model = (TableModel)this.getModel();
      if (name == "selectedRowIndex") {
         model.setSelectedRowIndex(Convert.toInt32(value), false);
         return false;
      } else if (name == "selectedColumnIndex") {
         model.setSelectedColumnIndex(Convert.toInt32(value), false);
         return false;
      } else {
         return super.notifyFieldChanged(name, value);
      }
   }
}
