package net.rim.device.apps.internal.addressbook.mailingaddress;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public final class MailingAddressModelImpl implements MailingAddressModel, FieldProvider, ConversionProvider, FieldLabelProvider {
   private byte[] _data;
   private int _type;
   private static final byte ADDRESS1_TAG;
   private static final byte ADDRESS2_TAG;
   private static final byte CITY_TAG;
   private static final byte AREA_TAG;
   private static final byte ZIP_TAG;
   private static final byte COUNTRY_TAG;
   private static final byte COMPRESSED_COUNTRY_TAG;
   private static final int REVERSE_ORDER;
   private static final int FORWARD_ORDER;
   private static String[] _countryNames = new String[]{"United States of America", "Canada"};

   @Override
   public final void setType(int type) {
      if (type != 0 && type != 1) {
         throw new Object();
      }

      this._type = type;
   }

   @Override
   public final int getType() {
      return this._type;
   }

   @Override
   public final void setAddressLine1(String value) {
      this._data = ConverterUtilities.addTag(this._data, (byte)1, value);
   }

   @Override
   public final void setAddressLine2(String value) {
      this._data = ConverterUtilities.addTag(this._data, (byte)2, value);
   }

   @Override
   public final void setCity(String value) {
      this._data = ConverterUtilities.addTag(this._data, (byte)3, value);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         boolean work = this._type == 0;
         this.convertField(syncBuffer, work ? 35 : 61, this.getAddressLine1());
         this.convertField(syncBuffer, work ? 36 : 62, this.getAddressLine2());
         this.convertField(syncBuffer, work ? 38 : 69, this.getCity());
         this.convertField(syncBuffer, work ? 39 : 70, this.getArea());
         this.convertField(syncBuffer, work ? 40 : 71, this.getZipOrPostalCode());
         this.convertField(syncBuffer, work ? 41 : 72, this.getCountry());
         return true;
      } else if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54) && target instanceof Object) {
         StringBuffer stringBuffer = (StringBuffer)target;
         this.convertField(stringBuffer, "Address1", this.getAddressLine1());
         this.convertField(stringBuffer, "Address2", this.getAddressLine2());
         this.convertField(stringBuffer, "City", this.getCity());
         this.convertField(stringBuffer, "State/Prov", this.getArea());
         this.convertField(stringBuffer, "ZIP/Postal Code", this.getZipOrPostalCode());
         this.convertField(stringBuffer, "Country", this.getCountry());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void setZipOrPostalCode(String value) {
      if (value != null) {
         value = value.toUpperCase();
      }

      this._data = ConverterUtilities.addTag(this._data, (byte)5, value);
   }

   @Override
   public final void setCountry(String value) {
      if (value != null) {
         value = value.trim();
         int index = lookupCountry(value);
         if (index != -1) {
            this._data = ConverterUtilities.removeTag(this._data, (byte)6, true);
            this._data = ConverterUtilities.addByteTag(this._data, (byte)7, (byte)index);
            return;
         }
      }

      this._data = ConverterUtilities.removeTag(this._data, (byte)7, false);
      this._data = ConverterUtilities.addTag(this._data, (byte)6, value);
   }

   @Override
   public final boolean hasData() {
      return this._data != null;
   }

   @Override
   public final String getAddressLine1() {
      return ConverterUtilities.getTagData(this._data, (byte)1);
   }

   @Override
   public final String getAddressLine2() {
      return ConverterUtilities.getTagData(this._data, (byte)2);
   }

   @Override
   public final String getCity() {
      return ConverterUtilities.getTagData(this._data, (byte)3);
   }

   @Override
   public final String getArea() {
      return ConverterUtilities.getTagData(this._data, (byte)4);
   }

   @Override
   public final String getZipOrPostalCode() {
      return ConverterUtilities.getTagData(this._data, (byte)5);
   }

   @Override
   public final String getCountry() {
      byte index = ConverterUtilities.getTagByteData(this._data, (byte)7);
      return index != -1 ? _countryNames[index] : ConverterUtilities.getTagData(this._data, (byte)6);
   }

   @Override
   public final Field getField(Object context) {
      int displayOrder = this.getDisplayOrder(context);
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(1152921504606846976L));
      vfm.setCookie(this);
      String label = this.getLabel();
      if (!ContextObject.getFlag(context, 0)) {
         Field labelField = (Field)(new Object(label));
         if (ContextObject.getFlag(context, 128)) {
            labelField.setTag(Tag.create("addressbook-mailingaddress-title"));
            vfm.setTag(Tag.create("addressbook-mailingaddress"));
         }

         vfm.add(labelField);
         StringBuffer line = (StringBuffer)(new Object());
         StringBuffer address = (StringBuffer)(new Object());
         String zip = this.getZipOrPostalCode();
         String area = this.getArea();
         String city = this.getCity();
         if (displayOrder != 1) {
            this.addLine(address, this.getCountry());
            if (zip != null) {
               line.append(zip);
            }

            if (area != null) {
               if (line.length() > 0) {
                  line.append(' ');
               }

               line.append(area);
            }

            if (city != null) {
               if (line.length() > 0) {
                  line.append(' ');
               }

               line.append(city);
            }

            if (line.length() > 0) {
               this.addLine(address, line.toString());
            }

            this.addLine(address, this.getAddressLine1());
            this.addLine(address, this.getAddressLine2());
            if (address.length() > 0) {
               this.addField(vfm, 0, address.toString(), false);
            }
         } else {
            this.addLine(address, this.getAddressLine1());
            this.addLine(address, this.getAddressLine2());
            if (city != null) {
               line.append(city);
               if (area != null) {
                  line.append(", ");
               }
            }

            if (area != null) {
               line.append(area);
            }

            if (zip != null) {
               if (line.length() > 0) {
                  line.append(' ');
               }

               line.append(zip);
            }

            if (line.length() > 0) {
               this.addLine(address, line.toString());
            }

            this.addLine(address, this.getCountry());
            if (address.length() > 0) {
               this.addField(vfm, 0, address.toString(), false);
            }
         }

         return vfm.getFieldCount() == 1 ? null : vfm;
      } else {
         int colon = label.indexOf(58);
         if (colon == -1) {
            colon = label.indexOf(65306);
         }

         if (colon != -1) {
            label = label.substring(0, colon);
         }

         Field labelField = (Field)(new Object(label));
         vfm.add(labelField);
         if (ContextObject.getFlag(context, 128)) {
            labelField.setTag(Tag.create("addressbook-mailingaddress-title-edit"));
            vfm.setTag(Tag.create("addressbook-mailingaddress-edit"));
         }

         EditField zipField = (EditField)(new Object(AddressBookResources.getString(704), this.getZipOrPostalCode(), 2048, 4505800798109696L));
         zipField.setFilter(new MailingAddressModelImpl$1(this));
         zipField.setCookie(this);
         zipField.setPadding(0, 0, 0, 8);
         if (displayOrder == 1) {
            this.addField(vfm, 700, this.getAddressLine1(), true);
            this.addField(vfm, 701, this.getAddressLine2(), true);
            this.addField(vfm, 702, this.getCity(), true);
            this.addField(vfm, 703, this.getArea(), true);
            vfm.add(zipField);
            this.addField(vfm, 705, this.getCountry(), true);
            return vfm;
         } else {
            this.addField(vfm, 705, this.getCountry(), true);
            vfm.add(zipField);
            this.addField(vfm, 703, this.getArea(), true);
            this.addField(vfm, 702, this.getCity(), true);
            this.addField(vfm, 700, this.getAddressLine1(), true);
            this.addField(vfm, 701, this.getAddressLine2(), true);
            return vfm;
         }
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof Object)) {
         throw new Object();
      }

      VerticalFieldManager vfm = (VerticalFieldManager)field;
      if (vfm.getFieldCount() != 7) {
         return false;
      }

      int line1;
      int line2;
      int city;
      int area;
      int zip;
      int country;
      if (this.getDisplayOrder(context) == 1) {
         line1 = 1;
         line2 = 2;
         city = 3;
         area = 4;
         zip = 5;
         country = 6;
      } else {
         country = 1;
         zip = 2;
         area = 3;
         city = 4;
         line1 = 5;
         line2 = 6;
      }

      EditField ef = (EditField)vfm.getField(line1);
      this.setAddressLine1(ef.getText().trim());
      ef = (EditField)vfm.getField(line2);
      this.setAddressLine2(ef.getText().trim());
      ef = (EditField)vfm.getField(city);
      this.setCity(ef.getText().trim());
      ef = (EditField)vfm.getField(area);
      this.setArea(ef.getText().trim());
      ef = (EditField)vfm.getField(zip);
      this.setZipOrPostalCode(ef.getText().trim());
      ef = (EditField)vfm.getField(country);
      this.setCountry(ef.getText().trim());
      return this.hasData();
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      boolean editable = ContextObject.getFlag(context, 0);
      if (this._type == 0) {
         return editable ? 4100 : 4500;
      } else {
         return editable ? 4200 : 4600;
      }
   }

   @Override
   public final void setArea(String value) {
      this._data = ConverterUtilities.addTag(this._data, (byte)4, value);
   }

   @Override
   public final String getLabel() {
      return AddressBookResources.getString(this._type == 0 ? 1728 : 1729);
   }

   @Override
   public final void setLabel(String label) {
   }

   @Override
   public final void setLabelStringProvider(StringProvider label) {
      throw new Object("Unsupported API");
   }

   public static final String getCountry(int index) {
      return _countryNames[index];
   }

   private final void addLine(StringBuffer address, String lineToAdd) {
      if (lineToAdd != null && lineToAdd.length() > 0) {
         if (address.length() != 0) {
            address.append('\n');
         }

         address.append(lineToAdd);
      }
   }

   public static final int lookupCountry(String value) {
      for (int i = _countryNames.length - 1; i >= 0; i--) {
         if (StringUtilities.compareToIgnoreCase(_countryNames[i], value) == 0) {
            return i;
         }
      }

      return -1;
   }

   private final void addField(VerticalFieldManager vfm, int resourceId, String value, boolean editable) {
      Field field;
      if (editable) {
         field = (Field)(new Object(AddressBookResources.getString(resourceId), value, 2048, 4505800798109696L));
      } else {
         if (value == null || value.length() <= 0) {
            return;
         }

         field = (Field)(new Object(value, 27021597764222976L));
      }

      field.setPadding(0, 0, 0, 8);
      field.setCookie(this);
      vfm.add(field);
   }

   MailingAddressModelImpl(Object initialData) {
      if (initialData != null) {
         ContextObject.verifyNonNull(initialData);
      }

      this._data = null;
      this._type = 0;
   }

   private final void convertField(SyncBuffer sb, int id, String value) {
      if (value != null) {
         sb.addField(id, value);
      }
   }

   private final void convertField(StringBuffer sb, String label, String value) {
      if (value != null) {
         sb.append('\r');
         if (this._type == 1) {
            sb.append("Home ");
         }

         sb.append(label);
         sb.append(':');
         sb.append(value);
      }
   }

   private final int getDisplayOrder(Object context) {
      Object locale = ContextObject.get(context, 1387359264132630355L);
      int localeCode;
      if (!(locale instanceof Object)) {
         localeCode = Locale.getDefaultForSystem().getCode();
      } else {
         localeCode = ((Locale)locale).getCode();
      }

      switch (localeCode) {
         case 1784741888:
         case 1920270336:
            return 0;
         default:
            return 1;
      }
   }
}
