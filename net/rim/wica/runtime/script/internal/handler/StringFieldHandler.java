package net.rim.wica.runtime.script.internal.handler;

import java.util.Date;
import net.rim.ecmascript.runtime.ESDate;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.appenv.ESEnum;
import net.rim.wica.runtime.util.Util;

public final class StringFieldHandler implements PropertyHandler {
   protected StringFieldHandler() {
   }

   @Override
   public final long getProperty(Component component, String name) throws ThrownValue {
      int id = component.getDef().getFieldHandle(name);
      switch (component.getDef().getAccessType(id)) {
         case 268435456:
         case 536870912:
            Object obj = component.getObjectFieldValue(id);
            if (!(obj instanceof String)) {
               return Value.NULL;
            }

            return Value.makeStringValue((String)obj);
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
            String vStr = (String)collection.getObjectFieldValue(handle, id);
            if (vStr == null) {
               return Value.NULL;
            }

            return Value.makeStringValue(vStr);
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
            component.setObjectFieldValue(id, getValue(value));
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
            collection.setObjectFieldValue(handle, id, getValue(value));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, name));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, name));
      }
   }

   public static final String getValue(long esObject) throws ThrownValue {
      int type = Value.getType(esObject);
      String fieldVal = null;
      switch (type) {
         case 0:
            return String.valueOf(Value.getIntegerValue(esObject));
         case 2:
            fieldVal = "UNDEFINED";
         case -1:
         case 1:
            throw ThrownValue.typeError(RuntimeResources.getString(144));
         case 3:
            return null;
         case 4:
            return String.valueOf(Value.getBooleanValue(esObject));
         case 5:
         default:
            return Value.getStringValue(esObject);
         case 6:
            Object o = Value.getObjectValue(esObject);
            if (!(o instanceof ESString)) {
               if (!(o instanceof ESEnum)) {
                  if (o instanceof ESDate) {
                     return Util.DEFAULT_DATE_FORMATTER.format(new Date((long)((ESDate)o).getValue()));
                  }

                  throw ThrownValue.typeError(RuntimeResources.getString(144));
               }

               return ((ESEnum)o).getStringValue();
            }

            return ((ESString)o).getValue();
         case 7:
            return String.valueOf(Value.getDoubleValue(esObject));
      }
   }
}
