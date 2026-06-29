package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class RoutineNull extends Routine {
   public RoutineNull(ClassDef classDef, DataSection dataSection) {
      super(classDef, -1);
      super._name = dataSection.getDataBytes().getNullIdentifier();
      super._protoTypeList = dataSection.getTypeLists().getNullTypeList();
   }

   @Override
   public final void write(StructuredOutputStream out) {
      int offset = out.getOffset();
      ClassDefLocal classDefLocal = (ClassDefLocal)super._classDef;
      classDefLocal.ratchetStartCodeOffset(offset);
      classDefLocal.ratchetEndCodeOffset(offset);
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      classDef.writeOrdinal(out);
      super.writeOffset(out);
   }

   @Override
   public final void writeOffset(StructuredOutputStream out) {
      if (super._classDef.getModule().getOrdinal() != 0) {
         out.writeByte(-1);
      }

      out.writeShort(-1);
   }

   @Override
   public final void writeOrdinal(StructuredOutputStream out) {
      out.writeShort(-1);
   }
}
