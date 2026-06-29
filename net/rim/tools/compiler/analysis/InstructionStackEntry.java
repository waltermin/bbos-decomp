package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.ClassUninitializedType;
import net.rim.tools.compiler.types.Type;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public final class InstructionStackEntry implements Constants {
   private int _numTypes;
   private Type[] _types;

   public InstructionStackEntry(Type[] types) {
      int num = types.length;

      for (int i = 0; i < num; i++) {
         Type type = types[i];
         if (type instanceof ClassType) {
            ClassType classType = (ClassType)type;
            if (classType.is(2048)) {
               type = classType.getBaseClassType();
               types[i] = type;
            }
         }
      }

      this._types = types;
      this._numTypes = num;
   }

   public final int getNumTypes() {
      return this._numTypes;
   }

   public final Type[] getTypes() {
      return this._types;
   }

   public final void fixupUninitializedOffsets(int offset, int ordinal) {
      int num = this._numTypes;

      for (int i = 0; i < num; i++) {
         Type type = this._types[i];
         if (type instanceof ClassUninitializedType) {
            ClassUninitializedType cut = (ClassUninitializedType)type;
            cut.fixupOffset(offset, ordinal);
         }
      }
   }

   public final void verifyUninitializedOffsets() throws CompileException {
      int num = this._numTypes;

      for (int i = 0; i < num; i++) {
         Type type = this._types[i];
         if (type instanceof ClassUninitializedType) {
            ClassUninitializedType cut = (ClassUninitializedType)type;
            if (cut.isPreverified()) {
               throw new CompileException("bad offset for NewObject StackMap entry");
            }
         }
      }
   }
}
