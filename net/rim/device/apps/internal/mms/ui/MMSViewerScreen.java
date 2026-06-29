package net.rim.device.apps.internal.mms.ui;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.ui.ViewFolderVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.DRMConverter;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.mms.verbs.MMSSaveMediaVerb;
import net.rim.vm.Array;

public final class MMSViewerScreen extends MMSModelScreen {
   private MMSViewField _viewField;
   private String[] _videos;
   private String[] _ringtones;
   private String[] _voicenotes;
   private ViewFolderVerb _viewFolderVerb;

   public MMSViewerScreen(Object context) {
      super(context);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   @Override
   public final void setModel(Object message) {
      label17:
      try {
         this._videos = null;
         this._ringtones = null;
         this._voicenotes = null;
         this.getMediaNames(((MMSMessageModel)message).getAttachmentDataProvider());
      } finally {
         break label17;
      }

      super.setModel(message);
   }

   @Override
   protected final void populateScreen() {
      ContextObject context = ContextObject.clone(super._context);
      context.setFlag(74);
      context.clearFlag(0);
      context.setFlag(9);
      this._viewField = new MMSViewField((MMSMessageModel)this.getMessageModel(), context);
      this.add(this._viewField);
      this._viewField.getInitialFocusField().setFocus();
   }

   private final void getMediaNames(AttachmentDataProvider attachmentProvider) {
      Enumeration names = attachmentProvider.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            MMSAttachment attachment = attachmentProvider.getAttachment(name);
            if (attachment != null) {
               while (attachment.getType() == 72) {
                  attachment = DRMConverter.unwrap(attachment);
                  if (attachment == null) {
                     System.out.println("MMS - DRM parse failure.");
                     return;
                  }
               }

               if (attachment.getType() == 62) {
                  byte[] data = attachment.getData();
                  MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(data);
                  this.getMediaNames(pdu);
               } else if (MMSUtilities.isVideoType(attachment.getType())) {
                  if (this._videos == null) {
                     this._videos = new String[]{attachment.getName()};
                  } else {
                     int index = this._videos.length;
                     Array.resize(this._videos, index + 1);
                     this._videos[index] = attachment.getName();
                  }
               } else if (MMSUtilities.isRingtoneAudioType(attachment.getType())) {
                  if (this._ringtones == null) {
                     this._ringtones = new String[]{attachment.getName()};
                  } else {
                     int index = this._ringtones.length;
                     Array.resize(this._ringtones, index + 1);
                     this._ringtones[index] = attachment.getName();
                  }
               } else if (MMSUtilities.isVoiceNoteAudioType(attachment.getType())) {
                  if (this._voicenotes == null) {
                     this._voicenotes = new String[]{attachment.getName()};
                  } else {
                     int index = this._voicenotes.length;
                     Array.resize(this._voicenotes, index + 1);
                     this._voicenotes[index] = attachment.getName();
                  }
               }
            }
         }
      }
   }

   private final void initializeCachedVerbs() {
      if (this._viewFolderVerb == null) {
         this._viewFolderVerb = ViewFolderVerb.getInstance();
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      this.initializeCachedVerbs();
      MMSMessageModel message = (MMSMessageModel)this.getMessageModel();
      MMSPayloadModel payload = message.getPayload();
      Verb defaultVerb = null;
      ContextObject ctx = new ContextObject(44, 37);
      if (instance == 65536) {
         ContextObject.setFlag(ctx, 81);
         DeleteSingleItemVerb deleteVerb = new DeleteSingleItemVerb(611472, 1000);
         deleteVerb.setParameters(message, ctx);
         menu.add(deleteVerb);
      }

      if (instance == 0 || instance == 65536) {
         super.makeMenu(menu, instance);
         VerbRepository verbRepository = VerbRepository.getVerbRepository(3433073725342984424L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         verbRepository = VerbRepository.getVerbRepository(5529224403653746205L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         Verb[] verbs = new Verb[0];
         defaultVerb = message.getVerbs(ctx, verbs);
         menu.add(verbs);
         menu.setDefault(defaultVerb);
      }

      if (instance == 0) {
         VerbFactory outerVerbFactory = (VerbFactory)((ContextObject)super._context).get(-2846768035584909703L);
         if (outerVerbFactory != null) {
            menu.add(outerVerbFactory.getVerbs(super._context));
         }

         Folder folder = FolderHierarchies.getFolder(message.getFolderId());
         if (folder != null && this._viewFolderVerb != null) {
            this._viewFolderVerb.setDefaultFolder(folder);
            menu.add(this._viewFolderVerb);
         }

         if (this._videos != null) {
            Verb verb = new MMSSaveMediaVerb(
               this._videos, message.getAttachmentDataProvider(), message.isForwardLocked(), MMSResources.getString(127), MMSResources.getString(126)
            );
            VerbMenuItem verbMenuItem = new VerbMenuItem(null, verb.getOrdering(), 500, verb, null);
            menu.add(verbMenuItem);
         }

         if (this._ringtones != null) {
            Verb verb = new MMSSaveMediaVerb(
               this._ringtones, message.getAttachmentDataProvider(), message.isForwardLocked(), MMSResources.getString(63), MMSResources.getString(64)
            );
            VerbMenuItem verbMenuItem = new VerbMenuItem(null, verb.getOrdering(), 500, verb, null);
            menu.add(verbMenuItem);
         }

         if (this._voicenotes != null) {
            Verb verb = new MMSSaveMediaVerb(
               this._voicenotes, message.getAttachmentDataProvider(), message.isForwardLocked(), MMSResources.getString(125), MMSResources.getString(124)
            );
            VerbMenuItem verbMenuItem = new VerbMenuItem(null, verb.getOrdering(), 500, verb, null);
            menu.add(verbMenuItem);
         }

         if (!this.isOnHyperlink() && defaultVerb != null) {
            MenuItem defaultItem = menu.getDefault();
            if (defaultItem == null || defaultItem.getPriority() > 100) {
               menu.setDefault(defaultVerb);
            }
         }

         if (!this.isOnHyperlink() && !this.isFieldInFocus(this._viewField.getAddressField())) {
            Verb[] contactVerbs = new Verb[0];
            RIMModel composeAddress = payload.getSender();
            if (composeAddress instanceof VerbProvider) {
               if (message.isSmartDialed()) {
                  ctx.setFlag(117);
               }

               ctx.clearFlag(44);
               ((VerbProvider)composeAddress).getVerbs(ctx, contactVerbs);
               menu.add(contactVerbs);
            }
         }
      }

      menu.coalesce(-3072555018635390988L, null);
   }

   @Override
   protected final boolean onSavePrompt() {
      return true;
   }

   private final boolean isFieldInFocus(Field fieldToCheck) {
      for (Field field = this.getFieldWithFocus(); field != null; field = ((Manager)field).getFieldWithFocus()) {
         if (field == fieldToCheck) {
            return true;
         }

         if (!(field instanceof Manager)) {
            break;
         }
      }

      return false;
   }

   @Override
   protected final boolean keyChar(char ch, int status, int time) {
      if (Keypad.getAltedChar(ch) == 127) {
         ch = 127;
      }

      switch (ch) {
         case ' ':
            int direction = (status & 2) == 0 ? 512 : 256;
            this.scroll(direction);
            return true;
         case '\u007f':
            DeleteSingleItemVerb deleteVerb = new DeleteSingleItemVerb(611472, 1000);
            deleteVerb.setParameters(this.getMessageModel(), super._context);
            super._returnValue = deleteVerb.invoke(null);
            if (ContextObject.getFlag(super._returnValue, 39)) {
               UiApplication.getUiApplication().popScreen(this);
            }

            return true;
         default:
            return super.keyChar(ch, status, time);
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      return super.keyUp(keycode, time) ? true : this.handleHotkey(MessageHotkeys.map(keycode));
   }

   private final boolean handleHotkey(int hotkey) {
      switch (hotkey) {
         case 141:
            this.getField(0).setFocus();
            return true;
         case 142:
            this.scroll(2);
            return true;
         case 148:
         case 150:
            ((HotKeyProvider)this.getMessageModel()).invokeHotkey(super._context, hotkey);
            return true;
         default:
            HotKeyCheck hotkeyChecker = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
            return hotkeyChecker != null && hotkeyChecker.invokeHotkey(hotkey, super._context);
      }
   }

   @Override
   public final void inHolster() {
      this.closeMessage();
   }

   @Override
   public final void powerOff() {
      this.closeMessage();
   }

   @Override
   protected final void globalEventDismissViewerOfObject() {
      this.closeMessage();
   }

   @Override
   protected final void globalEventSystemLocked() {
      this.closeMessage();
   }

   @Override
   protected final void globalEventSystemUnlocked() {
      MMSMessageModel message = (MMSMessageModel)this.getMessageModel();
      message.perform(5803508244060051872L, null);
      this.invalidate();
   }

   @Override
   protected final void globalEventMarkViewedItemAsOpen() {
      MMSMessageModel message = (MMSMessageModel)this.getMessageModel();
      if (message.isUnopened() && !ApplicationManager.getApplicationManager().isSystemLocked()) {
         message.perform(5803508244060051872L, null);
         this.invalidate();
      }

      UiApplication.getUiApplication().requestForeground();
   }
}
