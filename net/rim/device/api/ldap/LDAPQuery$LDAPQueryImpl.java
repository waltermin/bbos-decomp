package net.rim.device.api.ldap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.ServiceBookNotFoundException;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.ippp.StreamProtocol;
import net.rim.device.cldc.io.ippp.StreamProtocolListener;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.UsernamePasswordDialog;
import net.rim.vm.Array;

final class LDAPQuery$LDAPQueryImpl extends Thread implements StreamProtocolListener {
   private LDAPQuery _query;
   private DataInputStream _in;
   private DataOutputStream _out;
   private StreamConnection _conn;
   private int _size;
   private RIMLDAPEntry[] _entries;
   private String _queryString;
   private boolean _isDone;
   private final LDAPQuery this$0;
   private static final byte PARAM_USERNAME;
   private static final byte PARAM_PASSWORD;
   private static final byte PARAM_HASHED_ATTRIBUTE;
   private static final byte PARAM_CONNECTION_TYPE;

   LDAPQuery$LDAPQueryImpl(LDAPQuery _1, LDAPQuery query) {
      this.this$0 = _1;
      this._query = query;
      this._entries = new RIMLDAPEntry[0];
      this._queryString = this._query.toString();
      this._isDone = true;
   }

   private final void statusUpdate(int status) {
      this._query.statusUpdate(status);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      this._isDone = false;
      this._size = 0;
      this._entries = new RIMLDAPEntry[0];
      int finalStatusState = 0;
      boolean var64 = false /* VF: Semaphore variable */;

      label853: {
         label852: {
            try {
               label850:
               try {
                  var64 = true;
                  if (!ServiceRouting.getInstance().isServiceRoutable(this.this$0._serviceUID, -1)) {
                     this.this$0._errorCode = RadioInfo.getActiveWAFs() == 0 && !ServiceRouting.getInstance().isSerialBypassActive() ? 4 : 127;
                     throw new Object();
                  }

                  StringBuffer connectionStringBuffer = (StringBuffer)(new Object("ldap:"));
                  if (this.this$0._serviceUID != null) {
                     connectionStringBuffer.append(";connectionuid=");
                     connectionStringBuffer.append(this.this$0._serviceUID);
                  }

                  this._conn = (StreamConnection)Connector.open(connectionStringBuffer.toString(), 3, false);
                  if (this._conn instanceof StreamProtocol) {
                     ((StreamProtocol)this._conn).setStreamProtocolListener(this);
                  }

                  this._out = this._conn.openDataOutputStream();
                  this._in = this._conn.openDataInputStream();
                  this._out.writeUTF(this._queryString);
                  if (this.this$0._authType != 0) {
                     this._out.writeByte(1);
                     this._out.writeUTF(this._query._userDN);
                     this._out.writeByte(2);
                     this._out.writeUTF(this._query._password);
                  }

                  int numHashedAttributes = this._query._hashedAttributeList.length;

                  for (int i = 0; i < numHashedAttributes; i++) {
                     this._out.writeByte(3);
                     this._out.writeUTF(this._query._hashedAttributeList[i]);
                  }

                  this._out.flush();
                  if (this._in.readBoolean()) {
                     this._in = (DataInputStream)(new Object((InputStream)(new Object(this._in))));
                  }

                  String attributeName = "";
                  boolean newEntry = true;
                  boolean newAttribute = true;
                  boolean isByteValue = true;
                  boolean isComing = false;
                  String stringValue = "";
                  RIMLDAPAttribute currentAttribute = null;
                  isComing = this._in.readBoolean();
                  this.statusUpdate(5);

                  for (; isComing; isComing = this._in.readBoolean()) {
                     newEntry = this._in.readBoolean();
                     newAttribute = this._in.readBoolean();
                     if (newEntry) {
                        if (this._size > 0) {
                           this._query.entryReady(this._size - 1);
                           synchronized (this._query) {
                              this._query.notifyAll();
                           }
                        }

                        this._size++;
                        Array.resize(this._entries, this._size);
                        this._entries[this._size - 1] = new RIMLDAPEntry();
                     }

                     if (newAttribute) {
                        attributeName = this._in.readUTF();
                        currentAttribute = new RIMLDAPAttribute(attributeName);
                        this._entries[this._size - 1].addAttribute(currentAttribute);
                     }

                     isByteValue = this._in.readBoolean();
                     if (isByteValue) {
                        int arrayLength = 0;
                        arrayLength = this._in.readInt();
                        byte[] byteValue = new byte[arrayLength];
                        this._in.read(byteValue);
                        currentAttribute.addValue(byteValue);
                     } else {
                        stringValue = this._in.readUTF();
                        currentAttribute.addValue(stringValue);
                     }
                  }

                  if (this._size > 0) {
                     this._query.entryReady(this._size - 1);
                  }

                  synchronized (this._query) {
                     this._query.notifyAll();
                  }

                  if (this._in.readBoolean()) {
                     var83 = 11;
                     var64 = false;
                  } else {
                     var83 = 6;
                     var64 = false;
                  }
                  break label852;
               } catch (Throwable var81) {
                  if (e instanceof ServiceBookNotFoundException) {
                     var83 = 10;
                     this.this$0._errorCode = 2;
                     var64 = false;
                  } else if (e instanceof ConnectionClosedException) {
                     var83 = 8;
                     this.this$0._errorCode = 3;
                     var64 = false;
                  } else if (e instanceof Object) {
                     var83 = 9;
                     var64 = false;
                  } else {
                     var83 = 7;
                     if (this.this$0._errorCode == -1) {
                        this.this$0._errorCode = 0;
                        var64 = false;
                     } else {
                        var64 = false;
                     }
                  }
                  break label850;
               }
            } finally {
               if (var64) {
                  label798:
                  try {
                     if (this._in != null) {
                        boolean errorState = this._in.readBoolean();
                        this.this$0._errorCode = this._in.readInt();
                        if (errorState && this.this$0._errorCode == 0) {
                           this.this$0._errorMsg = this._in.readUTF();
                        } else {
                           this.this$0._errorMsg = this._in.readUTF();
                        }

                        this.handleErrorCode(this.this$0._errorCode);
                     }
                  } finally {
                     break label798;
                  }
               }
            }

            try {
               if (this._in != null) {
                  boolean errorState = this._in.readBoolean();
                  this.this$0._errorCode = this._in.readInt();
                  if (errorState && this.this$0._errorCode == 0) {
                     this.this$0._errorMsg = this._in.readUTF();
                  } else {
                     this.this$0._errorMsg = this._in.readUTF();
                  }

                  this.handleErrorCode(this.this$0._errorCode);
               }
               break label853;
            } finally {
               break label853;
            }
         }

         label830:
         try {
            if (this._in != null) {
               boolean errorState = this._in.readBoolean();
               this.this$0._errorCode = this._in.readInt();
               if (errorState && this.this$0._errorCode == 0) {
                  this.this$0._errorMsg = this._in.readUTF();
               } else {
                  this.this$0._errorMsg = this._in.readUTF();
               }

               this.handleErrorCode(this.this$0._errorCode);
            }
         } finally {
            break label830;
         }
      }

      this.statusUpdate(var83);

      label802:
      try {
         if (this._out != null) {
            this._out.close();
         }

         if (this._in != null) {
            this._in.close();
         }

         if (this._conn != null) {
            this._conn.close();
         }
      } finally {
         break label802;
      }

      this._isDone = true;
      synchronized (this._query) {
         this._query.notifyAll();
      }
   }

   public final synchronized void abort() {
      if (!this._isDone) {
         label38:
         try {
            if (this._out != null) {
               this._out.close();
            }

            if (this._in != null) {
               this._in.close();
            }

            if (this._conn != null) {
               this._conn.close();
            }

            this.statusUpdate(9);
         } finally {
            break label38;
         }
      }

      this._isDone = true;
   }

   @Override
   public final void event(int dgId, int code, Object context) {
      switch (code) {
         case -1:
            break;
         case 0:
            if (!this.this$0._firstSent) {
               this.this$0._firstSent = true;
               this.statusUpdate(4);
            }
            break;
         case 1:
         default:
            if (!this.this$0._firstPending && !this.this$0._firstSent) {
               this.this$0._firstPending = true;
               this.statusUpdate(2);
               return;
            }
      }
   }

   private final void handleErrorCode(int errorCode) {
      switch (errorCode) {
         case 128:
            return;
         case 129:
         case 131:
         default:
            LDAPPasswordCache cache = LDAPPasswordCache.getInstance();
            UsernamePasswordDialog dialog = (UsernamePasswordDialog)(new Object(CommonResource.getString(10029), this.this$0._userDN, null, null, 1, 134217728));
            BackgroundDialog.show(dialog);
            if (dialog.getCloseReason() == -1) {
               cache.cleanPassword(this.this$0._server, this.this$0._baseQuery);
               return;
            }

            this.this$0._userDN = dialog.getUsername();
            this.this$0._password = dialog.getPassword();
            cache.setPassword(this.this$0._server, this.this$0._baseQuery, this.this$0._userDN, this.this$0._password);
            this.run();
            return;
         case 130:
            BackgroundDialog.showMessage(CommonResource.getString(10030));
      }
   }
}
