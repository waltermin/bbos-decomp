package net.rim.device.apps.api.search.criteria;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.vm.WeakReference;

public class TextSearchModel implements PersistableRIMModel, SearchCriterion, EncryptableProvider {
   private Object _textEncoding;
   private PersistableRIMModel[] _subCriteria;
   private boolean _matchAllPatterns = true;
   private static WeakReference _valueWR = (WeakReference)(new Object(null));
   private static TextSearchModel _valueModel;

   public void setMatchAllPatterns(boolean matchAllPatterns) {
      this._matchAllPatterns = matchAllPatterns;
   }

   public boolean getMatchAllPatterns() {
      return this._matchAllPatterns;
   }

   protected String getText() {
      return PersistentContent.decodeString(this._textEncoding);
   }

   public boolean setValue(String text) {
      clearValue(this);
      if (text != null && text.length() != 0) {
         this._textEncoding = PersistentContent.encode(text);
         return true;
      } else {
         this._textEncoding = null;
         return false;
      }
   }

   public PersistableRIMModel[] getSubCriteria() {
      return this._subCriteria;
   }

   public synchronized void addSubCriterion(PersistableRIMModel searchCriterion) {
      if (this._subCriteria == null) {
         this._subCriteria = new Object[]{searchCriterion};
      } else {
         Arrays.add(this._subCriteria, searchCriterion);
      }
   }

   @Override
   public Object getValue() {
      return getValue(this);
   }

   @Override
   public int getType() {
      return 21;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._textEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._textEncoding = PersistentContent.reEncode(this._textEncoding);
      return null;
   }

   private static synchronized Object getValue(TextSearchModel model) {
      Object value = _valueWR.get();
      if (value != null && model == _valueModel) {
         return value;
      }

      if (model._textEncoding == null) {
         return null;
      }

      String text = model.getText();
      value = new Object(text, false, true, model._matchAllPatterns);
      _valueWR.set(value);
      _valueModel = model;
      return value;
   }

   private static synchronized void clearValue(TextSearchModel model) {
      if (model == _valueModel) {
         _valueWR.set(null);
      }
   }
}
