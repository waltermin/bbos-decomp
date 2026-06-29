package net.rim.device.api.crypto.pgp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.internal.crypto.pgp.PGPPublicKeyEncryptedSessionKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPSignaturePacket;
import net.rim.device.internal.crypto.pgp.PGPSymmetricKeyEncryptedSessionKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.vm.Array;

public class PGPInputStream extends InputStream {
   private byte[] _buffer;
   protected KeyStore _keyStore;
   protected InputStream _input;

   protected PGPInputStream(InputStream input, KeyStore keyStore) {
      if (input == null) {
         throw new IllegalArgumentException();
      }

      this._input = input;
      this._keyStore = keyStore;
   }

   public static PGPInputStream getPGPInputStream(InputStream input, KeyStore keyStore) {
      return getPGPInputStream(input, keyStore, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static PGPInputStream getPGPInputStream(InputStream input, KeyStore keyStore, boolean displayUI) {
      try {
         SharedInputStream sharedInput;
         if (!(input instanceof SharedInputStream)) {
            sharedInput = SharedInputStream.getSharedInputStream(input);
         } else {
            sharedInput = (SharedInputStream)input;
         }

         int[] parameters = new int[2];

         while (true) {
            byte[] encoding = PGPUtilities.readTagAndLength(sharedInput, parameters);
            int tag = parameters[0];
            int length = parameters[1];
            if (encoding != null) {
               length = encoding.length;
            }

            InputStream packetInput;
            if (encoding != null) {
               packetInput = new ByteArrayInputStream(encoding);
            } else {
               packetInput = sharedInput.readInputStream();
               ((SharedInputStream)packetInput).setLength(length);
            }

            if (tag == 11) {
               if (encoding == null) {
                  sharedInput.skip(length);
               }

               return new PGPLiteralInputStream(packetInput);
            }

            if (tag == 8) {
               if (encoding == null) {
                  sharedInput.skip(length);
               }

               return new PGPCompressedInputStream(packetInput, keyStore, displayUI);
            }

            if (tag == 1 || tag == 3 || tag == 9 || tag == 18) {
               Vector keyPackets = new Vector();

               while (tag != 9 && tag != 18) {
                  if (encoding == null) {
                     encoding = new byte[length];
                     sharedInput.read(encoding);
                  }

                  if (tag == 1) {
                     keyPackets.addElement(new PGPPublicKeyEncryptedSessionKeyPacket(tag, encoding));
                  } else {
                     if (tag != 3) {
                        throw new PGPEncodingException("Tag:" + tag);
                     }

                     keyPackets.addElement(new PGPSymmetricKeyEncryptedSessionKeyPacket(tag, encoding));
                  }

                  encoding = PGPUtilities.readTagAndLength(sharedInput, parameters);
                  tag = parameters[0];
                  length = parameters[1];
                  if (encoding != null) {
                     length = encoding.length;
                  }
               }

               if (encoding != null) {
                  packetInput = new ByteArrayInputStream(encoding);
               } else {
                  packetInput = sharedInput.readInputStream();
                  ((SharedInputStream)packetInput).setLength(length);
                  sharedInput.skip(length);
               }

               return new PGPEncryptedInputStream(packetInput, keyStore, keyPackets, tag == 18, displayUI);
            }

            if (tag == 2) {
               Vector signaturePackets = new Vector();
               int currentPosition = sharedInput.getCurrentPosition();

               while (tag == 2) {
                  if (encoding == null) {
                     encoding = new byte[parameters[1]];
                     sharedInput.read(encoding);
                  }

                  label831:
                  try {
                     signaturePackets.addElement(new PGPSignaturePacket(tag, encoding));
                  } finally {
                     break label831;
                  }

                  currentPosition = sharedInput.getCurrentPosition();
                  boolean var47 = false /* VF: Semaphore variable */;

                  try {
                     var47 = true;
                     encoding = PGPUtilities.readTagAndLength(sharedInput, parameters);
                     var47 = false;
                  } finally {
                     if (var47) {
                        sharedInput.setCurrentPosition(currentPosition);
                        return new PGPSignedInputStream(new ByteArrayInputStream(new byte[0]), keyStore, signaturePackets, null, true, true);
                     }
                  }

                  tag = parameters[0];
                  length = parameters[1];
                  if (encoding != null) {
                     length = encoding.length;
                  }
               }

               int packetLength = sharedInput.getCurrentPosition() - currentPosition + length;
               sharedInput.setCurrentPosition(currentPosition);
               byte[] mess = new byte[packetLength];
               int messLength = sharedInput.read(mess);
               Array.resize(mess, messLength);
               sharedInput.setCurrentPosition(currentPosition);
               PGPInputStream message = getPGPInputStream(sharedInput, keyStore, displayUI);
               return new PGPSignedInputStream(message, keyStore, signaturePackets, mess, false, displayUI);
            }

            if (tag == 4) {
               Vector signaturePackets = new Vector();
               int currentPosition = sharedInput.getCurrentPosition();

               while (tag == 4) {
                  if (encoding == null) {
                     sharedInput.skip(length);
                  }

                  currentPosition = sharedInput.getCurrentPosition();
                  encoding = PGPUtilities.readTagAndLength(sharedInput, parameters);
                  tag = parameters[0];
                  length = parameters[1];
                  if (encoding != null) {
                     length = encoding.length;
                  }
               }

               byte[] mess = null;
               PGPInputStream message = null;
               if (tag != 2) {
                  int packetLength = sharedInput.getCurrentPosition() - currentPosition + length;
                  sharedInput.setCurrentPosition(currentPosition);
                  mess = new byte[packetLength];
                  int messLength = sharedInput.read(mess);
                  Array.resize(mess, messLength);
                  sharedInput.setCurrentPosition(currentPosition);
                  message = getPGPInputStream(sharedInput, keyStore, displayUI);

                  label813:
                  try {
                     encoding = PGPUtilities.readTagAndLength(sharedInput, parameters);
                     tag = parameters[0];
                     length = parameters[1];
                     if (encoding != null) {
                        length = encoding.length;
                     }
                  } finally {
                     break label813;
                  }
               }

               while (tag == 2) {
                  if (encoding == null) {
                     encoding = new byte[parameters[1]];
                     sharedInput.read(encoding);
                  }

                  label805:
                  try {
                     signaturePackets.addElement(new PGPSignaturePacket(tag, encoding));
                  } finally {
                     break label805;
                  }

                  currentPosition = sharedInput.getCurrentPosition();
                  boolean var28 = false /* VF: Semaphore variable */;

                  try {
                     var28 = true;
                     encoding = PGPUtilities.readTagAndLength(sharedInput, parameters);
                     var28 = false;
                  } finally {
                     if (var28) {
                        sharedInput.setCurrentPosition(currentPosition);
                        break;
                     }
                  }

                  tag = parameters[0];
                  length = parameters[1];
                  if (encoding != null) {
                     length = encoding.length;
                  }
               }

               if (message == null) {
                  return new PGPSignedInputStream(new ByteArrayInputStream(new byte[0]), keyStore, signaturePackets, null, true, displayUI);
               }

               return new PGPSignedInputStream(message, keyStore, signaturePackets, mess, false, displayUI);
            }

            if (encoding == null) {
               if (parameters[1] < 0) {
                  sharedInput.skip(Integer.MAX_VALUE);
               } else {
                  sharedInput.skip(parameters[1]);
               }
            }
         }
      } finally {
         ;
      }
   }

   public PGPInputStream getPGPInputStream() {
      return !(this._input instanceof PGPInputStream) ? null : (PGPInputStream)this._input;
   }

   public PGPInputStream getNextStream() {
      return null;
   }

   @Override
   public int read(byte[] _1, int _2, int _3) {
      throw null;
   }

   @Override
   public int read() {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      return this.read(this._buffer, 0, 1) < 0 ? -1 : this._buffer[0] & 0xFF;
   }

   @Override
   public int read(byte[] b) {
      return this.read(b, 0, b.length);
   }

   @Override
   public long skip(long n) {
      return this._input.skip(n);
   }

   @Override
   public int available() {
      return this._input.available();
   }

   @Override
   public void close() {
      this._input.close();
   }

   public String getType() {
      throw null;
   }
}
