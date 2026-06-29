package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

public class RedirectedObject extends ESObject {
   public static final int DeleteDone;
   public static final int DisableDelete;
   public static final int DelegateDelete;

   public RedirectedObject() {
      this.setIsRedirected();
   }

   public RedirectedObject(String clazz, ESObject prototype) {
      super(clazz, prototype);
      this.setIsRedirected();
   }

   RedirectedObject(String clazz, ESObject prototype, long arrayLength) {
      super(clazz, prototype, arrayLength);
      this.setIsRedirected();
   }

   public RedirectedObject(String clazz, ESObject prototype, boolean nullPrototypeOK) {
      super(clazz, prototype, nullPrototypeOK);
      this.setIsRedirected();
   }

   public boolean notifyFieldChanged(String name, long value) {
      return true;
   }

   private boolean doNotifyFieldChanged(String name, long value) {
      return (this.getAttributesFieldNoAssert(name) & 16) != 0 ? true : this.notifyFieldChanged(name, value);
   }

   public boolean notifyElementChanged(long element, long value) {
      return false;
   }

   private boolean doNotifyElementChanged(long element, long value) {
      return (this.getAttributesElement(element) & 16) != 0 ? true : this.notifyElementChanged(element, value);
   }

   public long requestFieldValue(String name) {
      return Value.DEFAULT;
   }

   private long doRequestFieldValue(String name) {
      return (this.getAttributesFieldNoAssert(name) & 8) != 0 ? Value.DEFAULT : this.requestFieldValue(name);
   }

   public long requestElementValue(long element) {
      return Value.DEFAULT;
   }

   public long doRequestElementValue(long element) {
      return (this.getAttributesElement(element) & 8) != 0 ? Value.DEFAULT : this.requestElementValue(element);
   }

   public int notifyFieldDeleted(String name) {
      return 2;
   }

   @Override
   long[] getValueArray() {
      return null;
   }

   @Override
   long[] getArray() {
      return null;
   }

   boolean noRedirectDeleteField(String name) {
      return super.deleteField(name);
   }

   @Override
   public long getField(String name) {
      try {
         long value = this.doRequestFieldValue(name);
         return value != Value.DEFAULT ? value : this.noRedirectGetField(name);
      } catch (BuildArgumentsException bae) {
         return Value.UNDEFINED;
      } catch (GetFunctionLengthException gfle) {
         return Value.UNDEFINED;
      }
   }

   @Override
   public long getElement(long element) {
      try {
         long value = this.doRequestElementValue(element);
         if (value != Value.DEFAULT) {
            return value;
         }

         value = this.doRequestFieldValue(Convert.toInternString(element));
         return value != Value.DEFAULT ? value : this.noRedirectGetElement(element);
      } catch (BuildArgumentsException bae) {
         return Value.UNDEFINED;
      } catch (GetFunctionLengthException gfle) {
         return Value.UNDEFINED;
      }
   }

   @Override
   public long getFieldAllowExceptions(String name) {
      long value = this.doRequestFieldValue(name);
      return value != Value.DEFAULT ? value : this.noRedirectGetField(name);
   }

   @Override
   public long getElementAllowExceptions(long element) {
      long value = this.doRequestElementValue(element);
      if (value != Value.DEFAULT) {
         return value;
      }

      value = this.doRequestFieldValue(Convert.toInternString(element));
      return value != Value.DEFAULT ? value : this.noRedirectGetElement(element);
   }

   @Override
   public long getIndex(long index) {
      try {
         long value = this.doRequestFieldValue(Misc.stringIntern(Long.toString(index)));
         return value != Value.DEFAULT ? value : super.getIndex(index);
      } catch (BuildArgumentsException bae) {
         return Value.UNDEFINED;
      } catch (GetFunctionLengthException gfle) {
         return Value.UNDEFINED;
      }
   }

   @Override
   public boolean putField(String name, long value) {
      return !this.doNotifyFieldChanged(name, value) ? false : super.noRedirectPutField(name, value);
   }

   @Override
   public boolean putElement(long element, long value) {
      if (this.doNotifyElementChanged(element, value)) {
         return true;
      } else {
         return !this.doNotifyFieldChanged(Convert.toInternString(element), value) ? false : super.noRedirectPutElement(element, value);
      }
   }

   @Override
   public boolean putIndex(long index, long value) {
      return !this.doNotifyFieldChanged(Misc.stringIntern(Long.toString(index)), value) ? false : super.noRedirectPutIndex(index, value);
   }

   @Override
   public boolean deleteField(String name) {
      switch (this.notifyFieldDeleted(name)) {
         case -1:
            return this.noRedirectDeleteField(name);
         case 0:
         default:
            return true;
         case 1:
            return false;
      }
   }

   @Override
   public boolean deleteElement(long element) {
      return this.deleteField(Convert.toInternString(element));
   }

   @Override
   public boolean deleteIndex(long index) {
      return this.deleteField(Misc.stringIntern(Long.toString(index)));
   }

   @Override
   boolean putRedirectedField(String name, long value) {
      return !this.notifyFieldChanged(name, value);
   }

   @Override
   long getRedirectedField(String name) {
      try {
         return this.requestFieldValue(name);
      } catch (BuildArgumentsException var3) {
      } catch (GetFunctionLengthException var4) {
      }

      return Value.DEFAULT;
   }
}
