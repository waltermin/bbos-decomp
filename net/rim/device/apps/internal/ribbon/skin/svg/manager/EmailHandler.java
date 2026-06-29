package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.util.AnonymousMessage;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.ribbon.skin.svg.NewMessageFilter;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class EmailHandler extends Handler implements GlobalEventListener, VerbFactory {
   protected NewMessageFilter _collection;
   private boolean _includeSMSMMS;
   protected int _unreadCountId;
   protected String _hotspotType;
   ActionProvider[] _msgs = new ActionProvider[this.MaxEntries];
   ModelColumnPainter[] _painter = new ModelColumnPainter[this.MaxEntries];
   DeleteSingleItemVerb _deleteVerb = new DeleteSingleItemVerb(611472, 1000);
   Verb[] _verbArray = new Verb[]{this._deleteVerb};
   private static final int NUM_ENTRIES_PER_LOG = 3;

   protected boolean isItemValid(Object item) {
      return this._includeSMSMMS || item instanceof EmailMessageModel || item instanceof PhoneCallModelImpl || item instanceof AnonymousMessage;
   }

   protected void clearFields() {
      for (int i = 0; i < super.MaxEntries; i++) {
         this._msgs[i] = null;
         this.setDisplayable(this._hotspotType + (char)(i + 49) + "hotspot", false);
      }

      if (super._nodes != null) {
         for (int i = super._nodes.length - 1; i >= 0; i--) {
            if (super._nodes[i] != null) {
               super._nodes[i].setString(super.BLANK);
            }
         }
      }
   }

   @Override
   public Verb[] getVerbs(Object context) {
      return this._verbArray;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 4609271590317602928L) {
         boolean newIncludeSMSMMS = this.includeSMSMMS();
         if (newIncludeSMSMMS != this._includeSMSMMS) {
            this._includeSMSMMS = newIncludeSMSMMS;
            this.updateLater();
         }
      }
   }

   private boolean includeSMSMMS() {
      MessageListOptions mlo = MessageListOptions.getOptions();
      short smsInbox = mlo.getSMSEmailInbox();
      switch (smsInbox) {
         case 0:
            String option = ThemeManager.getActiveTheme().getOption("SMSFolder");
            if (option != null) {
               return false;
            }

            return true;
         case 1:
         default:
            return true;
         case 2:
            return false;
      }
   }

   @Override
   public void invoke(int index) {
      if (index >= 0 && index < super.MaxEntries) {
         VerbFactory factory = this;
         ActionProvider msg = this._msgs[index];
         if (msg != null) {
            ContextObject contextObject = ContextObject.castOrCreate(null);
            contextObject.put(250, msg);
            ContextObject.put(contextObject, 244, "messages_index");
            this._deleteVerb.setParameters(msg, null);
            contextObject.put(-2846768035584909703L, factory);
            msg.perform(6099736323056465049L, contextObject);
            this.update(false);
         }
      }
   }

   public EmailHandler(ModelInteractorImpl mi, UiApplication app, NewMessageFilter msgCollection) {
      super(mi, app);
      this._collection = msgCollection;
      this._includeSMSMMS = this.includeSMSMMS();
      this._hotspotType = "messages";
   }

   @Override
   public void update(boolean forceScreenUpdate) {
      if (this._collection != null && super.MaxEntries >= 1) {
         int paintedItems = 0;
         synchronized (FolderHierarchies.getLockObject()) {
            synchronized (super._modelInteractor) {
               this.clearFields();
               int size = this._collection.size();

               for (int index = 0; index < size && paintedItems < super.MaxEntries; index++) {
                  Object item;
                  try {
                     item = this._collection.getAt(size - index - 1);
                  } finally {
                     continue;
                  }

                  if (this.isItemValid(item) && item instanceof ColumnPaintProvider) {
                     this._msgs[paintedItems] = (ActionProvider)item;
                     ColumnPaintProvider cpp = (ColumnPaintProvider)item;
                     if (this._painter[paintedItems] == null) {
                        this._painter[paintedItems] = new ModelColumnPainter(
                           this, super._nodes[paintedItems * 3], super._nodes[paintedItems * 3 + 1], super._nodes[paintedItems * 3 + 2]
                        );
                     }

                     this._painter[paintedItems].clear();
                     cpp.paint(this._painter[paintedItems], null);
                     this.setDisplayable(this._hotspotType + (paintedItems + 1) + "hotspot", true);
                     paintedItems++;
                  }
               }
            }
         }

         if (forceScreenUpdate) {
            super._application.repaint();
         }
      }
   }
}
