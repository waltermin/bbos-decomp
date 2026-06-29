package net.rim.device.internal.browser.wap;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;

public interface IWAPPushProvider {
   void pushReceived(HttpHeaders var1, PushInputStream var2);

   boolean pushEnabled();
}
