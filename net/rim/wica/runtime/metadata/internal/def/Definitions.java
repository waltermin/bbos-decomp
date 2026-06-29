package net.rim.wica.runtime.metadata.internal.def;

import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.access.internal.data.AccessDataServiceImpl;

public class Definitions {
   private ToIntHashtable _collisionTable;
   private int _globalDataDefId;
   private DataDefAccess _dataAccess;
   private MsgDefAccess _msgAccess;
   private ScriptDefAccess _scriptAccess;
   private UIDefAccess _uiAccess;
   private ToIntHashtable _builtinDefs;

   public Definitions() {
   }

   public Definitions(
      ToIntHashtable collisions,
      int globalDef,
      ComponentDefStruct dataDefs,
      ComponentDefStruct msgDefs,
      ComponentDefStruct scriptDefs,
      ComponentDefStruct uiDefs
   ) {
      this._collisionTable = collisions;
      this._globalDataDefId = globalDef;
      this.setDataAccess(dataDefs);
      this.setMsgAccess(msgDefs);
      this.setScriptAccess(scriptDefs);
      this.setUiAccess(uiDefs);
      this._builtinDefs = new ToIntHashtable();
      AccessDataServiceImpl.getAllStdCompDefs(this._builtinDefs);
   }

   public int getCodeByName(String name) {
      int code = this._builtinDefs.get(name);
      if (code == -1) {
         code = this._collisionTable.containsKey(name) ? this._collisionTable.get(name) : name.hashCode();
      }

      return code;
   }

   public int getDefType(int def) {
      if (this._dataAccess.hasDefinition(def)) {
         return this._dataAccess.getType(def);
      } else if (this._msgAccess.hasDefinition(def)) {
         return 9;
      } else if (this._scriptAccess.hasDefinition(def)) {
         return 11;
      } else {
         return this._uiAccess.hasDefinition(def) ? this._uiAccess.getType(def) : -1;
      }
   }

   public boolean hasDefinition(int def) {
      return this.getDefType(def) != -1;
   }

   public int getGlobalDefId() {
      return this._globalDataDefId;
   }

   public MsgDefAccess getMsgDefs() {
      return this._msgAccess;
   }

   public DataDefAccess getDataDefs() {
      return this._dataAccess;
   }

   public ScriptDefAccess getScriptDefs() {
      return this._scriptAccess;
   }

   public UIDefAccess getUIDefs() {
      return this._uiAccess;
   }

   public ToIntHashtable getCollisionTable() {
      return this._collisionTable;
   }

   public DataDefAccess getDataAccess() {
      return this._dataAccess;
   }

   public int getGlobalDataDefId() {
      return this._globalDataDefId;
   }

   public MsgDefAccess getMsgAccess() {
      return this._msgAccess;
   }

   public ScriptDefAccess getScriptAccess() {
      return this._scriptAccess;
   }

   public UIDefAccess getUiAccess() {
      return this._uiAccess;
   }

   public void setCollisionTable(ToIntHashtable collisionTable) {
      this._collisionTable = collisionTable;
   }

   public void setDataAccess(ComponentDefStruct dataDefs) {
      this._dataAccess = new DataDefAccess(this, dataDefs);
   }

   public void setGlobalDataDefId(int globalDataDefId) {
      this._globalDataDefId = globalDataDefId;
   }

   public void setMsgAccess(ComponentDefStruct msgDefs) {
      this._msgAccess = new MsgDefAccess(this, msgDefs);
   }

   public void setScriptAccess(ComponentDefStruct scriptDefs) {
      this._scriptAccess = new ScriptDefAccess(scriptDefs);
   }

   public void setUiAccess(ComponentDefStruct uiDefs) {
      this._uiAccess = new UIDefAccess(uiDefs);
   }
}
