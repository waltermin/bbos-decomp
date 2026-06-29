package net.rim.device.apps.internal.securitymonitor;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.apps.internal.itadmin.DelayedWipeManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;

final class SecurityMonitor$SecurityMonitorImpl implements SystemListener, GlobalEventListener, RealtimeClockListener, DelayedWipeManager {
   private Security _security = Security.getInstance();
   private String _randomString = new String(RandomSource.getBytes(32));
   private String LOCK_WIPE = "lock";
   private String IT_POLICY_WIPE = "itpolicy";
   private String DELAYED_WIPE = "delayed";
   private boolean _lowBatteryWipe;
   private long _itPolicyWipeDelay;
   private long _lockWipeDelay;
   private int _lockCounter;
   private int _unlockCounter;
   private long _itPolicyTimestamp;
   private ApplicationDescriptor _itPolicyWipeApplicationDescriptor;
   private ApplicationDescriptor _lockWipeApplicationDescriptor;
   private ApplicationDescriptor _delayedWipeApplicationDescriptor;
   private long _nextTick;
   private PersistentObject _persistentObject;
   private SecurityMonitor$SecurityMonitorImpl$Data _data;
   private static final long ONE_MINUTE = 60000L;
   private static final long ONE_HOUR = 3600000L;

   private final void checkTimers(String timerName, String randomString) {
      if (timerName != null && this._randomString.equals(randomString)) {
         if (timerName.equals(this.LOCK_WIPE)) {
            this.checkLockWipeTimer();
         } else if (timerName.equals(this.IT_POLICY_WIPE)) {
            this.checkITPolicyWipeTimer();
         } else {
            if (timerName.equals(this.DELAYED_WIPE)) {
               this.checkDelayedWipeTimer();
            }
         }
      }
   }

   private SecurityMonitor$SecurityMonitorImpl() {
      ApplicationDescriptor ad = ApplicationDescriptor.currentApplicationDescriptor();
      this._itPolicyWipeApplicationDescriptor = new ApplicationDescriptor(ad, new String[]{this.IT_POLICY_WIPE, this._randomString});
      this._lockWipeApplicationDescriptor = new ApplicationDescriptor(ad, new String[]{this.LOCK_WIPE, this._randomString});
      this._delayedWipeApplicationDescriptor = new ApplicationDescriptor(ad, new String[]{this.DELAYED_WIPE, this._randomString});
      this._itPolicyWipeApplicationDescriptor.setPowerOnBehavior(3);
      this._lockWipeApplicationDescriptor.setPowerOnBehavior(3);
      this._delayedWipeApplicationDescriptor.setPowerOnBehavior(3);
      this._persistentObject = RIMPersistentStore.getPersistentObject(4540117967283091590L);
      Object data = this._persistentObject.getContents();
      if (data instanceof SecurityMonitor$SecurityMonitorImpl$Data) {
         this._data = (SecurityMonitor$SecurityMonitorImpl$Data)data;
      } else if (data == null) {
         this._data = new SecurityMonitor$SecurityMonitorImpl$Data(null);
         this._persistentObject.setContents(this._data, 51);
         this._persistentObject.commit();
      } else {
         this.wipe("data tampering detected");
      }

      this.clockUpdated();
      this.loadITPolicySettings();
      this._lockCounter = this._security.getLockCounter();
      this._unlockCounter = this._security.getUnlockCounter();
      this._itPolicyTimestamp = ITPolicyInternal.getProcessedTimeStamp();
      this.restartWipeTimers();
      Proxy proxy = Proxy.getInstance();
      proxy.addSystemListener(this);
      proxy.addGlobalEventListener(this);
      proxy.addRealtimeClockListener(this);
   }

   private final void loadITPolicySettings() {
      this._lowBatteryWipe = ITPolicy.getBoolean(24, 69, false);
      this._itPolicyWipeDelay = (long)ITPolicy.getInteger(24, 70, -1) * 3600000;
      this._lockWipeDelay = (long)ITPolicy.getInteger(24, 71, -1) * 3600000;
      if (this._itPolicyWipeDelay == 0) {
         this._itPolicyWipeDelay = 300000;
      }

      if (this._lockWipeDelay == 0) {
         this._lockWipeDelay = 300000;
      }

      this._security.setAutoOnRequired(this._lockWipeDelay > 0 || this._itPolicyWipeDelay > 0 || this._data._delayedWipeDelay > 0);
   }

   private final void restartTimer(ApplicationDescriptor ad, long startTime, long delay) {
      if (startTime > 0 && delay > 0 && !ApplicationManager.getApplicationManager().scheduleApplication(ad, startTime + delay, true)) {
         this.wipe("unable to start timer");
      }
   }

   private final void restartLockWipeTimer() {
      this.restartTimer(this._lockWipeApplicationDescriptor, this._data._lockWipeStartTime, this._lockWipeDelay);
   }

   private final void restartITPolicyWipeTimer() {
      this.restartTimer(this._itPolicyWipeApplicationDescriptor, this._data._itPolicyWipeStartTime, this._itPolicyWipeDelay);
   }

   private final void restartDelayedWipeTimer() {
      this.restartTimer(this._delayedWipeApplicationDescriptor, this._data._delayedWipeStartTime, this._data._delayedWipeDelay);
   }

   private final void restartWipeTimers() {
      this.restartLockWipeTimer();
      this.restartITPolicyWipeTimer();
      this.restartDelayedWipeTimer();
   }

   private final void checkLockWipeTimer() {
      if (this._lockWipeDelay > 0
         && this._data._lockWipeStartTime > 0
         && this._data._lockWipeStartTime + this._lockWipeDelay < System.currentTimeMillis() + 60000
         && this._unlockCounter == this._security.getUnlockCounter()
         && this._security.isPasswordEnabled()) {
         this.wipe(this.LOCK_WIPE);
      }
   }

   private final void checkITPolicyWipeTimer() {
      if (this._itPolicyWipeDelay > 0
         && this._data._itPolicyWipeStartTime > 0
         && this._data._itPolicyWipeStartTime + this._itPolicyWipeDelay < System.currentTimeMillis() + 60000
         && this._itPolicyTimestamp == ITPolicyInternal.getProcessedTimeStamp()) {
         this.wipe(this.IT_POLICY_WIPE);
      }
   }

   private final void checkDelayedWipeTimer() {
      if (this._data._delayedWipeDelay > 0
         && this._data._delayedWipeStartTime > 0
         && this._data._delayedWipeStartTime + this._data._delayedWipeStartTime < System.currentTimeMillis() + 60000
         && this._unlockCounter == this._security.getUnlockCounter()) {
         this.wipe(this.DELAYED_WIPE);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 8508406279413621091L && guid != -594020114676189989L) {
         if (guid == 8877632280522743328L) {
            this.dateTimeChanged();
            return;
         }

         if (guid == -7131874474196788121L) {
            int lockCounter = this._security.getLockCounter();
            if (this._lockCounter != lockCounter) {
               this._lockCounter = lockCounter;
               this._unlockCounter = this._security.getUnlockCounter();
               this.setLockWipeStartTime();
               this.restartLockWipeTimer();
            }
         }
      } else {
         long itPolicyTimestamp = ITPolicyInternal.getProcessedTimeStamp();
         if (this._itPolicyTimestamp != itPolicyTimestamp) {
            this._itPolicyTimestamp = itPolicyTimestamp;
            this.loadITPolicySettings();
            this.setITPolicyWipeStartTime();
            this.restartITPolicyWipeTimer();
            this.restartLockWipeTimer();
            return;
         }
      }
   }

   @Override
   public final boolean startDelayedWipe(long delay) {
      this.setDelayedWipeStartTime(delay);
      this._security.setAutoOnRequired(this._lockWipeDelay > 0 || this._itPolicyWipeDelay > 0 || this._data._delayedWipeDelay > 0);
      this.restartDelayedWipeTimer();
      return true;
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
      this._lockCounter = this._security.getLockCounter();
      this._unlockCounter = this._security.getUnlockCounter();
      this._itPolicyTimestamp = ITPolicyInternal.getProcessedTimeStamp();
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
      if ((status & -2147483648) != 0 && this._lowBatteryWipe) {
         Backlight.enable(false);
         this.wipe("dead battery");
      }
   }

   private final void setLockWipeStartTime() {
      this._data._lockWipeStartTime = System.currentTimeMillis();
      this._persistentObject.commit();
   }

   private final void setITPolicyWipeStartTime() {
      this._data._itPolicyWipeStartTime = System.currentTimeMillis();
      this._persistentObject.commit();
   }

   private final void setDelayedWipeStartTime(long delay) {
      this._data._delayedWipeStartTime = System.currentTimeMillis();
      this._data._delayedWipeDelay = delay;
      this._persistentObject.commit();
   }

   @Override
   public final void clockUpdated() {
      this._nextTick = System.currentTimeMillis() + 60000;
   }

   private final void dateTimeChanged() {
      long now = System.currentTimeMillis();
      long delta = now - this._nextTick;
      this.adjustWipeStartTimes(delta);
      this.restartWipeTimers();
   }

   private final void adjustWipeStartTimes(long delta) {
      this._data._lockWipeStartTime += delta;
      this._data._itPolicyWipeStartTime += delta;
      this._data._delayedWipeStartTime += delta;
      this._persistentObject.commit();
   }

   private final void wipe(String reason) {
      System.out.println("SecurityMonitor: wiping device after " + reason);
      if (ITPolicy.getBoolean(24, 77, false) && ITPolicyInternal.isITAdminEnabled()) {
         NvStore.resetToFactoryDefaults();
         CodeModuleManager.deleteThirdPartyApplications();
      }

      this._security.deviceUnderAttack();
   }

   SecurityMonitor$SecurityMonitorImpl(SecurityMonitor$1 x0) {
      this();
   }
}
