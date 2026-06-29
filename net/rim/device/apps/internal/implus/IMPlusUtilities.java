package net.rim.device.apps.internal.implus;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.ToggleQualifiedFriendlyVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.ToggleableField;
import net.rim.vm.Array;

final class IMPlusUtilities {
   public static final long SOURCE_ADDRESS_CARD = -570873356703084835L;
   private static final int MESSAGE_ADDRESS_DATA_SIZE = 8;
   private static final String MAILTO_PREFIX = "mailto:";
   private static Object _friendlyContext = new ContextObject(10);
   private static String[] _nameStrings = new String[2];

   private IMPlusUtilities() {
   }

   static final String initializeData(ContextObject contextObject) {
      Object test = contextObject.get(251);
      if (test != null) {
         String[] stringPair = (String[])test;
         String address = CMIMEUtilities.getAddressPart(stringPair);
         String friendlyName = CMIMEUtilities.getFriendlyPart(stringPair);
         return initializeData(address, friendlyName);
      } else {
         test = contextObject.get(253);
         return test != null ? initializeData((String)test) : "";
      }
   }

   static final String removePrefixes(String address) {
      if (StringUtilities.startsWithIgnoreCase(address, "mailto:", 1701707776)) {
         int beginIndex = address.indexOf(58) + 1;
         int endIndex = address.length();

         while (beginIndex < endIndex && address.charAt(beginIndex) == '/') {
            beginIndex++;
         }

         while (beginIndex < endIndex && address.charAt(endIndex - 1) == '/') {
            endIndex--;
         }

         address = address.substring(beginIndex, endIndex);
      }

      return address;
   }

   static final String initializeData(String address) {
      return initializeData(removePrefixes(address), null);
   }

   static final String initializeData(String address, String friendlyName) {
      address = trimString(address);
      friendlyName = trimString(friendlyName);
      if (address != null && friendlyName != null) {
         return address + '\u0000' + friendlyName;
      } else if (address != null) {
         return address;
      } else {
         return friendlyName != null ? '\u0000' + friendlyName : null;
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

   private static final String convertNamesToLocaleOrder(String data) {
      if (data == null) {
         return "";
      }

      int friendlyNameIndex = data.indexOf(0);
      String address = data.substring(0, friendlyNameIndex);
      String firstName = data.substring(friendlyNameIndex, data.length());
      String lastName = null;
      if (firstName != null && Locale.getSystemNameOrder() == 1) {
         firstName = firstName.trim();
         if (firstName.length() > 0) {
            int index = firstName.indexOf(44);
            if (index != -1) {
               return data;
            }

            index = firstName.indexOf(32);
            if (index != -1) {
               if (index != firstName.length() - 1) {
                  lastName = firstName.substring(index + 1).trim();
                  lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
               }

               firstName = Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1, index);
            }
         }

         return lastName == null ? data : address + "" + lastName + ", " + firstName;
      } else {
         return data;
      }
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
            return convertNamesToLocaleOrder(data).substring(friendlyNameIndex + 1);
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
      if (!(object instanceof RIMModel)) {
         object = null;
      }

      return (RIMModel)object;
   }

   static final RIMModel getAddressBookEntry(RIMModel address, Object context) {
      Object object = (RIMModel)ContextObject.get(context, 252);
      return !(object instanceof RIMModel) ? getAddressBookEntry(address) : (RIMModel)object;
   }

   static final Field getField(int type, int labelResourceId, RIMModel model, String data, Object context) {
      String label = "";
      boolean labelFlag = !ContextObject.getFlag(context, 1);
      if (labelFlag) {
         label = IMPlusResources.getString(labelResourceId) + ": ";
      }

      Field fieldToReturn = null;
      String addr = getAddress(data);
      if (ContextObject.getFlag(context, 0)) {
         switch (type) {
            case 3:
               break;
            case 4:
               EditField oneWayPagerField = new EditField(label, addr);
               oneWayPagerField.setFilter(new OneWayPagerTextFilter(oneWayPagerField));
               fieldToReturn = oneWayPagerField;
               break;
            case 5:
            default:
               EditField interactiveHHField = new EditField(label, addr);
               interactiveHHField.setFilter(new EmailWordTextFilter());
               fieldToReturn = interactiveHHField;
         }
      } else {
         Field friendly = null;
         Field qualified = null;
         String friendlyName = null;
         if (ContextObject.getFlag(context, 9)) {
            RIMModel address = getAddressBookEntry(model, context);
            if (address instanceof FieldProvider) {
               FieldProvider fieldProvider = (FieldProvider)address;
               boolean oldValue = ContextObject.getFlag(context, 51);
               boolean oldSingleValue = ContextObject.getFlag(context, 106);
               ContextObject.setFlag(context, 51, 106);
               friendly = fieldProvider.getField(context);
               if (friendly instanceof LabelField) {
                  friendlyName = ((LabelField)friendly).getText();
               }

               if (!oldValue) {
                  ContextObject.clearFlag(context, 51);
               }

               if (!oldSingleValue) {
                  ContextObject.clearFlag(context, 106);
               }
            }

            if (friendlyName == null) {
               friendlyName = getFriendlyName(data);
            }

            if (friendlyName != null) {
               friendly = new LabelField(friendlyName, 18014398509482048L);
            }
         }

         String valueText;
         if (addr != null) {
            valueText = addr;
         } else {
            valueText = IMPlusResources.getString(1);
         }

         if (labelFlag) {
            FlowFieldManager ffmgr = new FlowFieldManager();
            ffmgr.add(new LabelField(label));
            Field labelField = new RichTextField(valueText, 18014398509481984L);
            labelField.setCookie(model);
            ffmgr.add(labelField);
            qualified = ffmgr;
         } else {
            qualified = new RichTextField(valueText, 18014398576590848L);
         }

         qualified.setCookie(model);
         if (friendly != null) {
            fieldToReturn = new ToggleableField(friendly, qualified);
            friendly.setCookie(model);
            fieldToReturn.setCookie(model);
         } else {
            fieldToReturn = qualified;
         }

         if (!labelFlag) {
            StringBuffer postfixBuffer = new StringBuffer();
            postfixBuffer.append(' ');
            postfixBuffer.append('(');
            switch (type) {
               case 3:
                  break;
               case 4:
                  postfixBuffer.append(IMPlusResources.getString(13));
                  break;
               case 5:
               default:
                  postfixBuffer.append(IMPlusResources.getString(0));
            }

            postfixBuffer.append(')');
            FlowFieldManager ffm = new FlowFieldManager(fieldToReturn.getStyle());
            LabelField postfixLabel = new LabelField(postfixBuffer.toString(), 36028797018963968L);
            ffm.add(fieldToReturn);
            ffm.setCookie(fieldToReturn.getCookie());
            ffm.add(postfixLabel);
            fieldToReturn = ffm;
         }
      }

      return fieldToReturn;
   }

   static final int paint(RIMModel model, String data, Graphics g, int x, int y, int width, int height, Object context) {
      RIMModel addressBookEntry = getAddressBookEntry(model);
      if (!(addressBookEntry instanceof PaintProvider)) {
         if (data != null) {
            int friendlyNameIndex = data.indexOf(0);
            return friendlyNameIndex != -1
               ? g.drawText(convertNamesToLocaleOrder(data), friendlyNameIndex + 1, data.length() - friendlyNameIndex - 1, x, y, 64, width)
               : g.drawText(getAddress(data), x, y, 64, width);
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
         Object originalAddressCard = ContextObject.get(context, -4055106280780392421L);
         if (!(originalAddressCard instanceof RIMModel)) {
            addressCard = (RIMModel)AddressBookServices.reverseLookup(model);
         } else {
            addressCard = (RIMModel)originalAddressCard;
         }

         if (addressCard != null && !hyperlink && !composeOnly && !ContextObject.getFlag(context, 11)) {
            ContextObject contextObject = ContextObject.clone(context);
            ContextObject.setFlag(contextObject, 44);
            ((VerbProvider)addressCard).getVerbs(contextObject, verbs);
         } else {
            VerbRepository verbRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
            Verb composeVerb = IMPlusCmimeListener.getInstance()._composeIMPlusVerb;
            int pickOrder = 0;
            if (ContextObject.getFlag(context, 7)) {
               pickOrder = composeAsUseMenuOrder;
            }

            Array.resize(verbs, 1);
            verbs[0] = new IMPlusAddressComposeAdapter(model, composeVerb, pickOrder != 0 ? pickOrder : composeVerb.getOrdering(), context);
            if ((ContextObject.getFlag(context, 2) || ContextObject.getFlag(context, 114)) && verbs.length > 0) {
               defaultVerb = verbs[0];
            }
         }

         if (ContextObject.getFlag(context, 2)) {
            int index = verbs.length;
            if (addressCard != null) {
               if (!ContextObject.getFlag(context, 18) && !ContextObject.getFlag(context, 11)) {
                  Array.resize(verbs, index + 1);
                  verbs[index++] = new ViewAddressVerb(addressCard);
               }
            } else {
               VerbRepository repository = VerbRepository.getVerbRepository(1666635727707141867L);
               Verb[] addToABVerbs = repository.getVerbs(objectType);
               int verbCount = addToABVerbs.length;
               RIMModel headerAddressCard = (RIMModel)ContextObject.get(context, -570873356703084835L);
               if (verbCount > 0) {
                  RIMModel tmpModel = headerAddressCard;
                  if (tmpModel == null) {
                     ContextObject addressCreationContext = new ContextObject();
                     addressCreationContext.put(254, model);
                     tmpModel = (RIMModel)((Factory)ApplicationRegistry.getApplicationRegistry().waitFor(objectType)).createInstance(addressCreationContext);
                  }

                  Array.resize(verbs, index + verbCount);

                  for (int i = 0; i < verbCount; i++) {
                     verbs[index++] = new WrapperVerb(addToABVerbs[i], tmpModel, 16867328);
                  }
               }
            }
         }
      }

      if (!ContextObject.getFlag(context, 44)) {
         Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
         if (uiField != null && uiField.getManager() instanceof ToggleableField) {
            ToggleableField toggleField = (ToggleableField)uiField.getManager();
            int length = verbs.length;
            Array.resize(verbs, length + 1);
            int resId = toggleField.isFriendlyVisible() ? 1650 : 1700;
            verbs[length] = new ToggleQualifiedFriendlyVerb(toggleField, CommonResources.getResourceBundle(), resId);
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

         keyArray[index] = AddressBookServices.getReverseLookupCode(getAddress(_data));
         return 1;
      } else {
         return 0;
      }
   }

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
            if (target instanceof StringBuffer) {
               StringBuffer stringBuffer = (StringBuffer)target;
               if (address != null) {
                  stringBuffer.append(addressFieldName).append(address);
               }

               return true;
            }
         } else if (ContextObject.getFlag(context, 10) && target instanceof String[]) {
            String[] names = (String[])target;
            if (names.length == 2) {
               getAddressAndFriendlyName(model, data, names, false);
               return true;
            }
         } else if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            int bufferSize = 10;
            byte[] friendlyNameBytes = null;
            String fn = getFriendlyName(data);
            if (fn != null) {
               friendlyNameBytes = fn.getBytes();
               bufferSize += friendlyNameBytes.length;
            }

            byte[] addressBytes = null;
            if (address != null) {
               addressBytes = address.getBytes();
               bufferSize += addressBytes.length;
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
            if (friendlyNameBytes != null) {
               System.arraycopy(friendlyNameBytes, 0, buf, currentOffset, friendlyNameBytes.length);
               currentOffset += friendlyNameBytes.length;
            }

            currentOffset++;
            if (addressBytes != null) {
               System.arraycopy(addressBytes, 0, buf, currentOffset, addressBytes.length);
            }

            ((Object[])target)[0] = buf;
            return true;
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
            if (addressBookEntry instanceof ConversionProvider) {
               ConversionProvider converter = (ConversionProvider)addressBookEntry;
               if (converter.convert(_friendlyContext, names)) {
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
      String[] testWords = (String[])values[2];
      String nameStrings0;
      String nameStrings1;
      synchronized (_nameStrings) {
         getAddressAndFriendlyName(model, data, _nameStrings, true);
         nameStrings0 = _nameStrings[0];
         nameStrings1 = _nameStrings[1];
      }

      int result = Match.nameStringMatch(nameStrings1, testWords);
      if (result == 0) {
         result = Match.nameStringMatch(nameStrings0, testWords);
      }

      return result;
   }

   static final String getVerbDescription(RIMModel model, String data, Object context) {
      if (!ContextObject.getFlag(context, 51) && !ContextObject.getFlag(context, 63)) {
         Object originalAddressCard = ContextObject.get(context, -4055106280780392421L);
         RIMModel addressBookEntry;
         if (!(originalAddressCard instanceof RIMModel)) {
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
