package net.rim.device.apps.internal.explorer.file.bluetooth;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.obex.ClientSession;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class SendHandler extends Thread implements DiscoveryListener, FieldChangeListener, KeyListener {
   private String _url;
   private ServiceRecord _serviceRecord;
   private int _transactionID;
   private Object _searchObject;
   private boolean _doneSearch;
   private String _file;
   private VerticalFieldManager _vfm;
   private PopupScreen _popupScreen;
   private GaugeField _gaugeField;
   private LabelField _labelField;
   private ButtonField _cancelField;
   private SetProgressRunnable _setProgressRunnable;
   private SetStatusRunnable _statusRunnable;
   private boolean _stopped;
   private ClientSession _cs;
   private int _resultCode;
   private DiscoveryAgent _discoveryAgent;
   private RemoteDevice _remoteDevice;
   private static final int PACKET_SIZE = 2048;

   public SendHandler(String file) {
      this._file = file;

      try {
         this._discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
         RemoteDevice[] remoteDevices = this._discoveryAgent.retrieveDevices(1);
         if (remoteDevices.length == 0) {
            Status.show(ExplorerResources.getString(18), 2000);
         } else {
            SimpleChoiceDialog dialog = new SimpleChoiceDialog(ExplorerResources.getString(19), remoteDevices, 0, null);
            dialog.setModal(true);
            dialog.show();
            if (dialog.getCloseReason() == -1) {
               Status.show(ExplorerResources.getString(26), 2000);
            } else {
               this._remoteDevice = remoteDevices[dialog.getSelectedIndex()];
               this._serviceRecord = null;
               this._searchObject = new Object();
               this._vfm = new VerticalFieldManager(1152921504606846976L);
               this._popupScreen = new PopupScreen(this._vfm);
               this._gaugeField = new GaugeField("", 0, 100, 0, 4);
               this._labelField = new LabelField(ExplorerResources.getString(20), 12884901952L);
               this._cancelField = new ButtonField(CommonResource.getString(10044), 12884901888L);
               this._cancelField.setChangeListener(this);
               this._statusRunnable = new SetStatusRunnable(this._labelField);
               this._setProgressRunnable = new SetProgressRunnable(this._gaugeField);
               this._vfm.add(new LabelField(ExplorerResources.getString(31), 12884901888L));
               this._vfm.add(new LabelField(""));
               this._vfm.add(this._labelField);
               this._vfm.add(this._gaugeField);
               this._vfm.add(this._cancelField);
               this._popupScreen.addKeyListener(this);
               this.start();
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.stop();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
   }

   @Override
   public final void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
      synchronized (this._searchObject) {
         if (transID == this._transactionID) {
            this._serviceRecord = servRecord[0];
         }
      }
   }

   @Override
   public final void serviceSearchCompleted(int transID, int respCode) {
      synchronized (this._searchObject) {
         if (transID == this._transactionID) {
            if (this._serviceRecord != null) {
               this._url = this._serviceRecord.getConnectionURL(0, false);
            }

            this._doneSearch = true;
            this._resultCode = respCode;
            this._searchObject.notify();
         }
      }
   }

   @Override
   public final void inquiryCompleted(int discType) {
   }

   private final void stop() {
      if (!this._stopped) {
         this._stopped = true;
         synchronized (this._searchObject) {
            this._searchObject.notify();
         }

         if (this._cs != null) {
            label37:
            try {
               this._cs.close();
            } finally {
               break label37;
            }
         }

         this._setProgressRunnable.setProgress(100);
         this._statusRunnable.setStatus(ExplorerResources.getString(26));
         this.dismiss();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._cancelField) {
         this.stop();
      }
   }

   private final void dismiss() {
      Application.getApplication().invokeLater(new SendHandler$1(this));
      Application.getApplication().invokeLater(new SendHandler$2(this), 2000, false);
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 1
      // 002: aconst_null
      // 003: astore 2
      // 004: aconst_null
      // 005: astore 3
      // 006: aconst_null
      // 007: astore 4
      // 009: aconst_null
      // 00a: astore 5
      // 00c: aconst_null
      // 00d: astore 6
      // 00f: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 012: new net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler$3
      // 015: dup
      // 016: aload 0
      // 017: invokespecial net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler$3.<init> (Lnet/rim/device/apps/internal/explorer/file/bluetooth/SendHandler;)V
      // 01a: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 01d: bipush 1
      // 01e: anewarray 831
      // 021: astore 7
      // 023: aload 7
      // 025: bipush 0
      // 026: new javax/bluetooth/UUID
      // 029: dup
      // 02a: sipush 4357
      // 02d: i2l
      // 02e: invokespecial javax/bluetooth/UUID.<init> (J)V
      // 031: aastore
      // 032: bipush 3
      // 034: istore 8
      // 036: aload 0
      // 037: bipush 3
      // 039: putfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._resultCode I
      // 03c: aload 0
      // 03d: bipush 0
      // 03e: putfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._doneSearch Z
      // 041: aload 0
      // 042: aload 0
      // 043: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._discoveryAgent Ljavax/bluetooth/DiscoveryAgent;
      // 046: aconst_null
      // 047: aload 7
      // 049: aload 0
      // 04a: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._remoteDevice Ljavax/bluetooth/RemoteDevice;
      // 04d: aload 0
      // 04e: invokevirtual javax/bluetooth/DiscoveryAgent.searchServices ([I[Ljavax/bluetooth/UUID;Ljavax/bluetooth/RemoteDevice;Ljavax/bluetooth/DiscoveryListener;)I
      // 051: putfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._transactionID I
      // 054: aload 0
      // 055: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._searchObject Ljava/lang/Object;
      // 058: dup
      // 059: astore 9
      // 05b: monitorenter
      // 05c: aload 0
      // 05d: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._doneSearch Z
      // 060: ifne 06f
      // 063: aload 0
      // 064: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._searchObject Ljava/lang/Object;
      // 067: invokevirtual java/lang/Object.wait ()V
      // 06a: goto 06f
      // 06d: astore 10
      // 06f: aload 9
      // 071: monitorexit
      // 072: goto 07d
      // 075: astore 11
      // 077: aload 9
      // 079: monitorexit
      // 07a: aload 11
      // 07c: athrow
      // 07d: aload 0
      // 07e: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._stopped Z
      // 081: ifeq 08c
      // 084: new java/io/IOException
      // 087: dup
      // 088: invokespecial java/io/IOException.<init> ()V
      // 08b: athrow
      // 08c: aload 0
      // 08d: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._serviceRecord Ljavax/bluetooth/ServiceRecord;
      // 090: ifnonnull 0a6
      // 093: aload 0
      // 094: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._resultCode I
      // 097: bipush 6
      // 099: if_icmpne 0a6
      // 09c: iload 8
      // 09e: dup
      // 09f: bipush 1
      // 0a0: isub
      // 0a1: istore 8
      // 0a3: ifgt 036
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._serviceRecord Ljavax/bluetooth/ServiceRecord;
      // 0aa: ifnonnull 12d
      // 0ad: aload 0
      // 0ae: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._setProgressRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetProgressRunnable;
      // 0b1: bipush 100
      // 0b3: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetProgressRunnable.setProgress (I)V
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._statusRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable;
      // 0ba: bipush 21
      // 0bc: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 0bf: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable.setStatus (Ljava/lang/String;)V
      // 0c2: aload 0
      // 0c3: invokespecial net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler.dismiss ()V
      // 0c6: aload 4
      // 0c8: ifnull 0d5
      // 0cb: aload 4
      // 0cd: invokevirtual java/io/InputStream.close ()V
      // 0d0: goto 0d5
      // 0d3: astore 20
      // 0d5: aload 3
      // 0d6: ifnull 0e4
      // 0d9: aload 3
      // 0da: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0df: goto 0e4
      // 0e2: astore 20
      // 0e4: aload 5
      // 0e6: ifnull 0f3
      // 0e9: aload 5
      // 0eb: invokevirtual java/io/OutputStream.close ()V
      // 0ee: goto 0f3
      // 0f1: astore 20
      // 0f3: aload 6
      // 0f5: ifnull 112
      // 0f8: iload 1
      // 0f9: ifeq 106
      // 0fc: aload 6
      // 0fe: invokeinterface javax/obex/Operation.abort ()V 1
      // 103: goto 112
      // 106: aload 6
      // 108: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 10d: goto 112
      // 110: astore 20
      // 112: aload 2
      // 113: ifnull 12c
      // 116: aload 2
      // 117: aconst_null
      // 118: invokeinterface javax/obex/ClientSession.disconnect (Ljavax/obex/HeaderSet;)Ljavax/obex/HeaderSet; 2
      // 11d: pop
      // 11e: goto 123
      // 121: astore 20
      // 123: aload 2
      // 124: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 129: return
      // 12a: astore 20
      // 12c: return
      // 12d: aload 0
      // 12e: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._file Ljava/lang/String;
      // 131: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 134: checkcast javax/microedition/io/file/FileConnection
      // 137: astore 3
      // 138: bipush 0
      // 139: i2l
      // 13a: lstore 9
      // 13c: bipush 0
      // 13d: istore 11
      // 13f: aload 3
      // 140: dup
      // 141: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 144: ifne 14b
      // 147: pop
      // 148: goto 16f
      // 14b: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 14e: astore 12
      // 150: aload 12
      // 152: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 157: ifeq 16f
      // 15a: aload 12
      // 15c: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.openRawInputStream ()Ljava/io/InputStream; 1
      // 161: astore 4
      // 163: aload 12
      // 165: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.rawFileSize ()J 1
      // 16a: lstore 9
      // 16c: bipush 1
      // 16d: istore 11
      // 16f: aload 4
      // 171: ifnonnull 184
      // 174: aload 3
      // 175: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 17a: astore 4
      // 17c: aload 3
      // 17d: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 182: lstore 9
      // 184: aload 0
      // 185: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._statusRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable;
      // 188: bipush 22
      // 18a: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 18d: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable.setStatus (Ljava/lang/String;)V
      // 190: aload 0
      // 191: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._url Ljava/lang/String;
      // 194: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 197: checkcast javax/obex/ClientSession
      // 19a: astore 2
      // 19b: aload 0
      // 19c: aload 2
      // 19d: putfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._cs Ljavax/obex/ClientSession;
      // 1a0: aload 0
      // 1a1: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._stopped Z
      // 1a4: ifeq 1af
      // 1a7: new java/io/IOException
      // 1aa: dup
      // 1ab: invokespecial java/io/IOException.<init> ()V
      // 1ae: athrow
      // 1af: aload 2
      // 1b0: aconst_null
      // 1b1: invokeinterface javax/obex/ClientSession.connect (Ljavax/obex/HeaderSet;)Ljavax/obex/HeaderSet; 2
      // 1b6: pop
      // 1b7: aload 2
      // 1b8: invokeinterface javax/obex/ClientSession.createHeaderSet ()Ljavax/obex/HeaderSet; 1
      // 1bd: astore 12
      // 1bf: aload 0
      // 1c0: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._file Ljava/lang/String;
      // 1c3: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 1c6: astore 13
      // 1c8: iload 11
      // 1ca: ifne 1d4
      // 1cd: aload 13
      // 1cf: invokestatic net/rim/device/internal/io/file/FileUtilities.getFileNameAndStripEncryptionExt (Ljava/lang/String;)Ljava/lang/String;
      // 1d2: astore 13
      // 1d4: aload 12
      // 1d6: bipush 1
      // 1d7: aload 13
      // 1d9: invokeinterface javax/obex/HeaderSet.setHeader (ILjava/lang/Object;)V 3
      // 1de: aload 12
      // 1e0: sipush 195
      // 1e3: new java/lang/Long
      // 1e6: dup
      // 1e7: lload 9
      // 1e9: invokespecial java/lang/Long.<init> (J)V
      // 1ec: invokeinterface javax/obex/HeaderSet.setHeader (ILjava/lang/Object;)V 3
      // 1f1: aload 2
      // 1f2: aload 12
      // 1f4: invokeinterface javax/obex/ClientSession.put (Ljavax/obex/HeaderSet;)Ljavax/obex/Operation; 2
      // 1f9: astore 6
      // 1fb: aload 6
      // 1fd: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 202: astore 5
      // 204: aload 0
      // 205: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._statusRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable;
      // 208: bipush 23
      // 20a: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 20d: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable.setStatus (Ljava/lang/String;)V
      // 210: sipush 2048
      // 213: newarray 8
      // 215: astore 14
      // 217: bipush 0
      // 218: i2l
      // 219: lstore 15
      // 21b: lload 15
      // 21d: lload 9
      // 21f: lcmp
      // 220: ifge 270
      // 223: aload 0
      // 224: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._stopped Z
      // 227: ifeq 232
      // 22a: new java/io/IOException
      // 22d: dup
      // 22e: invokespecial java/io/IOException.<init> ()V
      // 231: athrow
      // 232: aload 4
      // 234: aload 14
      // 236: invokevirtual java/io/InputStream.read ([B)I
      // 239: istore 17
      // 23b: iload 17
      // 23d: bipush -1
      // 23f: if_icmpne 24a
      // 242: new java/io/EOFException
      // 245: dup
      // 246: invokespecial java/io/EOFException.<init> ()V
      // 249: athrow
      // 24a: aload 5
      // 24c: aload 14
      // 24e: bipush 0
      // 24f: iload 17
      // 251: invokevirtual java/io/OutputStream.write ([BII)V
      // 254: aload 0
      // 255: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._setProgressRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetProgressRunnable;
      // 258: lload 15
      // 25a: bipush 100
      // 25c: i2l
      // 25d: lmul
      // 25e: lload 9
      // 260: ldiv
      // 261: l2i
      // 262: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetProgressRunnable.setProgress (I)V
      // 265: lload 15
      // 267: iload 17
      // 269: i2l
      // 26a: ladd
      // 26b: lstore 15
      // 26d: goto 21b
      // 270: aload 4
      // 272: ifnull 27f
      // 275: aload 4
      // 277: invokevirtual java/io/InputStream.close ()V
      // 27a: goto 27f
      // 27d: astore 20
      // 27f: aload 3
      // 280: ifnull 28e
      // 283: aload 3
      // 284: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 289: goto 28e
      // 28c: astore 20
      // 28e: aload 5
      // 290: ifnull 29d
      // 293: aload 5
      // 295: invokevirtual java/io/OutputStream.close ()V
      // 298: goto 29d
      // 29b: astore 20
      // 29d: aload 6
      // 29f: ifnull 2bc
      // 2a2: iload 1
      // 2a3: ifeq 2b0
      // 2a6: aload 6
      // 2a8: invokeinterface javax/obex/Operation.abort ()V 1
      // 2ad: goto 2bc
      // 2b0: aload 6
      // 2b2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2b7: goto 2bc
      // 2ba: astore 20
      // 2bc: aload 2
      // 2bd: ifnonnull 2c3
      // 2c0: goto 42c
      // 2c3: aload 2
      // 2c4: aconst_null
      // 2c5: invokeinterface javax/obex/ClientSession.disconnect (Ljavax/obex/HeaderSet;)Ljavax/obex/HeaderSet; 2
      // 2ca: pop
      // 2cb: goto 2d0
      // 2ce: astore 20
      // 2d0: aload 2
      // 2d1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2d6: goto 42c
      // 2d9: astore 20
      // 2db: goto 42c
      // 2de: astore 7
      // 2e0: bipush 1
      // 2e1: istore 1
      // 2e2: aload 4
      // 2e4: ifnull 2f1
      // 2e7: aload 4
      // 2e9: invokevirtual java/io/InputStream.close ()V
      // 2ec: goto 2f1
      // 2ef: astore 20
      // 2f1: aload 3
      // 2f2: ifnull 300
      // 2f5: aload 3
      // 2f6: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2fb: goto 300
      // 2fe: astore 20
      // 300: aload 5
      // 302: ifnull 30f
      // 305: aload 5
      // 307: invokevirtual java/io/OutputStream.close ()V
      // 30a: goto 30f
      // 30d: astore 20
      // 30f: aload 6
      // 311: ifnull 32e
      // 314: iload 1
      // 315: ifeq 322
      // 318: aload 6
      // 31a: invokeinterface javax/obex/Operation.abort ()V 1
      // 31f: goto 32e
      // 322: aload 6
      // 324: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 329: goto 32e
      // 32c: astore 20
      // 32e: aload 2
      // 32f: ifnonnull 335
      // 332: goto 42c
      // 335: aload 2
      // 336: aconst_null
      // 337: invokeinterface javax/obex/ClientSession.disconnect (Ljavax/obex/HeaderSet;)Ljavax/obex/HeaderSet; 2
      // 33c: pop
      // 33d: goto 342
      // 340: astore 20
      // 342: aload 2
      // 343: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 348: goto 42c
      // 34b: astore 20
      // 34d: goto 42c
      // 350: astore 7
      // 352: bipush 1
      // 353: istore 1
      // 354: aload 4
      // 356: ifnull 363
      // 359: aload 4
      // 35b: invokevirtual java/io/InputStream.close ()V
      // 35e: goto 363
      // 361: astore 20
      // 363: aload 3
      // 364: ifnull 372
      // 367: aload 3
      // 368: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 36d: goto 372
      // 370: astore 20
      // 372: aload 5
      // 374: ifnull 381
      // 377: aload 5
      // 379: invokevirtual java/io/OutputStream.close ()V
      // 37c: goto 381
      // 37f: astore 20
      // 381: aload 6
      // 383: ifnull 3a0
      // 386: iload 1
      // 387: ifeq 394
      // 38a: aload 6
      // 38c: invokeinterface javax/obex/Operation.abort ()V 1
      // 391: goto 3a0
      // 394: aload 6
      // 396: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 39b: goto 3a0
      // 39e: astore 20
      // 3a0: aload 2
      // 3a1: ifnull 42c
      // 3a4: aload 2
      // 3a5: aconst_null
      // 3a6: invokeinterface javax/obex/ClientSession.disconnect (Ljavax/obex/HeaderSet;)Ljavax/obex/HeaderSet; 2
      // 3ab: pop
      // 3ac: goto 3b1
      // 3af: astore 20
      // 3b1: aload 2
      // 3b2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3b7: goto 42c
      // 3ba: astore 20
      // 3bc: goto 42c
      // 3bf: astore 18
      // 3c1: aload 4
      // 3c3: ifnull 3d0
      // 3c6: aload 4
      // 3c8: invokevirtual java/io/InputStream.close ()V
      // 3cb: goto 3d0
      // 3ce: astore 20
      // 3d0: aload 3
      // 3d1: ifnull 3df
      // 3d4: aload 3
      // 3d5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3da: goto 3df
      // 3dd: astore 20
      // 3df: aload 5
      // 3e1: ifnull 3ee
      // 3e4: aload 5
      // 3e6: invokevirtual java/io/OutputStream.close ()V
      // 3e9: goto 3ee
      // 3ec: astore 20
      // 3ee: aload 6
      // 3f0: ifnull 40d
      // 3f3: iload 1
      // 3f4: ifeq 401
      // 3f7: aload 6
      // 3f9: invokeinterface javax/obex/Operation.abort ()V 1
      // 3fe: goto 40d
      // 401: aload 6
      // 403: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 408: goto 40d
      // 40b: astore 20
      // 40d: aload 2
      // 40e: ifnull 429
      // 411: aload 2
      // 412: aconst_null
      // 413: invokeinterface javax/obex/ClientSession.disconnect (Ljavax/obex/HeaderSet;)Ljavax/obex/HeaderSet; 2
      // 418: pop
      // 419: goto 41e
      // 41c: astore 20
      // 41e: aload 2
      // 41f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 424: goto 429
      // 427: astore 20
      // 429: aload 18
      // 42b: athrow
      // 42c: aload 0
      // 42d: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._setProgressRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetProgressRunnable;
      // 430: bipush 100
      // 432: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetProgressRunnable.setProgress (I)V
      // 435: aload 0
      // 436: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._stopped Z
      // 439: ifeq 443
      // 43c: bipush 26
      // 43e: istore 7
      // 440: goto 452
      // 443: iload 1
      // 444: ifeq 44e
      // 447: bipush 24
      // 449: istore 7
      // 44b: goto 452
      // 44e: bipush 25
      // 450: istore 7
      // 452: aload 0
      // 453: getfield net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler._statusRunnable Lnet/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable;
      // 456: iload 7
      // 458: invokestatic net/rim/device/apps/internal/explorer/file/resource/ExplorerResources.getString (I)Ljava/lang/String;
      // 45b: invokevirtual net/rim/device/apps/internal/explorer/file/bluetooth/SetStatusRunnable.setStatus (Ljava/lang/String;)V
      // 45e: aload 0
      // 45f: invokespecial net/rim/device/apps/internal/explorer/file/bluetooth/SendHandler.dismiss ()V
      // 462: return
      // try (55 -> 58): 59 null
      // try (52 -> 62): 63 null
      // try (63 -> 66): 63 null
      // try (12 -> 102): 332 null
      // try (144 -> 289): 332 null
      // try (12 -> 102): 378 null
      // try (144 -> 289): 378 null
      // try (12 -> 102): 423 null
      // try (144 -> 289): 423 null
      // try (332 -> 335): 423 null
      // try (378 -> 381): 423 null
      // try (423 -> 424): 423 null
      // try (426 -> 428): 429 null
      // try (383 -> 385): 386 null
      // try (337 -> 339): 340 null
      // try (291 -> 293): 294 null
      // try (104 -> 106): 107 null
      // try (432 -> 434): 435 null
      // try (389 -> 391): 392 null
      // try (343 -> 345): 346 null
      // try (297 -> 299): 300 null
      // try (110 -> 112): 113 null
      // try (438 -> 440): 441 null
      // try (395 -> 397): 398 null
      // try (349 -> 351): 352 null
      // try (303 -> 305): 306 null
      // try (116 -> 118): 119 null
      // try (444 -> 451): 452 null
      // try (401 -> 408): 409 null
      // try (355 -> 362): 363 null
      // try (309 -> 316): 317 null
      // try (122 -> 129): 130 null
      // try (455 -> 459): 460 null
      // try (412 -> 416): 417 null
      // try (367 -> 371): 372 null
      // try (321 -> 325): 326 null
      // try (133 -> 137): 138 null
      // try (461 -> 463): 464 null
      // try (418 -> 420): 421 null
      // try (373 -> 375): 376 null
      // try (327 -> 329): 330 null
      // try (139 -> 141): 142 null
   }
}
