package net.rim.wica.runtime.script.internal.handler;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.Util;

public final class LongFieldHandler implements PropertyHandler {
   protected LongFieldHandler() {
   }

   @Override
   public final void putProperty(Component component, String name, long value) {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
            component.setLongFieldValue(id, (long)Convert.toDouble(value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   @Override
   public final long getProperty(DataCollection collection, long handle, String name) {
      int id = collection.getDef().getFieldHandle(name);
      switch (collection.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            long value = collection.getLongFieldValue(handle, id);
            if (value == (int)value) {
               return Value.makeIntegerValue((int)value);
            }

            return Value.makeLongValue(value);
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.NaN;
      }
   }

   @Override
   public final void putProperty(DataCollection collection, long cmpHandler, String name, long esObject) {
      int fieldID = collection.getDef().getFieldHandle(name);
      switch (collection.getDef().getAccessType(fieldID)) {
         case 268435456:
            collection.setLongFieldValue(cmpHandler, fieldID, (long)Convert.toDouble(esObject));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   @Override
   public final long getProperty(Component component, String name) {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            long value = component.getLongFieldValue(id);
            if (value == (int)value) {
               return Value.makeIntegerValue((int)value);
            }

            return Value.makeLongValue(value);
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.NaN;
      }
   }

   static final long string2Long(String b) {
      try {
         return Util.convertStringToLong(b);
      } finally {
         throw ThrownValue.typeError(RuntimeResources.getString(128));
      }
   }
}
