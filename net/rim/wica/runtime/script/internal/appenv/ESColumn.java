package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.control.TableColumnModel;

final class ESColumn extends RedirectedObject {
   private TableColumnModel _model;
   private static final String TitleProperty;
   private static final String FrozenProperty;

   ESColumn(TableColumnModel model) {
      this._model = model;
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == "visible") {
         return Value.makeBooleanValue(this._model.isVisible());
      } else if (name == "title") {
         return Value.makeStringValue(this._model.getTitle());
      } else {
         return name == "frozen" ? Value.makeBooleanValue(this._model.isFrozen()) : Value.DEFAULT;
      }
   }

   @Override
   public final long requestElementValue(long element) {
      return Value.makeStringValue(this._model.getCell(Convert.toInt32(element)));
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      if (name == "visible") {
         this._model.setVisible(Convert.toBoolean(value));
         return false;
      } else if (name == "title") {
         this._model.setTitle(Convert.toString(value));
         return false;
      } else if (name == "frozen") {
         this._model.setFrozen(Convert.toBoolean(value), false);
         return false;
      } else {
         return true;
      }
   }
}
