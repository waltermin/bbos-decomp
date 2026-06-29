package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLOptionElementPrototype extends ESObject {
   public ESHTMLOptionElementPrototype(ESElementPrototype nodeProto) {
      this.setPrototype(nodeProto);
   }
}
