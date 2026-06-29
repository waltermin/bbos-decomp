package net.rim.tools.compiler.classfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.Type;

public final class ConstantPoolClass extends ConstantPoolIndex {
   ConstantPoolUTF8 _name;
   String _cachedName;
   Type _type;

   public ConstantPoolClass(int tag, StructuredInputStream in) {
      super(tag, in);
   }

   @Override
   public final void resolve(ConstantPool constantPool) {
      if (this._name == null) {
         this._name = (ConstantPoolUTF8)constantPool.getEntry(super._index);
      }
   }

   @Override
   public final void verify() throws IOException {
      String className = this._name.getString();
      int len = className.length();
      int offset = 0;

      while (offset < len) {
         char ch = className.charAt(offset++);
         switch (ch) {
            case '/':
               if (offset != len) {
                  break;
               }

               throw new IOException("bad class name: " + className);
            case 'ႀ':
            case 'Ḁ':
            case '\u3040':
            case '㐀':
            case 'ﬀ':
               throw new IOException("bad class name: " + className);
         }
      }
   }

   public final String getName() {
      if (this._cachedName == null) {
         this._cachedName = this._name.getString().replace('/', '.');
      }

      return this._cachedName;
   }

   public final void setType(Type type) {
      this._type = type;
   }

   public final Type getType() {
      return this._type;
   }
}
