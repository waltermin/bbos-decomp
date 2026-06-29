package net.rim.wica.runtime.script.internal.handler;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;

public final class DecimalFieldHandler implements PropertyHandler {
   protected DecimalFieldHandler() {
   }

   @Override
   public final void putProperty(DataCollection dc, long cmpHandler, String field, long esObject) throws ThrownValue {
      int fieldID = dc.getDef().getFieldHandle(field);
      switch (dc.getDef().getAccessType(fieldID)) {
         case 268435456:
            dc.setDoubleFieldValue(cmpHandler, fieldID, Convert.toDouble(esObject));
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
            return Value.makeDoubleValue(dc.getDoubleFieldValue(cmpHandler, fieldID));
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
            cmp.setDoubleFieldValue(fieldID, Convert.toDouble(esObject));
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
            return Value.makeDoubleValue(cmp.getDoubleFieldValue(fieldID));
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
         default:
            return Value.NaN;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final double string2Double(String b) throws ThrownValue {
      double value = (double)0L;
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         value = Double.parseDouble(b);
         var5 = false;
      } finally {
         if (var5) {
            throw ThrownValue.typeError(RuntimeResources.getString(123));
         }
      }

      return value;
   }
}
