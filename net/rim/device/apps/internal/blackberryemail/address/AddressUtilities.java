package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.ToggleableField;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.i18n.UnicodeServiceRegistry;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class AddressUtilities {
   public static final long SOURCE_ADDRESS_CARD;
   private static final int MESSAGE_ADDRESS_DATA_SIZE;
   private static ContextObjectWR _friendlyContextWR = (ContextObjectWR)(new Object(10));
   private static String[] _nameStrings = new Object[2];
   private static WeakReference _smsAddressDataBufferWR = (WeakReference)(new Object(null));

   private AddressUtilities() {
   }

   static final String initializeData(ContextObject contextObject) {
      Object test = contextObject.get(251);
      if (test != null) {
         String[] stringPair = (Object[])test;
         String address = CMIMEUtilities.getAddressPart(stringPair);
         String friendlyName = CMIMEUtilities.getFriendlyPart(stringPair);
         return initializeData(address, friendlyName);
      } else {
         test = contextObject.get(253);
         return test != null ? initializeData((String)test) : "";
      }
   }

   static final String initializeData(String address) {
      return initializeData(net.rim.device.internal.util.AddressUtilities.removePrefixes(address), null);
   }

   static final String initializeData(String address, String friendlyName) {
      address = trimString(address);
      friendlyName = trimString(friendlyName);
      if (address == null) {
         if (friendlyName == null) {
            String data = null;
            return data;
         } else {
            StringBuffer buff = (StringBuffer)(new Object(friendlyName.length() + 1));
            buff.append('\u0000').append(friendlyName);
            String data = buff.toString();
            return data;
         }
      } else if (friendlyName == null) {
         String data = address;
         return data;
      } else {
         StringBuffer buff = (StringBuffer)(new Object(address.length() + friendlyName.length() + 1));
         buff.append(address).append('\u0000').append(friendlyName);
         return buff.toString();
      }
   }

   private static final String trimString(String string) {
      if (string != null) {
         string = string.trim();
         if (string.length() == 0) {
            string = null;
         }
      }

      return string;
   }

   private static final String convertNamesToLocaleOrder(String data, int friendlyNameIndex) {
      if (Locale.getSystemNameOrder() != 1) {
         return data;
      }

      int comma = data.indexOf(44, friendlyNameIndex);
      if (comma != -1) {
         return data;
      }

      int lastNameIndex = data.indexOf(32, friendlyNameIndex);
      if (lastNameIndex == -1) {
         return data;
      }

      StringBuffer result = (StringBuffer)(new Object());
      result.append(data.substring(0, friendlyNameIndex));
      result.append('\u0000');
      result.append(data.substring(lastNameIndex).trim());
      result.append(", ");
      result.append(data.substring(friendlyNameIndex + 1, lastNameIndex).trim());
      return result.toString();
   }

   static final String toString(String data) {
      String result = getAddress(data);
      if (result == null || result.length() == 0) {
         result = getFriendlyName(data);
      }

      if (result == null) {
         result = "";
      }

      return result;
   }

   static final String getFriendlyName(String data) {
      if (data != null) {
         int friendlyNameIndex = data.indexOf(0);
         if (friendlyNameIndex != -1) {
            return convertNamesToLocaleOrder(data, friendlyNameIndex).substring(friendlyNameIndex + 1);
         }
      }

      return null;
   }

   static final String getAddress(String data) {
      if (data != null) {
         int friendlyNameIndex = data.indexOf(0);
         if (friendlyNameIndex > 0) {
            return data.substring(0, friendlyNameIndex);
         }

         if (friendlyNameIndex == -1) {
            return data;
         }
      }

      return "";
   }

   static final RIMModel getAddressBookEntry(RIMModel address) {
      Object object = AddressBookServices.reverseLookup(address);
      if (!(object instanceof Object)) {
         object = null;
      }

      return (RIMModel)object;
   }

   static final RIMModel getAddressBookEntry(RIMModel address, Object context) {
      Object object = ContextObject.get(context, 252);
      return (RIMModel)(!(object instanceof Object) ? getAddressBookEntry(address) : object);
   }

   static final Field getField(int type, int labelResourceId, RIMModel model, String data, Object context) {
      String label = "";
      boolean labelFlag = !ContextObject.getFlag(context, 1);
      if (labelFlag) {
         label = EmailResources.getString(labelResourceId);
      }

      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      boolean isMultiSendMethodCapable = implusService != null && implusService.isIMPlusCompose(context);
      Field fieldToReturn = null;
      String addr = getAddress(data);
      if (ContextObject.getFlag(context, 0)) {
         switch (type) {
            case 1:
               EmailAddressEditField emailField = (EmailAddressEditField)(new Object(label, addr, 2048));
               emailField.setFilter(new CustomEmailAddressFilter());
               fieldToReturn = emailField;
               break;
            case 10:
               if (addr != null && addr.length() > 8) {
                  addr = addr.substring(0, 8);
               }

               EditField editField = (EditField)(new Object(label, addr, 8, 150994944));
               fieldToReturn = editField;
         }
      } else {
         Field friendly = null;
         Field qualified = null;
         if (ContextObject.getFlag(context, 9)) {
            RIMModel address = getAddressBookEntry(model, context);
            if (address instanceof Object) {
               FieldProvider fieldProvider = (FieldProvider)address;
               boolean oldValue = ContextObject.getFlag(context, 51);
               boolean oldSingleValue = ContextObject.getFlag(context, 106);
               boolean oldNoLabelFlag = ContextObject.getFlag(context, 1);
               if (ContextObject.getFlag(context, 55)) {
                  ContextObject.setFlag(context, 1);
               }

               ContextObject.setFlag(context, 51, 106);
               friendly = fieldProvider.getField(context);
               if (!oldValue) {
                  ContextObject.clearFlag(context, 51);
               }

               if (!oldSingleValue) {
                  ContextObject.clearFlag(context, 106);
               }

               if (!oldNoLabelFlag) {
                  ContextObject.clearFlag(context, 1);
               }
            }

            String fn = getFriendlyName(data);
            if (friendly == null && fn != null) {
               friendly = (Field)(new Object(fn, 18014398509481984L));
            }

            if (label.length() > 0 && friendly != null && ContextObject.getFlag(context, 55)) {
               HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
               hfm.add((Field)(new Object(label)));
               hfm.add(friendly);
               friendly = hfm;
            }
         }

         String valueText;
         if (addr != null) {
            valueText = addr;
         } else {
            valueText = EmailResources.getString(31);
         }

         if (labelFlag) {
            Field leftField = (Field)(new Object(label));
            Field rightField = (Field)(new Object(valueText, 18014398509481984L));
            Manager manager;
            if (ContextObject.getFlag(context, 128)) {
               manager = (Manager)(new Object(leftField, rightField));
            } else {
               manager = (Manager)(new Object());
               manager.add(leftField);
               manager.add(rightField);
            }

            rightField.setCookie(model);
            qualified = manager;
         } else {
            long style = 18014398509481984L;
            if (isMultiSendMethodCapable) {
               style |= 67108864;
            }

            qualified = (Field)(new Object(valueText, style));
         }

         qualified.setCookie(model);
         if (friendly != null) {
            fieldToReturn = (Field)(new Object(friendly, qualified));
            friendly.setCookie(model);
            fieldToReturn.setCookie(model);
         } else {
            fieldToReturn = qualified;
         }
      }

      if (!labelFlag && isMultiSendMethodCapable) {
         StringBuffer postfixBuffer = (StringBuffer)(new Object());
         postfixBuffer.append(' ');
         postfixBuffer.append('(');
         switch (type) {
            case 1:
               postfixBuffer.append(EmailResources.getString(41));
               break;
            case 10:
               postfixBuffer.append(EmailResources.getString(34));
         }

         postfixBuffer.append(')');
         FlowFieldManager ffm = (FlowFieldManager)(new Object(fieldToReturn.getStyle()));
         LabelField postfixLabel = (LabelField)(new Object(postfixBuffer.toString(), 36028797018963968L));
         ffm.add(fieldToReturn);
         ffm.setCookie(fieldToReturn.getCookie());
         ffm.add(postfixLabel);
         fieldToReturn = ffm;
      }

      return fieldToReturn;
   }

   static final int paint(RIMModel model, String data, Graphics g, int x, int y, int width, int height, Object context) {
      RIMModel addressBookEntry = getAddressBookEntry(model);
      if (!(addressBookEntry instanceof Object)) {
         if (data != null) {
            int friendlyNameIndex = data.indexOf(0);
            int textStyle = ContextObject.getFlag(context, 17) ? 64 : 0;
            if (friendlyNameIndex != -1) {
               String name = convertNamesToLocaleOrder(data, friendlyNameIndex);
               return g.drawText(name, friendlyNameIndex + 1, name.length() - friendlyNameIndex - 1, x, y, textStyle, width);
            } else {
               y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), data, y);
               return g.drawText(data, x, y, textStyle, width);
            }
         } else {
            return 0;
         }
      } else {
         PaintProvider paintProvider = (PaintProvider)addressBookEntry;
         return paintProvider.paint(g, x, y, width, height, context);
      }
   }

   static final Verb getVerbs(RIMModel model, long objectType, int composeAsUseMenuOrder, Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      Array.resize(verbs, 0);
      RIMModel addressCard = null;
      boolean picking = ContextObject.getFlag(context, 5);
      boolean editable = ContextObject.getFlag(context, 0);
      boolean hyperlink = ContextObject.getFlag(context, 83);
      boolean composeOnly = ContextObject.getFlag(context, 44);
      if (hyperlink || !editable && !picking) {
         RIMModel var20 = AddressBookServices.reverseLookup(model);
         if (var20 != null && !hyperlink && !composeOnly && !ContextObject.getFlag(context, 11)) {
            ContextObject contextObject = ContextObject.clone(context);
            ContextObject.setFlag(contextObject, 44);
            ((VerbProvider)var20).getVerbs(contextObject, verbs);
         } else {
            VerbRepository verbRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
            Verb[] composeVerbs = verbRepository.getVerbs(objectType);
            int pickOrder = 0;
            if (ContextObject.getFlag(context, 7)) {
               pickOrder = composeAsUseMenuOrder;
            }

            Array.resize(verbs, composeVerbs.length);

            for (int i = 0; i < composeVerbs.length; i++) {
               if (pickOrder != 0) {
                  if (composeVerbs[i].getOrdering() == 1266944) {
                     pickOrder = 327984;
                  } else {
                     pickOrder = composeAsUseMenuOrder;
                  }
               }

               verbs[i] = new AddressComposeAdapter(model, composeVerbs[i], pickOrder != 0 ? pickOrder : composeVerbs[i].getOrdering(), context);
               if (composeVerbs[i].getOrdering() == 1265920 && ContextObject.getFlag(context, 2)) {
                  defaultVerb = verbs[i];
               }
            }

            VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-7881764549058890736L);
            if (verbFactories != null && verbFactories.length > 0) {
               Object newContext = null;
               if (!(context instanceof Object)) {
                  newContext = context;
               } else {
                  newContext = ((ContextObject)context).clone();
                  ((LongHashtable)newContext).put(245, model);
               }

               for (int i = 0; i < verbFactories.length; i++) {
                  Verb[] currentVerbs = verbFactories[i].getVerbs(newContext);
                  if (currentVerbs != null) {
                     for (int j = 0; j < currentVerbs.length; j++) {
                        if (currentVerbs[j] != null) {
                           Arrays.add(verbs, new AddressComposeAdapter(model, currentVerbs[j], currentVerbs[j].getOrdering(), context));
                        }
                     }
                  }
               }
            }

            if (ContextObject.getFlag(context, 2) && verbs.length > 0 && defaultVerb == null) {
               defaultVerb = verbs[0];
            }
         }

         if (ContextObject.getFlag(context, 2) && !ContextObject.getFlag(context, 54)) {
            int index = verbs.length;
            if (var20 != null) {
               if (!ContextObject.getFlag(context, 18) && !ContextObject.getFlag(context, 11)) {
                  Array.resize(verbs, index + 1);
                  verbs[index++] = new ViewAddressVerb(var20);
               }
            } else {
               Verb addToABVerb = AddressBookServices.getAddToAddressBookVerb();
               RIMModel headerAddressCard = (RIMModel)ContextObject.get(context, -570873356703084835L);
               if (addToABVerb != null) {
                  RIMModel tmpModel = headerAddressCard;
                  if (tmpModel == null) {
                     ContextObject addressCreationContext = (ContextObject)(new Object());
                     addressCreationContext.put(254, model);
                     tmpModel = (RIMModel)((Factory)ApplicationRegistry.getApplicationRegistry().waitFor(objectType)).createInstance(addressCreationContext);
                  }

                  Array.resize(verbs, index + 1);
                  verbs[index++] = (Verb)(new Object(addToABVerb, tmpModel, 16867328));
               }
            }
         }
      }

      if (!ContextObject.getFlag(context, 44)) {
         Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
         if (uiField != null && uiField.getManager() instanceof Object) {
            ToggleableField toggleField = (ToggleableField)uiField.getManager();
            int length = verbs.length;
            Array.resize(verbs, length + 1);
            int resId = toggleField.isFriendlyVisible() ? 1650 : 1700;
            verbs[length] = (Verb)(new Object(toggleField, CommonResources.getResourceBundle(), resId));
         }
      }

      return defaultVerb;
   }

   static final int getKeys(String _data, Object context, Object[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         if (keyArray.length == index) {
            Array.resize(keyArray, index + 1);
         }

         keyArray[index] = AddressBookServices.getReverseLookupString(getAddress(_data));
         return 1;
      } else {
         return 0;
      }
   }

   static final int getKeys(String _data, Object context, int[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         if (keyArray.length == index) {
            Array.resize(keyArray, index + 1);
         }

         keyArray[index] = AddressBookServices.getReverseLookupCode(getAddress(_data), true);
         return 1;
      } else {
         return 0;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final boolean convert(RIMModel model, String data, int addressFieldId, String addressFieldName, Object context, Object target) {
      String address = getAddress(data);
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (address != null) {
            syncBuffer.addField(addressFieldId, address);
         }

         return true;
      } else {
         if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54)) {
            if (target instanceof Object) {
               StringBuffer stringBuffer = (StringBuffer)target;
               if (address != null) {
                  stringBuffer.append(addressFieldName).append(address);
               }

               return true;
            }
         } else if (ContextObject.getFlag(context, 10) && target instanceof Object[]) {
            String[] names = (Object[])target;
            if (names.length == 2) {
               getAddressAndFriendlyName(model, data, names, false);
               return true;
            }
         } else {
            if ((ContextObject.getFlag(context, 43) || ContextObject.getFlag(context, 94)) && ContextObject.getFlag(context, 19)) {
               int bufferSize = 8;
               String fn = getFriendlyName(data);
               String encoding = null;
               boolean isEncoded = false;
               byte encodingByte = -1;
               if (fn != null && !ConverterUtilities.isIntellisyncCompatible(fn) || address != null && !ConverterUtilities.isIntellisyncCompatible(address)) {
                  isEncoded = true;
                  encodingByte = ConverterUtilities.getConversionCurrentEncodingByte();
                  encoding = ConverterUtilities.getConversionCurrentEncodingName();
                  if (encodingByte == -1 || encoding == null || encoding.length() == 0) {
                     isEncoded = false;
                     encoding = null;
                  }
               }

               if (encoding == null) {
                  encoding = "windows-1252\r";
               }

               StringBuffer wholeAddress = (StringBuffer)(new Object(
                  (fn != null ? fn.length() : 0) + (address != null ? address.length() : 0) + (isEncoded ? 1 : 2)
               ));
               if (fn != null) {
                  wholeAddress.append(fn);
               }

               wholeAddress.append('\u0000');
               if (address != null) {
                  wholeAddress.append(address);
               }

               if (!isEncoded) {
                  wholeAddress.append('\u0000');
               }

               String wholeAddressString = wholeAddress.toString();
               byte[] wholeAddressBytes = null;
               if (wholeAddressString != null) {
                  boolean var19 = false /* VF: Semaphore variable */;

                  try {
                     var19 = true;
                     wholeAddressBytes = wholeAddressString.getBytes(encoding);
                     var19 = false;
                  } finally {
                     if (var19) {
                        label295: {
                           if (isEncoded) {
                              wholeAddress.append('\u0000');
                              wholeAddressString = wholeAddress.toString();
                           }

                           wholeAddressBytes = wholeAddressString.getBytes();
                           isEncoded = false;
                           break label295;
                        }
                     }
                  }

                  if (wholeAddressBytes != null) {
                     bufferSize += wholeAddressBytes.length;
                  } else {
                     isEncoded = false;
                  }
               }

               if (isEncoded && wholeAddressBytes != null) {
                  bufferSize++;
               }

               byte[] buf = new byte[bufferSize];
               if (ContextObject.getFlag(context, 38)) {
                  buf[0] = -1;
                  buf[1] = 7;
                  buf[2] = 0;
                  buf[3] = 0;
               } else if (ContextObject.getFlag(context, 104)) {
                  buf[0] = -1;
                  buf[1] = -1;
                  buf[2] = 31;
                  buf[3] = 0;
               } else if (ContextObject.getFlag(context, 75)) {
                  buf[0] = -1;
                  buf[1] = -1;
                  buf[2] = 63;
                  buf[3] = 0;
               } else {
                  buf[0] = -1;
                  buf[1] = -1;
                  buf[2] = -1;
                  buf[3] = 1;
               }

               buf[4] = -1;
               buf[5] = 0;
               buf[6] = (byte)addressFieldId;
               buf[7] = 3;
               int currentOffset = 8;
               if (wholeAddressBytes != null) {
                  if (isEncoded) {
                     buf[currentOffset++] = encodingByte;
                     UnicodeServiceRegistry ur = UnicodeServiceRegistry.getInstance();
                     if (ur != null) {
                        ur.setFlags(ur.getFlags() | 1);
                     }
                  }

                  System.arraycopy(wholeAddressBytes, 0, buf, currentOffset, wholeAddressBytes.length);
               }

               ((Object[])target)[0] = buf;
               if (((Object[])target).length > 1) {
                  ((byte[])((Object[])target)[1])[0] = (byte)(isEncoded ? 1 : 0);
               }

               return true;
            }

            if (ContextObject.getFlag(context, 55)) {
               if (ContextObject.getFlag(context, 21) && target instanceof Object) {
                  StringBuffer sb = (StringBuffer)target;
                  sb.setLength(0);
                  sb.append(address);
                  return true;
               }

               if (ContextObject.getFlag(context, 19) && target instanceof Object) {
                  SyncBuffer syncBuffer = (SyncBuffer)target;
                  DataBuffer smsAddressDataBuffer = WeakReferenceUtilities.getDataBuffer(_smsAddressDataBufferWR, false);
                  smsAddressDataBuffer.setLength(0);
                  if (address != null) {
                     ConverterUtilities.writeStringSmart(smsAddressDataBuffer, addressFieldId, address);
                     syncBuffer.addBytes(ContextObject.getIntegerData(context, 0), smsAddressDataBuffer.getArray());
                  }

                  return true;
               }
            }
         }

         return false;
      }
   }

   static final void getAddressAndFriendlyName(RIMModel model, String data, String[] names, boolean modelCheckedAlready) {
      names[1] = null;
      if (data == null) {
         names[0] = "";
      } else {
         int friendlyNameIndex = data.indexOf(0);
         if (friendlyNameIndex > 0) {
            names[0] = data.substring(0, friendlyNameIndex);
         } else if (friendlyNameIndex == -1) {
            names[0] = data;
         } else {
            names[0] = "";
         }

         if (!modelCheckedAlready) {
            RIMModel addressBookEntry = getAddressBookEntry(model);
            if (addressBookEntry instanceof Object) {
               ConversionProvider converter = (ConversionProvider)addressBookEntry;
               if (converter.convert(_friendlyContextWR.getContextObject(), names)) {
                  return;
               }
            }
         }

         if (friendlyNameIndex > 0) {
            names[1] = data.substring(friendlyNameIndex + 1);
         }
      }
   }

   static final int match(RIMModel model, String data, Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      if (!(crit.getValue() instanceof Object[])) {
         return -1;
      }

      Object[] values = (Object[])crit.getValue();
      String[] testWords = (Object[])values[2];
      String nameStrings0;
      String nameStrings1;
      synchronized (_nameStrings) {
         getAddressAndFriendlyName(model, data, _nameStrings, true);
         nameStrings0 = _nameStrings[0];
         nameStrings1 = _nameStrings[1];
         _nameStrings[0] = null;
         _nameStrings[1] = null;
      }

      for (int i = testWords.length - 1; i >= 0; i--) {
         StringMatch stringMatch = (StringMatch)(new Object(testWords[i], false, false));
         if ((nameStrings1 == null || stringMatch.indexOf(nameStrings1) < 0) && (nameStrings0 == null || stringMatch.indexOf(nameStrings0) < 0)) {
            return 0;
         }
      }

      return 1;
   }

   static final String getVerbDescription(RIMModel model, String data, Object context) {
      if (!ContextObject.getFlag(context, 51) && !ContextObject.getFlag(context, 63)) {
         String alternateAddressCardName = (String)ContextObject.get(context, 7065077197339612497L);
         if (alternateAddressCardName != null && alternateAddressCardName.length() > 0) {
            return alternateAddressCardName;
         }

         Object originalAddressCard = ContextObject.get(context, -4055106280780392421L);
         RIMModel addressBookEntry;
         if (!(originalAddressCard instanceof Object)) {
            addressBookEntry = getAddressBookEntry(model, context);
         } else {
            addressBookEntry = (RIMModel)originalAddressCard;
         }

         if (addressBookEntry != null) {
            return addressBookEntry.toString();
         }

         String fn = getFriendlyName(data);
         return fn != null ? fn : model.toString();
      } else {
         return model.toString();
      }
   }
}
