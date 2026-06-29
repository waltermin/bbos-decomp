package net.rim.wica.runtime.metadata.internal;

import java.util.Enumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.access.data.AccessDataService;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.component.ScriptCollection;
import net.rim.wica.runtime.metadata.component.ui.ResourceCollection;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.StyleCollection;
import net.rim.wica.runtime.metadata.internal.component.DataImpl;
import net.rim.wica.runtime.metadata.internal.component.EnumCollectionImpl;
import net.rim.wica.runtime.metadata.internal.component.KeyDataCollectionImpl;
import net.rim.wica.runtime.metadata.internal.component.KeylessDataCollection;
import net.rim.wica.runtime.metadata.internal.component.MsgImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.ScreenModelImpl;
import net.rim.wica.runtime.metadata.internal.def.DataDef;
import net.rim.wica.runtime.metadata.internal.def.Definitions;
import net.rim.wica.runtime.metadata.internal.util.Find;
import net.rim.wica.runtime.metadata.util.ValueResolver;
import net.rim.wica.runtime.util.Lock;
import net.rim.wica.runtime.util.LongVector;

public class WicletImpl implements WicletEx {
   private WicletContext _context;
   protected Definitions _defs;
   protected IntHashtable _cmpHandlers;
   protected ToIntHashtable _nameToHandle;
   protected ToIntHashtable _stdNameToHandle;
   private Lock _dataLock;
   private WicletRuntime _runtime;
   private Find _finder;
   private AccessDataService _access;
   private EnumCollection _enums;
   static Class class$net$rim$wica$runtime$access$data$AccessDataService;

   boolean hasUI() {
      return this._defs.getUIDefs().hasDefs();
   }

   boolean hasMessages() {
      return this._defs.getMsgDefs().hasDefs();
   }

   public Object getComponent(int defId) {
      Object cmp = this._cmpHandlers.get(defId);
      if (cmp != null) {
         return cmp;
      }

      switch (this.getDefType(defId)) {
         case 6:
            return this.getData(defId);
         case 9:
            return this.getMsg(defId);
         case 10:
            return this.getScreenModel(defId);
         default:
            return null;
      }
   }

   @Override
   public WicletContext getContext() {
      return this._context;
   }

   @Override
   public int getDefHandle(String def) {
      if (this._nameToHandle.containsKey(def)) {
         return this._nameToHandle.get(def);
      } else if (this._stdNameToHandle.containsKey(def)) {
         return this._stdNameToHandle.get(def);
      } else {
         int code = this._defs.getCodeByName(def);
         if (this._defs.hasDefinition(code)) {
            this._nameToHandle.put(def, code);
            return code;
         } else {
            return -1;
         }
      }
   }

   @Override
   public int getDefType(int defHandle) {
      int type = this._defs.getDefType(defHandle);
      if (type == -1) {
         type = this._access.getDefType(defHandle);
      }

      return type;
   }

   @Override
   public Object getData(int defId) {
      return defId == this._defs.getGlobalDefId() ? this.getGlobals() : this.getDataCollection(defId);
   }

   @Override
   public WicletRuntime getRuntime() {
      return this._runtime;
   }

   @Override
   public ComponentDef getComponentDef(int defId) {
      Object cmp = this.getComponent(defId);
      if (!(cmp instanceof Component)) {
         return !(cmp instanceof DataCollection) ? null : ((DataCollection)cmp).getDef();
      } else {
         return ((Component)cmp).getDef();
      }
   }

   @Override
   public boolean isGlobalVar(String name) {
      Data globals = this.getGlobals();
      return globals != null && globals.getDef().getFieldHandle(name) != -1;
   }

   @Override
   public Data getGlobals() {
      Data data = (Data)this._cmpHandlers.get(this._defs.getGlobalDefId());
      if (data == null && this._defs.getGlobalDefId() != -1) {
         data = new DataImpl(this._defs.getGlobalDefId(), this, this._defs.getDataDefs());
         this._cmpHandlers.put(this._defs.getGlobalDefId(), data);
      }

      return data;
   }

   @Override
   public DataCollection getDataCollection(int defHandle) {
      DataCollection c = (DataCollection)this._cmpHandlers.get(defHandle);
      if (c == null && defHandle != this._defs.getGlobalDefId()) {
         if (this._defs.getDataDefs().hasDefinition(defHandle)) {
            if (this._defs.getDataDefs().hasKey(defHandle)) {
               c = new KeyDataCollectionImpl(this, new DataDef(defHandle, this._defs.getDataDefs()));
            } else {
               c = new KeylessDataCollection(this, new DataDef(defHandle, this._defs.getDataDefs()));
            }
         } else {
            c = this._access.getDataCollection(defHandle, this);
         }

         if (c != null) {
            this._cmpHandlers.put(defHandle, c);
         }
      }

      return c;
   }

   @Override
   public Msg getMsg(int msgDef) {
      Msg msg = (Msg)this._cmpHandlers.get(msgDef);
      if (msg == null) {
         msg = new MsgImpl(msgDef, this, this._defs.getMsgDefs());
         this._cmpHandlers.put(msgDef, msg);
      }

      return msg;
   }

   @Override
   public Msg getMsgFromCode(int msgCode) {
      int def = this._defs.getMsgDefs().getMsgDefByCode(msgCode);
      return def == -1 ? null : this.getMsg(def);
   }

   @Override
   public ScriptCollection getScripts() {
      return this._defs.getScriptDefs();
   }

   @Override
   public EnumCollection getEnums() {
      return this._enums;
   }

   @Override
   public ScreenModel getScreenModel(int screenDef) {
      ScreenModel model = (ScreenModel)this._cmpHandlers.get(screenDef);
      if (model == null) {
         model = new ScreenModelImpl(screenDef, this, this._defs);
         this._cmpHandlers.put(screenDef, model);
      }

      return model;
   }

   @Override
   public StyleCollection getStyles() {
      return this._defs.getUIDefs().getStyles();
   }

   @Override
   public ResourceCollection getResources() {
      return this._defs.getUIDefs().getResources();
   }

   @Override
   public void save() {
      boolean isPersisted = false;
      Enumeration dc = this._cmpHandlers.elements();

      while (dc.hasMoreElements()) {
         Object cmp = dc.nextElement();
         if (cmp instanceof Data) {
            if (!isPersisted && ((Data)cmp).isModified()) {
               isPersisted = true;
               this._context.getWicletStore().storeDataStatus(false);
            }

            ((Data)cmp).save();
         } else if (cmp instanceof DataCollection) {
            if (!isPersisted && ((DataCollection)cmp).isModified()) {
               isPersisted = true;
               this._context.getWicletStore().storeDataStatus(false);
            }

            ((DataCollection)cmp).save();
         }
      }

      if (isPersisted) {
         this._context.getWicletStore().storeDataStatus(true);
      }
   }

   @Override
   public Object getValue(long handle, int[] fields, int start, int end) {
      if (handle == -1) {
         return null;
      }

      Object object = this.getData((int)(handle >> 32));
      if (!(object instanceof Data)) {
         if (!((DataCollection)object).contains(handle)) {
            return null;
         }
      } else {
         Data data = (Data)object;
         int type = data.getDef().getFieldType(fields[start]);
         if (type != 6) {
            return data.getFieldValueAsObject(fields[start]);
         }

         handle = data.getReferenceField(fields[start++]);
         if (handle == -1) {
            return null;
         }

         object = this.getDataCollection((int)(handle >> 32));
      }

      DataCollection dc = (DataCollection)object;

      for (int i = start; i < end; i++) {
         int type = dc.getDef().getFieldType(fields[i]);
         if (type != 6) {
            return dc.getFieldValueAsObject(handle, fields[i]);
         }

         handle = dc.getReferenceField(handle, fields[i]);
         if (handle == -1) {
            return null;
         }

         dc = this.getDataCollection((int)(handle >> 32));
      }

      return new Long(handle);
   }

   @Override
   public long getRef(long handle, int[] fields, int start, int end) {
      if (handle == -1) {
         return -1;
      }

      Object object = this.getData((int)(handle >> 32));
      if (!(object instanceof Data)) {
         if (!((DataCollection)object).contains(handle)) {
            return -1;
         }
      } else {
         Data data = (Data)object;
         int type = data.getDef().getFieldType(fields[start]);
         if (type != 6) {
            throw new RuntimeException("Can not be a primitive");
         }

         handle = data.getReferenceField(fields[start++]);
         if (handle == -1) {
            return -1;
         }

         object = this.getDataCollection((int)(handle >> 32));
      }

      DataCollection dc = (DataCollection)object;

      for (int i = start; i < end; i++) {
         int type = dc.getDef().getFieldType(fields[i]);
         if (type != 6) {
            throw new RuntimeException("Can not be a primitive");
         }

         handle = dc.getReferenceField(handle, fields[i]);
         if (handle == -1) {
            return -1;
         }

         dc = this.getDataCollection((int)(handle >> 32));
      }

      return handle;
   }

   @Override
   public long verifyHandle(long handle) {
      if (handle != -1) {
         Object object = this.getData((int)(handle >> 32));
         if (object instanceof DataCollection) {
            if (((DataCollection)object).contains(handle)) {
               return handle;
            }

            return -1;
         }

         if (object != null) {
            return handle;
         }
      }

      return -1;
   }

   @Override
   public int setValue(long handle, int[] fields, int start, int end, Object value) {
      if (handle == -1) {
         return start;
      }

      Object object = this.getData((int)(handle >> 32));
      if (!(object instanceof Data)) {
         if (!((DataCollection)object).contains(handle)) {
            return start;
         }
      } else {
         Data data = (Data)object;
         if (start == end - 1) {
            data.setFieldValueFromObject(fields[start], value);
            return -1;
         }

         handle = data.getReferenceField(fields[start]);
         if (handle == -1) {
            return start;
         }

         start++;
      }

      DataCollection dc = null;

      while (start < end - 1 && handle != -1) {
         dc = this.getDataCollection((int)(handle >> 32));
         handle = dc.getReferenceField(handle, fields[start]);
         start++;
      }

      if (start == end - 1 && handle != -1) {
         dc = this.getDataCollection((int)(handle >> 32));
         dc.setFieldValueFromObject(handle, fields[start], value);
         return -1;
      } else {
         return start;
      }
   }

   @Override
   public Lock getDataLock() {
      return this._dataLock;
   }

   @Override
   public long getId() {
      return this._context.getId();
   }

   @Override
   public void findWhere(LongVector results, DataCollection dataCollection, long[] handles, String expression, ValueResolver resolver) {
      if (this._finder == null) {
         this._finder = new Find(this);
      }

      this._finder.findWhere(results, dataCollection, handles, expression, resolver);
   }

   @Override
   public Definitions getDefinitions() {
      return this._defs;
   }

   @Override
   public void clear() {
      this._cmpHandlers.clear();
      this._access.clearDataCollections();
   }

   @Override
   public void clear(int defId) {
      this._cmpHandlers.remove(defId);
      this._access.clearDataCollection(defId);
   }

   @Override
   public boolean isBackground() {
      return false;
   }

   public WicletImpl(WicletContext context, WicletRuntime runtime) {
      this._context = context;
      this._defs = (Definitions)context.getWicletStore().loadDefinitions();
      this._cmpHandlers = new IntHashtable();
      this._nameToHandle = new ToIntHashtable();
      this._dataLock = new Lock();
      this._runtime = runtime;
      this._access = (AccessDataService)this._runtime
         .getService(
            class$net$rim$wica$runtime$access$data$AccessDataService == null
               ? (class$net$rim$wica$runtime$access$data$AccessDataService = class$("net.rim.wica.runtime.access.data.AccessDataService"))
               : class$net$rim$wica$runtime$access$data$AccessDataService
         );
      this._stdNameToHandle = this._access.getAllDefs();
      this._enums = new EnumCollectionImpl(this._defs.getDataDefs(), this._access.getEnumCollection());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
