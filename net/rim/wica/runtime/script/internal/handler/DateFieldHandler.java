package net.rim.wica.runtime.script.internal.handler;

import net.rim.device.api.io.http.HttpDateParser;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESDate;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;

public final class DateFieldHandler implements PropertyHandler {
   protected DateFieldHandler() {
   }

   @Override
   public final long getProperty(Component component, String name) throws ThrownValue {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            return Value.makeObjectValue((ESObject)(new Object(component.getLongFieldValue(id))));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
         default:
            return Value.NULL;
      }
   }

   @Override
   public final long getProperty(DataCollection collection, long handle, String name) throws ThrownValue {
      int id = collection.getDef().getFieldHandle(name);
      switch (collection.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            return Value.makeObjectValue((ESObject)(new Object(collection.getLongFieldValue(handle, id))));
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
            component.setLongFieldValue(id, getValue(value));
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
            collection.setLongFieldValue(handle, id, getValue(value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   public static final long getValue(long esObject) throws ThrownValue {
      long fieldVal = 0;
      Object obj = Convert.toObject(esObject);
      if (!(obj instanceof Object)) {
         if (!(obj instanceof Object)) {
            throw ThrownValue.typeError(RuntimeResources.getString(122));
         } else {
            return HttpDateParser.parse(((ESString)obj).getValue());
         }
      } else {
         return (long)((ESDate)obj).getValue();
      }
   }
}
