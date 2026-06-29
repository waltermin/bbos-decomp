package net.rim.device.api.crypto.oid;

import java.util.Hashtable;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.vm.WeakReference;

public final class OIDs {
   public static final int RSA;
   public static final int RSA_OAEP;
   public static final int RSA_PKCS1_MD2;
   public static final int RSA_PKCS1_MD4;
   public static final int RSA_PKCS1_MD5;
   public static final int RSA_PKCS1_SHA1;
   public static final int RSA_MGF1;
   public static final int RSA_PSPECIFIED;
   public static final int RSA_PSS;
   public static final int RSA_PKCS1_SHA224;
   public static final int RSA_PKCS1_SHA256;
   public static final int RSA_PKCS1_SHA384;
   public static final int RSA_PKCS1_SHA512;
   public static final int RSA_PKCS1_RIPEMD160;
   public static final int RSA_PKCS1_RIPEMD128;
   public static final int OIW_RSA_PKCS1_SHA1;
   public static final int PBKDF2;
   public static final int id_SPKM_KDF_with_SHA1;
   public static final int DH;
   public static final int DH_KEYAGREEMENT;
   public static final int DSA;
   public static final int DSA_SHA1;
   public static final int EC;
   public static final int ECDSA;
   public static final int ECDSA_SHA1;
   public static final int KEA;
   public static final int SECP160R1;
   public static final int SECP192R1;
   public static final int SECP224R1;
   public static final int SECP256R1;
   public static final int SECP384R1;
   public static final int SECP521R1;
   public static final int SECT163K1;
   public static final int SECT163R1;
   public static final int SECT163R2;
   public static final int SECT233K1;
   public static final int SECT233R1;
   public static final int SECT239K1;
   public static final int SECT283K1;
   public static final int SECT283R1;
   public static final int SECT409K1;
   public static final int SECT409R1;
   public static final int SECT571K1;
   public static final int SECT571R1;
   public static final int C2PND163V1;
   public static final int SHA1;
   public static final int SHA224;
   public static final int SHA256;
   public static final int SHA384;
   public static final int SHA512;
   public static final int MD2;
   public static final int MD4;
   public static final int MD5;
   public static final int RIPEMD160;
   public static final int RIPEMD128;
   public static final int AES_128_ECB;
   public static final int AES_128_CBC;
   public static final int AES_128_OFB;
   public static final int AES_128_CFB;
   public static final int AES_128_KEY_WRAP;
   public static final int AES_192_ECB;
   public static final int AES_192_CBC;
   public static final int AES_192_OFB;
   public static final int AES_192_CFB;
   public static final int AES_192_KEY_WRAP;
   public static final int AES_256_ECB;
   public static final int AES_256_CBC;
   public static final int AES_256_OFB;
   public static final int AES_256_CFB;
   public static final int AES_256_KEY_WRAP;
   public static final int CAST128_CBC;
   public static final int CAST128_CMS_KEY_WRAP;
   public static final int RIM_ARC4;
   public static final int RIM_AES;
   public static final int RIM_CAST128;
   public static final int RIM_DES;
   public static final int RIM_HMAC;
   public static final int RIM_RC2;
   public static final int RIM_RC5;
   public static final int RIM_Skipjack;
   public static final int RIM_TripleDES;
   public static final int idAtCommonName;
   public static final int idAtSurname;
   public static final int idAtCountryName;
   public static final int idAtLocalityName;
   public static final int idAtStateOrProvinceName;
   public static final int idAtStreetAddress;
   public static final int idAtOrganizationName;
   public static final int idAtOrganizationalUnitName;
   public static final int idAtTitle;
   public static final int idAtName;
   public static final int idAtGivenName;
   public static final int idAtInitials;
   public static final int idAtGenerationQualifier;
   public static final int idAtDnQualifier;
   public static final int idAtUserID;
   public static final int idAtDomainComponent;
   public static final int idCeSubjectDirectoryAttributes;
   public static final int idCeBasicConstraintsDeprecated;
   public static final int idCeSubjectKeyIdentifier;
   public static final int idCeKeyUsage;
   public static final int idCePrivateKeyUsagePeriod;
   public static final int idCeSubjectAltName;
   public static final int idCeIssuerAltName;
   public static final int idCeBasicConstraints;
   public static final int idCeCRLNumber;
   public static final int idCeReasonCode;
   public static final int idCeInstructionCode;
   public static final int idCeInvalidityDate;
   public static final int idCeDeltaCRLIndicator;
   public static final int idCeIssuingDistributionPoint;
   public static final int idCeCertificateIssuer;
   public static final int idCeNameConstraints;
   public static final int idCeCRLDistributionPoints;
   public static final int idCeCertificatePolicies;
   public static final int idCePolicyMappings;
   public static final int idCeAuthorityKeyIdentifier;
   public static final int idCePolicyConstraints;
   public static final int idCeExtKeyUsage;
   public static final int idKpServerAuth;
   public static final int idKpClientAuth;
   public static final int idKpCodeSigning;
   public static final int idKpEmailProtection;
   public static final int idKpTimeStamping;
   public static final int idKpOCSPSigning;
   public static final int pkcs9_oc_pkcsEntity;
   public static final int pkcs9_oc_naturalPerson;
   public static final int pkcs9_at_emailAddress;
   public static final int pkcs9_at_unstructuredName;
   public static final int pkcs9_at_contentType;
   public static final int pkcs9_at_messageDigest;
   public static final int pkcs9_at_signingTime;
   public static final int pkcs9_at_counterSignature;
   public static final int pkcs9_at_challengePassword;
   public static final int pkcs9_at_unstructuredAddress;
   public static final int pkcs9_at_extendedCertificateAttributes;
   public static final int pkcs9_at_signingDescription;
   public static final int pkcs9_at_extensionRequest;
   public static final int pkcs9_at_smimeCapabilities;
   public static final int pkcs9_at_friendlyName;
   public static final int pkcs9_at_localKeyId;
   public static final int pkcs9_at_userPKCS12;
   public static final int pkcs9_at_pkcs15Token;
   public static final int pkcs9_at_encryptedPrivateKeyInfo;
   public static final int pkcs9_at_randomNonce;
   public static final int pkcs9_at_sequenceNumber;
   public static final int pkcs9_at_pkcs7PDU;
   public static final int pkcs9_at_dateOfBirth;
   public static final int pkcs9_at_placeOfBirth;
   public static final int pkcs9_at_gender;
   public static final int pkcs9_at_countryOfCitizenship;
   public static final int pkcs9_at_countryOfResidence;
   public static final int pkcs9_sx_pkcs9String;
   public static final int pkcs9_sx_signingTime;
   public static final int pkcs9_mr_caseIgnoreMatch;
   public static final int pkcs9_mr_signingTimeMatch;
   public static final int id_aa_encrypKeyPref;
   public static final int id_aa_receiptRequest;
   public static final int id_aa_msgSigDigest;
   public static final int id_aa_contentReference;
   public static final int id_aa_signingCertificate;
   public static final int id_aa_mlExpandHistory;
   public static final int id_aa_securityLabel;
   public static final int id_aa_equivalentLabels;
   public static final int id_aa_contentHint;
   public static final int id_aa_contentIdentifier;
   public static final int cmsIdData;
   public static final int cmsIdSignedData;
   public static final int cmsIdEnvelopedData;
   public static final int cmsIdSignedAndEnvelopedData;
   public static final int cmsIdDigestedData;
   public static final int cmsIdEncrypedData;
   public static final int cmsIdAuthenticatedData;
   public static final int cmsIdCtReceipt;
   public static final int cmsIdCtContentInfo;
   public static final int cmsIdCtCompressedData;
   public static final int dhSinglePass_StdDH_SHA1KDF_Scheme;
   public static final int dhSinglePass_CofactorDH_SHA1KDF_Scheme;
   public static final int mqvSinglePass_SHA1KDF_Scheme;
   public static final int cmsIdAlgESDH;
   public static final int cmsIdAlgCMS3DESWrap;
   public static final int cmsIdAlgCMSRC2Wrap;
   public static final int cmsIdAlgZLibCompress;
   public static final int cmsIdAlgSSDH;
   public static final int cmsIdAlgPWRIKEK;
   public static final int desEDE3CBC;
   public static final int rc2CBC;
   public static final int rc4;
   public static final int hmacSHA1;
   public static final int idHmacSHA1;
   public static final int desCBC;
   public static final int ocspBasicResponse;
   public static final int ocspNonce;
   public static final int certAuthorityInfoAccess;
   public static final int certAccessDescriptionOCSP;
   public static final int emsEncryptRequestRecipientInformation;
   public static final int emsAcceptRequestRecipientName;
   public static final int emsExtendedKeyUsage;
   public static final int emsAcceptRequestSalutation;
   public static final int emsProofOfIntention;
   public static final int pbeWithSHAAnd128BitRC4;
   public static final int pbeWithSHAAnd40BitRC4;
   public static final int pbeWithSHAAnd3_KeyTripleDES_CBC;
   public static final int pbeWithSHAAnd2_KeyTripleDES_CBC;
   public static final int pbeWithSHAAnd128BitRC2_CBC;
   public static final int pbeWithSHAAnd40BitRC2_CBC;
   public static final int pkcs9_x509Certificate;
   public static final int pkcs9_sdsiCertificate;
   public static final int pkcs9_x509CRL;
   public static final int pkcs12_keyBag;
   public static final int pkcs12_pkcs8ShroudedKeyBag;
   public static final int pkcs12_certBag;
   public static final int pkcs12_crlBag;
   public static final int pkcs12_secretBag;
   public static final int pkcs12_safeContentsBag;
   public static final long KEY_ALGORITHM_NAME;
   public static final long SIGNATURE_ALGORITHM_NAME;
   public static final long HASH_ALGORITHM_NAME;
   public static final long ELLIPTIC_CURVE_ID;
   public static final long OID_NAME;
   public static final long SYMMETRIC_ALGORITHM_NAME;
   private static IntHashtable _oids;
   private static IntHashtable _idToOIDHashtable;
   private static LongHashtable _associationToOIDHashtables;
   private static LongHashtable _associationToObjectHashtables;
   private static int[] _idList = new int[200];
   private static OID[] _oidList = new OID[200];
   private static long[] _associationList = new long[200];
   private static Object[] _objectList = new Object[200];
   private static long[][][] _multiAssociationList = new long[200][][];
   private static Object[][][] _multiObjectList = new Object[200][][];
   private static int[] _hashCodeList = new int[200];
   private static int _nextOID = 0;
   private static final int NUM_OIDS;
   private static final int ASSOCIATION_INITIAL_LENGTH;

   private OIDs() {
   }

   private static final int addOID(int id, byte[] oidBytes) {
      if (_nextOID >= 200) {
         Array.resize(_idList, _nextOID + 1);
         Array.resize(_oidList, _nextOID + 1);
         Array.resize(_associationList, _nextOID + 1);
         Array.resize(_objectList, _nextOID + 1);
         Array.resize(_multiAssociationList, _nextOID + 1);
         Array.resize(_multiObjectList, _nextOID + 1);
         Array.resize(_hashCodeList, _nextOID + 1);
      }

      _idList[_nextOID] = id;
      OID oid = new OID(oidBytes);
      _oidList[_nextOID] = oid;
      _hashCodeList[_nextOID] = oid.hashCode();
      _nextOID++;
      return _nextOID - 1;
   }

   private static final void addAssociation(int oidIndex, long association, Object associatedObject) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private static final void commitOIDs() {
      Memory.createGroup(_oidList);

      for (int i = 0; i < _nextOID; i++) {
         commitOID(i);
      }
   }

   private static final void commitOID(int index) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   public static final OID getOID(int id) {
      return (OID)_idToOIDHashtable.get(id);
   }

   public static final OID internOID(OID oid) {
      synchronized (_oids) {
         int hashCode = oid.hashCode();
         OID internedOid = getInternedOID(hashCode);
         if (internedOid == null || !internedOid.equals(oid)) {
            internedOid = oid;
            _oids.put(hashCode, new Object(internedOid));
         }

         return internedOid;
      }
   }

   public static final OID internOID(byte[] encoding, int offset, int length) {
      if (encoding != null && offset >= 0 && length >= 0 && encoding.length - length >= offset) {
         synchronized (_oids) {
            int hashCode = HashCodeCalculator.getCRC32(encoding, offset, length);
            OID internedOid = getInternedOID(hashCode);
            if (internedOid == null || !internedOid.equals(encoding, offset, length)) {
               internedOid = new OID(encoding, offset, length);
               _oids.put(hashCode, new Object(internedOid));
            }

            return internedOid;
         }
      } else {
         throw new Object();
      }
   }

   private static final OID getInternedOID(int hashCode) {
      Object ref = _oids.get(hashCode);
      if (!(ref instanceof OID)) {
         return !(ref instanceof Object) ? null : (OID)((WeakReference)ref).get();
      } else {
         return (OID)ref;
      }
   }

   private static final synchronized Hashtable getOidHashtable(long association) {
      Hashtable hashtable = (Hashtable)_associationToOIDHashtables.get(association);
      if (hashtable == null) {
         hashtable = (Hashtable)(new Object());
         _associationToOIDHashtables.put(association, hashtable);
      }

      return hashtable;
   }

   private static final synchronized Hashtable getObjectHashtable(long association) {
      Hashtable hashtable = (Hashtable)_associationToObjectHashtables.get(association);
      if (hashtable == null) {
         hashtable = (Hashtable)(new Object());
         _associationToObjectHashtables.put(association, hashtable);
      }

      return hashtable;
   }

   public static final void addAssociation(long association, OID oid, Object object) {
      getOidHashtable(association).put(oid, object);
      getObjectHashtable(association).put(object, oid);
   }

   public static final Object getAssociatedObject(long association, OID oid) {
      return getOidHashtable(association).get(oid);
   }

   public static final String getAssociatedString(long association, OID oid) {
      return (String)getAssociatedObject(association, oid);
   }

   public static final String getAssociatedString(long association, OID oid, String defaultString) {
      String string = (String)getAssociatedObject(association, oid);
      return string != null ? string : defaultString;
   }

   public static final int getAssociatedInteger(long association, OID oid) {
      return getAssociatedInteger(association, oid, -1);
   }

   public static final int getAssociatedInteger(long association, OID oid, int defaultInteger) {
      Integer integer = (Integer)getAssociatedObject(association, oid);
      return integer != null ? integer : defaultInteger;
   }

   public static final OID getAssociatedOID(long association, Object object) {
      return (OID)getObjectHashtable(association).get(object);
   }

   public static final OID getAssociatedOID(long association, int integer) {
      return getAssociatedOID(association, new Object(integer));
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _oids = ar.getIntHashtable(-9127118664581965176L);
      _idToOIDHashtable = ar.getIntHashtable(7217361605658543867L);
      _associationToOIDHashtables = ar.getLongHashtable(-7292119662164228649L);
      _associationToObjectHashtables = ar.getLongHashtable(-7102478697320544195L);
      synchronized (_idToOIDHashtable) {
         if (_idToOIDHashtable.size() == 0) {
            String strRSA = "RSA";
            String strAES = "AES";
            int index = addOID(541853244, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 1});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1");
            index = addOID(543426108, new byte[]{42, -122, 72, -9, 13, 1, 1, 7});
            addAssociation(index, -5860934937401098689L, strRSA);
            index = addOID(542115388, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 2});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/MD2");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/MD2");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/MD2");
            index = addOID(542377532, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 3});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/MD4");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/MD4");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/MD4");
            index = addOID(542639676, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 4});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/MD5");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/MD5");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/MD5");
            index = addOID(1789057757, new byte[]{43, 14, 3, 2, 29});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/SHA1");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/SHA1");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/SHA1");
            index = addOID(542901820, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 5});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/SHA1");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/SHA1");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/SHA1");
            index = addOID(544212539, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 8});
            index = addOID(544212541, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 9});
            index = addOID(544212540, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 10});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PSS");
            index = addOID(544474683, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 14});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/SHA224");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/SHA224");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/SHA224");
            index = addOID(544474684, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 11});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/SHA256");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/SHA256");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/SHA256");
            index = addOID(544736828, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 12});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/SHA384");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/SHA384");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/SHA384");
            index = addOID(544998972, new byte[]{42, -122, 72, -122, -9, 13, 1, 1, 13});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/SHA512");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/SHA512");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/SHA512");
            index = addOID(1788926685, new byte[]{43, 36, 3, 3, 1, 2});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/RIPEMD160");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/RIPEMD160");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/RIPEMD160");
            index = addOID(273417788, new byte[]{42, -122, 72, -122, -9, 13, 1, 5, 12});
            index = addOID(273417789, new byte[]{43, 6, 1, 5, 5, 1, 30});
            index = addOID(1789057757, new byte[]{43, 36, 3, 3, 1, 3});
            addAssociation(index, -5860934937401098689L, strRSA);
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1/RIPEMD128");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V15/RIPEMD128");
            addAssociation(index, -5979163936319872658L, "RSA_PKCS1_V20/RIPEMD128");
            index = addOID(-1487623704, new byte[]{42, -122, 72, -50, 62, 2, 1});
            addAssociation(index, -5860934937401098689L, "DH");
            index = addOID(-472313138, new byte[]{42, -122, 72, -122, -9, 13, 1, 3, 1});
            addAssociation(index, -5860934937401098689L, "DH");
            index = addOID(-1487364632, new byte[]{42, -122, 72, -50, 56, 4, 1});
            addAssociation(index, -5860934937401098689L, "DSA");
            addAssociation(index, -5979163936319872658L, "DSA");
            index = addOID(-1487364624, new byte[]{42, -122, 72, -50, 56, 4, 3});
            addAssociation(index, -5860934937401098689L, "DSA");
            addAssociation(index, -5979163936319872658L, "DSA/SHA1");
            index = addOID(-1487624216, new byte[]{42, -122, 72, -50, 61, 2, 1});
            addAssociation(index, -5860934937401098689L, "EC");
            addAssociation(index, -5979163936319872658L, "ECDSA");
            index = addOID(1793700978, new byte[]{42, -122, 72, -50, 61, 4});
            addAssociation(index, -5860934937401098689L, "EC");
            addAssociation(index, -5979163936319872658L, "ECDSA");
            index = addOID(-1487362072, new byte[]{42, -122, 72, -50, 61, 4, 1});
            addAssociation(index, -5860934937401098689L, "EC");
            addAssociation(index, -5979163936319872658L, "ECDSA/SHA1");
            index = addOID(545973096, new byte[]{96, -122, 72, 1, 101, 2, 1, 1, 22});
            addAssociation(index, -5860934937401098689L, "KEA");
            index = addOID(774787685, new byte[]{43, -127, 4, 0, 8});
            addAssociation(index, -3607261449824502613L, "EC160R1");
            index = addOID(774787686, new byte[]{42, -122, 72, -50, 61, 3, 1, 1});
            addAssociation(index, -3607261449824502613L, "EC192R1");
            index = addOID(774787687, new byte[]{43, -127, 4, 0, 33});
            addAssociation(index, -3607261449824502613L, "EC224R1");
            index = addOID(774787688, new byte[]{42, -122, 72, -50, 61, 3, 1, 7});
            addAssociation(index, -3607261449824502613L, "EC256R1");
            index = addOID(774787689, new byte[]{43, -127, 4, 0, 34});
            addAssociation(index, -3607261449824502613L, "EC384R1");
            index = addOID(774787691, new byte[]{43, -127, 4, 0, 35});
            addAssociation(index, -3607261449824502613L, "EC521R1");
            index = addOID(774784101, new byte[]{43, -127, 4, 0, 1});
            addAssociation(index, -3607261449824502613L, "EC163K1");
            index = addOID(774784102, new byte[]{43, -127, 4, 0, 2});
            addAssociation(index, -3607261449824502613L, "EC163R1");
            index = addOID(774784103, new byte[]{43, -127, 4, 0, 15});
            addAssociation(index, -3607261449824502613L, "EC163R2");
            index = addOID(774796901, new byte[]{43, -127, 4, 0, 26});
            addAssociation(index, -3607261449824502613L, "EC233K1");
            index = addOID(774796902, new byte[]{43, -127, 4, 0, 27});
            addAssociation(index, -3607261449824502613L, "EC233R1");
            index = addOID(774785125, new byte[]{43, -127, 4, 0, 3});
            addAssociation(index, -3607261449824502613L, "EC239K1");
            index = addOID(774785126, new byte[]{43, -127, 4, 0, 16});
            addAssociation(index, -3607261449824502613L, "EC283K1");
            index = addOID(774785127, new byte[]{43, -127, 4, 0, 17});
            addAssociation(index, -3607261449824502613L, "EC283R1");
            index = addOID(774785128, new byte[]{43, -127, 4, 0, 36});
            addAssociation(index, -3607261449824502613L, "EC409K1");
            index = addOID(774785129, new byte[]{43, -127, 4, 0, 37});
            addAssociation(index, -3607261449824502613L, "EC409R1");
            index = addOID(774785131, new byte[]{43, -127, 4, 0, 38});
            addAssociation(index, -3607261449824502613L, "EC571K1");
            index = addOID(774785132, new byte[]{43, -127, 4, 0, 39});
            addAssociation(index, -3607261449824502613L, "EC571R1");
            index = addOID(-473587882, new byte[]{42, -122, 72, -50, 61, 3, 0, 1});
            addAssociation(index, -3607261449824502613L, "EC163K2");
            index = addOID(774767465, new byte[]{43, 14, 3, 2, 26});
            addAssociation(index, 3134008036018563479L, "SHA1");
            index = addOID(540600179, new byte[]{96, -122, 72, 1, 101, 3, 4, 2, 4});
            addAssociation(index, 3134008036018563479L, "SHA224");
            index = addOID(540600180, new byte[]{96, -122, 72, 1, 101, 3, 4, 2, 1});
            addAssociation(index, 3134008036018563479L, "SHA256");
            index = addOID(540862324, new byte[]{96, -122, 72, 1, 101, 3, 4, 2, 2});
            addAssociation(index, 3134008036018563479L, "SHA384");
            index = addOID(541124468, new byte[]{96, -122, 72, 1, 101, 3, 4, 2, 3});
            addAssociation(index, 3134008036018563479L, "SHA512");
            index = addOID(-472312114, new byte[]{42, -122, 72, -122, -9, 13, 2, 2});
            addAssociation(index, 3134008036018563479L, "MD2");
            index = addOID(-472310066, new byte[]{42, -122, 72, -122, -9, 13, 2, 4});
            addAssociation(index, 3134008036018563479L, "MD4");
            index = addOID(-472309042, new byte[]{42, -122, 72, -122, -9, 13, 2, 5});
            addAssociation(index, 3134008036018563479L, "MD5");
            index = addOID(774760297, new byte[]{43, 36, 3, 2, 1});
            addAssociation(index, 3134008036018563479L, "RIPEMD160");
            index = addOID(774760809, new byte[]{43, 36, 3, 2, 2});
            addAssociation(index, 3134008036018563479L, "RIPEMD128");
            index = addOID(540599156, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 1});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_128_ECB");
            index = addOID(540861300, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 2});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_128_CBC");
            index = addOID(541123444, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 3});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_128_OFB");
            index = addOID(541385588, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 4});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_128_CFB");
            index = addOID(541647732, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 5});
            index = addOID(545842036, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 21});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_192_ECB");
            index = addOID(546104180, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 22});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_192_CBC");
            index = addOID(546366324, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 23});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_192_OFB");
            index = addOID(546628468, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 24});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_192_CFB");
            index = addOID(546890612, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 25});
            index = addOID(551084916, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 41});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_256_ECB");
            index = addOID(551347060, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 42});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_256_CBC");
            index = addOID(551609204, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 43});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_256_OFB");
            index = addOID(551871348, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 44});
            addAssociation(index, -5860934937401098689L, strAES);
            addAssociation(index, 3557185416503033076L, "AES_256_CFB");
            index = addOID(552133492, new byte[]{96, -122, 72, 1, 101, 3, 4, 1, 45});
            index = addOID(552133493, new byte[]{42, -122, 72, -122, -10, 125, 7, 66, 10});
            addAssociation(index, -5860934937401098689L, "CAST128");
            addAssociation(index, 3557185416503033076L, "CAST128_CBC");
            index = addOID(552133494, new byte[]{42, -122, 72, -122, -10, 125, 7, 66, 14});
            index = addOID(1536208855, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 1});
            addAssociation(index, -5860934937401098689L, "ARC4");
            index = addOID(1536208863, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 2});
            addAssociation(index, -5860934937401098689L, strAES);
            index = addOID(1536208871, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 3});
            addAssociation(index, -5860934937401098689L, "CAST128");
            index = addOID(1536208879, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 4});
            addAssociation(index, -5860934937401098689L, "DES");
            index = addOID(1536208887, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 5});
            addAssociation(index, -5860934937401098689L, "HMAC");
            index = addOID(1536208895, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 6});
            addAssociation(index, -5860934937401098689L, "RC2");
            index = addOID(1536208903, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 7});
            addAssociation(index, -5860934937401098689L, "RC5");
            index = addOID(1536208911, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 8});
            addAssociation(index, -5860934937401098689L, "Skipjack");
            index = addOID(1536208919, new byte[]{43, 6, 1, 4, 1, -101, 74, 9, 1, 9});
            addAssociation(index, -5860934937401098689L, "TripleDES");
            index = addOID(-1253056853, new byte[]{85, 4, 3});
            addAssociation(index, -1803797844404304836L, "CN");
            index = addOID(-1252991317, new byte[]{85, 4, 4});
            addAssociation(index, -1803797844404304836L, "SN");
            index = addOID(-1252860245, new byte[]{85, 4, 6});
            addAssociation(index, -1803797844404304836L, "C");
            index = addOID(-1252794709, new byte[]{85, 4, 7});
            addAssociation(index, -1803797844404304836L, "L");
            index = addOID(-1252729173, new byte[]{85, 4, 8});
            addAssociation(index, -1803797844404304836L, "ST");
            index = addOID(-1252663637, new byte[]{85, 4, 9});
            addAssociation(index, -1803797844404304836L, "STREET");
            index = addOID(-1252598101, new byte[]{85, 4, 10});
            addAssociation(index, -1803797844404304836L, "O");
            index = addOID(-1252532565, new byte[]{85, 4, 11});
            addAssociation(index, -1803797844404304836L, "OU");
            index = addOID(-1252467029, new byte[]{85, 4, 12});
            index = addOID(-1250566485, new byte[]{85, 4, 41});
            index = addOID(-1250500949, new byte[]{85, 4, 42});
            index = addOID(-1250435413, new byte[]{85, 4, 43});
            index = addOID(-1250369877, new byte[]{85, 4, 44});
            index = addOID(-1250238805, new byte[]{85, 4, 46});
            index = addOID(-1250238803, new byte[]{9, -110, 38, -119, -109, -14, 44, 100, 1, 1});
            addAssociation(index, -1803797844404304836L, "UID");
            index = addOID(-1250238804, new byte[]{9, -110, 38, -119, -109, -14, 44, 100, 1, 25});
            addAssociation(index, -1803797844404304836L, "DC");
            index = addOID(-1252657237, new byte[]{85, 29, 9});
            index = addOID(-1252329557, new byte[]{85, 29, 14});
            index = addOID(-1252264021, new byte[]{85, 29, 15});
            index = addOID(-1252198485, new byte[]{85, 29, 16});
            index = addOID(-1252132949, new byte[]{85, 29, 17});
            index = addOID(-1252067413, new byte[]{85, 29, 18});
            index = addOID(-1252001877, new byte[]{85, 29, 19});
            index = addOID(-1252001878, new byte[]{85, 29, 10});
            index = addOID(-1251936341, new byte[]{85, 29, 20});
            index = addOID(-1251870805, new byte[]{85, 29, 21});
            index = addOID(-1251739733, new byte[]{85, 29, 23});
            index = addOID(-1251674197, new byte[]{85, 29, 24});
            index = addOID(-1251477589, new byte[]{85, 29, 27});
            index = addOID(-1251412053, new byte[]{85, 29, 28});
            index = addOID(-1251346517, new byte[]{85, 29, 29});
            index = addOID(-1251280981, new byte[]{85, 29, 30});
            index = addOID(-1251215445, new byte[]{85, 29, 31});
            index = addOID(-1251149909, new byte[]{85, 29, 32});
            index = addOID(-1251084373, new byte[]{85, 29, 33});
            index = addOID(-1250953301, new byte[]{85, 29, 35});
            index = addOID(-1250887765, new byte[]{85, 29, 36});
            index = addOID(-1250822229, new byte[]{85, 29, 37});
            index = addOID(-477712431, new byte[]{43, 6, 1, 5, 5, 7, 3, 1});
            index = addOID(-477711407, new byte[]{43, 6, 1, 5, 5, 7, 3, 2});
            index = addOID(-477710383, new byte[]{43, 6, 1, 5, 5, 7, 3, 3});
            index = addOID(-477709359, new byte[]{43, 6, 1, 5, 5, 7, 3, 4});
            index = addOID(-477705263, new byte[]{43, 6, 1, 5, 5, 7, 3, 8});
            index = addOID(1688405111, new byte[]{43, 6, 1, 5, 5, 7, 3, 9});
            index = addOID(1561794998, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 24, 1});
            index = addOID(1561795006, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 24, 2});
            index = addOID(541861436, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 1});
            addAssociation(index, -1803797844404304836L, "E");
            index = addOID(542123580, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 2});
            index = addOID(542385724, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 3});
            index = addOID(542647868, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 4});
            index = addOID(542910012, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 5});
            index = addOID(543172156, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 6});
            index = addOID(543434300, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 7});
            index = addOID(543696444, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 8});
            index = addOID(543958588, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 9});
            index = addOID(545007164, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 13});
            index = addOID(545269308, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 14});
            index = addOID(545531452, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 15});
            index = addOID(546842172, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 20});
            index = addOID(547104316, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 21});
            index = addOID(-479033224, new byte[]{96, -122, -8, 66, 3, 1, 1, 88});
            index = addOID(1562057142, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 25, 1});
            index = addOID(1562057150, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 25, 2});
            index = addOID(1562057158, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 25, 3});
            index = addOID(1562057166, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 25, 4});
            index = addOID(1562057174, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 25, 5});
            index = addOID(-477712407, new byte[]{43, 6, 1, 5, 5, 7, 9, 1});
            index = addOID(-477711383, new byte[]{43, 6, 1, 5, 5, 7, 9, 2});
            index = addOID(-477710359, new byte[]{43, 6, 1, 5, 5, 7, 9, 3});
            index = addOID(-477709335, new byte[]{43, 6, 1, 5, 5, 7, 9, 4});
            index = addOID(-477708311, new byte[]{43, 6, 1, 5, 5, 7, 9, 5});
            index = addOID(1562319286, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 26, 1});
            index = addOID(1562319294, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 26, 2});
            index = addOID(1562581430, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 27, 1});
            index = addOID(1562581438, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 27, 2});
            index = addOID(-1684020703, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 22, 1});
            index = addOID(-1684020702, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 22, 2});
            index = addOID(-1684020687, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 23, 1});
            index = addOID(314138129, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 1, 1});
            index = addOID(314138130, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 1, 2});
            index = addOID(314138131, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 1, 3});
            index = addOID(314138132, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 1, 4});
            index = addOID(314138133, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 1, 5});
            index = addOID(314138134, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 1, 6});
            index = addOID(312467713, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 10, 1, 1});
            index = addOID(312467714, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 10, 1, 2});
            index = addOID(312467715, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 10, 1, 3});
            index = addOID(312467716, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 10, 1, 4});
            index = addOID(312467717, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 10, 1, 5});
            index = addOID(314564870, new byte[]{42, -122, 72, -122, -9, 13, 1, 12, 10, 1, 6});
            index = addOID(-1721342672, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 11});
            index = addOID(-1721363152, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 1});
            index = addOID(-1721354960, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 5});
            index = addOID(-1721344720, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 10});
            index = addOID(-1721340624, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 12});
            index = addOID(-1721359056, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 3});
            index = addOID(-1721361104, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 2});
            index = addOID(-1721346768, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 9});
            index = addOID(-1721346767, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 4});
            index = addOID(-1721346766, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 2, 7});
            index = addOID(541859388, new byte[]{42, -122, 72, -122, -9, 13, 1, 7, 1});
            index = addOID(542121532, new byte[]{42, -122, 72, -122, -9, 13, 1, 7, 2});
            index = addOID(542383676, new byte[]{42, -122, 72, -122, -9, 13, 1, 7, 3});
            index = addOID(542645820, new byte[]{42, -122, 72, -122, -9, 13, 1, 7, 4});
            index = addOID(542907964, new byte[]{42, -122, 72, -122, -9, 13, 1, 7, 5});
            index = addOID(543170108, new byte[]{42, -122, 72, -122, -9, 13, 1, 7, 6});
            index = addOID(-1721361112, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 1, 2});
            index = addOID(-1721352925, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 1, 1});
            index = addOID(-1721352920, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 1, 6});
            index = addOID(-1721352904, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 1, 9});
            index = addOID(545398089, new byte[]{43, -127, 5, 16, -122, 72, 63, 0, 2});
            index = addOID(545660233, new byte[]{43, -127, 5, 16, -122, 72, 63, 0, 3});
            index = addOID(549068105, new byte[]{43, -127, 5, 16, -122, 72, 63, 0, 16});
            index = addOID(-1721354952, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 3, 5});
            index = addOID(-1721344712, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 3, 10});
            index = addOID(-1721352904, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 3, 6});
            index = addOID(-1721350856, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 3, 7});
            index = addOID(-1721348808, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 3, 8});
            index = addOID(-1721346760, new byte[]{42, -122, 72, -122, -9, 13, 1, 9, 16, 3, 9});
            index = addOID(-472306990, new byte[]{42, -122, 72, -122, -9, 13, 3, 7});
            addAssociation(index, -5860934937401098689L, "TripleDES");
            index = addOID(-472312110, new byte[]{42, -122, 72, -122, -9, 13, 3, 2});
            addAssociation(index, -5860934937401098689L, "RC2");
            index = addOID(-472310062, new byte[]{42, -122, 72, -122, -9, 13, 3, 4});
            addAssociation(index, -5860934937401098689L, "ARC4");
            index = addOID(-477580343, new byte[]{43, 6, 1, 5, 5, 8, 1, 2});
            addAssociation(index, -5860934937401098689L, "HMAC");
            index = addOID(-472306994, new byte[]{42, -122, 72, -122, -9, 13, 2, 7});
            addAssociation(index, -5860934937401098689L, "HMAC");
            index = addOID(774757737, new byte[]{43, 14, 3, 2, 7});
            addAssociation(index, -5860934937401098689L, "DES");
            index = addOID(536454135, new byte[]{43, 6, 1, 5, 5, 7, 48, 1, 1});
            index = addOID(-1299087525, new byte[]{43, 6, 1, 5, 5, 7, 48, 1, 2});
            index = addOID(-477712439, new byte[]{43, 6, 1, 5, 5, 7, 1, 1});
            index = addOID(-477712251, new byte[]{43, 6, 1, 5, 5, 7, 48, 1});
            index = addOID(-477712250, new byte[]{96, -122, 72, 1, -122, -6, 107, 22, 10});
            index = addOID(-477712249, new byte[]{96, -122, 72, 1, -122, -6, 107, 22, 12});
            index = addOID(-477712248, new byte[]{96, -122, 72, 1, -122, -6, 107, 40, 2});
            index = addOID(-477712247, new byte[]{96, -122, 72, 1, -122, -6, 107, 22, 13});
            index = addOID(-477712246, new byte[]{96, -122, 72, 1, -122, -6, 107, 22, 16});
            commitOIDs();
            _idList = null;
            _oidList = null;
            _associationList = null;
            _objectList = null;
            _multiAssociationList = (long[][][])((long[][])null);
            _multiObjectList = (Object[][])null;
            _hashCodeList = null;
         }
      }
   }
}
