package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.AddressBookNameSearch;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.vm.WeakReference;

public final class NameSearchModel extends AddressBookNameSearch implements PersistableRIMModel, FieldProvider, ConversionProvider, EncryptableProvider {
   private int _headerType = 5;
   private Object _nameValueEncoding;
   private static WeakReference _valueWR = (WeakReference)(new Object(null));
   private static NameSearchModel _valueModel;
   private static final int[] _headerTypeMap = new int[]{5, 4, 6, 7, 8, -804651006, 33, 34, -804651006, 35, 36, -804650998, 46, 47, 48, 49, 57, 59, 62, 63};

   public final void establishValue() {
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         String value = PersistentContent.decodeString(this._nameValueEncoding);
         if (value != null) {
            syncBuffer.addField(5, value);
            syncBuffer.addInt(6, this._headerType, 4);
         }

         return true;
      } else {
         return false;
      }
   }

   protected final boolean setValue(String name, int headerType) {
      clearValue(this);
      if (name != null && name.length() != 0) {
         this._nameValueEncoding = PersistentContent.encode(name);
         this._headerType = headerType;
         return true;
      } else {
         this._nameValueEncoding = null;
         this._headerType = 5;
         return false;
      }
   }

   public final void setType(int type) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setNameString(String value) {
      this.setValue(value, this._headerType);
   }

   @Override
   public final int getOrder(Object context) {
      return 12000;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      this.setValue(null, 0);
      if (!(field instanceof Object)) {
         return false;
      }

      VerticalFieldManager vfm = (VerticalFieldManager)field;
      EditField editField = (EditField)vfm.getField(0);
      ObjectChoiceField choiceField = (ObjectChoiceField)vfm.getField(1);
      return this.setValue(editField.getText(), _headerTypeMap[choiceField.getSelectedIndex()]);
   }

   @Override
   public final Field getField(Object context) {
      if (!ContextObject.getFlag(context, 0)) {
         return null;
      }

      Field field = (Field)(new Object(SearchResources.getString(18), PersistentContent.decodeString(this._nameValueEncoding), 1000000, 4505800798109696L));
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)(new Object(1152921504606846976L));
      field.setCookie(this);
      vifm.add(field);
      vifm.setCookie(this);
      Object[] choices = new Object[]{
         SearchResources.getString(20),
         SearchResources.getString(19),
         SearchResources.getString(21),
         SearchResources.getString(22),
         SearchResources.getString(23)
      };
      int headerType = 0;

      for (int i = 0; i < _headerTypeMap.length; i++) {
         if (this._headerType == _headerTypeMap[i]) {
            headerType = i;
            break;
         }
      }

      vifm.add((Field)(new Object(SearchResources.getString(24), choices, choices[headerType])), 16);
      return vifm;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._nameValueEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._nameValueEncoding = PersistentContent.reEncode(this._nameValueEncoding, compress, encrypt);
      return null;
   }

   private static final synchronized void clearValue(NameSearchModel model) {
      if (model == _valueModel) {
         _valueWR.set(null);
      }
   }

   private static final synchronized Object getValue(NameSearchModel model) {
      Object value = _valueWR.get();
      if (value != null && model == _valueModel) {
         return value;
      }

      if (model._nameValueEncoding == null) {
         return null;
      }

      String name = PersistentContent.decodeString(model._nameValueEncoding);
      value = model.establishValue(name);
      _valueWR.set(value);
      _valueModel = model;
      return value;
   }

   @Override
   public final int getType() {
      return this._headerType;
   }

   @Override
   public final Object getValue() {
      return getValue(this);
   }
}
