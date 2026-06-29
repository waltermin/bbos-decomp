package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESDate;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.script.internal.appenv.ESData;
import net.rim.wica.runtime.script.internal.appenv.ESEnum;
import net.rim.wica.runtime.script.internal.appenv.ESMDSArray;
import net.rim.wica.runtime.util.LongVector;

public final class TypeConverter {
   private WicaAppContext _context;

   public TypeConverter(WicaAppContext context) {
      this._context = context;
   }

   public final long convertMDSObjectToESValue(Object obj, long valueType, UIControl control) {
      int type = (int)(valueType & -1);
      long value = Value.DEFAULT;
      switch (type) {
         case 0:
            value = obj == null ? Value.FALSE : Value.makeBooleanValue((Boolean)obj);
            break;
         case 1:
            value = obj == null ? Value.ZERO : Value.makeIntegerValue((Integer)obj);
            break;
         case 2:
            value = obj == null ? Value.ZERO : Value.makeDoubleValue((Double)obj);
            break;
         case 3:
            value = obj == null ? Value.NULL : Value.makeStringValue((String)obj);
            break;
         case 4:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESDate(((Long)obj).longValue()));
            break;
         case 5:
            int enumType = (int)(valueType >>> 32);
            int enumValue = (Integer)obj;
            value = obj == null ? Value.NULL : this._context.createEnumValue(enumValue, enumType);
            break;
         case 6:
            if (obj == null) {
               value = Value.NULL;
            } else {
               long dataValue = (Long)obj;
               value = this._context.createDataInstance(this._context.getWiclet().getDataCollection((int)(dataValue >>> 32)), dataValue);
            }
            break;
         case 8:
            if (!(obj instanceof Long)) {
               if (!(obj instanceof Integer)) {
                  if (!(obj instanceof Double)) {
                     value = Value.ZERO;
                  } else {
                     value = Value.makeLongValue((long)((Double)obj).doubleValue());
                  }
               } else {
                  value = Value.makeLongValue(((Integer)obj).intValue());
               }
            } else {
               value = Value.makeLongValue((Long)obj);
            }
            break;
         case 32768:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESMDSArray(32768, obj, null, "boolean[]", control, this._context));
            break;
         case 32769:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESMDSArray(32769, obj, null, "int[]", control, this._context));
            break;
         case 32770:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESMDSArray(32770, obj, null, "double[]", control, this._context));
            break;
         case 32771:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESMDSArray(32771, obj, null, "String[]", control, this._context));
            break;
         case 32772:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESMDSArray(32772, obj, null, "Long_Date[]", control, this._context));
            break;
         case 32773:
            value = obj == null
               ? Value.NULL
               : Value.makeObjectValue(new ESMDSArray(32773, obj, null, "Enum[]", (int)(valueType >>> 32), control, this._context));
            break;
         case 32774:
            value = obj == null
               ? Value.NULL
               : Value.makeObjectValue(
                  new ESMDSArray(
                     32774,
                     obj,
                     ((LongVector)obj).size() == 0 ? null : this._context.getWiclet().getDataCollection((int)(((LongVector)obj).firstElement() >>> 32)),
                     "Data[]",
                     control,
                     this._context
                  )
               );
            break;
         case 32776:
            value = obj == null ? Value.NULL : Value.makeObjectValue(new ESMDSArray(32776, obj, null, "long[]", control, this._context));
            break;
         default:
            value = Value.NULL;
      }

      return value;
   }

   public final Object convertESValueToMDSObject(long value, int type) {
      Object obj = null;

      try {
         switch (type) {
            case -1:
            case 7:
               break;
            case 0:
            default:
               obj = Convert.toBoolean(value) ? Boolean.TRUE : Boolean.FALSE;
               break;
            case 1:
               obj = new Integer(Convert.toInt32(value));
               break;
            case 2:
               obj = new Double(Convert.toDouble(value));
               break;
            case 3:
               obj = Convert.toString(value);
               break;
            case 4:
               Object dateObj = Convert.toObject(value);
               if (dateObj instanceof ESDate) {
                  obj = new Long((long)((ESDate)dateObj).getValue());
               }
               break;
            case 5:
               Object enumObj = Convert.toObject(value);
               if (enumObj instanceof ESEnum) {
                  obj = new Integer(((ESEnum)enumObj).getValue());
               }
               break;
            case 6:
               Object dataObj = Convert.toObject(value);
               if (dataObj instanceof ESData) {
                  ESData esData = (ESData)dataObj;
                  DataCollection collection = esData.getCollection();
                  if (collection instanceof KeyDataCollection) {
                     KeyDataCollection kc = (KeyDataCollection)collection;
                     obj = kc.getPKey(esData.getHandle());
                  }
               }
               break;
            case 8:
               obj = new Long((long)Convert.toDouble(value));
         }
      } finally {
         ;
      }

      return obj;
   }

   public final long convertMDSObjectToESValue(Object obj, long valueType) {
      return this.convertMDSObjectToESValue(obj, valueType, null);
   }
}
