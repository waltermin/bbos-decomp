package net.rim.device.internal.crypto.pgp;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.vm.Array;

public class PGPSignatureSubPacketParser implements PGPSignatureSubPacketTags {
   private Vector _subPackets;
   private long _signatureCreationTime;
   private long _signatureExpirationTime;
   private boolean _exportableCertification = true;
   private int _trustAmount = -1;
   private int _trustLevel;
   private byte[] _regularExpression;
   private boolean _revocable = true;
   private long _keyExpirationTime;
   private byte[] _preferredSymmetricAlgorithms;
   private byte _revocationKeyClass;
   private byte _revocationKeyAlgorithmID;
   private byte[] _revocationKeyFingerprint;
   private byte[] _issuerKeyID;
   private byte[] _notationData;
   private byte[] _preferredHashAlgorithms;
   private byte[] _preferredCompressionAlgorithms;
   private byte[] _keyServerPreferences;
   private String _preferredKeyServer;
   private boolean _primaryUserID;
   private String _policyURL;
   private byte[] _keyFlags;
   private byte[] _signersUserID;
   private byte _revocationCode = -1;
   private byte[] _revocationReason;
   private byte[][][] _adkFingerprints;

   public PGPSignatureSubPacketParser(Vector subPackets) {
      if (subPackets == null) {
         throw new Object();
      }

      this._subPackets = subPackets;
      this.parse();
   }

   private void parse() {
      byte[] data = null;
      Enumeration enumeration = this._subPackets.elements();

      while (enumeration.hasMoreElements()) {
         PGPSignatureSubPacket packet = (PGPSignatureSubPacket)enumeration.nextElement();
         int tag = packet.getTag();
         switch (tag) {
            case 1:
            case 8:
            case 13:
            case 14:
            case 15:
            case 17:
            case 18:
            case 19:
               break;
            case 2:
            default:
               data = packet.getEncoding();
               if (data.length != 4) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._signatureCreationTime = PGPUtilities.convertTime(data, 0);
               break;
            case 3:
               data = packet.getEncoding();
               if (data.length != 4) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._signatureExpirationTime = PGPUtilities.convertTime(data, 0);
               break;
            case 4:
               data = packet.getEncoding();
               if (data.length != 1) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._exportableCertification = data[0] == 1;
               break;
            case 5:
               data = packet.getEncoding();
               if (data.length != 2) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._trustLevel = data[0];
               this._trustAmount = data[1];
               break;
            case 6:
               this._regularExpression = packet.getEncoding();
               break;
            case 7:
               data = packet.getEncoding();
               if (data.length != 1) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._revocable = data[0] == 1;
               break;
            case 9:
               data = packet.getEncoding();
               if (data.length != 4) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._keyExpirationTime = PGPUtilities.convertTime(data, 0);
               break;
            case 10:
               data = packet.getEncoding();
               if (data.length != 22) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               int numADKs = this._adkFingerprints == null ? 0 : this._adkFingerprints.length;
               if (this._adkFingerprints == null) {
                  this._adkFingerprints = new byte[1][][];
               } else {
                  Array.resize(this._adkFingerprints, numADKs + 1);
               }

               this._adkFingerprints[numADKs] = (byte[][])(new byte[20]);
               System.arraycopy(data, 2, this._adkFingerprints[numADKs], 0, 20);
               break;
            case 11:
               this._preferredSymmetricAlgorithms = packet.getEncoding();
               break;
            case 12:
               data = packet.getEncoding();
               if (data.length != 22) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._revocationKeyClass = data[0];
               this._revocationKeyAlgorithmID = data[1];
               this._revocationKeyFingerprint = new byte[20];
               System.arraycopy(data, 2, this._revocationKeyFingerprint, 0, 20);
               break;
            case 16:
               this._issuerKeyID = packet.getEncoding();
               if (this._issuerKeyID.length != 8) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }
               break;
            case 20:
               this._notationData = packet.getEncoding();
               break;
            case 21:
               this._preferredHashAlgorithms = packet.getEncoding();
               break;
            case 22:
               this._preferredCompressionAlgorithms = packet.getEncoding();
               break;
            case 23:
               this._keyServerPreferences = packet.getEncoding();
               break;
            case 24:
               this._preferredKeyServer = (String)(new Object(packet.getEncoding()));
               break;
            case 25:
               data = packet.getEncoding();
               if (data.length != 1) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._primaryUserID = data[0] == 1;
               break;
            case 26:
               this._policyURL = (String)(new Object(packet.getEncoding()));
               break;
            case 27:
               this._keyFlags = packet.getEncoding();
               break;
            case 28:
               this._signersUserID = packet.getEncoding();
               break;
            case 29:
               data = packet.getEncoding();
               if (data.length < 1) {
                  throw new PGPEncodingException(((StringBuffer)(new Object("SSLM:"))).append(tag).toString());
               }

               this._revocationCode = data[0];
               this._revocationReason = new byte[data.length - 1];
               System.arraycopy(data, 1, this._revocationReason, 0, data.length - 1);
         }
      }
   }

   public long getSignatureCreationTime() {
      return this._signatureCreationTime;
   }

   public long getSignatureExpirationTime() {
      return this._signatureExpirationTime;
   }

   public boolean isExportableCertification() {
      return this._exportableCertification;
   }

   public int getTrustAmount() {
      return this._trustAmount;
   }

   public int getTrustLevel() {
      return this._trustLevel;
   }

   public byte[] getRegularExpression() {
      return this._regularExpression;
   }

   public boolean isRevocable() {
      return this._revocable;
   }

   public long getKeyExpirationTime() {
      return this._keyExpirationTime;
   }

   public byte[] getPreferredSymmetricAlgorithms() {
      return this._preferredSymmetricAlgorithms;
   }

   public byte getRevocationKeyClass() {
      return this._revocationKeyClass;
   }

   public byte getRevocationKeyAlgorithmID() {
      return this._revocationKeyAlgorithmID;
   }

   public byte[] getRevocationKeyFingerprint() {
      return this._revocationKeyFingerprint;
   }

   public byte[] getIssuerKeyID() {
      return this._issuerKeyID;
   }

   public byte[] getNotationData() {
      return this._notationData;
   }

   public byte[] getPreferredHashAlgorithms() {
      return this._preferredHashAlgorithms;
   }

   public byte[] getPreferredCompressionAlgorithms() {
      return this._preferredCompressionAlgorithms;
   }

   public byte[] getKeyServerPreferences() {
      return this._keyServerPreferences;
   }

   public String getPreferredKeyServer() {
      return this._preferredKeyServer;
   }

   public boolean isPrimaryUserID() {
      return this._primaryUserID;
   }

   public String getPolicyURL() {
      return this._policyURL;
   }

   public byte[] getKeyFlags() {
      return this._keyFlags;
   }

   public byte[] getSignersUserID() {
      return this._signersUserID;
   }

   public byte getRevocationCode() {
      return this._revocationCode;
   }

   public byte[] getRevocationReason() {
      return this._revocationReason;
   }

   public byte[][][] getADKFingerprints() {
      return this._adkFingerprints;
   }
}
