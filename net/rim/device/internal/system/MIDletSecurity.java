package net.rim.device.internal.system;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.MIDletSecurityCrypto;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.vm.Array;
import net.rim.vm.DebugSupport;

public final class MIDletSecurity {
   public static final long MIDLET_UNTRUSTED_POLICY_GUID;
   private static final boolean ENABLE_HTTP_NEVER_DENY_KLUDGE;
   private static final byte DEFAULT_SETTING;
   private static final byte UNKNOWN_SETTING;
   private static final byte ALLOW_SETTING;
   private static final byte DISALLOW_SETTING;
   private static final int MAX_SIG_LEN;
   private static final int MAX_HASH_LEN;
   private static final String CONNECTOR_PREFIX;
   private static final byte[] DEFAULT_MIDLET_PERM_SETTING = new byte[]{
      3, 3, 3, 3, 3, 3, 3, 4, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 4, 4, 4, 4, 1, 1, 1, 3
   };
   private static byte[] _sessionCache;
   private static byte[] _installedCache;
   private static byte[] _domainCache;
   private static Object _userLock = new Object();
   private static boolean _isUntrusted;
   public static final String MIDLET_CERT_PREFIX;

   private MIDletSecurity() {
   }

   private static final void setAllConnectors(byte[] policy, byte setting) {
      String connectorPrefix = "javax.microedition.io.Connector.";

      for (int i = 0; i < 40; i++) {
         if (MIDletSecurityConstants.MIDletPermissions[i].startsWith(connectorPrefix)) {
            policy[i] = setting;
         }
      }
   }

   public static final byte[] ensureDefaultUntrustedPolicyInstalled() {
      byte[] policy = null;
      if (Branding.getVendorId() == 1 || DeviceInfo.isSimulator()) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         policy = (byte[])ar.get(-8029111670665436014L);
         if (policy == null) {
            policy = new byte[40];
            Arrays.fill(policy, (byte)5);
            System.arraycopy(DEFAULT_MIDLET_PERM_SETTING, 0, policy, 0, DEFAULT_MIDLET_PERM_SETTING.length);
            CodeSigningKey rri = CodeSigningKey.getBuiltInKey(51);
            ControlledAccess ca = new ControlledAccess(policy, rri);
            ar.put(-8029111670665436014L, ca);
         }
      }

      return policy;
   }

   private static final void fillInUntrustedPolicy(byte[] policy) {
      _isUntrusted = true;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      byte[] carrierPolicy = (byte[])ar.get(-8029111670665436014L);
      if (carrierPolicy != null) {
         int carrierLen = carrierPolicy.length;
         if (checkValidSettings(carrierPolicy, 0, carrierLen)) {
            System.arraycopy(carrierPolicy, 0, policy, 0, carrierLen);
            return;
         }
      }

      int length = DEFAULT_MIDLET_PERM_SETTING.length > policy.length ? policy.length : DEFAULT_MIDLET_PERM_SETTING.length;
      System.arraycopy(DEFAULT_MIDLET_PERM_SETTING, 0, policy, 0, length);
   }

   private static final int setDomainCache() {
      byte[] policy = new byte[40];
      int status = MIDletSecurityCrypto.verifyMIDletTrailer(policy);
      switch (status) {
         case -1:
         case 2:
            Arrays.fill(policy, (byte)0);
            break;
         case 0:
            byte[] cleansedPolicy = new byte[40];

            for (int i = 0; i < 40; i++) {
               byte setting = 1;
               if (i < policy.length) {
                  byte policySetting = policy[i];
                  switch (policySetting) {
                     case -1:
                     case 5:
                        break;
                     case 0:
                     case 1:
                     case 2:
                     case 3:
                     case 4:
                     case 6:
                     default:
                        setting = policySetting;
                  }
               }

               cleansedPolicy[i] = setting;
            }

            policy = cleansedPolicy;
            break;
         case 1:
         default:
            fillInUntrustedPolicy(policy);
            break;
         case 3:
            Arrays.fill(policy, (byte)6);
      }

      _domainCache = policy;
      return status;
   }

   private static final int getDomainSetting(int perm) {
      if (_domainCache == null) {
         setDomainCache();
      }

      return perm >= 0 && perm < _domainCache.length ? _domainCache[perm] : 0;
   }

   private static final byte[] fetchStoredSettings() {
      byte[] installed = MIDletSecurityCrypto.fetchStoredSettings();
      if (installed == null) {
         installed = new byte[40];
         Arrays.fill(installed, (byte)5);
      }

      _installedCache = installed;
      return installed;
   }

   private static final void updateStoredSettings() {
      MIDletSecurityCrypto.updateStoredSettings(_installedCache);
   }

   private static final synchronized byte[] getSessionCache() {
      if (_sessionCache == null) {
         byte[] installed = fetchStoredSettings();
         _sessionCache = Arrays.copy(installed);
      }

      return _sessionCache;
   }

   private static final int askUser(int perm, int setting, String target) {
      synchronized (_userLock) {
         String parm = DebugSupport.getenv("MIDP20SecurityOverride");
         if (parm != null) {
            return StringUtilities.strEqualIgnoreCase(parm, "No", 1701707776) ? 0 : 6;
         }

         int var10000;
         try {
            int finalperm = perm;
            int finalsetting = setting;
            ApplicationDescriptor finaldescriptor = ApplicationDescriptor.currentApplicationDescriptor();
            MIDletSecurity$PermDialog pd = new MIDletSecurity$PermDialog(finalperm, finalsetting, finaldescriptor, target);
            BackgroundDialog.showOnProxy(pd);
            var10000 = pd._setting;
         } catch (Throwable var10) {
            return 0;
         }

         return var10000;
      }
   }

   private static final void updateForGroup(byte[] settings, int perm, int groupSetting) {
      int groupBeingSet = MIDletSecurityConstants.MIDletPermissionGroups[perm];

      for (int i = settings.length - 1; i >= 0; i--) {
         int group = MIDletSecurityConstants.MIDletPermissionGroups[i];
         if (group == groupBeingSet) {
            settings[i] = (byte)groupSetting;
         }
      }
   }

   public static final void checkMIDletCreation() {
      synchronized (getSessionCache()) {
         int status = setDomainCache();
         if (status == 2) {
            throw new SecurityException();
         }
      }

      if (!MIDletSecurityCrypto.checkDRMTrailer()) {
         throw new SecurityException();
      }
   }

   public static final boolean checkUntrustedMIDlet() {
      if (CodeModuleManager.isMidlet()) {
         checkPermission(0, false, false, false, null);
         if (_isUntrusted) {
            return true;
         }
      }

      return false;
   }

   public static final void checkPermission(int perm) {
      checkPermission(perm, true, true, false, null);
   }

   public static final void checkPermission(int perm, String target) {
      checkPermission(perm, true, true, false, target);
   }

   public static final int checkPermissionNoPrompt(int perm) {
      int setting = checkPermission(perm, false, false, false, null);
      switch (setting) {
         case 0:
            return 0;
         case 6:
            return 1;
         default:
            return -1;
      }
   }

   public static final int checkRealPermissionNoPrompt(int perm) {
      return checkPermission(perm, false, false, true, null);
   }

   private static final int checkPermission(int perm, boolean askUser, boolean throwExc, boolean realValue, String target) {
      int setting = 1;
      byte[] sessionSettings = getSessionCache();
      synchronized (sessionSettings) {
         if (perm >= 0 && perm < sessionSettings.length) {
            int cacheSetting = sessionSettings[perm];
            if (cacheSetting == 5) {
               int domainSetting = getDomainSetting(perm);
               switch (domainSetting) {
                  case 0:
                  case 6:
                     sessionSettings[perm] = (byte)domainSetting;
                  default:
                     if (domainSetting != 5) {
                        setting = domainSetting;
                     }
               }
            } else {
               setting = cacheSetting;
            }
         } else {
            setting = 0;
         }

         if (!askUser && realValue) {
            return setting;
         }

         switch (setting) {
            case 0:
            case 5:
               setting = 0;
               break;
            case 1:
            case 2:
            default:
               if (askUser) {
                  setting = askUser(perm, setting, target);
               } else {
                  setting = 5;
               }
               break;
            case 3:
               if (askUser) {
                  setting = askUser(perm, setting, target);
                  updateForGroup(sessionSettings, perm, setting);
               } else {
                  setting = 5;
               }
               break;
            case 4:
               if (askUser) {
                  setting = askUser(perm, setting, target);
                  updateForGroup(sessionSettings, perm, setting);
                  if (setting == 6) {
                     updateForGroup(_installedCache, perm, setting);
                     updateStoredSettings();
                  }
               } else {
                  setting = 5;
               }
            case 6:
         }
      }

      if (setting == 0 && throwExc) {
         throw new SecurityException(MIDletSecurityConstants.MIDletPermissions[perm]);
      } else {
         return setting;
      }
   }

   public static final void setPermission(int perm, int setting, boolean allowed) {
      byte[] sessionSettings = getSessionCache();
      switch (setting) {
         case 3:
         default:
            updateForGroup(sessionSettings, perm, allowed ? 6 : 0);
            return;
         case 4:
            updateForGroup(sessionSettings, perm, allowed ? 6 : 0);
            if (setting == 6) {
               updateForGroup(_installedCache, perm, allowed ? 6 : 0);
               updateStoredSettings();
            }
         case 2:
      }
   }

   public static final int findPermission(String str) {
      String[] strPerms = MIDletSecurityConstants.MIDletPermissions;

      for (int i = strPerms.length - 1; i >= 0; i--) {
         if (str.equals(strPerms[i])) {
            return i;
         }
      }

      return 40;
   }

   public static final int checkSymbolicPermission(String str) {
      int perm = findPermission(str);
      return perm >= 0 && perm < 40 ? checkPermissionNoPrompt(perm) : 0;
   }

   public static final String getMIDletCertificateTag(int n, int m) {
      StringBuffer buff = new StringBuffer(48);
      buff.append("MIDlet-Certificate-");
      buff.append(n);
      buff.append('-');
      buff.append(m);
      return buff.toString();
   }

   public static final int checkJADCertChain(String[] certs) {
      return MIDletSecurityCrypto.checkJADCertChain(certs);
   }

   public static final Digest getMIDletSignatureDigest() {
      return MIDletSecurityCrypto.getMIDletSignatureDigest();
   }

   public static final int checkMIDletSignature(Digest digest, String rsaSHA1Sig, String[] signingCerts, byte[] refTrailerBytes, byte[] signerCertEncoding) {
      return MIDletSecurityCrypto.checkMIDletSignature(digest, rsaSHA1Sig, signingCerts, refTrailerBytes, signerCertEncoding);
   }

   public static final byte[] genMIDletTrailer(byte[] codfile, byte[] trailerBytes) {
      return MIDletSecurityCrypto.signMIDletTrailer(codfile, trailerBytes);
   }

   private static final boolean checkValidSettings(byte[] policy, int start, int len) {
      if (len != 40) {
         return false;
      }

      while (len > 0) {
         int perm = policy[start] & 255;
         if (perm < 0 || perm >= 7) {
            return false;
         }

         start++;
         len--;
      }

      return true;
   }

   public static final boolean checkDomainPolicy(byte[] policy) {
      int policyLen = policy.length;
      return policyLen != 61 ? false : checkValidSettings(policy, 21, policy[20] & 255);
   }

   public static final void createTrailerFromNvStoreDomain(int field, byte[] policy, byte[] refTrailer) {
      int policyLen = policy[20] & 255;
      if ((policyLen & 3) != 0) {
         throw new IllegalArgumentException();
      }

      Array.resize(refTrailer, policyLen + 4 + 20);
      System.arraycopy(policy, 0, refTrailer, 0, 20);
      refTrailer[20] = (byte)field;
      refTrailer[21] = policy[20];
      refTrailer[22] = 0;
      refTrailer[23] = 0;
      System.arraycopy(policy, 21, refTrailer, 24, policyLen);
   }

   public static final void copyPolicyFromMIDletTrailer(byte[] trailer, byte[] policy) {
      int policyLen = trailer[21] & 255;
      System.arraycopy(trailer, 24, policy, 0, Math.min(policyLen, policy.length));
   }

   private static final int calcBaseDomain(byte[] domainPolicy) {
      int base = 0;
      switch (domainPolicy[20]) {
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 33:
         default:
            base = 24;
         case 24:
         case 31:
         case 32:
            return base;
      }
   }

   public static final byte[] updateDesiredPolicy(byte[] desiredPolicy, byte[] domainPolicy) {
      int desiredLen = desiredPolicy.length;
      if (desiredLen > 40) {
         return null;
      }

      int baseDomain = calcBaseDomain(domainPolicy);
      if (baseDomain == 0) {
         return null;
      }

      int domainLen = domainPolicy.length;
      if (domainLen > baseDomain && domainLen <= baseDomain + 40) {
         byte[] intersection = new byte[baseDomain + 40];
         Arrays.fill(intersection, (byte)0);
         System.arraycopy(domainPolicy, 0, intersection, 0, baseDomain);

         for (int i = 0; i < 40; i++) {
            byte midletPerm = i < desiredLen ? desiredPolicy[i] : 0;
            if (midletPerm != 0) {
               int domainIndex = i + baseDomain;
               byte domainPerm = domainIndex < domainLen ? domainPolicy[domainIndex] : 0;
               byte desiredPerm;
               switch (domainPerm) {
                  case 0:
                     if (midletPerm != 5) {
                        return new byte[]{(byte)i};
                     }

                     desiredPerm = 0;
                     break;
                  default:
                     desiredPerm = domainPerm;
               }

               intersection[baseDomain + i] = desiredPerm;
            }
         }

         return intersection;
      } else {
         return null;
      }
   }

   private static final byte[] cleansePolicy(byte[] policy) {
      int policyLen = policy.length;
      if (policyLen > 0 && policyLen <= 40 && (policyLen & 3) == 0) {
         if (policyLen < 40) {
            byte[] cleansedPolicy = new byte[40];
            Arrays.fill(cleansedPolicy, (byte)0);
            System.arraycopy(policy, 0, cleansedPolicy, 0, policyLen);
            policy = cleansedPolicy;
         }

         return policy;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final void installRootDomain(byte[] sha1, byte[] policy) {
      policy = cleansePolicy(policy);
      int policyLen = policy.length;
      int sha1Len = sha1.length;

      for (int field = 26; field <= 30; field++) {
         byte[] probe = NvStore.readData(field);
         if (probe != null && Arrays.equals(probe, 0, sha1, 0, sha1Len)) {
            if (probe[sha1Len] == policyLen && Arrays.equals(probe, sha1Len + 1, policy, 0, policyLen)) {
               return;
            }

            probe = null;
         }

         if (probe == null) {
            int len = sha1Len + 1 + policyLen;
            byte[] data = new byte[len];
            System.arraycopy(sha1, 0, data, 0, sha1Len);
            data[sha1Len] = (byte)policyLen;
            System.arraycopy(policy, 0, data, sha1Len + 1, policyLen);
            if (!NvStore.writeData(field, data)) {
               throw new RuntimeException("IRDE");
            }

            return;
         }
      }

      throw new RuntimeException("TMRD");
   }

   public static final boolean prepareForRootDomainInstallation() {
      byte[][][] items = new byte[31][][];

      for (int field = 26; field <= 30; field++) {
         byte[] probe = NvStore.readData(field);
         if (probe != null) {
            if (!checkDomainPolicy(probe)) {
               if (!NvStore.deleteData(field)) {
                  return false;
               }
            } else {
               items[field] = (byte[][])probe;
            }
         }
      }

      for (int field = 26; field <= 30; field++) {
         byte[] probe1 = (byte[])items[field];
         if (probe1 != null) {
            for (int check = field + 1; check <= 30; check++) {
               byte[] probe2 = (byte[])items[check];
               if (probe2 != null && Arrays.equals(probe1, probe2)) {
                  if (!NvStore.deleteData(check)) {
                     return false;
                  }

                  items[check] = null;
               }
            }
         }
      }

      for (int field = 26; field <= 30; field++) {
         byte[] probe = (byte[])items[field];
         if (probe == null) {
            for (int check = field + 1; check <= 30; check++) {
               byte[] probe2 = (byte[])items[check];
               if (probe2 != null) {
                  if (!NvStore.writeData(field, probe2)) {
                     return false;
                  }

                  items[field] = (byte[][])probe2;
                  if (!NvStore.deleteData(check)) {
                     return false;
                  }

                  items[check] = null;
                  probe = probe2;
                  break;
               }
            }

            if (probe == null) {
               return true;
            }
         }
      }

      return true;
   }

   public static final byte[] createPolicy() {
      byte[] policy = new byte[40];
      Arrays.fill(policy, (byte)0);
      policy[0] = 6;
      return policy;
   }

   public static final int adjustPolicy(byte[] policy, String permName, boolean optPerm) {
      int perm = findPermission(permName);
      if (perm != 40) {
         if (optPerm) {
            if (policy[perm] != 6) {
               policy[perm] = 5;
               return perm;
            }
         } else {
            policy[perm] = 6;
         }
      }

      return perm;
   }
}
