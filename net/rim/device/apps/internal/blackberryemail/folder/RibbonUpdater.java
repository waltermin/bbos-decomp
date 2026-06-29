package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager$CountOptions;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.WeakReference;

public final class RibbonUpdater {
   private LongHashtable _unreadCounts = new LongHashtable(2);
   private static final long INSTANCE_GUID = 483921869676550299L;
   private static final String MESSAGING_MODULE_NAME = "net_rim_bb_messaging_app";
   private static RibbonUpdater _instance;
   private static WeakReference _stringBufferWR = new WeakReference(null);
   private static final MessageListOptions _messageListOptions;
   private static final UnreadCountManager$CountOptions _countOptions;

   private RibbonUpdater() {
   }

   public static final void handleServiceRecordAdded(ServiceRecord sr) {
      if (sr != null) {
         if (sr.getType() == 0) {
            Proxy.getInstance().invokeRunnable(new RibbonUpdater$RibbonUpdaterEvent(1, sr));
         }
      }
   }

   public static final void handleServiceRecordRemoved(ServiceRecord sr) {
      if (sr != null) {
         Proxy.getInstance().invokeRunnable(new RibbonUpdater$RibbonUpdaterEvent(2, sr));
      }
   }

   public static final void handleMessageListOptionsChanged() {
      Proxy.getInstance().invokeRunnable(new RibbonUpdater$RibbonUpdaterEvent(3, null));
   }

   public static final void handleThemeChanged() {
      handleRefreshRibbonEntryPoints();
   }

   public static final void handleDeviceInitialization() {
      IconFetcher.deleteUnusedIcons();
      handleRefreshRibbonEntryPoints();
   }

   private static final void handleRefreshRibbonEntryPoints() {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("CMIME");

      for (int i = 0; i < records.length; i++) {
         handleServiceRecordAdded(records[i]);
      }
   }

   private final RibbonUpdater$UnreadCount getUnreadCount(EmailHierarchy emailHierarchy) {
      RibbonUpdater$UnreadCount unreadCount = (RibbonUpdater$UnreadCount)this._unreadCounts.get(emailHierarchy.getLUID());
      if (unreadCount == null) {
         unreadCount = new RibbonUpdater$UnreadCount(null);
         this._unreadCounts.put(emailHierarchy.getLUID(), unreadCount);
      }

      return unreadCount;
   }

   public static final void updateUnreadCount(
      EmailHierarchy emailHierarchy,
      boolean updateCentralUnreadCounts,
      boolean increment,
      boolean updateShowFiled,
      boolean updateHideFiled,
      boolean updateNewCount,
      boolean updateUnreadCount
   ) {
      _instance.internalUpdateUnreadCount(
         emailHierarchy, updateCentralUnreadCounts, increment, updateShowFiled, updateHideFiled, updateNewCount, updateUnreadCount
      );
   }

   public static final void updateUnreadCount(
      EmailHierarchy emailHierarchy,
      boolean updateCentralUnreadCounts,
      boolean increment,
      boolean updateShowFiled,
      boolean updateHideFiled,
      boolean updateNewCount
   ) {
      _instance.internalUpdateUnreadCount(emailHierarchy, updateCentralUnreadCounts, increment, updateShowFiled, updateHideFiled, updateNewCount, true);
   }

   private final void internalUpdateUnreadCount(
      EmailHierarchy emailHierarchy,
      boolean updateCentralUnreadCounts,
      boolean increment,
      boolean updateShowFiled,
      boolean updateHideFiled,
      boolean updateNewCount,
      boolean updateUnreadCount
   ) {
      if (updateCentralUnreadCounts) {
         if (updateShowFiled) {
            if (increment) {
               UnreadCountManager.incrementUnreadCount(1, updateNewCount, updateUnreadCount);
            } else {
               UnreadCountManager.decrementUnreadCount(1, updateNewCount, updateUnreadCount);
            }
         }

         if (updateHideFiled) {
            if (increment) {
               UnreadCountManager.incrementUnreadCount(2, updateNewCount, updateUnreadCount);
            } else {
               UnreadCountManager.decrementUnreadCount(2, updateNewCount, updateUnreadCount);
            }
         }
      }

      if (emailHierarchy != null) {
         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            RibbonUpdater$UnreadCount unreadCount;
            ServiceRecord sr;
            synchronized (this._unreadCounts) {
               unreadCount = this.getUnreadCount(emailHierarchy);
               if (updateHideFiled) {
                  if (updateUnreadCount) {
                     unreadCount._hideFiledUnreadCount += increment ? 1 : -1;
                  }

                  if (updateNewCount) {
                     unreadCount._hideFiledNewCount += increment ? 1 : -1;
                  }
               }

               if (updateShowFiled) {
                  if (updateUnreadCount) {
                     unreadCount._showFiledUnreadCount += increment ? 1 : -1;
                  }

                  if (updateNewCount) {
                     unreadCount._showFiledNewCount += increment ? 1 : -1;
                  }
               }

               if (_messageListOptions.getFlag(16) ? !updateHideFiled : !updateShowFiled) {
                  return;
               }

               if (unreadCount._srId == -1) {
                  sr = ServiceBook.getSB()
                     .getRecordByCidAndUserId(
                        "CMIME", emailHierarchy.getServiceUserId(), emailHierarchy.getServiceNameHash(), emailHierarchy.getServiceUidHash()
                     );
                  if (sr != null) {
                     unreadCount._srId = sr.getId();
                  }
               } else {
                  sr = ServiceBook.getSB().getRecordById(unreadCount._srId);
               }

               if (sr == null) {
                  return;
               }
            }

            this.internalUpdateRibbonEntryPointDescription(ribbon, sr, emailHierarchy, null, unreadCount);
         }
      }
   }

   private static final void updateRibbonEntryPointDescription(
      RibbonLauncher ribbon, ServiceRecord sr, EmailHierarchy emailHierarchy, ApplicationEntryPoint entryPoint, RibbonUpdater$UnreadCount unreadCount
   ) {
      _instance.internalUpdateRibbonEntryPointDescription(ribbon, sr, emailHierarchy, entryPoint, unreadCount);
   }

   private final void internalUpdateRibbonEntryPointDescription(
      RibbonLauncher ribbon, ServiceRecord sr, EmailHierarchy emailHierarchy, ApplicationEntryPoint entryPoint, RibbonUpdater$UnreadCount unreadCount
   ) {
      String id = null;
      if (entryPoint == null) {
         id = "net_rim_bb_messaging_app" + sr.getId();
         entryPoint = (ApplicationEntryPoint)ribbon.getRegisteredAction(id);
         if (entryPoint == null) {
            return;
         }
      }

      int newUnreadCount = 0;
      boolean displayCount = true;
      synchronized (this._unreadCounts) {
         if (unreadCount == null) {
            unreadCount = this.getUnreadCount(emailHierarchy);
         }

         int displayOption = _messageListOptions.getDisplayMessageCount();
         if (1 == displayOption) {
            newUnreadCount = _messageListOptions.getFlag(16) ? unreadCount._hideFiledUnreadCount : unreadCount._showFiledUnreadCount;
         } else {
            displayCount = false;
         }
      }

      String description = displayCount ? null : "";
      StringBuffer sb = WeakReferenceUtilities.getStringBuffer(_stringBufferWR);
      synchronized (sb) {
         if (displayCount && newUnreadCount != 0) {
            sb.append('(').append(newUnreadCount).append(')').append(' ');
         }

         sb.append(sr.getName());
         description = sb.toString();
         sb.setLength(0);
         entryPoint.set(3, description);
      }

      if (displayCount) {
         if (this.displayNewMessageIcon(unreadCount)) {
            entryPoint.set(2, "new");
         } else {
            entryPoint.set(2, (String)null);
         }
      }

      if (id != null) {
         ribbon.updateRegisteredAction(id);
      }
   }

   private final boolean displayNewMessageIcon(RibbonUpdater$UnreadCount count) {
      if (count == null) {
         return false;
      }

      boolean hasNewMessages = _messageListOptions.getFlag(16) ? count._hideFiledNewCount > 0 : count._showFiledNewCount > 0;
      return hasNewMessages && _countOptions.getDisplayNewMessageIndicator();
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (RibbonUpdater)ar.getOrWaitFor(483921869676550299L);
      if (_instance == null) {
         _instance = new RibbonUpdater();
         ar.put(483921869676550299L, _instance);
      }

      _messageListOptions = MessageListOptions.getOptions();
      _countOptions = UnreadCountManager.getCountOptions();
   }
}
