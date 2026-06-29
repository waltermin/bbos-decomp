package net.rim.wica.runtime.provisioning.internal;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.provisioning.internal.elements.ScriptElement;

final class ScriptVisitor extends DefinitionVisitorAdapter {
   private EncodingContext _scriptDefs;
   private EncodingContextOffset _scriptDefsOffset;
   private UniqueCodeGenerator _uniqueCodeGenerator;

   ScriptVisitor(UniqueCodeGenerator ucg) {
      this._uniqueCodeGenerator = ucg;
      this._scriptDefs = new EncodingContext();
      this._scriptDefsOffset = new EncodingContextOffset();
   }

   final ComponentDefStruct getScriptDefs() {
      return this._scriptDefs.toComponentDefStruct();
   }

   @Override
   public final boolean visitScriptElement(ScriptElement script) {
      IntVector defs = this._scriptDefs._defs;
      int defOffset = this._scriptDefsOffset._defsOffset;
      int defsSize = defs.size() + 3;
      defs.setSize(defsSize);
      defs.setElementAt(11, defOffset + 0);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(script.getName());
      defs.setElementAt(uniqueCode, defOffset + 1);
      int objectOffset = this._scriptDefs._objectData.size();
      Vector objectData = this._scriptDefs._objectData;
      objectData.addElement(script.getName());
      defs.setElementAt(objectOffset, defOffset + 2);
      this._scriptDefsOffset._defsOffset = defsSize;
      return false;
   }
}
