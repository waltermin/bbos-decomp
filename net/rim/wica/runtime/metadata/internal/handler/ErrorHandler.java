package net.rim.wica.runtime.metadata.internal.handler;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.REError;
import net.rim.wica.runtime.metadata.component.REErrorDetails;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.ScriptEngine;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.ui.UiService;

public class ErrorHandler implements Serviceable, EventListener {
   private WicletRuntime _runtime;
   private ScriptEngine _scriptEngine;
   private UiService _uiService;
   private int _errorScript;
   private static final int RE_ERROR_CLASS_CONTINUE;
   private static final int RE_ERROR_CLASS_SHUTDOWN;
   private static final int RE_ERROR_CLASS_SECURITY_VIOLATION;
   private static final int RE_ERROR_CLASS_PERSISTENT_CONTENT;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$ui$UiService;
   static Class class$net$rim$wica$runtime$script$ScriptEngine;

   public void handleError(REError error) {
      this._runtime.enqueueRunnable(new ErrorHandler$1(this, error));
   }

   @Override
   public void handleEvent(Object sender, int event, int eventParam, Object data) {
      if (event == 605) {
         if (data instanceof REError) {
            this.handleError((REError)data);
            return;
         }

         String description = null;
         switch (eventParam) {
            case 99:
               description = RuntimeResources.getString(51);
               break;
            case 100:
            default:
               description = RuntimeResources.getString(26);
               break;
            case 101:
               description = RuntimeResources.getString(29);
               break;
            case 102:
               description = RuntimeResources.getString(30);
               break;
            case 103:
               description = RuntimeResources.getString(31);
               break;
            case 104:
               description = RuntimeResources.getString(32);
               break;
            case 105:
               description = RuntimeResources.getString(33);
               break;
            case 106:
               description = RuntimeResources.getString(61);
               break;
            case 107:
               description = RuntimeResources.getString(153);
         }

         this.handleError(new REError(eventParam, description, new REErrorDetails("", 0, "REError", data)));
      }
   }

   @Override
   public void setServices(ServiceProvider provider) {
      this._runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      eventService.addListener(605, this);
      this._uiService = (UiService)provider.getService(
         class$net$rim$wica$runtime$ui$UiService == null
            ? (class$net$rim$wica$runtime$ui$UiService = class$("net.rim.wica.runtime.ui.UiService"))
            : class$net$rim$wica$runtime$ui$UiService
      );
      this._errorScript = this._runtime.getWiclet().getDefHandle("onError");
      if (this._errorScript != -1) {
         this._scriptEngine = (ScriptEngine)provider.getService(
            class$net$rim$wica$runtime$script$ScriptEngine == null
               ? (class$net$rim$wica$runtime$script$ScriptEngine = class$("net.rim.wica.runtime.script.ScriptEngine"))
               : class$net$rim$wica$runtime$script$ScriptEngine
         );
      }
   }

   private static int getErrorClass(REError err) {
      switch (err.getCode()) {
         case 99:
         case 102:
         case 104:
         case 105:
            return 2;
         case 100:
         case 101:
         case 103:
         default:
            return 3;
         case 106:
            return 4;
         case 107:
            return 5;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
