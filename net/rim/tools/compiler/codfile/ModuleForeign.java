package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ModuleForeign extends Module {
   public ModuleForeign(DataSection dataSection, String name, String version) {
      super(dataSection, name, version, new CodfileVector(), new CodfileVector());
   }

   @Override
   public final void writeOrdinal(StructuredOutputStream out) {
      out.writeByte(-1);
   }

   @Override
   public final ClassDef makeClassDef(DataSection dataSection, String packageName, String className) {
      ClassDefForeign classDef = new ClassDefForeign(dataSection, packageName, className);
      this.addClassDef(classDef);
      return classDef;
   }
}
