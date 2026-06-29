package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.ldap.LDAPEntry;
import net.rim.device.api.ldap.LDAPQuery;

public class PGPLDAPCertificateFetch extends LDAPCertificateFetch {
   @Override
   protected void addAttributesAndFilters(LDAPQuery query, String[] emailAddresses) {
      query.addAttribute(PGPCertificate.PGP_KEY);
      StringBuffer buffer = (StringBuffer)(new Object());
      int numEmailAddresses = emailAddresses != null ? emailAddresses.length : 0;
      if (numEmailAddresses > 0) {
         buffer.append('(').append('|');

         for (int i = 0; i < numEmailAddresses; i++) {
            buffer.append('(').append(PGPCertificate.PGP_USER_ID).append("=*<").append(emailAddresses[i]).append(">*").append(')');
         }

         buffer.append(')');
         query.addFilter(buffer.toString());
      }
   }

   @Override
   protected void addAttributesAndFilters(LDAPQuery query, Object[] keyIDs) {
      query.addAttribute(PGPCertificate.PGP_KEY);
      StringBuffer buffer = (StringBuffer)(new Object());
      int numKeyIDs = keyIDs != null ? keyIDs.length : 0;
      if (numKeyIDs > 0) {
         buffer.append('(').append('|');

         for (int i = 0; i < numKeyIDs; i++) {
            Object var10000 = keyIDs[i];
            if (keyIDs[i] instanceof byte[]) {
               byte[] currKeyID = (byte[])var10000;
               buffer.append('(').append(PGPCertificate.PGP_CERT_ID).append("=");

               for (int j = 0; j < currKeyID.length; j++) {
                  buffer.append(this.byteToHex(currKeyID[j]));
               }

               buffer.append(")");
            }
         }

         buffer.append(')');
         query.addFilter(buffer.toString());
      }
   }

   @Override
   protected void addCertificates(LDAPEntry param1, Certificate[] param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 08
      // 04: aload 2
      // 05: ifnonnull 10
      // 08: new java/lang/Object
      // 0b: dup
      // 0c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0f: athrow
      // 10: aconst_null
      // 11: astore 3
      // 12: aload 1
      // 13: getstatic net/rim/device/api/crypto/certificate/pgp/PGPCertificate.PGP_KEY Ljava/lang/String;
      // 16: invokeinterface net/rim/device/api/ldap/LDAPEntry.getAttribute (Ljava/lang/String;)Lnet/rim/device/api/ldap/LDAPAttribute; 2
      // 1b: astore 3
      // 1c: goto 22
      // 1f: astore 4
      // 21: return
      // 22: aload 3
      // 23: invokeinterface net/rim/device/api/ldap/LDAPAttribute.getSize ()I 1
      // 28: istore 4
      // 2a: bipush 0
      // 2b: istore 5
      // 2d: iload 5
      // 2f: iload 4
      // 31: if_icmpge 8e
      // 34: aload 3
      // 35: iload 5
      // 37: invokeinterface net/rim/device/api/ldap/LDAPAttribute.getValue (I)Ljava/lang/Object; 2
      // 3c: checkcast java/lang/Object
      // 3f: astore 6
      // 41: new net/rim/device/api/crypto/pgp/PGPArmorDecoder
      // 44: dup
      // 45: new java/lang/Object
      // 48: dup
      // 49: aload 6
      // 4b: invokevirtual java/lang/String.getBytes ()[B
      // 4e: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 51: invokespecial net/rim/device/api/crypto/pgp/PGPArmorDecoder.<init> (Ljava/io/InputStream;)V
      // 54: astore 7
      // 56: aload 7
      // 58: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.numCertificates ()I
      // 5b: istore 8
      // 5d: bipush 0
      // 5e: istore 9
      // 60: iload 9
      // 62: iload 8
      // 64: if_icmpge 88
      // 67: aload 7
      // 69: iload 9
      // 6b: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getCertificate (I)Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 6e: astore 10
      // 70: aload 10
      // 72: ifnull 7b
      // 75: aload 2
      // 76: aload 10
      // 78: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 7b: iinc 9 1
      // 7e: goto 60
      // 81: astore 7
      // 83: goto 88
      // 86: astore 7
      // 88: iinc 5 1
      // 8b: goto 2d
      // 8e: return
      // try (10 -> 14): 15 null
      // try (30 -> 58): 58 null
      // try (30 -> 58): 60 null
   }

   private String byteToHex(byte b) {
      int i = b & 255;
      String hexString = Integer.toHexString(i);
      if (i < 16) {
         hexString = ((StringBuffer)(new Object("0"))).append(hexString).toString();
      }

      return hexString.toUpperCase();
   }
}
