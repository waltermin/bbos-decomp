package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class QuerySSOptions extends SSRequest {
   private int[] _ssOptions;
   private int[] _cspTypesMap;
   private int _cspServiceGroup;
   private int _cspFlags;
   private int _optCount;
   private boolean _checkCspFlags;
   private QuerySSOptions$Listener _listener;
   private int _index;
   static final int SKIP_CSP_CHECK;
   static final int SS_OPTION_NOT_PROVISIONED;

   QuerySSOptions(int[] friendlySSTypes, int[] ssOptions, int cspServiceGroup, int[] cspTypesMap, QuerySSOptions$Listener listener) {
      this._ssOptions = ssOptions;
      this._cspServiceGroup = cspServiceGroup;
      this._cspTypesMap = cspTypesMap;
      this._optCount = this._ssOptions.length;
      if (this._optCount > 1) {
         this.setNumProgressUpdates(this._optCount);
      }

      this._listener = listener;
      switch (RadioInfo.getNetworkType()) {
         case 3:
            break;
         case 4:
         case 5:
         default:
            this._cspServiceGroup = -1;
            this._checkCspFlags = false;
      }

      if (this._cspServiceGroup != -1) {
         try {
            this._cspFlags = SIMCard.getCSPFlags(cspServiceGroup);
            this._checkCspFlags = true;
         } finally {
            this._cspFlags = 0;
            this._checkCspFlags = false;
            return;
         }
      }
   }

   @Override
   public final void start() {
      if (!SSManager.getInstance().simReady()) {
         this._listener.onQueryFinished(new int[]{100, -804651004, 220, 6009}, false);
      } else {
         super.start();
      }
   }

   @Override
   public final synchronized void runTask() {
      int[] ssOptionFlags = new int[this._optCount];
      this._index = 0;

      for (this._index = 0; this._index < this._optCount && !super._aborted; this._index++) {
         boolean doQuery = this._checkCspFlags ? (this._cspFlags & this._cspTypesMap[this._index]) != 0 : true;
         if (doQuery) {
            int ssOption = this._ssOptions[this._index];
            this.querySSOption(ssOption);
            ssOptionFlags[this._index] = this.querySSOptionResult(ssOption);
         }

         this.updateProgress();
      }

      boolean success = !super._aborted;
      this._listener.onQueryFinished(ssOptionFlags, success);
   }

   private final synchronized void querySSOption(int ssOption) {
      try {
         Phone.getInstance().querySSOption(ssOption);
         this.waitForNetworkResponse();
      } finally {
         return;
      }
   }

   private final synchronized int querySSOptionResult(int ssOption) {
      int bearer = 0;
      byte var6;
      switch (PhoneUtilities.getCurrentLineId()) {
         case 0:
            var6 = 0;
            break;
         case 1:
         default:
            var6 = 2;
            break;
         case 2:
            var6 = 11;
      }

      try {
         return Phone.getInstance().querySSOptionResult(ssOption, var6);
      } finally {
         ;
      }
   }
}
