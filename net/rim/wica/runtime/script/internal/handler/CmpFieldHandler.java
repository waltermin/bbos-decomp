package net.rim.wica.runtime.script.internal.handler;

import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.WicaAppContext;
import net.rim.wica.runtime.script.internal.appenv.ESData;

public final class CmpFieldHandler implements PropertyHandler {
   private WicaAppContext _context;

   protected CmpFieldHandler(WicaAppContext context) {
      this._context = context;
   }

   @Override
   public final long getProperty(Component component, String name) throws ThrownValue {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            long v = component.getReferenceField(id);
            if (v == -1) {
               return Value.NULL;
            }

            return this._context.createDataInstance(this._context.getWiclet().getDataCollection(component.getDef().getFieldReferenceType(id)), v);
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.NULL;
      }
   }

   @Override
   public final long getProperty(DataCollection collection, long handle, String name) throws ThrownValue {
      int id = collection.getDef().getFieldHandle(name);
      int type = collection.getDef().getFieldReferenceType(id);
      DataCollection propertyCollection = this._context.getWiclet().getDataCollection(type);
      switch (collection.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            long v = collection.getReferenceField(handle, id);
            if (v == -1) {
               return Value.NULL;
            }

            return this._context.createDataInstance(propertyCollection, v);
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.NULL;
      }
   }

   @Override
   public final void putProperty(Component component, String name, long value) throws ThrownValue {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
            component.setReferenceField(id, getValue(component.getDef().getFieldReferenceType(id), value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   @Override
   public final void putProperty(DataCollection collection, long handle, String name, long value) throws ThrownValue {
      int id = collection.getDef().getFieldHandle(name);
      switch (collection.getDef().getAccessType(id)) {
         case 268435456:
            collection.setReferenceField(handle, id, getValue(collection.getDef().getFieldReferenceType(id), value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   public static final long getValue(int defID, long esObject) throws ThrownValue {
      long value = -1;
      if (esObject != Value.NULL) {
         Object obj = Value.getObjectValue(esObject);
         if (!(obj instanceof ESData)) {
            throw ThrownValue.typeError(RuntimeResources.getString(121));
         }

         ESData esData = (ESData)obj;
         validateDataValue(defID, esData.getCollection().getDef().getId());
         DataCollection dc = esData.getCollection();
         long cmpHandler = esData.getHandle();
         return cmpHandler != -1 && dc.contains(cmpHandler) ? cmpHandler : -1;
      } else {
         return value;
      }
   }

   static final void validateDataValue(int destDefID, int srcDefID) throws ThrownValue {
      if (destDefID != srcDefID) {
         throw ThrownValue.typeError(RuntimeResources.getString(120));
      }
   }
}
