package net.rim.wica.runtime.metadata.util;

import java.util.Date;
import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;

public final class SerializationUtilities {
   public static final String serializeObject(String name, Wiclet definition, DataCollection collection, long handle) {
      StringBuffer b = new StringBuffer();
      serializeObject(b, name, definition, collection, handle);
      return b.toString();
   }

   public static final String serializeCollection(String name, Wiclet definition, KeyDataCollection collection) {
      StringBuffer b = new StringBuffer();
      b.append("<collection");
      if (name != null) {
         b.append(" name=\"");
         writeEscaped(b, name);
         b.append('"');
      }

      long[] handles = collection.retrieveAll(false);
      int size = handles.length;
      b.append(" size=\"");
      b.append(size);
      if (size == 0) {
         b.append("\"/>");
      } else {
         b.append("\">\n");

         for (int i = 0; i < size; i++) {
            serializeObject(b, null, definition, collection, handles[i]);
            b.append('\n');
         }

         b.append("</collection>");
      }

      return b.toString();
   }

   private static final void serializeProperties(StringBuffer buffer, Wiclet definition, DataCollection collection, long handle) {
      ComponentDef def = collection.getDef();
      int size = def.getNumFields();

      for (int index = 0; index < size; index++) {
         int type = def.getFieldType(index);
         switch (type) {
            case 0:
               buffer.append("<boolean>");
               buffer.append(collection.getBooleanFieldValue(handle, index));
               buffer.append("</boolean>");
               break;
            case 1:
               buffer.append("<integer>");
               buffer.append(collection.getIntFieldValue(handle, index));
               buffer.append("</integer>");
               break;
            case 2:
               buffer.append("<decimal>");
               buffer.append(collection.getDoubleFieldValue(handle, index));
               buffer.append("</decimal>");
               break;
            case 3:
               buffer.append("<string");
               String value = (String)collection.getObjectFieldValue(handle, index);
               if (value != null) {
                  buffer.append('>');
                  writeEscaped(buffer, value);
                  buffer.append("</string>");
               } else {
                  buffer.append("/>");
               }
               break;
            case 4:
               buffer.append("<date>");
               writeDateValue(buffer, collection.getLongFieldValue(handle, index));
               buffer.append("</date>");
               break;
            case 5:
               buffer.append("<enum>");
               int enumValue = collection.getIntFieldValue(handle, index);
               writeEnumValue(buffer, definition, def.getFieldReferenceType(index), enumValue);
               buffer.append("</enum>");
               break;
            case 6:
               serializeObject(buffer, null, definition, collection.getReferenceField(handle, index));
               break;
            case 8:
               buffer.append("<long>");
               buffer.append(collection.getLongFieldValue(handle, index));
               buffer.append("</long>");
               break;
            case 32768:
               serializeBooleanArray(buffer, (IntVector)collection.getObjectFieldValue(handle, index));
               break;
            case 32769:
               serializeIntArray(buffer, (IntVector)collection.getObjectFieldValue(handle, index));
               break;
            case 32770:
               serializeDoubleArray(buffer, (DoubleVector)collection.getObjectFieldValue(handle, index));
               break;
            case 32771:
               serializeStringArray(buffer, (Vector)collection.getObjectFieldValue(handle, index));
               break;
            case 32772:
               serializeDateArray(buffer, (LongVector)collection.getObjectFieldValue(handle, index));
               break;
            case 32773:
               serializeEnumArray(buffer, definition, def.getFieldReferenceType(index), (IntVector)collection.getObjectFieldValue(handle, index));
               break;
            case 32774:
               serializeObjectArray(
                  buffer, definition, definition.getDataCollection(def.getFieldReferenceType(index)), (LongVector)collection.getObjectFieldValue(handle, index)
               );
               break;
            case 32776:
               serializeLongArray(buffer, (LongVector)collection.getObjectFieldValue(handle, index));
         }

         buffer.append('\n');
      }
   }

   private static final void serializeObject(StringBuffer buffer, String name, Wiclet definition, DataCollection collection, long handle) {
      if (handle != -1 && collection.contains(handle)) {
         buffer.append("<object");
         if (name != null) {
            buffer.append(" name=\"");
            writeEscaped(buffer, name);
            buffer.append('"');
         }

         buffer.append(">\n");
         serializeProperties(buffer, definition, collection, handle);
         buffer.append("</object>");
      } else {
         buffer.append("<object/>");
      }
   }

   private static final void serializeObject(StringBuffer buffer, String name, Wiclet definition, long handle) {
      serializeObject(buffer, name, definition, definition.getDataCollection((int)(handle >> 32)), handle);
   }

   public static final void serializeBooleanArray(StringBuffer buffer, IntVector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            buffer.append("<boolean>");
            buffer.append(dataArray.elementAt(i) == 1);
            buffer.append("</boolean>\n");
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeDateArray(StringBuffer buffer, LongVector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            buffer.append("<date>");
            writeDateValue(buffer, dataArray.elementAt(i));
            buffer.append("</date>\n");
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeDoubleArray(StringBuffer buffer, DoubleVector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            buffer.append("<decimal>");
            buffer.append(dataArray.elementAt(i));
            buffer.append("</decimal>\n");
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeEnumArray(StringBuffer buffer, Wiclet definition, int definitionId, IntVector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            buffer.append("<enum>");
            writeEnumValue(buffer, definition, definitionId, dataArray.elementAt(i));
            buffer.append("</enum>\n");
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeIntArray(StringBuffer buffer, IntVector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            buffer.append("<integer>");
            buffer.append(dataArray.elementAt(i));
            buffer.append("</integer>\n");
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeLongArray(StringBuffer buffer, LongVector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            buffer.append("<long>");
            buffer.append(dataArray.elementAt(i));
            buffer.append("</long>\n");
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeStringArray(StringBuffer buffer, Vector dataArray) {
      int size = dataArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            String value = (String)dataArray.elementAt(i);
            if (value == null) {
               buffer.append("<string/>");
            } else {
               buffer.append("<string>");
               buffer.append(value);
               buffer.append("</string>\n");
            }
         }

         buffer.append("</array>");
      }
   }

   public static final void serializeObjectArray(StringBuffer buffer, Wiclet definition, DataCollection collection, LongVector objectArray) {
      int size = objectArray.size();
      buffer.append("<array size=\"");
      buffer.append(size);
      if (size == 0) {
         buffer.append("\"/>");
      } else {
         buffer.append("\">\n");

         for (int i = 0; i < size; i++) {
            serializeObject(buffer, null, definition, collection, objectArray.elementAt(i));
            buffer.append("\n");
         }

         buffer.append("</array>");
      }
   }

   private static final void writeEnumValue(StringBuffer buffer, Wiclet definition, int definitionId, int value) {
      buffer.append(definition.getEnums().getEnum(definitionId)[value]);
   }

   private static final void writeDateValue(StringBuffer buffer, long date) {
      buffer.append(new Date(date));
   }

   private static final void writeEscaped(StringBuffer buffer, String text) {
      int length = text.length();
      buffer.ensureCapacity(buffer.length() + length);

      for (int i = 0; i < length; i++) {
         char c = text.charAt(i);
         switch (c) {
            case '\t':
               buffer.append("&#9;");
               break;
            case '\r':
               buffer.append("&#13;");
               break;
            case '"':
               buffer.append("&quot;");
               break;
            case '&':
               buffer.append("&amp;");
               break;
            case '\'':
               buffer.append("&apos;");
               break;
            case '<':
               buffer.append("&lt;");
               break;
            case '>':
               buffer.append("&gt;");
               break;
            default:
               buffer.append(c);
         }
      }
   }
}
