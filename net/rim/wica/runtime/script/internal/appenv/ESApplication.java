package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.script.internal.ScriptEngineInternal;
import net.rim.wica.runtime.script.internal.WicaAppContext;
import net.rim.wica.runtime.script.internal.handler.FieldHandlerFactory;
import net.rim.wica.runtime.script.internal.handler.PropertyHandler;

public final class ESApplication extends RedirectedObject {
   private WicaAppContext _context;
   private Wiclet _application;
   private ScriptEngineInternal _engine;
   private Data _globals;
   private static final String Platform = "platform";

   public ESApplication(WicaAppContext context, Data globals) {
      super("Application", GlobalObject.getInstance().getObjectPrototype());
      this._context = context;
      this._application = context.getWiclet();
      this._engine = context.getEngine();
      this._globals = globals;
      this.setGrowthIncrement(5);
      this.addHostFunction(new ESApplication$GetStringBundleFunction(this));
      this.addHostFunction(new ESApplication$SetTimeoutFunction(this));
      this.addHostFunction(new ESApplication$SetIntervalFunction(this));
      this.addHostFunction(new ESApplication$ClearTimeoutFunction(this));
      this.addHostFunction(new ESApplication$ClearIntervalFunction(this));
      this.addFieldSpecial(this._engine.getGlobalObject(), "application", 5, Value.makeObjectValue(this));
   }

   public final void dispose() {
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      if (this._application.isGlobalVar(name)) {
         this.getGlobalHandler(name).putProperty(this._globals, name, value);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public final long requestFieldValue(String name) {
      long tmp = this.noRedirectGetField(name);
      if (tmp != Value.UNDEFINED) {
         return tmp;
      }

      long value = Value.DEFAULT;
      if (this._application.isGlobalVar(name)) {
         PropertyHandler handler = this.getGlobalHandler(name);
         value = handler.getProperty(this._globals, name);
      } else {
         int handle = this._application.getDefHandle(name);
         if (handle == -1) {
            return value;
         }

         ESObject esObj = null;
         int type = this._application.getDefType(handle);
         switch (type) {
            case 4:
            case 7:
            case 8:
               break;
            case 5:
               esObj = this._context.createEnumeration(name, handle);
               break;
            case 6:
            default:
               esObj = new ESCollection(name, this._application.getDataCollection(handle), this._context.getCollectionPrototype());
               break;
            case 9:
               esObj = new ESMessage(name, this._application.getMsg(handle), this._context.getMessagePrototype());
               break;
            case 10:
               esObj = new ESScreen(name, this._application.getScreenModel(handle), this._context);
         }

         if (esObj != null) {
            value = Value.makeObjectValue(esObj);
            this.addGlobalField(this._engine.getGlobalObject(), name, 29, value);
         }
      }

      return value;
   }

   private final void addFieldSpecial(GlobalObject global, String name, int attributes, long value) {
      this.addField(name, attributes, value);
      this.addGlobalField(global, name, attributes, value);
   }

   private final void addGlobalField(GlobalObject global, String name, int attributes, long value) {
      global.addField(name, attributes, value);
   }

   private final PropertyHandler getGlobalHandler(String name) {
      return FieldHandlerFactory.getHandler(this._globals.getDef().getFieldType(this._globals.getDef().getFieldHandle(name)));
   }
}
