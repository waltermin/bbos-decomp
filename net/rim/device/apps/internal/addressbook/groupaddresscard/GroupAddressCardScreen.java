package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.vm.WeakReference;

class GroupAddressCardScreen extends ModelScreen implements ListFieldCallback, CollectionListener {
   protected Verb _viewMemberVerb;
   protected Verb _editGroupVerb;
   protected Verb _showNamesOrAddressesVerb;
   protected CollectionListField _listField;
   protected boolean _showNames;
   protected ContextObject _paintContext;
   protected GroupAddressCardModel _gacm;
   protected static final int MAX_NAME_LENGTH = 100;
   private static ContextObjectWR _contextWR = new ContextObjectWR(108);
   private static final int VIEW_MEMBER_VERB = 0;
   private static final int SHOW_NAMES_OR_ADDRESSES_VERB = 1;

   GroupAddressCardScreen(GroupAddressCardModel gacm) {
      super(0, gacm.getName(), _contextWR.getContextObject());
      if (gacm instanceof EditableProvider) {
         this._gacm = (GroupAddressCardModel)((EditableProvider)gacm).makeReadWrite();
      } else {
         this._gacm = gacm;
      }

      super.setModel(this._gacm);
      this.validateGroupAddressCard(this._gacm);
      this._listField = new CollectionListField(this._gacm, this);
      super.getMainManager().add(this._listField);
      this._showNames = true;
      this._viewMemberVerb = new GroupAddressCardScreen$GroupMemberVerb(this, 0, 607744, 806);
      this._editGroupVerb = new GroupAddressCardVerb(1, this._gacm, this);
      this._showNamesOrAddressesVerb = new GroupAddressCardScreen$GroupMemberVerb(this, 1, 16859712, 1008);
      this._paintContext = new ContextObject();
      this._paintContext.setFlag(3, 1, 17);
      this._paintContext.setFlag(17, 18);
      this._paintContext.setFlag(4);
      AddressBookServices.getAddressBook().addCollectionListener(new WeakReference(this));
   }

   protected Object getAddressCardAt(int index) {
      return AddressBookServices.getAddressCard(((GroupAddressCardMember)this._gacm.getAt(index)).getUID());
   }

   protected void validateGroupAddressCard(GroupAddressCardModel gacm) {
      int i = gacm.size();

      while (--i >= 0) {
         if (this.getAddressCardAt(i) == null) {
            gacm.removeAt(i);
         }
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (!(this instanceof EditGroupAddressCardScreen)) {
         menu.add(this._editGroupVerb);
         VerbRepository vr = VerbRepository.getVerbRepository(-2362642699356376043L);
         if (vr != null) {
            menu.add(vr.getVerbs(null));
         }
      } else {
         VerbRepository vr = VerbRepository.getVerbRepository(-8839945759096901113L);
         if (vr != null) {
            menu.add(vr.getVerbs(null));
         }
      }

      if (this._gacm.size() > 0) {
         menu.add(this._showNamesOrAddressesVerb);
         if (this.getFieldWithFocus() == this._listField) {
            menu.add(this._viewMemberVerb);
            menu.setDefault(this._viewMemberVerb);
         }
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (!attached && this._gacm instanceof EditableProvider) {
         ((EditableProvider)this._gacm).makeReadOnly();
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected ContextObject getMenuContextObject() {
      ContextObject co = this.getContext();
      co = ContextObject.castOrCreate(co);
      Object o = this._listField.getSelectedElement();
      if (o instanceof GroupAddressCardMember) {
         GroupAddressCardMember gacm = (GroupAddressCardMember)o;
         Object addressCard = gacm.getAddressCardModel();
         if (addressCard != null) {
            ContextObject.put(co, 3696141428889703675L, addressCard);
         }
      }

      return co;
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this._gacm.size() <= 0) {
         graphics.drawText(AddressBookResources.getString(305), 0, y, 4, width);
      } else {
         GroupAddressCardMember member = (GroupAddressCardMember)this._listField.getElementAt(index);
         RIMModel rm = member.getAddressModel();
         if (member != null) {
            if (this._showNames) {
               int xoffset = 0;
               PaintProvider painter = (PaintProvider)member.getAddressCardModel();
               if (painter != null) {
                  xoffset = painter.paint(graphics, 0, y, width, 100, this._paintContext);
               }

               if (rm instanceof FieldLabelProvider) {
                  graphics.drawText(" (" + ((FieldLabelProvider)rm).getLabel() + ')', xoffset, y, 64, width);
                  return;
               }
            } else {
               if (rm != null) {
                  graphics.drawText(rm.toString(), 0, y, 64, width);
                  return;
               }

               PaintProvider painter = (PaintProvider)member.getAddressCardModel();
               if (painter != null) {
                  int xoffset = painter.paint(graphics, 0, y, width, 100, this._paintContext);
                  graphics.drawText(" (" + AddressBookResources.getString(1736) + ')', xoffset, y, 64, width);
                  return;
               }
            }
         }
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._listField.getElementAt(index);
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return this._listField.getSelectedIndex();
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (element instanceof AddressCardModel) {
         int uid = ((AddressCardModel)element).getUID();

         for (int idx = 0; idx < this._gacm.size(); idx++) {
            GroupAddressCardMember member = (GroupAddressCardMember)this._gacm.getAt(idx);
            if (member.getUID() == uid) {
               boolean wasReadOnly = false;
               if (this._gacm instanceof EditableProvider && ((EditableProvider)this._gacm).isReadOnly()) {
                  this._gacm = (GroupAddressCardModel)((EditableProvider)this._gacm).makeReadWrite();
                  wasReadOnly = true;
               }

               this._gacm.removeAt(idx);
               if (this._gacm instanceof EditableProvider && wasReadOnly) {
                  this._gacm = (GroupAddressCardModel)((EditableProvider)this._gacm).makeReadOnly();
               }

               this._listField.updateList();
               this._listField.setDirty(true);
               return;
            }
         }
      }
   }
}
