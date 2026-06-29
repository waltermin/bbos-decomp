package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;

final class Notifications implements NotificationsEngineListener {
   private ContextObject _deferredContext;
   private Notifications$ContactAvailableRunnable _contactAvailableRunnable = new Notifications$ContactAvailableRunnable();
   private Notifications$NewMessageRunnable _newMessageRunnable = new Notifications$NewMessageRunnable(this);
   private Notifications$NewRequestRunnable _newRequestRunnable = new Notifications$NewRequestRunnable(this);
   private Notifications$OpenConversationRunnable _openConversationRunnable = new Notifications$OpenConversationRunnable();
   private Notifications$OpenNewRequestRunnable _openNewRequestRunnable = new Notifications$OpenNewRequestRunnable();
   private Notifications$CancelImmediateEvent _cancelImmediateEvent = new Notifications$CancelImmediateEvent();
   private static final long CONTACT_AVAILABLE;
   private static final long NEW_MESSAGE;

   static final void setLastEvent() {
   }

   Notifications() {
      this._deferredContext = (ContextObject)(new Object());
      this._deferredContext.setFlag(65);
      this.registerSource(-3969704423467496048L, 8);
      this.registerSource(4051365837710720090L, 47);
   }

   private final void registerSource(long sourceId, int nameId) {
      Object name = new LocalName(nameId);
      NotificationsManager.registerSource(sourceId, name, 2);
      NotificationsManager.registerNotificationsEngineListener(sourceId, this);
   }

   final void triggerContactAvailable(PeerContact contact) {
      setLastEvent();
      Notifications$ContactAvailableRunnable.access$000(this._contactAvailableRunnable, contact);
   }

   final void triggerNewMessage(PeerConversation conversation) {
      if (PeerApplication.getInstance().isUserAvailable()) {
         Notifications$NewMessageRunnable.access$100(this._newMessageRunnable, conversation);
      }

      setLastEvent();
   }

   final void triggerNewRequest(PeerRequest request) {
      if (PeerApplication.getInstance().isUserAvailable()) {
         Notifications$NewRequestRunnable.access$200(this._newRequestRunnable, request);
      }

      setLastEvent();
   }

   final void cancelContactAvailable() {
      this.cancelImmediateNotifications(-3969704423467496048L, 0, null);
   }

   final void cancelNewMessage(PeerConversation conversation) {
      this.cancelImmediateNotifications(4051365837710720090L, conversation.hashCode(), conversation);
   }

   final void cancelNewRequest(PeerRequest request) {
      this.cancelImmediateNotifications(4051365837710720090L, request.hashCode(), request);
   }

   private final void cancelImmediateNotifications(long type, int id, Object eventReference) {
      if (NotificationsManager.isImmediateEventOccuring(type)) {
         Notifications$CancelImmediateEvent cie = this._cancelImmediateEvent;
         boolean scheduled;
         synchronized (cie) {
            scheduled = Notifications$CancelImmediateEvent.access$300(cie);
            Notifications$CancelImmediateEvent.access$400(cie, type, id, eventReference, null);
         }

         if (!scheduled) {
            PeerApplication.getInstance().postInvokeLaterInternal(cie);
         }
      }
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceID, long eventID, Object eventReferenced, Object context) {
   }

   @Override
   public final void deferredEventWasSuperseded(long sourceID, long eventID, Object eventReference, Object context) {
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceId, long eventId, Object eventReferenced, Object context) {
      if (sourceId == 4051365837710720090L) {
         NotificationsManager.cancelDeferredEvent(sourceId, eventId, eventReferenced, 0, context);
         if (!VoiceServices.isPhoneActive()) {
            if (eventReferenced instanceof PeerConversation) {
               PeerApplication app = PeerApplication.getInstance();
               if (app != null) {
                  if (app._conversationScreen != null && app._conversationScreen.isDisplayed()) {
                     if (app._conversationScreen.getConversation() == eventReferenced) {
                        app._conversationScreen.getApplication().requestForeground();
                        return;
                     }

                     app._conversationScreen.dismiss();
                     Notifications$OpenConversationRunnable.access$600(this._openConversationRunnable, (PeerConversation)eventReferenced);
                     return;
                  }

                  Notifications$OpenConversationRunnable.access$600(this._openConversationRunnable, (PeerConversation)eventReferenced);
                  return;
               }
            } else if (eventReferenced instanceof PeerRequest) {
               Notifications$OpenNewRequestRunnable.access$700(this._openNewRequestRunnable, (PeerRequest)eventReferenced);
            }
         }
      }
   }

   static final ContextObject access$500(Notifications x0) {
      return x0._deferredContext;
   }
}
