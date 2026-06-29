package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.HostFunction;

final class ESWindowPrototype extends ESObject {
   private HostFunction[] _hostFunctions = new HostFunction[35];

   public ESWindowPrototype() {
      this.setGrowthIncrement(35);
      int offset = 0;
      this.appendHostFunction(offset++, new ESWindowPrototype$1(this, Names.Window, "alert", 1));
      this.appendHostFunction(offset++, new ESWindowPrototype$2(this, Names.Window, "atob", 1));
      this.appendHostFunction(offset++, new ESWindowPrototype$3(this, Names.Window, "back", 0));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "blur"));
      this.appendHostFunction(offset++, new ESWindowPrototype$4(this, Names.Window, "btoa", 1));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "captureEvents"));
      this.appendHostFunction(offset++, new ESWindowPrototype$5(this, Names.Window, "clearInterval", 1));
      this.appendHostFunction(offset++, new ESWindowPrototype$6(this, Names.Window, "clearTimeout", 1));
      this.appendHostFunction(offset++, new ESWindowPrototype$7(this, Names.Window, "close", 0));
      this.appendHostFunction(offset++, new ESWindowPrototype$8(this, Names.Window, "confirm", 1));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "disableExternalCaptureEvents"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "enableExternalCaptureEvents"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "find"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "focus"));
      this.appendHostFunction(offset++, new ESWindowPrototype$9(this, Names.Window, "forward", 0));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "handleEvent"));
      this.appendHostFunction(offset++, new ESWindowPrototype$10(this, Names.Window, "home", 0));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "moveBy"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "moveTo"));
      this.appendHostFunction(offset++, new ESWindowPrototype$11(this, Names.Window, "open", 3));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "print"));
      this.appendHostFunction(offset++, new ESWindowPrototype$12(this, Names.Window, "prompt", 1));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "releaseEvents"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "resizeBy"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "resizeTo"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "routeEvent"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "scroll"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "scrollBy"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "scrollTo"));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "setHotKeys"));
      this.appendHostFunction(offset++, new ESWindowPrototype$13(this, Names.Window, "setInterval", 2));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "setResizable"));
      this.appendHostFunction(offset++, new ESWindowPrototype$14(this, Names.Window, "setTimeout", 2));
      this.appendHostFunction(offset++, new NoopHostFunction(Names.Window, "setZOptions"));
      this.appendHostFunction(offset++, new ESWindowPrototype$15(this, Names.Window, "stop", 0));
   }

   final HostFunction[] getHostFunctions() {
      return this._hostFunctions;
   }

   private final void appendHostFunction(int index, HostFunction hf) {
      this._hostFunctions[index] = hf;
      this.addHostFunction(hf);
   }
}
