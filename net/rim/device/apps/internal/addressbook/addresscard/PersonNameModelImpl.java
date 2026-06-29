package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

final class PersonNameModelImpl
   implements PersonNameModel,
   FieldProvider,
   PaintProvider,
   KeyProvider,
   ConversionProvider,
   Copyable,
   EncryptableProvider,
   MatchProvider {
   private Object _firstNameEncoding;
   private Object _firstNameYOMIEncoding;
   private Object _lastNameEncoding;
   private Object _lastNameYOMIEncoding;
   private Object _yomiKeywordsEncoding;
   private Object _salutationEncoding;
   private static WeakReference _paintBufferWR = new WeakReference(null);

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return paint(this.getSalutation(), this.getFirstName(), this.getLastName(), g, x, y, width, height, context);
   }

   @Override
   public final void setFirstName(String fn) {
      this._firstNameEncoding = AddressCardUtilities.encodeString(fn);
      this._firstNameYOMIEncoding = null;
   }

   @Override
   public final int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      if (!(crit.getValue() instanceof Object[])) {
         return -1;
      }

      Object[] values = (Object[])crit.getValue();
      String[] testWords = (String[])values[2];

      for (int i = testWords.length - 1; i >= 0; i--) {
         StringMatch stringMatch = new StringMatch(testWords[i], false, false);
         String strToMatch = this.getFirstName();
         if (strToMatch == null || stringMatch.indexOf(strToMatch) < 0) {
            strToMatch = this.getLastName();
            if (strToMatch == null || stringMatch.indexOf(strToMatch) < 0) {
               strToMatch = this.getSalutation();
               if (strToMatch == null || stringMatch.indexOf(strToMatch) < 0) {
                  return 0;
               }
            }
         }
      }

      return 1;
   }

   @Override
   public final void setLastName(String ln) {
      this._lastNameEncoding = AddressCardUtilities.encodeString(ln);
      this._lastNameYOMIEncoding = null;
   }

   @Override
   public final void setSalutation(String salutation) {
      this._salutationEncoding = AddressCardUtilities.encodeSalutation(salutation);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      return AddressCardUtilities.convertPersonNameModel(context, target, this);
   }

   @Override
   public final String getFirstNameYOMI() {
      return AddressCardUtilities.decodeString(this._firstNameYOMIEncoding);
   }

   @Override
   public final String getLastName() {
      return AddressCardUtilities.decodeString(this._lastNameEncoding);
   }

   @Override
   public final String getLastNameYOMI() {
      return AddressCardUtilities.decodeString(this._lastNameYOMIEncoding);
   }

   @Override
   public final String getSalutation() {
      return AddressCardUtilities.decodeString(this._salutationEncoding);
   }

   @Override
   public final Object copy() {
      return new PersonNameModelImpl(this.getSalutation(), this.getFirstName(), this.getLastName(), this.getFirstNameYOMI(), this.getLastNameYOMI());
   }

   @Override
   public final Field getField(Object context) {
      String salutation = this.getSalutation();
      String firstName = this.getFirstName();
      String lastName = this.getLastName();
      if (!ContextObject.getFlag(context, 0)) {
         long flags = 0;
         if ((ContextObject.getFlag(context, 128) || !ContextObject.getFlag(context, 11) || ContextObject.getFlag(context, 9))
            && !ContextObject.getFlag(context, 26)) {
            flags |= 18014398509481984L;
         } else {
            flags |= 36028797018963968L;
         }

         if (ContextObject.getFlag(context, 17)) {
            flags |= 64;
         }

         String name = this.toString();
         if (ContextObject.getFlag(context, 11)) {
            String firstNameYOMI = this.getFirstNameYOMI();
            String lastNameYOMI = this.getLastNameYOMI();
            if (firstNameYOMI != null || lastNameYOMI != null) {
               StringBuffer yomiBuffer = new StringBuffer();
               yomiBuffer.append(name);
               yomiBuffer.append(" (");
               if (Locale.getSystemNameOrder() == 1 && lastNameYOMI != null) {
                  yomiBuffer.append(lastNameYOMI);
               }

               if (firstNameYOMI != null) {
                  yomiBuffer.append(firstNameYOMI);
               }

               if (Locale.getSystemNameOrder() != 1 && lastNameYOMI != null) {
                  yomiBuffer.append(lastNameYOMI);
               }

               yomiBuffer.append(')');
               name = yomiBuffer.toString();
            }
         }

         Field field;
         if (ContextObject.getFlag(context, 106)) {
            field = new LabelField(name, flags);
         } else {
            field = new RichTextField(name, flags | 67108864);
         }

         field.setCookie(this);
         return field;
      } else if (ContextObject.getFlag(context, 16)) {
         String name = this.toString();
         if (name == null) {
            name = "";
         }

         return new AutoTextEditField(AddressBookResources.getString(105), name, 6156, 2199023255552L);
      } else {
         FirstNameFocuser vField = new FirstNameFocuser(1152921504606846976L);
         String s = lastName;
         if (s == null) {
            s = "";
         }

         PersonalNameField lastNameField = new PersonalNameField(AddressBookResources.getString(104), s, 2048, 4505800798126336L);
         lastNameField.setCookie(this);
         s = firstName;
         if (s == null) {
            s = "";
         }

         PersonalNameField firstNameField = new FirstNameEditField(AddressBookResources.getString(103), s, 2048, 4505800798126336L, lastNameField);
         firstNameField.setCookie(this);
         s = salutation;
         if (s == null) {
            s = "";
         }

         EditField salutationField = new AutoTextEditField(AddressBookResources.getString(106), s, 2048, 4505800798109696L);
         YomiField firstNameYOMIField = null;
         YomiField lastNameYOMIField = null;
         String firstNameYOMI = this.getFirstNameYOMI();
         String lastNameYOMI = this.getLastNameYOMI();
         boolean hasJapaneseInputMethod = (InputContext.getInstance().getAvailableInputMethods() & 512) != 0;
         if (firstNameYOMI != null || lastNameYOMI != null || hasJapaneseInputMethod) {
            boolean dualSelect = firstName != null && lastName != null;
            if (lastNameYOMI == null) {
               lastNameYOMI = "";
               dualSelect = false;
            }

            if (firstNameYOMI == null) {
               firstNameYOMI = "";
            } else {
               dualSelect = false;
            }

            firstNameYOMIField = new YomiField(AddressBookResources.getString(1744), firstNameYOMI, 2048, 4505800798109696L);
            firstNameYOMIField.setCookie(this);
            YomiFieldTextChangeListener firstNameYOMIlistener = new YomiFieldTextChangeListener(firstNameYOMIField);
            firstNameField.addTextChangeListener(firstNameYOMIlistener);
            firstNameField.setRemoveAllOnFirstKey(dualSelect);
            firstNameField.setPeerYomiField(firstNameYOMIField);
            lastNameYOMIField = new YomiField(AddressBookResources.getString(1745), lastNameYOMI, 2048, 4505800798109696L);
            lastNameYOMIField.setCookie(this);
            YomiFieldTextChangeListener lastNameYOMIlistener = new YomiFieldTextChangeListener(lastNameYOMIField);
            lastNameField.addTextChangeListener(lastNameYOMIlistener);
            lastNameField.setRemoveAllOnFirstKey(dualSelect);
            lastNameField.setPeerYomiField(lastNameYOMIField);
         }

         vField.add(salutationField);
         int displayOrder = this.getDisplayOrder(context);
         if (displayOrder == 1) {
            vField.add(lastNameField);
            if (lastNameYOMIField != null) {
               vField.add(lastNameYOMIField);
            }
         }

         vField.add(firstNameField);
         if (firstNameYOMIField != null) {
            vField.add(firstNameYOMIField);
         }

         if (displayOrder == 0) {
            vField.add(lastNameField);
            if (lastNameYOMIField != null) {
               vField.add(lastNameYOMIField);
            }
         }

         vField.setCookie(this);
         return vField;
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (field instanceof VerticalFieldManager) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         int fieldCount = vfm.getFieldCount();
         if (fieldCount == 3 || fieldCount == 5) {
            int firstNameYOMIIndex = -1;
            int lastNameYOMIIndex = -1;
            int displayOrder = this.getDisplayOrder(context);
            int lastNameIndex;
            int firstNameIndex;
            if (displayOrder == 0) {
               firstNameIndex = 1;
               if (fieldCount == 5) {
                  firstNameYOMIIndex = 2;
                  lastNameIndex = 3;
                  lastNameYOMIIndex = 4;
               } else {
                  lastNameIndex = 2;
               }
            } else {
               lastNameIndex = 1;
               if (fieldCount == 5) {
                  lastNameYOMIIndex = 2;
                  firstNameIndex = 3;
                  firstNameYOMIIndex = 4;
               } else {
                  firstNameIndex = 2;
               }
            }

            String salutation = ((EditField)vfm.getField(0)).getText().trim();
            String firstName = ((EditField)vfm.getField(firstNameIndex)).getText();
            if (firstName.startsWith("  ")) {
               firstName = " " + firstName.trim();
            }

            String lastName = ((EditField)vfm.getField(lastNameIndex)).getText().trim();
            this.setNames(salutation, firstName, lastName);
            if (fieldCount == 5) {
               String firstNameYOMI = ((EditField)vfm.getField(firstNameYOMIIndex)).getText().trim();
               String lastNameYOMI = ((EditField)vfm.getField(lastNameYOMIIndex)).getText().trim();
               this.setFullNameYOMI(firstNameYOMI, lastNameYOMI);
            }

            if (firstName.length() <= 0 && lastName.length() <= 0 && salutation.length() <= 0) {
               return false;
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      if (field instanceof VerticalFieldManager) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         int fieldCount = vfm.getFieldCount();
         int lastNameIndex = 2;
         if (fieldCount == 5) {
            lastNameIndex = 3;
         }

         if (fieldCount == 3 || fieldCount == 5) {
            if (ContextObject.getFlag(context, 95)) {
               String firstName = ((EditField)vfm.getField(1)).getText().trim();
               String lastName = ((EditField)vfm.getField(lastNameIndex)).getText().trim();
               if (firstName.length() == 0 && lastName.length() == 0) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 1500 : 0;
      } else {
         return ContextObject.getFlag(context, 20) && ContextObject.getFlag(context, 4) ? 15300 : 0;
      }
   }

   @Override
   public final void setNames(String salutation, String firstName, String lastName) {
      this.setSalutation(salutation);
      this.setFirstName(firstName);
      this.setLastName(lastName);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int keyCount = 0;
      if (index + 5 > keyArray.length) {
         Array.resize(keyArray, index + 5);
      }

      String salutation = this.getSalutation();
      String firstName = this.getFirstName();
      String lastName = this.getLastName();
      String firstNameYOMI = AddressCardUtilities.convertYomi(this.getFirstNameYOMI());
      String lastNameYOMI = AddressCardUtilities.convertYomi(this.getLastNameYOMI());
      boolean hasYOMI = firstNameYOMI != null || lastNameYOMI != null;
      if (keyRequested != 1232448844688687736L && keyRequested != 4922084531409364683L) {
         if (keyRequested != -227891759293611117L && keyRequested != 8199160529614935340L) {
            if (keyRequested == -6544199576583918793L) {
               keyArray[index++] = salutation;
               keyArray[index++] = firstName;
               keyArray[index++] = lastName;
               keyCount = 3;
               if (this._yomiKeywordsEncoding != null) {
                  keyArray[index++] = this.getYOMIKeywords();
                  keyCount++;
               }
            }
         } else {
            if (lastNameYOMI != null) {
               keyArray[index++] = lastNameYOMI;
               keyCount++;
            } else if (lastName != null) {
               keyArray[index++] = lastName;
               keyCount++;
            }

            if (!hasYOMI && salutation != null && keyRequested == -227891759293611117L) {
               keyArray[index++] = salutation;
               keyCount++;
            }

            if (firstNameYOMI != null) {
               keyArray[index++] = firstNameYOMI;
               keyCount++;
            } else if (firstName != null) {
               keyArray[index++] = firstName;
               keyCount++;
            }

            if (hasYOMI) {
               if (lastName != null && lastNameYOMI != null) {
                  keyArray[index++] = lastName;
                  keyCount++;
               }

               if (firstName != null && firstNameYOMI != null) {
                  keyArray[index] = firstName;
                  keyCount++;
               }
            }
         }
      } else {
         if (keyRequested != 1232448844688687736L && salutation != null) {
            keyArray[index++] = salutation;
            keyCount++;
         }

         if (firstNameYOMI != null) {
            keyArray[index++] = firstNameYOMI;
            keyCount++;
         } else if (firstName != null) {
            keyArray[index++] = firstName;
            keyCount++;
         }

         if (lastNameYOMI != null) {
            keyArray[index++] = lastNameYOMI;
            keyCount++;
         } else if (lastName != null) {
            keyArray[index++] = lastName;
            keyCount++;
         }

         if (hasYOMI) {
            if (firstName != null && firstNameYOMI != null) {
               keyArray[index++] = firstName;
               keyCount++;
            }

            if (lastName != null && lastNameYOMI != null) {
               keyArray[index] = lastName;
               keyCount++;
            }
         }
      }

      return keyCount;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final String getFirstName() {
      return AddressCardUtilities.decodeString(this._firstNameEncoding);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._firstNameEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._lastNameEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._salutationEncoding, false, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._firstNameEncoding = PersistentContent.reEncode(this._firstNameEncoding, false, encrypt);
      this._lastNameEncoding = PersistentContent.reEncode(this._lastNameEncoding, false, encrypt);
      this._salutationEncoding = PersistentContent.reEncode(this._salutationEncoding, false, encrypt);
      return null;
   }

   @Override
   public final void setFullNameYOMI(String fny, String lny) {
      this._firstNameYOMIEncoding = AddressCardUtilities.encodeString(fny);
      this._lastNameYOMIEncoding = AddressCardUtilities.encodeString(lny);
      this._yomiKeywordsEncoding = AddressCardUtilities.buildYOMIKeywordsEncoding(this.getFirstName(), this.getLastName(), fny, lny);
   }

   PersonNameModelImpl() {
   }

   static final int paint(String salutation, String firstName, String lastName, Graphics g, int x, int y, int width, int height, Object context) {
      int flags = ContextObject.getFlag(context, 17) ? 64 : 0;
      Object sortOrderObject = ContextObject.get(context, 614335798810617774L);
      long sortOrder;
      if (!(sortOrderObject instanceof Long)) {
         switch (Locale.getSystemNameOrder()) {
            case 1:
               sortOrder = -227891759293611117L;
               break;
            default:
               sortOrder = 1232448844688687736L;
         }
      } else {
         sortOrder = (Long)sortOrderObject;
      }

      String firstNameSeparator = Locale.getPersonalNamesSeparator(0);
      String name;
      if (sortOrder != -227891759293611117L && sortOrder != -4388042602796535003L) {
         name = firstName;
         if (lastName != null) {
            if (name != null) {
               StringBuffer _paintBuffer = WeakReferenceUtilities.getStringBuffer(_paintBufferWR);
               _paintBuffer.setLength(0);
               if (salutation == null) {
                  _paintBuffer.append(name).append(firstNameSeparator).append(lastName);
               } else {
                  _paintBuffer.append(salutation).append(firstNameSeparator).append(name).append(firstNameSeparator).append(lastName);
               }

               y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), _paintBuffer, 0, _paintBuffer.length(), y);
               return g.drawText(_paintBuffer, 0, _paintBuffer.length(), x, y, flags, width);
            }

            name = lastName;
         }
      } else {
         String lastNameSeparator = Locale.getPersonalNamesSeparator(1);
         name = lastName;
         if (firstName != null || salutation != null) {
            StringBuffer _paintBuffer = WeakReferenceUtilities.getStringBuffer(_paintBufferWR);
            _paintBuffer.setLength(0);
            switch (Locale.getDefaultForSystem().getCode()) {
               case 1784741888:
               case 2053636096:
               case 2053653326:
               case 2053654603:
                  if (salutation != null) {
                     _paintBuffer.append(salutation).append(firstNameSeparator);
                  }

                  if (name != null) {
                     _paintBuffer.append(name);
                     if (firstName != null) {
                        _paintBuffer.append(lastNameSeparator);
                     }
                  }

                  if (firstName != null) {
                     _paintBuffer.append(firstName);
                  }
                  break;
               default:
                  if (name != null) {
                     if (salutation == null) {
                        _paintBuffer.append(name);
                        if (firstName != null) {
                           _paintBuffer.append(lastNameSeparator).append(firstName);
                        }
                     } else {
                        _paintBuffer.append(name).append(lastNameSeparator).append(salutation);
                        if (firstName != null) {
                           _paintBuffer.append(firstNameSeparator).append(firstName);
                        }
                     }
                  } else if (salutation == null) {
                     _paintBuffer.append(firstName);
                  } else {
                     _paintBuffer.append(salutation);
                     if (firstName != null) {
                        _paintBuffer.append(firstNameSeparator).append(firstName);
                     }
                  }
            }

            y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), _paintBuffer, 0, _paintBuffer.length(), y);
            return g.drawText(_paintBuffer, 0, _paintBuffer.length(), x, y, flags, width);
         }
      }

      if (name != null) {
         if (salutation == null) {
            y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), name, y);
            return g.drawText(name, x, y, flags, width);
         } else {
            StringBuffer _paintBuffer = WeakReferenceUtilities.getStringBuffer(_paintBufferWR);
            _paintBuffer.setLength(0);
            _paintBuffer.append(salutation).append(firstNameSeparator).append(name);
            y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), _paintBuffer, 0, _paintBuffer.length(), y);
            return g.drawText(_paintBuffer, 0, _paintBuffer.length(), x, y, flags, width);
         }
      } else {
         return salutation != null ? g.drawText(salutation, x, y, flags, width) : 0;
      }
   }

   PersonNameModelImpl(String salutation, String firstName, String lastName, String firstNameYOMI, String lastNameYOMI) {
      this(salutation, firstName, lastName);
      this.setFullNameYOMI(firstNameYOMI, lastNameYOMI);
   }

   @Override
   public final String toString() {
      StringBuffer result = CompressedAddressCardModel.toString(this.getSalutation(), this.getFirstName(), this.getLastName());
      return result.toString();
   }

   private final int getDisplayOrder(Object context) {
      Object locale = ContextObject.get(context, 1387359264132630355L);
      int localeCode;
      if (!(locale instanceof Locale)) {
         localeCode = Locale.getDefaultForSystem().getCode();
      } else {
         localeCode = ((Locale)locale).getCode();
      }

      switch (localeCode) {
         case 1784741887:
            return 0;
         case 1784741888:
         default:
            return 1;
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof PersonNameModel)) {
         return false;
      }

      PersonNameModel otherModel = (PersonNameModel)o;
      return !StringUtilities.strEqualIgnoreCase(this.getFirstName(), otherModel.getFirstName())
         ? false
         : StringUtilities.strEqualIgnoreCase(this.getLastName(), otherModel.getLastName());
   }

   private final String getYOMIKeywords() {
      return AddressCardUtilities.decodeString(this._yomiKeywordsEncoding);
   }

   PersonNameModelImpl(Object initialData) {
      ContextObject contextObject = ContextObject.verifyNonNull(initialData);
      Object test = contextObject.get(251);
      if (test != null) {
         String[] stringPair = (String[])test;
         this.setNames(null, stringPair[0], stringPair[1]);
      } else {
         test = contextObject.get(3129577024825566583L);
         if (test != null) {
            String[] stringArray = (String[])test;
            this.setNames(stringArray[0], stringArray[1], stringArray[2]);
         } else {
            test = contextObject.get(253);
            if (test != null) {
               this.setNames(null, (String)test, null);
            }
         }
      }
   }

   PersonNameModelImpl(String salutation, String firstName, String lastName) {
      this.setNames(salutation, firstName, lastName);
   }
}
