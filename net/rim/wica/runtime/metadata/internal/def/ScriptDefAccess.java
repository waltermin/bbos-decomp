package net.rim.wica.runtime.metadata.internal.def;

import net.rim.device.api.util.IntIntHashtable;
import net.rim.wica.runtime.metadata.component.ScriptCollection;

public class ScriptDefAccess implements ScriptCollection {
   private ComponentDefStruct _scriptDefs;
   private IntIntHashtable _idToDefIndex;

   public boolean hasDefinition(int defId) {
      return this._idToDefIndex.containsKey(defId);
   }

   @Override
   public String getFunctionName(int defId) {
      int dataIndex = this._idToDefIndex.get(defId);
      int objIndex = this._scriptDefs._defs[dataIndex + 2];
      return (String)this._scriptDefs._objectData[objIndex];
   }

   ScriptDefAccess(ComponentDefStruct scriptDefs) {
      this._scriptDefs = scriptDefs;
      int size = this._scriptDefs._defs.length;
      int scriptCount = size / 3;
      this._idToDefIndex = (IntIntHashtable)(new Object(scriptCount + (scriptCount >> 1)));

      for (int i = 0; i < size; i += 3) {
         this._idToDefIndex.put(this._scriptDefs._defs[i + 1], i);
      }
   }
}
