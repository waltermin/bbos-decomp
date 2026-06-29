package net.rim.device.apps.internal.commonmodels.title;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.vm.Array;

public final class TitleModelImpl
   implements PersistableRIMModel,
   TitleModel,
   FieldProvider,
   PaintProvider,
   ConversionProvider,
   KeyProvider,
   EncryptableProvider,
   CloneProvider,
   MatchProvider,
   SyncFieldIDProvider {
   private Object _titleEncoding;
   static final int MEMO_TITLE_SIZE;
   static final int TASK_TITLE_FIELD_TAG;
   static final int MEMO_SUBJECT_FIELD_TAG;
   public static final int SEARCH_TITLE_FIELD_TAG;

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      String title = this.getTitle();
      y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), title, y);
      return g.drawText(title, x, y, 64, width);
   }

   @Override
   public final String getTitle() {
      try {
         return PersistentContent.decodeString(this._titleEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      if (crit.getType() != 21) {
         return -1;
      }

      String title = this.getTitle();
      return title != null && ((StringMatch)crit.getValue()).indexOf(title) >= 0 ? 1 : 0;
   }

   @Override
   public final Field getField(Object context) {
      String label = "";
      boolean editable = false;
      boolean activeField = false;
      long spellcheckable = 1099511627776L;
      int length = 1000000;
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         int rc = -1;
         if (contextObject.getFlag(28)) {
            rc = 2005;
         } else if (contextObject.getFlag(8)) {
            rc = 2006;
            activeField = true;
            length = 255;
         } else if (contextObject.getFlag(56)) {
            rc = 2006;
         } else if (contextObject.getFlag(11)) {
            rc = 9138;
            spellcheckable = 2199023255552L;
            activeField = true;
            length = 2048;
         } else if (contextObject.getFlag(33)) {
            rc = 9092;
         }

         if (rc != -1) {
            label = CommonResources.getString(rc);
         }

         editable = contextObject.getFlag(0);
      }

      String s = this.getTitle();
      if (s == null) {
         s = "";
      }

      Field field;
      if (editable) {
         s = s.substring(0, Math.min(s.length(), length));
         field = (Field)(new Object(label, s, length, 4503601774854144L | spellcheckable));
      } else if (activeField) {
         field = (Field)(new Object(s, 18014398509481984L));
      } else {
         field = (Field)(new Object(s, 18014398509482048L));
      }

      field.setEditable(editable);
      field.setCookie(this);
      return field;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (field instanceof Object && field.isEditable()) {
         AutoTextEditField editField = (AutoTextEditField)field;
         this.setTitle(editField.getText());
         String title = this.getTitle();
         return title != null && title.length() > 0;
      } else {
         return false;
      }
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(19)) {
            SyncBuffer syncBuffer = (SyncBuffer)target;
            int field = this.getSyncFieldId(contextObject);
            if (field != -1) {
               syncBuffer.addField(field, this.getTitle());
               return true;
            }
         } else if (contextObject.getFlag(11) && contextObject.getFlag(43) && contextObject.getFlag(54) && target instanceof Object) {
            StringBuffer stringBuffer = (StringBuffer)target;
            String title = this.getTitle();
            if (title != null) {
               stringBuffer.append("\rTitle:");
               stringBuffer.append(title);
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public final Object clone(Object context) {
      return new TitleModelImpl(this.getTitle());
   }

   @Override
   public final boolean validate(Field field, Object context) {
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(56) || contextObject.getFlag(28) || contextObject.getFlag(8) || contextObject.getFlag(33)) {
            AutoTextEditField editField = (AutoTextEditField)field;
            String title = editField.getText();
            if (title != null) {
               title = title.trim();
            }

            if (title != null && title.length() > 0) {
               return true;
            }

            return false;
         }

         if (contextObject.getFlag(11)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final int getSyncFieldId(Object context) {
      return TitleModelFactory.getSyncFieldId(context);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(28) || contextObject.getFlag(35) || contextObject.getFlag(56)) {
            if (index + 1 > keyArray.length) {
               Array.resize(keyArray, index + 1);
            }

            String title = this.getTitle();
            if (title != null) {
               keyArray[index++] = title;
               return 1;
            }
         }
      }

      return 0;
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
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._titleEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._titleEncoding = PersistentContent.reEncode(this._titleEncoding, compress, encrypt);
      return null;
   }

   @Override
   public final void setTitle(String title) {
      if (title == null) {
         title = "";
      }

      this._titleEncoding = PersistentContent.encode(title, false, true);
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 28)) {
         return 13000;
      } else if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 2201 : 1101;
      } else {
         return 0;
      }
   }

   @Override
   public final String toString() {
      return this.getTitle();
   }

   TitleModelImpl(Object title) {
      this.setTitle(title == null ? null : title.toString());
   }
}
