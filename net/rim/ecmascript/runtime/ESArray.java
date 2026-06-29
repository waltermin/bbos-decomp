package net.rim.ecmascript.runtime;

public class ESArray extends ESObject {
   public ESArray() {
      this(0);
   }

   public ESArray(long length) {
      super("Array", GlobalObject.getInstance().arrayPrototype, length);
   }

   void clear(long length) {
      super.clearArray(length);
   }

   @Override
   public void clear() {
      super.clearArray(0);
   }

   public boolean append(long value) {
      return this.putIndex(this.getArrayLength(), value);
   }

   @Override
   public boolean putIndex(long index, long value) {
      return this.putArrayIndex(index, value);
   }

   @Override
   public boolean putElement(long name, long value) {
      return this.putArrayElement(name, value);
   }

   @Override
   public boolean putField(String name, long value) {
      return this.putArrayField(name, value);
   }

   @Override
   public long defaultNumberValue() {
      return GlobalObject.getInstance().version == 120 ? Value.makeLongValue(this.getArrayLength()) : super.defaultNumberValue();
   }
}
