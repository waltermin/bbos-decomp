package net.rim.tools.compiler.codfile;

public final class ModuleLocal extends Module {
   public ModuleLocal(DataSection dataSection, String name, String version, CodfileVector classDefs, CodfileVector routines) {
      super(dataSection, name, version, classDefs, routines);
   }

   @Override
   public final ClassDef makeClassDef(DataSection dataSection, String packageName, String className) {
      ClassDefLocal classDef = new ClassDefLocal(dataSection, packageName, className);
      this.addClassDef(classDef);
      return classDef;
   }

   public final void harvestRoutines() {
      super._routines.setSize(0);
      int num = super._classDefs.size();

      for (int i = 0; i < num; i++) {
         ClassDefLocal classDef = (ClassDefLocal)super._classDefs.elementAt(i);
         classDef.harvestRoutines(super._routines);
      }
   }
}
