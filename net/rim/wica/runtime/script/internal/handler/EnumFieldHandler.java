package net.rim.wica.runtime.script.internal.handler;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.WicaAppContext;
import net.rim.wica.runtime.script.internal.appenv.ESEnum;

public final class EnumFieldHandler implements PropertyHandler {
   private static WicaAppContext _context;

   protected EnumFieldHandler(WicaAppContext context) {
      _context = context;
   }

   @Override
   public final void putProperty(DataCollection dc, long cmpHandler, String field, long esObject) throws ThrownValue {
      int fieldID = dc.getDef().getFieldHandle(field);
      switch (dc.getDef().getAccessType(fieldID)) {
         case 268435456:
            dc.setIntFieldValue(cmpHandler, fieldID, getValue(dc.getDef().getFieldReferenceType(fieldID), esObject));
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
            int enumType = dc.getDef().getFieldReferenceType(fieldID);
            int value = dc.getIntFieldValue(cmpHandler, fieldID);
            return _context.createEnumValue(value, enumType);
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
         default:
            return Value.NULL;
      }
   }

   @Override
   public final void putProperty(Component cmp, String field, long esObject) throws ThrownValue {
      int fieldID = cmp.getDef().getFieldHandle(field);
      switch (cmp.getDef().getAccessType(fieldID)) {
         case 268435456:
            cmp.setIntFieldValue(fieldID, getValue(cmp.getDef().getFieldReferenceType(fieldID), esObject));
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
            int enumType = cmp.getDef().getFieldReferenceType(fieldID);
            int value = cmp.getIntFieldValue(fieldID);
            return _context.createEnumValue(value, enumType);
         case 1073741824:
            throw ThrownValue.typeError(RuntimeResources.getString(85, field));
         default:
            return Value.NULL;
      }
   }

   public static final int getValue(int type, long value) throws ThrownValue {
      int enumValue = -1;
      Object esObj = Convert.toObject(value);
      if (!(esObj instanceof ESEnum)) {
         if (esObj instanceof ESString) {
            if (_context == null) {
               FieldHandlerFactory.getHandler(5);
            }

            enumValue = _context.getEnumCollection().getEnumValueAsInt(type, ((ESString)esObj).getValue());
            if (enumValue == -1) {
               throw ThrownValue.typeError(RuntimeResources.getString(126));
            } else {
               return enumValue;
            }
         } else {
            throw ThrownValue.typeError(RuntimeResources.getString(126));
         }
      } else {
         ESEnum esEnum = (ESEnum)esObj;
         validateEnumValue(type, esEnum.getType());
         return esEnum.getValue();
      }
   }

   static final void validateEnumValue(int type, int assignmentType) throws ThrownValue {
      if (type != assignmentType) {
         throw ThrownValue.typeError(RuntimeResources.getString(124));
      }
   }
}
