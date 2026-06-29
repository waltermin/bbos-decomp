package net.rim.device.api.xml.jaxrpc;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.Type;

public final class SoapMessageEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void encode(OutputStream stream, Element element, Object value, String encoding, boolean compress, SoapWbxmlCodebook codebook) {
      try {
         Writer writer = new OutputStreamWriter(stream, encoding);
         writer.write(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
         );
         if (element != null) {
            String namespace = element.name.getNamespaceURI();
            writer.write(" xmlns:tns=\"" + namespace + "\">\n");
            writeStartTag(writer, "soapenv:Body", false, true);
            encode(writer, namespace, element, value);
            writeEndTag(writer, "soapenv:Body");
         } else {
            writer.write(">\n");
            writer.write("<soapenv:Body/>\n");
         }

         writeEndTag(writer, "soapenv:Envelope");
      } catch (Throwable var9) {
         JAXRPCUtil.handleError(e.getMessage());
         return;
      }
   }

   private static final void encode(Writer writer, String defaultNamespace, Element parent, Object value) {
      String id = null;
      boolean isDefaultNS = true;
      if (parent.name.getNamespaceURI().equals(defaultNamespace)) {
         id = "tns:" + parent.name.getLocalPart();
      } else {
         isDefaultNS = false;
         id = parent.name.getLocalPart() + " xmlns=\"" + parent.name.getNamespaceURI() + "\"";
      }

      if (value == null) {
         if (parent.isNillable && !parent.isArray) {
            writeStartTag(writer, id, true, true);
         } else {
            if (!parent.isOptional) {
               JAXRPCUtil.handleError("parameter value cannot be null and minOccurs is non-zero: {0}.", parent.name.getLocalPart());
            }
         }
      } else {
         int count = 1;
         Object[] values = new Object[]{value};
         if (parent.isArray) {
            boolean isPrimitiveArray = checkPrimitiveArray(writer, id, parent.contentType, value);
            if (value instanceof Object[]) {
               values = (Object[])value;
               count = values.length;
               checkProperRange(parent, values.length);
            } else {
               if (isPrimitiveArray) {
                  return;
               }

               JAXRPCUtil.handleError("type mismatch: the encoding parameter must be an array: {0}.", parent.name.getLocalPart());
            }
         }

         for (int i = 0; i < count; i++) {
            if (values[i] == null) {
               if (!parent.isNillable) {
                  JAXRPCUtil.handleError("null value for non-nillable element: {0}.", parent.name.getLocalPart());
               }

               writeStartTag(writer, id, true, true);
            } else {
               writeStartTag(writer, id, false, false);
               if (parent.contentType.value < 8) {
                  encodeSimpleType(writer, parent.contentType, values[i]);
               } else if (parent.contentType.value == 8) {
                  if (values[i] instanceof Object[]) {
                     writer.write(10);
                     encodeComplexType(writer, defaultNamespace, (ComplexType)parent.contentType, (Object[])values[i]);
                  } else {
                     JAXRPCUtil.handleError("the value for an element of type complex must be an Object array.");
                  }
               } else {
                  JAXRPCUtil.handleError("the description of " + parent.name.getLocalPart() + " is invalid.");
               }

               if (isDefaultNS) {
                  writeEndTag(writer, id);
               } else {
                  writeEndTag(writer, parent.name.getLocalPart());
               }
            }
         }
      }
   }

   private static final void encodeComplexType(Writer writer, String defaultNamespace, ComplexType complexType, Object[] values) {
      Element[] elements = complexType.elements;
      int length = elements.length;
      if (length != values.length) {
         JAXRPCUtil.handleError("wrong number of values passed to a complex type.");
      }

      for (int i = 0; i < length; i++) {
         encode(writer, defaultNamespace, elements[i], values[i]);
      }
   }

   private static final void encodeSimpleType(Writer writer, Type simpleType, Object value) {
      if (!isSimpleType(simpleType, value)) {
         JAXRPCUtil.handleError("simple type mismatch.");
      }

      byte v = (byte)simpleType.value;
      String s = value.toString();
      if (v < 5) {
         writer.write(s);
      } else if (v != 5 && v != 6) {
         replaceEscapingCharacters(writer, s);
      } else if (s.equals("-Infinity")) {
         writer.write("-INF");
      } else if (s.equals("Infinity")) {
         writer.write("INF");
      } else {
         writer.write(s);
      }
   }

   private static final boolean checkPrimitiveArray(Writer writer, String id, Type type, Object value) {
      boolean result = false;
      switch (type.value) {
         case -1:
            break;
         case 0:
         default:
            result = value instanceof boolean[];
            if (result) {
               boolean[] values = (boolean[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
            break;
         case 1:
            result = value instanceof byte[];
            if (result) {
               byte[] values = (byte[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
            break;
         case 2:
            result = value instanceof short[];
            if (result) {
               short[] values = (short[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
            break;
         case 3:
            result = value instanceof int[];
            if (result) {
               int[] values = (int[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
            break;
         case 4:
            result = value instanceof long[];
            if (result) {
               long[] values = (long[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
            break;
         case 5:
            result = value instanceof float[];
            if (result) {
               float[] values = (float[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
            break;
         case 6:
            result = value instanceof double[];
            if (result) {
               double[] values = (double[])value;
               int length = values.length;

               for (int i = 0; i < length; i++) {
                  writeTag(writer, id, String.valueOf(values[i]));
               }
            }
      }

      return result;
   }

   private static final boolean isSimpleType(Type type, Object value) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static final void checkProperRange(Element element, int length) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static final void writeEndTag(Writer writer, String qName) {
      StringBuffer b = new StringBuffer("</");
      b.append(qName);
      b.append(">\n");
      writer.write(b.toString());
   }

   private static final void replaceEscapingCharacters(Writer writer, String input) {
      int length = input.length();

      for (int i = 0; i < length; i++) {
         char c = input.charAt(i);
         switch (c) {
            case '"':
               writer.write("&quot;");
               break;
            case '&':
               writer.write("&amp;");
               break;
            case '\'':
               writer.write("&apos;");
               break;
            case '<':
               writer.write("&lt;");
               break;
            case '>':
               writer.write("&gt;");
               break;
            default:
               writer.write(c);
         }
      }
   }

   private static final void writeStartTag(Writer writer, String qName, boolean nil, boolean newLine) {
      StringBuffer b = new StringBuffer("<");
      b.append(qName);
      if (nil) {
         b.append(" xsi:nil=\"true\"/>");
      } else {
         b.append('>');
      }

      if (newLine) {
         b.append('\n');
      }

      writer.write(b.toString());
   }

   private static final void writeTag(Writer writer, String qName, String value) {
      StringBuffer b = new StringBuffer("<");
      b.append(qName);
      b.append('>');
      b.append(value);
      b.append("</");
      b.append(qName);
      b.append(">\n");
      writer.write(b.toString());
   }
}
