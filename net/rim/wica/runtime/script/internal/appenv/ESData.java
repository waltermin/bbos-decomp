package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.handler.PropertyHandler;

public final class ESData extends ESMDSObject {
   private DataCollection _collection;
   private long _handle;

   public ESData(DataCollection collection, long handle, ESObject prototype) {
      super("MDSData", null, prototype);
      this._collection = collection;
      this._handle = handle;
   }

   public final DataCollection getCollection() {
      return this._collection;
   }

   public final long getHandle() {
      return this._handle;
   }

   @Override
   public final int getFieldTypeByName(String name) {
      int fieldHandle = this._collection.getDef().getFieldHandle(name);
      return fieldHandle == -1 ? fieldHandle : this._collection.getDef().getFieldType(fieldHandle);
   }

   @Override
   protected final long getFieldValue(PropertyHandler handler, String name) {
      if (!this._collection.contains(this._handle)) {
         EcmaUtilities.throwESError(RuntimeResources.getString(97));
      }

      return handler.getProperty(this._collection, this._handle, name);
   }

   @Override
   protected final void setFieldValue(PropertyHandler handler, String name, long value) {
      if (!this._collection.contains(this._handle)) {
         EcmaUtilities.throwESError(RuntimeResources.getString(99));
      }

      handler.putProperty(this._collection, this._handle, name, value);
   }
}
