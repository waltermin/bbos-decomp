package net.rim.device.apps.internal.addressbook.userfields;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.vm.Array;

public final class UserFieldsModel implements PersistableRIMModel, VerbProvider, FieldProvider, ConversionProvider, MatchProvider {
   String[] _userDefinedFields = new Object[4];
   static final int USER_FIELD_COUNT = 4;

   public final void setUserDefinedField(int id, String value) {
      this._userDefinedFields[id] = value;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
      Array.resize(verbs, 0);
      if (uiField instanceof Object && uiField.isEditable()) {
         EditField editField = (EditField)uiField;
         int index = uiField.getIndex();
         if (index >= 0 && index < 4) {
            Array.resize(verbs, 1);
            verbs[0] = new EditLabelVerb(index, editField);
         }
      }

      return null;
   }

   @Override
   public final int match(Object criteria) {
      if (criteria instanceof Object) {
         SearchCriterion crit = (SearchCriterion)criteria;
         if (crit.getType() != 21) {
            return -1;
         }

         StringMatch stringMatch = (StringMatch)crit.getValue();

         for (int i = this._userDefinedFields.length - 1; i >= 0; i--) {
            if (Match.stringMatchMatch(stringMatch, this._userDefinedFields[i]) == 1) {
               return 1;
            }
         }
      }

      return -1;
   }

   public final String getUserDefinedField(int id) {
      return this._userDefinedFields[id];
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      boolean result = false;
      if (field instanceof Object) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         int fieldCount = vfm.getFieldCount();
         if (fieldCount == 4) {
            for (int i = 0; i < 4; i++) {
               String str = ((BasicEditField)vfm.getField(i)).getText();
               if (str != null && str.length() == 0) {
                  str = null;
               }

               this.setUserDefinedField(i, str);
               result |= str != null;
            }
         }
      }

      return result;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      String[] fields = this._userDefinedFields;
      int count = fields.length;
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;

         for (int i = 0; i < count; i++) {
            String field = fields[i];
            if (field != null && field.length() > 0) {
               syncBuffer.addField(UserFieldsModelFactory._addressBookSyncFieldIds[i], fields[i]);
            }
         }

         return true;
      } else {
         if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54)) {
            if (target instanceof Object) {
               StringBuffer stringBuffer = (StringBuffer)target;

               for (int i = 0; i < count; i++) {
                  String field = fields[i];
                  if (field != null && field.length() > 0) {
                     stringBuffer.append("\rUser");
                     stringBuffer.append(i + 1);
                     stringBuffer.append(':');
                     int length = field.length();
                     if (field.indexOf(10) != -1) {
                        for (int j = 0; j < length; j++) {
                           char c = field.charAt(j);
                           if (c == '\n') {
                              stringBuffer.append('\r').append('\t');
                           } else {
                              stringBuffer.append(c);
                           }
                        }
                     } else {
                        stringBuffer.append(field);
                     }
                  }
               }

               return true;
            }
         } else if (target instanceof Object[]) {
            Array.resize(target, count);
            System.arraycopy(fields, 0, target, 0, count);
            return true;
         }

         return false;
      }
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 5100 : 6100;
      } else {
         return 0;
      }
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final Field getField(Object context) {
      VerticalFieldManager vField = (VerticalFieldManager)(new Object(1152921504606846976L));
      long flags = ContextObject.getFlag(context, 0) ? 4503599627370496L : 9007199254740992L;

      for (int i = 0; i < this._userDefinedFields.length; i++) {
         String fieldLabel = this.getUserFieldLabel(i);
         String fieldText = this._userDefinedFields[i];
         if (fieldText != null || flags == 4503599627370496L) {
            if (fieldText == null) {
               fieldText = "";
            }

            EditField field = (EditField)(new Object(fieldLabel, fieldText, 3072, flags | 2199023255552L));
            field.setCookie(this);
            vField.add(field);
         }
      }

      vField.setCookie(this);
      return vField;
   }

   UserFieldsModel(Object initialData) {
      if (initialData instanceof Object[]) {
         String[] fields = (Object[])initialData;
         if (fields.length != 4) {
            throw new Object();
         }

         for (int i = 0; i < 4; i++) {
            this.setUserDefinedField(i, fields[i]);
         }
      } else if (initialData != null) {
         ContextObject.verifyNonNull(initialData);
      }
   }

   private final String getUserFieldLabel(int id) {
      return ((StringBuffer)(new Object())).append(AddressBookServices.getAddressBookOptions().getUserDefinedFieldLabel(id)).append(": ").toString();
   }
}
