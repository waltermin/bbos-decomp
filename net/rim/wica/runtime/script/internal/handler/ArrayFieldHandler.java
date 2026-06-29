package net.rim.wica.runtime.script.internal.handler;

import java.util.Vector;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESArray;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.WicaAppContext;
import net.rim.wica.runtime.script.internal.appenv.ESMDSArray;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.runtime.util.Util;

public final class ArrayFieldHandler implements PropertyHandler {
   private int _type;
   private WicaAppContext _context;

   protected ArrayFieldHandler(int type, WicaAppContext context) {
      this._type = type;
      this._context = context;
   }

   @Override
   public final long getProperty(Component component, String name) {
      int id = component.getDef().getFieldHandle(name);
      return this.getESValue(component.getDef(), id, this._type, component.getObjectFieldValue(id));
   }

   @Override
   public final long getProperty(DataCollection collection, long handle, String name) {
      int id = collection.getDef().getFieldHandle(name);
      return this.getESValue(collection.getDef(), id, this._type, collection.getObjectFieldValue(handle, id));
   }

   @Override
   public final void putProperty(Component component, String name, long value) {
      int id = component.getDef().getFieldHandle(name);
      component.setObjectFieldValue(id, this.getValue(component.getDef(), id, this._type, value));
   }

   @Override
   public final void putProperty(DataCollection collection, long handle, String name, long value) {
      int id = collection.getDef().getFieldHandle(name);
      collection.setObjectFieldValue(handle, id, this.getValue(collection.getDef(), id, this._type, value));
   }

   private final Object getValue(ComponentDef cDef, int fieldID, int arrayType, long esObject) {
      if (esObject == Value.NULL) {
         return null;
      }

      Object arrayVal = null;
      ESObject eso = Convert.toObject(esObject);
      if (!(eso instanceof Object)) {
         if (eso instanceof ESMDSArray) {
            arrayVal = this.validateAssignment(arrayType, cDef, fieldID, (ESMDSArray)eso);
            if (arrayVal == null) {
               throw ThrownValue.typeError(RuntimeResources.getString(151));
            }
         } else if (eso != null) {
            throw ThrownValue.typeError(RuntimeResources.getString(116));
         }
      } else {
         ESArray array = (ESArray)eso;
         long size = array.getArrayLength();
         arrayVal = this.initArray(arrayType, (int)size);
         switch (arrayType) {
            case 32767:
            case 32775:
               break;
            case 32768:
            default:
               IntVector boolArrayVal = (IntVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  boolArrayVal.addElement(Convert.toBoolean(array.getIndex(i)) ? 1 : 0);
               }
               break;
            case 32769:
               IntVector intArrayVal = (IntVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  intArrayVal.addElement(Convert.toInt32(array.getIndex(i)));
               }
               break;
            case 32770:
               DoubleVector doubleArrayVal = (DoubleVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  doubleArrayVal.addElement(Convert.toDouble(array.getIndex(i)));
               }
               break;
            case 32771:
               Vector strArrayVal = (Vector)arrayVal;

               for (int i = 0; i < size; i++) {
                  strArrayVal.addElement(StringFieldHandler.getValue(array.getIndex(i)));
               }
               break;
            case 32772:
               LongVector dateArrayVal = (LongVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  dateArrayVal.addElement(DateFieldHandler.getValue(array.getIndex(i)));
               }
               break;
            case 32773:
               IntVector enumArrayVal = (IntVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  enumArrayVal.addElement(EnumFieldHandler.getValue(cDef.getFieldReferenceType(fieldID), array.getIndex(i)));
               }
               break;
            case 32774:
               LongVector dataArrayVal = (LongVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  dataArrayVal.addElement(CmpFieldHandler.getValue(cDef.getFieldReferenceType(fieldID), array.getIndex(i)));
               }
               break;
            case 32776:
               LongVector longArrayVal = (LongVector)arrayVal;

               for (int i = 0; i < size; i++) {
                  longArrayVal.addElement((long)Convert.toDouble(array.getIndex(i)));
               }
         }
      }

      return arrayVal;
   }

   private final Object validateAssignment(int desType, ComponentDef cDef, int fieldID, ESMDSArray eso) {
      Object result = null;
      Object arrayVal = eso.getValue();
      int sourceType = eso.getType();
      switch (desType) {
         case 32767:
         case 32775:
            break;
         case 32768:
         default:
            switch (sourceType) {
               case 32767:
               case 32769:
               case 32770:
                  return result;
               case 32768:
               default:
                  return arrayVal;
               case 32771:
                  Vector source = (Vector)arrayVal;
                  int len = source.size();
                  IntVector destination = (IntVector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(BooleanFieldHandler.string2Boolean((String)source.elementAt(i)) ? 1 : 0);
                  }

                  return destination;
            }
         case 32769:
            switch (sourceType) {
               case 32768:
               case 32770:
                  return result;
               case 32769:
               default:
                  return arrayVal;
               case 32771:
                  Vector source = (Vector)arrayVal;
                  int len = source.size();
                  IntVector destination = (IntVector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(IntegerFieldHandler.string2Int((String)source.elementAt(i)));
                  }

                  return destination;
            }
         case 32770:
            switch (sourceType) {
               case 32768:
                  return result;
               case 32769:
                  IntVector source = (IntVector)arrayVal;
                  int len = source.size();
                  DoubleVector destination = new DoubleVector(len);

                  for (int i = 0; i < len; i++) {
                     destination.addElement(source.elementAt(i));
                  }

                  return destination;
               case 32770:
               default:
                  return arrayVal;
               case 32771:
                  Vector source = (Vector)arrayVal;
                  int len = source.size();
                  DoubleVector destination = new DoubleVector(len);

                  for (int i = 0; i < len; i++) {
                     destination.addElement(DecimalFieldHandler.string2Double((String)source.elementAt(i)));
                  }

                  return destination;
            }
         case 32771:
            switch (sourceType) {
               case 32767:
               case 32774:
               case 32775:
                  return result;
               case 32768:
                  IntVector source = (IntVector)arrayVal;
                  int len = source.size();
                  Vector destination = (Vector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(source.elementAt(i) == 0 ? "false" : "true");
                  }

                  return destination;
               case 32769:
                  IntVector source = (IntVector)arrayVal;
                  int len = source.size();
                  Vector destination = (Vector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(String.valueOf(source.elementAt(i)));
                  }

                  return destination;
               case 32770:
                  DoubleVector source = (DoubleVector)arrayVal;
                  int len = source.size();
                  Vector destination = (Vector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(String.valueOf(source.elementAt(i)));
                  }

                  return destination;
               case 32771:
               default:
                  return arrayVal;
               case 32772:
                  LongVector source = (LongVector)arrayVal;
                  int len = source.size();
                  Vector destination = (Vector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(Util.DEFAULT_DATE_FORMATTER.format(new Object(source.elementAt(i))));
                  }

                  return destination;
               case 32773:
                  IntVector source = (IntVector)arrayVal;
                  int len = source.size();
                  Vector destination = (Vector)(new Object(len));
                  EnumCollection enumC = this._context.getWiclet().getEnums();
                  int enumDefID = eso.getEnumId();

                  for (int i = 0; i < len; i++) {
                     destination.addElement(enumC.getEnum(enumDefID)[source.elementAt(i)]);
                  }

                  return result;
               case 32776:
                  LongVector source = (LongVector)arrayVal;
                  int len = source.size();
                  Vector destination = (Vector)(new Object(len));

                  for (int i = 0; i < len; i++) {
                     destination.addElement(String.valueOf(source.elementAt(i)));
                  }

                  return destination;
            }
         case 32772:
            switch (sourceType) {
               case 32770:
                  return result;
               case 32771:
                  Vector source = (Vector)arrayVal;
                  int len = source.size();
                  LongVector destination = new LongVector(len);

                  for (int i = 0; i < len; i++) {
                     destination.addElement(HttpDateParser.parse((String)source.elementAt(i)));
                  }

                  return result;
               case 32772:
               default:
                  return arrayVal;
            }
         case 32773:
            if (sourceType == 32773) {
               EnumFieldHandler.validateEnumValue(cDef.getFieldReferenceType(fieldID), eso.getEnumId());
               result = arrayVal;
            }
            break;
         case 32774:
            if (sourceType == 32774) {
               CmpFieldHandler.validateDataValue(cDef.getFieldReferenceType(fieldID), eso.getCollection().getDef().getId());
               result = arrayVal;
            }
            break;
         case 32776:
            switch (sourceType) {
               case 32768:
               case 32772:
               case 32773:
               case 32774:
               case 32775:
                  break;
               case 32769:
                  IntVector source = (IntVector)arrayVal;
                  int len = source.size();
                  LongVector destination = new LongVector(len);

                  for (int i = 0; i < len; i++) {
                     destination.addElement(source.elementAt(i));
                  }

                  result = destination;
                  break;
               case 32770:
                  DoubleVector source = (DoubleVector)arrayVal;
                  int len = source.size();
                  LongVector destination = new LongVector(len);

                  for (int i = 0; i < len; i++) {
                     destination.addElement((long)source.elementAt(i));
                  }

                  result = destination;
                  break;
               case 32771:
                  Vector source = (Vector)arrayVal;
                  int len = source.size();
                  LongVector destination = new LongVector(len);

                  for (int i = 0; i < len; i++) {
                     destination.addElement(LongFieldHandler.string2Long((String)source.elementAt(i)));
                  }

                  result = destination;
                  break;
               case 32776:
               default:
                  result = arrayVal;
            }
      }

      return result;
   }

   private final long getESValue(ComponentDef cDef, int fieldID, int arrayType, Object v) {
      long objId = Value.NULL;
      if (v != null) {
         ESMDSArray wrapper = new ESMDSArray(
            arrayType,
            v,
            arrayType == 32774 ? this._context.getWiclet().getDataCollection(cDef.getFieldReferenceType(fieldID)) : null,
            "Object[]",
            arrayType == 32773 ? cDef.getFieldReferenceType(fieldID) : -1,
            null,
            this._context
         );
         objId = Value.makeObjectValue(wrapper);
      }

      return objId;
   }

   private final Object initArray(int arrayType, int size) {
      Object array = null;
      switch (arrayType) {
         case 32768:
         case 32769:
         case 32773:
         default:
            return new Object(size);
         case 32770:
            return new DoubleVector(size);
         case 32771:
            array = new Object(size);
         case 32767:
         case 32775:
            return array;
         case 32772:
         case 32774:
         case 32776:
            return new LongVector(size);
      }
   }
}
