package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.ui.ViewFolderVerb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.FindVerbManager;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.framework.ModelScreen$NotificationRunnable;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.apps.internal.sms.SMSChangeStatusVerb;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.apps.internal.sms.ui.StatusIconField;
import net.rim.device.apps.internal.sms.ui.StatusTextField;

public final class SMSViewerScreen extends ModelScreen implements HolsterListener, SystemListener {
   private SMSChangeStatusVerb _smsMarkUnopenedVerb;
   private SMSChangeStatusVerb _smsMarkOpenedVerb;
   private SMSChangeStatusVerb _smsSaveVerb;
   private SMSForwardVerb _smsForwardVerb;
   private ViewFolderVerb _viewFolderVerb;
   private FindVerbManager _findVerbManager = new FindVerbManager(this.getDelegate());
   private Manager _historyManager;
   private Field[] _addressFields;
   private SMSMessageModel _model;

   public SMSViewerScreen(Object context) {
      super(0, null, ContextObject.clone(context));
      ContextObject.setFlag(super._context, 15);
      ContextObject.setFlag(super._context, 96);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         ModelViewListenerRegistry.notifyModelOpened(this, super._context);
         super._application.addHolsterListener(this);
         super._application.addSystemListener(this);
      } else {
         ModelViewListenerRegistry.notifyModelClosed(this, super._context);
         this._model = null;
         super._application.removeHolsterListener(this);
         super._application.removeSystemListener(this);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   public final void setModel(Object model) {
      this._model = (SMSMessageModel)model;
      super.setModel(model);
      this.deleteAll();
      this.populateScreen();
      this.setSecurityServiceColours(false);
   }

   protected final void populateScreen() {
      PersistableRIMModel[] addresses = this._model._payload.getAddresses();
      FieldProvider fieldProvider = null;
      Field field = null;
      Field addressField = null;
      ContextObject addressContextObject = ContextObject.clone(super._context);
      addressContextObject.setFlag(9);
      addressContextObject.setFlag(1);
      addressContextObject.clearFlag(0);
      int loopEnd = addresses.length;
      VerticalFieldManager vfm = new VerticalFieldManager();

      for (int i = 0; i < loopEnd; i++) {
         PersistableRIMModel address = addresses[i];
         fieldProvider = (FieldProvider)address;
         addressField = fieldProvider.getField(ContextObject.clone(addressContextObject));
         if (addressField != null) {
            if (this._addressFields == null) {
               this._addressFields = new Field[0];
            }

            Arrays.add(this._addressFields, addressField);
            FlowFieldManager statusFfm = new FlowFieldManager();
            statusFfm.add(new StatusIconField(this._model, i));
            statusFfm.add(addressField);
            statusFfm.add(new StatusTextField(this._model, i));
            vfm.add(statusFfm);
         }
      }

      this.add(vfm);
      this.add(new SeparatorField());
      ContextObject contextObject = ContextObject.clone(super._context);
      field = this._model.getField(contextObject);
      if (field != null) {
         this.add(field);
         field.setFocus();
      }

      contextObject.setFlag(54);
      this._historyManager = new VerticalFieldManager();
      includeMessageThread(this._model, this._historyManager, contextObject, this._model._payload._creationDate, false);
      if (this._historyManager.getFieldCount() == 0) {
         this._historyManager = null;
      } else {
         this.add(this._historyManager);
      }
   }

   private final void initializeCachedVerbs() {
      if (this._smsMarkUnopenedVerb == null) {
         this._smsMarkUnopenedVerb = new SMSChangeStatusVerb(602450, 16, 0, 1, 0, null, null);
         this._smsMarkOpenedVerb = new SMSChangeStatusVerb(602448, 14, 1, 0, 0, null, null);
         this._smsSaveVerb = new SMSChangeStatusVerb(602480, 18, 16, 0, 0, null, null);
         this._smsForwardVerb = new SMSForwardVerb();
         this._viewFolderVerb = ViewFolderVerb.getInstance();
      }
   }

   private final boolean isAddressFieldFocused(Field fieldWithFocus) {
      Field currentField = fieldWithFocus;
      if (this._addressFields != null) {
         while (currentField != null) {
            for (int i = this._addressFields.length - 1; i > -1; i--) {
               if (this._addressFields[i] == currentField) {
                  return true;
               }
            }

            if (!(currentField instanceof Manager)) {
               return false;
            }

            currentField = ((Manager)currentField).getFieldWithFocus();
         }
      }

      return false;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      this.initializeCachedVerbs();
      Verb defaultVerb = null;
      boolean enableSMS = SMSPacketHeader.isSendSupported();
      if (this._model.inbound()) {
         if (enableSMS && !this._model.isAddressEmpty(super._context)) {
            defaultVerb = new SMSReplyVerb(this._model);
            menu.add(defaultVerb);
         }

         if (instance != 65536) {
            if (this._model.flagsSet(1)) {
               this._smsMarkUnopenedVerb.setParameters(this._model);
               menu.add(this._smsMarkUnopenedVerb);
            } else {
               this._smsMarkOpenedVerb.setParameters(this._model);
               menu.add(this._smsMarkOpenedVerb);
            }
         }
      } else if (enableSMS && SMSService.canSend()) {
         boolean successfullySent = this._model.isSuccessfullySent();
         if (instance == 0 || !successfullySent) {
            Verb resendVerb = new SMSResendVerb(this._model);
            menu.add(resendVerb);
            if (!successfullySent) {
               defaultVerb = resendVerb;
            }
         }
      }

      if (enableSMS) {
         this._smsForwardVerb.setParameter(this._model);
         menu.add(this._smsForwardVerb);
         if (!this._model.inbound() && defaultVerb == null) {
            defaultVerb = this._smsForwardVerb;
         }

         if (instance != 65536) {
            ForwardAsVerb forwardAsVerb = new ForwardAsVerb(this._model);
            if (forwardAsVerb.canInvoke(null)) {
               menu.add(forwardAsVerb);
            }
         }
      }

      if (instance == 65536) {
         DeleteSingleItemVerb deleteSingleItemVerb = new DeleteSingleItemVerb(611472, 1000);
         deleteSingleItemVerb.setParameters(this._model, ContextObject.castOrCreate(super._context));
         menu.add(deleteSingleItemVerb);
      } else {
         if (!this._model.flagsSet(16)) {
            this._smsSaveVerb.setParameters(this._model);
            menu.add(this._smsSaveVerb);
         }

         Folder folder = FolderHierarchies.getFolder(this._model.getFolderId());
         if (folder != null && this._viewFolderVerb != null) {
            this._viewFolderVerb.setDefaultFolder(folder);
            menu.add(this._viewFolderVerb);
         }

         menu.add(this._findVerbManager.getVerbs());
         VerbFactory outerVerbFactory = (VerbFactory)((ContextObject)super._context).get(-2846768035584909703L);
         if (outerVerbFactory != null) {
            menu.add(outerVerbFactory.getVerbs(super._context));
         }

         VerbRepository verbRepository = VerbRepository.getVerbRepository(3433073725342984424L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         verbRepository = VerbRepository.getVerbRepository(9096799525298506811L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         if (this._historyManager != null) {
            menu.add(new SMSViewerScreen$RemoveHistoryVerb(this));
         }
      }

      if (defaultVerb != null) {
         menu.setDefault(defaultVerb);
      }

      menu.coalesce(-3072555018635390988L, null);
   }

   private final boolean isOnHyperlink() {
      Field focusedField = this.getFieldWithFocus();
      if (focusedField instanceof CookieProvider) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)focusedField).getCookieWithFocus());
         if (cookie instanceof RIMModel) {
            return true;
         }
      }

      focusedField = this.getLeafFieldWithFocus();
      if (focusedField instanceof CookieProvider) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)focusedField).getCookieWithFocus());
         if (cookie instanceof RIMModel) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char keyPress = Keypad.map(keycode);
      return keyPress != 17 && keyPress != '\n' ? super.keyDown(keycode, time) : true;
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      char keyPress = Keypad.map(keycode);
      int status = Keypad.status(keycode);
      if (super.keyDown(keycode, time)) {
         return true;
      }

      if (keyPress != 127 && Keypad.getAltedChar(keyPress) != 127) {
         boolean enableSMS = SMSPacketHeader.isSendSupported();
         int k = MessageHotkeys.map(keycode);
         switch (k) {
            case 141:
               Field f = this.getField(0);
               f.setFocus();
               return true;
            case 142:
               this.scroll(2);
               return true;
            case 148:
               if (this._model.inbound() && enableSMS) {
                  Verb replyVerb = new SMSReplyVerb(this._model);
                  Object returnValue = replyVerb.invoke(super._context);
                  if (ContextObject.getFlag(returnValue, 39)) {
                     UiApplication.getUiApplication().popScreen(this);
                  }

                  return true;
               }
               break;
            case 150:
               if (enableSMS) {
                  SMSForwardVerb forwardVerb = new SMSForwardVerb();
                  forwardVerb.setParameter(this._model);
                  Object returnValue = forwardVerb.invoke(super._context);
                  if (ContextObject.getFlag(returnValue, 39)) {
                     UiApplication.getUiApplication().popScreen(this);
                  }

                  return true;
               }
               break;
            case 154:
               this._findVerbManager.invokeFind(false, super._context);
               return true;
            case 155:
               this._findVerbManager.invokeFind(true, super._context);
               return true;
         }

         HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
         if (hotkey != null && hotkey.invokeHotkey(k, super._context)) {
            return true;
         }

         switch (Keypad.map(keycode)) {
            case ' ':
               int direction = (status & 2) == 0 ? 512 : 256;
               this.scroll(direction);
               return true;
            default:
               return false;
         }
      } else {
         DeleteSingleItemVerb deleteVerb = new DeleteSingleItemVerb(611472, 1000);
         deleteVerb.setParameters(this._model, super._context);
         super._returnValue = deleteVerb.invoke(null);
         if (ContextObject.getFlag(super._returnValue, 39)) {
            UiApplication.getUiApplication().popScreen(this);
         }

         return true;
      }
   }

   static final void includeMessageThread(SMSModel message, Manager fieldManager, Object context, long fromTime, boolean includeMessageAtFromTime) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private final void removeHistory() {
      if (this._historyManager != null) {
         this.delete(this._historyManager);
         this._historyManager = null;
         this._model.setFlags(32);
      }
   }

   private final void closeMessage() {
      UiApplication.getUiApplication().popScreen(this);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (object0 == this._model) {
         if (guid == 1202366544244619460L) {
            this.closeMessage();
         } else if (guid == -6275418955626563374L) {
            int curFlags = this._model.getFlags();
            boolean deviceLocked = ApplicationManager.getApplicationManager().isSystemLocked();
            if ((curFlags & 1) == 0 && !deviceLocked) {
               this._model.perform(5803508244060051872L, null);
               this.invalidate();
            }

            if (!deviceLocked || !PersistentContent.isEncryptionEnabled()) {
               UiApplication.getUiApplication().requestForeground();
            }

            return;
         }
      } else if (guid == -7131874474196788121L || guid == 7884295420352689779L) {
         this.closeMessage();
      } else if (guid == 6345609069135580235L) {
         if (PersistentContent.isEncryptionEnabled()) {
            this.setModel(this._model);
         }

         this.invalidate();
         UiApplication.getUiApplication().requestForeground();
         this._model.changeStatus(1, 0, 0, 0, true, false, true, null);
      }

      super.eventOccurred(guid, data0, data1, object0, object1);
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
   public final void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
      if (this._model == oldModel && newModel instanceof SMSMessageModel) {
         if (super._application != Application.getApplication() || !Application.isEventDispatchThread()) {
            super._application.invokeLater(new ModelScreen$NotificationRunnable(this, oldModel, newModel, moreContext));
            return;
         }

         if (moreContext instanceof ContextObject && ((ContextObject)moreContext).getFlag(27)) {
            this.invalidate();
            return;
         }

         PopupStatus.show(SMSResources.getString(387), 1000);
         this.setModel(newModel);
      }
   }
}
