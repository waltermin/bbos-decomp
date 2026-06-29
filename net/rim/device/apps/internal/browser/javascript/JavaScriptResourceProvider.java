package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.RequestedResource;

public interface JavaScriptResourceProvider {
   void requestResourceFromJavascript(RequestedResource var1, JavaScriptResourceCallback var2);

   void cancelResourceFromJavascript(RequestedResource var1);
}
