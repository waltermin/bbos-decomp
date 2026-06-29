package net.rim.device.api.browser.field;

import javax.microedition.io.InputConnection;

public interface ResourceProvider {
   InputConnection getInputConnection(RequestedResource var1, BrowserContent var2);
}
