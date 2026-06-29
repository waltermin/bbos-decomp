package net.rim.wica.runtime.script.internal.handler;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.Util;

public final class IntegerFieldHandler implements PropertyHandler {
   protected IntegerFieldHandler() {
   }

   @Override
   public final void putProperty(DataCollection dc, long cmpHandler, String field, long esObject) throws ThrownValue {
      int fieldID = dc.getDef().getFieldHandle(field);
      switch (dc.getDef().getAccessType(fieldID)) {
         case 268435456:
            dc.setIntFieldValue(cmpHandler, fieldID, Convert.toInt32(esObject));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, field));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
      }
   }

   @Override
   public final long getProperty(DataCollection dc, long cmpHandler, String field) throws ThrownValue {
      int fieldID = dc.getDef().getFieldHandle(field);
      switch (dc.getDef().getAccessType(fieldID)) {
         case 268435456:
         case 536870912:
            return Value.makeIntegerValue(dc.getIntFieldValue(cmpHandler, fieldID));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
         default:
            return Value.NaN;
      }
   }

   @Override
   public final void putProperty(Component cmp, String field, long esObject) throws ThrownValue {
      int fieldID = cmp.getDef().getFieldHandle(field);
      switch (cmp.getDef().getAccessType(fieldID)) {
         case 268435456:
            cmp.setIntFieldValue(fieldID, Convert.toInt32(esObject));
            return;
         case 536870912:
            throw ThrownValue.typeError(RuntimeResources.getString(117, field));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
      }
   }

   @Override
   public final long getProperty(Component cmp, String field) throws ThrownValue {
      int fieldID = cmp.getDef().getFieldHandle(field);
      switch (cmp.getDef().getAccessType(fieldID)) {
         case 268435456:
         case 536870912:
            return Value.makeIntegerValue(cmp.getIntFieldValue(fieldID));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
         default:
            return Value.NaN;
      }
   }

   static final int string2Int(String b) throws ThrownValue {
      try {
         return Util.convertStringToInt(b);
      } finally {
         throw ThrownValue.typeError(RuntimeResources.getString(127));
      }
   }
}
