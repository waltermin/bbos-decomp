package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PasswordDialog;

public class NNEPasswordManager implements PersistentContentListener, RealtimeClockListener, GlobalEventListener {
   private IntHashtable _cache = (IntHashtable)(new Object(2));
   private long _nextTimestamp = -1;
   private static final long INSTANCE_GUID;
   private static NNEPasswordManager _instance;
   private static final int MAX_NNE_PASSWORD_LENGTH;
   private static final int IT_POLICY_PASSWORD_NEVER_EXPIRES;
   private static final int IT_POLICY_PASSWORD_TIMEOUT_DEFAULT;
   private static final int INVALID_TIME;

   private NNEPasswordManager() {
   }

   @Override
   public void clockUpdated() {
      if (!this._cache.isEmpty()) {
         int timeout = getPasswordTimeoutMillis();
         if (timeout == 0) {
            this._nextTimestamp = -1;
            this._cache = (IntHashtable)(new Object(2));
         } else if (timeout != -1) {
            if (timeout > 0 && this._nextTimestamp + timeout < System.currentTimeMillis()) {
               this.purgeExpiredPasswords();
            }
         }
      }
   }

   private void purgeExpiredPasswords() {
      int timeout = getPasswordTimeoutMillis();
      if (timeout >= 0) {
         long expiredTime = System.currentTimeMillis() - timeout;
         this._nextTimestamp = -1;
         int[] expiredKeys = new int[0];
         synchronized (this._cache) {
            IntEnumeration e = this._cache.keys();

            while (e.hasMoreElements()) {
               int key = e.nextElement();
               NNEPasswordManager$NNEPassword password = (NNEPasswordManager$NNEPassword)this._cache.get(key);
               if (password != null) {
                  if (password._timestamp < expiredTime) {
                     Arrays.add(expiredKeys, key);
                  } else if (this._nextTimestamp == -1 || password._timestamp < this._nextTimestamp) {
                     this._nextTimestamp = password._timestamp;
                  }
               }
            }

            for (int i = 0; i < expiredKeys.length; i++) {
               this._cache.remove(expiredKeys[i]);
            }
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this._cache = (IntHashtable)(new Object(2));
         this._nextTimestamp = -1;
      }
   }

   public static boolean isPasswordRequired(EmailMessageModel message) {
      boolean passwordRequired = false;
      if (message == null || message.flagsSet(8192)) {
         return false;
      }

      if (message.isNNE()) {
         return true;
      }

      Object payload = SubmemberUtilities.getFirstSubmember(message, EmailPayloadRecognizer.getInstance());
      if (payload instanceof EmailPayloadModel && ((EmailPayloadModel)payload).isNNE()) {
         passwordRequired = true;
      }

      return passwordRequired;
   }

   public static Object confirmEncodedPassword(EmailMessageModel message) {
      switch (message.getType()) {
         case 1:
         case 2:
         case 4:
         case 8:
         case 16:
            if (Dialog.ask(3, EmailResources.getString(108), -1) != 4) {
               return null;
            }
         default:
            try {
               return getEncodedPassword(EmailMessageUtilities.getServiceRecordForMessage(message));
            } finally {
               ;
            }
      }
   }

   private static Object getEncodedPassword(ServiceRecord serviceRecord) {
      if (serviceRecord == null) {
         throw new Object();
      }

      NNEPasswordManager$NNEPassword cached = getCachedEncodedPassword(serviceRecord);
      if (cached != null) {
         return cached._encodedPassword;
      }

      String prompt = MessageFormat.format(EmailResources.getString(206), new Object[]{serviceRecord.getName()});
      PasswordDialog passwordDialog = (PasswordDialog)(new Object(prompt, false, 32, 134217728));
      BackgroundDialog.show(passwordDialog);
      if (passwordDialog.getCloseReason() == -1) {
         return null;
      }

      Object password = PersistentContent.encode((String)(new Object(passwordDialog.getPassword())));
      if (getPasswordTimeoutMillis() != 0) {
         long timestamp = System.currentTimeMillis();
         _instance._cache.put(serviceRecord.getId(), new NNEPasswordManager$NNEPassword(timestamp, password));
         if (_instance._nextTimestamp == -1 || timestamp < _instance._nextTimestamp) {
            _instance._nextTimestamp = timestamp;
         }
      }

      return password;
   }

   private static NNEPasswordManager$NNEPassword getCachedEncodedPassword(ServiceRecord serviceRecord) {
      return serviceRecord == null ? null : (NNEPasswordManager$NNEPassword)_instance._cache.get(serviceRecord.getId());
   }

   public static void clearCachedPassword(ServiceRecord serviceRecord) {
      if (serviceRecord != null) {
         _instance._cache.remove(serviceRecord.getId());
      }
   }

   private static int getPasswordTimeoutMillis() {
      short value = (short)ITPolicy.getInteger(23, 10, -1);
      return value == -1 ? value : value * 60 * 1000;
   }

   @Override
   public synchronized void persistentContentStateChanged(int state) {
   }

   @Override
   public synchronized void persistentContentModeChanged(int generation) {
      IntEnumeration e = this._cache.keys();

      while (e.hasMoreElements()) {
         int key = e.nextElement();
         NNEPasswordManager$NNEPassword nnePassword = (NNEPasswordManager$NNEPassword)this._cache.get(key);
         nnePassword._encodedPassword = PersistentContent.reEncode(nnePassword._encodedPassword);
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (NNEPasswordManager)ar.getOrWaitFor(7962629012429714450L);
      if (_instance == null) {
         _instance = new NNEPasswordManager();
         ar.put(7962629012429714450L, _instance);
         PersistentContent.addWeakListener(_instance);
         Proxy.getInstance().addRealtimeClockListener(_instance);
         Proxy.getInstance().addGlobalEventListener(_instance);
      }
   }
}
