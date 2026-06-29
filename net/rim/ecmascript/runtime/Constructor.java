package net.rim.ecmascript.runtime;

public class Constructor extends HostFunction {
   private ESObject _protoObject;

   public Constructor(String clazz, ESObject prototype) {
      this(clazz, prototype, 1);
   }

   Constructor(String clazz, ESObject prototype, int length) {
      super(clazz, clazz, length, true);
      this._protoObject = prototype;
   }

   @Override
   public long requestFieldValue(String name) {
      return name == "prototype" ? Value.makeObjectValue(this._protoObject) : super.requestFieldValue(name);
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return name == "prototype" ? 1 : 2;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      return name == "prototype" ? false : super.notifyFieldChanged(name, value);
   }
}
