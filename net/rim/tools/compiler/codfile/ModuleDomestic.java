package net.rim.tools.compiler.codfile;

public final class ModuleDomestic extends Module {
   public ModuleDomestic(DataSection dataSection, String name, String version) {
      super(dataSection, name, version, new CodfileVector(), new CodfileVector());
   }

   @Override
   public final ClassDef makeClassDef(DataSection dataSection, String packageName, String className) {
      ClassDefDomestic classDef = new ClassDefDomestic(dataSection, packageName, className);
      this.addClassDef(classDef);
      return classDef;
   }
}
