package net.rim.device.api.crypto.certificate.pgp;

import java.util.Vector;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.crypto.pgp.PGPPacket;
import net.rim.device.internal.crypto.pgp.PGPPublicKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPSignaturePacket;
import net.rim.device.internal.crypto.pgp.PGPSignatureSubPacketParser;
import net.rim.device.internal.crypto.pgp.PGPTrustPacket;
import net.rim.device.internal.crypto.pgp.PGPUnsupportedPacket;
import net.rim.device.internal.crypto.pgp.PGPUserAttributePacket;
import net.rim.device.internal.crypto.pgp.PGPUserIDPacket;

class Content implements Persistable {
   protected byte[] _encoding;
   protected int _version;
   protected PGPPublicKeyPacket _publicKey;
   protected byte[] _keyID;
   protected int _publicKeyAlgorithm;
   protected long _notBefore;
   protected long _notAfter;
   protected String _friendlyName;
   protected int _signatureAlgorithm;
   protected byte[] _fingerprint;
   protected byte[] _preferredSymmetricAlgorithms;
   protected int _trustState;
   protected PGPUserIDPacket[] _userIDs = new PGPUserIDPacket[0];
   protected PGPUserIDPacket _primaryUserID;
   protected PGPUserAttributePacket[] _userAttributes = new PGPUserAttributePacket[0];
   protected PGPPublicKeyPacket[] _subKeys = new PGPPublicKeyPacket[0];
   protected byte[][] _subKeyIDs = new byte[0][];
   protected long[] _subKeyNotBefore = new long[0];
   protected long[] _subKeyNotAfter = new long[0];
   protected PGPUnsupportedPacket[] _unsupportedPackets = new PGPUnsupportedPacket[0];
   protected PGPSignaturePacket[] _parentKeySignatures = new PGPSignaturePacket[0];
   protected PGPSignaturePacket[][] _userIDSignatures = new PGPSignaturePacket[0][];
   protected PGPSignaturePacket[][] _userAttributeSignatures = new PGPSignaturePacket[0][];
   protected PGPSignaturePacket[][] _subKeySignatures = new PGPSignaturePacket[0][];
   protected PGPSignaturePacket[][] _unsupportedSignatures = new PGPSignaturePacket[0][];
   protected PGPEmbeddedCertificate[] _x509EmbeddedCertificates = new PGPEmbeddedCertificate[0];

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Content(byte[] encoding, PGPPacket[] packets) {
      if (encoding != null && packets != null) {
         this._encoding = encoding;
         PGPSignaturePacket[] currentSignatureArray = null;
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;

            for (PGPPacket currentPacket : packets) {
               switch (currentPacket.getTag()) {
                  case 2:
                     if (currentSignatureArray == null) {
                        throw new PGPEncodingException("COrS");
                     }

                     this.handleSignature((PGPSignaturePacket)currentPacket, currentSignatureArray);
                     break;
                  case 5:
                  case 7:
                     throw new PGPEncodingException("CPrK");
                  case 6:
                     currentSignatureArray = this.handlePublicKey((PGPPublicKeyPacket)currentPacket);
                     break;
                  case 12:
                     this.handleTrustPacket((PGPTrustPacket)currentPacket);
                     break;
                  case 13:
                     currentSignatureArray = this.handleUserID((PGPUserIDPacket)currentPacket);
                     break;
                  case 14:
                     currentSignatureArray = this.handleSubKey((PGPPublicKeyPacket)currentPacket);
                     break;
                  case 17:
                     currentSignatureArray = this.handleUserAttribute((PGPUserAttributePacket)currentPacket);
                     break;
                  default:
                     currentSignatureArray = this.handleUnsupportedPacket(currentPacket);
               }
            }

            var8 = false;
         } finally {
            if (var8) {
               throw new PGPEncodingException("CIPT");
            }
         }

         if (this._publicKey != null && this._userIDs.length != 0) {
            if (this._primaryUserID == null) {
               this._primaryUserID = this._userIDs[0];
            }

            this._friendlyName = this._primaryUserID.getName();
         } else {
            throw new PGPEncodingException("CInK");
         }
      } else {
         throw new Object();
      }
   }

   private PGPSignaturePacket[] handlePublicKey(PGPPublicKeyPacket packet) {
      if (this._publicKey != null) {
         throw new PGPEncodingException("CMKs");
      }

      this._publicKey = packet;
      this._publicKeyAlgorithm = packet.getPublicKeyAlgorithm();
      this._version = packet.getVersion();
      this._notBefore = packet.getCreationTime();
      this._notAfter = packet.getExpirationTime();
      if (this._version == 3 && this._notAfter == 0) {
         this._notAfter = Long.MAX_VALUE;
      }

      this._fingerprint = packet.getFingerprint();
      this._keyID = packet.getKeyID();
      return this._parentKeySignatures;
   }

   private PGPSignaturePacket[] handleSubKey(PGPPublicKeyPacket packet) {
      Arrays.add(this._subKeys, packet);
      Arrays.add(this._subKeyIDs, packet.getKeyID());
      Arrays.add(this._subKeyNotBefore, packet.getCreationTime());
      Arrays.add(this._subKeyNotAfter, packet.getExpirationTime());
      PGPSignaturePacket[] newSignatures = new PGPSignaturePacket[0];
      Arrays.add(this._subKeySignatures, newSignatures);
      return newSignatures;
   }

   private void handleSignature(PGPSignaturePacket packet, PGPSignaturePacket[] signatureArray) {
      if (this._signatureAlgorithm <= 0) {
         this._signatureAlgorithm = packet.getPublicKeyAlgorithm();
      }

      Arrays.add(signatureArray, packet);
      if (Arrays.equals(packet.getSignerKeyID(), this._keyID) && packet.getVersion() == 4) {
         Vector subSignatures = packet.getSignatureSubPackets();
         PGPSignatureSubPacketParser parser = new PGPSignatureSubPacketParser(subSignatures);
         switch (packet.getSignatureType()) {
            case 16:
            case 17:
            case 18:
            case 19:
               if (parser.isPrimaryUserID()) {
                  this._primaryUserID = this._userIDs[this._userIDs.length - 1];
                  this._notAfter = this.getNotAfter(this._notBefore, parser);
                  this._preferredSymmetricAlgorithms = parser.getPreferredSymmetricAlgorithms();
               } else {
                  if (this._notAfter == Long.MAX_VALUE) {
                     this._notAfter = this.getNotAfter(this._notBefore, parser);
                  }

                  if (this._preferredSymmetricAlgorithms == null) {
                     this._preferredSymmetricAlgorithms = parser.getPreferredSymmetricAlgorithms();
                  }
               }
               break;
            case 24:
               int subKeyOffset = this._subKeyNotAfter.length - 1;
               this._subKeyNotAfter[subKeyOffset] = this.getNotAfter(this._subKeyNotBefore[subKeyOffset], parser);
         }
      }

      if (packet.containsX509Certificate()) {
         Arrays.add(this._x509EmbeddedCertificates, new PGPEmbeddedCertificate(this._keyID, packet));
      }
   }

   private long getNotAfter(long notBefore, PGPSignatureSubPacketParser parser) {
      long keyExpirationTime = parser.getKeyExpirationTime();
      return keyExpirationTime == 0 ? Long.MAX_VALUE : notBefore + keyExpirationTime;
   }

   private PGPSignaturePacket[] handleUserID(PGPUserIDPacket packet) {
      Arrays.add(this._userIDs, packet);
      PGPSignaturePacket[] newSignatures = new PGPSignaturePacket[0];
      Arrays.add(this._userIDSignatures, newSignatures);
      return newSignatures;
   }

   private PGPSignaturePacket[] handleUserAttribute(PGPUserAttributePacket packet) {
      Arrays.add(this._userAttributes, packet);
      PGPSignaturePacket[] newSignatures = new PGPSignaturePacket[0];
      Arrays.add(this._userAttributeSignatures, newSignatures);
      return newSignatures;
   }

   private void handleTrustPacket(PGPTrustPacket packet) {
      if (this._userIDs.length == 0 && this._userIDSignatures.length == 0 && this._subKeySignatures.length == 0 && this._unsupportedSignatures.length == 0) {
         this._trustState = packet.getTrustState();
      }
   }

   private PGPSignaturePacket[] handleUnsupportedPacket(PGPPacket packet) {
      Arrays.add(this._unsupportedPackets, packet);
      PGPSignaturePacket[] newSignatures = new PGPSignaturePacket[0];
      Arrays.add(this._unsupportedSignatures, newSignatures);
      return newSignatures;
   }
}
