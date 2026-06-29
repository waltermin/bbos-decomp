package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class PGPUserAttributePacket extends PGPPacket implements Persistable {
   byte[][][] _userImageEncodings = new byte[0][][];
   private static final byte SUBPACKET_TYPE_IMAGE;
   private static final byte[] VERSION_1_JPEG_HEADER = new byte[]{16, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   public PGPUserAttributePacket(int tag, byte[] encoding) {
      super(tag, encoding);
      if (EncodedImage.isMIMETypeSupported("image/jpeg")) {
         int offset = 0;
         int encodingLength = encoding.length;

         while (offset < encodingLength) {
            int temp = encoding[offset++] & 255;
            int subPacketLength;
            if (temp >= 0 && temp < 192) {
               subPacketLength = temp;
            } else if (temp >= 192 && temp < 255) {
               subPacketLength = temp - 192 << 8;
               subPacketLength += encoding[offset++] & 255;
               subPacketLength += 192;
            } else {
               if (temp != 255) {
                  throw new PGPEncodingException("InvL");
               }

               subPacketLength = (encoding[offset++] & 255) << 24;
               subPacketLength |= (encoding[offset++] & 255) << 16;
               subPacketLength |= (encoding[offset++] & 255) << 8;
               subPacketLength |= encoding[offset++] & 255;
            }

            int subPacketType = encoding[offset] & 255;
            if (subPacketType == 1 && Arrays.equals(encoding, offset + 1, VERSION_1_JPEG_HEADER, 0, VERSION_1_JPEG_HEADER.length)) {
               byte[] userImageEncoding = new byte[subPacketLength - 1 - VERSION_1_JPEG_HEADER.length];
               System.arraycopy(encoding, offset + 1 + VERSION_1_JPEG_HEADER.length, userImageEncoding, 0, userImageEncoding.length);
               Arrays.add(this._userImageEncodings, userImageEncoding);
            }

            offset += subPacketLength;
         }
      }
   }

   public final Bitmap[] getImages() {
      Bitmap[] userImages = new Object[0];
      int numUserImageEncodings = this._userImageEncodings.length;

      for (int i = 0; i < numUserImageEncodings; i++) {
         byte[] currentUserImageEncoding = (byte[])this._userImageEncodings[i];
         EncodedImage encodedImage = EncodedImage.createEncodedImage(currentUserImageEncoding, 0, currentUserImageEncoding.length, "image/jpeg");
         if (encodedImage != null) {
            Bitmap encodedImageBitmap = encodedImage.getBitmap();
            if (encodedImageBitmap != null) {
               Arrays.add(userImages, encodedImageBitmap);
            }
         }
      }

      return userImages;
   }
}
