package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.itpolicy.ITPolicy;

public final class JavaScriptRegistry {
   public static boolean _sendQuincy;
   public static boolean _debugMode;
   public static boolean _ajaxed;

   private JavaScriptRegistry() {
   }

   public static final JavaScriptInterpreter getInstance() {
      return new JavaScriptEngine();
   }

   public static final boolean isInstalled() {
      return true;
   }

   public static final String[] getMimeTypes(boolean fullList) {
      if (!ITPolicy.getBoolean(30, 2, false)) {
         String[] types = _debugMode ? JavaScriptEngine.CORE_MIME_TYPES_DEBUG : JavaScriptEngine.CORE_MIME_TYPES;
         if (fullList) {
            String[] fullTypes = new Object[types.length + 2];
            System.arraycopy(types, 0, fullTypes, 0, types.length);
            fullTypes[types.length] = "text/javascript";
            fullTypes[types.length + 1] = "text/ecmascript";
            return fullTypes;
         } else {
            return types;
         }
      } else {
         return null;
      }
   }
}
