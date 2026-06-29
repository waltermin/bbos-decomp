package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.search.SearchCriterion;

public class EncodingActionSearchModel implements PersistableRIMModel, SearchCriterion, FieldProvider, ConversionProvider {
   private Integer _allowedEncodingActions = (Integer)(new Object(3));
   private boolean _ticketPromtsAllowed = true;
   static final int ENCRYPTED_MESSAGES_INCLUDED = 3;
   static final int ENCRYPTED_MESSAGES_EXCLUDED = 1;

   boolean ticketPromptsAllowed() {
      return this._ticketPromtsAllowed;
   }

   void setTicketPromptsAllowed(boolean ticketPromptsAllowed) {
      this._ticketPromtsAllowed = ticketPromptsAllowed;
   }

   void setAllowedEncodingActions(int allowedEncodingActions) {
      this._allowedEncodingActions = (Integer)(new Object(allowedEncodingActions));
      this._ticketPromtsAllowed = true;
   }

   @Override
   public int getType() {
      return 27;
   }

   @Override
   public Object getValue() {
      return this._allowedEncodingActions;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addInt(16, this._allowedEncodingActions, 4);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      int selectedIndex = ((ChoiceField)field).getSelectedIndex();
      switch (selectedIndex) {
         case 0:
            this.setAllowedEncodingActions(3);
            return true;
         default:
            this.setAllowedEncodingActions(1);
            return true;
      }
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      return 12225;
   }

   @Override
   public Field getField(Object context) {
      if (!ContextObject.getFlag(context, 0)) {
         return null;
      }

      int selectedIndex;
      switch (this._allowedEncodingActions) {
         case 3:
            selectedIndex = 0;
            break;
         default:
            selectedIndex = 1;
      }

      return (Field)(new Object(SecureEmailResources.getString(136), SecureEmailResources.getStringArray(158), selectedIndex));
   }

   EncodingActionSearchModel() {
   }
}
