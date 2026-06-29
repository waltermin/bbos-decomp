package net.rim.device.apps.internal.mms.verbs;

import java.io.InputStream;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.model.PresentationModelFactory;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.internal.io.file.FileUtilities;

public class MMSComposeVerb extends AbstractComposeVerb implements ConditionalVerb {
   private int _stringID;

   protected void addMessageBody(MMSMessageModelBuilder builder, Object context) {
      MMSPresentationModel newPresentation = PresentationModelFactory.createInstance(65536);
      String textAttachmentName = ((StringBuffer)(new Object(""))).append(System.currentTimeMillis()).append(".txt").toString();
      builder.addAttachment(textAttachmentName, 3, "");
      newPresentation.addPresentationElement(textAttachmentName, 3, true);
      Object mime = ContextObject.get(context, -4241241545455759532L);
      if (mime != null && mime instanceof Object) {
         String mimeType = (String)mime;
         int type = MMSUtilities.getMIMEType(mimeType);
         if (type != -1) {
            String name = (String)ContextObject.get(context, -4886909117188079897L);
            if (name == null) {
               name = MMSUtilities.getDefaultAttachmentName(type);
            }

            byte[] mimeData = null;
            InputStream mimeInputStream = (InputStream)ContextObject.get(context, 5473606008898265655L);
            String filename = (String)ContextObject.get(context, 2765042845091913199L);
            if (mimeInputStream != null) {
               label75:
               try {
                  mimeData = IOUtilities.streamToBytes(mimeInputStream);
               } finally {
                  break label75;
               }
            } else if (filename != null) {
               mimeData = FileUtilities.getData(filename);
            }

            builder.addAttachment(name, type, mimeData, null);
            newPresentation.addPresentationElement(name, type, true);
         }
      } else if (mime != null && mime instanceof Object[]) {
         String[] mimeTypes = (Object[])mime;
         String[] filePaths = (Object[])ContextObject.get(context, 2765042845091913199L);
         String mimeType = null;
         String name = null;
         String fileName = null;
         int type = -1;
         byte[] mimeData = null;
         if (filePaths != null && mimeTypes.length == filePaths.length) {
            for (int index = 0; index <= mimeTypes.length - 1; index++) {
               mimeType = mimeTypes[index];
               type = MMSUtilities.getMIMEType(mimeType);
               if (type != -1) {
                  name = MMSUtilities.getDefaultAttachmentName(type);
                  fileName = filePaths[index];
                  if (fileName != null) {
                     mimeData = FileUtilities.getData(fileName);
                  }

                  builder.addAttachment(name, type, mimeData, null);
                  newPresentation.addPresentationElement(name, type, true);
               }
            }
         }
      }

      builder.addAttachment(newPresentation);
   }

   @Override
   public boolean canInvoke(Object context) {
      if (ContextObject.getFlag(context, 74)) {
         return false;
      } else {
         return MMSTransportServiceBook.getMMSCUrl() == null ? false : this.validateAttachment(context);
      }
   }

   public MMSComposeVerb(int verbGroupId, int menuOrdering, int stringID) {
      super(menuOrdering);
      this._stringID = stringID;
      super._verbGroupId = verbGroupId;
   }

   @Override
   public Object copy() {
      return new MMSComposeVerb(this.getVerbGroupId(), this.getOrdering(), this._stringID);
   }

   @Override
   public String toString(Object context) {
      String address = this.getAddressDescription(context);
      if (address == null) {
         return MMSResources.getString(this._stringID);
      }

      boolean addressOnly = ContextObject.getFlag(context, 51);
      boolean coalesced = ContextObject.getFlag(context, 63);
      if (coalesced && super._address != null) {
         if (super._address instanceof Object) {
            Object previousAddressCard = ContextObject.get(context, 252);
            if (super._addressCard != null) {
               ContextObject.put(context, 252, super._addressCard);
            } else {
               ContextObject.remove(context, 252);
            }

            String result = ((VerbDescriptionProvider)super._address).getVerbDescription(context);
            if (previousAddressCard != null) {
               ContextObject.put(context, 252, previousAddressCard);
            } else {
               ContextObject.remove(context, 252);
            }

            return result;
         } else {
            return super._address.toString();
         }
      } else {
         if (addressOnly) {
            return address;
         }

         String formatString = MMSResources.getString(11);
         return MessageFormat.format(formatString, new Object[]{address});
      }
   }

   @Override
   public Object doInvoke(Object context) {
      PersistableRIMModel origAddress = super._address;
      boolean origSmartDialing = super._useSmartDialing;
      ContextObject contextObject = ContextObject.clone(context);
      if (super._address == null) {
         super._address = (PersistableRIMModel)contextObject.get(254);
         if (super._address == null) {
            this.resolveAddress();
            if (super._address == null) {
               return null;
            }
         }
      }

      if (MMSUtilities.modelIsAGroupWithAllInvalidAddresses(super._address)) {
         return null;
      }

      MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
      builder.inheritDefaultReporting();
      builder.addRecipient(super._address);
      String subject = (String)contextObject.get(-1188330299125235844L);
      if (subject != null) {
         builder.setSubject(subject);
      }

      this.addMessageBody(builder, context);
      builder.setStatus(Integer.MAX_VALUE);
      if (super._useSmartDialing) {
         builder.enableSmartDialing();
      }

      super._address = origAddress;
      super._useSmartDialing = origSmartDialing;
      if (super._addressCard != null) {
         contextObject.put(252, super._addressCard);
      }

      MMSMessageModel message = builder.getResult();
      contextObject.put(-7651695713744129224L, message);
      contextObject.setFlag(6);
      ContextObject returnContext = ContextObject.castOrCreate(super.doInvoke(contextObject));
      boolean clearTerminalFlag = ContextObject.getFlag(context, 43)
         || ContextObject.getFlag(context, 74)
         || ContextObject.getFlag(context, 55)
         || ContextObject.getPrivateFlag(context, -337556985625701066L, 0);
      if (clearTerminalFlag) {
         returnContext.clearFlag(39);
      } else {
         returnContext.setFlag(39);
      }

      returnContext.clearFlag(40);
      return returnContext;
   }

   public MMSComposeVerb(int menuOrdering, int stringID) {
      this(12759082, menuOrdering, stringID);
   }

   public MMSComposeVerb() {
      this(1267024, 8);
   }

   private boolean validateAttachment(Object context) {
      InputStream mimeInputStream = (InputStream)ContextObject.get(context, 5473606008898265655L);
      if (mimeInputStream != null) {
         return true;
      }

      Object mime = ContextObject.get(context, -4241241545455759532L);
      if (mime != null && mime instanceof Object) {
         String fileName = (String)ContextObject.get(context, 2765042845091913199L);
         if (fileName != null && this.getFileSize(fileName) >= 1500000) {
            return false;
         }
      } else if (mime != null && mime instanceof Object[]) {
         String[] filePaths = (Object[])ContextObject.get(context, 2765042845091913199L);
         String fileName = null;

         for (int fileIndex = 0; fileIndex <= filePaths.length - 1; fileIndex++) {
            fileName = filePaths[fileIndex];
            if (fileName != null && this.getFileSize(fileName) >= 1500000) {
               return false;
            }
         }
      }

      return true;
   }

   private long getFileSize(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: aload 1
      // 03: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06: checkcast java/lang/Object
      // 09: astore 2
      // 0a: aload 2
      // 0b: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 10: ifeq 2a
      // 13: aload 2
      // 14: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 19: lstore 3
      // 1a: aload 2
      // 1b: ifnull 28
      // 1e: aload 2
      // 1f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 24: lload 3
      // 25: lreturn
      // 26: astore 5
      // 28: lload 3
      // 29: lreturn
      // 2a: aload 2
      // 2b: ifnull 61
      // 2e: aload 2
      // 2f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 34: bipush 0
      // 35: i2l
      // 36: lreturn
      // 37: astore 3
      // 38: bipush 0
      // 39: i2l
      // 3a: lreturn
      // 3b: astore 3
      // 3c: aload 2
      // 3d: ifnull 61
      // 40: aload 2
      // 41: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 46: bipush 0
      // 47: i2l
      // 48: lreturn
      // 49: astore 3
      // 4a: bipush 0
      // 4b: i2l
      // 4c: lreturn
      // 4d: astore 6
      // 4f: aload 2
      // 50: ifnull 5e
      // 53: aload 2
      // 54: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 59: goto 5e
      // 5c: astore 7
      // 5e: aload 6
      // 60: athrow
      // 61: bipush 0
      // 62: i2l
      // 63: lreturn
      // try (14 -> 16): 18 null
      // try (23 -> 25): 28 null
      // try (2 -> 12): 32 null
      // try (35 -> 37): 40 null
      // try (2 -> 12): 44 null
      // try (32 -> 33): 44 null
      // try (47 -> 49): 50 null
      // try (44 -> 45): 44 null
   }
}
