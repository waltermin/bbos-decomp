package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAKeyPair;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.pgp.PGPArmorDecoder;
import net.rim.device.api.crypto.pgp.PGPArmorEncoder;
import net.rim.device.api.crypto.pgp.PGPKeyEncoder;
import net.rim.device.api.crypto.pgp.PGPPrivateKey;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.xml.XMLHashtable;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethodFactory;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.device.apps.internal.blackberryemail.sendmethods.SendMethodSelector;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailLookupFailureCache;
import net.rim.device.apps.internal.secureemail.encodings.SecureEmailEncodingManager;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPResources;
import net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods.PGPSecureEmailServerDefaultSendMethodFactory;
import net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy.GranularPolicy;
import net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy.GranularPolicyAction;
import net.rim.device.apps.internal.secureemail.server.SecureEmailCertificateServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;
import net.rim.device.internal.crypto.pgp.PGPPrivateKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleOKCancelInputDialog;
import net.rim.device.internal.ui.component.UsernamePasswordDialog;
import net.rim.ecmascript.regexp.RegExp;
import net.rim.ecmascript.regexp.RegExp$MatchResult;
import net.rim.vm.Array;

public class PGPUniversalServer implements SecureEmailPolicyServer, SecureEmailCertificateServer, GlobalEventListener, VerbProvider {
   private ResourceBundleFamily _rb = ResourceBundle.getBundle(-4585341889014701753L, "net.rim.device.apps.internal.resource.secureemail.PGP");
   private String _baseURL;
   private String[] _serviceUIDs;
   private PGPUniversalServerSOAPHandler _soapHandler;
   private PGPUniversalKeyCache _pgpUniversalKeyCache;
   private SecureEmailLookupFailureCache _failedFetchesByEmail;
   private SecureEmailLookupFailureCache _failedFetchesByID;
   private int _policyType = 0;
   private XMLHashtable _policy;
   private GranularPolicy _granularPolicy;
   private long _lastPolicyUpdate;
   private Object _initializeLock;
   private boolean _initializeInProgress;
   private long _lastEnrollmentRequestTimeStamp;
   private Vector _enrollmentPendingDialogs;
   private SendMethodFactory _sendMethodFactory;
   private int _authenticationState;
   private static final long PERSISTED_COOKIES_ID = 3499486419240605781L;
   private static PersistentObject _persistedCookieHashtable;
   private static Hashtable _cookieHashtable;
   private static final long[] ENCODING_UIDS = new long[]{
      -742709496102783169L,
      3681505275764314063L,
      5942148136637320404L,
      182808770805039415L,
      278367830017L,
      8503078133287878657L,
      7741536031146863471L,
      -4200449977755808667L,
      7567174288028533760L,
      -8624397353558676449L,
      8237197472314032243L,
      9113357136763027615L,
      4856013397946796032L,
      576587756053358890L,
      8081718327528987432L,
      821863123462976319L,
      8482260963811657728L,
      8749380752460218469L,
      4744727354991668805L,
      -8563241713844760974L,
      4918221066291319145L,
      8331503661213828683L,
      32476727737387008L,
      4788304288347341064L,
      7262858560579912515L,
      8522341497298223276L,
      8088755203631303818L,
      8102047232267832688L,
      576510910670095053L,
      -8491689650216209343L,
      28544882803393117L,
      6730182323398263048L
   };
   private static Object _persistentObjectLock = new Object();
   private static final int POLICY_TYPE_LEGACY = 0;
   private static final int POLICY_TYPE_GRANULAR = 1;
   private static final long ENROLLMENT_RETRY_WINDOW = 300000L;
   private static final int STATE_NOT_ENROLLED = 0;
   private static final int STATE_ENROLLMENT_PENDING = 1;
   private static final int STATE_NOT_AUTHENTICATED = 2;
   private static final int STATE_AUTHENTICATED = 3;
   private static final int ENROLLMENT_FAILED = 0;
   private static final int ENROLLMENT_COMPLETE = 1;
   private static final int ENROLLMENT_PENDING = 2;
   private static final int AUTHENTICATE_FAILED = 0;
   private static final int AUTHENTICATE_COMPLETE = 1;
   private static final int AUTHENTICATE_RE_ENROLL = 2;
   private static final int DOWNLOAD_KEYS_FAILED = 0;
   private static final int DOWNLOAD_KEYS_COMPLETE = 1;
   private static final int DOWNLOAD_KEYS_NO_PRIVATE_KEY = 2;

   public String[] getServiceUIDs() {
      return this._serviceUIDs;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Array.resize(verbs, 0);
      String authenticationCookie = this.getAuthenticationCookie();
      if (authenticationCookie == null) {
         Arrays.add(verbs, new PGPUniversalServer$UniversalServerVerb(this, 0, 1638480, this._rb, 8070, null));
      } else if (!this._soapHandler.isAuthenticated()) {
         Arrays.add(verbs, new PGPUniversalServer$UniversalServerVerb(this, 1, 1638496, this._rb, 8071, authenticationCookie));
      } else {
         Arrays.add(verbs, new PGPUniversalServer$UniversalServerVerb(this, 2, 1638512, this._rb, 8072, null));
         Arrays.add(verbs, new PGPUniversalServer$UniversalServerVerb(this, 3, 1638528, this._rb, 8108, null));
      }

      return verbs[0];
   }

   public void uninitialize() {
      this._authenticationState = 0;
      SendMethodSelector.getInstance().unregisterSendMethodFactory(this._sendMethodFactory);
      Proxy.getInstance().removeGlobalEventListener(this);
   }

   public String getURL() {
      return this._baseURL;
   }

   @Override
   public long getPreferredEncodingUID(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord, RecipientData[] recipientData, int encodingAction) {
      if ((encodingAction & 2) != 0) {
         return this.determinePreferredEncryptionEncodingUID(recipientData);
      } else {
         return (encodingAction & 1) != 0
            ? this.determinePreferredSigningEncodingUID(emailMessageModel, serviceRecord, recipientData, encodingAction)
            : 182808770805039415L;
      }
   }

   @Override
   public boolean isPolicyUpdateRequired() {
      long policyUpdateInterval = ITPolicy.getInteger(26, 10, 24) * 60 * 60 * 1000;
      return this._policyType == 0 && this._policy == null
         || this._policyType == 1 && this._granularPolicy == null
         || System.currentTimeMillis() - this._lastPolicyUpdate > policyUpdateInterval;
   }

   @Override
   public synchronized boolean updatePolicy(SecureEmailServerOperationListener param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: sipush 8109
      // 05: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.updateListener (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;I)V
      // 08: aload 0
      // 09: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.isPolicyUpdateRequired ()Z
      // 0c: ifne 11
      // 0f: bipush 1
      // 10: ireturn
      // 11: sipush 8111
      // 14: istore 2
      // 15: bipush 0
      // 16: istore 3
      // 17: aload 0
      // 18: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._policyType I
      // 1b: tableswitch 25 -1 1 93 25 57
      // 34: aload 0
      // 35: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._soapHandler Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler;
      // 38: aload 1
      // 39: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler.getPolicy (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)Lnet/rim/device/api/xml/XMLHashtable;
      // 3c: astore 4
      // 3e: aload 4
      // 40: ldc_w "/GetPolicyResponse"
      // 43: invokevirtual net/rim/device/api/xml/XMLHashtable.isPresent (Ljava/lang/String;)Z
      // 46: ifeq 78
      // 49: aload 0
      // 4a: aload 4
      // 4c: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._policy Lnet/rim/device/api/xml/XMLHashtable;
      // 4f: bipush 1
      // 50: istore 3
      // 51: goto 78
      // 54: aload 0
      // 55: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._soapHandler Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler;
      // 58: aload 1
      // 59: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler.getGranularPolicy (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)Lnet/rim/device/api/xml/XMLHashtable;
      // 5c: astore 5
      // 5e: aload 5
      // 60: ldc_w "/GetGranularPolicyResponse"
      // 63: invokevirtual net/rim/device/api/xml/XMLHashtable.isPresent (Ljava/lang/String;)Z
      // 66: ifeq 78
      // 69: aload 0
      // 6a: new net/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy
      // 6d: dup
      // 6e: aload 5
      // 70: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy.<init> (Lnet/rim/device/api/xml/XMLHashtable;)V
      // 73: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._granularPolicy Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy;
      // 76: bipush 1
      // 77: istore 3
      // 78: iload 3
      // 79: ifeq 8e
      // 7c: aload 0
      // 7d: invokestatic java/lang/System.currentTimeMillis ()J
      // 80: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._lastPolicyUpdate J
      // 83: bipush 1
      // 84: ireturn
      // 85: astore 3
      // 86: sipush 8110
      // 89: istore 2
      // 8a: goto 8e
      // 8d: astore 3
      // 8e: aload 1
      // 8f: ifnull 99
      // 92: iload 2
      // 93: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 96: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 99: bipush 0
      // 9a: ireturn
      // try (11 -> 54): 55 null
      // try (11 -> 54): 59 null
   }

   @Override
   public String getServerName() {
      return PGPResources.getString(8073);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Certificate getCertificateByEmailAddress(String emailAddress, int encodingActions, SecureEmailServerOperationListener listener) {
      Certificate cachedKey = this._pgpUniversalKeyCache.getCachedKey(emailAddress);
      if (cachedKey != null) {
         return cachedKey;
      }

      if (this._failedFetchesByEmail.checkFetchFailure(emailAddress)) {
         return null;
      }

      if (!this.internalInitialize(null)) {
         return null;
      }

      this.updateListener(listener, 8089);

      try {
         XMLHashtable getKeyByEmailResponse = this._soapHandler.getKeyByEmail(emailAddress, listener);
         if (getKeyByEmailResponse.getBoolean("/GetKeyByEmailResponse/excluded", false)) {
            String signSetting = getKeyByEmailResponse.getString("/GetKeyByEmailResponse/sign", null);
            int newEncodingActions;
            if (StringUtilities.strEqualIgnoreCase(signSetting, "true", 1701707776)) {
               newEncodingActions = 1;
            } else if (StringUtilities.strEqualIgnoreCase(signSetting, "false", 1701707776)) {
               newEncodingActions = 0;
            } else {
               newEncodingActions = encodingActions & -3;
            }

            throw new Object(newEncodingActions);
         } else {
            String keyBlock = getKeyByEmailResponse.getString("/GetKeyByEmailResponse/keyblock");
            if (keyBlock != null && keyBlock.length() != 0) {
               ByteArrayInputStream keyBlockInputStream = (ByteArrayInputStream)(new Object(keyBlock.getBytes()));
               PGPArmorDecoder pgpArmorDecoder = (PGPArmorDecoder)(new Object(keyBlockInputStream));
               int numCertificates = pgpArmorDecoder.numCertificates();
               if (numCertificates <= 0) {
                  this._failedFetchesByEmail.recordFetchFailure(emailAddress);
                  return null;
               } else {
                  PGPCertificate fetchedKey = pgpArmorDecoder.getCertificate(0);
                  if (fetchedKey == null) {
                     this._failedFetchesByEmail.recordFetchFailure(emailAddress);
                     return null;
                  } else {
                     this._pgpUniversalKeyCache.cacheKey(fetchedKey, 0);
                     return fetchedKey;
                  }
               }
            } else {
               this._failedFetchesByEmail.recordFetchFailure(emailAddress);
               return null;
            }
         }
      } catch (Throwable var12) {
         EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
         this._failedFetchesByEmail.recordFetchFailure(emailAddress);
         return null;
      }
   }

   @Override
   public Certificate getCertificateByCertificateID(Object certificateID, SecureEmailServerOperationListener listener) {
      return this.getCertificateByCertificateID(certificateID, null, listener);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Certificate getCertificateByCertificateID(Object certificateID, String emailAddress, SecureEmailServerOperationListener listener) {
      byte[] keyIDBytes = (byte[])certificateID;
      Certificate cachedKey = this._pgpUniversalKeyCache.getCachedKey(keyIDBytes, emailAddress);
      if (cachedKey != null) {
         return cachedKey;
      }

      String keyIDString = PGPUtilities.binaryToHexASCIIString(keyIDBytes);
      if (this._failedFetchesByID.checkFetchFailure(keyIDString)) {
         return null;
      }

      if (!this.internalInitialize(null)) {
         return null;
      }

      this.updateListener(listener, 8089);

      try {
         XMLHashtable getKeyByIDResponse;
         if (emailAddress != null) {
            getKeyByIDResponse = this._soapHandler.getKeyByID(keyIDString, emailAddress, listener);
         } else {
            getKeyByIDResponse = this._soapHandler.getKeyByID(keyIDString, listener);
         }

         String keyBlock = getKeyByIDResponse.getString("/GetKeyByKeyIDResponse/keyblock");
         if (keyBlock != null && keyBlock.length() != 0) {
            ByteArrayInputStream keyBlockInputStream = (ByteArrayInputStream)(new Object(keyBlock.getBytes()));
            PGPArmorDecoder pgpArmorDecoder = (PGPArmorDecoder)(new Object(keyBlockInputStream));
            int numCertificates = pgpArmorDecoder.numCertificates();
            if (numCertificates <= 0) {
               this._failedFetchesByID.recordFetchFailure(keyIDString);
               return null;
            }

            PGPCertificate fetchedCertificate = pgpArmorDecoder.getCertificate(0);
            if (fetchedCertificate == null) {
               this._failedFetchesByID.recordFetchFailure(keyIDString);
               return null;
            }

            int certificateProperties = 0;
            if (!getKeyByIDResponse.getBoolean("/GetKeyByKeyIDResponse/valid", false)) {
               certificateProperties |= 1;
            }

            if (getKeyByIDResponse.getBoolean("/GetKeyByKeyIDResponse/dont-cache", true)) {
               certificateProperties |= 2;
            }

            this._pgpUniversalKeyCache.cacheKey(fetchedCertificate, certificateProperties);
            return fetchedCertificate;
         } else {
            this._failedFetchesByID.recordFetchFailure(keyIDString);
            return null;
         }
      } catch (Throwable var15) {
         EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
         this._failedFetchesByID.recordFetchFailure(keyIDString);
         return null;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Long getCertificateProperties(Certificate certificate, long date) {
      if (!this.internalInitialize(null)) {
         return null;
      }

      int cachedKeyProperties = this._pgpUniversalKeyCache.getCachedKeyProperties(certificate);
      if (cachedKeyProperties >= 0) {
         long certificateProperties = 0;
         if ((cachedKeyProperties & 1) != 0) {
            certificateProperties |= 8;
         }

         return (Long)(new Object(certificateProperties));
      } else {
         if (!(certificate instanceof Object)) {
            return null;
         }

         X509Certificate x509Certificate = (X509Certificate)certificate;

         try {
            label79: {
               ByteArrayOutputStream encodedCertificateStream = (ByteArrayOutputStream)(new Object());
               Base64OutputStream base64OutputStream = (Base64OutputStream)(new Object(encodedCertificateStream, true, true));
               base64OutputStream.write(x509Certificate.getEncoding());
               base64OutputStream.close();
               StringBuffer encodedCertificateStringBuffer = (StringBuffer)(new Object());
               encodedCertificateStringBuffer.append("-----BEGIN CERTIFICATE-----\r\n");
               encodedCertificateStringBuffer.append((String)(new Object(encodedCertificateStream.toByteArray())));
               encodedCertificateStringBuffer.append("-----END CERTIFICATE-----\r\n");
               XMLHashtable getCertificateChainResponse = this._soapHandler.getCertificateChain(encodedCertificateStringBuffer.toString(), null);
               String keyBlock = getCertificateChainResponse.getString("/GetCertificateChainResponse/keyblock");
               if (keyBlock != null && keyBlock.length() != 0) {
                  cachedKeyProperties = 0;
                  ByteArrayInputStream keyBlockInputStream = (ByteArrayInputStream)(new Object(keyBlock.getBytes()));
                  PGPArmorDecoder pgpArmorDecoder = (PGPArmorDecoder)(new Object(keyBlockInputStream));
                  int numCertificates = pgpArmorDecoder.numCertificates();
                  if (numCertificates <= 1) {
                     cachedKeyProperties |= 1;
                  }

                  this._pgpUniversalKeyCache.cacheKey(x509Certificate, cachedKeyProperties);
                  break label79;
               }

               return null;
            }
         } catch (Throwable var15) {
            EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
            return null;
         }
      }
   }

   @Override
   public long[] getEncodingUIDs() {
      return ENCODING_UIDS;
   }

   @Override
   public boolean providesCertificatesForService(ServiceRecord serviceRecord) {
      return this.appliesToService(serviceRecord);
   }

   @Override
   public boolean providesPolicyForService(ServiceRecord serviceRecord) {
      return this.appliesToService(serviceRecord);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this.clearCache();
      }
   }

   @Override
   public int[] getEncodingActions(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord, RecipientData[] recipientData, int userSelectedActions) {
      if ((this._policyType != 0 || this._policy != null) && (this._policyType != 1 || this._granularPolicy != null)) {
         int defaultActions = userSelectedActions;
         boolean keyNotFoundBounce = false;
         boolean nonExcludedKeyFound = false;
         int keyExcludedActions = 0;
         String emailMessageSubject = this.extractSubject(emailMessageModel);
         int emailMessageSensitivity = emailMessageModel.getSensitivity();

         for (RecipientData currentRecipientData : recipientData) {
            String[] currentRecipientAddresses = currentRecipientData.getAddresses();
            int numCurrentRecipientAddresses = currentRecipientAddresses != null ? currentRecipientAddresses.length : 0;
            if (currentRecipientData.isExcluded()) {
               keyExcludedActions |= currentRecipientData.getNewEncodingAction();
            } else {
               nonExcludedKeyFound = true;

               for (int j = 0; j < numCurrentRecipientAddresses; j++) {
                  switch (this._policyType) {
                     case -1:
                        break;
                     case 0:
                     default:
                        int policyDomainIndex = this.getPolicyDomainIndex(this._policy, currentRecipientAddresses[j]);
                        if (policyDomainIndex >= 0) {
                           String signLookupKey;
                           String encryptLookupKey;
                           String knfLookupKey;
                           if (this.usePolicyOverrideSettings(emailMessageSubject, emailMessageSensitivity, policyDomainIndex)) {
                              signLookupKey = "/GetPolicyResponse/policy-item/sign-override";
                              encryptLookupKey = "/GetPolicyResponse/policy-item/encrypt-override";
                              knfLookupKey = "/GetPolicyResponse/policy-item/knf-override";
                           } else {
                              signLookupKey = "/GetPolicyResponse/policy-item/sign";
                              encryptLookupKey = "/GetPolicyResponse/policy-item/encrypt";
                              knfLookupKey = "/GetPolicyResponse/policy-item/keynotfound";
                           }

                           if (this._policy.getBooleanAt(signLookupKey, policyDomainIndex, false)) {
                              defaultActions |= 1;
                           }

                           if (this._policy.getBooleanAt(encryptLookupKey, policyDomainIndex, false)) {
                              defaultActions |= 2;
                              String knfAction = this._policy.getStringAt(knfLookupKey, policyDomainIndex);
                              if (StringUtilities.strEqualIgnoreCase(knfAction, "bounce", 1701707776)) {
                                 keyNotFoundBounce = true;
                              }
                           }
                        }
                        break;
                     case 1:
                        GranularPolicyAction granularPolicyAction = this._granularPolicy
                           .determineAction(currentRecipientAddresses[j], serviceRecord, emailMessageModel);
                        if (granularPolicyAction != null) {
                           switch (granularPolicyAction.getOperation()) {
                              case 0:
                                 defaultActions |= granularPolicyAction.getRequiredActions();
                                 String knfAction = granularPolicyAction.getKeyNotFound();
                                 if (StringUtilities.strEqualIgnoreCase(knfAction, "bounce", 1701707776)) {
                                    keyNotFoundBounce = true;
                                 }
                                 break;
                              case 1:
                              case 9:
                                 BackgroundDialog.showMessage("Your secure email policy prevents you from sending this message.");
                                 throw new Object();
                           }
                        }
                  }
               }
            }
         }

         int alternateActions = defaultActions;
         if ((defaultActions & 2) != 0) {
            if (!keyNotFoundBounce) {
               if (nonExcludedKeyFound) {
                  alternateActions = 0;
               } else {
                  defaultActions = keyExcludedActions;
                  alternateActions = defaultActions;
               }
            }
         } else if (!nonExcludedKeyFound) {
            defaultActions = keyExcludedActions;
            alternateActions = defaultActions;
         }

         return new int[]{defaultActions, alternateActions};
      } else {
         throw new Object();
      }
   }

   @Override
   public boolean isInitialized() {
      return this._authenticationState == 3;
   }

   @Override
   public boolean initialize(SecureEmailServerOperationListener listener) {
      return this.initialize(listener, true);
   }

   private void removeAuthenticationCookie() {
      Object ticket = this.waitForPersistentContentTicket();
      ticket.hashCode();
      this._authenticationState = 0;
      getCookieHashtable().remove(this.getURL());
      _persistedCookieHashtable.commit();
   }

   private Object waitForPersistentContentTicket() {
      return PersistentContent.waitForTicket();
   }

   private long determinePreferredSigningEncodingUID(
      EmailMessageModel emailMessageModel, ServiceRecord serviceRecord, RecipientData[] recipientData, int encodingAction
   ) {
      if ((this._policyType != 0 || this._policy != null) && (this._policyType != 1 || this._granularPolicy != null)) {
         SecureEmailEncodingManager secureEmailEncodingManager = SecureEmailEncodingManager.getInstance();
         long preferredEncodingUID = -1;
         int preferredEncodingPriority = -1;

         for (RecipientData currentRecipientData : recipientData) {
            String[] currentRecipientAddresses = currentRecipientData.getAddresses();
            int numCurrentRecipientAddresses = currentRecipientAddresses != null ? currentRecipientAddresses.length : 0;

            for (int j = 0; j < numCurrentRecipientAddresses; j++) {
               switch (this._policyType) {
                  case -1:
                     break;
                  case 0:
                  default:
                     int policyDomainIndex = this.getPolicyDomainIndex(this._policy, currentRecipientAddresses[j]);
                     if (policyDomainIndex >= 0) {
                        String currentEncodingString = this._policy.getStringAt("/GetPolicyResponse/policy-item/signformat", policyDomainIndex);
                        long currentEncodingUID = this.encodingStringToEncodingUID(currentEncodingString);
                        int currentEncodingPriority = secureEmailEncodingManager.getEncodingPriority(currentEncodingUID);
                        if (currentEncodingPriority > preferredEncodingPriority) {
                           preferredEncodingUID = currentEncodingUID;
                           preferredEncodingPriority = currentEncodingPriority;
                        }
                     }
                     break;
                  case 1:
                     GranularPolicyAction granularPolicyAction = this._granularPolicy
                        .determineAction(currentRecipientAddresses[j], serviceRecord, emailMessageModel);
                     if (granularPolicyAction != null) {
                        switch (granularPolicyAction.getOperation()) {
                           case 0:
                              String currentEncodingString = granularPolicyAction.getEncodingFormat();
                              long currentEncodingUID = this.encodingStringToEncodingUID(currentEncodingString);
                              int currentEncodingPriority = secureEmailEncodingManager.getEncodingPriority(currentEncodingUID);
                              if (currentEncodingPriority > preferredEncodingPriority) {
                                 preferredEncodingUID = currentEncodingUID;
                                 preferredEncodingPriority = currentEncodingPriority;
                              }
                        }
                     }
               }
            }
         }

         return preferredEncodingUID;
      } else {
         throw new Object();
      }
   }

   private boolean internalInitialize(SecureEmailServerOperationListener listener) {
      if (!this.initialize(listener, false)) {
         if (this._authenticationState != 0 && this._authenticationState != 1) {
            return false;
         } else {
            throw new Object("Must enroll with the universal server");
         }
      } else {
         return true;
      }
   }

   private long encodingStringToEncodingUID(String encodingString) {
      if (StringUtilities.strEqualIgnoreCase(encodingString, "openpgp", 1701707776)
         || StringUtilities.strEqualIgnoreCase(encodingString, "pgp_partitioned", 1701707776)
         || StringUtilities.strEqualIgnoreCase(encodingString, "pgp_mime", 1701707776)) {
         return 3681505275764314063L;
      } else {
         return !StringUtilities.strEqualIgnoreCase(encodingString, "smime", 1701707776)
               && !StringUtilities.strEqualIgnoreCase(encodingString, "s_mime", 1701707776)
            ? -1
            : 5942148136637320404L;
      }
   }

   private boolean initialize(SecureEmailServerOperationListener param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.isInitialized ()Z
      // 004: ifeq 009
      // 007: bipush 1
      // 008: ireturn
      // 009: bipush 0
      // 00a: istore 3
      // 00b: aload 0
      // 00c: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 00f: dup
      // 010: astore 4
      // 012: monitorenter
      // 013: aload 0
      // 014: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeInProgress Z
      // 017: ifne 021
      // 01a: aload 0
      // 01b: bipush 1
      // 01c: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeInProgress Z
      // 01f: bipush 1
      // 020: istore 3
      // 021: aload 4
      // 023: monitorexit
      // 024: goto 02f
      // 027: astore 5
      // 029: aload 4
      // 02b: monitorexit
      // 02c: aload 5
      // 02e: athrow
      // 02f: sipush 8083
      // 032: istore 4
      // 034: iload 3
      // 035: ifne 03b
      // 038: goto 11a
      // 03b: aload 0
      // 03c: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.getAuthenticationCookie ()Ljava/lang/String;
      // 03f: astore 5
      // 041: aload 5
      // 043: ifnonnull 096
      // 046: bipush -1
      // 048: istore 4
      // 04a: aload 0
      // 04b: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._authenticationState I
      // 04e: bipush 1
      // 04f: if_icmpne 08e
      // 052: aload 0
      // 053: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._lastEnrollmentRequestTimeStamp J
      // 056: ldc_w 300000
      // 059: i2l
      // 05a: ladd
      // 05b: invokestatic java/lang/System.currentTimeMillis ()J
      // 05e: lcmp
      // 05f: ifle 08e
      // 062: iload 2
      // 063: ifne 08e
      // 066: bipush 0
      // 067: istore 6
      // 069: aload 0
      // 06a: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 06d: dup
      // 06e: astore 7
      // 070: monitorenter
      // 071: aload 0
      // 072: bipush 0
      // 073: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeInProgress Z
      // 076: aload 0
      // 077: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 07a: invokevirtual java/lang/Object.notifyAll ()V
      // 07d: aload 7
      // 07f: monitorexit
      // 080: goto 08b
      // 083: astore 8
      // 085: aload 7
      // 087: monitorexit
      // 088: aload 8
      // 08a: athrow
      // 08b: iload 6
      // 08d: ireturn
      // 08e: aload 0
      // 08f: aload 1
      // 090: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.promptToEnroll (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)V
      // 093: goto 0d1
      // 096: aload 0
      // 097: bipush 2
      // 099: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._authenticationState I
      // 09c: aload 0
      // 09d: aload 5
      // 09f: aload 1
      // 0a0: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.authenticate (Ljava/lang/String;Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)I
      // 0a3: istore 6
      // 0a5: iload 6
      // 0a7: bipush 2
      // 0a9: if_icmpne 0b8
      // 0ac: bipush -1
      // 0ae: istore 4
      // 0b0: aload 0
      // 0b1: aload 1
      // 0b2: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.promptToEnroll (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)V
      // 0b5: goto 0d1
      // 0b8: iload 6
      // 0ba: bipush 1
      // 0bb: if_icmpne 0d1
      // 0be: bipush -1
      // 0c0: istore 4
      // 0c2: goto 0d1
      // 0c5: astore 6
      // 0c7: sipush 8110
      // 0ca: istore 4
      // 0cc: goto 0d1
      // 0cf: astore 6
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 0d5: dup
      // 0d6: astore 5
      // 0d8: monitorenter
      // 0d9: aload 0
      // 0da: bipush 0
      // 0db: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeInProgress Z
      // 0de: aload 0
      // 0df: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 0e2: invokevirtual java/lang/Object.notifyAll ()V
      // 0e5: aload 5
      // 0e7: monitorexit
      // 0e8: goto 140
      // 0eb: astore 9
      // 0ed: aload 5
      // 0ef: monitorexit
      // 0f0: aload 9
      // 0f2: athrow
      // 0f3: astore 10
      // 0f5: aload 0
      // 0f6: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 0f9: dup
      // 0fa: astore 11
      // 0fc: monitorenter
      // 0fd: aload 0
      // 0fe: bipush 0
      // 0ff: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeInProgress Z
      // 102: aload 0
      // 103: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._initializeLock Ljava/lang/Object;
      // 106: invokevirtual java/lang/Object.notifyAll ()V
      // 109: aload 11
      // 10b: monitorexit
      // 10c: goto 117
      // 10f: astore 12
      // 111: aload 11
      // 113: monitorexit
      // 114: aload 12
      // 116: athrow
      // 117: aload 10
      // 119: athrow
      // 11a: aload 1
      // 11b: ifnull 140
      // 11e: new net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$WaitForInitialization
      // 121: dup
      // 122: aload 0
      // 123: aconst_null
      // 124: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$WaitForInitialization.<init> (Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer;Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$1;)V
      // 127: aload 1
      // 128: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$WaitForInitialization.doWait (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)V
      // 12b: aload 0
      // 12c: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._authenticationState I
      // 12f: bipush 3
      // 131: if_icmpeq 13c
      // 134: aload 0
      // 135: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._authenticationState I
      // 138: bipush 1
      // 139: if_icmpne 140
      // 13c: bipush -1
      // 13e: istore 4
      // 140: aload 1
      // 141: ifnull 153
      // 144: iload 4
      // 146: bipush -1
      // 148: if_icmpeq 153
      // 14b: iload 4
      // 14d: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 150: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 153: aload 0
      // 154: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._authenticationState I
      // 157: bipush 3
      // 159: if_icmpne 15e
      // 15c: bipush 1
      // 15d: ireturn
      // 15e: bipush 0
      // 15f: ireturn
      // try (12 -> 22): 23 null
      // try (23 -> 26): 23 null
      // try (61 -> 69): 70 null
      // try (70 -> 73): 70 null
      // try (84 -> 103): 104 null
      // try (84 -> 103): 108 null
      // try (114 -> 122): 123 null
      // try (123 -> 126): 123 null
      // try (33 -> 56): 128 null
      // try (77 -> 109): 128 null
      // try (134 -> 142): 143 null
      // try (143 -> 146): 143 null
      // try (128 -> 129): 128 null
   }

   public PGPUniversalServer(String url, String[] serviceUIDs) {
      this._baseURL = url;
      this._serviceUIDs = serviceUIDs;
      this._soapHandler = new PGPUniversalServerSOAPHandler(url, serviceUIDs);
      this._pgpUniversalKeyCache = PGPUniversalKeyCache.createInstance();
      this._failedFetchesByEmail = (SecureEmailLookupFailureCache)(new Object(3600000));
      this._failedFetchesByID = (SecureEmailLookupFailureCache)(new Object(3600000));
      this._initializeLock = new Object();
      this._enrollmentPendingDialogs = (Vector)(new Object());
      String authenticationCookie = this.getAuthenticationCookie();
      if (authenticationCookie != null) {
         this._authenticationState = 2;
      }

      this._sendMethodFactory = new PGPSecureEmailServerDefaultSendMethodFactory();
      SendMethodSelector.getInstance().registerSendMethodFactory(this._sendMethodFactory);
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private void updateListener(SecureEmailServerOperationListener listener, int messageResourceID) {
      if (listener != null) {
         listener.updateServerOperationProgress(PGPResources.getString(messageResourceID));
      }
   }

   private long certificateToEncodingUID(Certificate certificate) {
      PGPCertificate pgpCertificate = (PGPCertificate)certificate;
      return pgpCertificate.containsEmbeddedX509Certificates() && pgpCertificate.isPreExistingCertificate() ? 5942148136637320404L : 3681505275764314063L;
   }

   private void putAuthenticationCookie(String cookie) {
      Object ticket = this.waitForPersistentContentTicket();
      ticket.hashCode();
      getCookieHashtable().put(this.getURL(), cookie);
      _persistedCookieHashtable.commit();
      this._authenticationState = 3;
   }

   private boolean appliesToService(ServiceRecord serviceRecord) {
      if (serviceRecord == null) {
         return false;
      }

      int numServiceUIDs = this._serviceUIDs.length;
      if (numServiceUIDs == 0) {
         return true;
      }

      String serviceUIDToTest = serviceRecord.getUid();

      for (int i = 0; i < numServiceUIDs; i++) {
         if (StringUtilities.strEqualIgnoreCase(this._serviceUIDs[i], serviceUIDToTest, 1701707776)) {
            return true;
         }
      }

      return false;
   }

   private long determinePreferredEncryptionEncodingUID(RecipientData[] recipientData) {
      SecureEmailEncodingManager secureEmailEncodingManager = SecureEmailEncodingManager.getInstance();
      long preferredEncodingUID = -1;
      int preferredEncodingPriority = -1;

      for (RecipientData currentRecipientData : recipientData) {
         RecipientData$CertificateDetails[] currentRecipientCertificates = currentRecipientData.getRecommendedCertificates();
         int numCurrentRecipientCertificates = currentRecipientCertificates != null ? currentRecipientCertificates.length : 0;

         for (int j = 0; j < numCurrentRecipientCertificates; j++) {
            long currentEncodingUID = this.certificateToEncodingUID(currentRecipientCertificates[j].getCertificate());
            int currentEncodingPriority = secureEmailEncodingManager.getEncodingPriority(currentEncodingUID);
            if (currentEncodingPriority > preferredEncodingPriority) {
               preferredEncodingUID = currentEncodingUID;
               preferredEncodingPriority = currentEncodingPriority;
            }
         }
      }

      return preferredEncodingUID;
   }

   private void clearCache() {
      this._pgpUniversalKeyCache.clear();
      this._failedFetchesByEmail.clear();
      this._failedFetchesByID.clear();
      this._lastPolicyUpdate = 0;
   }

   private String[] extractRecipientAddresses(EmailMessageModel emailMessageModel) {
      String[] recipientAddresses = new Object[0];
      Object[] messageRecipients = SubmemberUtilities.getSubmembers(emailMessageModel, new PGPUniversalServer$RecipientRecognizer());
      int numMessageRecipients = messageRecipients.length;

      for (int i = 0; i < numMessageRecipients; i++) {
         EmailHeaderModel emailHeaderModel = (EmailHeaderModel)messageRecipients[i];
         if (!emailHeaderModel.isBlank()) {
            String[] addressAndName = new Object[2];
            if (emailHeaderModel.convert(null, addressAndName)) {
               int lastAddressIndex = addressAndName.length - 2;

               for (int j = 0; j <= lastAddressIndex; j += 2) {
                  Arrays.add(recipientAddresses, addressAndName[j]);
               }
            }
         }
      }

      return recipientAddresses;
   }

   private String extractSubject(EmailMessageModel emailMessageModel) {
      int numSubModels = emailMessageModel.size();

      for (int i = 0; i < numSubModels; i++) {
         Object currentSubModel = emailMessageModel.getAt(i);
         if (currentSubModel instanceof Object) {
            return ((SubjectModel)currentSubModel).getSubject();
         }
      }

      return null;
   }

   private int getPolicyDomainIndex(XMLHashtable currentPolicy, String recipientEmailAddress) {
      int defaultIndex = -1;
      int longestMatchingDomainIndex = -1;
      int longestMatchingDomainLength = -1;
      int numDomains = currentPolicy.getNumValues("/GetPolicyResponse/policy-item");

      for (int i = 0; i < numDomains; i++) {
         if (defaultIndex == -1 && currentPolicy.getBooleanAt("/GetPolicyResponse/policy-item/default", i, false)) {
            defaultIndex = i;
         } else {
            String domain = currentPolicy.getStringAt("/GetPolicyResponse/policy-item/domain", i);
            if (domain != null && StringUtilities.toLowerCase(recipientEmailAddress, 1701707776).endsWith(StringUtilities.toLowerCase(domain, 1701707776))) {
               int domainLength = domain.length();
               if (domainLength > longestMatchingDomainLength) {
                  longestMatchingDomainLength = domainLength;
                  longestMatchingDomainIndex = i;
               }
            }
         }
      }

      return longestMatchingDomainIndex >= 0 ? longestMatchingDomainIndex : defaultIndex;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean usePolicyOverrideSettings(String emailMessageSubject, int messageSensitivity, int policyDomainIndex) {
      if (messageSensitivity == 4 && this._policy.getBooleanAt("/GetPolicyResponse/policy-item/override-on-confidential", policyDomainIndex, false)) {
         return true;
      }

      if (emailMessageSubject != null && this._policy.getBooleanAt("/GetPolicyResponse/policy-item/override-on-subject", policyDomainIndex, false)) {
         String overrideSubject = this._policy.getStringAt("/GetPolicyResponse/policy-item/override-subject", policyDomainIndex, null);
         if (overrideSubject == null) {
            return false;
         }

         try {
            RegExp subjectRegExp = (RegExp)(new Object(overrideSubject, true));
            RegExp$MatchResult subjectRegExpMatchResult = subjectRegExp.match(emailMessageSubject, 0);
            return subjectRegExpMatchResult != null;
         } catch (Throwable var8) {
            EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
            return false;
         }
      } else {
         return false;
      }
   }

   private String getAuthenticationCookie() {
      Object ticket = this.waitForPersistentContentTicket();
      ticket.hashCode();
      return (String)getCookieHashtable().get(this.getURL());
   }

   private static Hashtable getCookieHashtable() {
      if (_cookieHashtable == null) {
         _persistedCookieHashtable = RIMPersistentStore.getPersistentObject(3499486419240605781L);
         synchronized (_persistentObjectLock) {
            _cookieHashtable = (Hashtable)_persistedCookieHashtable.getContents(CodeSigningKey.getBuiltInKey(4801362));
            if (_cookieHashtable == null) {
               _cookieHashtable = (Hashtable)(new Object());
               _persistedCookieHashtable.setContents(_cookieHashtable, 4801362);
               _persistedCookieHashtable.commit();
            }
         }
      }

      return _cookieHashtable;
   }

   public static PGPUniversalServer getRegisteredServer(String url) {
      SecureEmailPolicyServer[] secureEmailPolicyServers = SecureEmailServerManager.getInstance().getPolicyServers();
      int numSecureEmailPolicyServers = secureEmailPolicyServers.length;

      for (int i = 0; i < numSecureEmailPolicyServers; i++) {
         SecureEmailPolicyServer var10000 = secureEmailPolicyServers[i];
         if (secureEmailPolicyServers[i] instanceof PGPUniversalServer) {
            PGPUniversalServer currentUniversalServer = (PGPUniversalServer)var10000;
            if (StringUtilities.strEqualIgnoreCase(url, currentUniversalServer.getURL(), 1701707776)) {
               return currentUniversalServer;
            }
         }
      }

      return null;
   }

   private void promptToEnroll(SecureEmailServerOperationListener param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: sipush 8106
      // 03: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 06: sipush 8107
      // 09: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getStringArray (I)[Ljava/lang/String;
      // 0c: bipush 0
      // 0d: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 10: istore 2
      // 11: iload 2
      // 12: ifeq 16
      // 15: return
      // 16: bipush 0
      // 17: istore 3
      // 18: sipush 8080
      // 1b: istore 4
      // 1d: aload 0
      // 1e: aload 1
      // 1f: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.enroll (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)I
      // 22: istore 5
      // 24: iload 5
      // 26: bipush 1
      // 27: if_icmpne 32
      // 2a: sipush 8105
      // 2d: istore 4
      // 2f: goto 4f
      // 32: iload 5
      // 34: bipush 2
      // 36: if_icmpne 4f
      // 39: sipush 8081
      // 3c: istore 4
      // 3e: bipush 1
      // 3f: istore 3
      // 40: goto 4f
      // 43: astore 5
      // 45: sipush 8110
      // 48: istore 4
      // 4a: goto 4f
      // 4d: astore 5
      // 4f: new java/lang/Object
      // 52: dup
      // 53: iload 4
      // 55: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 58: sipush 10004
      // 5b: invokestatic net/rim/device/internal/i18n/CommonResource.getStringArray (I)[Ljava/lang/String;
      // 5e: bipush 0
      // 5f: bipush 0
      // 60: invokestatic net/rim/device/api/system/Bitmap.getPredefinedBitmap (I)Lnet/rim/device/api/system/Bitmap;
      // 63: ldc_w 134217728
      // 66: i2l
      // 67: invokespecial net/rim/device/internal/ui/component/SimpleChoiceDialog.<init> (Ljava/lang/String;[Ljava/lang/Object;ILnet/rim/device/api/system/Bitmap;J)V
      // 6a: astore 5
      // 6c: iload 3
      // 6d: ifeq 8f
      // 70: aload 0
      // 71: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._enrollmentPendingDialogs Ljava/util/Vector;
      // 74: dup
      // 75: astore 6
      // 77: monitorenter
      // 78: aload 0
      // 79: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._enrollmentPendingDialogs Ljava/util/Vector;
      // 7c: aload 5
      // 7e: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 81: aload 6
      // 83: monitorexit
      // 84: goto 8f
      // 87: astore 7
      // 89: aload 6
      // 8b: monitorexit
      // 8c: aload 7
      // 8e: athrow
      // 8f: aload 5
      // 91: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.show (Lnet/rim/device/internal/ui/component/PopupDialog;)V
      // 94: return
      // try (14 -> 31): 32 null
      // try (14 -> 31): 36 null
      // try (57 -> 63): 64 null
      // try (64 -> 67): 64 null
   }

   private int enroll(SecureEmailServerOperationListener listener) {
      int enrollmentType = ITPolicy.getInteger(26, 9, 1);
      switch (enrollmentType) {
         case 1:
            return this.beginEnrollmentEmail(listener);
         default:
            return this.enrollUsernamePassword(listener);
      }
   }

   private int enrollUsernamePassword(SecureEmailServerOperationListener listener) {
      UsernamePasswordDialog usernamePasswordPrompt = (UsernamePasswordDialog)(new Object(PGPResources.getString(8104), null, null, null, 1, 134217728));
      BackgroundDialog.show(usernamePasswordPrompt);
      if (usernamePasswordPrompt.getCloseReason() == -1) {
         return 0;
      }

      String username = usernamePasswordPrompt.getUsername();
      String password = usernamePasswordPrompt.getPassword();
      if (username != null && password != null) {
         XMLHashtable authenticateInternalResponse = this._soapHandler.authenticateInternalWithUsernamePassword(username, password, listener);
         if (!authenticateInternalResponse.isPresent("/AuthenticateInternalPassphraseResponse")) {
            return 0;
         }

         this.detectPolicyType(authenticateInternalResponse, "/AuthenticateInternalPassphraseResponse");
         XMLHashtable getCookieResponse = this._soapHandler.getAuthenticationCookie(listener);
         String authenticationCookie = getCookieResponse.getString("/GetNonExpiringCookieResponse/cookie");
         if (authenticationCookie == null) {
            return 0;
         }

         this.completeEnrollment(authenticationCookie, listener);
         return 1;
      } else {
         return 0;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int beginEnrollmentEmail(SecureEmailServerOperationListener listener) {
      ServiceBook serviceBook = ServiceBook.getSB();
      String defaultEmailAddress = null;
      int numServiceUIDs = this._serviceUIDs.length;

      for (int i = 0; i < numServiceUIDs && defaultEmailAddress == null; i++) {
         ServiceRecord currentServiceRecord = serviceBook.getRecordByUidAndCid(this._serviceUIDs[i], "CMIME");
         defaultEmailAddress = CMIMEUtilities.getEmailAddress(currentServiceRecord);
      }

      if (defaultEmailAddress == null) {
         ServiceRecord[] allCMIMEServiceRecords = serviceBook.findRecordsByCid("CMIME");
         int numServiceRecords = allCMIMEServiceRecords.length;

         for (int i = 0; i < numServiceRecords && defaultEmailAddress == null; i++) {
            defaultEmailAddress = CMIMEUtilities.getEmailAddress(allCMIMEServiceRecords[i]);
         }
      }

      SimpleOKCancelInputDialog emailAddressPrompt = (SimpleOKCancelInputDialog)(new Object(8, PGPResources.getString(8079), 0, 1000000, 134217728));
      if (defaultEmailAddress != null) {
         emailAddressPrompt.setText(defaultEmailAddress);
      }

      BackgroundDialog.show(emailAddressPrompt);
      if (emailAddressPrompt.getCloseReason() == -1) {
         return 0;
      }

      String emailAddress = emailAddressPrompt.getText();
      if (emailAddress == null) {
         return 0;
      }

      this.updateListener(listener, 8087);
      Thread.yield();
      long currentTime = System.currentTimeMillis();

      try {
         RSAKeyPair ephemeralKeyPair = (RSAKeyPair)(new Object((RSACryptoSystem)(new Object(1024))));
         RSAPublicKey ephemeralPublicKey = ephemeralKeyPair.getRSAPublicKey();
         RSAPrivateKey ephemeralPrivateKey = ephemeralKeyPair.getRSAPrivateKey();
         ByteArrayOutputStream publicKeyStream = (ByteArrayOutputStream)(new Object());
         publicKeyStream.write(4);
         publicKeyStream.write((byte)(currentTime >> 24));
         publicKeyStream.write((byte)(currentTime >> 16));
         publicKeyStream.write((byte)(currentTime >> 8));
         publicKeyStream.write((byte)currentTime);
         publicKeyStream.write(1);
         PGPUtilities.writeMPI(publicKeyStream, ephemeralPublicKey.getN());
         PGPUtilities.writeMPI(publicKeyStream, ephemeralPublicKey.getE());
         byte[] publicKeyArray = publicKeyStream.toByteArray();
         ByteArrayOutputStream userIDStream = (ByteArrayOutputStream)(new Object());
         StringBuffer fullUserID = (StringBuffer)(new Object());
         fullUserID.append(emailAddress).append(' ').append('<').append(emailAddress).append('>');
         userIDStream.write(fullUserID.toString().getBytes());
         byte[] userIDArray = userIDStream.toByteArray();
         ByteArrayOutputStream certificateStream = (ByteArrayOutputStream)(new Object());
         PGPUtilities.writeTagAndLength(certificateStream, 6, publicKeyArray.length, 4);
         certificateStream.write(publicKeyArray);
         PGPUtilities.writeTagAndLength(certificateStream, 13, userIDArray.length, 4);
         certificateStream.write(userIDArray);
         PGPCertificate pgpEphemeralCertificate = (PGPCertificate)(new Object(certificateStream.toByteArray()));
         ByteArrayOutputStream publicKeyBlockStream = (ByteArrayOutputStream)(new Object());
         PGPArmorEncoder pgpArmorEncoder = (PGPArmorEncoder)(new Object(publicKeyBlockStream));
         pgpArmorEncoder.write(PGPKeyEncoder.getEncoding(pgpEphemeralCertificate));
         pgpArmorEncoder.close();
         String ephemeralPublicKeyBlock = (String)(new Object(publicKeyBlockStream.toByteArray()));
         ByteArrayOutputStream privateKeyStream = (ByteArrayOutputStream)(new Object());
         privateKeyStream.write(publicKeyArray);
         privateKeyStream.write(0);
         PGPUtilities.writeMPI(privateKeyStream, ephemeralPrivateKey.getD());
         PGPUtilities.writeMPI(privateKeyStream, ephemeralPrivateKey.getP());
         PGPUtilities.writeMPI(privateKeyStream, ephemeralPrivateKey.getQ());
         byte[] privateKeyArray = privateKeyStream.toByteArray();
         PGPPrivateKeyPacket privateKeyPacket = (PGPPrivateKeyPacket)(new Object(5, privateKeyArray));
         PGPPrivateKey pgpEphemeralPrivateKey = (PGPPrivateKey)(new Object(new Object[]{privateKeyPacket}));
         PGPUniversalEnrollmentKeyStore.getInstance()
            .set(null, PGPResources.getString(8088), pgpEphemeralPrivateKey, null, 1, pgpEphemeralCertificate, null, null);
         EventLogger.logEvent(234044482576569793L, 1430604611);
         this.updateListener(listener, 8089);
         String userValue = Long.toString(RandomSource.getLong(), 16);
         if (!this._soapHandler.requestEnrollment(emailAddress, ephemeralPublicKeyBlock, userValue, listener)) {
            return 0;
         }

         Hashtable pgpUniversalServerEnrollmentData = ApplicationRegistry.getApplicationRegistry().getHashtable(3676539340381219095L);
         pgpUniversalServerEnrollmentData.put(new Object(pgpEphemeralCertificate.getKeyID()), userValue);
         this._authenticationState = 1;
         this._lastEnrollmentRequestTimeStamp = System.currentTimeMillis();
         return 2;
      } catch (Throwable var29) {
         EventLogger.logEvent(234044482576569793L, e.toString().getBytes());
         return 0;
      }
   }

   public static void completeEnrollmentEmail(String universalServerAddress, String authenticationCookie, byte[] ephemeralKeyID) {
      PGPUniversalServer universalServer = getRegisteredServer(universalServerAddress);
      if (universalServer != null) {
         universalServer.completeEnrollmentEmail(authenticationCookie, ephemeralKeyID);
      }
   }

   private void completeEnrollmentEmail(String authenticationCookie, byte[] ephemeralKeyID) {
      PGPUniversalServer$CompleteEnrollmentEmailThread completeEnrollmentEmailThread = new PGPUniversalServer$CompleteEnrollmentEmailThread(
         this, authenticationCookie, ephemeralKeyID, null
      );
      completeEnrollmentEmailThread.start();
   }

   private void completeEnrollment(String param1, SecureEmailServerOperationListener param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.putAuthenticationCookie (Ljava/lang/String;)V
      // 05: invokestatic net/rim/device/apps/internal/blackberryemail/properties/MessagePropertiesDefaults.getInstance ()Lnet/rim/device/apps/internal/blackberryemail/properties/MessagePropertiesDefaults;
      // 08: astore 3
      // 09: aload 3
      // 0a: invokevirtual net/rim/device/apps/internal/blackberryemail/properties/MessagePropertiesDefaults.getMessageClassification ()I
      // 0d: istore 4
      // 0f: aload 3
      // 10: ldc2_w -742709496102783169
      // 13: bipush 3
      // 15: iload 4
      // 17: invokevirtual net/rim/device/apps/internal/blackberryemail/properties/MessagePropertiesDefaults.setProperties (JII)V
      // 1a: aload 0
      // 1b: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._enrollmentPendingDialogs Ljava/util/Vector;
      // 1e: dup
      // 1f: astore 5
      // 21: monitorenter
      // 22: aload 0
      // 23: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._enrollmentPendingDialogs Ljava/util/Vector;
      // 26: invokevirtual java/util/Vector.size ()I
      // 29: istore 6
      // 2b: bipush 0
      // 2c: istore 7
      // 2e: iload 7
      // 30: iload 6
      // 32: if_icmpge 80
      // 35: aload 0
      // 36: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._enrollmentPendingDialogs Ljava/util/Vector;
      // 39: iload 7
      // 3b: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 3e: checkcast java/lang/Object
      // 41: astore 8
      // 43: aload 8
      // 45: invokevirtual net/rim/device/api/ui/Screen.getApplication ()Lnet/rim/device/api/system/Application;
      // 48: astore 9
      // 4a: aload 9
      // 4c: ifnull 7a
      // 4f: aload 9
      // 51: invokevirtual net/rim/device/api/system/Application.getAppEventLock ()Ljava/lang/Object;
      // 54: dup
      // 55: astore 10
      // 57: monitorenter
      // 58: aload 8
      // 5a: invokevirtual net/rim/device/api/ui/Screen.isDisplayed ()Z
      // 5d: ifeq 6c
      // 60: aload 8
      // 62: invokevirtual net/rim/device/api/ui/Screen.getUiEngine ()Lnet/rim/device/api/ui/UiEngine;
      // 65: aload 8
      // 67: invokeinterface net/rim/device/api/ui/UiEngine.popScreen (Lnet/rim/device/api/ui/Screen;)V 2
      // 6c: aload 10
      // 6e: monitorexit
      // 6f: goto 7a
      // 72: astore 11
      // 74: aload 10
      // 76: monitorexit
      // 77: aload 11
      // 79: athrow
      // 7a: iinc 7 1
      // 7d: goto 2e
      // 80: aload 0
      // 81: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._enrollmentPendingDialogs Ljava/util/Vector;
      // 84: invokevirtual java/util/Vector.removeAllElements ()V
      // 87: aload 5
      // 89: monitorexit
      // 8a: goto 95
      // 8d: astore 12
      // 8f: aload 5
      // 91: monitorexit
      // 92: aload 12
      // 94: athrow
      // 95: sipush 8102
      // 98: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 9b: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestionOnBackground (Ljava/lang/String;)Z
      // 9e: ifeq d2
      // a1: sipush 8085
      // a4: istore 5
      // a6: aload 0
      // a7: aload 2
      // a8: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.downloadKeys (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)I
      // ab: istore 6
      // ad: iload 6
      // af: bipush 1
      // b0: if_icmpne bb
      // b3: sipush 8086
      // b6: istore 5
      // b8: goto d2
      // bb: iload 6
      // bd: bipush 2
      // bf: if_icmpne d2
      // c2: sipush 8112
      // c5: istore 5
      // c7: return
      // c8: astore 6
      // ca: sipush 8110
      // cd: istore 5
      // cf: return
      // d0: astore 6
      // d2: return
      // try (43 -> 52): 53 null
      // try (53 -> 56): 53 null
      // try (18 -> 65): 66 null
      // try (66 -> 69): 66 null
      // try (77 -> 92): 93 null
      // try (77 -> 92): 97 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void detectPolicyType(XMLHashtable serverResponse, String rootKey) {
      String majorKey = ((StringBuffer)(new Object())).append(rootKey).append("/server-version/major").toString();
      String minorKey = ((StringBuffer)(new Object())).append(rootKey).append("/server-version/minor").toString();
      boolean var8 = false /* VF: Semaphore variable */;

      try {
         var8 = true;
         int e = serverResponse.getInteger(majorKey);
         int serverVersionMinor = serverResponse.getInteger(minorKey);
         if (e != -1 && serverVersionMinor != -1 && e >= 2 && (e != 2 || serverVersionMinor >= 5)) {
            this._policyType = 1;
            return;
         }

         this._policyType = 0;
         var8 = false;
      } finally {
         if (var8) {
            this._policyType = 0;
            return;
         }
      }
   }

   private int authenticate(String authenticationCookie, SecureEmailServerOperationListener listener) {
      this.updateListener(listener, 8089);

      try {
         XMLHashtable authenticateInternalResponse = this._soapHandler.authenticateInternalWithCookie(authenticationCookie, listener);
         if (!authenticateInternalResponse.isPresent("/AuthenticateInternalResponse")) {
            return 0;
         }

         this.detectPolicyType(authenticateInternalResponse, "/AuthenticateInternalResponse");
      } catch (PGPUniversalServerSoapFaultException e) {
         this.removeAuthenticationCookie();
         return 2;
      }

      this.putAuthenticationCookie(authenticationCookie);
      return 1;
   }

   private int downloadKeys(SecureEmailServerOperationListener param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: sipush 8096
      // 005: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.updateListener (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;I)V
      // 008: invokestatic net/rim/device/api/crypto/keystore/PGPKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/PGPKeyStore;
      // 00b: astore 2
      // 00c: aload 2
      // 00d: new java/lang/Object
      // 010: dup
      // 011: invokespecial net/rim/device/api/crypto/certificate/pgp/PGPKeyIDKeyStoreIndex.<init> ()V
      // 014: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 019: pop
      // 01a: aload 0
      // 01b: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer._soapHandler Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler;
      // 01e: aload 1
      // 01f: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServerSOAPHandler.downloadKey (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)Lnet/rim/device/api/xml/XMLHashtable;
      // 022: astore 3
      // 023: aload 3
      // 024: ldc_w "/DownloadPrivateKeyResponse/keyblock"
      // 027: invokevirtual net/rim/device/api/xml/XMLHashtable.getString (Ljava/lang/String;)Ljava/lang/String;
      // 02a: astore 4
      // 02c: aload 4
      // 02e: ifnonnull 033
      // 031: bipush 0
      // 032: ireturn
      // 033: new java/lang/Object
      // 036: dup
      // 037: aload 4
      // 039: invokevirtual java/lang/String.getBytes ()[B
      // 03c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 03f: astore 5
      // 041: new java/lang/Object
      // 044: dup
      // 045: aload 5
      // 047: invokespecial net/rim/device/api/crypto/pgp/PGPArmorDecoder.<init> (Ljava/io/InputStream;)V
      // 04a: astore 6
      // 04c: aload 6
      // 04e: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.numCertificates ()I
      // 051: istore 7
      // 053: iload 7
      // 055: ifne 05a
      // 058: bipush 0
      // 059: ireturn
      // 05a: bipush 0
      // 05b: anewarray 4444
      // 05e: astore 8
      // 060: bipush 0
      // 061: anewarray 4446
      // 064: astore 9
      // 066: bipush 0
      // 067: anewarray 4448
      // 06a: astore 10
      // 06c: new java/lang/Object
      // 06f: dup
      // 070: invokespecial java/util/Vector.<init> ()V
      // 073: astore 11
      // 075: bipush 0
      // 076: istore 12
      // 078: bipush 0
      // 079: istore 13
      // 07b: iload 13
      // 07d: iload 7
      // 07f: if_icmplt 085
      // 082: goto 11a
      // 085: aload 6
      // 087: iload 13
      // 089: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getCertificate (I)Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 08c: astore 14
      // 08e: aload 6
      // 090: iload 13
      // 092: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getPrivateKey (I)Lnet/rim/device/api/crypto/PrivateKey;
      // 095: astore 15
      // 097: aload 15
      // 099: ifnonnull 0a2
      // 09c: bipush 1
      // 09d: istore 12
      // 09f: goto 114
      // 0a2: bipush 0
      // 0a3: istore 16
      // 0a5: aload 2
      // 0a6: ldc2_w -2737350786039236692
      // 0a9: aload 14
      // 0ab: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getKeyID ()[B
      // 0ae: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 0b3: astore 17
      // 0b5: aload 17
      // 0b7: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0bc: ifeq 0f4
      // 0bf: aload 17
      // 0c1: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0c6: checkcast java/lang/Object
      // 0c9: astore 18
      // 0cb: aload 18
      // 0cd: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 0d2: ifeq 0ea
      // 0d5: aload 18
      // 0d7: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 0dc: aload 14
      // 0de: invokestatic net/rim/device/api/util/ObjectUtilities.objEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 0e1: ifeq 0ea
      // 0e4: bipush 1
      // 0e5: istore 16
      // 0e7: goto 0b5
      // 0ea: aload 11
      // 0ec: aload 18
      // 0ee: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0f1: goto 0b5
      // 0f4: iload 16
      // 0f6: ifeq 0fc
      // 0f9: goto 114
      // 0fc: aload 8
      // 0fe: aload 14
      // 100: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 103: aload 9
      // 105: aload 15
      // 107: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 10a: aload 10
      // 10c: aload 14
      // 10e: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getSubjectFriendlyName ()Ljava/lang/String;
      // 111: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 114: iinc 13 1
      // 117: goto 07b
      // 11a: aload 8
      // 11c: arraylength
      // 11d: ifle 168
      // 120: aload 2
      // 121: sipush 8100
      // 124: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 127: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 2
      // 12c: astore 13
      // 12e: aload 11
      // 130: invokevirtual java/util/Vector.size ()I
      // 133: istore 14
      // 135: bipush 0
      // 136: istore 15
      // 138: iload 15
      // 13a: iload 14
      // 13c: if_icmpge 157
      // 13f: aload 2
      // 140: aload 11
      // 142: iload 15
      // 144: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 147: checkcast java/lang/Object
      // 14a: aload 13
      // 14c: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.removeKey (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V 3
      // 151: iinc 15 1
      // 154: goto 138
      // 157: aload 8
      // 159: aload 9
      // 15b: aload 10
      // 15d: aload 2
      // 15e: aload 13
      // 160: invokestatic net/rim/device/api/crypto/certificate/CertificateImporterFactory.importCertificates ([Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/PrivateKey;[Ljava/lang/String;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Z
      // 163: ifne 168
      // 166: bipush 0
      // 167: ireturn
      // 168: aload 3
      // 169: ldc_w "/DownloadPrivateKeyResponse/keyid"
      // 16c: invokevirtual net/rim/device/api/xml/XMLHashtable.getString (Ljava/lang/String;)Ljava/lang/String;
      // 16f: astore 13
      // 171: aload 13
      // 173: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.hexASCIIStringToBinary (Ljava/lang/String;)[B
      // 176: astore 14
      // 178: aload 14
      // 17a: ifnull 1ce
      // 17d: aload 2
      // 17e: ldc2_w -2737350786039236692
      // 181: aload 14
      // 183: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 188: astore 15
      // 18a: aload 15
      // 18c: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 191: ifeq 1ce
      // 194: aload 15
      // 196: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 19b: checkcast java/lang/Object
      // 19e: astore 16
      // 1a0: aload 16
      // 1a2: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 1a7: ifeq 18a
      // 1aa: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPFactory.getInstance ()Lnet/rim/device/apps/internal/secureemail/encodings/pgp/PGPFactory;
      // 1ad: astore 17
      // 1af: aload 17
      // 1b1: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/PGPFactory.createGlobalOptionsCopy ()Lnet/rim/device/apps/internal/secureemail/SecureEmailOptions;
      // 1b4: checkcast net/rim/device/apps/internal/secureemail/encodings/pgp/PGPOptions
      // 1b7: astore 18
      // 1b9: aload 18
      // 1bb: aload 16
      // 1bd: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptions.setSigningKeyStoreData (Lnet/rim/device/api/crypto/keystore/KeyStoreData;)V
      // 1c0: aload 18
      // 1c2: aload 16
      // 1c4: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptions.setEncryptionKeyStoreData (Lnet/rim/device/api/crypto/keystore/KeyStoreData;)V
      // 1c7: aload 17
      // 1c9: aload 18
      // 1cb: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.saveGlobalOptions (Lnet/rim/device/apps/internal/secureemail/SecureEmailOptions;)V
      // 1ce: iload 12
      // 1d0: ifeq 1d6
      // 1d3: bipush 2
      // 1d5: ireturn
      // 1d6: bipush 1
      // 1d7: ireturn
      // 1d8: astore 3
      // 1d9: bipush 0
      // 1da: ireturn
      // 1db: astore 3
      // 1dc: ldc2_w 234044482576569793
      // 1df: aload 3
      // 1e0: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 1e3: invokevirtual java/lang/String.getBytes ()[B
      // 1e6: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 1e9: pop
      // 1ea: bipush 0
      // 1eb: ireturn
      // try (12 -> 24): 201 null
      // try (25 -> 42): 201 null
      // try (43 -> 155): 201 null
      // try (156 -> 198): 201 null
      // try (199 -> 200): 201 null
      // try (12 -> 24): 204 null
      // try (25 -> 42): 204 null
      // try (43 -> 155): 204 null
      // try (156 -> 198): 204 null
      // try (199 -> 200): 204 null
   }
}
