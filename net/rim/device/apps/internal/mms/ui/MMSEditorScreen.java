package net.rim.device.apps.internal.mms.ui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.DraftSaveable;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.verbs.AddImageVerb;
import net.rim.device.apps.internal.mms.verbs.AddRecipientVerb;
import net.rim.device.apps.internal.mms.verbs.AttachAddressCardVerb;
import net.rim.device.apps.internal.mms.verbs.AttachAppointmentVerb;
import net.rim.device.apps.internal.mms.verbs.AttachAudioVerb;
import net.rim.device.apps.internal.mms.verbs.AttachVideoVerb;
import net.rim.device.apps.internal.mms.verbs.AttachVoiceNoteVerb;
import net.rim.device.apps.internal.mms.verbs.ComposeOptionsVerb;
import net.rim.device.apps.internal.mms.verbs.DeleteRecipientVerb;
import net.rim.device.apps.internal.mms.verbs.MMSSaveDraftVerb;
import net.rim.device.apps.internal.mms.verbs.MMSSendVCalVerb;
import net.rim.device.apps.internal.mms.verbs.MMSSendVerb;

public final class MMSEditorScreen extends MMSModelScreen implements MMSPayloadModel, Confirmation, DraftSaveable {
   private MMSSendVerb _sendVerb;
   private MMSSaveDraftVerb _saveDraftVerb;
   private ComposeOptionsVerb _optionsVerb;
   private long _creationDate;
   private RIMModel _sender;
   private Hashtable _attributes;
   private RecipientFieldManager _addressFields;
   private RecipientFieldManager _ccAddressFields;
   private RecipientFieldManager _bccAddressFields;
   private ActiveAutoTextEditField _subjectField;
   private MMSPresentationField _presentationField;
   private ContextObject _context;

   public final void prepareToSend() {
      if (MMSClientServiceBook.allowImageReductionBeforeSend()) {
         boolean needToReduce = false;
         int bytesToTrim = 0;
         int maxImageWidth = Integer.MAX_VALUE;
         int maxImageHeight = Integer.MAX_VALUE;
         if (MMSClientServiceBook.getRestrictedSizeMode() == 1) {
            needToReduce = true;
            bytesToTrim = Math.max(bytesToTrim, this._presentationField.getTotalAttachmentDataSize() - MMSClientServiceBook.getMaxMessageSize());
         }

         if (MMSClientServiceBook.getRestrictedSendMode() == 1) {
            needToReduce = true;
            maxImageWidth = MMSClientServiceBook.getMaxImageWidth();
            maxImageHeight = MMSClientServiceBook.getMaxImageHeight();
         }

         if (needToReduce) {
            this._presentationField.reduceImages(bytesToTrim, maxImageWidth, maxImageHeight);
         }
      }
   }

   public final AttachmentDataProvider getAttachmentDataProvider() {
      return this._presentationField;
   }

   public final void setAttribute(String name, String value) {
      if (this._attributes == null) {
         this._attributes = (Hashtable)(new Object());
      }

      this._attributes.put(name, value);
   }

   @Override
   public final Vector getCcRecipients() {
      return this._ccAddressFields.getRecipients();
   }

   @Override
   public final Vector getBccRecipients() {
      return this._bccAddressFields.getRecipients();
   }

   @Override
   public final Vector getRecipients() {
      return this._addressFields.getRecipients();
   }

   @Override
   public final String getAttribute(String attributeName) {
      if (attributeName.equals("subject")) {
         return this._subjectField.getText();
      } else {
         return (String)(this._attributes == null ? null : this._attributes.get(attributeName));
      }
   }

   @Override
   public final Enumeration attributeNames() {
      return this._attributes == null ? null : this._attributes.keys();
   }

   @Override
   public final MMSPresentationModel getPresentationModel() {
      return this._presentationField;
   }

   @Override
   public final RIMModel getSender() {
      return this._sender;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      return !this.isDirty() || Dialog.ask(3, CommonResources.getString(3004), 4) != -1;
   }

   @Override
   public final void draftSaveOnClose() {
      ContextObject context = (ContextObject)(new Object());
      ContextObject.setFlag(context, 121);
      this._saveDraftVerb.invoke(context);
   }

   @Override
   public final long getCreationDate() {
      return this._creationDate;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      ContextObject ctx = (ContextObject)(new Object(37));
      Verb defaultVerb = null;
      if ((instance == 0 || instance == 65536) && !this.isSelecting()) {
         Verb[] verbs = new Object[0];
         ((VerbProvider)this.getMessageModel()).getVerbs(ctx, verbs);
         menu.add(verbs);
         defaultVerb = this._saveDraftVerb;
         menu.add(this._saveDraftVerb);
         if (MMSUtilities.canSend() && this.getRecipientCount() > 0) {
            defaultVerb = this._sendVerb;
            menu.add(this._sendVerb);
         }
      }

      if (instance == 65536 && this.isSelecting()) {
         super.makeMenu(menu, instance);
      }

      if (instance == 0) {
         super.makeMenu(menu, instance);
         VerbRepository verbRepository = VerbRepository.getVerbRepository(1247995981604341294L);
         menu.add(verbRepository.getVerbs(null));
         verbRepository = VerbRepository.getVerbRepository(5529224403653746205L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         menu.add(this._optionsVerb);
         if (this.getRecipientCount() < MMSClientServiceBook.getMaxRecipientCount() || MMSClientServiceBook.getRestrictedSendMode() != 1) {
            String addToString = ((StringBuffer)(new Object())).append(EmailResources.getString(25)).append(EmailResources.getString(51)).toString();
            String addCcString = ((StringBuffer)(new Object())).append(EmailResources.getString(25)).append(EmailResources.getString(52)).toString();
            String addBccString = ((StringBuffer)(new Object())).append(EmailResources.getString(25)).append(EmailResources.getString(53)).toString();
            Verb addToVerb = new AddRecipientVerb(addToString, 16859648, this._addressFields);
            Verb addCcVerb = new AddRecipientVerb(addCcString, 16859664, this._ccAddressFields);
            Verb addBccVerb = new AddRecipientVerb(addBccString, 16859680, this._bccAddressFields);
            menu.add(addToVerb);
            menu.add(addCcVerb);
            menu.add(addBccVerb);
            Field focusField = this.getLeafFieldWithFocus();
            if (this._addressFields.getLeafFieldWithFocus() == focusField) {
               defaultVerb = addToVerb;
               menu.add(new DeleteRecipientVerb(focusField, this._addressFields));
            }

            if (this._ccAddressFields.getLeafFieldWithFocus() == focusField) {
               defaultVerb = addCcVerb;
               menu.add(new DeleteRecipientVerb(focusField, this._ccAddressFields));
            }

            if (this._bccAddressFields.getLeafFieldWithFocus() == focusField) {
               defaultVerb = addBccVerb;
               menu.add(new DeleteRecipientVerb(focusField, this._bccAddressFields));
            }
         }

         if (!this._presentationField.isImmutable() && !this._presentationField.hasTemplate()) {
            boolean oneVideoPerMMS = MMSClientServiceBook.oneVideoPerMMS();
            boolean hasAttachments = false;
            boolean hasVideoAttachment = false;
            if (MMSClientServiceBook.oneVideoPerMMS()) {
               Enumeration names = this._presentationField.attachmentNames();
               if (names != null) {
                  while (names.hasMoreElements()) {
                     String name = (String)names.nextElement();
                     int type = this._presentationField.getAttachmentType(name);
                     if (!MMSUtilities.isAudioType(type)
                        && !MMSUtilities.isImageType(type)
                        && !MMSUtilities.isAppointmentType(type)
                        && !MMSUtilities.isCalendarType(type)) {
                        if (MMSUtilities.isVideoType(type)) {
                           hasAttachments = true;
                           hasVideoAttachment = true;
                           break;
                        }
                     } else {
                        hasAttachments = true;
                     }
                  }
               }
            }

            if (!oneVideoPerMMS || !hasAttachments) {
               AttachVideoVerb attachVideoVerb = new AttachVideoVerb(this.getPresentationModel());
               menu.add(attachVideoVerb);
            }

            if (!oneVideoPerMMS || !hasVideoAttachment) {
               AddImageVerb addImageVerb = new AddImageVerb(this.getPresentationModel());
               menu.add(addImageVerb);
               AttachAudioVerb attachAudioVerb = new AttachAudioVerb(this.getPresentationModel());
               menu.add(attachAudioVerb);
               AttachVoiceNoteVerb attachVoiceNoteVerb = new AttachVoiceNoteVerb(this.getPresentationModel());
               menu.add(attachVoiceNoteVerb);
               AttachAppointmentVerb attachAppointmentVerb = new AttachAppointmentVerb(this.getPresentationModel());
               menu.add(attachAppointmentVerb);
               AttachAddressCardVerb attachAddressCardVerb = new AttachAddressCardVerb(this.getPresentationModel());
               if (attachAddressCardVerb.canInvoke(null)) {
                  menu.add(attachAddressCardVerb);
               }
            }
         }
      }

      menu.setDefault(defaultVerb);
      menu.coalesce(-3072555018635390988L, null);
   }

   @Override
   protected final void populateScreen() {
      MMSMessageModel message = (MMSMessageModel)this.getMessageModel();
      MMSPayloadModel payload = message.getPayload();
      this._creationDate = payload.getCreationDate();
      this._sender = payload.getSender();
      Enumeration names = payload.attributeNames();
      if (names != null) {
         this._attributes = (Hashtable)(new Object());

         while (names.hasMoreElements()) {
            String attributeName = (String)names.nextElement();
            this._attributes.put(attributeName, payload.getAttribute(attributeName));
         }
      }

      ContextObject context = ContextObject.clone(this._context);
      context.setFlag(74);
      context.setFlag(9);
      context.clearFlag(0);
      this._addressFields = new RecipientFieldManager(payload.getRecipients(), MMSResources.getString(16), true, context);
      this.add(this._addressFields);
      this._ccAddressFields = new RecipientFieldManager(payload.getCcRecipients(), MMSResources.getString(17), true, context);
      this.add(this._ccAddressFields);
      this._bccAddressFields = new RecipientFieldManager(payload.getBccRecipients(), MMSResources.getString(65), true, context);
      this.add(this._bccAddressFields);
      context.setFlag(0);
      String subjectLabel = MMSResources.getString(18);
      String subjectText = payload.getAttribute("subject");
      long subjectFlags = 4503601774854144L;
      this._subjectField = (ActiveAutoTextEditField)(new Object(subjectLabel, subjectText, 40, subjectFlags));
      this.add(this._subjectField);
      this.add((Field)(new Object()));
      context.put(-7651695713744129224L, message);
      MMSPresentationModel presentation = payload.getPresentationModel();
      if (presentation != null) {
         this._presentationField = (MMSPresentationField)((FieldProvider)presentation).getField(context);
         if (this._presentationField != null) {
            this.add(this._presentationField);
            if (ContextObject.getFlag(this._context, 6)) {
               this._presentationField.setDirty(true);
            }
         }
      } else {
         this.add((Field)(new Object(MMSResources.getString(26), 36028797018963968L)));
      }

      this._subjectField.setFocus();
   }

   @Override
   public final void save() {
      ContextObject context = this._context.clone();
      int[] flags = new int[]{13, 37, 51, 1866989824, 727916, 1829528321, -1989393782, -1566170261};
      context.setFlags(flags);
      this._saveDraftVerb.invoke(context);
   }

   @Override
   protected final Field getInitialTopField() {
      if (this._addressFields.getFieldCount() > 0) {
         return this._addressFields;
      } else {
         return this._ccAddressFields.getFieldCount() > 0 ? this._ccAddressFields : super.getInitialTopField();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L && PersistentContent.isEncryptionEnabled()) {
         this.closeEditor();
      } else {
         super.eventOccurred(guid, data0, data1, object0, object1);
      }
   }

   private final synchronized void closeEditor() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof Object) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         this.save();
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   private final int getRecipientCount() {
      return this._addressFields.getFieldCount() + this._ccAddressFields.getFieldCount() + this._bccAddressFields.getFieldCount();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (!attached) {
         MMSSendVCalVerb sendVCalVerb = MMSUtilities.getSendAsVCalVerb();
         if (sendVCalVerb != null) {
            sendVCalVerb.setPresentationModel(null);
         }
      }

      super.onUiEngineAttached(attached);
   }

   public MMSEditorScreen(Object context) {
      super(context);
      if (context instanceof Object) {
         this._context = (ContextObject)context;
      }

      this._sendVerb = new MMSSendVerb(this);
      this._saveDraftVerb = new MMSSaveDraftVerb(this);
      this._optionsVerb = new ComposeOptionsVerb(this);
   }
}
