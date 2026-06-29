package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.xml.XMLHashtable;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import org.xml.sax.Attributes;

public final class GranularPolicy extends GranularPolicyElement {
   public GranularPolicy(XMLHashtable param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ldc_w ""
      // 04: aconst_null
      // 05: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicyElement.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicyElement;)V
      // 08: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 0b: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 0e: astore 2
      // 0f: goto 2c
      // 12: astore 3
      // 13: new java/lang/Object
      // 16: dup
      // 17: aload 3
      // 18: invokevirtual net/rim/device/api/xml/parsers/ParserConfigurationException.toString ()Ljava/lang/String;
      // 1b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: astore 3
      // 20: new java/lang/Object
      // 23: dup
      // 24: aload 3
      // 25: invokevirtual org/xml/sax/SAXException.toString ()Ljava/lang/String;
      // 28: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // 2c: aload 1
      // 2d: ldc_w "/GetGranularPolicyResponse/policy"
      // 30: invokevirtual net/rim/device/api/xml/XMLHashtable.getString (Ljava/lang/String;)Ljava/lang/String;
      // 33: astore 3
      // 34: aload 3
      // 35: ifnonnull 43
      // 38: new java/lang/Object
      // 3b: dup
      // 3c: ldc_w "no policy data"
      // 3f: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 42: athrow
      // 43: new java/lang/Object
      // 46: dup
      // 47: new java/lang/Object
      // 4a: dup
      // 4b: aload 3
      // 4c: invokevirtual java/lang/String.getBytes ()[B
      // 4f: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 52: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 55: astore 4
      // 57: new net/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy$XMLToGranularPolicyHandler
      // 5a: dup
      // 5b: aload 0
      // 5c: aconst_null
      // 5d: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy$XMLToGranularPolicyHandler.<init> (Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy;Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy$1;)V
      // 60: astore 5
      // 62: aload 2
      // 63: aload 4
      // 65: aload 5
      // 67: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 6a: goto 7c
      // 6d: astore 4
      // 6f: new java/lang/Object
      // 72: dup
      // 73: aload 4
      // 75: invokevirtual org/xml/sax/SAXException.toString ()Ljava/lang/String;
      // 78: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 7b: athrow
      // 7c: aload 0
      // 7d: bipush 0
      // 7e: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/server/policy/GranularPolicy.debugPrint (I)V
      // 81: return
      // try (4 -> 7): 8 null
      // try (4 -> 7): 15 null
      // try (33 -> 52): 53 null
   }

   @Override
   public final GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return StringUtilities.strEqualIgnoreCase(localName, "chains")
         ? new GranularPolicyChainGroup(localName, this)
         : super.handleStartElement(localName, attributes);
   }

   @Override
   public final String getDebugPrintInfo() {
      return "Policy Root";
   }

   public final GranularPolicyAction determineAction(String recipientAddress, ServiceRecord serviceRecord, EmailMessageModel emailMessageModel) {
      System.out.println("********* EVALUATING GRANULAR POLICY *********");
      Vector childElements = this.getChildElements();
      int numChildElements = childElements.size();

      for (int i = 0; i < numChildElements; i++) {
         GranularPolicyElement currentChildElement = (GranularPolicyElement)childElements.elementAt(i);
         if (currentChildElement instanceof GranularPolicyChainGroup) {
            GranularPolicyChainGroup currentChainGroup = (GranularPolicyChainGroup)currentChildElement;
            return this.determineAction(currentChainGroup, recipientAddress, serviceRecord, emailMessageModel);
         }
      }

      return null;
   }

   public final GranularPolicyAction determineAction(
      GranularPolicyChainGroup chainGroup, String recipientAddress, ServiceRecord serviceRecord, EmailMessageModel emailMessageModel
   ) {
      StringBuffer eventLogString = (StringBuffer)(new Object("UGPP: "));
      eventLogString.append(recipientAddress);
      eventLogString.append(':').append(' ');
      GranularPolicyChain currentChain = chainGroup.locateChain(null);

      label52:
      while (currentChain != null) {
         System.out.println(((StringBuffer)(new Object())).append(currentChain.getDebugPrintInfo()).append(": Processing").toString());
         eventLogString.append('C').append(currentChain.getOrder()).append(',');
         Vector currentChainChildElements = currentChain.getChildElements();
         int numCurrentChainChildElements = currentChainChildElements.size();

         for (int i = 0; i < numCurrentChainChildElements; i++) {
            Object currentChainChildElement = currentChainChildElements.elementAt(i);
            if (currentChainChildElement instanceof GranularPolicyRule) {
               GranularPolicyRule currentRule = (GranularPolicyRule)currentChainChildElement;
               if (!currentRule.evaluate(recipientAddress, serviceRecord, emailMessageModel)) {
                  System.out.println(((StringBuffer)(new Object("  "))).append(currentRule.getDebugPrintInfo()).append(": FALSE").toString());
                  eventLogString.append('R').append(currentRule.getOrder()).append('F').append(',');
               } else {
                  System.out.println(((StringBuffer)(new Object("  "))).append(currentRule.getDebugPrintInfo()).append(": TRUE").toString());
                  eventLogString.append('R').append(currentRule.getOrder()).append('T').append(',');
                  GranularPolicyActionGroup currentActionGroup = currentRule.getActionGroup();
                  if (currentActionGroup == null) {
                     System.out.println(((StringBuffer)(new Object("  "))).append(currentRule.getDebugPrintInfo()).append(": no actions").toString());
                  } else {
                     Vector currentActionGroupChildElements = currentActionGroup.getChildElements();
                     int numCurrentActionGroupChildElements = currentActionGroupChildElements.size();

                     for (int j = 0; j < numCurrentActionGroupChildElements; j++) {
                        Object currentActionGroupChildElement = currentActionGroupChildElements.elementAt(j);
                        if (currentActionGroupChildElement instanceof GranularPolicyAction) {
                           GranularPolicyAction currentAction = (GranularPolicyAction)currentActionGroupChildElement;
                           System.out.println(((StringBuffer)(new Object("    "))).append(currentAction.getDebugPrintInfo()).toString());
                           switch (currentAction.getOperation()) {
                              case 4:
                              case 9:
                                 EventLogger.logEvent(234044482576569793L, eventLogString.toString().getBytes());
                                 return currentAction;
                              case 5:
                              default:
                                 currentChain = chainGroup.locateChain(currentAction.getChainName());
                                 continue label52;
                              case 6:
                              case 7:
                              case 8:
                              case 10:
                           }
                        }
                     }
                  }
               }
            }
         }
         break;
      }

      EventLogger.logEvent(234044482576569793L, eventLogString.toString().getBytes());
      return null;
   }
}
