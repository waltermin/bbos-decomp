package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.Method;

public final class ConstantPoolInterfaceMethodRef extends ConstantPoolField {
   Method _method;

   public ConstantPoolInterfaceMethodRef(int tag, StructuredInputStream in) {
      super(tag, in);
   }

   @Override
   public final void verify() {
      super._constantPoolNameAndType.checkMethodType();
   }

   public final void setMethod(Method method) {
      this._method = method;
   }

   public final Method getMethod() {
      return this._method;
   }
}
