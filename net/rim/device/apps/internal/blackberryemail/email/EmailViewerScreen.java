package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.model.AddressVerifier;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.EmailBodyProvider;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.ui.ViewFolderVerb;
import net.rim.device.apps.api.messaging.util.FileMessageVerb;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.InvokeLaterRunnable;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.FindVerbManager;
import net.rim.device.apps.api.utility.viewer.ViewReadableListRIMModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

final class EmailViewerScreen extends ViewReadableListRIMModel implements SystemListener, HolsterListener, AddressVerifier {
   private boolean _autoMoreAlreadyInvoked;
   private EmailMessageModelImpl _message;
   private EmailBodyProvider _ebp;
   private LabelSeparatorField _moreLabel;
   private boolean _processedDismissViewerEvent;
   private EmailResponseVerb _replyVerb;
   private EmailResponseVerb _replyToAllVerb;
   private EmailResponseVerb _forwardVerb;
   private EmailChangeStatusVerb _markReadVerb;
   private EmailChangeStatusVerb _markUnreadVerb;
   private EmailChangeStatusVerb _lockVerb;
   private FileMessageVerb _fileVerb;
   private ViewFolderVerb _viewFolderVerb;
   private EmailViewerScreen$EditMessageVerb _editMessageVerb;
   private EmailResendVerb _resendVerb;
   private DeleteSingleItemVerb _deleteSingleItemVerb;
   private EmailMoreVerb _moreVerb;
   private EmailMoreVerb _autoMoreVerb;
   private EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel _transitoryEmailMessageModel = new EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel();
   private boolean _messageInfoVisible;
   private InvokeLaterRunnable _invokeLaterRunnable = (InvokeLaterRunnable)(new Object());
   private String _currentSecondLevelDomain;
   private boolean _trustedAddressCheckingEnabled = false;
   private boolean _automoreUnavailableIsLogged;
   FindVerbManager _findVerbManager = (FindVerbManager)(new Object(this.getDelegate()));
   static final long EMAIL_PRIVATE_FLAGS;
   static final int MORE_UPDATE_BODY_FLAG;

   EmailViewerScreen(Object context) {
      super(288239172244733952L, null, context, 0, RecognizerRepository.getRecognizers(-1249752217278100236L));
      ContextObject.setFlag(super._context, 43);
      this.setScrollBehaviourView(false);
      this.getMainManager().setVerticalQuantization(1);
      this.getMainManager().setTag(ThemeUtilities.EMAIL_VIEWER_SCREEN_TAG);
      ContextObject.put(super._context, -6581931217101110672L, this);
      ContextObject.put(super._context, 9120441889802231811L, this);
      ContextObject.setFlag(super._context, 96);
      int separatorInsertionPoint = 2600;
      this.addForcedSeparator(separatorInsertionPoint);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (object0 != this._message) {
         if (guid == 6345609069135580235L) {
            if (PersistentContent.isEncryptionEnabled()) {
               this.notifyOfOpenedModelChange(this._message, this._message, null);
               UiApplication.getUiApplication().requestForeground();
            }

            this.markMessageOpened();
         } else if (guid == -7131874474196788121L) {
            this.closeMessage();
         }
      } else {
         if (guid == 1202366544244619460L) {
            this._processedDismissViewerEvent = true;
            UiApplication.getUiApplication().popScreen(this);
            return;
         }

         if (guid == -6275418955626563374L) {
            if (this._processedDismissViewerEvent) {
               EventLogger.logEvent(-1237457833540244999L, 1297039684, 2);
            }

            boolean deviceLocked = ApplicationManager.getApplicationManager().isSystemLocked();
            if ((this._message.getFlags() & 1) == 0) {
               if (!deviceLocked) {
                  this.markMessageOpened();
               }

               this._transitoryEmailMessageModel.setModel(this._message);
            }

            if (!deviceLocked || !PersistentContent.isEncryptionEnabled()) {
               UiApplication.getUiApplication().requestForeground();
            }

            return;
         }
      }

      super.eventOccurred(guid, data0, data1, object0, object1);
   }

   final void markMessageOpened() {
      boolean oldOpenedStatus = this._message.flagsSet(1);
      this._message.changeStatus(1, 0, 0, 0, true, true, true, false, super._context);
      if (!oldOpenedStatus && this._message.flagsSet(64)) {
         IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
         if (implusService != null) {
            implusService.sendReceipt(this._message, true);
         }
      }
   }

   @Override
   public final Object getModel() {
      return this._message;
   }

   @Override
   public final Object getModel(boolean removeEmpty) {
      return this._message;
   }

   @Override
   protected final boolean supportsIncrementalUpdate(Object context) {
      if (ContextObject.getFlag(context, 98)) {
         return ContextObject.getPrivateFlag(super._context, -4104667787783617270L, 0) && this._ebp instanceof EmailFieldUpdater
            ? ((EmailFieldUpdater)this._ebp).supportsEmailFieldUpdate(context)
            : false;
      } else {
         return ContextObject.getFlag(context, 137);
      }
   }

   @Override
   protected final boolean performIncrementalUpdate(Field[] fields) {
      if (!ContextObject.getFlag(super._context, 98)) {
         return ContextObject.getFlag(super._context, 137);
      }

      if (ContextObject.getPrivateFlag(super._context, -4104667787783617270L, 0)) {
         Field bodyField = null;
         EmailFieldUpdater bodyAppender = null;

         for (int i = 0; i < fields.length; i++) {
            if (fields[i].getCookie() instanceof EmailFieldUpdater) {
               bodyField = (Field)fields[i];
               bodyAppender = (EmailFieldUpdater)fields[i].getCookie();
               break;
            }
         }

         if (bodyAppender != null) {
            return bodyAppender.performEmailFieldUpdate(bodyField, super._context);
         }
      }

      return false;
   }

   @Override
   protected final boolean validateFieldProvider(Field field, FieldProvider fieldProvider) {
      boolean validated = super.validateFieldProvider(field, fieldProvider);
      if (!validated && fieldProvider instanceof ProxyModel) {
         Object innerFieldProvider = ((ProxyModel)fieldProvider).getObject();
         if (innerFieldProvider instanceof Object) {
            validated = super.validateFieldProvider(field, (FieldProvider)innerFieldProvider);
         }
      }

      return validated;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void setModel(Object model) {
      boolean isIncrementalUpdate = false;
      if (this._message != null && (ContextObject.getFlag(super._context, 137) || ContextObject.getFlag(super._context, 98))) {
         isIncrementalUpdate = true;
      }

      this._message = (EmailMessageModelImpl)model;
      if (!this._message.inbound()) {
         super._initiallyFocussedRecognizer = RecognizerRepository.getRecognizers(4724113409898500292L);
      }

      if (this._message.flagsSet(8192)) {
         ContextObject.setFlag(super._context, 94);
      } else {
         ContextObject.clearFlag(super._context, 94);
      }

      if (!isIncrementalUpdate) {
         this._transitoryEmailMessageModel.setModel(this._message);
      }

      ContextObject.put(super._context, 246, model);
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         super.setModel(this._transitoryEmailMessageModel);
         var7 = false;
      } finally {
         if (var7) {
            ContextObject.remove(super._context, 246);
         }
      }

      ContextObject.remove(super._context, 246);
      if (super._fieldToGetFocus != null) {
         while (super._fieldToGetFocus.getManager() != null && !super._fieldToGetFocus.getManager().isStyle(281474976710656L)) {
            super._fieldToGetFocus = super._fieldToGetFocus.getManager();
         }
      }

      this.locateEmailBodyProvider(this);
      if (isIncrementalUpdate && this._moreLabel != null && this._moreLabel.getManager() != null) {
         this._moreLabel.getManager().delete(this._moreLabel);
      }

      if (this._message.flagsSet(256) || this._message.flagsSet(4096)) {
         if (this._moreLabel == null) {
            this._moreLabel = new LabelSeparatorField();
            this._moreLabel.setTag(ThemeUtilities.EMAIL_MORE_STATUS_TAG);
         }

         int moreFieldIndex = this.getFieldCount();
         Manager messageBodyManager = this;
         if (this._ebp != null) {
            Field messageBodyField = this.findFieldByCookie(this._ebp);
            moreFieldIndex = messageBodyField.getIndex() + 1;
            messageBodyManager = messageBodyField.getManager();
         }

         messageBodyManager.insert(this._moreLabel, moreFieldIndex);
         this.setMoreMessageStatus(ContextObject.getFlag(super._context, 98));
      }

      if (!isIncrementalUpdate) {
         ServiceRecord thisMessageServiceRecord = EmailMessageUtilities.getServiceRecordForMessage(this._message);
         if (thisMessageServiceRecord != null && this._message.flagsSet(16777216)) {
            this.setSecurityServiceColours(ITPolicyInternal.verifyITAdminService(thisMessageServiceRecord.getUid(), false));
            return;
         }

         if (this._message.flagsSet(8192) && this._message.flagsSet(32768) && !this._message.flagsSet(1048576)) {
            this.setSecurityServiceColours(true);
            return;
         }

         this.setSecurityServiceColours(false);
      }
   }

   private final void initializeCachedVerbs() {
      if (this._replyVerb == null) {
         this._replyVerb = new EmailResponseVerb(602624, 9147, 53, false, false);
      }

      if (this._replyToAllVerb == null) {
         this._replyToAllVerb = new EmailResponseVerb(603136, 9148, 30, false, false);
      }

      if (this._forwardVerb == null) {
         this._forwardVerb = new EmailResponseVerb(602880, 9149, 13, false, false);
      }

      if (this._markReadVerb == null) {
         this._markReadVerb = new EmailChangeStatusVerb(602448, 17, 1, 0, 0);
      }

      if (this._markUnreadVerb == null) {
         this._markUnreadVerb = new EmailChangeStatusVerb(602450, 18, 0, 1, 0);
      }

      if (this._lockVerb == null) {
         this._lockVerb = new EmailChangeStatusVerb(602480, 19, 16, 0, 0);
      }

      if (this._fileVerb == null) {
         this._fileVerb = (FileMessageVerb)(new Object(602464));
      }

      if (this._viewFolderVerb == null) {
         this._viewFolderVerb = ViewFolderVerb.getInstance();
      }

      if (this._editMessageVerb == null) {
         this._editMessageVerb = new EmailViewerScreen$EditMessageVerb(this);
      }

      if (this._resendVerb == null) {
         this._resendVerb = new EmailResendVerb();
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (instance == 65536 && this.isScrollBehaviourView()) {
         menu.add(MenuItem.getPrefab(16));
      }

      super.makeMenu(menu, instance);
   }

   private final Verb getMoreVerb(boolean moreAll) {
      if (!this._message.flagsSet(256)) {
         return null;
      }

      if (moreAll) {
         if (EmailMoreVerb.isMoreAllAllowed(this._message)) {
            EmailMoreVerb moreAllVerb = new EmailMoreVerb(this._message, (byte)2);
            moreAllVerb.setMoreRequestTarget((byte)1);
            return moreAllVerb;
         } else {
            return null;
         }
      } else {
         if (this._moreVerb == null) {
            this._moreVerb = new EmailMoreVerb(this._message, (byte)1);
            this._moreVerb.setMoreRequestTarget((byte)1);
         }

         return this._moreVerb;
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      this.initializeCachedVerbs();
      Field focusedField = this.getModelFieldWithFocus();
      Object cookie = null;
      if (focusedField != null) {
         cookie = focusedField.getCookie();
      }

      Verb[] factoryVerbs = new Object[0];
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(6003662854924499794L);
      if (verbFactories != null && verbFactories.length > 0) {
         ContextObject verbFactoryContext = (ContextObject)(new Object(43));
         verbFactoryContext.put(-7450314121582082994L, this._message);
         if (cookie != null) {
            verbFactoryContext.put(250, cookie);
         }

         for (int i = verbFactories.length - 1; i >= 0; i--) {
            Verb[] verbs = verbFactories[i].getVerbs(verbFactoryContext);
            if (verbs != null) {
               Arrays.append(factoryVerbs, verbs);
            }
         }
      }

      long folderID = this._message.getFolderId();
      EmailFolder folder = (EmailFolder)FolderHierarchies.getFolder(folderID);
      if (folder == null) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(folderID);
         if (hierarchy != null) {
            folder = (EmailFolder)hierarchy.getFolder(hierarchy.getInboxFolder());
         }
      }

      boolean messageInValidFolder = false;
      if (folder != null && folder.isInFolderDatabase()) {
         this._fileVerb.setParameters(this._message);
         messageInValidFolder = true;
         if (this._viewFolderVerb != null) {
            this._viewFolderVerb.setDefaultFolder(folder);
            this._viewFolderVerb.setHierarchy((Folder)folder.getEmailHierarchy());
         }
      }

      Verb moreVerb = this.getMoreVerb(false);
      Verb moreAllVerb = this.getMoreVerb(true);
      boolean sendFailed = false;
      boolean calendarAttachmentPresent = false;
      if (!this._message.inbound()
         && EmailMessageUtilities.allowOutboundMessages(this._message)
         && (this._message.flagsSet(8192) || EmailMessageUtilities.getServiceRecordForMessage(this._message) != null)) {
         this._resendVerb.setParameters(this._message);
         ReadableList payload = this._message.getPayload();

         for (int i = 0; i < payload.size(); i++) {
            if (payload.getAt(i) instanceof Object) {
               calendarAttachmentPresent = true;
            }
         }

         int messageStatus = this._message.getStatus();
         sendFailed = messageStatus == 8191 || messageStatus == 16383;
      }

      if (instance != 65536) {
         if (instance == 65537) {
            menu.setAlignment(12884901888L, 34359738368L);
         } else {
            if (super._defaultVerb != null && cookie instanceof Object && cookie instanceof Object && ((FieldProvider)cookie).getOrder(super._context) < 6500) {
               super._defaultVerb = null;
            }

            if (this._message.inbound()) {
               if (this._message.canBeRepliedTo() && EmailMessageUtilities.allowOutboundMessages(this._message)) {
                  this._replyVerb.setParameter(this._message);
                  menu.add(this._replyVerb);
                  if (super._defaultVerb == null) {
                     super._defaultVerb = this._replyVerb;
                  }
               }

               if (this._message.flagsSet(1)) {
                  this._markUnreadVerb.setParameters(this._message);
                  menu.add(this._markUnreadVerb);
               } else {
                  this._markReadVerb.setParameters(this._message);
                  menu.add(this._markReadVerb);
               }
            } else {
               if (EmailMessageUtilities.allowOutboundMessages(this._message)
                  && (this._message.flagsSet(8192) || EmailMessageUtilities.getServiceRecordForMessage(this._message) != null)
                  && (!calendarAttachmentPresent || sendFailed)) {
                  menu.add(this._resendVerb);
                  if (sendFailed && super._defaultVerb == null) {
                     super._defaultVerb = this._resendVerb;
                  }
               }

               menu.add(this._editMessageVerb);
            }

            if (!this._message.flagsSet(16)) {
               this._lockVerb.setParameters(this._message);
               menu.add(this._lockVerb);
            }

            if (messageInValidFolder) {
               menu.add(this._fileVerb);
               if (this._viewFolderVerb != null) {
                  menu.add(this._viewFolderVerb);
                  if (cookie instanceof Object) {
                     FieldProvider fp = (FieldProvider)cookie;
                     if (fp.getOrder(super._context) == 2040) {
                        super._defaultVerb = this._viewFolderVerb;
                     }
                  }
               }
            }

            if (this._message.canBeForwarded() && EmailMessageUtilities.allowOutboundMessages(this._message)) {
               if (this._message.canBeRepliedTo()) {
                  this._replyToAllVerb.setParameter(this._message);
                  menu.add(this._replyToAllVerb);
               }

               this._forwardVerb.setParameter(this._message);
               menu.add(this._forwardVerb);
               ForwardAsVerb forwardAsVerb = (ForwardAsVerb)(new Object(this._message));
               if (forwardAsVerb.canInvoke(null)) {
                  menu.add(forwardAsVerb);
               }

               if (super._defaultVerb == null) {
                  super._defaultVerb = this._forwardVerb;
               }
            }

            if (moreVerb != null) {
               menu.add(moreVerb);
               if (this.isFocusAtOrBeyondEndOfMessage(true)) {
                  super._defaultVerb = moreVerb;
               }
            }

            if (moreAllVerb != null) {
               menu.add(moreAllVerb);
            }

            menu.add(this._findVerbManager.getVerbs());
            VerbFactory outerVerbFactory = (VerbFactory)ContextObject.get(super._context, -2846768035584909703L);
            if (outerVerbFactory != null) {
               Object oldItemToWorkRelativeTo = ContextObject.get(super._context, -321822713458159100L);
               ContextObject.put(super._context, -321822713458159100L, this._message);
               menu.add(outerVerbFactory.getVerbs(super._context));
               if (oldItemToWorkRelativeTo != null) {
                  ContextObject.put(super._context, -321822713458159100L, oldItemToWorkRelativeTo);
               } else {
                  ContextObject.remove(super._context, -321822713458159100L);
               }
            }

            VerbRepository verbRepository = VerbRepository.getVerbRepository(3433073725342984424L);
            if (verbRepository != null) {
               menu.add(verbRepository.getVerbs(null));
            }

            verbRepository = VerbRepository.getVerbRepository(-6481681929958323011L);
            if (verbRepository != null) {
               menu.add(verbRepository.getVerbs(null));
            }

            if (factoryVerbs != null) {
               for (int j = factoryVerbs.length - 1; j >= 0; j--) {
                  if ((cookie instanceof EmailViewerScreen$AttachmentInfoModel || cookie instanceof ProxyModel || cookie instanceof UnknownMimePartModel)
                     && factoryVerbs[j].getOrdering() == 603904) {
                     super._defaultVerb = factoryVerbs[j];
                  }

                  menu.add(factoryVerbs[j]);
               }
            }

            menu.setDefault(super._defaultVerb);
            DefaultVerbProvider defaultVerbProvider = null;
            if (!(cookie instanceof EmailHeaderModel)) {
               if (!this.isOnHyperlink(false) && this._message.canBeRepliedTo()) {
                  Field emailHeaderField = this.getEmailHeaderFieldForCommunicationVerbs();
                  if (emailHeaderField != null && emailHeaderField != this.getModelFieldWithFocus()) {
                     Verb oldDefaultVerb = menu.getDefaultVerb();
                     this.loadFocusedFieldVerbs(menu, emailHeaderField, emailHeaderField);
                     menu.setDefault(oldDefaultVerb);
                  }
               }
            } else {
               EmailHeaderModel headerModel = (EmailHeaderModel)cookie;
               RIMModel addressBookEntry = headerModel.getAddressBookEntry();
               if (addressBookEntry != null) {
                  defaultVerbProvider = (DefaultVerbProvider)(new Object(addressBookEntry));
               }
            }

            menu.coalesce(-3072555018635390988L, defaultVerbProvider);
         }
      } else {
         if (messageInValidFolder) {
            menu.add(this._fileVerb);
         }

         if (super._defaultVerb != null && cookie instanceof Object && cookie instanceof Object && ((FieldProvider)cookie).getOrder(super._context) < 6500) {
            super._defaultVerb = null;
         }

         if (this._message.inbound()) {
            if (this._message.canBeRepliedTo() && EmailMessageUtilities.allowOutboundMessages(this._message)) {
               this._replyVerb.setParameter(this._message);
               menu.add(this._replyVerb);
               if (super._defaultVerb == null) {
                  super._defaultVerb = this._replyVerb;
               }
            }
         } else if (sendFailed) {
            menu.add(this._resendVerb);
            if (super._defaultVerb == null) {
               super._defaultVerb = this._resendVerb;
            }
         }

         if (this._message.canBeForwarded() && EmailMessageUtilities.allowOutboundMessages(this._message)) {
            if (this._message.canBeRepliedTo()) {
               this._replyToAllVerb.setParameter(this._message);
               menu.add(this._replyToAllVerb);
            }

            this._forwardVerb.setParameter(this._message);
            menu.add(this._forwardVerb);
            if (super._defaultVerb == null) {
               super._defaultVerb = this._forwardVerb;
            }
         }

         DeleteSingleItemVerb deleteSingleItemVerb = (DeleteSingleItemVerb)(new Object(611472, 1000));
         deleteSingleItemVerb.setParameters(this._message, ContextObject.castOrCreate(super._context));
         menu.add(deleteSingleItemVerb);
         if (factoryVerbs != null) {
            for (int j = factoryVerbs.length - 1; j >= 0; j--) {
               if (factoryVerbs[j].getOrdering() == 603904) {
                  menu.add(factoryVerbs[j]);
                  if (cookie instanceof EmailViewerScreen$AttachmentInfoModel || cookie instanceof ProxyModel || cookie instanceof UnknownMimePartModel) {
                     super._defaultVerb = factoryVerbs[j];
                  }
                  break;
               }
            }
         }

         if (this.isFocusAtOrBeyondEndOfMessage(true)) {
            if (moreVerb != null) {
               menu.add(moreVerb);
               super._defaultVerb = moreVerb;
            }

            if (moreAllVerb != null) {
               menu.add(moreAllVerb);
            }
         }

         menu.setDefault(super._defaultVerb);
         menu.setAlignment(12884901888L, 34359738368L);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1 && this.isOnHyperlink(true)) {
         this.invokeDefaultMenuItem(0);
         return true;
      } else {
         return super.invokeAction(action);
      }
   }

   private final boolean isOnHyperlink(boolean browserURLOnly) {
      Field focusedField = this.getFieldWithFocus();
      if (focusedField instanceof Object) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)focusedField).getCookieWithFocus());
         if (browserURLOnly) {
            if (cookie instanceof Object) {
               if (((URLProvider)cookie).getURLType() == 1) {
                  return true;
               }

               return false;
            }
         } else if (cookie instanceof Object) {
            return true;
         }
      }

      focusedField = this.getLeafFieldWithFocus();
      if (focusedField instanceof Object) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)focusedField).getCookieWithFocus());
         if (browserURLOnly) {
            if (cookie instanceof Object) {
               if (((URLProvider)cookie).getURLType() == 1) {
                  return true;
               }

               return false;
            }
         } else if (cookie instanceof Object) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected final Object invokeVerb(Verb verb, Object parameter) {
      Object result = null;
      if (verb == this._fileVerb) {
         ContextObject.put(super._context, -321822713458159100L, this._message);
         ContextObject.setFlag(super._context, 100);
      }

      if (verb instanceof Object) {
         this.deleteViewedMessage();
         return result;
      }

      ContextObject contextObject = ContextObject.clone(parameter);
      contextObject.put(246, this._message);
      if (!(verb instanceof EmailMoreVerb)) {
         Field leafFieldWithFocus = this.getLeafFieldWithFocus();
         Object selectedModel = null;
         if (leafFieldWithFocus != null) {
            selectedModel = leafFieldWithFocus.getCookie();
         }

         if (selectedModel instanceof Object) {
            contextObject.put(254, selectedModel);
         }
      }

      return super.invokeVerb(verb, contextObject);
   }

   private final void deleteViewedMessage() {
      if (this._deleteSingleItemVerb == null) {
         this._deleteSingleItemVerb = (DeleteSingleItemVerb)(new Object(611472, 1000));
      }

      this._deleteSingleItemVerb.setParameters(this._message, super._context);
      super._returnValue = this._deleteSingleItemVerb.invoke(null);
      if (ContextObject.getFlag(super._returnValue, 39)) {
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   private final boolean processKeyChar(char key, int status, int time) {
      switch (key) {
         case ' ':
            int direction = (status & 2) == 0 ? 512 : 256;
            if (direction == 512) {
               this.checkWhetherShouldAutoMore();
            }

            this.scroll(direction);
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean processed = this.processKeyChar(key, status, time);
      if (!processed) {
         processed = super.keyChar(key, status, time);
      }

      return processed;
   }

   private final boolean checkWhetherShouldAutoMore() {
      if (!this._message.isMissingMessageOnServer() && this._message.getServiceRecordForMessage() != null) {
         if (!this._autoMoreAlreadyInvoked) {
            MessageListOptions options = MessageListOptions.getOptions();
            if (!options.getFlag(64)) {
               return false;
            }

            MorePartModel morePartModel = EmailMoreVerb.findBodyMorePartModel(this._message);
            if (morePartModel instanceof Object && morePartModel.isMoreAvailable() && morePartModel.isAutoMoreAvailable()) {
               EmailBodyProvider cursorProvider = (EmailBodyProvider)morePartModel;
               Field morePartField = this.findFieldByCookie(morePartModel);
               int pos = cursorProvider.getCurrentCursorPosition(morePartField, null);
               int maxCursorPos = cursorProvider.getCursorCount(morePartField, null);
               if (maxCursorPos == 0) {
                  return false;
               }

               if (maxCursorPos - pos < options.getAutoMoreTrigger()) {
                  if (this._autoMoreVerb == null) {
                     this._autoMoreVerb = new EmailMoreVerb(this._message, (byte)3);
                     this._autoMoreVerb.setMoreRequestTarget((byte)1);
                  }

                  this._autoMoreVerb.invoke(null);
                  if ((this._message.getFlags() & 65536) != 0) {
                     this._autoMoreAlreadyInvoked = true;
                  }

                  return true;
               }
            }
         }

         return false;
      } else {
         if (!this._automoreUnavailableIsLogged) {
            this._automoreUnavailableIsLogged = true;
            EventLogger.logEvent(-1237457833540244999L, 1632980545, 3);
         }

         return false;
      }
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      this.checkWhetherShouldAutoMore();
      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      this.checkWhetherShouldAutoMore();
      return super.navigationMovement(dx, dy, status, time);
   }

   private final void insertMessageInfo() {
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      EmailFolder folder = (EmailFolder)FolderHierarchies.getFolder(this._message.getFolderId());
      EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(this._message.getFolderId());
      vfm.add((Field)(new Object(((StringBuffer)(new Object("RefId: "))).append(this._message.getCMIMEReferenceIdentifier()).toString(), 18014398509481984L)));
      if (folder != null) {
         vfm.add((Field)(new Object(((StringBuffer)(new Object("FolderId: "))).append(folder.getFolderId()).toString(), 18014398509481984L)));
      }

      if (hierarchy != null) {
         vfm.add((Field)(new Object(((StringBuffer)(new Object("ServiceUserId: "))).append(hierarchy.getServiceUserId()).toString(), 18014398509481984L)));
      }

      vfm.add((Field)(new Object()));
      this.insert(vfm, 0);
      vfm.setFocus();
      this._messageInfoVisible = true;
   }

   @Override
   protected final boolean openProductionBackdoor(int backDoor) {
      switch (backDoor) {
         case 1447642454:
            return super.openProductionBackdoor(backDoor);
         case 1447642455:
         default:
            if (!this._messageInfoVisible) {
               this.insertMessageInfo();
            }

            return true;
      }
   }

   private final boolean invokeHotkeyOnMessage(int hotkeyId) {
      Object returnValue = this._message.invokeHotkey(super._context, hotkeyId);
      if (returnValue != null) {
         if (ContextObject.getFlag(returnValue, 39)) {
            UiApplication.getUiApplication().popScreen(this);
         }

         return true;
      } else {
         return false;
      }
   }

   private final void gotoLastPosition() {
      if (this._ebp != null) {
         Field ebpField = this.findFieldByCookie(this._ebp);
         this._ebp.setCursorPosition(ebpField, this._message.getCursorPosition(), super._context);
         this._ebp.setFocus(ebpField, super._application, super._context);
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      char key = Keypad.map(keycode);
      if (super.keyUp(keycode, time)) {
         return true;
      }

      if (key == 127 || Keypad.getAltedChar(key) == 127) {
         this.deleteViewedMessage();
         return true;
      }

      if (key == 27) {
         return false;
      }

      if (InternalServices.isReducedFormFactor() && (Keypad.status(keycode) & 1) == 0) {
         int direction = 256;
         switch (Keypad.key(keycode)) {
            case 66:
               return this.dispatchTrackwheelEvent(519, 1, 0, time);
            case 71:
               this.gotoLastPosition();
               return true;
            case 77:
               direction = 512;
               this.checkWhetherShouldAutoMore();
            case 85:
               this.scroll(direction);
               return true;
            case 84:
               return this.dispatchTrackwheelEvent(519, -1, 0, time);
         }
      }

      Field f = null;
      Verb verb = null;
      int k = MessageHotkeys.map(keycode);
      switch (k) {
         case 141:
            this.saveCurrentCursorPosition();
            f = this.getField(0);

            while (f instanceof Object) {
               Manager m = (Manager)f;
               f = m.getField(0);
            }

            if (f == this.getModelFieldWithFocus() && this._ebp != null) {
               this.setFocusToBody(true);
               return true;
            }

            this.getMainManager().setVerticalScroll(0);
            f.setFocus();
            return true;
         case 142:
            this.saveCurrentCursorPosition();
            boolean atBottomOfScreen = false;
            int fieldCount = this.getFieldCount() - 1;
            Manager m = this.getMainManager();

            for (f = this.getField(fieldCount); f instanceof Object; f = m.getField(fieldCount)) {
               m = (Manager)f;
               fieldCount = m.getFieldCount() - 1;
            }

            if (f instanceof LabelSeparatorField) {
               f = m.getField(fieldCount - 1);
            }

            if (f instanceof Object && f == this.getModelFieldWithFocus()) {
               RichTextField rtf = (RichTextField)f;
               if (rtf.getCursorPosition() == rtf.getTextLength()) {
                  atBottomOfScreen = true;
               }
            }

            if (atBottomOfScreen) {
               this.setFocusToBody(false);
            } else {
               this.scroll(2);
               if (f instanceof Object) {
                  RichTextField rtf = (RichTextField)f;
                  rtf.setCursorPosition(rtf.getTextLength());
               }
            }

            this.checkWhetherShouldAutoMore();
            return true;
         case 147:
            this.gotoLastPosition();
            return true;
         case 154:
            this._findVerbManager.invokeFind(false, super._context);
            return true;
         case 155:
            this._findVerbManager.invokeFind(true, super._context);
            return true;
         default:
            verb = HotKeys.getVerb(2, key);
            if (verb != null) {
               ContextObject clone = ContextObject.clone(super._context);
               clone.put(254, this._message);
               super._returnValue = verb.invoke(clone);
               if (ContextObject.getFlag(super._returnValue, 39)) {
                  UiApplication.getUiApplication().popScreen(this);
               }

               return true;
            } else {
               int hotk = MessageHotkeys.map(keycode);
               if (hotk != 0 && this._message instanceof Object) {
                  if (hotk == 153) {
                     ContextObject.put(super._context, -321822713458159100L, this._message);
                     ContextObject.setFlag(super._context, 100);
                  }

                  if (this.invokeHotkeyOnMessage(hotk)) {
                     return true;
                  }
               }

               HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
               if (hotkey != null) {
                  Object oldItemToWorkRelativeTo = ContextObject.get(super._context, -321822713458159100L);
                  if (k == 185 || k == 186) {
                     ContextObject.put(super._context, -321822713458159100L, this._message);
                  }

                  if (hotkey.invokeHotkey(k, super._context)) {
                     return true;
                  }

                  if (oldItemToWorkRelativeTo != null) {
                     ContextObject.put(super._context, -321822713458159100L, oldItemToWorkRelativeTo);
                  } else {
                     ContextObject.remove(super._context, -321822713458159100L);
                  }
               }

               return super.keyUp(keycode, time);
            }
      }
   }

   @Override
   public final long getModelType(Object context) {
      return -6822293833372928884L;
   }

   @Override
   public final RIMModel getOpenedModel(Object context) {
      return this._message;
   }

   protected final int getOrdinalForField(Field field, ReadableList list) {
      if (field != null) {
         Object cookie = field.getCookie();
         String attachmentName = null;
         int attachmentMorePartId = 0;
         if (cookie instanceof UnknownMimePartModel) {
            UnknownMimePartModel attachment = (UnknownMimePartModel)cookie;
            attachmentName = attachment.getFilename();
            attachmentMorePartId = attachment.getMorePartID();
         }

         for (int i = list.size() - 1; i >= 0; i--) {
            Object cookieToCheck = list.getAt(i);
            if (cookieToCheck instanceof ProxyModel && !(cookie instanceof ProxyModel)) {
               cookieToCheck = ((ProxyModel)cookieToCheck).getObject();
            }

            if (cookieToCheck == cookie) {
               return i;
            }

            if (attachmentName != null && cookieToCheck instanceof UnknownMimePartModel) {
               UnknownMimePartModel attachmentToCheck = (UnknownMimePartModel)cookieToCheck;
               if (attachmentMorePartId == attachmentToCheck.getMorePartID() && attachmentName.equals(attachmentToCheck.getFilename())) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   protected final Field findFieldForOrdinal(int ordinal, ReadableList list) {
      if (ordinal >= 0) {
         Object cookie = list.getAt(ordinal);
         if (cookie instanceof ProxyModel) {
            cookie = ((ProxyModel)cookie).getObject();
         }

         return this.findFieldByCookie(cookie);
      } else {
         return null;
      }
   }

   private final void doInvokeLaterForNotifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
      Runnable runnable = (Runnable)(new Object(this, oldModel, newModel, moreContext));
      if (oldModel == newModel && moreContext == null) {
         synchronized (this._invokeLaterRunnable) {
            this._invokeLaterRunnable.setRunnable(runnable);
            if (this._invokeLaterRunnable.doneProcessing()) {
               this._invokeLaterRunnable.resetDoneProcessing();
               super._application.invokeLater(this._invokeLaterRunnable);
            } else {
               this._invokeLaterRunnable.resetDoneProcessing();
            }
         }
      } else {
         super._application.invokeLater(runnable);
      }
   }

   @Override
   public final void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
      if (this._message == oldModel && newModel instanceof EmailMessageModelImpl) {
         boolean invokeLater = true;

         label224:
         try {
            invokeLater = super._application != Application.getApplication() || !Application.isEventDispatchThread();
         } finally {
            break label224;
         }

         if (invokeLater) {
            this.doInvokeLaterForNotifyOfOpenedModelChange(oldModel, newModel, moreContext);
            return;
         }

         ContextObject moreContextPrivateFlags = ContextObject.castOrCreate(moreContext);
         this._message = (EmailMessageModelImpl)newModel;
         ReadableList oldPayload = (ReadableList)ContextObject.get(moreContext, -967294354293097868L);
         if (oldPayload == null) {
            oldPayload = ((EmailMessageModel)oldModel).getPayload();
         }

         if (ContextObject.getFlag(moreContext, 98)) {
            EventLogger.logEvent(-1237457833540244999L, 1631801944, 0);
            this._autoMoreAlreadyInvoked = false;
            String statusStringToUse = EmailResources.getString(90);
            int transmissionError = this._message.getTransmissionError();
            if (transmissionError == 49 || transmissionError == 80 || transmissionError == 81) {
               EventLogger.logEvent(-1237457833540244999L, 1631800914, transmissionError, 16, 3);
               statusStringToUse = EmailResources.getString(1003);
            }

            if (!this._message.flagsSet(65536)) {
               PopupStatus.show(statusStringToUse, 1000);
            }

            if (moreContextPrivateFlags.getPrivateFlag(-4104667787783617270L, 0) && this.isFocusAtOrBeyondEndOfMessage(false)) {
               this.setFocusToBody(false);
            }
         }

         Field fieldWithFocus = this.getModelFieldWithFocus();
         Object cookie = null;
         if (fieldWithFocus != null) {
            cookie = fieldWithFocus.getCookie();
         }

         int oldOffset = 0;
         if (!(cookie instanceof Object)) {
            if (!(fieldWithFocus instanceof Object)) {
               if (fieldWithFocus instanceof Object) {
                  oldOffset = ((RichTextField)fieldWithFocus).getCursorPosition();
               }
            } else {
               oldOffset = ((BasicEditField)fieldWithFocus).getCursorPosition();
            }
         } else {
            EmailBodyProvider cursorProvider = (EmailBodyProvider)cookie;
            oldOffset = cursorProvider.getCurrentCursorPosition(fieldWithFocus, moreContext);
         }

         if (oldOffset < 0) {
            oldOffset = 0;
         }

         int oldVerticalScroll = this.getMainManager().getVerticalScroll();
         int verticalHeight = this.getMainManager().getVirtualHeight();
         boolean oldFlagValue = ContextObject.getFlag(super._context, 98);
         if (ContextObject.getFlag(moreContext, 98)) {
            ContextObject.setFlag(super._context, 98);
         }

         if (ContextObject.getFlag(moreContext, 137)) {
            ContextObject.setFlag(super._context, 137);
         }

         if (moreContextPrivateFlags.getPrivateFlag(-4104667787783617270L, 0)) {
            ContextObject.setPrivateFlag(super._context, -4104667787783617270L, 0);
         }

         this.setModel(this._message);
         if (!oldFlagValue) {
            ContextObject.clearFlag(super._context, 98);
         }

         ContextObject.clearFlag(super._context, 137);
         if (moreContextPrivateFlags.getPrivateFlag(-4104667787783617270L, 0)) {
            ContextObject.clearPrivateFlag(super._context, -4104667787783617270L, 0);
         }

         Field fieldToHaveFocus = this.findFieldForOrdinal(this.getOrdinalForField(fieldWithFocus, oldPayload), this._message);
         if (fieldToHaveFocus == null && cookie instanceof Object) {
            fieldToHaveFocus = this.findFieldByCookie(this._ebp);
         }

         if (fieldToHaveFocus == null) {
            fieldToHaveFocus = this.getField(this.getFieldWithFocusIndex());
         }

         cookie = fieldToHaveFocus.getCookie();
         if (!(cookie instanceof Object)) {
            if (!(fieldToHaveFocus instanceof Object)) {
               if (fieldToHaveFocus instanceof Object) {
                  RichTextField rtf = (RichTextField)fieldToHaveFocus;
                  int maxOffset = rtf.getTextLength();
                  if (oldOffset > maxOffset) {
                     oldOffset = maxOffset;
                  }

                  if (this._ebp != null) {
                     Field ebpField = this.findFieldByCookie(this._ebp);
                     int cursorPosition = this._ebp.getCursorCount(ebpField, super._context) - 1;
                     this._ebp.setCursorPosition(ebpField, cursorPosition, super._context);
                  }

                  rtf.setCursorPosition(oldOffset);
                  oldVerticalScroll += this.getMainManager().getVirtualHeight() - verticalHeight;
               }
            } else {
               EditField ef = (EditField)fieldToHaveFocus;
               int maxOffset = ef.getTextLength();
               if (oldOffset > maxOffset) {
                  oldOffset = maxOffset;
               }

               ef.setCursorPosition(oldOffset);
            }
         } else {
            EmailBodyProvider ebp = (EmailBodyProvider)cookie;
            int maxOffset = ebp.getCursorCount(fieldToHaveFocus, moreContext);
            if (oldOffset > maxOffset) {
               oldOffset = maxOffset;
            }

            ebp.setCursorPosition(fieldToHaveFocus, oldOffset, moreContext);
         }

         if (!(cookie instanceof Object)) {
            fieldToHaveFocus.setFocus();
         } else {
            EmailBodyProvider emailBodyProvider = (EmailBodyProvider)cookie;
            emailBodyProvider.setFocus(fieldToHaveFocus, super._application, super._context);
         }

         if (oldVerticalScroll >= 0 && oldVerticalScroll <= this.getMainManager().getVirtualHeight()) {
            this.getMainManager().setVerticalScroll(oldVerticalScroll);
         }
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         ModelViewListenerRegistry.notifyModelOpened(this, super._context);
         if (this._transitoryEmailMessageModel != null) {
            this._transitoryEmailMessageModel.updateStatus(this._message);
         }

         super._application.addHolsterListener(this);
         super._application.addSystemListener(this);
         super.onUiEngineAttached(attached);
         this.setFocusToBody(true);
      } else {
         ModelViewListenerRegistry.notifyModelClosed(this, super._context);
         this.saveCurrentCursorPosition();
         this._message = null;
         this._transitoryEmailMessageModel.setModel(null);
         super._application.removeHolsterListener(this);
         super._application.removeSystemListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   protected final void saveCurrentCursorPosition() {
      if (this._ebp != null && this._message != null) {
         this._message.setCursorPosition(this._ebp.getCurrentCursorPosition(this.findFieldByCookie(this._ebp), super._context));
      }
   }

   @Override
   protected final void organizeFields(Field[] fields, int[] orders) {
      boolean isOutboundMessage = !this._message.inbound();
      int bodyIndex = Arrays.binarySearch(orders, 5700);
      if (bodyIndex < 0) {
         bodyIndex = -bodyIndex - 2;
         if (bodyIndex < 0) {
            return;
         }
      }

      VerticalFieldManager statusHeaders = (VerticalFieldManager)(new Object());
      EmailViewerScreen$RightJustifiedHorizontalFieldManager subjectHeaders = new EmailViewerScreen$RightJustifiedHorizontalFieldManager();
      VerticalFieldManager body = (VerticalFieldManager)(new Object());
      Field initialRecipient = null;
      statusHeaders.setTag(ThemeUtilities.EMAIL_HEADER_AREA_TAG);
      subjectHeaders.setTag(ThemeUtilities.EMAIL_SUBJECT_AREA_TAG);
      body.setTag(ThemeUtilities.EMAIL_BODY_AREA_TAG);

      for (int i = 0; i <= bodyIndex; i++) {
         if (orders[i] > 2750) {
            body.add(fields[i]);
         } else {
            switch (orders[i]) {
               case 2230:
               case 2240:
               case 2250:
                  fields[i].setTag(ThemeUtilities.EMAIL_HEADER_TAG);
                  if (isOutboundMessage) {
                     subjectHeaders.add(fields[i]);
                  } else {
                     statusHeaders.add(fields[i]);
                     if (initialRecipient == null) {
                        initialRecipient = fields[i];
                     }
                  }
                  break;
               case 2500:
                  fields[i].setTag(ThemeUtilities.EMAIL_HEADER_TAG);
                  if (subjectHeaders.getRightJustifiedField() == null) {
                     Object cookie = fields[i].getCookie();
                     if (cookie instanceof Object) {
                        DisplayPictureModel pictureModel = getDisplayPictureModelFromAddress((AddressReference)cookie);
                        if (pictureModel != null) {
                           Field pictureField = createFieldForDisplayPictureModel(pictureModel);
                           if (pictureField != null) {
                              subjectHeaders.setRightJustifiedField(pictureField);
                           }
                        }
                     }
                  }

                  subjectHeaders.add(fields[i]);
                  break;
               case 2600:
                  fields[i].setTag(ThemeUtilities.EMAIL_SUBJECT_TAG);
                  subjectHeaders.add(fields[i]);
                  break;
               case 2700:
                  fields[i].setTag(ThemeUtilities.EMAIL_TIMESTAMP_TAG);
                  subjectHeaders.getFooterManager().add(fields[i]);
                  break;
               case 2750:
                  fields[i].setTag(ThemeUtilities.EMAIL_ATTACHMENT_STATUS_TAG);
                  subjectHeaders.getFooterManager().add(fields[i]);
                  break;
               default:
                  fields[i].setTag(ThemeUtilities.EMAIL_HEADER_TAG);
                  statusHeaders.add(fields[i]);
            }
         }
      }

      if (!isOutboundMessage && initialRecipient != null) {
         initialRecipient.setTag(ThemeUtilities.EMAIL_HEADER_FIRST_RECIPIENT_TAG);
      }

      fields[0] = statusHeaders;
      fields[1] = subjectHeaders;
      fields[2] = body;
      orders[0] = 2230;
      orders[1] = 2750;
      orders[2] = 5500;
      int numRemainingFields = fields.length - bodyIndex;
      System.arraycopy(fields, bodyIndex + 1, fields, 3, numRemainingFields - 1);
      Array.resize(fields, 3 + numRemainingFields - 1);
      System.arraycopy(orders, bodyIndex + 1, orders, 3, numRemainingFields - 1);
      Array.resize(orders, 3 + numRemainingFields - 1);
      int attachmentIndex = -1;
      int i = fields.length - 1;

      while (i >= 0 && orders[i] == 6500) {
         attachmentIndex = i--;
      }

      if (attachmentIndex >= 0) {
         VerticalFieldManager attachments = (VerticalFieldManager)(new Object());
         attachments.setTag(ThemeUtilities.EMAIL_ATTACHMENT_AREA_TAG);

         for (int ix = attachmentIndex; ix < fields.length; ix++) {
            fields[ix].setTag(ThemeUtilities.EMAIL_HEADER_TAG);
            attachments.add(fields[ix]);
         }

         fields[attachmentIndex] = attachments;
         orders[attachmentIndex] = 6500;
         numRemainingFields = attachmentIndex + 1;
         Array.resize(fields, numRemainingFields);
         Array.resize(orders, numRemainingFields);
      }
   }

   private final boolean isFocusAtOrBeyondEndOfMessage(boolean considerDefaultVerb) {
      if (this._ebp != null) {
         Field fieldWithFocus = this.getModelFieldWithFocus();
         Field ebpField = this.findFieldByCookie(this._ebp);
         if (fieldWithFocus == ebpField) {
            return this._ebp.getCurrentCursorPosition(ebpField, super._context) + 30 >= this._ebp.getCursorCount(ebpField, super._context);
         } else {
            return considerDefaultVerb && !(fieldWithFocus instanceof LabelSeparatorField) && super._defaultVerb != null
               ? false
               : this.getFieldIndex(fieldWithFocus) > this.getFieldIndex(ebpField);
         }
      } else {
         return false;
      }
   }

   private final void setFocusToBody(boolean top) {
      if (this._ebp != null) {
         Field ebpField = this.findFieldByCookie(this._ebp);
         int cursorPosition;
         if (top) {
            cursorPosition = 0;
         } else {
            cursorPosition = this._ebp.getCursorCount(ebpField, super._context) - 1;
         }

         this._ebp.setCursorPosition(ebpField, cursorPosition, super._context);
         this._ebp.setFocus(ebpField, super._application, super._context);
      }
   }

   private final void setMoreMessageStatus(boolean markOpened) {
      if (markOpened && !this._message.flagsSet(1) && !ApplicationManager.getApplicationManager().isSystemLocked()) {
         this._message.changeStatus(1, 0, 0, 0, true, true, false, false, super._context);
      }

      boolean deleteMoreLabel = true;
      if (this._ebp instanceof MorePartModel) {
         MorePartModel morePartModel = (MorePartModel)this._ebp;
         int availableLength = morePartModel.getAvailableLength();
         int lengthOnDevice = morePartModel.getLengthOnDevice();
         int trueLength = morePartModel.getTrueLength();
         int amountRemainingOnServer = availableLength - lengthOnDevice;
         if (amountRemainingOnServer > 0) {
            String status = ((StringBuffer)(new Object()))
               .append(EmailResources.getString(82))
               .append(Integer.toString(amountRemainingOnServer))
               .append(EmailResources.getString(92))
               .toString();
            this._moreLabel.setText(status);
            deleteMoreLabel = false;
         } else {
            int amountTruncated = trueLength - availableLength;
            if (amountTruncated > 0) {
               String status = ((StringBuffer)(new Object()))
                  .append(EmailResources.getString(148))
                  .append(Integer.toString(amountTruncated))
                  .append(EmailResources.getString(92))
                  .toString();
               this._moreLabel.setText(status);
               deleteMoreLabel = false;
            }
         }
      }

      if (deleteMoreLabel) {
         try {
            this.delete(this._moreLabel);
         } finally {
            return;
         }
      }
   }

   private final boolean locateEmailBodyProvider(Manager parentManager) {
      this._ebp = null;

      for (int i = parentManager.getFieldCount() - 1; i >= 0; i--) {
         Field f = parentManager.getField(i);
         Object o = f.getCookie();
         if (o instanceof Object) {
            this._ebp = (EmailBodyProvider)o;
            int cp = this._message.getCursorPosition();
            int bodyLen = this._ebp.getCursorCount(f, null);
            if (cp < 0 || bodyLen > 0 && cp > bodyLen) {
               this._message.setCursorPosition(0);
            }
            break;
         }

         if (o == null && f instanceof Object && this.locateEmailBodyProvider((Manager)f)) {
            break;
         }
      }

      return this._ebp != null;
   }

   private final Field getEmailHeaderFieldForCommunicationVerbs() {
      return this.getEmailHeaderFieldForCommunicationVerbs(this.getMainManager());
   }

   private final Field getEmailHeaderFieldForCommunicationVerbs(Manager manager) {
      Field emailHeaderField = null;
      boolean incoming = this._message.inbound();

      for (int i = 0; i < manager.getFieldCount(); i++) {
         Field f = manager.getField(i);
         if (f instanceof Object) {
            f = this.getEmailHeaderFieldForCommunicationVerbs((Manager)f);
            if (f == null) {
               continue;
            }
         }

         Object cookie = f.getCookie();
         if (cookie instanceof EmailHeaderModel) {
            switch (((EmailHeaderModel)cookie).getHeaderType()) {
               case -1:
                  break;
               case 0:
               case 1:
               case 2:
               default:
                  if (incoming) {
                     continue;
                  }
                  break;
               case 3:
               case 4:
                  if (!incoming) {
                     continue;
                  }
            }

            if (emailHeaderField != null) {
               return null;
            }

            emailHeaderField = f;
         }
      }

      return emailHeaderField;
   }

   public final int getReferenceIdentifier() {
      return this._message.getCMIMEReferenceIdentifier();
   }

   private final synchronized void closeMessage() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof Object) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   @Override
   public final void inHolster() {
      this.closeMessage();
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOff() {
      this.closeMessage();
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final boolean isAddressTrusted(String address, Object context) {
      if (this._trustedAddressCheckingEnabled && address != null && address.length() > 0) {
         ServiceRecord sr = this._message.getServiceRecordForMessage();
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

   private static final DisplayPictureModel getDisplayPictureModelFromAddress(AddressReference address) {
      if (address != null) {
         RIMModel addressModel = address.getInsideModel();
         if (addressModel instanceof Object) {
            Object addressCard = AddressBookServices.reverseLookup(addressModel);
            if (addressCard instanceof Object) {
               return ((AddressCardModel)addressCard).getContactPicture(null);
            }
         }
      }

      return null;
   }

   private static final Field createFieldForDisplayPictureModel(DisplayPictureModel pictureModel) {
      BitmapField field = null;
      if (pictureModel != null) {
         int scale;
         if (InternalServices.getFormFactor() == 13) {
            scale = Fixed32.toFP(2);
         } else {
            scale = Fixed32.tenThouToFP(14285);
         }

         byte[] imageData = pictureModel.getDisplayPicture();
         if (imageData != null) {
            field = (BitmapField)(new Object(null, 36028797018963968L));
            field.setSpace(1, 1);
            EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
            image = image.scaleImage32(scale, scale);
            field.setImage(image);
         }
      }

      return field;
   }

   private final Field findEmailBodyField(Object cookie) {
      Field ebpField = this.findFieldByCookie(cookie);
      if (ebpField instanceof Object) {
         Manager bodyFieldManager = (Manager)ebpField;
         int numFields = bodyFieldManager.getFieldCount();

         for (int i = 0; i < numFields; i++) {
            Field childField = bodyFieldManager.getField(i);
            if (childField.getCookie() == cookie) {
               return childField;
            }
         }
      }

      return ebpField;
   }
}
