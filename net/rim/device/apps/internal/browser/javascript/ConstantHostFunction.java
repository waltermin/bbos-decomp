package net.rim.device.apps.internal.browser.javascript;

final class ConstantHostFunction extends JavaScriptHostFunction {
   private long _value;

   public ConstantHostFunction(String clazz, String name, long value) {
      super(clazz, name);
      this._value = value;
   }

   @Override
   public final long run() {
      return this._value;
   }
}
