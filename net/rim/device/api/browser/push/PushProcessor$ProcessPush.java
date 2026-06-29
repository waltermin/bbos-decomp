package net.rim.device.api.browser.push;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;

class PushProcessor$ProcessPush implements Runnable {
   private Pushlet _listener;
   private PushInputStream _in;
   private HttpHeaders _headers;

   public PushProcessor$ProcessPush(Pushlet listener, HttpHeaders headers, PushInputStream in) {
      this._headers = headers;
      this._in = in;
      this._listener = listener;
   }

   @Override
   public void run() {
      this._listener.messageReceived(this._headers, this._in);
   }
}
