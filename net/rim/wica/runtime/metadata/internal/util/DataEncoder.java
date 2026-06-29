package net.rim.wica.runtime.metadata.internal.util;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.DefaultValueDef;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

public final class DataEncoder {
   private Wiclet _wiclet;

   public DataEncoder(Wiclet wiclet) {
      this._wiclet = wiclet;
   }

   public final void encode(Component cmp, WritableDataStream out) {
      ComponentDef def = cmp.getDef();
      DefaultValueDef defaults = def.getDefaultValues();
      int numFields = def.getNumFields();

      for (int i = 0; i < numFields; i++) {
         switch (def.getFieldType(i)) {
            case 0:
               this.writeBoolean(out, cmp.getBooleanFieldValue(i), defaults, i);
               break;
            case 1:
            case 5:
               this.writeInt(out, cmp.getIntFieldValue(i), defaults, i);
               break;
            case 2:
               this.writeDouble(out, cmp.getDoubleFieldValue(i), defaults, i);
               break;
            case 3:
               this.writeString(out, (String)cmp.getObjectFieldValue(i), defaults, i);
               break;
            case 4:
            case 8:
               this.writeLong(out, cmp.getLongFieldValue(i), defaults, i);
               break;
            case 6:
               this.encode(cmp.getExistingReferenceField(i), out);
               break;
            case 32768:
               this.writeBooleanArray(out, (IntVector)cmp.getObjectFieldValue(i));
               break;
            case 32769:
            case 32773:
               out.writeIntArray(((IntVector)cmp.getObjectFieldValue(i)).toArray());
               break;
            case 32770:
               out.writeDoubleArray(((DoubleVector)cmp.getObjectFieldValue(i)).toArray());
               break;
            case 32771:
               this.writeStringArray(out, (Vector)cmp.getObjectFieldValue(i));
               break;
            case 32772:
            case 32776:
               out.writeLongArray(((LongVector)cmp.getObjectFieldValue(i)).toArray());
               break;
            case 32774:
               this.writeDataArray(out, (LongVector)cmp.getObjectFieldValue(i));
               break;
            default:
               throw new RuntimeException("Not recognized type");
         }
      }
   }

   public final void encode(long handle, WritableDataStream out) {
      out.startComponentWrite(handle == -1);
      if (handle != -1) {
         DataCollection dc = this._wiclet.getDataCollection((int)(handle >> 32));
         ComponentDef def = dc.getDef();
         DefaultValueDef defaults = def.getDefaultValues();
         int numFields = def.getNumFields();

         for (int i = 0; i < numFields; i++) {
            switch (def.getFieldType(i)) {
               case 0:
                  this.writeBoolean(out, dc.getBooleanFieldValue(handle, i), defaults, i);
                  break;
               case 1:
               case 5:
                  this.writeInt(out, dc.getIntFieldValue(handle, i), defaults, i);
                  break;
               case 2:
                  this.writeDouble(out, dc.getDoubleFieldValue(handle, i), defaults, i);
                  break;
               case 3:
                  this.writeString(out, (String)dc.getObjectFieldValue(handle, i), defaults, i);
                  break;
               case 4:
               case 8:
                  this.writeLong(out, dc.getLongFieldValue(handle, i), defaults, i);
                  break;
               case 6:
                  this.encode(dc.getReferenceField(handle, i), out);
                  break;
               case 32768:
                  this.writeBooleanArray(out, (IntVector)dc.getObjectFieldValue(handle, i));
                  break;
               case 32769:
               case 32773:
                  out.writeIntArray(((IntVector)dc.getObjectFieldValue(handle, i)).toArray());
                  break;
               case 32770:
                  out.writeDoubleArray(((DoubleVector)dc.getObjectFieldValue(handle, i)).toArray());
                  break;
               case 32771:
                  this.writeStringArray(out, (Vector)dc.getObjectFieldValue(handle, i));
                  break;
               case 32772:
               case 32776:
                  out.writeLongArray(((LongVector)dc.getObjectFieldValue(handle, i)).toArray());
                  break;
               case 32774:
                  this.writeDataArray(out, (LongVector)dc.getObjectFieldValue(handle, i));
                  break;
               default:
                  throw new RuntimeException("Unrecognized field type");
            }
         }
      }
   }

   private final void writeInt(WritableDataStream out, int value, DefaultValueDef def, int field) {
      if (def.hasDefaultValue(field)) {
         if (value == def.getIntDefaultValue(field)) {
            out.noteDefaultField(true);
         } else {
            out.noteDefaultField(false);
            out.writeInt(value);
         }
      } else {
         out.writeInt(value);
      }
   }

   private final void writeBoolean(WritableDataStream out, boolean value, DefaultValueDef def, int field) {
      if (def.hasDefaultValue(field)) {
         if (value == def.getBooleanDefaultValue(field)) {
            out.noteDefaultField(true);
         } else {
            out.noteDefaultField(false);
            out.writeBoolean(value);
         }
      } else {
         out.writeBoolean(value);
      }
   }

   private final void writeLong(WritableDataStream out, long value, DefaultValueDef def, int field) {
      if (def.hasDefaultValue(field)) {
         if (value == def.getLongDefaultValue(field)) {
            out.noteDefaultField(true);
         } else {
            out.noteDefaultField(false);
            out.writeLong(value);
         }
      } else {
         out.writeLong(value);
      }
   }

   private final void writeDouble(WritableDataStream out, double value, DefaultValueDef def, int field) {
      if (def.hasDefaultValue(field)) {
         if (value == def.getDoubleDefaultValue(field)) {
            out.noteDefaultField(true);
         } else {
            out.noteDefaultField(false);
            out.writeDouble(value);
         }
      } else {
         out.writeDouble(value);
      }
   }

   private final void writeString(WritableDataStream out, String value, DefaultValueDef def, int field) {
      if (def.hasDefaultValue(field)) {
         if (def.getObjectDefaultValue(field).equals(value)) {
            out.noteDefaultField(true);
         } else {
            out.noteDefaultField(false);
            out.writeString(value);
         }
      } else {
         out.writeString(value);
      }
   }

   private final void writeDataArray(WritableDataStream out, LongVector dataArray) {
      int size = dataArray.size();
      out.startComponentArrayWrite(size);

      for (int i = 0; i < size; i++) {
         this.encode(dataArray.elementAt(i), out);
      }
   }

   private final void writeStringArray(WritableDataStream out, Vector strings) {
      String[] objects = new String[strings.size()];
      strings.copyInto(objects);
      out.writeStringArray(objects);
   }

   private final void writeBooleanArray(WritableDataStream out, IntVector booleans) {
      int size = booleans.size();
      boolean[] value = new boolean[size];

      for (int i = 0; i < size; i++) {
         value[i] = booleans.elementAt(i) == 1;
      }

      out.writeBooleanArray(value);
   }
}
