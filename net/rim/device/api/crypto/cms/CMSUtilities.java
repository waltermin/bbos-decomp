package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

final class CMSUtilities {
   private CMSUtilities() {
   }

   public static final boolean isCertificateAllowed(Certificate certificate, long usage1, long usage2) {
      return certificate != null && (certificate.queryKeyUsage(usage1) != 0 || certificate.queryKeyUsage(usage2) != 0);
   }

   public static final String getSignatureDigestName(CMSEntityIdentifier param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.getDigestAlgorithm ()[B
      // 04: astore 1
      // 05: aload 1
      // 06: invokestatic net/rim/device/api/crypto/cms/CMSUtilities.getSignatureDigest ([B)Lnet/rim/device/api/crypto/Digest;
      // 09: invokeinterface net/rim/device/api/crypto/Digest.getAlgorithm ()Ljava/lang/String; 1
      // 0e: areturn
      // 0f: astore 1
      // 10: aconst_null
      // 11: areturn
      // 12: astore 1
      // 13: aconst_null
      // 14: areturn
      // try (0 -> 6): 7 null
      // try (0 -> 6): 10 null
   }

   public static final Digest getSignatureDigest(byte[] digestAlgorithmIdentifier) {
      ASN1InputByteArray digestAlgorithm = (ASN1InputByteArray)(new Object(digestAlgorithmIdentifier));
      digestAlgorithm.readSequence();
      OID digestOID = digestAlgorithm.readOID();
      Digest digest = null;
      if (digestOID.equals(OIDs.getOID(774767465))) {
         return (Digest)(new Object());
      } else if (digestOID.equals(OIDs.getOID(-472309042))) {
         return (Digest)(new Object());
      } else if (digestOID.equals(OIDs.getOID(540600180))) {
         return (Digest)(new Object());
      } else if (digestOID.equals(OIDs.getOID(540862324))) {
         return (Digest)(new Object());
      } else if (digestOID.equals(OIDs.getOID(541124468))) {
         return (Digest)(new Object());
      } else {
         throw new Object();
      }
   }
}
