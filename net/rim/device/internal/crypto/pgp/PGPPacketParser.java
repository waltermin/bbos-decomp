package net.rim.device.internal.crypto.pgp;

import java.io.InputStream;
import java.util.Vector;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.io.SharedInputStream;
import net.rim.vm.Array;

public class PGPPacketParser {
   private byte[][] _encodings;
   private boolean[] _privateKey;
   private Vector[] _packets;

   public PGPPacketParser(byte[] encoding) {
      this(SharedInputStream.getSharedInputStream(encoding));
   }

   public PGPPacketParser(InputStream input) {
      SharedInputStream sharedInput = null;
      if (!(input instanceof SharedInputStream)) {
         sharedInput = SharedInputStream.getSharedInputStream(input);
      } else {
         sharedInput = (SharedInputStream)input;
      }

      this.parse(sharedInput);
   }

   public PGPPacket[][] getPackets() {
      int length = this._packets.length;
      PGPPacket[][] packets = new PGPPacket[length][];

      for (int i = 0; i < length; i++) {
         int size = this._packets[i].size();
         packets[i] = new PGPPacket[size];

         for (int j = 0; j < size; j++) {
            packets[i][j] = (PGPPacket)this._packets[i].elementAt(j);
         }
      }

      return packets;
   }

   public byte[][] getEncodings() {
      return this._encodings;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void parse(SharedInputStream input) {
      this._encodings = new byte[1][];
      this._privateKey = new boolean[1];
      this._packets = new Vector[1];
      boolean keyPacketFound = false;
      int currentKeyStartPosition = input.getCurrentPosition();
      int currentKeyIndex = 0;
      Vector currentKeyPackets = new Vector();
      this._packets[0] = currentKeyPackets;

      while (true) {
         int currentPacketStartPosition = input.getCurrentPosition();
         boolean var16 = false /* VF: Semaphore variable */;

         byte[] currentPacketEncoding;
         int tag;
         try {
            var16 = true;
            int[] e = new int[2];
            currentPacketEncoding = PGPUtilities.readTagAndLength(input, e);
            tag = e[0];
            if (currentPacketEncoding == null) {
               int length = e[1];
               if (length < 0) {
                  throw new PGPEncodingException();
               }

               currentPacketEncoding = new byte[length];
               int numBytesRead = input.read(currentPacketEncoding);
               if (numBytesRead != length) {
                  throw new PGPEncodingException();
               }

               var16 = false;
            } else {
               int length = currentPacketEncoding.length;
               var16 = false;
            }
         } finally {
            if (var16) {
               this._encodings[currentKeyIndex] = this.readEncoding(input, currentKeyStartPosition, currentPacketStartPosition);
               return;
            }
         }

         switch (tag) {
            case 2:
               try {
                  currentKeyPackets.addElement(new PGPSignaturePacket(tag, currentPacketEncoding));
                  break;
               } finally {
                  break;
               }
            case 5:
            case 6:
               if (keyPacketFound) {
                  this._encodings[currentKeyIndex] = this.readEncoding(input, currentKeyStartPosition, currentPacketStartPosition);
                  Array.resize(this._encodings, ++currentKeyIndex + 1);
                  Array.resize(this._privateKey, currentKeyIndex + 1);
                  Array.resize(this._packets, currentKeyIndex + 1);
                  currentKeyPackets = new Vector();
                  this._packets[currentKeyIndex] = currentKeyPackets;
                  currentKeyStartPosition = currentPacketStartPosition;
               }

               keyPacketFound = true;
               switch (tag) {
                  case 4:
                     continue;
                  case 5:
                     this._privateKey[currentKeyIndex] = true;
                     currentKeyPackets.addElement(new PGPPrivateKeyPacket(tag, currentPacketEncoding));
                     continue;
                  case 6:
                  default:
                     currentKeyPackets.addElement(new PGPPublicKeyPacket(tag, currentPacketEncoding));
                     continue;
               }
            case 7:
               this._privateKey[currentKeyIndex] = true;
               currentKeyPackets.addElement(new PGPPrivateKeyPacket(tag, currentPacketEncoding));
               break;
            case 12:
               currentKeyPackets.addElement(new PGPTrustPacket(tag, currentPacketEncoding));
               break;
            case 13:
               currentKeyPackets.addElement(new PGPUserIDPacket(tag, currentPacketEncoding));
               break;
            case 14:
               currentKeyPackets.addElement(new PGPPublicKeyPacket(tag, currentPacketEncoding));
               break;
            case 17:
               currentKeyPackets.addElement(new PGPUserAttributePacket(tag, currentPacketEncoding));
               break;
            default:
               currentKeyPackets.addElement(new PGPUnsupportedPacket(tag, currentPacketEncoding));
         }
      }
   }

   public boolean[] isPrivateKey() {
      return this._privateKey;
   }

   private byte[] readEncoding(SharedInputStream input, int startPosition, int endPosition) {
      int currentPosition = input.getCurrentPosition();
      byte[] encoding = new byte[endPosition - startPosition];
      input.setCurrentPosition(startPosition);
      int length = input.read(encoding);
      input.setCurrentPosition(currentPosition);
      if (length < 0) {
         return new byte[0];
      }

      if (length != encoding.length) {
         Array.resize(encoding, length);
      }

      return encoding;
   }
}
