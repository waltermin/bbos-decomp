package net.rim.wica.runtime.script.internal.handler;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;

public final class BooleanFieldHandler implements PropertyHandler {
   protected BooleanFieldHandler() {
   }

   @Override
   public final long getProperty(Component component, String name) {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            return Value.makeBooleanValue(component.getBooleanFieldValue(id));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.FALSE;
      }
   }

   @Override
   public final long getProperty(DataCollection collection, long handle, String name) {
      int id = collection.getDef().getFieldHandle(name);
      switch (collection.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            return Value.makeBooleanValue(collection.getBooleanFieldValue(handle, id));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.FALSE;
      }
   }

   @Override
   public final void putProperty(Component component, String name, long value) {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
            component.setBooleanFieldValue(id, Convert.toBoolean(value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   @Override
   public final void putProperty(DataCollection collection, long handle, String name, long value) {
      int id = collection.getDef().getFieldHandle(name);
      switch (collection.getDef().getAccessType(id)) {
         case 268435456:
            collection.setBooleanFieldValue(handle, id, Convert.toBoolean(value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   static final boolean string2Boolean(String b) {
      boolean value = false;
      if (b != null && b.length() > 0) {
         value = true;
      }

      return value;
   }
}
