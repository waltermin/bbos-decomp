package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public class RoutineRef extends MemberRef {
   protected TypeList _retList;
   private boolean _writeRet;

   public RoutineRef(ClassDef classDef, ClassRef classRef, Identifier name, TypeList parmsList, TypeList retList) {
      super(classDef, classRef, name, parmsList);
      this._retList = retList;
      this._writeRet = false;
   }

   @Override
   public void write(StructuredOutputStream out) {
      this.setOffset(out);
      super._classRef.writeOffset(out);
      super._name.writeOffset(out);
      super._typeList.writeOffset(out);
      if (this._writeRet) {
         this._retList.writeOffset(out);
      }

      this.setExtent(out);
   }

   public void setWriteRet(DataSection dataSection) {
      this._writeRet = dataSection.fixupsHaveRetType();
   }
}
