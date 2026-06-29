package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.ClassType;

public final class ClassfileExceptionHandler {
   private int _start;
   private int _end;
   private int _handler;
   private int _iTypeName;
   private String _typeName;
   private ClassType _classType;

   public ClassfileExceptionHandler(StructuredInputStream in, ConstantPool constantPool) {
      this._start = in.readUnsignedShort();
      this._end = in.readUnsignedShort();
      this._handler = in.readUnsignedShort();
      this._iTypeName = in.readUnsignedShort();
      if (this._iTypeName == 0) {
         this._typeName = null;
      } else {
         this._typeName = constantPool.getClassName(this._iTypeName);
      }
   }

   public final int getStart() {
      return this._start;
   }

   public final int getEnd() {
      return this._end;
   }

   public final int getHandler() {
      return this._handler;
   }

   public final String getTypeName() {
      return this._typeName;
   }

   public final void setClassType(ClassType classType) {
      this._classType = classType;
   }

   public final ClassType getClassType() {
      return this._classType;
   }
}
