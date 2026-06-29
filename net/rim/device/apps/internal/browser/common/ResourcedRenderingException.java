package net.rim.device.apps.internal.browser.common;

import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public class ResourcedRenderingException extends RenderingException {
   private int _resourceId = -1;

   public ResourcedRenderingException() {
   }

   public ResourcedRenderingException(String str) {
      super(str);
   }

   public ResourcedRenderingException(int resourceId, String additionalString) {
      super(additionalString);
      this._resourceId = resourceId;
   }

   @Override
   public String getMessage() {
      String superMessage = super.getMessage();
      if (this._resourceId != -1) {
         String resourceStr = BrowserResources.getString(this._resourceId);
         return superMessage != null ? ((StringBuffer)(new Object())).append(resourceStr).append(superMessage).toString() : resourceStr;
      } else {
         return superMessage;
      }
   }

   public int getResourceId() {
      return this._resourceId;
   }
}
