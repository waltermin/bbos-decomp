package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;
import net.rim.device.internal.i18n.UnicodeServiceRegistry;

final class AddressCardConverter extends BaseConverter {
   static String ADDRESS_BOOK_MIME_TYPE = "application/x-rimdeviceaddress book";
   static String CASE_SENSITIVE_OUTBOUND_ADDRESS_BOOK_MIME_TYPE = "Application/X-rimdeviceAddress Book";
   static String ADDRESS_BOOK_ABBREVIATED_MIME_TYPE = "Address Book";
   static String EMAIL_ATTACHMENT_ADDRESS_CARD_HEADER = "";

   @Override
   public final boolean canConvert(Object parameters) {
      if (parameters instanceof String) {
         String string = (String)parameters;
         if (StringUtilities.startsWithIgnoreCase(string, "application/x-rimdeviceaddress book") || string.equals(ADDRESS_BOOK_ABBREVIATED_MIME_TYPE)) {
            return true;
         }
      }

      if (!(parameters instanceof Parameters)) {
         return false;
      }

      Parameters cmimeParameters = (Parameters)parameters;
      String type = CMIMEContentType.getBaseType(cmimeParameters.getFirst((byte)1));
      return this.canConvert(type);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object convert(byte[] inputBytes, Object context) throws SerializationException {
      ContextObject contextObject = new ContextObject(11, 43, 54);
      int length = inputBytes.length;
      if (length < 5) {
         throw new SerializationException();
      }

      if (inputBytes[length - 5] == 13 && inputBytes[length - 4] == 13) {
         length -= 4;
      } else if (inputBytes[length - 4] == 13 && inputBytes[length - 3] == 13) {
         length -= 3;
      }

      Vector strings = new Vector();
      int startOfLabel = 0;
      int endOfLabel = 0;
      int startOfValue = 0;
      String encoding = null;
      String uTag = "u_";

      for (int i = 0; i < length; i++) {
         switch (inputBytes[i]) {
            case 9:
               startOfValue = i + 1;
               break;
            case 13:
               if (startOfLabel == 0 && endOfLabel == 0) {
                  startOfLabel = i + 1;
               } else if (startOfLabel != i) {
                  if (inputBytes[startOfLabel] == 9 && strings.size() > 1) {
                     String value = null;
                     String label = (String)strings.elementAt(strings.size() - 2);
                     String previous = (String)strings.elementAt(strings.size() - 1);
                     int labelLen = label.length();
                     if (labelLen > 2 && label.startsWith(uTag)) {
                        encoding = "UTF-8\r";
                     } else {
                        encoding = "windows-1252\r";
                     }

                     boolean var22 = false /* VF: Semaphore variable */;

                     label250:
                     try {
                        var22 = true;
                        value = new String(inputBytes, startOfValue, i - startOfValue, encoding);
                        var22 = false;
                     } finally {
                        if (var22) {
                           value = new String(inputBytes, startOfValue, i - startOfValue);
                           break label250;
                        }
                     }

                     previous = previous + '\n' + value;
                     strings.setElementAt(previous, strings.size() - 1);
                     startOfLabel = i + 1;
                     startOfValue = 0;
                  } else {
                     int labelLen = endOfLabel - 1 - startOfLabel;
                     if (labelLen > 0) {
                        String label = null;
                        String value = null;
                        if (labelLen > 2 && inputBytes[startOfLabel] == 117 && inputBytes[startOfLabel + 1] == 95) {
                           encoding = "UTF-8\r";
                        } else {
                           encoding = "windows-1252\r";
                        }

                        boolean var19 = false /* VF: Semaphore variable */;

                        label238:
                        try {
                           var19 = true;
                           label = new String(inputBytes, startOfLabel, labelLen, encoding);
                           value = new String(inputBytes, startOfValue, i - startOfValue, encoding);
                           var19 = false;
                        } finally {
                           if (var19) {
                              label = new String(inputBytes, startOfLabel, labelLen);
                              value = new String(inputBytes, startOfValue, i - startOfValue);
                              break label238;
                           }
                        }

                        if (label != null && value != null) {
                           strings.addElement(label);
                           strings.addElement(value);
                        }
                     }

                     startOfLabel = i + 1;
                     startOfValue = 0;
                  }
               }
               break;
            case 32:
               if (startOfLabel == i) {
                  startOfLabel = i + 1;
               } else if (startOfValue == i) {
                  startOfValue = i + 1;
               }
               break;
            case 58:
               if (startOfValue == 0) {
                  endOfLabel = i + 1;
                  startOfValue = i + 1;
               }
         }
      }

      for (int i = 0; i + 1 < strings.size(); i += 2) {
         String label = (String)strings.elementAt(i);
         if (label.length() > 2 && label.startsWith(uTag) && i + 3 < strings.size()) {
            String labelA = (String)strings.elementAt(i + 2);
            if (label.endsWith(labelA)) {
               strings.setElementAt(labelA, i);
               strings.removeElementAt(i + 2);
               strings.removeElementAt(i + 2);
            }
         }
      }

      contextObject.put(249, strings);
      Object result = FactoryUtil.createInstance(-3124646573404667739L, contextObject);
      if (result == null) {
         throw new SerializationException();
      }

      AddressCardUtilities.createGroup((AddressCardModel)result);
      return result;
   }

   @Override
   public final byte[] convert(Object inputObject, Object context) throws SerializationException {
      if (inputObject instanceof AddressCardModel && inputObject instanceof ConversionProvider) {
         ConversionProvider converter = (ConversionProvider)inputObject;
         ContextObject contextObject = new ContextObject(11, 43, 54);
         StringBuffer stringBuffer = new StringBuffer();
         if (converter.convert(contextObject, stringBuffer)) {
            return convertAddressCardAttachment(stringBuffer.toString());
         }
      }

      throw new SerializationException();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final byte[] convertAddressCardAttachment(String data) {
      if (data == null) {
         return null;
      }

      boolean toBeEncoded = false;
      String encoding = "windows-1252\r";
      String header = null;
      String trailer = null;
      if (StringUtilities.getCharacterSize(data) != 1 && !ConverterUtilities.isIntellisyncCompatible(data)) {
         toBeEncoded = true;
         encoding = "UTF-8\r";
      }

      if (toBeEncoded) {
         int endOfHeader = -1;
         int startOfLabel = 0;
         int endOfLabel = 0;
         int startOfValue = 0;
         int length = data.length();
         int i = 0;
         Vector strings = new Vector();

         while (i < length) {
            switch (data.charAt(i)) {
               case '\r':
                  endOfHeader = i;
                  break;
               case ':':
                  if (endOfHeader > -1) {
                     i = length;
                  }
            }

            i++;
         }

         if (endOfHeader > -1) {
            i = endOfHeader;
            endOfLabel = endOfHeader;
            startOfLabel = endOfHeader;
         } else {
            endOfHeader = 0;
            i = 0;
            endOfLabel = 0;
            startOfLabel = 0;
         }

         if (endOfHeader > 0) {
            header = data.substring(0, endOfHeader);
         }

         for (; i < length; i++) {
            switch (data.charAt(i)) {
               case '\r':
                  if (startOfLabel > endOfLabel) {
                     endOfLabel = i;
                     startOfLabel = i;
                  } else if (i + 1 < data.length()
                     && data.charAt(i + 1) != '\t'
                     && startOfValue > 0
                     && startOfValue == endOfLabel
                     && data.charAt(endOfLabel) == ':') {
                     strings.addElement(data.substring(startOfLabel, endOfLabel + 1));
                     strings.addElement(data.substring(startOfValue + 1, i));
                     endOfLabel = i;
                     startOfLabel = i;
                     startOfValue = 0;
                  }
                  break;
               case ':':
                  if (startOfValue == 0) {
                     endOfLabel = i;
                     startOfValue = i;
                  }
            }
         }

         if (startOfValue > 0 && startOfValue == endOfLabel && startOfLabel < endOfLabel && startOfValue + 1 < data.length()) {
            strings.addElement(data.substring(startOfLabel, endOfLabel + 1));
            strings.addElement(data.substring(startOfValue + 1, data.length()));
         } else if (startOfLabel < data.length() - 1) {
            if (startOfValue == 0 && startOfLabel == endOfLabel) {
               trailer = data.substring(startOfLabel, data.length());
            } else {
               String previous = (String)strings.elementAt(strings.size() - 1);
               previous = previous + data.substring(startOfLabel, data.length());
               strings.setElementAt(previous, strings.size() - 1);
            }
         }

         StringBuffer sb = new StringBuffer(length);
         String errValue = "Unsupported Encoding";
         String uTag = "u_";
         String label = null;
         String value = null;

         for (int j = 0; j + 1 < strings.size(); j += 2) {
            label = (String)strings.elementAt(j);
            value = (String)strings.elementAt(j + 1);
            if (ConverterUtilities.isIntellisyncCompatible(value)) {
               sb.append(label);
               sb.append(value);
            } else if (label.length() > 1) {
               sb.append(label);
               sb.insert(sb.length() - label.length() + 1, uTag);
               sb.append(value);
               sb.append(label);
               sb.append(errValue);
            }
         }

         if (trailer != null) {
            sb.append(trailer);
         }

         data = sb.toString();
      }

      byte[] headerBytes = null;
      if (header != null) {
         boolean var23 = false /* VF: Semaphore variable */;

         label280:
         try {
            var23 = true;
            headerBytes = header.getBytes("windows-1252\r");
            var23 = false;
         } finally {
            if (var23) {
               headerBytes = header.getBytes();
               break label280;
            }
         }
      }

      byte[] resultBytes = null;
      byte[] dataBytes = null;
      boolean var20 = false /* VF: Semaphore variable */;

      label276:
      try {
         var20 = true;
         dataBytes = data.getBytes(encoding);
         var20 = false;
      } finally {
         if (var20) {
            dataBytes = data.getBytes();
            toBeEncoded = false;
            break label276;
         }
      }

      if (headerBytes != null && dataBytes != null) {
         resultBytes = new byte[headerBytes.length + dataBytes.length];
         System.arraycopy(headerBytes, 0, resultBytes, 0, headerBytes.length);
         System.arraycopy(dataBytes, 0, resultBytes, headerBytes.length, dataBytes.length);
      } else {
         resultBytes = dataBytes;
      }

      if (toBeEncoded) {
         UnicodeServiceRegistry ur = UnicodeServiceRegistry.getInstance();
         if (ur != null) {
            ur.setFlags(ur.getFlags() | 1);
         }
      }

      return resultBytes;
   }
}
