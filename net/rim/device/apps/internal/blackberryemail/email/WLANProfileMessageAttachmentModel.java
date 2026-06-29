package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DeleteConfirmationProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.ContentPartIDGenerator;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;

public class WLANProfileMessageAttachmentModel
   extends UnknownMimePartModel
   implements PersistableRIMModel,
   Persistable,
   ConversionProvider,
   CloneProvider,
   DeleteConfirmationProvider,
   VerbProvider,
   FieldProvider {
   private static final String PROFILE_EXT = ".prf";

   public WLANProfileMessageAttachmentModel() {
      super(null);
   }

   public WLANProfileMessageAttachmentModel(Object initialData) {
      super(initialData);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      ViewWLANProfileMessageAttachmentVerb verb = null;
      if (verbs != null) {
         verb = new ViewWLANProfileMessageAttachmentVerb(this);
         Arrays.add(verbs, verb);
      }

      return verb;
   }

   @Override
   public boolean confirmDelete(Object context) {
      return true;
   }

   @Override
   public Object clone(Object context) {
      WLANProfileMessageAttachmentModel clone = new WLANProfileMessageAttachmentModel(context);
      clone.setData(this.getData());
      return clone;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return false;
      }

      RIMMessagingOutgoingMessage outgoingMessage = (RIMMessagingOutgoingMessage)target;
      String name = this.getFilename();
      if (name == null) {
         name = NativeAttachmentRequestProcessor$Helper.getUnknownFileName(null);
      }

      Parameters p = CMIMEUtilities.createContentDispositionParameters(outgoingMessage, name);
      DataBuffer db = p.getDataBuffer();
      Parameters var12 = null;
      ContextObject contextObject = (ContextObject)context;
      ContentPartIDGenerator contentPartIDGenerator = (ContentPartIDGenerator)contextObject.get(-1943436819741481055L);
      int contentPartID = contentPartIDGenerator.generateContentPartID();
      CMIMEParameters parameters = (CMIMEParameters)(new Object(db, 2, 2));
      parameters.addCMIMEInteger((byte)-15, contentPartID);
      byte[] data = this.getData();
      parameters.addCMIMEInteger((byte)-13, this.getData().length);
      outgoingMessage.addAttachment(data, parameters, WLANProfileMessageAttachmentConverter.getMimeType());
      return true;
   }

   @Override
   public Field getField(Object context) {
      AutoTextEditField field = (AutoTextEditField)(new Object(null, this.getFilename()));
      field.setEditable(false);
      return field;
   }

   @Override
   public int getOrder(Object context) {
      return 6500;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return false;
   }

   public static boolean isProfileFileName(String fileName) {
      return fileName == null ? false : fileName.endsWith(".prf");
   }

   @Override
   public void setFilename(String filename) {
      super.setFilename(getProfileFriendlyFileName(filename));
   }

   private static String getProfileFriendlyFileName(String fileName) {
      if (fileName == null) {
         return null;
      } else {
         return isProfileFileName(fileName) ? fileName : ((StringBuffer)(new Object())).append(fileName).append(".prf").toString();
      }
   }
}
