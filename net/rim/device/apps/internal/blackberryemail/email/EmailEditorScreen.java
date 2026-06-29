package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.AddressVerifier;
import net.rim.device.apps.api.framework.model.AddressVerifierAwareField;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.OpenViewer;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.util.DraftSaveable;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory$AddWrapperVerb;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.addressbook.lookup.RequestModel;
import net.rim.device.apps.internal.addressbook.lookup.Result;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderAddVerb;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModelFactory;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.internal.system.ITPolicyInternal;

public final class EmailEditorScreen
   extends EditorUsingRIMModelFactory
   implements OpenViewer,
   CollectionListener,
   Runnable,
   HolsterListener,
   DraftSaveable,
   AddressVerifier {
   private EmailSaveDraftVerb _saveDraftVerb;
   private EmailDeleteOriginalTextVerb _deleteOriginalTextVerb;
   private UiApplication _app = UiApplication.getUiApplication();
   private boolean _updateLookupScheduled;
   private EmailPayloadModel _originalPayload;
   private boolean _prependedDisclaimerModelExists;
   private Manager _headerManager;
   private String _currentSecondLevelDomain;
   private boolean _trustedAddressCheckingEnabled = false;
   private EmailEditorScreen$EmailEditorScreenTransitoryEmailMessageModel _transitoryEmailMessageModel = new EmailEditorScreen$EmailEditorScreenTransitoryEmailMessageModel(
      this
   );
   private static ValidRecognizer _validRecognizer = new ValidRecognizer();
   private static final Recognizer PREPEND_DISCLAIMER_MODEL_RECOGNIZER = PrependedDisclaimerModel.getRecognizer();

   public final boolean hasAddressCardAttachments() {
      Object[] smallAttachments = this.getAttachedModels(new AddressCardAttachmentRecognizer());
      return smallAttachments != null && smallAttachments.length > 0;
   }

   public final AddressCardModel[] getAddresCardAttachedModels() {
      Object[] attachedModels = this.getAttachedModels(new AddressCardAttachmentRecognizer());
      AddressCardModel[] models = new Object[attachedModels == null ? 0 : attachedModels.length];
      if (models.length > 0) {
         System.arraycopy(attachedModels, 0, models, 0, models.length);
      }

      return models;
   }

   public final boolean hasSmallAttachments() {
      Object[] smallAttachments = this.getAttachedModels(new SmallAttachmentRecognizer());
      return smallAttachments != null && smallAttachments.length > 0;
   }

   public final CompressedFileAttachmentModel[] getCompressedFileAttachedModels() {
      Object[] attachedModels = this.getAttachedModels(new SmallAttachmentRecognizer());
      CompressedFileAttachmentModel[] models = new CompressedFileAttachmentModel[attachedModels == null ? 0 : attachedModels.length];
      if (models.length > 0) {
         System.arraycopy(attachedModels, 0, models, 0, models.length);
      }

      return models;
   }

   public final void updateLookupFields() {
      Field[] fields = new Object[0];
      this.collectLookupFields(this.getMainManager(), fields);
      boolean aFieldHasResolved = false;
      int fieldCount = fields.length;

      for (int i = 0; i < fieldCount; i++) {
         Field field = fields[i];
         EmailHeaderModel emailHeaderModel = (EmailHeaderModel)fields[i].getCookie();
         RIMModel address = emailHeaderModel.getInsideModel();
         if (address instanceof Object) {
            ResolvedStatusProvider resolvedStatus = (ResolvedStatusProvider)address;
            EmailHeaderModel newModel = emailHeaderModel;
            boolean hadFocus = this.getModelFieldWithFocus() == field;
            boolean isResolved = resolvedStatus.isResolved();
            if (isResolved) {
               Object newResolvedModel = resolvedStatus.getResolvedSubItem();
               ContextObject contextObject = (ContextObject)(new Object());
               contextObject.put(254, newResolvedModel);
               newModel = EmailHeaderModelFactory.createInstance(emailHeaderModel.getHeaderType(), contextObject);
               aFieldHasResolved = true;
            }

            if (newModel instanceof Object) {
               FieldProvider newModelFieldProvider = newModel;
               ContextObject context = (ContextObject)(new Object());
               context.put(9120441889802231811L, this);
               context.setFlag(0);
               if (isResolved) {
                  context.setFlag(114);
               }

               Field newField = newModelFieldProvider.getField(context);
               int order = emailHeaderModel.getOrder(context);
               int newOrder = newModelFieldProvider.getOrder(context);
               if (order == newOrder) {
                  int oldScroll = 0;
                  VerticalFieldManager vfm = this.getVerticalFieldManager();
                  if (vfm != null) {
                     oldScroll = vfm.getVerticalScroll();
                  }

                  this.replaceFieldsWithSameOrder(field, newField);
                  if (vfm != null) {
                     vfm.setVerticalScroll(oldScroll);
                  }
               } else {
                  this.replaceField(field, newField, newOrder);
               }

               newField.setDirty(true);
               if (hadFocus && !isResolved) {
                  newField.setFocus();
               }

               this.replaceModel(emailHeaderModel, newModel, true);
               if (field.getInputContext().getActiveInputMethodID() == 512 && hadFocus && !isResolved && address instanceof Object) {
                  Request r = ((RequestModel)address).fetchRequest();
                  if (r != null) {
                     Result result = r.getResult();
                     if (result != null && result.getIncludedMatches() == 0 && r.getSearchString().indexOf(32) < 0) {
                        Dialog.alert(MessageResources.getString(237));
                     }
                  }
               }

               if (hadFocus && !isResolved && !aFieldHasResolved && address instanceof Object && !this.searchViewScreenIsOpen()) {
                  this.autoOpen((RequestModel)address);
               }

               if (isResolved) {
                  this.handleRecipientAdded(newModel);
               }
            }
         }
      }

      Screen activeScreen = this.getActiveMainScreen();
      if (activeScreen instanceof Object) {
         activeScreen.setFocus();
      }
   }

   public final boolean hasLargeAttachments() {
      return this.getLargeAttachmentAttachedModels().length > 0;
   }

   public final void handleRecipientAdded(EmailHeaderModel newRecipient) {
      TransitoryMessagePropertiesModel messagePropertiesModel = this._transitoryEmailMessageModel.getMessagePropertiesModel();
      if (messagePropertiesModel != null) {
         messagePropertiesModel.recipientAdded(newRecipient);
      }
   }

   public final void handleRecipientRemoved(EmailHeaderModel oldRecipient) {
      TransitoryMessagePropertiesModel messagePropertiesModel = this._transitoryEmailMessageModel.getMessagePropertiesModel();
      if (messagePropertiesModel != null) {
         messagePropertiesModel.recipientRemoved(oldRecipient);
      }
   }

   public final boolean addBlankHeader(int headerType, boolean onlyIfNonExistent) {
      if (onlyIfNonExistent && this.findBlankHeader(headerType) != null) {
         return false;
      }

      ContextObject context = (ContextObject)(new Object());
      context.put(254, EmailHeaderModel.createBlankFreeFormAddress(super._context));
      context.putIntegerData(headerType);
      EmailHeaderModel header = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, context);
      this.insertModel(header);
      return true;
   }

   public final Field findBlankHeader(int headerType) {
      return this.findField(new BlankRecognizer(headerType));
   }

   public final Field findBlankHeader() {
      return this.findBlankHeader(-1);
   }

   public final Field findNonBlankHeader(int headerType) {
      return this.findField(new NonBlankRecognizer(headerType));
   }

   public final Field findNonBlankHeader() {
      return this.findNonBlankHeader(-1);
   }

   public final Field findValidHeader() {
      return this.findField(_validRecognizer);
   }

   public final Field findUnresolvedLookup() {
      return this.findField(new UnresolvedRecognizer());
   }

   public final void setModelAndSaveCopy(Object model) {
      this.setModel(model);
      this.saveOriginalPayload();
   }

   public final boolean validateCompressedFileAttachmentModels(ServiceRecord sr) {
      if (!CMIMEUtilities.isLargeAttachmentUploadAllowed(sr)) {
         CompressedFileAttachmentModel[] compressedFileAttachmentModels = this.getCompressedFileAttachedModels();
         if (compressedFileAttachmentModels.length == 1 && !this.hasAddressCardAttachments()) {
            return true;
         } else if (compressedFileAttachmentModels.length == 0) {
            return true;
         } else if (this.hasLargeAttachments()) {
            Dialog.alert(MessageResources.getString(235));
            return false;
         } else {
            Dialog.alert(MessageResources.getString(234));
            return false;
         }
      } else {
         return true;
      }
   }

   public final LargeAttachmentModel[] getLargeAttachmentAttachedModels() {
      Object[] attachedModels = this.getAttachedModels(new LargeAttachmentRecognizer());
      LargeAttachmentModel[] largeAttachmentModels = new LargeAttachmentModel[attachedModels == null ? 0 : attachedModels.length];
      if (largeAttachmentModels.length > 0) {
         System.arraycopy(attachedModels, 0, largeAttachmentModels, 0, largeAttachmentModels.length);
      }

      return largeAttachmentModels;
   }

   protected final void setFocusToEditableBody(boolean top) {
      Field editableBodyField = this.findEditableBodyField(this.getMainManager());
      if (editableBodyField instanceof Object) {
         TextField textField = (TextField)editableBodyField;
         textField.setCursorPosition(top ? 0 : textField.getTextLength());
         textField.setFocus();
      }
   }

   @Override
   public final void inHolster() {
      this.closeEditor();
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void draftSaveOnClose() {
      ContextObject.setFlag(super._context, 121);
      this._saveDraftVerb.invoke(super._context);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         this.updateLookupFields();
         var9 = false;
      } finally {
         if (var9) {
            synchronized (this) {
               this._updateLookupScheduled = false;
            }
         }
      }

      synchronized (this) {
         this._updateLookupScheduled = false;
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == ALPConfiguration.getManager()) {
         synchronized (this) {
            if (!this._updateLookupScheduled) {
               this._updateLookupScheduled = true;
               this._app.invokeLater(this);
            }
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final boolean isAddressTrusted(String address, Object context) {
      if (this._trustedAddressCheckingEnabled && address != null && address.length() > 0) {
         SendMethod sm = this._transitoryEmailMessageModel.getMessagePropertiesModel().getSelectedSendMethod();
         ServiceRecord sr = sm == null ? null : sm.getServiceRecord();
         if (sr == null) {
            return true;
         }

         if (!ITPolicyInternal.verifyITAdminService(sr.getUid(), false)) {
            return true;
         }

         if (this._currentSecondLevelDomain == null) {
            String emailAddress = CMIMEUtilities.getEmailAddress(sr);
            this._currentSecondLevelDomain = CMIMEUtilities.getSecondLevelDomain(emailAddress);
            if (this._currentSecondLevelDomain == null || this._currentSecondLevelDomain.length() <= 0) {
               return true;
            }
         }

         int domainLength = this._currentSecondLevelDomain.length();
         int startMatchAt = address.length() - domainLength;
         return startMatchAt < 0 ? true : address.regionMatches(true, startMatchAt, this._currentSecondLevelDomain, 0, domainLength);
      } else {
         return true;
      }
   }

   private final VerticalFieldManager getVerticalFieldManager() {
      Manager mainManager = this.getMainManager();
      return (VerticalFieldManager)(!(mainManager instanceof Object) ? null : mainManager);
   }

   @Override
   public final Object getModel(boolean removeEmpty) {
      this.grabDataFromEdit(removeEmpty);
      return this._transitoryEmailMessageModel.getModel();
   }

   @Override
   protected final void grabDataFromEdit(boolean removeEmpty) {
      super.grabDataFromEdit(removeEmpty);
      EmailMessageModel model = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
      model.setTimestamp(System.currentTimeMillis());
   }

   private final void updateMessageEncodingHints(EmailMessageModel model, Font font) {
      byte encoding = model.getEncoding();
      if ((encoding & 112) == 0) {
         if (font != null) {
            model.setEncoding(CMIMEUtilities.addHints(encoding, font));
            return;
         }

         model.setEncoding(CMIMEUtilities.addHints(encoding));
      }
   }

   private final Verb getSendVerb() {
      if (this.findValidHeader() == null) {
         return null;
      }

      SendMethod sm = this._transitoryEmailMessageModel.getMessagePropertiesModel().getSelectedSendMethod();
      ServiceRecord sr = sm == null ? null : sm.getServiceRecord();
      LargeAttachmentModel[] models = this.getLargeAttachmentAttachedModels();
      if (models != null && models.length > 0) {
         if (sr == null) {
            return null;
         }

         if (NativeAttachmentRequestProcessor$Helper.validateServiceSupportsMessageForLargeAttachments(sr, models, 0) != 0) {
            return null;
         }

         if (sm != null && (sm.getEncodingAction() & 3) != 0) {
            return null;
         }
      }

      if (!CMIMEUtilities.isLargeAttachmentUploadAllowed(sr) && !this.validateCompressedFileAttachmentModels(sr)) {
         return null;
      }

      TransitoryMessagePropertiesModel messagePropertiesModel = this._transitoryEmailMessageModel.getMessagePropertiesModel();
      if (messagePropertiesModel != null) {
         EmailMessageModel emailMessageModel = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
         if (emailMessageModel.flagsSet(8192) && !EmailMessageUtilities.canSendPIN()) {
            return null;
         }

         SendMethod sendMethod = messagePropertiesModel.getSelectedSendMethod();
         if (sendMethod != null && sendMethod.isConfigured(emailMessageModel, super._context)) {
            return new EmailSendVerb(this, messagePropertiesModel);
         }
      }

      return null;
   }

   private final Screen getActiveMainScreen() {
      Screen activeScreen = this._app.getActiveScreen();

      while (activeScreen != null && !(activeScreen instanceof Object)) {
         activeScreen = activeScreen.getScreenBelow();
      }

      return activeScreen;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (instance != 65537) {
         EmailMessageModel model = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
         int type = model.getType();
         Verb defaultVerb = this._saveDraftVerb;
         ServiceRecord sr = null;
         boolean var24 = false /* VF: Semaphore variable */;

         try {
            var24 = true;
            TransitoryMessagePropertiesModel sendVerb = this._transitoryEmailMessageModel.getMessagePropertiesModel();
            SendMethod defaultSet = sendVerb.getSelectedSendMethod();
            if (defaultSet != null) {
               sr = defaultSet.getServiceRecord();
            }

            if (sr != null) {
               EmailTransport et = (EmailTransport)sr.getTransport();
               if (et != null) {
                  ContextObject.put(super._context, -7627884060408300435L, et);
               }
            }

            Field field = this.getModelFieldWithFocus();
            if (field != null) {
               Object cookie = field.getCookie();
               if (cookie instanceof EmailHeaderModel) {
                  EmailHeaderModel ehm = (EmailHeaderModel)cookie;
                  if (ehm.isBlank()) {
                     ContextObject.put(super._context, -3076179409848094191L, new Object(ehm.getHeaderType()));
                  }
               }
            }

            super.makeMenu(menu, instance);
            var24 = false;
         } finally {
            if (var24) {
               ContextObject.remove(super._context, -7627884060408300435L);
               ContextObject.remove(super._context, -3076179409848094191L);
            }
         }

         ContextObject.remove(super._context, -7627884060408300435L);
         ContextObject.remove(super._context, -3076179409848094191L);
         menu.add(this._saveDraftVerb);
         Verb sendVerb = this.getSendVerb();
         if (sendVerb != null) {
            menu.add(sendVerb);
            if (this.isDirty() || type == 16) {
               defaultVerb = sendVerb;
            }
         }

         if (instance == 65536) {
            menu.setDefault(defaultVerb);
            TransitoryMessagePropertiesModel messagePropertiesModel = this._transitoryEmailMessageModel.getMessagePropertiesModel();
            if (messagePropertiesModel != null) {
               messagePropertiesModel.getVerbs(menu, super._context);
            }
         } else {
            if (instance == 0) {
               boolean defaultSet = menu.getDefaultVerb() != null;
               Field focusedField = this.getModelFieldWithFocus();
               if (focusedField.getCookie() instanceof TransitoryMessagePropertiesModel && !focusedField.isMuddy()) {
                  defaultSet = true;
               }

               if (!defaultSet) {
                  menu.setDefault(defaultVerb);
               }

               if (type == 2 || type == 8) {
                  menu.add(this._deleteOriginalTextVerb);
               }

               this.processAttachFileVerb(sr, model, menu);
               TransitoryMessagePropertiesModel messagePropertiesModel = this._transitoryEmailMessageModel.getMessagePropertiesModel();
               if (messagePropertiesModel != null) {
                  messagePropertiesModel.getVerbs(menu, super._context);
               }

               EmailMessageModel message = (EmailMessageModelImpl)this._transitoryEmailMessageModel.getModel();
               if (EmailSendUtility.determineWhetherMessageAlreadyFiled(message, super._context)) {
                  DeleteSingleItemVerb deleteVerb = (DeleteSingleItemVerb)(new Object(611472, 1000));
                  deleteVerb.setParameters(message, super._context);
                  menu.add(deleteVerb);
               }

               if (this._prependedDisclaimerModelExists) {
                  PrependedDisclaimerModelField disclaimerField = (PrependedDisclaimerModelField)this.findField(PREPEND_DISCLAIMER_MODEL_RECOGNIZER);
                  if (disclaimerField != null) {
                     menu.add(new EmailEditorScreen$TogglePrependedDisclaimerModelFieldVerb(disclaimerField));
                  }
               }

               VerbRepository verbRepository = VerbRepository.getVerbRepository(1247995981604341294L);
               menu.add(verbRepository.getVerbs(null));
               verbRepository = VerbRepository.getVerbRepository(5244072729690617291L);
               menu.add(verbRepository.getVerbs(null));
               Field field = this.getModelFieldWithFocus();
               Object cookie = null;
               if (field != null) {
                  cookie = field.getCookie();
               }

               if (cookie instanceof EmailHeaderModel) {
                  EmailHeaderModel emailHeaderModel = (EmailHeaderModel)cookie;
                  int headerType = emailHeaderModel.getHeaderType();
                  Verb[] verbs = menu.getVerbs();
                  boolean focusIsOnLookup = emailHeaderModel.getInsideModel() instanceof Object;
                  boolean pickedADefault = false;
                  if (focusIsOnLookup) {
                     for (int i = 0; i < verbs.length; i++) {
                        if (verbs[i] instanceof Object) {
                           menu.setDefault(verbs[i]);
                           pickedADefault = true;
                           break;
                        }
                     }
                  }

                  if (!pickedADefault) {
                     for (int i = 0; i < verbs.length; i++) {
                        Verb var10000 = verbs[i];
                        if (verbs[i] instanceof Object) {
                           EditorUsingRIMModelFactory$AddWrapperVerb addWrapperVerb = (EditorUsingRIMModelFactory$AddWrapperVerb)var10000;
                           if (addWrapperVerb.getWrappedVerb() instanceof EmailHeaderAddVerb) {
                              EmailHeaderAddVerb emailHeaderAddVerb = (EmailHeaderAddVerb)addWrapperVerb.getWrappedVerb();
                              if (emailHeaderAddVerb.getHeaderType() == headerType) {
                                 menu.setDefault(verbs[i]);
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private final void processAttachFileVerb(ServiceRecord sr, EmailMessageModel model, SystemEnabledMenu menu) {
      if (!model.flagsSet(8192)) {
         ServiceRecord serviceRecord = sr;
         if (serviceRecord == null) {
            serviceRecord = CMIMEUtilities.findFirstAvailableServiceRecordSupportingNativeAttachment();
         }

         boolean serviceSupportsLargeAttachments = serviceRecord != null && CMIMEUtilities.isLargeAttachmentUploadAllowed(serviceRecord);
         if (serviceSupportsLargeAttachments) {
            menu.add((Verb)(new Object(this, new NativeAttachmentVerb(this, serviceRecord))));
         } else if (!this.hasSmallAttachments() && !this.hasLargeAttachments()) {
            if (!this.hasAddressCardAttachments()) {
               menu.add((Verb)(new Object(this, new NativeAttachmentVerb(this, sr, true))));
            }
         } else {
            this.removeAttachAddressVerb(menu);
         }
      }
   }

   private final void removeAttachAddressVerb(SystemEnabledMenu menu) {
      if (menu != null) {
         Verb[] verbs = menu.getVerbs();

         for (int i = 0; i < verbs.length; i++) {
            Verb verb = verbs[i];
            if (verb instanceof Object) {
               verb = ((EditorUsingRIMModelFactory$AddWrapperVerb)verb).getWrappedVerb();
            }

            if (verb instanceof EmailEditorScreen$AttachSmallAttachmentVerb) {
               menu.remove(verbs[i]);
               return;
            }
         }
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         ModelViewListenerRegistry.notifyModelOpened(this, super._context);
         this._app.addHolsterListener(this);
         super.onUiEngineAttached(attached);
         EmailMessageModel message = (EmailMessageModelImpl)this._transitoryEmailMessageModel.getModel();
         if (message != null && EmailSendUtility.determineWhetherMessageAlreadyFiled(message, super._context)) {
            this.updateLookupFields();
            return;
         }
      } else {
         ModelViewListenerRegistry.notifyModelClosed(this, super._context);
         ALPConfiguration.getManager().removeCollectionListener(this);
         this._app.removeHolsterListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   @Override
   protected final boolean onSave() {
      boolean saved = super.onSave();
      if (saved) {
         this._originalPayload = null;
      }

      return saved;
   }

   @Override
   public final boolean onClose() {
      boolean closed = super.onClose();
      if (closed && this._originalPayload != null && this.isDirty()) {
         this.restoreOriginalPayload();
      }

      return closed;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L && PersistentContent.isEncryptionEnabled()) {
         this.closeEditor();
      } else {
         if (guid == -8040378802380461050L) {
            EmailMessageModel model = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
            if (model != null) {
               this.updateMessageEncodingHints(model, null);
               return;
            }
         } else {
            super.eventOccurred(guid, data0, data1, object0, object1);
         }
      }
   }

   private final synchronized void closeEditor() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof Object) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         this._saveDraftVerb.invoke(super._context);
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   @Override
   protected final Manager createManagerForField(Field f, int order) {
      Manager manager = null;
      if (order < 2600) {
         manager = (Manager)(new Object());
         manager.setTag(ThemeUtilities.EMAIL_COMPOSE_HEADER_AREA_TAG);
         this._headerManager = manager;
         return manager;
      }

      if (order == 2600) {
         manager = new EmailEditorScreen$HeaderVerticalFieldManager();
         manager.setTag(ThemeUtilities.EMAIL_COMPOSE_SUBJECT_AREA_TAG);
         return manager;
      }

      if (order >= 5300 && order < 6500) {
         manager = (Manager)(new Object());
         manager.setTag(ThemeUtilities.EMAIL_COMPOSE_BODY_AREA_TAG);
         return manager;
      }

      if (order >= 6500) {
         manager = (Manager)(new Object());
         manager.setTag(ThemeUtilities.EMAIL_COMPOSE_ATTACHMENT_AREA_TAG);
      }

      return manager;
   }

   @Override
   public final long getModelType(Object context) {
      return -6822293833372928884L;
   }

   @Override
   public final RIMModel getOpenedModel(Object context) {
      return (RIMModel)this._transitoryEmailMessageModel.getModel();
   }

   @Override
   public final void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
      if (ContextObject.get(moreContext, 7801730636987331473L) == oldModel
         && this._transitoryEmailMessageModel.getModel() == oldModel
         && newModel instanceof EmailMessageModelImpl) {
         if (super._application != Application.getApplication() || !Application.isEventDispatchThread()) {
            super._application.invokeLater((Runnable)(new Object(this, oldModel, newModel, moreContext)));
            return;
         }

         EmailMessageModel emm = (EmailMessageModel)newModel;
         this._transitoryEmailMessageModel.setModel(emm);
         super.setModel(this._transitoryEmailMessageModel);
         this.setDirty(true);
      }
   }

   @Override
   public final void save() {
      this._saveDraftVerb.invoke(super._context);
   }

   private final int getModelsInScreenOrder(EmailPayloadModelImpl payload, Manager manager, Object[] models, int startIndex) {
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = manager.getField(i);
         Object cookie = field.getCookie();
         if (cookie == null) {
            if (field instanceof Object) {
               startIndex = this.getModelsInScreenOrder(payload, (Manager)field, models, startIndex);
            }
         } else if (cookie instanceof Object && cookie instanceof Object && payload.contains(cookie)) {
            for (int j = payload.size() - 1; j >= 0; j--) {
               if (cookie == payload.getAt(j)) {
                  payload.removeAt(j);
                  break;
               }
            }

            models[startIndex++] = cookie;
         }
      }

      return startIndex;
   }

   @Override
   protected final void ensureModelsInScreenOrder() {
      EmailMessageModel message = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
      if (message != null) {
         EmailPayloadModelImpl payload = (EmailPayloadModelImpl)message.getPayload();
         if (payload instanceof EmailPayloadModelImpl) {
            Object[] orderedModels = new Object[payload.size()];
            int index = this.getModelsInScreenOrder(payload, this.getMainManager(), orderedModels, 0);

            for (int i = payload.size() - 1; i >= 0; i--) {
               Object model = payload.getAt(i);
               if (model instanceof Object) {
                  payload.removeAt(i);
                  orderedModels[index++] = model;
               }
            }

            for (int i = 0; i < index; i++) {
               payload.add(orderedModels[i]);
            }
         }
      }
   }

   private final void saveCurrentCursorPosition(Field f) {
      EmailMessageModelImpl model = (EmailMessageModelImpl)this._transitoryEmailMessageModel.getModel();
      if (f instanceof Object) {
         VerticalFieldManager vfm = (VerticalFieldManager)f;
         Field saveField = vfm.getFieldWithFocus();
         if (saveField instanceof Object && saveField.getCookie() instanceof Object) {
            RichTextField rtf = (RichTextField)saveField;
            model.setCursorPosition(rtf.getCursorPosition());
            return;
         }

         model.setCursorPosition(0);
      }
   }

   private final void restoreSavedCursorPosition(Field f) {
      EmailMessageModelImpl model = (EmailMessageModelImpl)this._transitoryEmailMessageModel.getModel();
      int fieldCursor = model.getCursorPosition();
      if (f instanceof Object) {
         VerticalFieldManager vfm = (VerticalFieldManager)f;
         int origMsgIndex = -1;
         int fieldCount = vfm.getFieldCount();

         for (int i = 0; i < fieldCount; i++) {
            if (vfm.getField(i).getCookie() instanceof Object) {
               origMsgIndex = i;
            }
         }

         Field saveField = vfm.getField(origMsgIndex);
         if (saveField instanceof Object && fieldCursor > -1) {
            RichTextField rtf = (RichTextField)saveField;
            rtf.setCursorPosition(fieldCursor);
            rtf.setFocus();
            return;
         }

         saveField.setFocus();
      }
   }

   private final Field findEditableBodyField(Manager manager) {
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = manager.getField(i);
         if (field != null) {
            if (field instanceof Object) {
               field = this.findEditableBodyField((Manager)field);
               if (field != null) {
                  return field;
               }
            } else if (field.getCookie() instanceof Object && field.isEditable()) {
               return field;
            }
         }
      }

      return null;
   }

   private final boolean autoOpen(RequestModel requestModel) {
      if (requestModel.isResolved()) {
         return false;
      } else if (requestModel.numberAvailableForResolution() <= 1) {
         return false;
      } else {
         Verb[] verbs = new Object[0];
         Verb defaultVerb = requestModel.getVerbs(super._context, verbs);
         if (defaultVerb != null) {
            defaultVerb.invoke(super._context);
            return true;
         } else {
            return false;
         }
      }
   }

   private final void setFocusToOriginalMessageBottom(Field f) {
      if (f instanceof Object) {
         VerticalFieldManager vfm = (VerticalFieldManager)f;
         int vfmFieldCount = vfm.getFieldCount();
         Field lastField = vfm.getField(vfmFieldCount - 1);
         if (lastField instanceof Object) {
            RichTextField rtf = (RichTextField)lastField;
            int rtfLength = rtf.getTextLength();
            rtf.setCursorPosition(rtfLength > 0 ? rtfLength - 1 : 0);
            rtf.setFocus();
            return;
         }

         lastField.setFocus();
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.autoOpenUnresolvedLookup()) {
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   private final boolean autoOpenUnresolvedLookup() {
      Object cookie = this.getModelFieldWithFocus().getCookie();
      if (cookie instanceof EmailHeaderModel) {
         EmailHeaderModel emailHeaderModel = (EmailHeaderModel)cookie;
         if (emailHeaderModel.getInsideModel() instanceof Object && this.autoOpen((RequestModel)emailHeaderModel.getInsideModel())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 127 || Keypad.getAltedChar(key) == 127) {
         Object cookie = this.getModelFieldWithFocus().getCookie();
         if (cookie instanceof EmailHeaderModel) {
            EmailHeaderModel emailHeaderModel = (EmailHeaderModel)cookie;
            if (emailHeaderModel.hasFreeFormAddress() && !emailHeaderModel.isBlank()) {
               this.handleRecipientRemoved(emailHeaderModel);
            } else if (this.canDeleteModel(emailHeaderModel)) {
               this.deleteModel(cookie);
               return true;
            }
         }
      }

      return key == '\n' && this.autoOpenUnresolvedLookup() ? true : super.keyChar(key, status, time);
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      if (super.keyDown(keycode, time)) {
         return true;
      }

      Field focusField = this.getModelFieldWithFocus();
      Object cookie = focusField.getCookie();
      if (!(cookie instanceof EmailPayloadModelImpl)) {
         return false;
      }

      int k = MessageHotkeys.map(keycode);
      switch (k) {
         case 141:
            this.saveCurrentCursorPosition(focusField);
            this.setFocusToEditableBody(false);
            return true;
         case 142:
            this.saveCurrentCursorPosition(focusField);
            this.setFocusToOriginalMessageBottom(focusField);
            return true;
         case 147:
            this.restoreSavedCursorPosition(focusField);
            return true;
         default:
            return false;
      }
   }

   private final void saveOriginalPayload() {
      if (this._originalPayload == null) {
         EmailMessageModel message = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
         if (message != null) {
            EmailPayloadModel payload = message.getPayload();
            if (payload instanceof Object) {
               CloneProvider cloneable = (CloneProvider)payload;
               this._originalPayload = (EmailPayloadModel)cloneable.clone(super._context);
               if (this._originalPayload instanceof EmailPayloadModelImpl) {
                  EmailPayloadModelImpl originalImpl = (EmailPayloadModelImpl)this._originalPayload;
                  EmailPayloadModelImpl payloadImpl = (EmailPayloadModelImpl)payload;
                  originalImpl._timestamp = payloadImpl._timestamp;
                  originalImpl.setCreationDate(payloadImpl.getCreationDate());
               }
            }
         }
      }
   }

   private final void restoreOriginalPayload() {
      if (this._originalPayload != null) {
         EmailMessageModel message = (EmailMessageModel)this._transitoryEmailMessageModel.getModel();
         if (message != null) {
            message.setPayload(this._originalPayload);
         }
      }
   }

   @Override
   protected final int getOrderForManagerForField(Field field, int order) {
      if (order == 2030 && field instanceof Object && ((Manager)field).getFieldCount() > 0) {
         field.setTag(ThemeUtilities.EMAIL_COMPOSE_HEADER_MAILBOX_TAG);
      }

      if (order < 2600) {
         return 2240;
      } else if (order == 2600) {
         return 2600;
      } else if (order >= 5300 && order < 6500) {
         return 5500;
      } else {
         return order >= 6500 ? 6500 : -1;
      }
   }

   public EmailEditorScreen(Object context) {
      super(8796093022208L, context, null, ContextObject.getFlag(context, 94) ? 3893959701496671961L : 2497613418300956405L, -1);
      this._saveDraftVerb = new EmailSaveDraftVerb(this);
      this._deleteOriginalTextVerb = new EmailDeleteOriginalTextVerb(this);
      ALPConfiguration.getManager().addCollectionListener(this);
      ContextObject.put(super._context, -6581931217101110672L, this);
      ContextObject.put(super._context, 9120441889802231811L, this);
      ContextObject.setFlag(super._context, 43);
      ContextObject.put(super._context, 32241034113959076L, this._transitoryEmailMessageModel.getMessagePropertiesModel());
      ContextObject.put(super._context, 244, "messages_index");
      ContextObject.setFlag(super._context, 96);
      this.getMainManager().setTag(ThemeUtilities.EMAIL_EDITOR_SCREEN_TAG);
   }

   private final boolean searchViewScreenIsOpen() {
      return this.getActiveMainScreen() instanceof Object;
   }

   private final void collectLookupFields(Manager manager, Field[] fields) {
      for (int i = manager.getFieldCount() - 1; i >= 0; i--) {
         Field field = manager.getField(i);
         if (field != null && field.getCookie() instanceof EmailHeaderModel) {
            Arrays.add(fields, field);
         } else if (field instanceof Object) {
            this.collectLookupFields((Manager)field, fields);
         }
      }
   }

   @Override
   public final ContextObject getContext() {
      return (ContextObject)super._context;
   }

   @Override
   public final Field findField(Recognizer recognizer) {
      return this.findField(this.getMainManager(), recognizer);
   }

   private final Field findField(Manager manager, Recognizer recognizer) {
      int size = manager.getFieldCount();

      for (int i = 0; i < size; i++) {
         Field field = manager.getField(i);
         Object cookie = field.getCookie();
         if (recognizer.recognize(cookie)) {
            return field;
         }

         if (field instanceof Object && !(cookie instanceof EmailPayloadModel)) {
            field = this.findField((Manager)field, recognizer);
            if (field != null) {
               return field;
            }
         }
      }

      return null;
   }

   @Override
   public final void insertModel(Object m) {
      if (m instanceof EmailHeaderModel) {
         EmailHeaderModel ehm = (EmailHeaderModel)m;
         if (ehm.isBlank()) {
            Field blankField = this.findBlankHeader(ehm.getHeaderType());
            if (blankField != null) {
               blankField.setFocus();
               return;
            }
         }
      }

      super.insertModel(m);
   }

   @Override
   public final void deleteModel(Object m) {
      super.deleteModel(m);
      if (m instanceof EmailHeaderModel) {
         EmailHeaderModel emailHeaderModel = (EmailHeaderModel)m;
         this.handleRecipientRemoved(emailHeaderModel);
      }
   }

   @Override
   protected final boolean canDeleteModel(RIMModel model) {
      boolean canDel = super.canDeleteModel(model);
      if (canDel && model instanceof EmailHeaderModel) {
         EmailHeaderModel hm = (EmailHeaderModel)model;
         if (0 == hm.getHeaderType()) {
            Recognizer recognizer = new ToFieldRecognizer();
            canDel = this.getModelCount(recognizer) > 1;
            Object var5 = null;
         }
      }

      return canDel;
   }

   @Override
   public final void setModel(Object model) {
      EmailMessageModel message = (EmailMessageModel)model;
      boolean isPin = message.flagsSet(8192);
      if (isPin) {
         ContextObject.setFlag(super._context, 94);
      } else {
         ContextObject.clearFlag(super._context, 94);
      }

      if (message.flagsSet(8388608)) {
         ContextObject.setPrivateFlag(super._context, -3859986508589425865L, 1);
      } else {
         ContextObject.clearPrivateFlag(super._context, -3859986508589425865L, 1);
      }

      PrependedDisclaimerModel disclaimerModel = (PrependedDisclaimerModel)SubmemberUtilities.getFirstSubmember(message, PREPEND_DISCLAIMER_MODEL_RECOGNIZER);
      String prependedDisclaimer = ITPolicy.getString(23, 3);
      if (!isPin && prependedDisclaimer != null && prependedDisclaimer.length() > 0) {
         if (disclaimerModel == null) {
            EmailPayloadModelImpl payload = (EmailPayloadModelImpl)message.getPayload();
            disclaimerModel = new PrependedDisclaimerModel();
            BodyModel body = message.getBodyModel();
            if (body == null) {
               body = (BodyModel)FactoryUtil.createInstance(5987399499453925075L, null);
               payload.add(body);
            }

            payload.insertAt(disclaimerModel, payload.getIndex(body));
         }

         this._prependedDisclaimerModelExists = true;
      } else if (disclaimerModel != null) {
         message.remove(disclaimerModel);
         this._prependedDisclaimerModelExists = false;
      }

      this._transitoryEmailMessageModel.setModel(message);
      super.setModel(this._transitoryEmailMessageModel);
   }

   private final void validateAllEmailAddressFields() {
      this._currentSecondLevelDomain = null;
      if (this._headerManager != null) {
         for (int i = this._headerManager.getFieldCount() - 1; i >= 0; i--) {
            Field f = this._headerManager.getField(i);
            if (f instanceof Object) {
               ((AddressVerifierAwareField)f).verifyAddress(null);
            }
         }
      }
   }
}
