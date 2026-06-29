package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.vm.WeakReference;

public final class SubjectSearchModel implements PersistableRIMModel, SearchCriterion, FieldProvider, ConversionProvider, EncryptableProvider {
   private Object _subjectEncoding;
   private static WeakReference _valueWR = new WeakReference(null);
   private static SubjectSearchModel _valueModel;

   public final boolean setValue(String subject) {
      clearValue(this);
      if (subject != null && subject.length() != 0) {
         this._subjectEncoding = PersistentContent.encode(subject);
         return true;
      } else {
         this._subjectEncoding = null;
         return false;
      }
   }

   @Override
   public final Object getValue() {
      return getValue(this);
   }

   @Override
   public final Field getField(Object context) {
      return !ContextObject.getFlag(context, 0)
         ? null
         : new AutoTextEditField(SearchResources.getString(29), PersistentContent.decodeString(this._subjectEncoding), 1000000, 4503601774854144L);
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      this.setValue(null);
      if (!(field instanceof EditField)) {
         return false;
      }

      EditField editField = (EditField)field;
      String subjectStr = editField.getText().trim();
      return this.setValue(subjectStr);
   }

   @Override
   public final int getType() {
      return 2;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         String value = PersistentContent.decodeString(this._subjectEncoding);
         if (value != null) {
            syncBuffer.addField(4, value);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int getOrder(Object context) {
      return 12100;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._subjectEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._subjectEncoding = PersistentContent.reEncode(this._subjectEncoding, compress, encrypt);
      return null;
   }

   private static final synchronized void clearValue(SubjectSearchModel model) {
      if (model == _valueModel) {
         _valueWR.set(null);
      }
   }

   private static final synchronized Object getValue(SubjectSearchModel model) {
      Object value = _valueWR.get();
      if (value != null && model == _valueModel) {
         return value;
      }

      if (model._subjectEncoding == null) {
         return null;
      }

      String subject = PersistentContent.decodeString(model._subjectEncoding);
      value = new StringMatch(subject, false);
      _valueWR.set(value);
      _valueModel = model;
      return value;
   }
}
