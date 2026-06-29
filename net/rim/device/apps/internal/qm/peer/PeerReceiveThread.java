package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.gme.GMEAddress;
import net.rim.device.cldc.io.gme.GMETarget;

final class PeerReceiveThread extends Thread {
   private DatagramConnectionBase _connection;
   private PeerController _controller;
   private boolean _alive;
   private DatagramBase _dg;

   public PeerReceiveThread(DatagramConnectionBase connection, PeerController controller) {
      this._connection = connection;
      this._controller = controller;
      this._alive = true;
      this._dg = (DatagramBase)this._connection.newDatagram(0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         boolean var7 = false /* VF: Semaphore variable */;

         label77: {
            label76: {
               try {
                  label74:
                  try {
                     var7 = true;
                     if (!this._alive) {
                        var7 = false;
                        break label77;
                     }

                     this._connection.receive(this._dg);
                     EventLogger.logEvent(-9029900896793868512L, 1381516132, 0);
                     String src = this.extractSrc(this._dg);
                     if (src != null) {
                        src = src.toUpperCase();
                     }

                     PeerDataBlob[] blob = this.parseDatagram(this._dg);
                     this._controller.handleDataBlobs(src, blob);
                     var7 = false;
                     break label76;
                  } catch (Throwable var10) {
                     t.printStackTrace();
                     var7 = false;
                     break label74;
                  }
               } finally {
                  if (var7) {
                     this._dg.reset();
                     String src = null;
                     PeerDataBlob[] blob = null;
                  }
               }

               this._dg.reset();
               String var12 = null;
               PeerDataBlob[] var16 = null;
               continue;
            }

            this._dg.reset();
            String var14 = null;
            PeerDataBlob[] var18 = null;
            continue;
         }

         this._dg.reset();
         String src = null;
         PeerDataBlob[] blob = null;
         return;
      }
   }

   private final String extractSrc(DatagramBase dg) {
      DatagramAddressBase addr = dg.getAddressBase();
      if (addr instanceof Object) {
         GMETarget targ = ((GMEAddress)addr).getSrc();
         if (targ != null) {
            return targ.address;
         }
      }

      return null;
   }

   private final PeerDataBlob[] parseDatagram(DatagramBase dg) {
      int version = dg.readByte();
      PeerDataBlob[] blobs = new PeerDataBlob[0];
      if ((version & 240) != 16) {
         throw new Object("Invalid version");
      }

      int type;
      while ((type = dg.readByte()) != 0) {
         int length = dg.readCompressedInt();
         PeerDataBlob blob = null;
         switch (type) {
            case 0:
            case 3:
            case 4:
            case 5:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
               dg.skipBytes(length);
               continue;
            case 1:
            default:
               blob = new SimpleMsgDataBlob();
               break;
            case 2:
               blob = new NotificationDataBlob();
               break;
            case 6:
               blob = new InviteAcceptedDataBlob();
               break;
            case 7:
               blob = new UserInfoDataBlob();
               break;
            case 8:
               blob = new ReadReceiptDataBlob();
               break;
            case 9:
               blob = new ConvInviteBlob();
               break;
            case 16:
               blob = new ConvAcceptBlob();
               break;
            case 17:
               blob = new ConvJoinBlob();
               break;
            case 18:
               blob = new ConvRemBlob();
               break;
            case 19:
               blob = new ChangeInfoBlob();
               break;
            case 20:
               blob = new DeleteContactBlob();
               break;
            case 21:
               blob = new PasswordKeyBlob();
               break;
            case 22:
               blob = new FileTransferBlob();
               break;
            case 23:
               blob = new VerifyHashBlob();
               break;
            case 24:
               blob = new SessionBlob();
         }

         blob.unPickle(dg, length);
         Arrays.add(blobs, blob);
      }

      return blobs;
   }
}
