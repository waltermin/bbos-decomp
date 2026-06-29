package net.rim.device.apps.internal.blackberryemail.email;

import java.lang.ref.WeakReference;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class NativeAttachmentVerb extends Verb implements EmailEditorScreen$AttachSmallAttachmentVerb {
   protected WeakReference _serviceRecordReference;
   protected EmailEditorScreen _emailEditorScreen;
   protected boolean _compressImage;
   private static final long GUID = -1237457833540244999L;
   private static final int LOG_SERVICE_ROCORD_IS_NULL = 1;
   private static final int LOG_EMAIL_EDITOR_SCREEN_IS_NULL = 2;
   private static final int LOG_NATIVE_ATTACHMENT_MODEL_CREATED = 3;
   private static final int LOG_NATIVE_ATTACHMENT_MODEL_IS_NULL = 4;
   private static final int LOG_IO_EXCEPTION = 5;

   public NativeAttachmentVerb(EmailEditorScreen emailEditorScreen, ServiceRecord serviceRecord) {
      this(emailEditorScreen, serviceRecord, false);
   }

   public NativeAttachmentVerb(EmailEditorScreen emailEditorScreen, ServiceRecord serviceRecord, boolean compressImage) {
      super(16864032, EmailResources.getResourceBundle(), 102);
      if (emailEditorScreen == null) {
         throw new IllegalArgumentException();
      }

      this._emailEditorScreen = emailEditorScreen;
      if (serviceRecord != null) {
         this._serviceRecordReference = new WeakReference(serviceRecord);
      }

      this._compressImage = compressImage;
   }

   @Override
   public Object invoke(Object context) {
      try {
         if (this._emailEditorScreen == null) {
            EventLogger.logEvent(-1237457833540244999L, 2, 5);
            return null;
         } else {
            ServiceRecord serviceRecord = this._serviceRecordReference != null ? (ServiceRecord)this._serviceRecordReference.get() : null;
            if (serviceRecord == null && !this._compressImage) {
               EventLogger.logEvent(-1237457833540244999L, 1, 5);
               return null;
            } else if (this._compressImage) {
               Dialog.alert(MessageResources.getString(236));
               return this.createCompressedAttachment(context);
            } else {
               return this.createLargeAttachemt(context, serviceRecord);
            }
         }
      } finally {
         EventLogger.logEvent(-1237457833540244999L, 5, 2);
         return null;
      }
   }

   private Object createCompressedAttachment(Object context) {
      ContextObject ctx = new ContextObject(39);
      FileSelector fileSelector = new FileSelector(null, ctx, 1, null);
      fileSelector.onlySelectForwardUnlocked();
      fileSelector.setMediaType(1);
      String selectedFilePath = fileSelector.selectFile(null);
      return selectedFilePath == null ? null : CompressedFileAttachmentModel$Helper.createModelForImage(selectedFilePath);
   }

   private Object createLargeAttachemt(Object context, ServiceRecord serviceRecord) {
      String path = NativeAttachmentRequestProcessor$Helper.getFilePath();
      long[] totalExistingLargeAttachmentSizes = NativeAttachmentRequestProcessor$Helper.getLargeAttachmentSizes(
         this._emailEditorScreen.getLargeAttachmentAttachedModels()
      );
      LargeAttachmentModel attachmentModel = NativeAttachmentRequestProcessor$Helper.createAttachment(serviceRecord, totalExistingLargeAttachmentSizes, path);
      if (attachmentModel == null) {
         EventLogger.logEvent(-1237457833540244999L, 4, 5);
      } else {
         EventLogger.logEvent(-1237457833540244999L, 3, 5);
      }

      return attachmentModel;
   }
}
