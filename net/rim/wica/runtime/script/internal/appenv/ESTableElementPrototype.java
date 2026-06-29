package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.wica.runtime.script.internal.WicaAppContext;

public final class ESTableElementPrototype extends ESObject {
   public ESTableElementPrototype(WicaAppContext context) {
      this.setPrototype(context.getControlPrototype());
   }
}
