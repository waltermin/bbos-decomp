package net.rim.tools.compiler.codfile;

public final class ModuleNull extends Module {
   public ModuleNull(DataSection dataSection) {
      super(dataSection);
   }

   @Override
   public final ClassDef makeClassDef(DataSection dataSection, String packageName, String className) {
      return super._nullClassDef;
   }
}
