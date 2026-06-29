package net.rim.device.apps.internal.blackberryemail.address;

import java.util.Vector;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailAddressModelImpl
   implements EmailAddressModel,
   VerbProvider,
   FieldProvider,
   PaintProvider,
   KeyProvider,
   ConversionProvider,
   MatchProvider,
   VerbDescriptionProvider,
   Copyable,
   CloneProvider,
   EncryptableProvider,
   ActiveFieldCookie,
   URLProvider,
   FieldLabelProvider,
   SyncFieldIDProvider,
   AccessibleContextProxy {
   private Object _dataEncoding;
   private boolean _isFreeForm;
   private static final String EMAIL_FIELD_NAME = "\rEmail:";

   @Override
   public final String getURL() {
      return "mailto:" + this.getAddress();
   }

   @Override
   public final int getURLType() {
      return 2;
   }

   @Override
   public final int match(Object criteria) {
      return AddressUtilities.match(this, this.getData(), criteria);
   }

   @Override
   public final void setAddress(String address) {
      this.setData(AddressUtilities.initializeData(address, this.getFriendlyName()));
   }

   @Override
   public final String getVerbDescription(Object context) {
      return AddressUtilities.getVerbDescription(this, this.getData(), context);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      return AddressUtilities.convert(this, this.getData(), this.getSyncFieldId(null), "\rEmail:", context, target);
   }

   @Override
   public final Object clone(Object context) {
      return new EmailAddressModelImpl(this);
   }

   @Override
   public final String getAddress() {
      return AddressUtilities.getAddress(this.getData());
   }

   @Override
   public final void setAddressAndFriendlyName(String address, String fn) {
      this.setData(AddressUtilities.initializeData(address, fn));
   }

   @Override
   public final Field getField(Object context) {
      return AddressUtilities.getField(this.getSyncFieldId(null), 40, this, this.getData(), context);
   }

   @Override
   public final String getLabel() {
      return EmailResources.getString(41);
   }

   @Override
   public final void setLabel(String label) {
   }

   @Override
   public final void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof EmailAddressEditField)) {
         return false;
      }

      EmailAddressEditField editField = (EmailAddressEditField)field;
      String address = editField.getText().trim();
      this.setData(AddressUtilities.initializeData(address, null));
      return address.length() > 0;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final void setFreeForm(boolean isFreeForm) {
      this._isFreeForm = isFreeForm;
   }

   @Override
   public final boolean isFreeForm() {
      return this._isFreeForm;
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 2500 : 3100;
      } else {
         return 0;
      }
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return AddressUtilities.getKeys(this.getData(), context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return AddressUtilities.getKeys(this.getData(), context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return AddressUtilities.paint(this, this.getData(), g, x, y, width, height, context);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return AddressUtilities.getVerbs(this, -2985347935260258684L, 327952, context, verbs);
   }

   @Override
   public final int getSyncFieldId(Object context) {
      return 1;
   }

   @Override
   public final String getFriendlyName() {
      return AddressUtilities.getFriendlyName(this.getData());
   }

   @Override
   public final Object copy() {
      return new EmailAddressModelImpl(this);
   }

   @Override
   public final void setFriendlyName(String fn) {
      this.setData(AddressUtilities.initializeData(this.getAddress(), fn));
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._dataEncoding, false, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      if (ObjectGroup.isInGroup(this)) {
         EmailAddressModelImpl newModel = new EmailAddressModelImpl(this);
         newModel.reCrypt(compress, encrypt);
         return newModel;
      } else {
         this._dataEncoding = PersistentContent.reEncode(this._dataEncoding, false, encrypt);
         return null;
      }
   }

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return ControllerUtilities.invokeApplicationKeyVerb(this);
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   @Override
   public final AccessibleContext getAccessibleContext() {
      String _accessibleName = this.getFriendlyName();
      if (_accessibleName == null) {
         _accessibleName = this.getAddress();
      }

      return new AccessibleContextFactory(_accessibleName);
   }

   private final void setData(String data) {
      this.setData(data, true);
   }

   private final String getData() {
      try {
         return PersistentContent.decodeString(this._dataEncoding);
      } finally {
         ;
      }
   }

   private final void setData(String data, boolean encrypt) {
      if (data == null) {
         data = "";
      }

      this._dataEncoding = PersistentContent.encode(data, false, encrypt);
   }

   public EmailAddressModelImpl(Object initialData) {
      String data = null;
      if (!(initialData instanceof String)) {
         if (initialData != null) {
            ContextObject contextObject = ContextObject.verifyNonNull(initialData);
            Object test = contextObject.get(254);
            if (test instanceof EmailAddressModel) {
               EmailAddressModelImpl model = (EmailAddressModelImpl)test;
               data = model.getData();
               this._isFreeForm = model.isFreeForm();
            } else {
               data = AddressUtilities.initializeData(contextObject);
            }
         }
      } else {
         data = AddressUtilities.initializeData((String)initialData);
      }

      boolean skipEncryption = ContextObject.castOrCreate(initialData).getPrivateFlag(4567630869418996525L, 0);
      this.setData(data, !skipEncryption);
   }

   public EmailAddressModelImpl(EmailAddressModelImpl model) {
      if (model != null) {
         this._dataEncoding = PersistentContent.copyEncoding(model._dataEncoding);
         this._isFreeForm = model.isFreeForm();
      }
   }

   @Override
   public final String toString() {
      return AddressUtilities.toString(this.getData());
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof EmailAddressModelImpl)) {
         return false;
      }

      EmailAddressModelImpl addressModel = (EmailAddressModelImpl)object;
      return StringUtilities.strEqualIgnoreCase(this.getData(), addressModel.getData(), 1701707776);
   }
}
