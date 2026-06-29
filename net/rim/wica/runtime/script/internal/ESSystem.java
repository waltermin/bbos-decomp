package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;

public final class ESSystem extends ESObject {
   private WicaAppContext _context;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   public ESSystem(WicaAppContext context) {
      super("System", GlobalObject.getInstance().getObjectPrototype());
      this._context = context;
      this.setGrowthIncrement(6);
      this.addHostFunction(new ESSystem$1(this, "System", "sleep", 1));
      this.addHostFunction(new ESSystem$2(this, "System", "validateRuntimeCompatibility", 1));
      this.addHostFunction(new ESSystem$3(this, "System", "exit", 0));
      this.addHostFunction(new ESSystem$4(this, "System", "exec", 1));
      this.addHostFunction(new ESSystem$5(this, "System", "startCall", 1));
      this.addHostFunction(new ESSystem$6(this, "System", "play", 1));
      this.addHostFunction(new ESSystem$7(this, "System", "loadURL", 1));
      this.addHostFunction(new ESSystem$8(this, "System", "getSP", 0));
      this.addHostFunction(new ESSystem$9(this, "System", "getProperty", 1));
      this.addHostFunction(new ESSystem$10(this, "System", "setProperty", 2));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
