package net.rim.device.apps.internal.browser.javascript;

final class ESLocation$ToStringHostFunction extends JavaScriptHostFunction {
   private final ESLocation this$0;

   public ESLocation$ToStringHostFunction(ESLocation _1) {
      super(Names.Location, Names.toString);
      this.this$0 = _1;
   }

   @Override
   public final long run() {
      try {
         return this.this$0.getField(Names.href);
      } finally {
         ;
      }
   }
}
