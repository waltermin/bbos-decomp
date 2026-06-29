package net.rim.device.internal.firewall;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class FirewallImpl$FirewallSyncItem extends OTASyncCapableSyncItem {
   private DataBuffer _buffer;
   private boolean _failed;
   private final FirewallImpl this$0;

   FirewallImpl$FirewallSyncItem(FirewallImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final String getSyncName() {
      return "Firewall Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   private final void writeSetting(FirewallImpl$Setting setting) {
      this._buffer.writeByte(1);
      this._buffer.writeByte(1);
      this._buffer.writeByteArray(setting._moduleName.getBytes());
      this._buffer.writeByte(setting._appIndex);
      this._buffer.writeByte(1);
      this._buffer.writeByteArray(setting._protocol.getBytes());
      if (setting._target != null) {
         this._buffer.writeByte(1);
         this._buffer.writeByteArray(setting._target.getBytes());
      } else {
         this._buffer.writeByte(0);
      }

      this._buffer.writeInt(setting._permission);
   }

   private final void writeBlocking(FirewallImpl$Blocking blocking) {
      this._buffer.writeByte(blocking._type);
      this._buffer.writeBoolean(blocking._enabled);
      this._buffer.writeCompressedInt(blocking._count);
   }

   @Override
   public final synchronized boolean getSyncData(DataBuffer buffer, int version) {
      this._failed = false;
      this._buffer = (DataBuffer)(new Object(buffer.isBigEndian()));
      synchronized (this.this$0._settings) {
         int length = this.this$0._settings.size();

         for (int i = 0; i < length; i++) {
            FirewallImpl$Setting s = (FirewallImpl$Setting)this.this$0._settings.elementAt(i);
            this.writeSetting(s);
         }
      }

      this._buffer.writeByte(0);
      synchronized (this.this$0._blockings) {
         for (byte i = 5; i > 0; i--) {
            FirewallImpl$Blocking b = this.this$0.getBlocking(i);
            if (b != null) {
               this.writeBlocking(b);
            }
         }
      }

      this._buffer.writeByte(0);
      int l = this._buffer.getLength();
      this._buffer.rewind();
      if (!this._failed) {
         buffer.writeShort(l);
         buffer.writeByte(0);
         buffer.write(this._buffer, l);
      }

      this._buffer = null;
      return !this._failed;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized boolean setSyncData(DataBuffer buffer, int version) {
      this._failed = false;
      synchronized (this.this$0._settings) {
         boolean var18 = false /* VF: Semaphore variable */;
         boolean var22 = false /* VF: Semaphore variable */;

         label172: {
            try {
               label153:
               try {
                  var22 = true;
                  var18 = true;
                  this.this$0._doCommits = false;
                  this.this$0._settings.removeAllElements();
                  buffer.readShort();
                  buffer.readByte();

                  byte c;
                  while ((c = buffer.readByte()) != 0) {
                     if (c != 1) {
                        this._failed = true;
                     }

                     c = buffer.readByte();
                     if (c != 1) {
                        this._failed = true;
                     }

                     String moduleName = (String)(new Object(buffer.readByteArray()));
                     int appIndex = buffer.readByte();
                     c = buffer.readByte();
                     if (c != 1) {
                        this._failed = true;
                     }

                     String protocol = (String)(new Object(buffer.readByteArray()));
                     c = buffer.readByte();
                     String target;
                     if (c == 1) {
                        target = (String)(new Object(buffer.readByteArray()));
                     } else {
                        target = null;
                     }

                     int permission = buffer.readInt();
                     this.this$0.setPermission(moduleName, appIndex, protocol, target, permission);
                  }

                  if (buffer.available() <= 1) {
                     var18 = false;
                     var22 = false;
                  } else {
                     while ((c = buffer.readByte()) != 0) {
                        byte type = c;
                        boolean enabled = buffer.readBoolean();
                        int count = buffer.readCompressedInt();
                        if (!this.this$0.setBlocking(type, enabled, count)) {
                           this._failed = true;
                        }
                     }

                     var18 = false;
                     var22 = false;
                  }
                  break label172;
               } finally {
                  if (var22) {
                     this._failed = true;
                     var18 = false;
                     break label153;
                  }
               }
            } finally {
               if (var18) {
                  this.this$0._doCommits = true;
                  this.this$0.commit();
               }
            }

            this.this$0._doCommits = true;
            this.this$0.commit();
            return !this._failed;
         }

         this.this$0._doCommits = true;
         this.this$0.commit();
      }

      return !this._failed;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this.this$0._settings.removeAllElements();
      this.this$0._blockings.clear();
      this.this$0.commit();
      return true;
   }
}
