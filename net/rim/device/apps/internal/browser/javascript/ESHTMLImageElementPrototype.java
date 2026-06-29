package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLImageElementPrototype extends ESObject {
   ESHTMLImageElementPrototype(ESElementPrototype nodeProto) {
      this.setPrototype(nodeProto);
   }
}
