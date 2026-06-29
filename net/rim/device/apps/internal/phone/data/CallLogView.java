package net.rim.device.apps.internal.phone.data;

import java.util.TimeZone;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.WeakRealtimeClockListener;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.WeakReference;

final class CallLogView extends PhoneListView implements CallLogCollection$Listener, MemoryCleanerListener, RealtimeClockListener {
   private CallLogView$CachedCallLogStrings[] _cachedStrings = new CallLogView$CachedCallLogStrings[this._items.getCapacity()];
   private long _lastKnownSystemTime;
   private Font _previousFont;

   CallLogView(Application app) {
      super(app, 2, 0);
      MemoryCleanerDaemon.addWeakListener(this, false);
      app.addRealtimeClockListener(new WeakRealtimeClockListener(this));
   }

   @Override
   protected final String getDeletePrompt(boolean multipleItemsSelected) {
      return multipleItemsSelected ? PhoneResources.getString(6259) : PhoneResources.getString(6258);
   }

   @Override
   public final void getVerbContextFlags(PhoneListItem item, ContextObject context) {
      super.getVerbContextFlags(item, context);
      context.setFlag(49);
      PhoneUtilities.setPrivateFlag(context, 72);
   }

   private final synchronized CallLogView$CachedCallLogStrings getCachedStringsByIndex(PhoneCallModelImpl callLog, int index) {
      if (super._resetCache) {
         for (int i = 0; i < this._cachedStrings.length; i++) {
            this._cachedStrings[i] = null;
         }

         super._resetCache = false;
      }

      if (index >= 0 && index < this._cachedStrings.length && (super._flags & 2) != 0) {
         index = this._cachedStrings.length - index - 1;
      }

      if (this._cachedStrings[index] == null) {
         this._cachedStrings[index] = this.getCachedStringsFromCallLog(callLog);
      }

      return this._cachedStrings[index];
   }

   final String getDateTimeString(PhoneCallModelImpl callLog, int index) {
      return this.getCachedStringsByIndex(callLog, index)._dateTimeString;
   }

   final String getNumberTypeString(PhoneCallModelImpl callLog, int index) {
      return this.getCachedStringsByIndex(callLog, index)._numberTypeString;
   }

   final String getCallerIDString(PhoneCallModelImpl callLog, int index) {
      return this.getCachedStringsByIndex(callLog, index)._callerIDString;
   }

   private final CallLogView$CachedCallLogStrings getCachedStringsFromCallLog(PhoneCallModelImpl callLog) {
      CallLogView$CachedCallLogStrings strings = new CallLogView$CachedCallLogStrings();
      StringBuffer buf = callLog.getDateTimeString(34);
      strings._dateTimeString = buf.toString();
      strings._numberTypeString = callLog.getNumberTypeString();
      strings._callerIDString = callLog.getCallerIDString();
      return strings;
   }

   @Override
   protected final void loadItems() {
      CallLogCollection collection = CallLogCollection.getInstance();
      collection.addListener(new WeakReference(this));
      super._items = collection;
   }

   @Override
   public final boolean showsSpeedDialKeys() {
      return false;
   }

   @Override
   protected final int getDeleteVerbOrdering() {
      return 1332254;
   }

   @Override
   public final void clockUpdated() {
      long currentTime = System.currentTimeMillis();
      if (!DateTimeUtilities.isSameDate(this._lastKnownSystemTime, currentTime, TimeZone.getDefault(), null)) {
         this.resetCache();
      }

      this._lastKnownSystemTime = currentTime;
   }

   @Override
   public final void onEvent(int event, CallLogItem item, int index) {
      boolean updateDisplay = true;
      switch (event) {
         case 2:
         default:
            updateDisplay = false;
         case 0:
         case 1:
         case 3:
         case 5:
         case 6:
            this.resetCache();
         case -1:
         case 4:
            if (updateDisplay) {
               this.updateOnCorrectThread();
            }
      }
   }

   @Override
   public final void onRemovedFromScreen() {
      super._app.removeRealtimeClockListener(this);
   }

   @Override
   protected final ContextObject getPaintingContextObject(PhoneListItem itemToPaint) {
      ContextObject context = super.getPaintingContextObject(itemToPaint);
      ContextObject.put(context, 9045827404276417370L, this);
      PhoneUtilities.setPrivateFlag(context, 72);
      return context;
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      Font font = super._listField.getFont();
      if (font != this._previousFont) {
         this._previousFont = font;
         CallLogItem.calculateAllotments(font);
      }
   }

   @Override
   public final synchronized boolean cleanNow(int event) {
      if (event != 10) {
         return false;
      }

      for (int i = this._cachedStrings.length - 1; i >= 0; i--) {
         this._cachedStrings[i] = null;
      }

      super._resetCache = false;
      return true;
   }

   @Override
   public final String getDescription() {
      return null;
   }
}
