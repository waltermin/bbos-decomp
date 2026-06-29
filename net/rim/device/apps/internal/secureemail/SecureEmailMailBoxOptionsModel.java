package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;

public class SecureEmailMailBoxOptionsModel implements RIMModel, FieldProvider, VerbProvider {
   TransitoryMessagePropertiesModel _messagePropertiesModel;
   RIMModel _selectedEncodingOptionsModel;

   @Override
   public Field getField(Object context) {
      if (!(this._selectedEncodingOptionsModel instanceof Object)) {
         return null;
      }

      FieldProvider fieldProvider = (FieldProvider)this._selectedEncodingOptionsModel;
      return fieldProvider.getField(context);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (!(this._selectedEncodingOptionsModel instanceof Object)) {
         return null;
      }

      VerbProvider verbProvider = (VerbProvider)this._selectedEncodingOptionsModel;
      return verbProvider.getVerbs(context, verbs);
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (!(this._selectedEncodingOptionsModel instanceof Object)) {
         return false;
      }

      FieldProvider fieldProvider = (FieldProvider)this._selectedEncodingOptionsModel;
      return fieldProvider.grabDataFromField(field, context);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean validate(Field field, Object context) {
      if (!(this._selectedEncodingOptionsModel instanceof Object)) {
         return false;
      }

      FieldProvider fieldProvider = (FieldProvider)this._selectedEncodingOptionsModel;
      SendMethod selectedSendMethod = this._messagePropertiesModel.getSelectedSendMethod();
      int encodingAction = selectedSendMethod.getEncodingAction();
      boolean var9 = false /* VF: Semaphore variable */;

      boolean var6;
      try {
         var9 = true;
         ContextObject.put(context, -6134954812221890959L, new Object(encodingAction));
         var6 = fieldProvider.validate(field, context);
         var9 = false;
      } finally {
         if (var9) {
            ContextObject.remove(context, -6134954812221890959L);
         }
      }

      ContextObject.remove(context, -6134954812221890959L);
      return var6;
   }

   @Override
   public int getOrder(Object context) {
      return 6600;
   }

   SecureEmailMailBoxOptionsModel(RIMModel[] encodingOptionsModels, Object context) {
      if (encodingOptionsModels == null) {
         throw new Object();
      }

      this._messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
      SendMethod selectedSendMethod = this._messagePropertiesModel.getSelectedSendMethod();
      if (selectedSendMethod != null) {
         long selectedEncodingUID = selectedSendMethod.getEncodingUID();

         for (RIMModel currentModel : encodingOptionsModels) {
            if (currentModel instanceof EncodingSupporter && ((EncodingSupporter)currentModel).isEncodingSupported(selectedEncodingUID)) {
               this._selectedEncodingOptionsModel = currentModel;
               return;
            }
         }
      }
   }
}
