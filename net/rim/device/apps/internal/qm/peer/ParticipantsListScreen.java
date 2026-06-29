package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.qm.peer.common.QmMainScreen;
import net.rim.device.apps.internal.qm.peer.common.QmVerb;

final class ParticipantsListScreen extends QmMainScreen implements ListFieldCallback, CollectionListener {
   Vector _participants;
   ParticipantsListField _list;
   ParticipationListTitle _title;
   PeerConversation _conversation;
   int _fontHeight;
   int _iconHeight;
   private static QmVerb _newMessageVerb = new QmVerb(332034, 48);
   private static QmVerb _addVerb = new QmVerb(528641, 4);

   ParticipantsListScreen(PeerConversation conversation) {
      super(299069310238720L);
      this._title = new ParticipationListTitle(conversation.getTitle());
      this.setTitle(this._title);
      this._participants = conversation.getParticipants();
      if (this._participants == null) {
         this._participants = (Vector)(new Object());
      }

      this._conversation = conversation;
      this._conversation.addCollectionListener(new Object(this));
      this._list = new ParticipantsListField(this._participants.size());
      this._list.setCallback(this);
      this.add(this._list);
      this._fontHeight = this.getFont().getHeight();
      this._iconHeight = PeerResources.getIconHeight(this.getFont());
   }

   public final void go() {
      UiApplication.getUiApplication().pushScreen(this);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      ((PeerContact)this._participants.elementAt(index)).paint(graphics, null, 0, y, width, this._fontHeight, this._iconHeight, false);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._participants.elementAt(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      if (start >= 0 && start < this._participants.size()) {
         int size = this._participants.size();

         for (int index = start; index < size; index++) {
            if (this._participants.elementAt(index).toString().startsWith(prefix)) {
               return index;
            }
         }

         return this._list.getSelectedIndex();
      } else {
         return this._list.getSelectedIndex();
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == this._conversation) {
         int size = this._conversation.getParticipants().size();
         if (this._list.getSize() != size) {
            synchronized (UiApplication.getUiApplication().getAppEventLock()) {
               this._list.setSize(size);
               return;
            }
         }
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private final PeerContact getContact() {
      PeerContact contact = null;
      Field fieldWithFocus = this.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof ParticipantsListField) {
         int index = ((ListField)fieldWithFocus).getSelectedIndex();
         if (index > -1) {
            contact = (PeerContact)this._participants.elementAt(index);
         }
      }

      return contact;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      QmVerb._screen = this;
      PeerContact contact = this.getContact();
      if (contact != null) {
         if (contact.isAuthorized()) {
            menu.add(_newMessageVerb);
            menu.setDefault(_newMessageVerb);
            return;
         }

         menu.add(_addVerb);
         menu.setDefault(_addVerb);
      }
   }

   @Override
   public final void invokeVerb(int id) {
      PeerContact contact = this.getContact();
      switch (id) {
         case 4:
            if (contact != null) {
               String info = contact.getOriginalContactInfo();
               if (info == null) {
                  info = contact.getId();
               }

               String[] names = new Object[2];
               names[0] = info;
               names[1] = info;
               ContextObject context = (ContextObject)(new Object());
               ContextObject.put(context, 251, names);
               EmailInvitationComposeVerb.doInvite(
                  FactoryUtil.createInstance(info.indexOf(64) == -1 ? 4246852237058296601L : -2985347935260258684L, context), null
               );
               this.close();
            }
         default:
            return;
         case 48:
            this.close();
            PeerApplication.getInstance().openConversation(contact);
      }
   }

   @Override
   public final void invokeVerbSpecial(int id) {
   }
}
