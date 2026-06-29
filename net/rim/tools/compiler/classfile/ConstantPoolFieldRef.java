package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.Field;

public final class ConstantPoolFieldRef extends ConstantPoolField {
   Field _field;

   public ConstantPoolFieldRef(int tag, StructuredInputStream in) {
      super(tag, in);
   }

   @Override
   public final void verify() {
      super._constantPoolNameAndType.checkFieldType();
   }

   public final void setField(Field field) {
      this._field = field;
   }

   public final Field getField() {
      return this._field;
   }
}
