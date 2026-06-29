package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class ShowSearchModel implements PersistableRIMModel, SearchCriterion, FieldProvider, ConversionProvider {
   public int _index;
   public static final int SHOW_INVALID;
   public static final int SHOW_SENT_RECEIVED;
   public static final int SHOW_RECEIVED;
   public static final int SHOW_SENT;
   public static final int SHOW_SAVED;
   public static final int SHOW_DRAFT;
   public static final int SHOW_UNOPENED;

   public final void setIndex(int index) {
      switch (index) {
         case 9:
            this._index = 1;
            return;
         case 10:
            this._index = 2;
            return;
         case 11:
            this._index = 3;
            return;
         case 22:
            this._index = 4;
            return;
         case 28:
            this._index = 5;
            return;
         default:
            this._index = 0;
      }
   }

   public final Verb getDefaultVerb(Verb[] verbs, Object context) {
      return null;
   }

   public final Verb[] getVerbs(Object context) {
      return null;
   }

   @Override
   public final int getOrder(Object context) {
      return 12400;
   }

   @Override
   public final int getType() {
      switch (this._index) {
         case 1:
            return 9;
         case 2:
         default:
            return 10;
         case 3:
            return 11;
         case 4:
            return 22;
         case 5:
            return 28;
      }
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         int value = this._index;
         if (value != 0) {
            syncBuffer.addInt(7, value, 4);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      ObjectChoiceField ocf = (ObjectChoiceField)field;
      this._index = ocf.getSelectedIndex();
      return true;
   }

   @Override
   public final Object getValue() {
      return this._index > 0 ? this : null;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final Field getField(Object context) {
      Object[] choices = new Object[]{
         SearchResources.getString(25),
         SearchResources.getString(26),
         SearchResources.getString(27),
         SearchResources.getString(39),
         SearchResources.getString(42),
         SearchResources.getString(69)
      };
      return (Field)(new Object(SearchResources.getString(28), choices, choices[this._index]));
   }

   ShowSearchModel() {
   }
}
