package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.qm.peer.common.Contact;
import net.rim.device.apps.internal.qm.peer.common.Conversation;
import net.rim.device.apps.internal.qm.peer.common.NewNotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.NotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.TypingNotificationMessage;

public final class NotificationMessageQueue {
   private Vector _messages = (Vector)(new Object());
   private int _next = 0;
   private boolean _dormant;
   private static final long NOTIFICATION_MESSAGE_QUEUE;
   static final long TIME_OUT;
   private static NotificationMessageQueue _pool;

   static final NotificationMessageQueue getInstance() {
      if (_pool == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         synchronized (applicationRegistry) {
            Object obj = applicationRegistry.get(-8276972507418970457L);
            if (!(obj instanceof NotificationMessageQueue)) {
               _pool = new NotificationMessageQueue();
               applicationRegistry.put(-8276972507418970457L, _pool);
            } else {
               _pool = (NotificationMessageQueue)obj;
            }
         }
      }

      return _pool;
   }

   private NotificationMessageQueue() {
      new NotificationMessageQueue$NotificationMessageQueueRunner(this).start();
   }

   final void lock() {
      synchronized (this._messages) {
         int size = this._messages.size();

         for (int index = 0; index < size; index++) {
            Object current = this._messages.elementAt(index);
            if (current instanceof NewNotificationMessage) {
               ((NewNotificationMessage)current).lock();
            }
         }
      }
   }

   public static final void postMessage(NotificationMessage message) {
      if (!(message instanceof TypingNotificationMessage) || !messageExists((TypingNotificationMessage)message)) {
         getInstance().put(message);
      }
   }

   private static final boolean messageExists(TypingNotificationMessage msg) {
      Enumeration elements = getInstance().getElements();

      while (elements.hasMoreElements()) {
         Object cur = elements.nextElement();
         if (cur instanceof TypingNotificationMessage && cur.equals(msg)) {
            return true;
         }
      }

      return false;
   }

   private final synchronized void put(NotificationMessage message) {
      synchronized (this._messages) {
         this._messages.insertElementAt(message, this._next);
      }

      if (message instanceof NewNotificationMessage && expireTypingNotifications(message.getContact())) {
         PeerApplication app = PeerApplication.getInstance();
         if (app != null) {
            app.notificationQueueChanged(null);
         }
      }

      if (this._dormant) {
         this.notify();
         this._dormant = !this._dormant;
      }
   }

   final Enumeration getElements() {
      return this._messages.elements();
   }

   final int size() {
      return this._messages.size();
   }

   static final boolean expireTypingNotifications(Contact contact) {
      boolean expired = false;
      Enumeration elements = getInstance().getElements();

      while (elements.hasMoreElements()) {
         Object cur = elements.nextElement();
         if (cur instanceof TypingNotificationMessage) {
            TypingNotificationMessage msg = (TypingNotificationMessage)cur;
            if (contact == null) {
               msg.setValid(false);
               expired = true;
            } else {
               Contact curContact = msg.getContact();
               if (curContact != null && curContact.equals(contact)) {
                  msg.setValid(false);
                  expired = true;
               }
            }
         }
      }

      return expired;
   }

   static final boolean expireNewNotifications(PeerConversation conversation) {
      return expireNewNotifications(conversation.getFirstParticipant(), conversation);
   }

   static final boolean expireNewNotifications(Contact contact, Conversation conversation) {
      boolean expired = false;
      Enumeration elements = getInstance().getElements();

      while (elements.hasMoreElements()) {
         Object cur = elements.nextElement();
         if (cur instanceof NewNotificationMessage) {
            NewNotificationMessage msg = (NewNotificationMessage)cur;
            Contact curContact = msg.getContact();
            Conversation curConversation = msg.getConversation();
            if (conversation == null || curContact != null && curContact.equals(contact) && curConversation != null && curConversation.equals(conversation)) {
               msg.setValid(false);
               expired = true;
            }
         }
      }

      return expired;
   }

   private final synchronized NotificationMessage get(boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._messages Ljava/util/Vector;
      // 04: invokevirtual java/util/Vector.size ()I
      // 07: ifne 23
      // 0a: invokestatic net/rim/device/apps/internal/qm/peer/PeerApplication.getInstance ()Lnet/rim/device/apps/internal/qm/peer/PeerApplication;
      // 0d: astore 2
      // 0e: aload 2
      // 0f: ifnull 17
      // 12: aload 2
      // 13: aconst_null
      // 14: invokevirtual net/rim/device/apps/internal/qm/peer/PeerApplication.notificationQueueChanged (Lnet/rim/device/apps/internal/qm/peer/common/NotificationMessage;)V
      // 17: aload 0
      // 18: bipush 1
      // 19: putfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._dormant Z
      // 1c: aload 0
      // 1d: invokevirtual java/lang/Object.wait ()V
      // 20: goto 38
      // 23: iload 1
      // 24: ifeq 38
      // 27: aload 0
      // 28: sipush 2000
      // 2b: i2l
      // 2c: invokevirtual java/lang/Object.wait (J)V
      // 2f: goto 38
      // 32: astore 2
      // 33: aconst_null
      // 34: areturn
      // 35: astore 2
      // 36: aconst_null
      // 37: areturn
      // 38: aconst_null
      // 39: astore 2
      // 3a: aload 0
      // 3b: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._messages Ljava/util/Vector;
      // 3e: invokevirtual java/util/Vector.size ()I
      // 41: ifeq 9d
      // 44: aload 0
      // 45: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._messages Ljava/util/Vector;
      // 48: aload 0
      // 49: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 4c: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 4f: checkcast net/rim/device/apps/internal/qm/peer/common/NotificationMessage
      // 52: astore 2
      // 53: aload 2
      // 54: invokevirtual net/rim/device/apps/internal/qm/peer/common/NotificationMessage.isValid ()Z
      // 57: ifne 81
      // 5a: aconst_null
      // 5b: astore 2
      // 5c: aload 0
      // 5d: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._messages Ljava/util/Vector;
      // 60: aload 0
      // 61: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 64: invokevirtual java/util/Vector.removeElementAt (I)V
      // 67: aload 0
      // 68: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 6b: invokestatic net/rim/device/apps/internal/qm/peer/NotificationMessageQueue.getInstance ()Lnet/rim/device/apps/internal/qm/peer/NotificationMessageQueue;
      // 6e: invokevirtual net/rim/device/apps/internal/qm/peer/NotificationMessageQueue.size ()I
      // 71: if_icmplt 79
      // 74: aload 0
      // 75: bipush 0
      // 76: putfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 79: aload 0
      // 7a: bipush 0
      // 7b: invokespecial net/rim/device/apps/internal/qm/peer/NotificationMessageQueue.get (Z)Lnet/rim/device/apps/internal/qm/peer/common/NotificationMessage;
      // 7e: astore 2
      // 7f: aload 2
      // 80: areturn
      // 81: aload 0
      // 82: aload 0
      // 83: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 86: bipush 1
      // 87: iadd
      // 88: putfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 8b: aload 0
      // 8c: getfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 8f: invokestatic net/rim/device/apps/internal/qm/peer/NotificationMessageQueue.getInstance ()Lnet/rim/device/apps/internal/qm/peer/NotificationMessageQueue;
      // 92: invokevirtual net/rim/device/apps/internal/qm/peer/NotificationMessageQueue.size ()I
      // 95: if_icmplt 9d
      // 98: aload 0
      // 99: bipush 0
      // 9a: putfield net/rim/device/apps/internal/qm/peer/NotificationMessageQueue._next I
      // 9d: aload 2
      // 9e: areturn
      // try (0 -> 23): 24 null
      // try (0 -> 23): 27 null
   }

   static final NotificationMessage access$000(NotificationMessageQueue x0, boolean x1) {
      return x0.get(x1);
   }
}
