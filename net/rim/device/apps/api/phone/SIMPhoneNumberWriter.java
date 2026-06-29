package net.rim.device.apps.api.phone;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardEFListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.vm.Array;

public final class SIMPhoneNumberWriter extends PopupScreen implements SIMCardEFListener {
   private UiApplication _app;
   private int _timeoutTimerId = -1;
   private boolean _timeoutExpired;
   private boolean _closeOnEscape;
   private String _phoneNumber;
   private String _phoneNumberDescription;
   private LabelField _statusField;
   private BitmapField _iconField;
   private int _id;
   private int _structure;
   private boolean _show;
   private int _record;
   static final byte INTERNATIONAL_CONST = -112;
   static final byte UNKNOWN_CONST = -128;
   private static final long DEFAULT_TIMEOUT = 20000L;

   private SIMPhoneNumberWriter(String phoneNumber, String phoneNumberDescription, int structure, int id, int record, boolean show) {
      super((Manager)(new Object()), 0);
      this._app = UiApplication.getUiApplication();
      this._phoneNumber = phoneNumber;
      this._phoneNumberDescription = phoneNumberDescription;
      String title = CommonResources.getString(9162);
      this._statusField = (LabelField)(new Object(title, 64));
      this._iconField = (BitmapField)(new Object(Bitmap.getPredefinedBitmap(3)));
      this._iconField.setPadding(0, 3, 0, 0);
      this.add(this._iconField);
      this.add(this._statusField);
      this._id = id;
      this._structure = structure;
      this._show = show;
      this._record = record;
   }

   public static final void write(String phoneNumber, int structure, int id, int record, boolean show) {
      new SIMPhoneNumberWriter(phoneNumber, null, structure, id, record, show).write();
   }

   public static final void write(String phoneNumber, String phoneNumberDescription, int structure, int id, int record, boolean show) {
      new SIMPhoneNumberWriter(phoneNumber, phoneNumberDescription, structure, id, record, show).write();
   }

   private final synchronized void write() {
      Runnable closeRunner = new SIMPhoneNumberWriter$1(this);
      SIMCard.addListener(this._app, this);
      this._timeoutTimerId = this._app.invokeLater(closeRunner, 20000, false);
      this._app.pushModalScreen(this);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.readRecord(256);
      }
   }

   @Override
   protected final boolean keyCharUnhandled(char key, int status, int time) {
      if (key == 27 && this._closeOnEscape) {
         this.close();
         return true;
      } else {
         return false;
      }
   }

   private final void onError(int resID) {
      System.out.println(CommonResources.getString(resID));
      if (this._show) {
         this.updateStatus(2, CommonResources.getString(resID));
         this._closeOnEscape = true;
      } else {
         this.close();
      }
   }

   private final void updateStatus(int bitmap, String status) {
      this._iconField.setBitmap(Bitmap.getPredefinedBitmap(bitmap));
      this._statusField.setText(status);
   }

   @Override
   public final void close() {
      if (!this._timeoutExpired && this._timeoutTimerId != -1) {
         this._app.cancelInvokeLater(this._timeoutTimerId);
      }

      SIMCard.removeListener(this._app, this);

      try {
         this._app.popScreen(this);
      } finally {
         return;
      }
   }

   @Override
   public final void responseEFInfo(int code, int id, int fileStatus, int structure, int fileSize, int recordLength, int numRecords) {
   }

   @Override
   public final void responseEFRead(int code, int id, int structure, int length, int recordNumber) {
      if (id == this._id) {
         if (code == 0 && length >= 14) {
            this.readRecord(length);
         } else {
            this.onError(9115);
         }
      }
   }

   @Override
   public final void responseEFWrite(int code, int id, int structure, int recordNumber) {
      if (id == this._id) {
         if (code != 0) {
            this.onError(9116);
         } else {
            this.close();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readRecord(int length) {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         byte[] e = new byte[length];
         int cnt = SIMCard.requestEFRead(this._id, this._structure, this._record, e);
         if (cnt < 0) {
            var5 = false;
         } else {
            if (cnt >= 14) {
               Array.resize(e, cnt);
               this.updateRecord(e);
               this.writeRecord(e);
               return;
            }

            this.onError(9115);
            var5 = false;
         }
      } finally {
         if (var5) {
            this.onError(9115);
            return;
         }
      }
   }

   private final void updateRecord(byte[] buffer) {
      int length = buffer.length;
      if (this._phoneNumberDescription != null) {
         byte[] alphaId = SIMCard.encodeAlphaId(this._phoneNumberDescription);
         int alphaIdLength = alphaId == null ? 0 : alphaId.length;

         for (int i = length - 15; i >= 0; i--) {
            if (i >= alphaIdLength) {
               buffer[i] = -1;
            } else {
               buffer[i] = alphaId[i];
            }
         }
      }

      if (this._phoneNumber == null) {
         this._phoneNumber = "";
      }

      int phoneNumberLength = this._phoneNumber.length();
      int digitCount = 0;
      int idxIn = 0;
      int idxOut = 0;

      while (idxOut < 20) {
         byte b;
         if (idxIn >= phoneNumberLength) {
            b = -1;
         } else {
            char ch = this._phoneNumber.charAt(idxIn++);
            if (ch < '0' || ch > '9') {
               continue;
            }

            b = (byte)(ch - '0' & 15);
            digitCount++;
         }

         int offset = length - 14 + 2 + idxOut / 2;
         if (idxOut % 2 == 0) {
            buffer[offset] = b;
         } else {
            buffer[offset] = (byte)(buffer[offset] | b << 4);
         }

         idxOut++;
      }

      buffer[length - 14] = (byte)((digitCount + 1) / 2 + 1);
      byte tonAndNpi = 1;
      if (phoneNumberLength > 0 && this._phoneNumber.charAt(0) == '+') {
         tonAndNpi = (byte)(tonAndNpi & 15 | -112);
      } else {
         tonAndNpi = (byte)(tonAndNpi & 15 | -128);
      }

      buffer[length - 14 + 1] = tonAndNpi;
   }

   private final void writeRecord(byte[] buffer) {
      try {
         SIMCard.requestEFWrite(this._id, this._structure, this._record, buffer);
      } finally {
         this.onError(9137);
         return;
      }
   }
}
