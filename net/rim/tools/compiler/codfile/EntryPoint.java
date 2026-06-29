package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class EntryPoint extends CodfileItem {
   private ClassRef _classRef;
   private Identifier _name;
   private TypeList _typeList;

   public EntryPoint(ClassRef classRef, Identifier name, TypeList typeList) {
      this._classRef = classRef;
      this._name = name;
      this._typeList = typeList;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._classRef.writeOffset(out);
      this._name.writeOffset(out);
      this._typeList.writeOffset(out);
      this.setExtent(out);
   }
}
