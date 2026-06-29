package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.RedirectedObject;

class ESHTMLElement$StyleObject extends RedirectedObject {
   private final ESHTMLElement this$0;

   public ESHTMLElement$StyleObject(ESHTMLElement _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      if (name == Names.display) {
         try {
            String displayMode = Convert.toString(value);
            Application.getApplication().invokeAndWait(new ESHTMLElement$StyleObject$1(this, displayMode));
            return false;
         } finally {
            ;
         }
      } else if (name == Names.visibility) {
         try {
            String visibilityMode = Convert.toString(value);
            Application.getApplication().invokeAndWait(new ESHTMLElement$StyleObject$2(this, visibilityMode));
            return false;
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }
}
