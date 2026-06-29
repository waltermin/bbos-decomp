package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESDate;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.WicaAppContext;
import net.rim.wica.runtime.script.internal.handler.CmpFieldHandler;
import net.rim.wica.runtime.script.internal.handler.DateFieldHandler;
import net.rim.wica.runtime.script.internal.handler.EnumFieldHandler;
import net.rim.wica.runtime.script.internal.handler.StringFieldHandler;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

public final class ESMDSArray extends RedirectedObject {
   private String _id;
   private Object _array;
   private DataCollection _collection;
   private int _type;
   private int _enumId;
   private UIControl _control;
   private WicaAppContext _context;

   public ESMDSArray(int type, Object array, DataCollection collection, String id, WicaAppContext context) {
      this(type, array, collection, id, -1, null, context);
   }

   public ESMDSArray(int type, Object array, DataCollection collection, String id, UIControl control, WicaAppContext context) {
      this(type, array, collection, id, -1, control, context);
   }

   public ESMDSArray(int type, Object array, DataCollection collection, String id, int enumId, UIControl control, WicaAppContext context) {
      super("MDSArray", context.getArrayPrototype());
      this._id = id;
      this._array = array;
      this._collection = collection;
      this._type = type;
      this._enumId = enumId;
      this._control = control;
      this._context = context;
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == "length" ? Value.makeLongValue(this.getSize()) : Value.DEFAULT;
   }

   @Override
   public final long requestElementValue(long element) {
      long objId = Value.DEFAULT;
      int index = (int)this.parseIndex(element);
      if (index == -1) {
         return objId;
      }

      if (this._collection != null) {
         return this._context.createDataInstance(this._collection, ((LongVector)this._array).elementAt(index));
      }

      if (this._enumId != -1) {
         return this._context.createEnumValue(((IntVector)this._array).elementAt(index), this._enumId);
      }

      switch (this._type) {
         case 32767:
         case 32773:
         case 32774:
         case 32775:
            return objId;
         case 32768:
         default:
            return Value.makeBooleanValue(((IntVector)this._array).elementAt(index) == 1);
         case 32769:
            return Value.makeIntegerValue(((IntVector)this._array).elementAt(index));
         case 32770:
            return Value.makeDoubleValue(((DoubleVector)this._array).elementAt(index));
         case 32771:
            String vStr = (String)((Vector)this._array).elementAt(index);
            objId = vStr == null ? Value.NULL : Value.makeStringValue(vStr);
            return objId;
         case 32772:
            return Value.makeObjectValue(new ESDate(((LongVector)this._array).elementAt(index)));
         case 32776:
            long theValue = ((LongVector)this._array).elementAt(index);
            return theValue == (int)theValue ? Value.makeIntegerValue((int)theValue) : Value.makeLongValue(theValue);
      }
   }

   @Override
   public final boolean notifyElementChanged(long element, long value) {
      int index = (int)this.parseIndex(element);
      if (index == -1) {
         return false;
      }

      switch (this._type) {
         case 32767:
         case 32775:
            break;
         case 32768:
         default:
            ((IntVector)this._array).setElementAt(Convert.toBoolean(value) ? 1 : 0, index);
            break;
         case 32769:
            ((IntVector)this._array).setElementAt(Convert.toInt32(value), index);
            break;
         case 32770:
            ((DoubleVector)this._array).setElementAt(Convert.toDouble(value), index);
            break;
         case 32771:
            ((Vector)this._array).setElementAt(StringFieldHandler.getValue(value), index);
            break;
         case 32772:
            ((LongVector)this._array).setElementAt(DateFieldHandler.getValue(value), index);
            break;
         case 32773:
            ((IntVector)this._array).setElementAt(EnumFieldHandler.getValue(this._enumId, value), index);
            break;
         case 32774:
            if (value != Value.NULL) {
               ((LongVector)this._array).setElementAt(CmpFieldHandler.getValue(this._collection.getDef().getId(), value), index);
            }
            break;
         case 32776:
            ((LongVector)this._array).setElementAt((long)Convert.toDouble(value), index);
      }

      if (this._control != null) {
         this._control.setValue(this._array, false);
      }

      return true;
   }

   @Override
   public final int notifyFieldDeleted(String name) {
      int index = (int)this.getIndexFromName(name);
      int op = 2;
      if (index != -1) {
         op = 0;
         switch (this._type) {
            case 32767:
            case 32775:
               break;
            case 32768:
            case 32769:
            case 32773:
            default:
               ((IntVector)this._array).removeElementAt(index);
               break;
            case 32770:
               ((DoubleVector)this._array).removeElementAt(index);
               break;
            case 32771:
               ((Vector)this._array).removeElementAt(index);
               break;
            case 32772:
            case 32774:
            case 32776:
               ((LongVector)this._array).removeElementAt(index);
         }

         if (this._control != null) {
            this._control.setValue(this._array, false);
         }
      }

      return op;
   }

   final WicaAppContext getContext() {
      return this._context;
   }

   public final String getId() {
      return this._id;
   }

   public final Object getValue() {
      return this._array;
   }

   public final int getType() {
      return this._type;
   }

   public final DataCollection getCollection() {
      return this._collection;
   }

   public final void setCollection(DataCollection collection) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getEnumId() {
      return this._enumId;
   }

   public final UIControl getControl() {
      return this._control;
   }

   final int getSize() {
      int size = 0;
      switch (this._type) {
         case 32768:
         case 32769:
         case 32773:
         default:
            return ((IntVector)this._array).size();
         case 32770:
            return ((DoubleVector)this._array).size();
         case 32771:
            size = ((Vector)this._array).size();
         case 32767:
         case 32775:
            return size;
         case 32772:
         case 32774:
         case 32776:
            return ((LongVector)this._array).size();
      }
   }

   final void removeDataElement(long value) {
      if (value != Value.NULL) {
         int vType = Value.getType(value);
         switch (vType) {
            case 0:
               ((LongVector)this._array).removeElementAt(Value.getIntegerValue(value));
               return;
            case 6:
               if (this._collection == null) {
                  EcmaUtilities.throwESError(this.getId(), RuntimeResources.getString(74));
               }

               try {
                  ((LongVector)this._array).removeElement(CmpFieldHandler.getValue(this._collection.getDef().getId(), value));
                  return;
               } finally {
                  EcmaUtilities.throwESError(this.getId(), RuntimeResources.getString(74));
                  return;
               }
            default:
               EcmaUtilities.throwESError(this.getId(), RuntimeResources.getString(74));
         }
      }
   }

   private final long parseIndex(long element) {
      return Value.getType(element) == 0 && this.isValidIndex(element) ? (int)element : this.getIndexFromName(Convert.toString(element));
   }

   private final boolean isValidIndex(long index) {
      return index >= 0 && index < this.getSize();
   }

   private final long getIndexFromName(String name) {
      try {
         long index = Long.parseLong(name);
         if (name.charAt(0) == '0' && name.length() != 1) {
            return -1;
         } else {
            return !this.isValidIndex(index) ? -1 : index;
         }
      } finally {
         return -1;
      }
   }
}
