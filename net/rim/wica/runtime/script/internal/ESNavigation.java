package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.RedirectedObject;

final class ESNavigation extends RedirectedObject {
   private WicaAppContext _context;
   private static final String ActiveScreen = "activeScreen";

   ESNavigation(WicaAppContext context) {
      this._context = context;
      this.setGrowthIncrement(2);
      this.addHostFunction(new ESNavigation$1(this, "Screen", "close", 1));
      this.addHostFunction(new ESNavigation$2(this, "Screen", "refresh", 0));
   }
}
