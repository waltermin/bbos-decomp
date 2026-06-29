package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class FullCallerIDField extends VerticalFieldManager {
   private static final int[] BASE_CALLER_ID_CONTEXT_FLAGS = new int[]{
      58, 17, 106, -804651007, 64, -804651007, 65, -804782078, 19661400, -804782066, 8193400, 8192000
   };

   public FullCallerIDField(CallerIDInfo callerIDInfo, ContextObject contextObject) {
      RIMModel number = callerIDInfo.getNumber();
      RIMModel address = null;
      PersistableRIMModel var27;
      if (callerIDInfo.displayCompanyInfo() && callerIDInfo._friendlyName instanceof Object) {
         var27 = callerIDInfo._friendlyName;
      } else {
         var27 = callerIDInfo.getAddress();
      }

      this.setCookie(callerIDInfo);
      Font font = Font.getDefault();
      Font newFont = null;
      if (PhoneUtilities.getPrivateFlag(contextObject, 13)) {
         newFont = Font.getDefault().derive(0, 12);
      } else {
         Integer fontSizeInt = (Integer)contextObject.get(-1490249361230225348L);
         if (fontSizeInt != null) {
            if (PhoneUtilities.smallScreen()) {
               FontFamily systemFontFamily = null;

               label255:
               try {
                  systemFontFamily = FontFamily.forName(FontFamily.FAMILY_SYSTEM);
               } finally {
                  break label255;
               }

               if (systemFontFamily != null) {
                  newFont = systemFontFamily.getFont(0, fontSizeInt, 3);
               } else {
                  newFont = font.derive(0, fontSizeInt, 3);
               }
            } else {
               newFont = font.derive(0, fontSizeInt);
            }
         }
      }

      Field addressField = null;
      Field numberField = null;
      Field typeField = null;
      Field friendlyNameField = null;
      boolean haveAddressField = false;
      if (number != null && number.toString().length() != 0) {
         if (var27 instanceof Object) {
            contextObject.setFlags(BASE_CALLER_ID_CONTEXT_FLAGS);
            addressField = ((FieldProvider)var27).getField(contextObject);
            if (addressField != null) {
               if (newFont != null) {
                  this.updateFont(addressField, newFont);
               }

               addressField.setCookie(callerIDInfo);
               this.add(addressField);
               haveAddressField = true;
            }
         } else if (callerIDInfo._friendlyName instanceof Object) {
            contextObject.setFlag(58, 17, 106);
            friendlyNameField = ((FieldProvider)callerIDInfo._friendlyName).getField(contextObject);
            if (friendlyNameField != null) {
               if (newFont != null) {
                  this.updateFont(friendlyNameField, newFont);
               }

               friendlyNameField.setCookie(callerIDInfo);
               this.add(friendlyNameField);
               haveAddressField = true;
            }
         } else {
            ContextObject.setFlag(contextObject, 1);
         }

         boolean redirectedCall = PhoneUtilities.getPrivateFlag(contextObject, 57);
         boolean dualMode = PhoneUtilities.getPrivateFlag(contextObject, 21);
         boolean conference = ContextObject.getFlag(contextObject, 80);
         boolean sparse = ContextObject.getFlag(contextObject, 59);
         if (haveAddressField && sparse) {
            if (conference || dualMode) {
               return;
            }

            if (!redirectedCall) {
               return;
            }
         }

         ContextObject.setFlag(contextObject, 1);
         if (!haveAddressField || !redirectedCall) {
            AbstractPhoneNumberModel pnm = (AbstractPhoneNumberModel)number;
            String typeString = null;
            if (contextObject.getFlag(34) && pnm != null && pnm.getType() != 0) {
               typeString = ((AbstractPhoneNumberModel)number).getTypeString();
            }

            String phoneNumberString = (String)contextObject.get(-799495460678763170L);
            if (phoneNumberString != null) {
               if (typeString != null) {
                  StringBuffer buf = (StringBuffer)(new Object());
                  buf.append(typeString);
                  buf.append(' ');
                  buf.append(phoneNumberString);
                  numberField = (Field)(new Object(buf, 64));
               } else {
                  numberField = (Field)(new Object(phoneNumberString, 64));
               }
            } else if (typeString != null) {
               phoneNumberString = pnm.getDisplayablePhoneNumber();
               StringBuffer buf = (StringBuffer)(new Object());
               buf.append(typeString);
               buf.append(' ');
               buf.append(phoneNumberString);
               long style = 64;
               if (contextObject.getFlag(24)) {
                  style |= 18014398509481984L;
               }

               numberField = (Field)(new Object(buf, style));
            } else if (number instanceof Object) {
               numberField = ((FieldProvider)number).getField(contextObject);
            }
         }

         if (numberField != null) {
            if (newFont != null) {
               this.updateFont(numberField, newFont);
               if (typeField != null) {
                  this.updateFont(typeField, newFont);
               }
            }

            numberField.setCookie(callerIDInfo);
            this.add(numberField);
         }

         if (redirectedCall && !dualMode && !conference) {
            String redirectedNumber = (String)contextObject.get(9190530831625408279L);
            if (redirectedNumber != null && redirectedNumber.length() > 0) {
               boolean incoming = callerIDInfo.isIncomingCall();
               int stringId = incoming ? 6029 : 6028;
               Field labelField = (Field)(new Object(PhoneResources.getString(stringId)));
               if (newFont != null) {
                  labelField.setFont(newFont);
               }

               this.add(labelField);
               Object model = PhoneUtilities.createNumberModel(redirectedNumber);
               if (!(model instanceof Object)) {
                  this.add((Field)(new Object(redirectedNumber)));
               } else {
                  CallerIDInfo cidi = new CallerIDInfo((PersistableRIMModel)model, null, incoming, false);
                  boolean oldSparseFlag = contextObject.getFlag(59);
                  contextObject.setFlag(59);
                  PhoneUtilities.clearPrivateFlag(contextObject, 57);
                  String oldPhoneNumberString = (String)contextObject.get(-799495460678763170L);
                  if (oldPhoneNumberString != null) {
                     contextObject.remove(-799495460678763170L);
                  }

                  Field field = cidi.getField(contextObject);
                  if (field != null) {
                     this.add(field);
                  }

                  if (!oldSparseFlag) {
                     contextObject.clearFlag(59);
                  }

                  PhoneUtilities.setPrivateFlag(contextObject, 57);
                  if (oldPhoneNumberString != null) {
                     contextObject.put(-799495460678763170L, oldPhoneNumberString);
                     return;
                  }
               }
            }
         }
      } else {
         if (callerIDInfo.isPrivateNumber()) {
            addressField = (Field)(new Object(PhoneResources.getString(156)));
         } else {
            addressField = (Field)(new Object(PhoneResources.getString(117)));
         }

         if (newFont != null) {
            addressField.setFont(newFont);
         }

         this.add(addressField);
      }
   }

   private final void updateFont(Field field, Font font) {
      if (!(field instanceof Object)) {
         field.setFont(font);
      } else {
         Manager mgr = (Manager)field;

         for (int i = mgr.getFieldCount() - 1; i >= 0; i--) {
            this.updateFont(mgr.getField(i), font);
         }
      }
   }
}
