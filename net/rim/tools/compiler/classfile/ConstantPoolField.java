package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.ClassType;

public class ConstantPoolField extends ConstantPoolTwoIndex {
   ConstantPoolClass _constantPoolClass;
   ConstantPoolNameAndType _constantPoolNameAndType;

   public ConstantPoolField(int tag, StructuredInputStream in) {
      super(tag, in);
   }

   @Override
   public void resolve(ConstantPool constantPool) {
      if (this._constantPoolClass == null) {
         this._constantPoolClass = (ConstantPoolClass)constantPool.getEntry(super._index1);
         this._constantPoolNameAndType = (ConstantPoolNameAndType)constantPool.getEntry(super._index2);
      }
   }

   public ConstantPoolClass getConstantPoolClass() {
      return this._constantPoolClass;
   }

   public String getClassName() {
      return this._constantPoolClass.getName();
   }

   public ClassType getClassType() {
      return (ClassType)this._constantPoolClass.getType();
   }

   public String getName() {
      return this._constantPoolNameAndType.getName();
   }

   public String getType() {
      return this._constantPoolNameAndType.getType();
   }
}
