package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESTextPrototype extends ESObject {
   ESTextPrototype(ESNodePrototype nodeProto) {
      this.setPrototype(nodeProto);
      this.addHostFunction(new ESTextPrototype$1(this, Names.Input, "splitText", 1));
   }
}
