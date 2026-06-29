package net.rim.device.api.browser.push;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;

public interface Pushlet {
   void messageReceived(HttpHeaders var1, PushInputStream var2);
}
