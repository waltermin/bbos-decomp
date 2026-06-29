package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.util.StringUtilities;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESNavigator$1 extends JavaScriptHostFunction {
   private final RenderingOptions val$renderingOptions;

   ESNavigator$1(String x0, String x1, int x2, RenderingOptions _4) {
      super(x0, x1, x2);
      this.val$renderingOptions = _4;
   }

   @Override
   public long run() {
      try {
         int count = this.getNumParms();
         if (count >= 1) {
            String str = Convert.toString(this.getParm(0));
            if (StringUtilities.strEqualIgnoreCase(str, "general.always_load_images", 1701707776)) {
               return Value.makeBooleanValue(this.val$renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, true));
            }

            if (StringUtilities.strEqualIgnoreCase(str, "security.enable_java", 1701707776)) {
               return Value.makeBooleanValue(true);
            }

            if (StringUtilities.strEqualIgnoreCase(str, "javascript.enabled", 1701707776)) {
               return Value.makeBooleanValue(this.val$renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false));
            }

            if (StringUtilities.strEqualIgnoreCase(str, "browser.enable_style_sheets", 1701707776)) {
               return Value.makeBooleanValue(this.val$renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 18, true));
            }

            if (StringUtilities.strEqualIgnoreCase(str, "autoupdate.enabled", 1701707776)) {
               return Value.makeBooleanValue(false);
            }

            if (StringUtilities.strEqualIgnoreCase(str, "network.cookie.cookieBehavior", 1701707776)) {
               return Value.makeIntegerValue(0);
            }

            if (StringUtilities.strEqualIgnoreCase(str, "network.cookie.warnAboutCookies", 1701707776)) {
               return Value.makeBooleanValue(true);
            }
         }
      } finally {
         return Value.NULL;
      }

      return Value.NULL;
   }
}
