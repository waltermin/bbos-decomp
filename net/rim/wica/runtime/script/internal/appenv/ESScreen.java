package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.Command;
import net.rim.wica.runtime.metadata.component.ui.MenuModel;
import net.rim.wica.runtime.metadata.component.ui.ScreenManager;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.metadata.component.ui.control.TableModel;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.WicaAppContext;

final class ESScreen extends RedirectedObject {
   private String _id;
   private ScreenModel _model;
   private WicaAppContext _context;
   private static final String Menu = "menu";
   private static final String MenuUpperCase = "MENU";
   private static final String DefaultAction = "defaultAction";
   private static final String CurrentFocus = "currentFocus";

   ESScreen(String id, ScreenModel model, WicaAppContext context) {
      super("MDSScreenElement", context.getScreenPrototype());
      this._id = id;
      this._model = model;
      this._context = context;
   }

   @Override
   public final long requestFieldValue(String name) {
      long tmp = this.noRedirectGetField(name);
      if (tmp == Value.UNDEFINED) {
         long uiValue = this.getUIProperty(name);
         return uiValue == Value.DEFAULT ? this.getScreenVariable(name) : uiValue;
      } else {
         return tmp;
      }
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      return this.setUIProperty(name, value) ? this.setScreenVariable(name, value) : false;
   }

   final String getId() {
      return this._id;
   }

   final ScreenManager getScreenManager() {
      return this._context.getScreenManager();
   }

   final ScreenModel getScreenModel() {
      return this._model;
   }

   private final ESObject createElement(UIComponent component) {
      ESObject obj = this._context.getEngine().getCachedObject(component);
      if (obj == null) {
         switch (component.getType()) {
            case 136:
               obj = new ESMenu((MenuModel)component, this._context);
               break;
            case 140:
               obj = new ESTableElement((TableModel)component, this._context);
               break;
            default:
               obj = new ESControl(component, this._context);
         }

         this._context.getEngine().putCachedObject(component, obj);
      }

      return obj;
   }

   private final long getScreenVariable(String name) {
      long result = Value.DEFAULT;
      int index = this._model.getVarIndex(name);
      if (index != -1) {
         long value = this._model.getVarValue(index, true);
         result = this._context.createDataInstance(this._context.getWiclet().getDataCollection((int)(value >>> 32)), value);
      }

      return result;
   }

   private final long getUIProperty(String name) {
      long value = Value.DEFAULT;
      if (name == "menu" || name == "MENU") {
         MenuModel menuModel = this._model.getMenu();
         if (menuModel != null) {
            value = Value.makeObjectValue(this.createElement(menuModel));
            this.addField(name, 28, value);
            return value;
         } else {
            EcmaUtilities.throwESError(this.getId(), RuntimeResources.getString(115));
            return value;
         }
      } else {
         if (name == "currentFocus") {
            return Value.makeObjectValue(this.createElement(this._model.getCurrentFocus()));
         }

         if (name == "defaultAction") {
            Command command = this._model.getDefaultAction();
            return command instanceof UIControl ? Value.makeObjectValue(this.createElement((UIControl)command)) : Value.NULL;
         }

         UIComponent component = this._model.getComponent(name);
         if (component != null) {
            value = Value.makeObjectValue(this.createElement(component));
            this.addField(name, 28, value);
         }

         return value;
      }
   }

   private final boolean setScreenVariable(String name, long value) {
      boolean result = true;
      int index = this._model.getVarIndex(name);
      if (index != -1) {
         Object dataObj = Convert.toObject(value);
         if (!(dataObj instanceof ESData)) {
            throw ThrownValue.typeError(RuntimeResources.getString(121));
         }

         ESData esData = (ESData)dataObj;
         long handle = esData.getHandle();
         if ((int)(handle >>> 32) != this._model.getVarType(index)) {
            throw ThrownValue.typeError(RuntimeResources.getString(120));
         }

         this._model.setVarValue(index, handle);
         result = false;
      }

      return result;
   }

   private final boolean setUIProperty(String name, long value) {
      if (name == "defaultAction") {
         ESObject esObj = Convert.toObject(value);
         Command command = null;
         if (esObj instanceof ESControl) {
            command = (Command)((ESControl)esObj).getModel();
            this._model.setDefaultAction(command);
         }

         return false;
      } else {
         return true;
      }
   }
}
