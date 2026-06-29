package net.rim.device.apps.internal.messaging;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.MMS;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.Tooltip;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MessageEntryPoint;
import net.rim.device.apps.api.messaging.MessageFilter;
import net.rim.device.apps.api.messaging.NonpersistedUtilityFolders;
import net.rim.device.apps.api.messaging.messagelist.AutoHolsterViewerListener;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.messagelist.MessageListUI;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.api.messaging.util.AnonymousMessages;
import net.rim.device.apps.api.messaging.util.DraftSaveable;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.ui.DialogWithBackgroundThread;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.vm.WeakReference;

public final class MessagingApp extends UiApplication implements GlobalEventListener {
   private MessageEntryPoint _entry;
   private MessageListUI _mainMessageCentreMessageListUI;
   private MessageListUI _savedMessageListUI;
   private MessageListUI _currentlyVisibleMessageListUI;
   private int _currentlyVisibleScreenType;
   private MessageListUI _previouslyVisibleMessageListUI;
   private int _previouslyVisibleScreenType;
   private boolean _resetScreenType;
   private LongHashtable _serviceMessageListUIs = new LongHashtable(2);
   private long _currentlyVisibleServiceID;
   private static final int SET_FILTER_SLOW_THRESHOLD = 1500;

   public static final void main(String[] args) {
      if (args != null && args.length == 1) {
         MessageEntryPoint entry = MessageEntryPoint.findEntry(args[0]);
         if (args[0].equals("saved")) {
            ShowMessageApp.showMessageApp(entry, 1855809477, null);
            return;
         }

         if (args[0].equals("mainview")) {
            ShowMessageApp.showMessageApp(entry, -246332839, null);
            return;
         }

         if (args[0].equals("search")) {
            ShowMessageApp.showMessageApp(entry, -1588283800, null);
            return;
         }

         if (args[0].startsWith("search=")) {
            ShowMessageApp.showMessageApp(entry, -1001931407, args[0].substring(7));
            return;
         }

         if (args[0].startsWith("service=")) {
            ShowMessageApp.showMessageApp(entry, 1064620590, args[0].substring(8));
            return;
         }

         if (args[0].startsWith("searchinit")) {
            int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_messaging_app");
            ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
            if (descriptors == null || descriptors.length < 1) {
               return;
            }

            ApplicationDescriptor ad = null;

            for (int i = descriptors.length - 1; i >= 0; i--) {
               if (descriptors[i].getName().equals("SearchMessages")) {
                  ad = descriptors[i];
                  break;
               }
            }

            if (ad != null) {
               RibbonLauncher rl = RibbonLauncher.getInstance();
               if (rl != null) {
                  rl.registerAction("SearchMessages", new ApplicationEntryPoint(ad));
                  return;
               }
            }
         }
      } else {
         if (!ShowMessageApp.isRegistered()) {
            NonpersistedUtilityFolders.ensureUtilityFolderAddedToMerge(7175316403005034194L, 6368823655991217730L);
            MessageListOptions.getOptions().enableSynchronization();
            AnonymousMessagesImpl.register();
            if (AnonymousMessages.isFirstBoot()) {
               byte[] brandingMessageFrom = Branding.getData(4101);
               byte[] brandingMessageSubject = Branding.getData(4099);
               byte[] brandingMessageBody = Branding.getData(4100);
               String encoding = "UTF8";
               if (brandingMessageFrom == null) {
                  brandingMessageFrom = Branding.getData(4098);
                  brandingMessageSubject = Branding.getData(4096);
                  brandingMessageBody = Branding.getData(4097);
                  encoding = "ISO8859_1";
               }

               label125:
               try {
                  if (brandingMessageFrom != null && brandingMessageSubject != null && brandingMessageBody != null) {
                     AnonymousMessages.createAnonymousMessage(
                        new String(brandingMessageFrom, encoding), new String(brandingMessageSubject, encoding), new String(brandingMessageBody, encoding)
                     );
                  }
               } finally {
                  break label125;
               }
            }

            ShowMessageApp.register();
            registerMessagingHotKeys();
            Proxy.getInstance().addRealtimeClockListener(new MessageListCleaner());
         }

         new MessagingApp(null).enterEventDispatcher();
      }
   }

   public static final void registerMessagingHotKeys() {
      if (!InternalServices.isReducedFormFactor()) {
         char hotKey = MessageResources.getString(130).charAt(0);
         Verb v = new MessagingApp$ScreenChangeVerb(-246332839);
         HotKeys.registerHotKey(1, hotKey, v, true);
         HotKeys.registerHotKey(3, hotKey, v, true);
         hotKey = MessageResources.getString(131).charAt(0);
         v = new MessagingApp$ScreenChangeVerb(-1676600994);
         HotKeys.registerHotKey(1, hotKey, v, true);
         HotKeys.registerHotKey(3, hotKey, v, true);
      }
   }

   public MessagingApp(String argument) {
      MessageEntryPoint mainview = MessageEntryPoint.register("Messages", "mainview", 0);
      UnreadCountManager.setAction(1, mainview);
      UnreadCountManager.setAction(2, mainview);
      MessageListOptions.getOptions().applyOptions(mainview);
      this._savedMessageListUI = new MessageListUI(MessageResources.getString(50), null, 2);
      this._savedMessageListUI.loadFrom(FolderMerge.getMergeCollection(6368823655991217730L));
      this._mainMessageCentreMessageListUI = new MessageListUI(null, null, 2);
      this._mainMessageCentreMessageListUI.loadFrom(this.wrapWithFilter(MessageListOptions.getOptions().getMessageCollection()));
      this.addGlobalEventListener(this);
      this.enableKeyUpEvents(true);
      this.addGlobalEventListener(this._mainMessageCentreMessageListUI);
      AutoHolsterViewerListener.getInstance().setMessagingAppInstance(this);
      this.addGlobalEventListener(AutoHolsterViewerListener.getInstance());
      this.addHolsterListener(AutoHolsterViewerListener.getInstance());
      Tooltip.init();
      this.gotoView(-246332839, 0, null);
   }

   private final void suspendNotifies(MessageListUI msgList, Object context) {
      if (msgList != null
         && msgList != this._mainMessageCentreMessageListUI
         && msgList != this._savedMessageListUI
         && !this._serviceMessageListUIs.contains(msgList)) {
         synchronized (FolderHierarchies.getLockObject()) {
            msgList.suspendNotification(context);
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 4609271590317602928L) {
         if (guid == -7912584003624203437L) {
            if (object1 instanceof MessageEntryPoint) {
               this._entry = (MessageEntryPoint)object1;
            } else {
               this._entry = null;
            }

            this.gotoView(data0, data1, object0);
         } else if (guid == 2625733777879188248L) {
            this.suspendNotifies(this._currentlyVisibleMessageListUI, object0);
            this.suspendNotifies(this._previouslyVisibleMessageListUI, object0);
         } else if (guid == -273986034351666339L) {
            registerMessagingHotKeys();
         } else if (guid == -7464003439710973532L) {
            this._savedMessageListUI.setTitle(MessageResources.getString(50));
         } else {
            if (guid != -8639396151207124460L) {
               if (guid == -3632668756051372542L && object0 instanceof Long) {
                  long serviceID = (Long)object0;
                  if (serviceID != -7118119043524803584L && serviceID != -942103673428357213L && serviceID != -4696470826620059293L) {
                     MessageListUI msgList = (MessageListUI)this._serviceMessageListUIs.get(serviceID);
                     if (msgList != null) {
                        this._serviceMessageListUIs.remove(serviceID);
                        if (msgList == this._currentlyVisibleMessageListUI) {
                           this.gotoView(-246332839, 0, null);
                        }
                     }
                  }
               }
            } else if (object0 instanceof Long) {
               long serviceID = (Long)object0;
               if (serviceID == -942103673428357213L || serviceID == -7118119043524803584L || serviceID == -4696470826620059293L) {
                  int pid = Proxy.getInstance().getProcessId();
                  RIMGlobalMessagePoster.postGlobalEvent(pid, -8639396151207124460L, 0, 0, null, null);
                  return;
               }

               String msgListTitle = null;
               int titleID = getMessageListTitleID(serviceID);
               if (titleID != -1) {
                  msgListTitle = MessageResources.getString(titleID);
                  MessageEntryPoint mep = MessageEntryPoint.findEntry("service=" + serviceID);
                  if (mep != null) {
                     mep.set(3, new Integer(titleID));
                  }
               } else {
                  Folder hierarchy = FolderHierarchies.getFolderHierarchy(serviceID);
                  if (hierarchy != null) {
                     msgListTitle = hierarchy.getFriendlyName();
                  }
               }

               MessageListUI msgList = (MessageListUI)this._serviceMessageListUIs.get(serviceID);
               if (msgList != null && msgListTitle != null) {
                  msgList.setTitle(msgListTitle);
                  return;
               }
            }
         }
      } else {
         MessageListOptions.getOptions().applyOptions(MessageEntryPoint.findEntry("mainview"));
         this._mainMessageCentreMessageListUI.loadFrom(this.wrapWithFilter(MessageListOptions.getOptions().getMessageCollection()), true);
         LongEnumeration e = this._serviceMessageListUIs.keys();

         while (e.hasMoreElements()) {
            long serviceID = e.nextElement();
            MessageListUI msgList = (MessageListUI)this._serviceMessageListUIs.get(serviceID);
            if (msgList != null) {
               Collection messagesCollection;
               if (serviceID != -7118119043524803584L && serviceID != -942103673428357213L && serviceID != -4696470826620059293L) {
                  messagesCollection = MessageListOptions.getOptions().getMessageCollection(serviceID);
               } else {
                  messagesCollection = FolderMerge.getMergeCollection(serviceID);
               }

               msgList.loadFrom(this.wrapWithFilter(messagesCollection), true);
            }
         }

         this._savedMessageListUI.updateColumnInfo();
      }
   }

   private static final int getMessageListTitleID(long serviceID) {
      if (serviceID == -7118119043524803584L) {
         return 180;
      } else if (serviceID == -942103673428357213L) {
         return 181;
      } else if (serviceID == -4696470826620059293L) {
         return MMS.isEnabled() ? 209 : 180;
      } else {
         return -1;
      }
   }

   private final MessageListUI getTopMessageListUI() {
      for (Screen screen = this.getActiveScreen(); screen != null; screen = screen.getScreenBelow()) {
         if (screen instanceof MessageListUI) {
            return (MessageListUI)screen;
         }
      }

      return null;
   }

   private final void popScreenAndHigher(Screen screen) {
      this.checkAndSaveEmails();

      Screen topScreen;
      do {
         topScreen = this.getActiveScreen();
         this.popScreen(topScreen);
      } while (topScreen != screen);
   }

   private final void checkAndSaveEmails() {
      Screen currentScreen = this.getActiveScreen();

      while (!(currentScreen instanceof MessageListUI)) {
         if (currentScreen == null) {
            return;
         }

         if (!(currentScreen instanceof DraftSaveable)) {
            currentScreen = currentScreen.getScreenBelow();
         } else {
            ((DraftSaveable)currentScreen).draftSaveOnClose();
            UiApplication.getUiApplication().popScreen(currentScreen);
            currentScreen = this.getActiveScreen();
         }
      }
   }

   private final void gotoView(int screenType, int intParameter, Object objectParameter) {
      Screen topScreen = this.getTopMessageListUI();
      if (topScreen != this._currentlyVisibleMessageListUI) {
         if (topScreen != this._previouslyVisibleMessageListUI) {
            return;
         }

         this._currentlyVisibleMessageListUI = this._previouslyVisibleMessageListUI;
         this._currentlyVisibleScreenType = this._previouslyVisibleScreenType;
         this._previouslyVisibleMessageListUI = null;
      }

      MessageListUI searchUI = null;
      if (objectParameter instanceof MessageListUI) {
         searchUI = (MessageListUI)objectParameter;
      }

      if (this._currentlyVisibleScreenType != -1244342860 || this._currentlyVisibleMessageListUI != searchUI) {
         boolean overrideExitAction = false;
         int exitAction = 0;
         if (this._currentlyVisibleScreenType == -1676600994
            || this._currentlyVisibleScreenType == 1855809477
            || this._currentlyVisibleScreenType == -1244342860) {
            this._currentlyVisibleMessageListUI.updateLastSeenTimeStamp();
            this._currentlyVisibleMessageListUI.initiateBulkMarkOldOperation();
            exitAction = this._currentlyVisibleMessageListUI.getExitAction();
            overrideExitAction = true;
            this.popScreenAndHigher(topScreen);
            this._currentlyVisibleMessageListUI = this._previouslyVisibleMessageListUI;
            this._currentlyVisibleScreenType = this._previouslyVisibleScreenType;
            this._previouslyVisibleMessageListUI = null;
            topScreen = this.getTopMessageListUI();
         }

         long serviceID = 0;
         String friendlyName = null;
         MessageListUI serviceMessageListUI = null;
         if ((screenType == 830104111 || screenType == 1064620590) && objectParameter instanceof String) {
            serviceID = Long.parseLong((String)objectParameter);
            MessageEntryPoint entry = MessageEntryPoint.findEntry("service=" + serviceID);
            if (entry != null) {
               friendlyName = entry.get(3, "");
            }

            serviceMessageListUI = (MessageListUI)this._serviceMessageListUIs.get(serviceID);
            if (serviceMessageListUI != null) {
               serviceMessageListUI.setTitle(friendlyName);
            }
         }

         if (topScreen == null || screenType != this._currentlyVisibleScreenType || serviceID != 0 && serviceID != this._currentlyVisibleServiceID) {
            if (topScreen == null) {
               exitAction = 2;
               overrideExitAction = true;
            }

            this._previouslyVisibleScreenType = this._currentlyVisibleScreenType;
            this._currentlyVisibleScreenType = screenType;
            this._currentlyVisibleServiceID = 0;
            switch (screenType) {
               case -1676600994:
               case 1855809477:
                  boolean fromRibbonx = screenType == 1855809477;
                  this._previouslyVisibleMessageListUI = this._currentlyVisibleMessageListUI;
                  if (!overrideExitAction || fromRibbonx) {
                     exitAction = fromRibbonx ? 2 : 1;
                  }

                  this._savedMessageListUI.setExitAction(exitAction);
                  this._currentlyVisibleMessageListUI = this._savedMessageListUI;
                  this._savedMessageListUI.run(new ContextObject(3, 52));
                  break;
               case -1588283800:
               case 2076204577:
                  boolean fromRibbon = screenType == -1588283800;
                  MessageSearch searchInterfacex = MessageSearch.getInstance();
                  if (searchInterfacex != null) {
                     EditorUsingRIMModelFactory screen = null;
                     if (objectParameter != null) {
                        screen = searchInterfacex.getSearchEditScreen(objectParameter, fromRibbon);
                     }

                     if (screen == null) {
                        screen = searchInterfacex.getSearchEditScreen(fromRibbon);
                     }

                     this._resetScreenType = true;
                     screen.go(false);
                     synchronized (this) {
                        if (this._resetScreenType) {
                           this._currentlyVisibleScreenType = 0;
                           this._resetScreenType = false;
                        }
                     }
                  }
                  break;
               case -1244342860:
                  synchronized (this) {
                     this._resetScreenType = false;
                     this._currentlyVisibleScreenType = -1244342860;
                  }

                  if (searchUI != null) {
                     this._previouslyVisibleMessageListUI = this._currentlyVisibleMessageListUI;
                     this._currentlyVisibleMessageListUI = searchUI;
                     if (overrideExitAction) {
                        searchUI.setExitAction(exitAction);
                     }

                     searchUI.run(new ContextObject(3, 22));
                  }
                  break;
               case -1001931407:
                  MessageSearch searchInterface = MessageSearch.getInstance();
                  if (searchInterface != null && objectParameter instanceof String) {
                     searchInterface.runSearch((String)objectParameter, true);
                     this._currentlyVisibleScreenType = 0;
                  }
                  break;
               case -246332839:
                  if (topScreen != null) {
                     this.popScreenAndHigher(topScreen);
                  }

                  this._previouslyVisibleMessageListUI = null;
                  this._currentlyVisibleMessageListUI = this._mainMessageCentreMessageListUI;
                  this._mainMessageCentreMessageListUI.run(new ContextObject(3));
                  break;
               case 830104111:
               case 1064620590:
                  if (topScreen != null) {
                     this.popScreenAndHigher(topScreen);
                  }

                  this._currentlyVisibleServiceID = serviceID;
                  if (serviceMessageListUI == null) {
                     Collection messagesCollection = null;
                     int titleID = getMessageListTitleID(serviceID);
                     if (titleID != -1) {
                        messagesCollection = FolderMerge.getMergeCollection(serviceID);
                     } else {
                        Folder hierarchy = FolderHierarchies.getFolderHierarchy(serviceID);
                        if (hierarchy != null) {
                           friendlyName = hierarchy.getFriendlyName();
                        } else {
                           friendlyName = "";
                        }

                        messagesCollection = MessageListOptions.getOptions().getMessageCollection(serviceID);
                     }

                     serviceMessageListUI = new MessageListUI(friendlyName, null, 2);
                     serviceMessageListUI.loadFrom(this.wrapWithFilter(messagesCollection));
                     this._serviceMessageListUIs.put(serviceID, serviceMessageListUI);
                  }

                  this._previouslyVisibleMessageListUI = this._currentlyVisibleMessageListUI;
                  this._currentlyVisibleMessageListUI = serviceMessageListUI;
                  boolean fromRibbonx = screenType == 1064620590;
                  if (!overrideExitAction || fromRibbonx) {
                     exitAction = fromRibbonx ? 2 : 1;
                  }

                  serviceMessageListUI.setExitAction(exitAction);
                  ContextObject context = new ContextObject(3);
                  if (serviceID != -7118119043524803584L && serviceID != -942103673428357213L && serviceID != -4696470826620059293L) {
                     context.put(-953487338188658393L, new Long(serviceID));
                  } else {
                     context.put(8505215491443778230L, new Long(serviceID));
                  }

                  serviceMessageListUI.run(context);
            }

            if (this._previouslyVisibleMessageListUI != null && !this.launchedFromRibbon(this._currentlyVisibleScreenType)) {
               this._previouslyVisibleMessageListUI.updateLastSeenTimeStamp();
            }
         }
      }
   }

   @Override
   public final void activate() {
      if (this._entry != null) {
         this._entry.activate();
      }

      if (this._currentlyVisibleMessageListUI != null) {
         this._currentlyVisibleMessageListUI.onExposed();
      }

      super.activate();
   }

   @Override
   public final void deactivate() {
      if (!DeviceInfo.isInHolster() && !Security.getInstance().isLockRequired()) {
         this._currentlyVisibleMessageListUI.updateLastSeenTimeStamp();
         this._currentlyVisibleMessageListUI.initiateBulkMarkOldOperation();
         this._currentlyVisibleMessageListUI.initiateRemoveFlagsOperation();
         if (!this.launchedFromRibbon(this._currentlyVisibleScreenType) && this._previouslyVisibleMessageListUI != null) {
            this._previouslyVisibleMessageListUI.initiateBulkMarkOldOperation();
         }

         if (this._entry != null) {
            this._entry.deactivate();
         }
      }
   }

   private final boolean launchedFromRibbon(int screenType) {
      return screenType == 1064620590 || screenType == 1855809477 || screenType == -1588283800;
   }

   private final Collection wrapWithFilter(Collection messages) {
      if (MessageListOptions.getOptions().getFlag(512)) {
         MessageFilter filter = new MessageFilter((ReadableList)messages, (byte)1);
         ((CollectionEventSource)messages).addCollectionListener(new WeakReference(filter));
         int unfilteredSize = ((ReadableList)messages).size();
         if (unfilteredSize > 1500) {
            this.setFilterWithProgress(filter);
         }

         return filter;
      } else {
         return messages;
      }
   }

   private final void setFilterWithProgress(MessageFilter filter) {
      DialogWithBackgroundThread dialog = new DialogWithBackgroundThread();
      SetFilterRunnable activity = new SetFilterRunnable(filter);
      String progressTitle = MessageResources.getString(202);
      dialog.initialize(progressTitle, activity);
      dialog.run();
      BlockModalThread blocker = new BlockModalThread(activity);
      blocker.run();
   }
}
