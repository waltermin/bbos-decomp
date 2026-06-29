package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ClassRef extends CodfileItem {
   protected Module _module;
   protected Identifier _packageName;
   protected Identifier _className;
   protected ClassDef _classDef;

   public ClassRef(DataSection dataSection, ClassDef classDef) {
      super(0);
      super._ordinal = -1;
      this._classDef = classDef;
      this._module = classDef.getModule();
      this._packageName = classDef.getPackageName();
      this._className = classDef.getClassName();
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      out.writeShort(this._module.getOrdinal());
      this._packageName.writeOffset(out);
      this._className.writeOffset(out);
      out.writeByte(0);
      if (this._classDef instanceof ClassDefDomestic) {
         out.writeByte(this._classDef.getOrdinal());
      } else {
         out.writeByte(0);
      }

      this.setExtent(out);
   }

   public final int getModuleNum() {
      return this._module.getOrdinal();
   }

   public final ClassDef getClassDef() {
      return this._classDef;
   }

   @Override
   public final int compareTo(Object o) {
      ClassRef other = (ClassRef)o;
      return this.getModuleNum() - other.getModuleNum();
   }
}
