package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;

final class ContactTreeField$ContactLeaf extends Field implements TreeItem, BranchLeaf {
   private Tag TAG;
   private final ContactTreeField this$0;

   final PeerContact getContact() {
      return (PeerContact)this.getCookie();
   }

   @Override
   public final void doInvalidate(boolean andRelated) {
      PeerContact contact = (PeerContact)this.getCookie();
      this.invalidate();
      Branch[] branches = ContactTreeField.access$1200(this.this$0, contact);
      Vector existing = (Vector)ContactTreeField.access$1300(this.this$0).get(contact);
      Vector oldManagers = this.moveLeaves(existing);
      if (branches.length == 1 && oldManagers == null) {
         this.one2oneBranch(branches);
      } else if (branches.length == 1 && oldManagers != null && oldManagers.size() > 1) {
         this.many2oneBranch(branches, contact, oldManagers, existing);
      } else if (branches.length > 1 && oldManagers == null) {
         this.one2manyBranches(branches, contact);
      } else {
         this.many2manyBranches(branches, contact, oldManagers, existing);
      }

      if (andRelated) {
         PeerConversation conversation = PeerApplication.getInstance().getConvByContact(contact);
         if (conversation != null) {
            ContactTreeField$ConversationLeaf cl = ContactTreeField.access$1400(this.this$0, conversation);
            if (cl != null) {
               cl.doInvalidate(true);
            }
         }
      }
   }

   private final Vector moveLeaves(Vector existing) {
      Vector oldManagers = null;
      if (existing != null) {
         oldManagers = new Vector();

         for (int i = 0; i < existing.size(); i++) {
            Manager m = ((ContactTreeField$ContactLeaf)existing.elementAt(i)).getManager();
            if (m != null) {
               oldManagers.addElement(m);
            } else {
               existing.removeElementAt(i);
            }
         }
      }

      return oldManagers;
   }

   private final void one2oneBranch(Branch[] branches) {
      Manager oldManager = this.getManager();
      if (branches[0] == oldManager) {
         ((TreeItem)oldManager).doInvalidate(true);
      } else {
         oldManager.delete(this);
         branches[0].add(this);
      }
   }

   private final void many2oneBranch(Branch[] branches, PeerContact contact, Vector oldManagers, Vector existing) {
      ContactTreeField.access$1300(this.this$0).remove(contact);
      boolean found = false;

      for (int i = 0; i < oldManagers.size(); i++) {
         Manager oldManager = (Manager)oldManagers.elementAt(i);
         if (branches[0] == oldManager) {
            found = true;
            ((TreeItem)oldManager).doInvalidate(true);
            ContactTreeField.access$1500(this.this$0).put(contact, existing.elementAt(i));
         } else {
            oldManager.delete((Field)existing.elementAt(i));
         }
      }

      if (!found) {
         ContactTreeField$ContactLeaf leaf = this.this$0.createContactLeaf(contact);
         branches[0].add(leaf);
         ContactTreeField.access$1500(this.this$0).put(contact, leaf);
      }
   }

   private final void one2manyBranches(Branch[] branches, PeerContact contact) {
      ContactTreeField.access$1500(this.this$0).remove(contact);
      Vector contacts = new Vector(branches.length);
      boolean found = false;
      Manager oldManager = this.getManager();

      for (int i = 0; i < branches.length; i++) {
         if (oldManager == branches[i]) {
            found = true;
            ((TreeItem)oldManager).doInvalidate(true);
            contacts.addElement(this);
         } else {
            ContactTreeField$ContactLeaf leaf = new ContactTreeField$ContactLeaf(this.this$0, contact);
            branches[i].add(leaf);
            contacts.addElement(leaf);
         }
      }

      ContactTreeField.access$1300(this.this$0).put(contact, contacts);
      if (!found) {
         oldManager.delete(this);
      }
   }

   private final void many2manyBranches(Branch[] branches, PeerContact contact, Vector oldManagers, Vector existing) {
      Vector branchVector = new Vector(branches.length);

      for (int i = 0; i < branches.length; i++) {
         branchVector.addElement(branches[i]);
      }

      if (oldManagers != null) {
         for (int i = 0; i < oldManagers.size(); i++) {
            Manager oldManager = (Manager)oldManagers.elementAt(i);
            if (branchVector.contains(oldManager)) {
               ((TreeItem)oldManager).doInvalidate(true);
               branchVector.removeElement(oldManager);
            } else {
               oldManager.delete((Field)existing.elementAt(i));
               existing.removeElementAt(i);
               oldManagers.removeElementAt(i);
               i--;
            }
         }
      }

      for (int i = 0; i < branchVector.size(); i++) {
         ContactTreeField$ContactLeaf leaf = this.this$0.createContactLeaf(contact);
         ((Branch)branchVector.elementAt(i)).add(leaf);
      }
   }

   @Override
   public final int getPreferredHeight() {
      if (((PeerContact)this.getCookie()).isFiltered()) {
         return 0;
      }

      int height = QmUtil.calculateTrueFontHeight(((PeerContact)this.getCookie()).getDisplayName());
      return Math.max(
         Math.max(height, ContactTreeField.access$1600(this.this$0)),
         ContactTreeField._smileyFacility != null ? ContactTreeField._smileyFacility.emoticonSize() : 0
      );
   }

   @Override
   protected final void layout(int width, int height) {
      height = this.getPreferredHeight();
      this.setExtent(width, height);
   }

   @Override
   public final boolean isFocusable() {
      return !((PeerContact)this.getCookie()).isFiltered();
   }

   @Override
   protected final void paint(Graphics graphics) {
      PeerContact contact = (PeerContact)this.getCookie();
      this.this$0.paintContact(graphics, ContactTreeField.access$1700(this.this$0).getConvByContact(contact), contact, this);
   }

   ContactTreeField$ContactLeaf(ContactTreeField _1, PeerContact contact) {
      this.this$0 = _1;
      this.TAG = Tag.create("bbmessenger-contactleaf");
      this.setTag(this.TAG);
      this.setCookie(contact);
   }
}
