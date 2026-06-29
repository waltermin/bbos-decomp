package net.rim.device.api.browser.field;

public class ExecutingScriptEvent extends Event {
   private int _type;
   public static final int TYPE_WMLSCRIPT = 0;
   public static final int TYPE_JAVASCRIPT = 1;

   public ExecutingScriptEvent(Object src, int type) {
      super(10003, src);
      this._type = type;
   }

   public int getType() {
      return this._type;
   }
}
