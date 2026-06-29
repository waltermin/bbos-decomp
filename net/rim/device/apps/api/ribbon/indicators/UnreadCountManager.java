package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.utility.props.StringProps;

public class UnreadCountManager implements TestPoint {
   private IntHashtable _unreadCounts = new IntHashtable();
   private IndicatorManager _indicatorManager;
   public static final int TEXT = 1;
   public static final int TEXT_HIDE_FILED = 2;
   public static final int VOICE = 3;
   public static final int SMS = 4;
   public static final int MMS = 5;
   public static final int BROWSER = 6;
   public static final int ANONYMOUS = 7;
   public static final int CALENDAR = 8;
   public static final int TASK = 9;
   public static final int IM = 10;
   public static final int SMSMMS = 11;
   private static final long UNREAD_COUNT_MANAGER = -683099695629948826L;
   private static UnreadCountManager _unreadCountManager;
   private static UnreadCountManager$CountOptions countOptions;

   private UnreadCountManager(IndicatorManager im) {
      this._indicatorManager = im;
   }

   public static int incrementUnreadCount(int unreadCountUid) {
      return incrementUnreadCount(unreadCountUid, false, true);
   }

   public static int incrementUnreadCount(int unreadCountUid, boolean updateNewCount, boolean updateUnreadCount) {
      return modifyUnreadCount(unreadCountUid, 1, updateNewCount, updateUnreadCount);
   }

   public static int decrementUnreadCount(int unreadCountUid) {
      return decrementUnreadCount(unreadCountUid, false, true);
   }

   public static int decrementUnreadCount(int unreadCountUid, boolean updateNewCount, boolean updateUnreadCount) {
      return modifyUnreadCount(unreadCountUid, -1, updateNewCount, updateUnreadCount);
   }

   public static int modifyUnreadCount(int unreadCountUid, int byHowMany) {
      return _unreadCountManager.internalModifyUnreadCount(unreadCountUid, byHowMany, false, true);
   }

   public static int modifyUnreadCount(int unreadCountUid, int byHowMany, boolean updateNewCount, boolean updateUnreadCount) {
      return _unreadCountManager.internalModifyUnreadCount(unreadCountUid, byHowMany, updateNewCount, updateUnreadCount);
   }

   public static int getUnreadCount(int unreadCountUid) {
      UnreadCount unreadCount = _unreadCountManager.internalGetUnreadCount(unreadCountUid);
      return unreadCount == null ? 0 : unreadCount.getCount();
   }

   public static UnreadCount getUnreadCountObject(int unreadCountUid) {
      return _unreadCountManager.internalGetUnreadCount(unreadCountUid);
   }

   private UnreadCount internalGetUnreadCount(int unreadCountUid) {
      UnreadCount unreadCount = (UnreadCount)this._unreadCounts.get(unreadCountUid);
      if (unreadCount == null) {
         boolean visible = true;
         boolean isMessagingCount = false;
         int counterID;
         int priority;
         switch (unreadCountUid) {
            case 0:
               return null;
            case 1:
            default:
               counterID = 0;
               priority = 2;
               isMessagingCount = true;
               break;
            case 2:
               counterID = 1;
               priority = 2;
               isMessagingCount = true;
               break;
            case 3:
               counterID = 2;
               priority = 4;
               break;
            case 4:
               counterID = 6;
               priority = 2;
               visible = false;
               isMessagingCount = true;
               break;
            case 5:
               counterID = 6;
               priority = 2;
               visible = false;
               isMessagingCount = true;
               break;
            case 6:
               counterID = 3;
               priority = 8;
               break;
            case 7:
               counterID = 7;
               priority = 4;
               isMessagingCount = true;
               break;
            case 8:
               counterID = 4;
               priority = 7;
               break;
            case 9:
               counterID = 8;
               priority = 10;
               break;
            case 10:
               counterID = 5;
               priority = 6;
               break;
            case 11:
               counterID = 9;
               priority = 2;
               visible = false;
               isMessagingCount = true;
         }

         unreadCount = isMessagingCount ? new UnreadCount$NewAndUnreadCount(counterID, priority) : new UnreadCount(counterID, priority);
         this.internalRegisterNewUnreadCount(unreadCountUid, unreadCount);
         if (!visible) {
            unreadCount.setVisible(false);
         }

         unreadCount = (UnreadCount)this._unreadCounts.get(unreadCountUid);
      }

      return unreadCount;
   }

   private void updateUnreadCount(UnreadCount unreadCount) {
      if (this._indicatorManager != null) {
         this._indicatorManager.updateIndicators();
      }
   }

   private int internalModifyUnreadCount(int unreadCountUid, int byHowMany, boolean updateNewCount, boolean updateUnreadCount) {
      UnreadCount unreadCount = this.internalGetUnreadCount(unreadCountUid);
      if (unreadCount == null) {
         return 0;
      }

      if (unreadCount._mergeWithText) {
         this.internalModifyUnreadCount(1, byHowMany, updateNewCount, updateUnreadCount);
         this.internalModifyUnreadCount(2, byHowMany, updateNewCount, updateUnreadCount);
      }

      int newCount = 0;
      if (!(unreadCount instanceof UnreadCount$NewAndUnreadCount)) {
         newCount = unreadCount.modifyUnreadCount(byHowMany);
      } else {
         newCount = ((UnreadCount$NewAndUnreadCount)unreadCount)
            .modifyUnreadCount(byHowMany, updateNewCount, updateUnreadCount, !this._indicatorManager.isIndicatorUpdatingSuspended());
      }

      this.updateUnreadCount(unreadCount);
      return newCount;
   }

   public static void setUnreadCountVisible(int unreadCountUid, boolean visible) {
      _unreadCountManager.internalSetUnreadCountVisible(unreadCountUid, visible);
   }

   private void internalSetUnreadCountVisible(int unreadCountUid, boolean visible) {
      UnreadCount unreadCount = this.internalGetUnreadCount(unreadCountUid);
      if (unreadCount != null) {
         unreadCount.setVisible(visible);
         this.updateUnreadCount(unreadCount);
      }
   }

   public static void registerNewUnreadCount(int unreadCountUid, UnreadCount unreadCount) {
      _unreadCountManager.internalRegisterNewUnreadCount(unreadCountUid, unreadCount);
   }

   private void internalRegisterNewUnreadCount(int unreadCountUid, UnreadCount unreadCount) {
      if (this._indicatorManager != null) {
         synchronized (this._unreadCounts) {
            if (!this._unreadCounts.containsKey(unreadCountUid)) {
               this._unreadCounts.put(unreadCountUid, unreadCount);
               this._indicatorManager.addIndicator(unreadCount);
            }
         }
      }
   }

   public static void deregisterNewUnreadCount(int unreadCountUid) {
      _unreadCountManager.internalDeregisterNewUnreadCount(unreadCountUid);
   }

   private void internalDeregisterNewUnreadCount(int unreadCountUid) {
      if (this._indicatorManager != null) {
         synchronized (this._unreadCounts) {
            UnreadCount unreadCount = (UnreadCount)this._unreadCounts.remove(unreadCountUid);
            if (unreadCount != null) {
               this._indicatorManager.removeIndicator(unreadCount);
            }
         }
      }
   }

   public static void setAction(int unreadCountUid, StringProps action) {
      UnreadCount unreadCount = _unreadCountManager.internalGetUnreadCount(unreadCountUid);
      if (unreadCount != null) {
         unreadCount._action = action;
      }
   }

   public static void mergeWithText(int unreadCountUid, boolean mergeWithText) {
      UnreadCount unreadCount = _unreadCountManager.internalGetUnreadCount(unreadCountUid);
      if (unreadCount != null) {
         unreadCount._mergeWithText = mergeWithText;
      }
   }

   @Override
   public void test(Object id, Object value) {
      int unreadCountUid = 0;
      if (id instanceof Integer) {
         unreadCountUid = (Integer)id;
         int newCount = 0;
         if (value instanceof Integer) {
            newCount = (Integer)value;
            int oldCount = getUnreadCount(unreadCountUid);
            modifyUnreadCount(unreadCountUid, newCount - oldCount);
         }
      }
   }

   public static UnreadCountManager$CountOptions getCountOptions() {
      if (countOptions == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         countOptions = (UnreadCountManager$CountOptions)ar.getOrWaitFor(7056244251443120672L);
         if (countOptions == null) {
            throw new RuntimeException("Could not retrieve messsage options");
         }
      }

      return countOptions;
   }

   static {
      IndicatorManager im = IndicatorManager.getInstance();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _unreadCountManager = (UnreadCountManager)ar.getOrWaitFor(-683099695629948826L);
      if (_unreadCountManager == null) {
         _unreadCountManager = new UnreadCountManager(im);
         ar.put(-683099695629948826L, _unreadCountManager);
      }
   }
}
