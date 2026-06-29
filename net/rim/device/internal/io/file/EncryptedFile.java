package net.rim.device.internal.io.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.io.DRMIOException;
import net.rim.device.api.io.FileInfo;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.EncryptionUtilities;
import net.rim.device.internal.system.DRMServices;

public final class EncryptedFile {
   private byte[] _context;
   private int _handle;
   private CodeSigningKey _protectionKey;
   private int _headerLength;
   private FileInfo _fileInfo = new FileInfo();
   private boolean _drmForwardLocked;
   public static final int BLOCK_LENGTH;
   private static final int SIGNATURE_LENGTH;

   private EncryptedFile() {
   }

   public static final EncryptedFile readHeader(int handle, DataInputStream in, byte[] masterKey) {
      if (in.readUnsignedByte() == 82 && in.readUnsignedByte() == 69 && in.readUnsignedByte() == 77 && in.readUnsignedByte() == 70) {
         int version = in.readUnsignedByte();
         if (version != 1) {
            throw new IOException("Wrong version");
         }

         EncryptedFile ef = new EncryptedFile();
         int headerLen = in.readShort();
         if (headerLen < 0) {
            throw new IOException("Bad header");
         }

         ef._headerLength = 7 + headerLen;
         byte[] header = new byte[headerLen];
         in.readFully(header);
         FileSystem.seek(handle, ef._headerLength, 0);

         try {
            headerLen = EncryptionUtilities.decrypt(masterKey, header, 0, headerLen, header, 0);
         } catch (RuntimeException e) {
            throw new IOException("Error decrypting header");
         }

         if (headerLen < 32) {
            throw new IOException("Bad header2");
         }

         SHA256Digest digest = new SHA256Digest();
         digest.update(header, 0, headerLen - 32);
         if (!Arrays.equals(digest.getDigest(), 0, header, headerLen - 32, 32)) {
            throw new IOException("Bad digest");
         }

         DataInputStream bytesIn = new DataInputStream(new ByteArrayInputStream(header, 0, headerLen));
         if (bytesIn.readByte() == 0 && bytesIn.readByte() == 0) {
            int codeSigningId = bytesIn.readInt();
            if (codeSigningId != 0) {
               int codeSigningLength = bytesIn.readShort();
               if (codeSigningLength == 0) {
                  ef._protectionKey = CodeSigningKey.getBuiltInKey(codeSigningId);
               } else {
                  if (codeSigningLength < 0) {
                     throw new IOException("Bad public key");
                  }

                  byte[] publicKey = new byte[codeSigningLength];
                  bytesIn.readFully(publicKey);
                  ef._protectionKey = new CodeSigningKey(codeSigningId, publicKey, null);
               }
            }

            short sessionKeyLength = bytesIn.readShort();
            if (sessionKeyLength < 0) {
               throw new IOException("Bad session key");
            }

            byte[] sessionKey = new byte[sessionKeyLength];
            switch (bytesIn.readUnsignedByte()) {
               case 0:
                  throw new IOException("Bad key usage");
               case 1:
               default:
                  bytesIn.readFully(sessionKey);
                  break;
               case 2:
                  byte[] mac = new byte[32];
                  bytesIn.readFully(mac);
                  digest.reset();
                  short encryptedLength = bytesIn.readShort();
                  if (encryptedLength < 0) {
                     throw new IOException("Bad session key");
                  }

                  byte[] encryptedKey = new byte[encryptedLength];
                  bytesIn.readFully(encryptedKey);
                  boolean keyFound = false;

                  try {
                     byte[] subKey = DRMServices.getSubscriberKey();
                     if (subKey != null && EncryptionUtilities.decrypt(subKey, encryptedKey, 0, encryptedKey.length, sessionKey, 0) == sessionKeyLength) {
                        digest.update(sessionKey);
                        keyFound = Arrays.equals(digest.getDigest(), mac);
                     }
                  } catch (RuntimeException var18) {
                  }

                  if (keyFound) {
                     bytesIn.skip(encryptedLength);
                  } else {
                     digest.reset();
                     bytesIn.readFully(encryptedKey);

                     try {
                        if (EncryptionUtilities.decrypt(DRMServices.getDeviceKey(), encryptedKey, 0, encryptedKey.length, sessionKey, 0) == sessionKeyLength) {
                           digest.update(sessionKey);
                           keyFound = Arrays.equals(digest.getDigest(), mac);
                        }
                     } catch (RuntimeException var17) {
                     }
                  }

                  if (!keyFound) {
                     throw new DRMIOException("No matching key found");
                  }

                  ef._drmForwardLocked = true;
            }

            ef._handle = handle;
            ef.init(sessionKey);
            return ef;
         } else {
            throw new IOException("Bad algorithm");
         }
      } else {
         throw new IOException("Bad signature");
      }
   }

   public static final EncryptedFile createFile(int handle, DataOutputStream out, boolean isDRMProtected, byte[] key, CodeSigningKey protectionKey) {
      out.write(82);
      out.write(69);
      out.write(77);
      out.write(70);
      out.write(1);
      NoCopyByteArrayOutputStream bytesOut = new NoCopyByteArrayOutputStream();
      DataOutputStream dataOut = new DataOutputStream(bytesOut);
      dataOut.write(0);
      dataOut.write(0);
      if (protectionKey == null) {
         dataOut.writeInt(0);
      } else {
         int signerId = protectionKey.getSignerIdAsInt();
         dataOut.writeInt(signerId);
         switch (signerId) {
            case 51:
            case 4342354:
            case 4408146:
            case 4801362:
            case 5391186:
            case 5526098:
               dataOut.writeShort(0);
               break;
            default:
               byte[] keyData = protectionKey.getPublicKey();
               dataOut.writeShort(keyData.length);
               dataOut.write(keyData);
         }
      }

      byte[] sessionKey = new byte[32];
      RandomSource.getBytes(sessionKey);
      dataOut.writeShort(sessionKey.length);
      SHA256Digest digest = new SHA256Digest();
      if (!isDRMProtected) {
         dataOut.write(1);
         dataOut.write(sessionKey);
      } else {
         dataOut.write(2);
         digest.update(sessionKey);
         dataOut.write(digest.getDigest());
         digest.reset();
         byte[] encryptedKey = new byte[EncryptionUtilities.getCiphertextLength(sessionKey.length)];
         dataOut.writeShort(encryptedKey.length);
         byte[] subKey = DRMServices.getSubscriberKey();
         if (subKey != null) {
            EncryptionUtilities.encrypt(subKey, sessionKey, 0, sessionKey.length, encryptedKey, 0);
         }

         dataOut.write(encryptedKey);
         EncryptionUtilities.encrypt(DRMServices.getDeviceKey(), sessionKey, 0, sessionKey.length, encryptedKey, 0);
         dataOut.write(encryptedKey);
      }

      digest.update(bytesOut.getByteArray(), 0, bytesOut.size());
      dataOut.write(digest.getDigest());
      byte[] cipher = new byte[EncryptionUtilities.getCiphertextLength(bytesOut.size())];
      EncryptionUtilities.encrypt(key, bytesOut.getByteArray(), 0, bytesOut.size(), cipher, 0);
      out.writeShort(cipher.length);
      out.write(cipher);
      EncryptedFile ef = new EncryptedFile();
      ef._protectionKey = protectionKey;
      ef._handle = handle;
      ef.init(sessionKey);
      ef._headerLength = (int)FileSystem.tell(handle);
      return ef;
   }

   public final long getFileSize() {
      FileInfo info = new FileInfo();
      int status = FileSystem.getFileInfo(this._handle, info);
      if (status != 0) {
         throw new FileIOException(status);
      } else {
         long bytesToSkip = (info.getFileSize() - FileSystem.tell(this._handle)) / 16 * 16;
         status = FileSystem.seek(this._handle, bytesToSkip, 1);
         if (status != 0) {
            throw new FileIOException(status);
         } else {
            byte[] lastByte = new byte[1];
            long readStatus = FileSystem.read(this._handle, lastByte);
            if ((int)readStatus != 0) {
               throw new FileIOException((int)readStatus);
            } else {
               int size = (int)(readStatus >> 32);
               if (size != 1) {
                  return 0;
               } else if (lastByte[0] > 0 && lastByte[0] <= 16) {
                  return bytesToSkip - (16 - lastByte[0]);
               } else {
                  throw new FileIOException(6);
               }
            }
         }
      }
   }

   public final int getHeaderLength() {
      return this._headerLength;
   }

   public final CodeSigningKey getCodeSigningKey() {
      return this._protectionKey;
   }

   public final boolean isDrmForwardLocked() {
      return this._drmForwardLocked;
   }

   private final native void init(byte[] var1);

   public final native long readBlocks(byte[] var1);

   public final native int writeBlocks(byte[] var1, int var2, int var3);

   public final native int truncate(long var1);
}
