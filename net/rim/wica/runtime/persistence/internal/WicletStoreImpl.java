package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.vm.Memory;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.metadata.internal.def.Definitions;
import net.rim.wica.runtime.persistence.Recryptable;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.persistence.WicletStore;

final class WicletStoreImpl implements WicletStore {
   private PersistenceServiceImpl _persistenceService;
   private PersistedApplicationModel _persistedModel;
   private ResourceCache _resourceCache;

   WicletStoreImpl(PersistenceServiceImpl persistenceService, PersistedApplicationModel persistedModel) {
      this._persistenceService = persistenceService;
      this._persistedModel = persistedModel;
      this._resourceCache = new ResourceCache(this);
   }

   final PersistedApplicationModel getModel() {
      return this._persistedModel;
   }

   final void persistentContentModeChanged(int generation) {
      IntSubstore dataStore = this._persistedModel._dataSubstore;
      if (dataStore != null) {
         Recryptable data = null;
         IntEnumeration keys = dataStore.keys();

         while (keys.hasMoreElements()) {
            int id = keys.nextElement();
            data = (Recryptable)dataStore.get(id);
            data.recrypt();
         }

         this._resourceCache.recrypt();
         this.save();
      }
   }

   @Override
   public final int getCacheFlashSize() {
      return this._resourceCache.size();
   }

   @Override
   public final int getDataFlashSize() {
      return this._persistedModel._dataSubstore != null ? Memory.objectFlashSize(this._persistedModel._dataSubstore) : 0;
   }

   @Override
   public final WicletInfo getInfo() {
      return this._persistedModel._info;
   }

   @Override
   public final String getMemoryStatistics() {
      int mappingTblSize = Memory.objectFlashSize(this._persistedModel._mappingTable);
      int collisionTblSize = Memory.objectFlashSize(this._persistedModel._defCollisions);
      int dataDefSize = Memory.objectFlashSize(this._persistedModel._dataDefs);
      int messageDefSize = Memory.objectFlashSize(this._persistedModel._msgDefs);
      int scriptSize = Memory.objectFlashSize(this._persistedModel._scriptDefs);
      int uiDefSize = Memory.objectFlashSize(this._persistedModel._uiDefs);
      int resourcesSize = this.getCacheFlashSize();
      int dataSize = this.getDataFlashSize();
      int totalSize = collisionTblSize + dataDefSize + messageDefSize + uiDefSize + scriptSize + resourcesSize + dataSize;
      StringBuffer stats = (StringBuffer)(new Object());
      stats.append("Collision table size: ");
      stats.append(collisionTblSize);
      stats.append(" bytes\nData definitions size: ");
      stats.append(dataDefSize);
      stats.append(" bytes\nMapping table size: ");
      stats.append(mappingTblSize);
      stats.append(" bytes\nMessage definitions size: ");
      stats.append(messageDefSize);
      stats.append(" bytes\nUI definitions size: ");
      stats.append(uiDefSize);
      stats.append(" bytes\nResources size: ");
      stats.append(resourcesSize);
      stats.append(" bytes\nData size: ");
      stats.append(dataSize);
      stats.append(" bytes\nTotal size: ");
      stats.append(totalSize);
      stats.append(" bytes");
      return stats.toString();
   }

   @Override
   public final int getMetadataFlashSize() {
      int mappingTblSize = Memory.objectFlashSize(this._persistedModel._mappingTable);
      int collisionTblSize = Memory.objectFlashSize(this._persistedModel._defCollisions);
      int dataDefSize = Memory.objectFlashSize(this._persistedModel._dataDefs);
      int messageDefSize = Memory.objectFlashSize(this._persistedModel._msgDefs);
      int scriptSize = Memory.objectFlashSize(this._persistedModel._scriptDefs);
      int uiDefSize = Memory.objectFlashSize(this._persistedModel._uiDefs);
      return mappingTblSize + collisionTblSize + dataDefSize + messageDefSize + uiDefSize + scriptSize;
   }

   @Override
   public final byte[] getPackage() {
      return this._persistedModel._applicationPackage;
   }

   @Override
   public final Object loadDefinitions() {
      return new Definitions(
         this._persistedModel._defCollisions,
         this._persistedModel._globalDefId,
         this._persistedModel._dataDefs,
         this._persistedModel._msgDefs,
         this._persistedModel._scriptDefs,
         this._persistedModel._uiDefs
      );
   }

   @Override
   public final byte[] loadMappingTable() {
      return this._persistedModel._mappingTable;
   }

   @Override
   public final void save() {
      this._persistenceService.saveApplications();
   }

   @Override
   public final void storeApplication(WicletInfo info, byte[] applicationPackage) {
      this._persistedModel._info = info;
      this._persistedModel._applicationPackage = applicationPackage;
      this.save();
   }

   @Override
   public final void storeApplication(
      WicletInfo info,
      byte[] mappingTable,
      ToIntHashtable defCollisions,
      int globalDefId,
      ComponentDefStruct dataDefs,
      ComponentDefStruct msgDefs,
      ComponentDefStruct scriptDefs,
      ComponentDefStruct uiDefs,
      Resource[] resources,
      byte[] applicationPackage
   ) {
      this._persistedModel._info = info;
      this._persistedModel._mappingTable = mappingTable;
      ObjectGroup.createGroupIgnoreTooBig(this._persistedModel._mappingTable);
      this._persistedModel._defCollisions = defCollisions;
      ObjectGroup.createGroupIgnoreTooBig(this._persistedModel._defCollisions);
      this._persistedModel._globalDefId = globalDefId;
      this._persistedModel._dataDefs = dataDefs;
      ObjectGroup.createGroupIgnoreTooBig(this._persistedModel._dataDefs);
      this._persistedModel._msgDefs = msgDefs;
      ObjectGroup.createGroupIgnoreTooBig(this._persistedModel._msgDefs);
      this._persistedModel._scriptDefs = scriptDefs;
      ObjectGroup.createGroupIgnoreTooBig(this._persistedModel._scriptDefs);
      this._persistedModel._uiDefs = uiDefs;
      ObjectGroup.createGroupIgnoreTooBig(this._persistedModel._uiDefs);
      if (resources != null && resources.length > 0) {
         for (int i = resources.length - 1; i >= 0; i--) {
            this._resourceCache.put(resources[i]);
         }
      }

      this._persistedModel._applicationPackage = applicationPackage;
      this.save();
   }

   @Override
   public final void storeResource(Resource resource) {
      this._resourceCache.put(resource);
   }

   @Override
   public final Resource getResource(String uri) {
      return this._resourceCache.get(uri);
   }

   @Override
   public final int freeResources() {
      return this._resourceCache.evict();
   }

   @Override
   public final void storeData(int definitionId, Persistable data) {
      if (this._persistedModel._dataSubstore == null) {
         this._persistedModel._dataSubstore = new IntSubstore();
      }

      this._persistedModel._dataSubstore.put(definitionId, data);
      this.save();
   }

   @Override
   public final void storeDataStatus(boolean safe) {
      this._persistedModel._safeData = safe;
      this.save();
   }

   @Override
   public final boolean getDataStatus() {
      return this._persistedModel._safeData;
   }

   @Override
   public final void wipeData() {
      this._persistedModel._dataSubstore.clear();
      this._persistedModel._safeData = true;
      this.save();
   }

   @Override
   public final Persistable loadData(int definitionId) {
      return (Persistable)(this._persistedModel._dataSubstore != null ? this._persistedModel._dataSubstore.get(definitionId) : null);
   }

   @Override
   public final void copyData(WicletStore wicletStore) {
      IntEnumeration keys = wicletStore.loadDataDefinitionIds();
      if (keys != null) {
         if (this._persistedModel._dataSubstore == null) {
            this._persistedModel._dataSubstore = new IntSubstore();
         }

         Persistable data = null;

         while (keys.hasMoreElements()) {
            int defId = keys.nextElement();
            data = wicletStore.loadData(defId);
            this._persistedModel._dataSubstore.put(defId, data);
         }

         this.save();
      }
   }

   @Override
   public final IntEnumeration loadDataDefinitionIds() {
      return this._persistedModel._dataSubstore != null ? this._persistedModel._dataSubstore.keys() : null;
   }
}
