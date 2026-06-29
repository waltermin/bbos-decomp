package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESXMLHttpRequestPrototype extends ESObject {
   public ESXMLHttpRequestPrototype() {
      this.addHostFunction(new OpenHostFunction());
      this.addHostFunction(new SendHostFunction());
      this.addHostFunction(new SetRequestHeaderHostFunction());
      this.addHostFunction(new GetAllRequestHeadersHostFunction());
      this.addHostFunction(new GetResponseHeaderHostFunction());
      this.addHostFunction(new AbortHostFunction());
   }
}
