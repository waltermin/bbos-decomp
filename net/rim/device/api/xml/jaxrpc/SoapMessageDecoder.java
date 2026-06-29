package net.rim.device.api.xml.jaxrpc;

import java.io.InputStream;
import java.rmi.MarshalException;
import java.rmi.ServerException;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.FaultDetailException;
import javax.microedition.xml.rpc.FaultDetailHandler;
import javax.microedition.xml.rpc.Type;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.JAXRPCException;
import net.rim.device.api.util.IntStack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class SoapMessageDecoder extends DefaultHandler {
   private SAXParser _parser;
   private StringBuffer _charData;
   private String _tagName;
   private boolean _isNillable;
   private Stack _typeStack;
   private Stack _objectGraphStack;
   private IntStack _stateStack;
   private int _state;
   private FaultDetailHandler _faultDetailHandler;
   private Element _faultDetail;
   private QName _faultDetailName;
   private String _errorMessage;
   private boolean _processingHeader;
   private int _bodyAndEnvelopeCount;
   private boolean _noMoreElement;
   private boolean _matched;
   private boolean _compress;
   private SoapWbxmlCodebook _codebook;
   private static final int DECODE_SIMPLE_STATE = 1;
   private static final int DECODE_COMPLEX_STATE = 2;
   private static final int ERROR_STATE = 4;
   private static final int FAULT_DETAIL_INDEX = 3;
   private static final Object NIL = new Object();

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public SoapMessageDecoder(boolean compress, SoapWbxmlCodebook codebook) {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         SAXParserFactory e = SAXParserFactory.newInstance();
         e.setNamespaceAware(true);
         e.setValidating(false);
         this._parser = e.newSAXParser();
         var5 = false;
      } finally {
         if (var5) {
            throw new Object("Unable to instantiate SAX parser.");
         }
      }

      this._charData = (StringBuffer)(new Object());
      this._objectGraphStack = (Stack)(new Object());
      this._typeStack = (Stack)(new Object());
      this._stateStack = (IntStack)(new Object(5));
      this._compress = compress;
      this._codebook = codebook;
   }

   public final synchronized Object decode(InputStream param1, Element param2, String param3, FaultDetailHandler param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial net/rim/device/api/xml/jaxrpc/SoapMessageDecoder.clearStacks ()V
      // 004: aload 0
      // 005: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._typeStack Ljava/util/Stack;
      // 008: aload 2
      // 009: invokevirtual java/util/Stack.push (Ljava/lang/Object;)Ljava/lang/Object;
      // 00c: pop
      // 00d: getstatic net/rim/device/api/xml/jaxrpc/OperationImpl.processingFault Z
      // 010: ifeq 019
      // 013: aload 0
      // 014: aload 4
      // 016: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._faultDetailHandler Ljavax/microedition/xml/rpc/FaultDetailHandler;
      // 019: aload 0
      // 01a: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._compress Z
      // 01d: ifeq 051
      // 020: aload 0
      // 021: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._codebook Lnet/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook;
      // 024: ifnull 051
      // 027: aload 0
      // 028: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._parser Ljavax/xml/parsers/SAXParser;
      // 02b: checkcast net/rim/device/api/xml/jaxp/RIMSAXParser
      // 02e: aload 1
      // 02f: aload 0
      // 030: aload 0
      // 031: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._codebook Lnet/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook;
      // 034: invokeinterface net/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook.getTags ()[Ljava/lang/String; 1
      // 039: aload 0
      // 03a: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._codebook Lnet/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook;
      // 03d: invokeinterface net/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook.getAttributes ()[Ljava/lang/String; 1
      // 042: aload 0
      // 043: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._codebook Lnet/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook;
      // 046: invokeinterface net/rim/device/api/xml/jaxrpc/SoapWbxmlCodebook.getAttributeValues ()[Ljava/lang/String; 1
      // 04b: invokevirtual net/rim/device/api/xml/jaxp/RIMSAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V
      // 04e: goto 09d
      // 051: aload 0
      // 052: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._parser Ljavax/xml/parsers/SAXParser;
      // 055: aload 1
      // 056: aload 0
      // 057: invokevirtual javax/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 05a: goto 09d
      // 05d: astore 5
      // 05f: goto 09d
      // 062: astore 5
      // 064: aload 0
      // 065: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._stateStack Lnet/rim/device/api/util/IntStack;
      // 068: bipush 4
      // 06a: invokevirtual net/rim/device/api/util/IntStack.push (I)I
      // 06d: pop
      // 06e: aload 0
      // 06f: ldc_w "SAXParseException in the return SOAP message from the server."
      // 072: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._errorMessage Ljava/lang/String;
      // 075: goto 09d
      // 078: astore 5
      // 07a: aload 0
      // 07b: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._stateStack Lnet/rim/device/api/util/IntStack;
      // 07e: bipush 4
      // 080: invokevirtual net/rim/device/api/util/IntStack.push (I)I
      // 083: pop
      // 084: aload 0
      // 085: new java/lang/Object
      // 088: dup
      // 089: ldc_w "Unexpected Exception: "
      // 08c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 08f: aload 5
      // 091: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 094: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 097: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 09a: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._errorMessage Ljava/lang/String;
      // 09d: aload 0
      // 09e: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._stateStack Lnet/rim/device/api/util/IntStack;
      // 0a1: invokevirtual net/rim/device/api/util/IntVector.isEmpty ()Z
      // 0a4: ifne 0b5
      // 0a7: aload 0
      // 0a8: aload 0
      // 0a9: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._stateStack Lnet/rim/device/api/util/IntStack;
      // 0ac: invokevirtual net/rim/device/api/util/IntStack.pop ()I
      // 0af: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._state I
      // 0b2: goto 0d1
      // 0b5: aload 0
      // 0b6: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._bodyAndEnvelopeCount I
      // 0b9: ifeq 0cc
      // 0bc: aload 0
      // 0bd: bipush 4
      // 0bf: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._state I
      // 0c2: aload 0
      // 0c3: ldc_w "The end tag for either SOAP Envelope or SOAP Body is missing."
      // 0c6: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._errorMessage Ljava/lang/String;
      // 0c9: goto 0d1
      // 0cc: aload 0
      // 0cd: bipush 0
      // 0ce: putfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._state I
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._state I
      // 0d5: bipush 4
      // 0d7: if_icmpne 0ed
      // 0da: new javax/xml/rpc/JAXRPCException
      // 0dd: dup
      // 0de: new java/rmi/MarshalException
      // 0e1: dup
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._errorMessage Ljava/lang/String;
      // 0e6: invokespecial java/rmi/MarshalException.<init> (Ljava/lang/String;)V
      // 0e9: invokespecial javax/xml/rpc/JAXRPCException.<init> (Ljava/lang/Throwable;)V
      // 0ec: athrow
      // 0ed: aload 0
      // 0ee: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._objectGraphStack Ljava/util/Stack;
      // 0f1: invokevirtual java/util/Vector.isEmpty ()Z
      // 0f4: ifeq 119
      // 0f7: aload 2
      // 0f8: getfield javax/microedition/xml/rpc/Element.isOptional Z
      // 0fb: ifne 105
      // 0fe: aload 2
      // 0ff: getfield javax/microedition/xml/rpc/Element.isNillable Z
      // 102: ifeq 107
      // 105: aconst_null
      // 106: areturn
      // 107: new javax/xml/rpc/JAXRPCException
      // 10a: dup
      // 10b: new java/rmi/MarshalException
      // 10e: dup
      // 10f: ldc_w "The return data from the server is missing."
      // 112: invokespecial java/rmi/MarshalException.<init> (Ljava/lang/String;)V
      // 115: invokespecial javax/xml/rpc/JAXRPCException.<init> (Ljava/lang/Throwable;)V
      // 118: athrow
      // 119: getstatic net/rim/device/api/xml/jaxrpc/OperationImpl.processingFault Z
      // 11c: ifeq 12b
      // 11f: aload 0
      // 120: aload 0
      // 121: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._objectGraphStack Ljava/util/Stack;
      // 124: invokevirtual java/util/Stack.pop ()Ljava/lang/Object;
      // 127: invokespecial net/rim/device/api/xml/jaxrpc/SoapMessageDecoder.decodeFault (Ljava/lang/Object;)Ljava/lang/Object;
      // 12a: areturn
      // 12b: aload 0
      // 12c: aload 0
      // 12d: getfield net/rim/device/api/xml/jaxrpc/SoapMessageDecoder._objectGraphStack Ljava/util/Stack;
      // 130: invokevirtual java/util/Stack.pop ()Ljava/lang/Object;
      // 133: invokespecial net/rim/device/api/xml/jaxrpc/SoapMessageDecoder.decodeObjectGraph (Ljava/lang/Object;)Ljava/lang/Object;
      // 136: areturn
      // try (12 -> 39): 40 null
      // try (12 -> 39): 42 null
      // try (12 -> 39): 52 null
   }

   @Override
   public final void startElement(String URI, String localName, String qname, Attributes attributes) {
      this._tagName = this.getTagName(localName, qname);
      if (this._tagName.toLowerCase().equals("envelope") || this._tagName.toLowerCase().equals("body")) {
         this.handleEnvelopeAndBody(URI, "start");
      } else if (this._tagName.toLowerCase().equals("header")) {
         this.handleHeader(URI, "start");
      } else {
         if (this._processingHeader) {
            this.handleHeaderBlock(attributes);
         }

         if (this._bodyAndEnvelopeCount != 2) {
            this.generateError("Either SOAP Body or SOAP Envelope is missing.");
         }

         if (this._noMoreElement) {
            this.generateError("There are elments following the SOAP Body.");
         }

         if (this._isNillable) {
            this.generateError(((StringBuffer)(new Object("Nillable element contains value: "))).append(this._tagName).toString());
         }

         String nilAttribute = attributes.getValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
         this._isNillable = nilAttribute != null && (nilAttribute.toLowerCase().equals("true") || nilAttribute.equals("1"));
         this._charData.delete(0, this._charData.length());
         this.startElementAction(URI, this._tagName);
      }
   }

   private final void startElementAction(String URI, String name) {
      Type topType = (Type)this._typeStack.peek();
      if (!(topType instanceof Element)) {
         if (topType instanceof ComplexType) {
            ComplexType topComplex = (ComplexType)topType;
            int index = this.findMatchingElement(topComplex, URI, name);
            if (this._matched) {
               if (topComplex.elements[index].contentType instanceof ComplexType) {
                  this.graphTemplateForComplex((ComplexType)topComplex.elements[index].contentType);
                  return;
               }
            } else {
               if (OperationImpl.processingFault && this._faultDetailHandler != null && this._faultDetailName == null) {
                  this._faultDetailName = new QName(URI, name);
                  Element faultDetail = this._faultDetailHandler.handleFault(this._faultDetailName);
                  if (faultDetail != null) {
                     this._faultDetail = faultDetail;
                     this.graphTemplateForElement(this._faultDetail);
                     return;
                  }
               }

               if (this._faultDetail == null) {
                  this.generateError("The Element object describing the SOAP fault detail is not available.");
               }
            }
         }
      } else {
         Element topElement = (Element)topType;
         if (topElement.name.getNamespaceURI() == "") {
            this.generateError("The children of the soap:Body element must be namespace qualified.");
         }

         if (topElement.contentType.value <= 8) {
            this.checkElementMatching(topElement, URI, name);
            this.graphTemplateForElement(topElement);
            return;
         }
      }
   }

   @Override
   public final void endElement(String URI, String localName, String qname) {
      this._tagName = this.getTagName(localName, qname);
      if (this._tagName.toLowerCase().equals("envelope") || this._tagName.toLowerCase().equals("body")) {
         this.handleEnvelopeAndBody(URI, "end");
      } else if (this._tagName.toLowerCase().equals("header")) {
         this.handleHeader(URI, "end");
      } else {
         this.endElementAction(URI, this._tagName);
         this._isNillable = false;
      }
   }

   private final void endElementAction(String URI, String name) {
      Type topType = (Type)this._typeStack.pop();
      this._state = this._stateStack.peek();
      switch (this._state) {
         case 0:
            return;
         case 1:
         default:
            Element topElement = (Element)topType;
            if (!this.isHandlingFaultDetail(URI, name)) {
               Object simpleVal = null;
               if (topElement.isArray) {
                  if (!this._isNillable) {
                     simpleVal = this.decodeSimpleType(topElement.contentType, this._charData.toString());
                     Vector simpleElements = (Vector)this._objectGraphStack.peek();
                     simpleElements.addElement(simpleVal);
                  }
               } else if (!this._isNillable) {
                  this._objectGraphStack.push(this.decodeSimpleType(topElement.contentType, this._charData.toString()));
                  this._typeStack.pop();
               }
            } else if (!this._isNillable) {
               this.handleFaultDetail(topElement.contentType);
            }

            this._stateStack.pop();
            return;
         case 2:
            Object typeVal = null;
            ComplexType topComplex = (ComplexType)topType;
            int index = this.findMatchingElement(topComplex, URI, name);
            if (!this._matched) {
               this._stateStack.pop();
               if (this._stateStack.isEmpty()) {
                  topType = (Type)this._typeStack.pop();
                  if (topType instanceof Element && isRightElement(((Element)topType).name, URI, name)) {
                     return;
                  }

                  this.generateError("There is a mismatch between response message and element description.");
               } else {
                  this._state = this._stateStack.peek();
                  if (this._state != 2) {
                     this.generateError("There is a mismatch between response message and element description.");
                  }

                  topType = (Type)this._typeStack.pop();
                  topComplex = (ComplexType)topType;
                  index = this.findMatchingElement(topComplex, URI, name);
                  if (this._matched) {
                     if (!this._isNillable) {
                        Object[] elms = (Object[])this._objectGraphStack.peek();
                        int len = elms.length;

                        for (int i = 0; i < len; i++) {
                           if (elms[i] == null) {
                              this.generateError("not nillable element of array type in response is missed.");
                           }
                        }

                        typeVal = this._objectGraphStack.pop();
                     }
                  } else {
                     if (this.isHandlingFaultDetail(URI, name)) {
                        this.handleFaultDetail(topComplex);
                        this._typeStack.push(topType);
                        return;
                     }

                     this.generateError("There is a mismatch between response message and element description.");
                  }
               }
            }

            Object[] elements = (Object[])this._objectGraphStack.peek();
            boolean typeValFault = false;
            this.checkElementMatching(topComplex.elements[index], URI, name);
            if (OperationImpl.processingFault && this._faultDetail != null && index == 3) {
               typeVal = elements[3];
               typeValFault = true;
            } else if (typeVal == null && !this._isNillable) {
               if (topComplex.elements[index].contentType.value >= 8) {
                  if (!topComplex.elements[index].isNillable) {
                     this.generateError("Nillable mismatch.");
                  }

                  if (elements[index] == null) {
                     elements[index] = NIL;
                  }

                  this._stateStack.pop();
                  if (this._objectGraphStack.size() > 1) {
                     typeVal = this._objectGraphStack.pop();
                     elements = (Object[])this._objectGraphStack.peek();
                     elements[index] = typeVal;
                  }

                  return;
               }

               typeVal = this.decodeSimpleType(topComplex.elements[index].contentType, this._charData.toString());
            }

            if (topComplex.elements[index].isArray) {
               Vector arrayElement = (Vector)elements[index];
               if (typeVal == null) {
                  arrayElement.addElement(NIL);
               } else {
                  arrayElement.addElement(typeVal);
               }
            } else if (elements[index] != null && !typeValFault) {
               this.generateError("One of low-level ComplexType members is duplicated.");
            } else {
               for (int i = 0; i < index; i++) {
                  if (elements[i] == null && !topComplex.elements[i].isNillable && topComplex.elements[i].minOccurs != 0) {
                     this.generateError("wrong order of child element in response.");
                  }
               }

               elements[index] = typeVal;
            }

            this._typeStack.push(topType);
      }
   }

   @Override
   public final void characters(char[] chars, int start, int len) {
      this._charData.append(chars, start, len);
   }

   @Override
   public final void ignorableWhitespace(char[] ch, int start, int length) {
      this._charData.append(ch, start, length);
   }

   private final String getTagName(String localName, String qName) {
      this._tagName = localName;
      if (this._tagName == null || this._tagName.length() == 0) {
         this._tagName = qName;
      }

      return this._tagName;
   }

   private final void graphTemplateForComplex(ComplexType complexType) {
      if (!this._isNillable) {
         int length = complexType.elements.length;
         Object[] ogTemplate = new Object[length];

         for (int i = 0; i < length; i++) {
            if (complexType.elements[i].isArray) {
               Element element = complexType.elements[i];
               Vector vect = (Vector)(new Object());
               vect.addElement(new Object(element.contentType.value));
               vect.addElement(new Object(element.isNillable));
               ogTemplate[i] = vect;
            }
         }

         this._objectGraphStack.push(ogTemplate);
      }

      this._typeStack.push(complexType);
      this._stateStack.push(2);
   }

   private final void graphTemplateForSimple(Element element) {
      if (this._objectGraphStack.isEmpty() && !this._isNillable && element.isArray) {
         Vector vect = (Vector)(new Object());
         vect.addElement(new Object(element.contentType.value));
         vect.addElement(new Object(element.isNillable));
         this._objectGraphStack.push(vect);
      }

      this._typeStack.push(element);
      this._stateStack.push(1);
   }

   private final void graphTemplateForElement(Element element) {
      if (element.contentType instanceof ComplexType) {
         this.graphTemplateForComplex((ComplexType)element.contentType);
      } else {
         this.graphTemplateForSimple(element);
      }
   }

   private final void handleEnvelopeAndBody(String URI, String startOrEnd) {
      if (this._tagName.toLowerCase().equals("envelope") && !URI.equals("http://schemas.xmlsoap.org/soap/envelope/")) {
         this.generateError(
            ((StringBuffer)(new Object("Invalid URI from server: ")))
               .append(URI)
               .append(", expected: ")
               .append("http://schemas.xmlsoap.org/soap/envelope/")
               .toString()
         );
      }

      if (startOrEnd.equals("start")) {
         this._bodyAndEnvelopeCount++;
      } else {
         if (this._tagName.toLowerCase().equals("body")) {
            this._noMoreElement = true;
         }

         this._bodyAndEnvelopeCount--;
      }
   }

   private final void handleFaultDetail(Type simpleType) {
      Object faultDetail = null;
      switch (this._state) {
         case 0:
            break;
         case 1:
         default:
            faultDetail = this.decodeSimpleType(simpleType, this._charData.toString());
            break;
         case 2:
            faultDetail = this._objectGraphStack.pop();
      }

      Object[] faultElements = (Object[])this._objectGraphStack.peek();
      faultElements[3] = faultDetail;
   }

   private final void handleHeader(String URI, String startOrEnd) {
      if (this._bodyAndEnvelopeCount != 1) {
         this.generateError("The header element must be the first immediate child element of the SOAP envelope.");
      }

      if (startOrEnd.equals("start")) {
         this._processingHeader = true;
      } else {
         this._processingHeader = false;
      }
   }

   private final void handleHeaderBlock(Attributes attributes) {
      String mustUnderstand = attributes.getValue("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
      if (mustUnderstand != null) {
         if (!mustUnderstand.equals("0") && !mustUnderstand.equals("1")) {
            this.generateError("The mustUnderstand attribute must use the lexical forms \"0\" and \"1\".");
         }

         if (mustUnderstand.equals("1")) {
            this.generateError("Unsupported header element with mustUnderstand set to \"1\".");
         }
      }
   }

   private final boolean isHandlingFaultDetail(String URI, String name) {
      return this._faultDetail != null && this._faultDetailName != null && isRightElement(this._faultDetailName, URI, name);
   }

   private final Object decodeFault(Object objectGraph) throws MarshalException, ServerException {
      Object[] fault = (Object[])objectGraph;
      String faultCode = (String)fault[0];
      if (faultCode == null) {
         throw new JAXRPCException("The faultcode is missing.");
      }

      if (faultCode.indexOf("DataEncodingUnknown") != -1) {
         throw new MarshalException("Parameters are encoded in a data encoding unknown to the server.");
      }

      if (faultCode.indexOf("Server") != -1) {
         throw new ServerException("Server cannot handle the message because of some temporary condition.");
      }

      if (fault[1] == null) {
         throw new JAXRPCException("The faultstring is missing.");
      }

      String detailMessage;
      if (fault[2] == null) {
         detailMessage = ((StringBuffer)(new Object("\nfaultcode:   "))).append(faultCode).append("\nfaultstring: ").append((String)fault[1]).toString();
      } else {
         detailMessage = ((StringBuffer)(new Object("\nfaultcode:   ")))
            .append(faultCode)
            .append("\nfaultstring: ")
            .append((String)fault[1])
            .append("\nfaultfactor: ")
            .append((String)fault[2])
            .toString();
      }

      if (this._faultDetailName == null) {
         if (fault[3] != null) {
            detailMessage = ((StringBuffer)(new Object())).append(detailMessage).append("\ndetail:      ").append((String)fault[3]).toString();
         }

         throw new JAXRPCException(new ServerException(detailMessage));
      } else {
         throw new JAXRPCException(detailMessage, new FaultDetailException(this._faultDetailName, this.decodeObjectGraph(fault[3])));
      }
   }

   private final Object decodeObjectGraph(Object objectGraph) {
      if (objectGraph instanceof Object[]) {
         Object[] objectArray = (Object[])objectGraph;
         int length = objectArray.length;

         for (int i = 0; i < length; i++) {
            Object var10000 = objectArray[i];
            if (!(objectArray[i] instanceof Object)) {
               if (objectArray[i] instanceof Object[]) {
                  objectArray[i] = this.decodeObjectGraph(objectArray[i]);
               } else if (objectArray[i] == NIL) {
                  objectArray[i] = null;
               }
            } else {
               Vector vect = (Vector)var10000;
               objectArray[i] = this.decodeVector(vect);
            }
         }
      } else if (objectGraph instanceof Object) {
         objectGraph = this.decodeVector((Vector)objectGraph);
      }

      return objectGraph;
   }

   private final Object decodeVector(Vector typedVect) {
      Vector vect = typedVect;
      int typeValue = vect.elementAt(0);
      boolean isNillable = vect.elementAt(1);
      vect.removeElementAt(0);
      vect.removeElementAt(0);
      int n = vect.size();
      switch (typeValue) {
         case -1:
            Object[] elements = new Object[n];
            vect.copyInto(elements);
            return this.decodeObjectGraph(elements);
         case 0:
         default:
            if (isNillable) {
               Boolean[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Boolean)obj;
                  }
               }

               return arrayPS;
            }

            boolean[] arrayPS = new boolean[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 1:
            if (isNillable) {
               Byte[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Byte)obj;
                  }
               }

               return arrayPS;
            }

            byte[] arrayPS = new byte[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 2:
            if (isNillable) {
               Short[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Short)obj;
                  }
               }

               return arrayPS;
            }

            short[] arrayPS = new short[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 3:
            if (isNillable) {
               Integer[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Integer)obj;
                  }
               }

               return arrayPS;
            }

            int[] arrayPS = new int[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 4:
            if (isNillable) {
               Long[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Long)obj;
                  }
               }

               return arrayPS;
            }

            long[] arrayPS = new long[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 5:
            if (isNillable) {
               Float[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Float)obj;
                  }
               }

               return arrayPS;
            }

            float[] arrayPS = new float[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 6:
            if (isNillable) {
               Double[] arrayPS = new Object[n];

               for (int j = n - 1; j >= 0; j--) {
                  Object obj = vect.elementAt(j);
                  if (obj != NIL) {
                     arrayPS[j] = (Double)obj;
                  }
               }

               return arrayPS;
            }

            double[] arrayPS = new double[n];

            for (int j = n - 1; j >= 0; j--) {
               arrayPS[j] = vect.elementAt(j);
            }

            return arrayPS;
         case 7:
            Object[] arrayPS = new Object[n];

            for (int j = n - 1; j >= 0; j--) {
               Object obj = vect.elementAt(j);
               if (obj != NIL) {
                  arrayPS[j] = obj;
               }
            }

            return arrayPS;
      }
   }

   private final Object decodeSimpleType(Type type, String simpleValue) {
      int valueType = type.value;
      if (valueType > 0 && valueType < 5 && simpleValue.startsWith("+")) {
         simpleValue = simpleValue.substring(1);
      }

      switch (valueType) {
         case -1:
            this._stateStack.push(4);
            this._errorMessage = ((StringBuffer)(new Object("Expected Java object wrapper for a primitive type, received: "))).append(simpleValue).toString();
            throw new JAXRPCException(this._errorMessage);
         case 0:
         default:
            simpleValue = simpleValue.toLowerCase();
            if (simpleValue.equals("true") || simpleValue.equals("1")) {
               return new Object(true);
            } else if (simpleValue.equals("false") || simpleValue.equals("0")) {
               return new Object(false);
            } else {
               this.generateError(((StringBuffer)(new Object("Expected Boolean, received "))).append(simpleValue).toString());
            }
         case 1:
            label388:
            try {
               return new Object(Byte.parseByte(simpleValue));
            } finally {
               this.generateError(((StringBuffer)(new Object("Expected Byte, received "))).append(simpleValue).toString());
               break label388;
            }
         case 2:
            label386:
            try {
               return new Object(Short.parseShort(simpleValue));
            } finally {
               this.generateError(((StringBuffer)(new Object("Expected Short, received "))).append(simpleValue).toString());
               break label386;
            }
         case 3:
            label384:
            try {
               return new Object(Integer.parseInt(simpleValue));
            } finally {
               this.generateError(((StringBuffer)(new Object("Expected Integer, received "))).append(simpleValue).toString());
               break label384;
            }
         case 4:
            label382:
            try {
               return new Object(Long.parseLong(simpleValue));
            } finally {
               this.generateError(((StringBuffer)(new Object("Expected Long, received "))).append(simpleValue).toString());
               break label382;
            }
         case 5:
            label380:
            try {
               if (simpleValue.equals("INF")) {
                  return new Object((float)2139095040);
               } else if (simpleValue.equals("-INF")) {
                  return new Object((float)-8388608);
               } else {
                  if (simpleValue.equals("NaN")) {
                     return new Object((float)2143289344);
                  }

                  return Float.valueOf(simpleValue);
               }
            } finally {
               this.generateError(((StringBuffer)(new Object("Expected Float, received "))).append(simpleValue).toString());
               break label380;
            }
         case 6:
            try {
               if (simpleValue.equals("INF")) {
                  return new Object((double)9218868437227405312L);
               } else if (simpleValue.equals("-INF")) {
                  return new Object((double)-4503599627370496L);
               } else {
                  if (simpleValue.equals("NaN")) {
                     return new Object((double)9221120237041090560L);
                  }

                  return Double.valueOf(simpleValue);
               }
            } finally {
               this.generateError(((StringBuffer)(new Object("Expected Double, received "))).append(simpleValue).toString());
               return simpleValue;
            }
         case 7:
            return simpleValue;
      }
   }

   private final void clearStacks() {
      this._typeStack.removeAllElements();
      this._stateStack.removeAllElements();
      this._objectGraphStack.removeAllElements();
   }

   private final void checkElementMatching(Element element, String URI, String name) {
      if (!name.equals(element.name.getLocalPart())) {
         this.generateError(
            ((StringBuffer)(new Object("Invalid local name from server: "))).append(name).append(", expected: ").append(element.name.getLocalPart()).toString()
         );
      } else if (!URI.equals(element.name.getNamespaceURI())) {
         this.generateError(
            ((StringBuffer)(new Object("Invalid Namespace URI from server: ")))
               .append(URI)
               .append(", expected: ")
               .append(element.name.getNamespaceURI())
               .append(" for element: ")
               .append(name)
               .toString()
         );
      } else {
         if (this._isNillable && !element.isNillable) {
            this.generateError(((StringBuffer)(new Object("Nillable mismatch from server for: "))).append(name).toString());
         }
      }
   }

   private final int findMatchingElement(ComplexType complex, String URI, String name) {
      int length = complex.elements.length;

      for (int i = 0; i < length; i++) {
         if (isRightElement(complex.elements[i].name, URI, name)) {
            this._matched = true;
            return i;
         }
      }

      this._matched = false;
      return -1;
   }

   private final void generateError(String errorMessage) {
      this._stateStack.push(4);
      this._errorMessage = errorMessage;
      throw new Object(this._errorMessage);
   }

   private static final boolean isRightElement(QName qname, String URI, String name) {
      return qname.getNamespaceURI().equals(URI) && qname.getLocalPart().equals(name);
   }
}
