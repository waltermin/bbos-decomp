package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class ServiceSearchModel implements PersistableRIMModel, SearchCriterion, FieldProvider, ConversionProvider {
   int[] _values = new int[]{-1, -1, 426115328, 1778450541, 1948795843, -1016463104, -1670884541, -1016463104};
   private static final int ALL_SERVICES_UID_HASH = -1;

   public final void setService(int userId, int uidHash) {
      this._values[0] = userId;
      this._values[1] = uidHash;
   }

   @Override
   public final Object getValue() {
      return this._values[1] == -1 ? null : this._values;
   }

   @Override
   public final int getType() {
      return 17;
   }

   @Override
   public final Field getField(Object context) {
      Object[] serviceChoices = new Object[0];
      Arrays.add(serviceChoices, new ServiceSearchModel$ServiceChoice(SearchResources.getString(67), -1, -1));
      int selectedServiceIndex = 0;
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");
      int numServiceRecords = serviceRecords != null ? serviceRecords.length : 0;

      for (int i = 0; i < numServiceRecords; i++) {
         int userId = serviceRecords[i].getUserId();
         int uidHash = serviceRecords[i].getUidHash();
         Arrays.add(serviceChoices, new ServiceSearchModel$ServiceChoice(serviceRecords[i].getName(), userId, uidHash));
         if (userId != -1 && userId == this._values[0] || uidHash == this._values[1]) {
            selectedServiceIndex = i + 1;
         }
      }

      return (Field)(new Object(SearchResources.getString(68), serviceChoices, selectedServiceIndex));
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      ObjectChoiceField choiceField = (ObjectChoiceField)field;
      ServiceSearchModel$ServiceChoice serviceChoice = (ServiceSearchModel$ServiceChoice)choiceField.getChoice(choiceField.getSelectedIndex());
      this.setService(serviceChoice.getUserId(), serviceChoice.getUidHash());
      return true;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         int uidHash = this._values[1];
         if (uidHash != -1) {
            SyncBuffer sb = (SyncBuffer)target;
            sb.addInt(14, uidHash, 4);
            sb.addInt(15, this._values[0], 4);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 12250;
   }
}
