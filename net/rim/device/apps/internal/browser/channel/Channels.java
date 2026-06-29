package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.SortedCollection;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.RenderingSessionImpl;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.store.BrowserFolders;

public final class Channels implements Runnable {
   private static final String BUNDLE_NAME = "net.rim.device.apps.internal.browser.core.Channels";
   private static final String BROWSER_MODULE_NAME = "net_rim_bb_browser_daemon";
   private static final byte[] DEFAULT_READ_ICON_DATA = new byte[]{
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      -4,
      -1,
      -1,
      3,
      2,
      0,
      0,
      4,
      -6,
      -1,
      -43,
      4,
      10,
      0,
      1,
      4,
      10,
      0,
      85,
      5,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      4,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      5,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      5,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      4,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      5,
      10,
      0,
      1,
      4,
      -6,
      -1,
      85,
      4,
      2,
      0,
      0,
      4,
      92,
      -3,
      -1,
      3,
      -32,
      3,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0
   };
   private static final byte[] DEFAULT_UNREAD_ICON_DATA = new byte[]{
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      -4,
      -1,
      -1,
      3,
      2,
      0,
      0,
      4,
      -6,
      -1,
      -43,
      4,
      10,
      0,
      1,
      4,
      10,
      0,
      85,
      5,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      4,
      10,
      0,
      1,
      5,
      -54,
      33,
      85,
      5,
      -22,
      119,
      1,
      5,
      -54,
      33,
      85,
      5,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      4,
      10,
      0,
      1,
      5,
      10,
      0,
      85,
      5,
      10,
      0,
      1,
      4,
      -6,
      -1,
      85,
      4,
      2,
      0,
      0,
      4,
      92,
      -3,
      -1,
      3,
      -32,
      3,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0
   };
   private static final Bitmap DEFAULT_READ_ICON = new Bitmap(1, 28, 28, DEFAULT_READ_ICON_DATA);
   private static final Bitmap DEFAULT_UNREAD_ICON = new Bitmap(1, 28, 28, DEFAULT_UNREAD_ICON_DATA);

   @Override
   public final void run() {
      SortedCollection channels = (SortedCollection)FolderHierarchies.getFolder(
            BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
         )
         .getContainedItems();

      for (int i = 0; i < channels.size(); i++) {
         ChannelModel channel = (ChannelModel)channels.getAt(i);
         BrowserConfigRecord browserConfig = BrowserConfigRecord.getDecodedConfig(channel.getConfigUID(), channel.getConfigType(), channel.getTransportCID());
         if (browserConfig != null && !browserConfig.isITEnabled()) {
            removeChannelFromRibbon(channel);
         } else {
            addChannelToRibbon(channel);
         }
      }
   }

   public static final void openChannel(String id) {
      ChannelModel channel = getChannel(id);
      if (channel != null) {
         openChannel(channel);
      }
   }

   public static final void openChannel(ChannelModel channel) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      ModelResult modelResult = new ModelResult(channel.getURL(), 8193, null);
      BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(channel.getConfigUID(), channel.getConfigType(), channel.getTransportCID());
      FetchRequest fetch = new FetchRequest(modelResult, browserConfigRecord, 8);
      browser.initiateFetchRequest(fetch);
      browser.invokeLater(new Channels$1(browser, channel));
   }

   public static final void addChannel(ChannelModel channel) {
      ChannelModel currentChannel;
      synchronized (FolderHierarchies.getLockObject()) {
         Folder channelsFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID);
         WritableSet channelItems = (WritableSet)channelsFolder.getContainedItems();
         currentChannel = getChannel(channel.getID());
         long newChannelKey;
         long newChannelLUID;
         if (currentChannel == null) {
            newChannelKey = ((ReadableList)channelItems).size();
            newChannelLUID = UIDGenerator.getUID();
         } else {
            newChannelKey = currentChannel.getTimestamp();
            newChannelLUID = currentChannel.getLUID();
            if (channel.getReadIconData() == null) {
               channel.setReadIconData(currentChannel.getReadIconData());
            }

            if (channel.getUnreadIconData() == null) {
               channel.setUneadIconData(currentChannel.getUnreadIconData());
            }

            channelItems.remove(currentChannel);
         }

         channel.setTimestamp(newChannelKey);
         channel.setLUID(newChannelLUID);
         channel.setParentFolderLUID(channelsFolder.getLUID());
         if (!channel.checkCrypt(true, true)) {
            channel.reCrypt(true, true);
         }

         channelItems.add(channel);
      }

      addChannelToRibbon(channel);
      if (currentChannel == null) {
         BrowserDaemonRegistry.broadCastEvent(150, channel);
      } else {
         BrowserDaemonRegistry.broadCastEvent(152, channel);
      }
   }

   public static final void addChannelToRibbon(ChannelModel channel) {
      Object ticket = PersistentContent.waitForTicket();
      ticket.hashCode();
      int status = channel.getStatus();
      Bitmap defaultIcon = status == 0 ? DEFAULT_READ_ICON : DEFAULT_UNREAD_ICON;
      Bitmap customIcon = null;
      Bitmap readIcon = channel.getReadIcon();
      Bitmap unreadIcon = channel.getUnreadIcon();
      if ((status != 0 || readIcon == null) && unreadIcon != null) {
         customIcon = unreadIcon;
      } else {
         customIcon = readIcon;
      }

      String id = new String(channel.getID().toCharArray());
      int nameResourceId = id.hashCode();
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_browser_daemon");
      if (moduleHandle > 0) {
         ApplicationDescriptor[] browserDescriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
         String[] args = new String[]{"channel", id};
         int newFlags = browserDescriptors[0].getFlags() & -5;
         String ribbonID = "channel." + id;
         ApplicationDescriptor newDescriptor = new ApplicationDescriptor(
            browserDescriptors[0],
            ribbonID,
            args,
            defaultIcon,
            channel.getRibbonPosition(),
            "net.rim.device.apps.internal.browser.core.Channels",
            nameResourceId,
            newFlags
         );
         ChannelApplicationEntryPoint newEntryPoint = new ChannelApplicationEntryPoint(newDescriptor, channel);
         if (customIcon != null) {
            newEntryPoint.set(4, customIcon);
         }

         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            ribbon.registerAction(ribbonID, newEntryPoint);
         }
      }
   }

   public static final void removeAllChannelsFromRibbon() {
      ReadableList channels = (ReadableList)FolderHierarchies.getFolder(
            BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
         )
         .getContainedItems();

      for (int i = channels.size() - 1; i >= 0; i--) {
         removeChannelFromRibbon((ChannelModel)channels.getAt(i));
      }
   }

   public static final void removeChannelFromRibbon(ChannelModel channel) {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         String ribbonID = "channel." + channel.getID();
         if (ribbon.getRegisteredAction(ribbonID) != null) {
            ribbon.unregisterAction(ribbonID);
         }
      }
   }

   public static final void deleteChannel(String id) {
      ChannelModel channel = getChannel(id);
      if (channel != null) {
         deleteChannel(channel);
      }
   }

   public static final void deleteChannel(ChannelModel channel) {
      doDelete(channel);
      String deleteUrl = channel.getDeleteURL();
      if (deleteUrl != null && deleteUrl.length() > 0) {
         sendDeleteNotification(channel, 1);
      }
   }

   public static final void sendDeleteNotification(ChannelModel channel, int retries) {
      String configUID = channel.getConfigUID();
      String transportCID = channel.getTransportCID();
      if (transportCID == null) {
         transportCID = BrowserConfigRecord.IPPP_SERVICE_CID;
      }

      BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(configUID, channel.getConfigType(), transportCID);
      if (browserConfigRecord != null) {
         configUID = browserConfigRecord.getUid();
         transportCID = browserConfigRecord.getPropertyAsString(3);
      }

      BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
      RenderingSession renderingSession = RenderingSessionImpl.getNewInstance();
      RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
      browserImpl.setRenderingOptions(renderingOptions, browserConfigRecord);
      HttpHeaders requestHeaders = new HttpHeaders();
      browserImpl.addStandardRequestHeaders(requestHeaders, renderingSession);
      ModelResult modelResult = new ModelResult(channel.getDeleteURL(), 5, requestHeaders);
      modelResult.setConfigUID(configUID);
      modelResult.setTransportCID(transportCID);
      FetchRequest fetchRequest = new FetchRequest(modelResult, browserConfigRecord);
      fetchRequest.addPendingRequest(new SendDeleteListener(channel, retries));
   }

   private static final void doDelete(ChannelModel channel) {
      if (channel.getStatus() != 0 && channel.getPriority() == 3) {
         NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
      }

      synchronized (FolderHierarchies.getLockObject()) {
         WritableSet channelItemsW = (WritableSet)FolderHierarchies.getFolder(
               BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
            )
            .getContainedItems();
         ReadableList channelItemsR = (ReadableList)channelItemsW;
         channelItemsW.remove(channel);
         removeChannelFromRibbon(channel);
         BrowserDaemonRegistry.broadCastEvent(151, channel);
         int numItems = channelItemsR.size();
         int deletedKey = (int)channel.getTimestamp();
         int numToFix = numItems - deletedKey;
         if (numToFix != 0) {
            if (numToFix < 0) {
               deletedKey = 0;
               numToFix = numItems;
            }

            ChannelModel[] nodesToFix = new ChannelModel[numToFix];
            channelItemsR.getAt(deletedKey, numToFix, nodesToFix, 0);

            for (int i = numToFix - 1; i >= 0; i--) {
               channelItemsW.remove(nodesToFix[i]);
            }

            int newKey = channelItemsR.size();

            for (int i = 0; i < numToFix; i++) {
               nodesToFix[i].setTimestamp(newKey++);
               channelItemsW.add(nodesToFix[i]);
               BrowserDaemonRegistry.broadCastEvent(152, nodesToFix[i]);
            }
         }
      }
   }

   public static final void channelProperties(ChannelModel channel) {
      String[] choice = new String[]{CommonResources.getString(117)};
      String title = CommonResources.getString(2006);
      String description = BrowserResources.getString(514);
      String url = BrowserResources.getString(277);
      String id = BrowserResources.getString(348);
      Dialog channelDialog = new Dialog(
         title + channel.getTitle() + "\n\n" + description + channel.getDescription() + "\n\n" + url + channel.getURL() + "\n\n" + id + channel.getID(),
         choice,
         null,
         0,
         null
      );
      channelDialog.doModal();
   }

   public static final void moveChannel(ChannelModel node, int amount) {
      if (amount != 0) {
         synchronized (FolderHierarchies.getLockObject()) {
            int keyToMove = (int)node.getTimestamp();
            WritableSet channelsItemsW = (WritableSet)FolderHierarchies.getFolder(
                  BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
               )
               .getContainedItems();
            ReadableList channelsItemsR = (ReadableList)channelsItemsW;
            int numItems = channelsItemsR.size();
            int newPosition = keyToMove + amount;
            if (newPosition < 0) {
               newPosition = 0;
            } else if (newPosition >= numItems) {
               newPosition = numItems - 1;
            }

            if (newPosition != keyToMove) {
               int numToFix = newPosition - keyToMove;
               if (numToFix < 0) {
                  numToFix = keyToMove - newPosition;
               }

               channelsItemsW.remove(node);
               ChannelModel[] nodesToFix = new ChannelModel[numToFix];
               channelsItemsR.getAt(Math.min(keyToMove, newPosition), numToFix, nodesToFix, 0);

               for (int i = numToFix - 1; i >= 0; i--) {
                  channelsItemsW.remove(nodesToFix[i]);
               }

               int keyChange = amount > 0 ? -1 : 1;

               for (int i = 0; i < numToFix; i++) {
                  nodesToFix[i].setTimestamp(nodesToFix[i].getTimestamp() + keyChange);
                  channelsItemsW.add(nodesToFix[i]);
                  BrowserDaemonRegistry.broadCastEvent(152, nodesToFix[i]);
               }

               node.setTimestamp(newPosition);
               channelsItemsW.add(node);
               BrowserDaemonRegistry.broadCastEvent(152, node);
            }
         }
      }
   }

   private static final ChannelModel getChannel(String id) {
      ChannelModel channel = null;
      synchronized (FolderHierarchies.getLockObject()) {
         SortedCollection channels = (SortedCollection)FolderHierarchies.getFolder(
               BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
            )
            .getContainedItems();

         for (int i = 0; i < channels.size(); i++) {
            ChannelModel c = (ChannelModel)channels.getAt(i);
            if (c.getID().equals(id)) {
               channel = c;
               break;
            }
         }

         return channel;
      }
   }

   public static final int getChannelCount() {
      ReadableList channels = (ReadableList)FolderHierarchies.getFolder(
            BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
         )
         .getContainedItems();
      return channels.size();
   }
}
