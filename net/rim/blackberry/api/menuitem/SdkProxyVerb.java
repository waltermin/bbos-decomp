package net.rim.blackberry.api.menuitem;

import javax.microedition.pim.PIM;
import javax.microedition.pim.ToDoList;
import net.rim.blackberry.api.mail.PackageProxy;
import net.rim.blackberry.api.maps.MapView;
import net.rim.blackberry.api.pdap.ContactImpl;
import net.rim.blackberry.api.pdap.ContactListImpl;
import net.rim.blackberry.api.pdap.EventImpl;
import net.rim.blackberry.api.pdap.EventListImpl;
import net.rim.blackberry.api.pdap.InternalBlackBerryMemoList;
import net.rim.blackberry.api.pdap.MemoListFactory;
import net.rim.blackberry.api.pdap.ToDoFactory;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.ModelUser;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.cldc.io.mms.Protocol;
import net.rim.device.cldc.io.sms.TextMessageImpl;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Message;

class SdkProxyVerb extends Verb {
   private long _menuItemId;
   private ApplicationMenuItem _ami;
   private ApplicationDescriptor _application;
   private static ContactListImpl _contactList;
   private static EventListImpl _eventList;
   private static ToDoList _todoList;
   private static InternalBlackBerryMemoList _memoList;

   SdkProxyVerb(ApplicationMenuItem ami, long mii, ApplicationDescriptor application) {
      super(ami._order);
      this._ami = ami;
      this._menuItemId = mii;
      this._application = application;
   }

   @Override
   public Object invoke(Object context) {
      RIMModel selected = null;
      Object externalContext = null;
      if (selected == null) {
         ApplicationSpecificProcessing asp = (ApplicationSpecificProcessing)ApplicationMenuItemRepository.getInstance()
            .getApplicationSpecificProcessingTable()
            .get(this._menuItemId);
         if (asp != null) {
            Object sel = asp.lookForAppropriateObject(this._menuItemId, context);
            if (sel instanceof MapView) {
               externalContext = sel;
            } else {
               selected = (RIMModel)sel;
               externalContext = this.createPublicObject(selected);
            }
         }

         if (externalContext == null) {
            ModelUser mu = (ModelUser)ContextObject.get(context, -6581931217101110672L);
            if (mu != null) {
               selected = (RIMModel)mu.getModel(false);
               externalContext = this.createPublicObject(selected);
            }

            if (selected == null || externalContext == null) {
               selected = (RIMModel)ContextObject.get(context, 254);
               externalContext = this.createPublicObject(selected);
               if (selected == null || externalContext == null) {
                  Object o = ContextObject.get(context, 3696141428889703675L);
                  if (!(o instanceof RIMModel)) {
                     if (o instanceof String) {
                        return this._ami.run(o);
                     }
                  } else {
                     selected = (RIMModel)o;
                     externalContext = this.createPublicObject(selected);
                  }

                  if (selected == null || externalContext == null) {
                     selected = (RIMModel)ContextObject.get(context, 250);
                     externalContext = this.createPublicObject(selected);
                  }

                  if ((selected == null || externalContext == null) && context instanceof String) {
                     externalContext = context;
                  }
               }
            }
         }
      }

      if (this._application == null) {
         return this._ami.run(externalContext);
      }

      this.runOnApplicationThread(externalContext);
      return context;
   }

   ApplicationMenuItem getApplicationMenuItem() {
      return this._ami;
   }

   private Object createPublicObject(RIMModel selected) {
      Object o = null;
      if (selected instanceof EmailMessageModel && this._ami._flags.isSet(0)) {
         EmailMessageModel emm = (EmailMessageModel)selected;
         return PackageProxy.createMessageFromInternalModel(emm);
      }

      if (selected instanceof AddressCardModel && this._ami._flags.isSet(1)) {
         return new ContactImpl(selected, _contactList);
      }

      if (selected instanceof Event) {
         Event e = (Event)selected;
         return new EventImpl(3, e, _eventList);
      }

      if (ToDoFactory.isInternalToDoModel(selected) && this._ami._flags.isSet(1)) {
         return ToDoFactory.createToDo(3, selected, _todoList);
      }

      if (MemoListFactory.isInternalMemoModel(selected) && this._ami._flags.isSet(1)) {
         return _memoList.createMemo(selected);
      }

      if (selected instanceof PhoneCallModel) {
         return PhoneLogs.createCallLogFromInternalModel(selected);
      }

      if (selected instanceof SMSModel) {
         SMSModel smsModel = (SMSModel)selected;
         TextMessageImpl msg = null;
         if (smsModel.inbound()) {
            msg = new TextMessageImpl(smsModel.getSender());
         } else {
            String[] recipients = smsModel.getRecipients();
            String receiver = null;
            if (recipients != null && recipients.length >= 1) {
               receiver = recipients[0];
            }

            msg = new TextMessageImpl(receiver);
         }

         msg.setPayloadText(smsModel.getBody());
         return msg;
      } else if (selected instanceof MMSMessageModel) {
         try {
            MMSMessageModel mmsMessage = (MMSMessageModel)selected;
            return Protocol.createWMAMessage(mmsMessage, mmsMessage.getPayload().getAttribute("content-type"));
         } finally {
            return o;
         }
      } else {
         return o;
      }
   }

   @Override
   public String toString() {
      return this._ami.toString();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void runOnApplicationThread(Object externalContext) {
      int pid = -1;
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         pid = appManager.runApplication(this._application, false);
         var6 = false;
      } finally {
         if (var6) {
            return;
         }
      }

      Message invokeLaterMessage = new Message(0, 2, new SdkProxyVerb$1(this, externalContext), null);
      ((ApplicationManagerInternal)appManager).postMessage(pid, invokeLaterMessage);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      PIM p = PIM.getInstance();

      label59:
      try {
         _contactList = (ContactListImpl)p.openPIMList(1, 3);
         _eventList = (EventListImpl)p.openPIMList(2, 3);
      } catch (Throwable var13) {
         new Throwable(e.toString());
         break label59;
      }

      label56:
      try {
         _todoList = (ToDoList)p.openPIMList(3, 3);
      } catch (Throwable var12) {
         new Throwable(e.toString());
         break label56;
      }

      try {
         _memoList = (InternalBlackBerryMemoList)p.openPIMList(5, 3);
      } catch (Throwable var11) {
         new Throwable(e.toString());
         return;
      }
   }
}
