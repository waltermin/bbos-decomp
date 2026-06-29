package net.rim.device.apps.internal.lbs.model;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class SearchAddressModel implements MailingAddressModel, FieldProvider, ConversionProvider {
   private byte[] _data;
   private int _type;
   private static final byte ADDRESS_TAG = 1;
   private static final byte CITY_TAG = 2;
   private static final byte AREA_TAG = 3;
   private static final byte COUNTRY_TAG = 4;
   private static final byte COMPRESSED_COUNTRY_TAG = 5;
   private static String[] _countryNames = new String[]{"United States of America", "Canada"};

   @Override
   public final void setType(int type) {
      if (type != 0 && type != 1) {
         throw new IllegalArgumentException();
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
   }

   @Override
   public final void setCity(String value) {
      this._data = ConverterUtilities.addTag(this._data, (byte)2, value);
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
      } else {
         return false;
      }
   }

   @Override
   public final void setCountry(String value) {
      if (value != null) {
         value = value.trim();
         int index = lookupCountry(value);
         if (index != -1) {
            this._data = ConverterUtilities.removeTag(this._data, (byte)4, true);
            this._data = ConverterUtilities.addByteTag(this._data, (byte)5, (byte)index);
            return;
         }
      }

      this._data = ConverterUtilities.removeTag(this._data, (byte)5, false);
      this._data = ConverterUtilities.addTag(this._data, (byte)4, value);
   }

   @Override
   public final String getZipOrPostalCode() {
      return null;
   }

   @Override
   public final void setZipOrPostalCode(String c) {
   }

   @Override
   public final boolean hasData() {
      return this._data != null;
   }

   @Override
   public final String getAddressLine1() {
      String str = ConverterUtilities.getTagData(this._data, (byte)1);
      if (str != null) {
         while (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
         }
      }

      if (str == null) {
         str = "";
      }

      return str;
   }

   @Override
   public final String getAddressLine2() {
      return null;
   }

   @Override
   public final String getCity() {
      String str = ConverterUtilities.getTagData(this._data, (byte)2);
      if (str == null) {
         str = "";
      }

      return str;
   }

   @Override
   public final String getArea() {
      String str = ConverterUtilities.getTagData(this._data, (byte)3);
      if (str == null) {
         str = "";
      }

      return str;
   }

   @Override
   public final String getCountry() {
      byte index = ConverterUtilities.getTagByteData(this._data, (byte)5);
      if (index != -1) {
         return _countryNames[index];
      }

      String str = ConverterUtilities.getTagData(this._data, (byte)4);
      if (str == null) {
         str = "";
      }

      return str;
   }

   @Override
   public final Field getField(Object context) {
      VerticalFieldManager vfm = new VerticalFieldManager(1152921504606846976L);
      vfm.setCookie(this);
      this.addField(vfm, 128, this.getAddressLine1(), false);
      this.addField(vfm, 702, this.getCity(), true);
      this.addField(vfm, 703, this.getArea(), true);
      this.addField(vfm, 705, this.getCountry(), true);
      return vfm;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof VerticalFieldManager)) {
         throw new RuntimeException();
      }

      VerticalFieldManager vfm = (VerticalFieldManager)field;
      EditField ef = (EditField)vfm.getField(0);
      this.setAddressLine1(ef.getText().trim());
      ef = (EditField)vfm.getField(1);
      this.setCity(ef.getText().trim());
      ef = (EditField)vfm.getField(2);
      this.setArea(ef.getText().trim());
      ef = (EditField)vfm.getField(3);
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
      this._data = ConverterUtilities.addTag(this._data, (byte)3, value);
   }

   public static final int lookupCountry(String value) {
      for (int i = _countryNames.length - 1; i >= 0; i--) {
         if (StringUtilities.compareToIgnoreCase(_countryNames[i], value) == 0) {
            return i;
         }
      }

      return -1;
   }

   private final void addField(VerticalFieldManager vfm, int resourceId, String value, boolean useAddressBookResource) {
      AutoTextEditField field = new AutoTextEditField(
         useAddressBookResource ? AddressBookResources.getString(resourceId) : LBSResources.getString(resourceId), value, 2048, 4505800798109696L
      );
      field.setCookie(this);
      vfm.add(field);
   }

   private final void convertField(SyncBuffer sb, int id, String value) {
      if (value != null) {
         sb.addField(id, value);
      }
   }

   public SearchAddressModel(Object initialData) {
      if (initialData != null) {
         ContextObject.verifyNonNull(initialData);
      }

      this._data = null;
      this._type = 0;
   }
}
