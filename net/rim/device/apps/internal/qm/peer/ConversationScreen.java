package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TextChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.apps.internal.qm.peer.common.Conversation;
import net.rim.device.apps.internal.qm.peer.common.NewNotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.NotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.QmMainScreen;
import net.rim.device.apps.internal.qm.peer.common.QmPopupStatus;
import net.rim.device.apps.internal.qm.peer.common.QmVerb;
import net.rim.device.apps.internal.qm.peer.common.TypingNotificationMessage;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.vm.WeakReference;

final class ConversationScreen extends QmMainScreen implements CollectionListener, TextChangeListener {
   private PeerApplication _application = PeerApplication.getInstance();
   private UiApplication _uiApplication;
   private PeerConversation _conversation;
   private ConversationTitle _title;
   private ConversationScreen$BottomUpManager _clientManager;
   private MessageList _messageList;
   private NewMessageField _replyField;
   private Field _notificationField;
   private TypingTimer _typingTimer;
   private ContextObject _context;
   private int _delayedInvocationId = -1;
   private Runnable _timeOutRunnable = new ConversationScreen$1(this);
   private static Tag TAG = Tag.create("bbmessenger-conversationscreen");
   private static final boolean _walkieTalkie;
   private static QmVerb _sendVerb = new QmVerb(332032, 61);
   private static QmVerb _closeVerb = new QmVerb(268501007, 79);
   private static QmVerb _endVerb = new QmVerb(268501007, 28);
   private static QmVerb _addVerb = new QmVerb(528641, 6);
   private static QmVerb _inviteVerb = new QmVerb(397569, 42);
   private static QmVerb _getContactInfoVerb = new QmVerb(594178, 31);
   private static QmVerb _clearVerb = new QmVerb(332036, 14);
   private static QmVerb _buzzVerb = new QmVerb(594184, 10);
   private static QmVerb _participantsVerb = new QmVerb(397570, 80);
   private static QmVerb _emailChatHistoryVerb = new QmVerb(332038, 26);
   private static QmVerb _replyVerb = new QmVerb(332032, 97);
   private static PeerVerb _sendFileVerb = new PeerVerb(397571, 2036);
   private static QmVerb _sendVoiceNoteVerb = new QmVerb(397573, 105);
   private static PeerVerb _startServiceVerb = new PeerVerb(397571, 2042);

   final void onFontChanged() {
      synchronized (this.getLock()) {
         this._title.onFontChanged();

         for (int i = this._messageList.size() - 1; i >= 0; i--) {
            Object o = this._messageList.getAt(i);
            if (o instanceof MessageField) {
               ((MessageField)o).onFontChanged();
            }
         }

         this.updateLayout();
      }
   }

   final void lock() {
      this._title.lock();
      this._messageList.lock();
      if (this._replyField != null) {
         this._replyField.lock();
      }

      if (this._notificationField != null) {
         this._clientManager.delete(this._notificationField);
         this._notificationField = null;
      }
   }

   final void doModal(Object context) {
      synchronized (this) {
         if (this.isDisplayed()) {
            this.dismiss();
         }

         this._uiApplication = UiApplication.getUiApplication();
         this._application.postInvokeLaterInternal(this._uiApplication, new ConversationScreen$2(this, context));
         this._application.scheduleApplication(this._uiApplication);
      }

      this._uiApplication.pushModalScreen(this);
   }

   final void onLocaleChanged() {
      this._title.refresh();
      this._replyField.onLocaleChanged();
   }

   final PeerConversation getConversation() {
      return this._conversation;
   }

   final void dismiss() {
      if (this._uiApplication != null) {
         this._uiApplication.invokeAndWait(new ConversationScreen$3(this));
      }
   }

   final void updateNotification(Object message) {
      if (this._uiApplication != null) {
         if (this._uiApplication.isEventThread()) {
            this.handleNotificationMessage(message);
            return;
         }

         this._application.postInvokeLaterInternal(this._uiApplication, new ConversationScreen$4(this, message));
      }
   }

   final void sendVoiceNote() {
      if (VoiceNoteDialog.doModal()) {
         PeerApplication.getInstance().sendFile(this._conversation.getFirstParticipant(), "audio/amr", "voicenote.amr", VoiceNoteDialog.getVoiceClip());
      }
   }

   final void sendFile() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: ldc_w "/store/"
      // 007: invokespecial net/rim/device/apps/api/framework/file/FileSelector.<init> (Ljava/lang/String;)V
      // 00a: astore 1
      // 00b: aload 1
      // 00c: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.onlySelectForwardUnlocked ()V
      // 00f: aload 1
      // 010: aconst_null
      // 011: invokevirtual net/rim/device/apps/api/framework/file/FileSelector.selectFile (Ljava/lang/String;)Ljava/lang/String;
      // 014: astore 2
      // 015: aload 2
      // 016: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 019: astore 3
      // 01a: aload 2
      // 01b: ifnonnull 021
      // 01e: goto 116
      // 021: aload 3
      // 022: ifnonnull 028
      // 025: goto 10d
      // 028: aload 3
      // 029: ldc_w "image"
      // 02c: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 02f: ifne 03f
      // 032: aload 3
      // 033: ldc_w "audio"
      // 036: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 039: ifne 03f
      // 03c: goto 10d
      // 03f: aconst_null
      // 040: astore 4
      // 042: aconst_null
      // 043: astore 5
      // 045: aload 2
      // 046: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 049: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 04c: checkcast java/lang/Object
      // 04f: astore 4
      // 051: aload 4
      // 053: dup
      // 054: instanceof java/lang/Object
      // 057: ifne 05e
      // 05a: pop
      // 05b: goto 088
      // 05e: checkcast java/lang/Object
      // 061: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 066: ifeq 088
      // 069: aload 5
      // 06b: ifnull 078
      // 06e: aload 5
      // 070: invokevirtual java/io/InputStream.close ()V
      // 073: goto 078
      // 076: astore 6
      // 078: aload 4
      // 07a: ifnull 087
      // 07d: aload 4
      // 07f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 084: return
      // 085: astore 6
      // 087: return
      // 088: aload 4
      // 08a: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 08f: astore 5
      // 091: invokestatic net/rim/device/apps/internal/qm/peer/PeerApplication.getInstance ()Lnet/rim/device/apps/internal/qm/peer/PeerApplication;
      // 094: aload 0
      // 095: getfield net/rim/device/apps/internal/qm/peer/ConversationScreen._conversation Lnet/rim/device/apps/internal/qm/peer/PeerConversation;
      // 098: invokevirtual net/rim/device/apps/internal/qm/peer/PeerConversation.getFirstParticipant ()Lnet/rim/device/apps/internal/qm/peer/PeerContact;
      // 09b: aload 3
      // 09c: aload 2
      // 09d: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 0a0: aload 5
      // 0a2: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 0a5: invokevirtual net/rim/device/apps/internal/qm/peer/PeerApplication.sendFile (Lnet/rim/device/apps/internal/qm/peer/PeerContact;Ljava/lang/String;Ljava/lang/String;[B)V
      // 0a8: aload 5
      // 0aa: ifnull 0b7
      // 0ad: aload 5
      // 0af: invokevirtual java/io/InputStream.close ()V
      // 0b2: goto 0b7
      // 0b5: astore 6
      // 0b7: aload 4
      // 0b9: ifnull 116
      // 0bc: aload 4
      // 0be: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0c3: return
      // 0c4: astore 6
      // 0c6: return
      // 0c7: astore 6
      // 0c9: aload 5
      // 0cb: ifnull 0d8
      // 0ce: aload 5
      // 0d0: invokevirtual java/io/InputStream.close ()V
      // 0d3: goto 0d8
      // 0d6: astore 6
      // 0d8: aload 4
      // 0da: ifnull 116
      // 0dd: aload 4
      // 0df: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e4: return
      // 0e5: astore 6
      // 0e7: return
      // 0e8: astore 7
      // 0ea: aload 5
      // 0ec: ifnull 0f9
      // 0ef: aload 5
      // 0f1: invokevirtual java/io/InputStream.close ()V
      // 0f4: goto 0f9
      // 0f7: astore 8
      // 0f9: aload 4
      // 0fb: ifnull 10a
      // 0fe: aload 4
      // 100: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 105: goto 10a
      // 108: astore 8
      // 10a: aload 7
      // 10c: athrow
      // 10d: sipush 2057
      // 110: invokestatic net/rim/device/apps/internal/qm/peer/PeerResources.getString (I)Ljava/lang/String;
      // 113: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 116: return
      // try (49 -> 51): 52 null
      // try (55 -> 57): 58 null
      // try (75 -> 77): 78 null
      // try (81 -> 83): 84 null
      // try (33 -> 47): 86 null
      // try (60 -> 73): 86 null
      // try (89 -> 91): 92 null
      // try (95 -> 97): 98 null
      // try (33 -> 47): 100 null
      // try (60 -> 73): 100 null
      // try (86 -> 87): 100 null
      // try (103 -> 105): 106 null
      // try (109 -> 111): 112 null
      // try (100 -> 101): 100 null
   }

   @Override
   public final void inputMethodTextChanged(Object source, InputMethodEvent event) {
      this._typingTimer.onKey();
   }

   @Override
   public final void textValueChanged(Object source, int eventID) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (element instanceof MessengerMessage) {
         if (this._uiApplication == null) {
            synchronized (this) {
               this._messageList.append((MessengerMessage)element);
               return;
            }
         }

         if (this._uiApplication.isEventThread()) {
            this._messageList.append((MessengerMessage)element);
            return;
         }

         this._application.postInvokeLaterInternal(this._uiApplication, new ConversationScreen$6(this, element));
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof MessengerMessage) {
         if (this._uiApplication == null) {
            synchronized (this) {
               this._messageList.remove((MessengerMessage)element);
               return;
            }
         }

         if (this._uiApplication.isEventThread()) {
            this._messageList.remove((MessengerMessage)element);
            return;
         }

         this._application.postInvokeLaterInternal(this._uiApplication, new ConversationScreen$7(this, element));
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._uiApplication == null) {
         synchronized (this) {
            this.handleElementUpdated(collection, oldElement, newElement);
         }
      } else if (this._uiApplication.isEventThread()) {
         this.handleElementUpdated(collection, oldElement, newElement);
      } else {
         this._application.postInvokeLaterInternal(this._uiApplication, new ConversationScreen$8(this, collection, oldElement, newElement));
      }
   }

   @Override
   public final void reset(Collection collection) {
   }

   ConversationScreen(PeerConversation conversation, ContextObject context) {
      super(562952100904960L);
      this.setDefaultClose(false);
      this._conversation = conversation;
      this.setCookie(this._conversation);
      this._title = new ConversationTitle(conversation);
      this.setTitle(this._title);
      this._clientManager = new ConversationScreen$BottomUpManager(this);
      this._clientManager.setTag(TAG);
      this._messageList = new MessageList(conversation);
      this._clientManager.add(this._messageList);
      this._replyField = new NewMessageField();
      ((TextField)this._replyField.getEditField()).addTextChangeListener(this);
      this._clientManager.add(this._replyField);
      this.add(this._clientManager);
      this._context = context;
      if (context == null || !context.getFlag(26)) {
         this._replyField.setFocus();
      }

      this._typingTimer = new TypingTimer(this._conversation);
      WeakReference collectionListener = (WeakReference)(new Object(this));
      this._application.getContactListCollection().addCollectionListener(collectionListener);
      this._conversation.addCollectionListener(collectionListener);
   }

   private final void handleNotificationMessage(Object message) {
      if (message == null) {
         this.handleNotificationMessageDefault();
      } else {
         if (!this.onNewNotificationMessage(message) && message instanceof Object || this._notificationField != null && !this._notificationField.isFocus()) {
            Field newNotifField = ((FieldProvider)message).getField(null);
            if (!this.areNotificationFieldsEqual(newNotifField)) {
               if (this._notificationField != null) {
                  this._clientManager.delete(this._notificationField);
               }

               this._notificationField = newNotifField;
               if (this._notificationField != null) {
                  this._clientManager.add(this._notificationField);
               }
            }
         }
      }
   }

   @Override
   public final void close() {
      super.close();
      this._uiApplication = null;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Field fieldWithFocus = this._clientManager.getFieldWithFocus();
      switch (key) {
         case '\n':
            if ((status & 4) == 0 && (status & 2) != 0) {
               return super.keyChar(key, status, time);
            }

            if (fieldWithFocus == this._replyField) {
               this.send();
               return true;
            }

            MenuItem defaultMenuItem = fieldWithFocus.getContextMenu().getDefaultItem();
            if (defaultMenuItem instanceof Object) {
               ((VerbMenuItem)defaultMenuItem).getVerb().invoke(this._context);
               return true;
            }
            break;
         case '\u001b':
            if (fieldWithFocus == this._replyField && this._replyField != null && this._replyField.getText().length() > 0) {
               this._replyField.clear();
               return true;
            }

            this.close();
            return true;
         case ' ':
            if (fieldWithFocus == this._replyField) {
               this._typingTimer.onKey();
            }

            if (!super.keyChar(key, status, time) && fieldWithFocus != this._replyField) {
               this.scroll((status & 2) == 0 ? 512 : 256);
            }

            return true;
         default:
            if (fieldWithFocus == this._replyField) {
               this._typingTimer.onKey();
            }
      }

      return super.keyChar(key, status, time);
   }

   static final boolean isPTTKey(int keycode) {
      return Keypad.key(keycode) == 21 && leftSideConvenienceKeySupported();
   }

   private static final boolean leftSideConvenienceKeySupported() {
      return !DirectConnect.isSupported() && Keypad.hasLeftSideConvenienceKey();
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      this._conversation.markUnread(false);
      if (super.keyDown(keycode, time)) {
         return true;
      }

      if (this._replyField == null || this._replyField.getEditField() != this.getLeafFieldWithFocus()) {
         int k = MessageHotkeys.map(keycode);
         switch (k) {
            case 141:
            case 142:
               Field scrolledTo = this._messageList.scroll(k);
               if (scrolledTo != null) {
                  scrolledTo.setFocus();
                  return true;
               }
               break;
            case 148:
               this.setFocusToEditor();
               return true;
            default:
               if (this._context != null) {
                  HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(this._context, -7922982350060060892L);
                  if (hotkey != null && hotkey.invokeHotkey(k, this._context)) {
                     return true;
                  }
               }
         }
      }

      return false;
   }

   @Override
   protected final boolean handleSendKey() {
      boolean ret = super.handleSendKey();
      if (!ret) {
         PeerContact contact = this._conversation.getParticipants().size() == 1 ? this._conversation.getFirstParticipant() : null;
         if (contact != null) {
            Object item = Utils.getAddressCard(contact.getOriginalContactInfo());
            if (item != null) {
               ContextObject resultContext = (ContextObject)(new Object(73));
               resultContext.put(252, item);
               if (ControllerUtilities.invokeSendKeyVerb(item, resultContext)) {
                  ret = true;
               }
            }
         }
      }

      return ret;
   }

   @Override
   public final boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      switch (event) {
         case 519:
         case 6913:
            if ((status & 8) != 0) {
               status &= -9;
            }
         default:
            return super.processNavigationEvent(event, dx, dy, status, time);
      }
   }

   private final void setFocusToEditor() {
      this._messageList.scroll(142);
      if (this._replyField != null) {
         this._replyField.setFocus();
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this._conversation.markUnread(false);
      return super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      this._conversation.markUnread(false);
      return super.trackwheelRoll(amount, status, time);
   }

   private final void onInviteMultiChat() {
      PeerContact[] contacts = this._application.getInvitationChoices(this._conversation);
      if (contacts.length > 0) {
         SelectContactDialog scd = new SelectContactDialog(contacts, 0);
         if (scd.doModal()) {
            PeerApplication.getSession().inviteUser(this._conversation, scd.getSelection());
            return;
         }
      } else {
         this._application.alertUser(3, PeerResources.getString(887));
      }
   }

   @Override
   public final void invokeVerb(int id) {
      switch (id) {
         case 6:
            this._application.getContactListCollection().addContact(this._conversation.getFirstParticipant());
            return;
         case 10:
            if (this._conversation.getParticipants().size() == 1) {
               this.send("<ding>");
               return;
            }
            break;
         case 14:
            this._conversation.clear();
            this._messageList.clear();
            return;
         case 26:
            String message = this._conversation.getHistory();
            Clipboard.getClipboard().put(message);
            QmPopupStatus.show(PeerResources.getString(2025), 750);
            return;
         case 28:
            PeerApplication.endConversation(this._conversation);
            return;
         case 31:
            PeerContact contact = this._conversation.getFirstParticipant();
            UserInfoScreen screen = new UserInfoScreen(contact.getUserInfoFields());
            screen.go();
            return;
         case 42:
            this.onInviteMultiChat();
            return;
         case 61:
            this.send();
            return;
         case 79:
            this.close();
            if (this._uiApplication != PeerApplication.getInstance()) {
               PeerEntry.getInstance().run();
               return;
            }
            break;
         case 80:
            new ParticipantsListScreen(this._conversation).go();
            return;
         case 97:
            this.setFocusToEditor();
            return;
         case 105:
            this.sendVoiceNote();
      }
   }

   @Override
   public final void invokeVerbSpecial(int id) {
      switch (id) {
         case 2036:
            this.sendFile();
            return;
         case 2042:
            SelectServiceDialog ssd = new SelectServiceDialog(this._application.getBlackBerryMessengerApi().getRegisteredServices());
            if (ssd.doModal()) {
               PeerContact first = this._conversation.getFirstParticipant();
               if (first != null) {
                  this._application.getBlackBerryMessengerApi().invokeService(ssd.getSelection(), first);
               }
            }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      QmVerb._screen = this;
      boolean defaultSet = false;
      if ((this._replyField == null || this._replyField.getEditField() != this.getLeafFieldWithFocus() || instance == 0) && this._conversation.size() > 0) {
         menu.add(_clearVerb);
         menu.add(_emailChatHistoryVerb);
         if (instance != 0) {
            menu.add(_replyVerb);
            menu.setDefault(_replyVerb);
            return;
         }
      }

      if (this._replyField != null && !this._replyField.isEmpty()) {
         menu.add(_sendVerb);
         menu.setDefault(_sendVerb);
         defaultSet = true;
      }

      int numParticipants = this._conversation.getParticipants().size() - 1;
      if (numParticipants == 0) {
         if (this._conversation.getFirstParticipant().isAvailable()) {
            menu.add(_buzzVerb);
         }

         menu.add(_sendFileVerb);
         if (VoiceRecordController.isSupported()) {
            menu.add(_sendVoiceNoteVerb);
         }

         if (!defaultSet) {
            menu.setDefault(_sendFileVerb);
            defaultSet = true;
         }
      }

      if (instance == 0) {
         PeerContact contact = this._conversation.getFirstParticipant();
         menu.add(_closeVerb);
         menu.add(_endVerb);
         if (numParticipants == 0) {
            menu.add(_getContactInfoVerb);
            if (contact.getContactLists().size() == 0) {
               menu.add(_addVerb);
            }

            if (this._application.getBlackBerryMessengerApi().getRegisteredServices().length != 0) {
               menu.add(_startServiceVerb);
            }

            Verb[] verbs = Utils.getVerbs(contact.getOriginalContactInfo());
            if (verbs != null && verbs.length > 0) {
               menu.add(verbs);
            }
         } else if (numParticipants > 0) {
            menu.add(_participantsVerb);
         }

         menu.add(_inviteVerb);
         if (!defaultSet) {
            menu.setDefault(_inviteVerb);
            defaultSet = true;
         }

         VerbRepository repository = VerbRepository.getVerbRepository(-1390867101630436713L);
         menu.add(repository.getVerbs(null));
         VerbRepository verbRepository = VerbRepository.getVerbRepository(1247995981604341294L);
         menu.add(verbRepository.getVerbs(null));
         Verb viewCalendarVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(8025740836317336000L);
         if (viewCalendarVerb != null) {
            menu.add(viewCalendarVerb);
         }
      }
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this._conversation.markUnread(false);
      this._messageList.setInitialScroll();
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      NotificationMessageQueue.expireTypingNotifications(null);
      if (this._notificationField != null && this._notificationField.getManager() != null) {
         this._clientManager.delete(this._notificationField);
         this._notificationField = null;
      }

      this._typingTimer.onClose();
   }

   private final void send() {
      this._typingTimer.onSend();
      String message = this._replyField.getText();
      if (message.length() > 0) {
         this._replyField.clear();
         this.send(message);
      }
   }

   private final void send(String message) {
      this._application.send(this._conversation, message);
   }

   private final Object getLock() {
      return this._uiApplication == null ? this : this._uiApplication.getAppEventLock();
   }

   private final void handleElementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (oldElement == newElement) {
         this._title.refresh();
         if (collection instanceof PeerContactListCollection && newElement instanceof PeerContact) {
            PeerContact contact = (PeerContact)newElement;
            if (contact.isTyping()) {
               NotificationMessageQueue.postMessage(new TypingNotificationMessage(this._conversation, contact));
               return;
            }

            NotificationMessageQueue.expireTypingNotifications(contact);
            this.updateNotification(null);
         }
      }
   }

   private final boolean areNotificationFieldsEqual(Field field) {
      return this._notificationField != null && field != null && this._notificationField.getCookie() == field.getCookie();
   }

   private final void handleNotificationMessageDefault() {
      if (this._notificationField != null) {
         synchronized (this._notificationField) {
            Object cookie = this._notificationField.getCookie();
            if (cookie instanceof NotificationMessage && !((NotificationMessage)cookie).isValid()) {
               if (!this._notificationField.isFocus()) {
                  this._clientManager.delete(this._notificationField);
                  this._notificationField = null;
                  this.cancelDelayedInvocation();
               } else if (this._delayedInvocationId == -1) {
                  this._delayedInvocationId = this._uiApplication.invokeLater(this._timeOutRunnable, 2000, true);
               }
            }
         }
      } else {
         this.cancelDelayedInvocation();
      }
   }

   private final boolean onNewNotificationMessage(Object message) {
      if (message instanceof NewNotificationMessage) {
         NewNotificationMessage nnm = (NewNotificationMessage)message;
         Conversation conv = nnm.getConversation();
         if (this._conversation.equals(conv)) {
            nnm.setValid(false);
            return true;
         }
      }

      return false;
   }

   private final void cancelDelayedInvocation() {
      if (this._delayedInvocationId != -1) {
         this._uiApplication.cancelInvokeLater(this._delayedInvocationId);
         this._delayedInvocationId = -1;
      }
   }

   static final void access$000(ConversationScreen x0, Object x1) {
      x0.handleNotificationMessage(x1);
   }

   static final NewMessageField access$100(ConversationScreen x0) {
      return x0._replyField;
   }

   static final MessageList access$200(ConversationScreen x0) {
      return x0._messageList;
   }

   static final ContextObject access$300(ConversationScreen x0) {
      return x0._context;
   }

   static final ContextObject access$302(ConversationScreen x0, ContextObject x1) {
      return x0._context = x1;
   }

   static final UiApplication access$400(ConversationScreen x0) {
      return x0._uiApplication;
   }

   static final void access$500(ConversationScreen x0, Collection x1, Object x2, Object x3) {
      x0.handleElementUpdated(x1, x2, x3);
   }
}
