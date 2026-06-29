package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.criteria.TextSearchModel;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class BodySearchModel extends TextSearchModel implements PersistableRIMModel, FieldProvider, ConversionProvider {
   @Override
   public final int getType() {
      return 1;
   }

   @Override
   public final Field getField(Object context) {
      if (!ContextObject.getFlag(context, 0)) {
         return null;
      }

      AutoTextEditField textField = (AutoTextEditField)(new Object(SearchResources.getString(17), this.getText(), 1000000, 4503601774854144L));
      RIMModelFactory[] subCriterionFactories = BodySearchModelFactory.getInstance().getSubCriterionFactories();
      if (subCriterionFactories == null) {
         return textField;
      }

      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)(new Object());
      textField.setCookie(this);
      vifm.add(textField);
      int numSubCriterionFactories = subCriterionFactories.length;

      label58:
      for (int i = 0; i < numSubCriterionFactories; i++) {
         PersistableRIMModel[] subCriteria = this.getSubCriteria();
         int numSubCriteria = subCriteria == null ? 0 : subCriteria.length;

         for (int j = 0; j < numSubCriteria; j++) {
            if (subCriterionFactories[i].recognize(subCriteria[j])) {
               continue label58;
            }
         }

         Object subCriterion = subCriterionFactories[i].createInstance(context);
         if (subCriterion != null) {
            this.addSubCriterion((PersistableRIMModel)subCriterion);
         }
      }

      PersistableRIMModel[] subCriteria = this.getSubCriteria();
      int numSubCriteria = subCriteria == null ? 0 : subCriteria.length;

      for (int j = 0; j < numSubCriteria; j++) {
         PersistableRIMModel currentModel = subCriteria[j];
         if (currentModel instanceof Object) {
            Field field = ((FieldProvider)currentModel).getField(context);
            if (field != null) {
               field.setCookie(currentModel);
               vifm.add(field, 16);
            }
         }
      }

      return vifm;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      this.setValue(null);
      if (field instanceof Object) {
         return this.setValue(((BasicEditField)field).getText());
      }

      if (!(field instanceof Object)) {
         return false;
      }

      Manager manager = (Manager)field;
      boolean atLeastOneFieldGrabbedData = false;
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field currentField = manager.getField(i);
         Object currentCookie = currentField.getCookie();
         SearchCriterion criterionToGrabData = this.locateCriterionByType(currentCookie);
         if (criterionToGrabData instanceof Object) {
            FieldProvider fieldProvider = (FieldProvider)criterionToGrabData;
            atLeastOneFieldGrabbedData |= fieldProvider.grabDataFromField(currentField, context);
         }
      }

      return atLeastOneFieldGrabbedData;
   }

   private final SearchCriterion locateCriterionByType(Object criterionToMatch) {
      if (criterionToMatch instanceof Object) {
         int typeToMatch = ((SearchCriterion)criterionToMatch).getType();
         if (this.getType() == typeToMatch) {
            return this;
         }

         PersistableRIMModel[] subCriteria = this.getSubCriteria();
         int numSubCriteria = subCriteria == null ? 0 : subCriteria.length;

         for (int i = 0; i < numSubCriteria; i++) {
            SearchCriterion currentCriterion = (SearchCriterion)subCriteria[i];
            if (currentCriterion.getType() == typeToMatch) {
               return currentCriterion;
            }
         }
      }

      return null;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 12200;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         String text = this.getText();
         if (text != null) {
            syncBuffer.addField(3, text);
         }

         PersistableRIMModel[] subCriteria = this.getSubCriteria();
         int numSubCriteria = subCriteria == null ? 0 : subCriteria.length;

         for (int i = 0; i < numSubCriteria; i++) {
            PersistableRIMModel currentModel = subCriteria[i];
            if (currentModel instanceof Object) {
               ((ConversionProvider)currentModel).convert(context, syncBuffer);
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
