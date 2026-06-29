package net.rim.wica.runtime.metadata.internal.util;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.common.metadata.component.DefaultValueDef;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.component.DataImpl;
import net.rim.wica.runtime.metadata.internal.component.KeylessDataCollection;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

public final class DataDecoder {
   private Wiclet _wiclet;
   private PersistenceListener _persistenceListener;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;

   public DataDecoder(Wiclet wiclet) {
      this._wiclet = wiclet;
      this._persistenceListener = (PersistenceListener)this._wiclet
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
         );
   }

   public final void decode(Component cmp, ReadableDataStream in) {
      ComponentDef def = cmp.getDef();
      DefaultValueDef defaults = def.getDefaultValues();
      int numFields = def.getNumFields();

      for (int i = 0; i < numFields; i++) {
         switch (def.getFieldType(i)) {
            case 0:
               cmp.setBooleanFieldValue(i, this.readBoolean(in, defaults, i));
               break;
            case 1:
            case 5:
               cmp.setIntFieldValue(i, this.readInt(in, defaults, i));
               break;
            case 2:
               cmp.setDoubleFieldValue(i, this.readDouble(in, defaults, i));
               break;
            case 3:
               cmp.setObjectFieldValue(i, this.readString(in, defaults, i));
               break;
            case 4:
            case 8:
               cmp.setLongFieldValue(i, this.readLong(in, defaults, i));
               break;
            case 6:
               boolean var10000;
               label38: {
                  label37: {
                     if (def.isPersistable()) {
                        if (!(cmp instanceof DataImpl)) {
                           break label37;
                        }

                        if (((DataImpl)cmp).isPersistable(i)) {
                           break label37;
                        }
                     }

                     var10000 = false;
                     break label38;
                  }

                  var10000 = true;
               }

               boolean persistable = var10000;
               cmp.setReferenceField(i, this.decode(def.getFieldReferenceType(i), in, persistable));
               break;
            case 32768:
               this.readBooleanArray(in, (IntVector)cmp.getObjectFieldValue(i));
               break;
            case 32769:
            case 32773:
               this.readIntArray(in, (IntVector)cmp.getObjectFieldValue(i));
               break;
            case 32770:
               this.readDoubleArray(in, (DoubleVector)cmp.getObjectFieldValue(i));
               break;
            case 32771:
               this.readStringArray(in, (Vector)cmp.getObjectFieldValue(i));
               break;
            case 32772:
            case 32776:
               this.readLongArray(in, (LongVector)cmp.getObjectFieldValue(i));
               break;
            case 32774:
               this.readDataArray(in, def.getFieldReferenceType(i), (LongVector)cmp.getObjectFieldValue(i), def.isPersistable());
               break;
            default:
               throw new RuntimeException("Unrecognized field type");
         }
      }
   }

   public final long decode(int defId, ReadableDataStream in, boolean persistableParent) {
      boolean cmpExists = in.startComponentRead();
      DataCollection dc = this._wiclet.getDataCollection(defId);
      DataComponentDef def = dc.getDef();
      DefaultValueDef defaults = def.getDefaultValues();
      long handle;
      if (def.hasKey()) {
         if (!cmpExists) {
            return -1;
         }

         persistableParent = def.isPersistable();
         handle = dc.create();
      } else {
         if (!(dc instanceof KeylessDataCollection)) {
            handle = dc.create();
         } else {
            handle = ((KeylessDataCollection)dc).create(persistableParent);
            this._persistenceListener.createdKeylessOnDecoding(handle);
         }

         if (!cmpExists) {
            return handle;
         }
      }

      int numFields = def.getNumFields();
      Object pkeyFromMessage = null;

      for (int i = 0; i < numFields; i++) {
         switch (def.getFieldType(i)) {
            case 0:
               dc.setBooleanFieldValue(handle, i, this.readBoolean(in, defaults, i));
               break;
            case 1:
            case 5:
               int value = this.readInt(in, defaults, i);
               dc.setIntFieldValue(handle, i, value);
               if (def.isBuiltinComponent() && def.hasKey() && def.getKeyFields()[0] == i) {
                  pkeyFromMessage = new Integer(value);
               }
               break;
            case 2:
               dc.setDoubleFieldValue(handle, i, this.readDouble(in, defaults, i));
               break;
            case 3:
               dc.setObjectFieldValue(handle, i, this.readString(in, defaults, i));
               break;
            case 4:
            case 8:
               dc.setLongFieldValue(handle, i, this.readLong(in, defaults, i));
               break;
            case 6:
               dc.setReferenceField(handle, i, this.decode(def.getFieldReferenceType(i), in, persistableParent));
               break;
            case 32768:
               this.readBooleanArray(in, (IntVector)dc.getObjectFieldValue(handle, i));
               break;
            case 32769:
            case 32773:
               this.readIntArray(in, (IntVector)dc.getObjectFieldValue(handle, i));
               break;
            case 32770:
               this.readDoubleArray(in, (DoubleVector)dc.getObjectFieldValue(handle, i));
               break;
            case 32771:
               this.readStringArray(in, (Vector)dc.getObjectFieldValue(handle, i));
               break;
            case 32772:
            case 32776:
               this.readLongArray(in, (LongVector)dc.getObjectFieldValue(handle, i));
               break;
            case 32774:
               this.readDataArray(in, def.getFieldReferenceType(i), (LongVector)dc.getObjectFieldValue(handle, i), persistableParent);
               break;
            default:
               throw new RuntimeException("Unrecognized field type");
         }
      }

      if (def.hasKey()) {
         KeyDataCollection kdc = (KeyDataCollection)dc;
         Object key = def.isBuiltinComponent() ? pkeyFromMessage : kdc.getPKey(handle);
         if (key == null) {
            throw new RuntimeException("Primary key of keyed component is null");
         }

         long update = kdc.find(key);
         if (update != -1) {
            dc.copyFields(update, handle, false);
            dc.remove(handle);
            handle = update;
         } else if (!def.isBuiltinComponent()) {
            kdc.restoreKey(handle, key);
         }
      }

      return handle;
   }

   private final int readInt(ReadableDataStream in, DefaultValueDef def, int field) {
      return def.hasDefaultValue(field) ? in.readInt(def.getIntDefaultValue(field)) : in.readInt();
   }

   private final boolean readBoolean(ReadableDataStream in, DefaultValueDef def, int field) {
      return def.hasDefaultValue(field) ? in.readBoolean(def.getBooleanDefaultValue(field)) : in.readBoolean();
   }

   private final long readLong(ReadableDataStream in, DefaultValueDef def, int field) {
      return def.hasDefaultValue(field) ? in.readLong(def.getLongDefaultValue(field)) : in.readLong();
   }

   private final double readDouble(ReadableDataStream in, DefaultValueDef def, int field) {
      return def.hasDefaultValue(field) ? in.readDouble(def.getDoubleDefaultValue(field)) : in.readDouble();
   }

   private final String readString(ReadableDataStream in, DefaultValueDef def, int field) {
      return def.hasDefaultValue(field) ? in.readString((String)def.getObjectDefaultValue(field)) : in.readString();
   }

   private final void readDataArray(ReadableDataStream in, int defId, LongVector dataArray, boolean persistableParent) {
      int size = in.startComponentArrayRead();
      dataArray.removeAllElements();
      dataArray.ensureCapacity(size);

      for (int i = 0; i < size; i++) {
         dataArray.addElement(this.decode(defId, in, persistableParent), false);
      }
   }

   private final void readStringArray(ReadableDataStream in, Vector values) {
      values.removeAllElements();
      String[] array = in.readStringArray();
      if (array != null) {
         int size = array.length;
         values.ensureCapacity(size);

         for (int i = 0; i < size; i++) {
            values.addElement(array[i]);
         }
      }
   }

   private final void readBooleanArray(ReadableDataStream in, IntVector values) {
      values.removeAllElements();
      boolean[] array = in.readBooleanArray();
      if (array != null) {
         int size = array.length;
         values.ensureCapacity(size);

         for (int i = 0; i < size; i++) {
            values.addElement(array[i] ? 1 : 0);
         }
      }
   }

   private final void readIntArray(ReadableDataStream in, IntVector values) {
      int[] array = in.readIntArray();
      if (array != null) {
         values.setSize(array.length);
         System.arraycopy(array, 0, values.getArray(), 0, array.length);
      } else {
         values.removeAllElements();
      }
   }

   private final void readLongArray(ReadableDataStream in, LongVector values) {
      long[] array = in.readLongArray();
      if (array != null) {
         values.setSize(array.length);
         System.arraycopy(array, 0, values.getArray(), 0, array.length);
      } else {
         values.removeAllElements();
      }
   }

   private final void readDoubleArray(ReadableDataStream in, DoubleVector values) {
      double[] array = in.readDoubleArray();
      if (array != null) {
         values.setSize(array.length);
         System.arraycopy(array, 0, values.getArray(), 0, array.length);
      } else {
         values.removeAllElements();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
