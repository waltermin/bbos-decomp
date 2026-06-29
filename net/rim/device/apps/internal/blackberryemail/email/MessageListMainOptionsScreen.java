package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.messagelist.MessageListUI;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingOptionsScreen;
import net.rim.device.apps.internal.blackberryemail.email.filters.EmailFilterList;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAFMOptionsScreen;

public class MessageListMainOptionsScreen extends MainScreen implements ListFieldCallback {
   private ListField _listField;
   private Vector _messageOptionsList;
   private short _inboxOption;
   private static Vector _additionalOptionsList;
   private static final long GUID = 2830986604998026732L;

   public MessageListMainOptionsScreen() {
      this.setTitle((Field)(new Object(MessageResources.getString(199), 64)));
      this._messageOptionsList = (Vector)(new Object());
      this._listField = (ListField)(new Object());
      this.add(this._listField);
      this._listField.setCallback(this);
      this._messageOptionsList.addElement(new Object());
      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      boolean displaySyncOptions = EmailOptionsManager.getInstance().displaySyncOptions();
      if (displaySyncOptions || implusService != null && implusService.getReceiptCapableServiceRecIds().length > 0) {
         this._messageOptionsList.addElement(EmailSettingOptionsScreen.getInstance());
         if (displaySyncOptions) {
            this._messageOptionsList.addElement(EmailFilterList.getInstance());
         }
      }

      if (EmailOptionsManager.getInstance().displayCmimeOptions()) {
         this._messageOptionsList.addElement(OTAFMOptionsScreen.getInstance());
      }

      Enumeration enumeration = _additionalOptionsList.elements();

      while (enumeration.hasMoreElements()) {
         this._messageOptionsList.addElement(enumeration.nextElement());
      }

      this._listField.setSize(this._messageOptionsList.size());
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.openSelectedItem();
            return true;
         default:
            return false;
      }
   }

   private MessageListUI getTopMessageListUI() {
      for (Screen screen = this.getUiEngine().getActiveScreen(); screen != null; screen = screen.getScreenBelow()) {
         if (screen instanceof Object) {
            return (MessageListUI)screen;
         }
      }

      return null;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._inboxOption = MessageListOptions.getOptions().getSMSEmailInbox();
      } else if (this._inboxOption != MessageListOptions.getOptions().getSMSEmailInbox()) {
         MessageListUI msgList = this.getTopMessageListUI();
         if (msgList != null) {
            long mergeCollectionId = msgList.getMessageListMergeCollectionID();
            if ((mergeCollectionId == -7118119043524803584L || mergeCollectionId == -4696470826620059293L)
               && MessagingUtil.getMessageServiceMergedCollectionId("SMSFolder") != mergeCollectionId) {
               ShowMessageApp.showMessageApp(-246332839, null);
            }
         }
      }

      super.onUiEngineAttached(attached);
   }

   private void openSelectedItem() {
      Object item = this._messageOptionsList.elementAt(this._listField.getSelectedIndex());
      if (item instanceof Object) {
         ActionProvider actionProvider = (ActionProvider)item;
         actionProvider.perform(6099736323056465049L, null);
      }
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int height = listField.getFont().getHeight();
      Object item = this._messageOptionsList.elementAt(index);
      if (item instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)item;
         paintProvider.paint(graphics, 0, y, width, height, null);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._messageOptionsList.elementAt(index);
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else if (key == '\n') {
         this.openSelectedItem();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      this.openSelectedItem();
      return true;
   }

   public static void registerOptions(MainScreenOptionsListItem option) {
      if (option != null) {
         synchronized (_additionalOptionsList) {
            if (!_additionalOptionsList.contains(option)) {
               _additionalOptionsList.addElement(option);
            }
         }
      }
   }

   public static void deRegisterOptions(MainScreenOptionsListItem option) {
      if (option != null) {
         synchronized (_additionalOptionsList) {
            if (_additionalOptionsList.contains(option)) {
               _additionalOptionsList.removeElement(option);
            }
         }
      }
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1095123266:
            MessageListOptions._addFromAddressBookEnabled = true;
            return true;
         case 1162628169:
            MessageListOptions.getOptions().toggleDisableMessageListEllipses();
            MessageListOptions.getOptions().commit();
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _additionalOptionsList = (Vector)registry.get(2830986604998026732L);
      if (_additionalOptionsList == null) {
         synchronized (registry) {
            _additionalOptionsList = (Vector)registry.get(2830986604998026732L);
            if (_additionalOptionsList == null) {
               _additionalOptionsList = (Vector)(new Object());
               registry.put(2830986604998026732L, _additionalOptionsList);
            }
         }
      }
   }
}
