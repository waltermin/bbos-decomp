package net.rim.device.apps.internal.browser.javascript;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.markup.HTMLBinaryConstantsTagProvider;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.device.internal.browser.util.TimeLogger;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.runtime.ESFunction;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.ecmascript.util.Misc;
import net.rim.vm.ThreadSpecificData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLAppletElement;
import org.w3c.dom.html2.HTMLAreaElement;
import org.w3c.dom.html2.HTMLBRElement;
import org.w3c.dom.html2.HTMLBaseElement;
import org.w3c.dom.html2.HTMLBaseFontElement;
import org.w3c.dom.html2.HTMLBodyElement;
import org.w3c.dom.html2.HTMLButtonElement;
import org.w3c.dom.html2.HTMLDListElement;
import org.w3c.dom.html2.HTMLDirectoryElement;
import org.w3c.dom.html2.HTMLDivElement;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFieldSetElement;
import org.w3c.dom.html2.HTMLFontElement;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLFrameElement;
import org.w3c.dom.html2.HTMLFrameSetElement;
import org.w3c.dom.html2.HTMLHRElement;
import org.w3c.dom.html2.HTMLHeadElement;
import org.w3c.dom.html2.HTMLHeadingElement;
import org.w3c.dom.html2.HTMLHtmlElement;
import org.w3c.dom.html2.HTMLIFrameElement;
import org.w3c.dom.html2.HTMLImageElement;
import org.w3c.dom.html2.HTMLInputElement;
import org.w3c.dom.html2.HTMLIsIndexElement;
import org.w3c.dom.html2.HTMLLIElement;
import org.w3c.dom.html2.HTMLLabelElement;
import org.w3c.dom.html2.HTMLLegendElement;
import org.w3c.dom.html2.HTMLLinkElement;
import org.w3c.dom.html2.HTMLMapElement;
import org.w3c.dom.html2.HTMLMenuElement;
import org.w3c.dom.html2.HTMLMetaElement;
import org.w3c.dom.html2.HTMLModElement;
import org.w3c.dom.html2.HTMLOListElement;
import org.w3c.dom.html2.HTMLObjectElement;
import org.w3c.dom.html2.HTMLOptGroupElement;
import org.w3c.dom.html2.HTMLOptionElement;
import org.w3c.dom.html2.HTMLParagraphElement;
import org.w3c.dom.html2.HTMLParamElement;
import org.w3c.dom.html2.HTMLPreElement;
import org.w3c.dom.html2.HTMLQuoteElement;
import org.w3c.dom.html2.HTMLScriptElement;
import org.w3c.dom.html2.HTMLSelectElement;
import org.w3c.dom.html2.HTMLStyleElement;
import org.w3c.dom.html2.HTMLTableCaptionElement;
import org.w3c.dom.html2.HTMLTableCellElement;
import org.w3c.dom.html2.HTMLTableColElement;
import org.w3c.dom.html2.HTMLTableElement;
import org.w3c.dom.html2.HTMLTableRowElement;
import org.w3c.dom.html2.HTMLTableSectionElement;
import org.w3c.dom.html2.HTMLTextAreaElement;
import org.w3c.dom.html2.HTMLTitleElement;
import org.w3c.dom.html2.HTMLUListElement;

public final class JavaScriptEngine implements JavaScriptInterpreter {
   private ByteArrayOutputStream _bytesOut;
   private OutputStreamWriter _outputWriter;
   private GlobalObject _globalObject;
   private ESWindow _window;
   private int _functionCount;
   private StringBuffer _compileBuffer;
   private Hashtable _actionsToFunctions;
   private boolean _suppressPopups;
   private boolean _javascriptEnabled;
   private boolean _quicySent;
   private boolean _userBasedAction;
   private Hashtable _esObjectTable;
   private long _clickID = -1;
   private int _watchDogTimeout;
   private int _watchDogId = -1;
   private Runnable _watchDog = new JavaScriptEngine$WatchDog(this);
   BrowserContent _browserContent;
   ESElementPrototype _elementPrototype;
   ESDOMExceptionPrototype _domExceptionPrototype;
   ESNamedNodeMapPrototype _namedNodeMapPrototype;
   ESCharacterDataPrototype _characterDataPrototype;
   ESNodeListPrototype _nodeListPrototype;
   ESNodePrototype _nodePrototype;
   ESTextPrototype _textPrototype;
   ESHTMLAnchorElementPrototype _htmlAnchorElementPrototype;
   ESHTMLTextAreaPrototype _htmlTextAreaPrototype;
   ESHTMLTableElementPrototype _htmlTableElementPrototype;
   ESHTMLTableRowElementPrototype _htmlTableRowElementPrototype;
   ESHTMLTableSectionElementPrototype _htmlTableSectionElementPrototype;
   ESHTMLSelectElementPrototype _selectPrototype;
   ESWindowPrototype _windowPrototype;
   ESHTMLOptionElementPrototype _optionPrototype;
   ESHTMLInputElementPrototype _inputPrototype;
   ESHTMLImageElementPrototype _htmlImagePrototype;
   ESHTMLDocumentPrototype _documentPrototype;
   ESHTMLFormElementPrototype _formPrototype;
   ESHistoryPrototype _historyPrototype;
   ESXMLHttpRequestPrototype _xmlHttpRequestPrototype;
   ESBlackberry _blackberry;
   RenderingOptions _renderingOptions;
   private static String SPECIAL_METHOD = "RIMInternalBrowserJSFunction";
   static String[] CORE_MIME_TYPES_DEBUG = new Object[]{
      ((StringBuffer)(new Object("application/vnd.rim.jscriptc;v=")))
         .append(Compiler.getMajorVersion())
         .append('-')
         .append(Compiler.getMinorVersion())
         .append("-0")
         .toString(),
      "application/x-javascript"
   };
   static String[] CORE_MIME_TYPES = new Object[]{
      ((StringBuffer)(new Object("application/vnd.rim.jscriptc;v=")))
         .append(Compiler.getMajorVersion())
         .append('-')
         .append(Compiler.getMinorVersion())
         .append('-')
         .append(8)
         .toString(),
      "application/x-javascript"
   };
   private static boolean DEBUG = DeviceInfo.isSimulator();
   private static final int MAX_CACHE_SIZE = 50;
   private static int _cacheSize = 0;
   private static int[] _verifiedScriptsCache = new int[50];
   private static byte[][][] _verifiedScriptsHash = new byte[50][][];
   private static SHA1Digest _verifyHash = (SHA1Digest)(new Object());

   final ESWindow getCurrentWindow() {
      return this._window;
   }

   final ESObject lookupElementToESObject(Node node) {
      if (node == null) {
         return null;
      }

      ESObject obj = (ESObject)this._esObjectTable.get(node);
      if (obj != null) {
         return obj;
      }

      if (node instanceof HTMLBinaryConstantsTagProvider) {
         switch (((HTMLBinaryConstantsTagProvider)node).getTagNameInt()) {
            case 4:
            case 6:
            case 7:
            case 8:
            case 11:
            case 14:
            case 15:
            case 16:
            case 21:
            case 22:
            case 23:
            case 25:
            case 26:
            case 28:
            case 32:
            case 33:
            case 48:
            case 54:
            case 62:
            case 63:
            case 70:
            case 73:
            case 74:
            case 77:
            case 78:
            case 79:
            case 80:
            case 82:
            case 83:
            case 93:
            case 94:
               break;
            case 5:
            default:
               obj = new ESHTMLAnchorElement((HTMLAnchorElement)node);
               break;
            case 9:
               obj = new ESHTMLAppletElement((HTMLAppletElement)node);
               break;
            case 10:
               obj = new ESHTMLAreaElement((HTMLAreaElement)node);
               break;
            case 12:
               obj = new ESHTMLBaseElement((HTMLBaseElement)node);
               break;
            case 13:
               obj = new ESHTMLBaseFontElement((HTMLBaseFontElement)node);
               break;
            case 17:
               obj = new ESHTMLBodyElement((HTMLBodyElement)node);
               break;
            case 18:
               obj = new ESHTMLBRElement((HTMLBRElement)node);
               break;
            case 19:
               obj = new ESHTMLButtonElement((HTMLButtonElement)node);
               break;
            case 20:
               obj = new ESHTMLTableCaptionElement((HTMLTableCaptionElement)node);
               break;
            case 24:
               obj = new ESHTMLTableColElement((HTMLTableColElement)node);
               break;
            case 27:
            case 52:
               obj = new ESHTMLModElement((HTMLModElement)node);
               break;
            case 29:
               obj = new ESHTMLDirectoryElement((HTMLDirectoryElement)node);
               break;
            case 30:
               obj = new ESHTMLDivElement((HTMLDivElement)node);
               break;
            case 31:
               obj = new ESHTMLDListElement((HTMLDListElement)node);
               break;
            case 34:
               obj = new ESHTMLFieldSetElement((HTMLFieldSetElement)node);
               break;
            case 35:
               obj = new ESHTMLFontElement((HTMLFontElement)node);
               break;
            case 36:
               obj = new ESHTMLFormElement((HTMLFormElement)node);
               break;
            case 37:
               obj = new ESHTMLFrameElement((HTMLFrameElement)node);
               break;
            case 38:
               obj = new ESHTMLFrameSetElement((HTMLFrameSetElement)node);
               break;
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
               obj = new ESHTMLHeadingElement((HTMLHeadingElement)node);
               break;
            case 45:
               obj = new ESHTMLHeadElement((HTMLHeadElement)node);
               break;
            case 46:
               obj = new ESHTMLHRElement((HTMLHRElement)node);
               break;
            case 47:
               obj = new ESHTMLHtmlElement((HTMLHtmlElement)node);
               break;
            case 49:
               obj = new ESHTMLIFrameElement((HTMLIFrameElement)node);
               break;
            case 50:
               obj = new ESHTMLImageElement((HTMLImageElement)node);
               break;
            case 51:
               obj = new ESHTMLInputElement((HTMLInputElement)node);
               break;
            case 53:
               obj = new ESHTMLIsIndexElement((HTMLIsIndexElement)node);
               break;
            case 55:
               obj = new ESHTMLLabelElement((HTMLLabelElement)node);
               break;
            case 56:
               obj = new ESHTMLLegendElement((HTMLLegendElement)node);
               break;
            case 57:
               obj = new ESHTMLLIElement((HTMLLIElement)node);
               break;
            case 58:
               obj = new ESHTMLLinkElement((HTMLLinkElement)node);
               break;
            case 59:
               obj = new ESHTMLMapElement((HTMLMapElement)node);
               break;
            case 60:
               obj = new ESHTMLMenuElement((HTMLMenuElement)node);
               break;
            case 61:
               obj = new ESHTMLMetaElement((HTMLMetaElement)node);
               break;
            case 64:
               obj = new ESHTMLObjectElement((HTMLObjectElement)node);
               break;
            case 65:
               obj = new ESHTMLOListElement((HTMLOListElement)node);
               break;
            case 66:
               obj = new ESHTMLOptGroupElement((HTMLOptGroupElement)node);
               break;
            case 67:
               obj = new ESHTMLOptionElement((HTMLOptionElement)node);
               break;
            case 68:
               obj = new ESHTMLParagraphElement((HTMLParagraphElement)node);
               break;
            case 69:
               obj = new ESHTMLParamElement((HTMLParamElement)node);
               break;
            case 71:
               obj = new ESHTMLPreElement((HTMLPreElement)node);
               break;
            case 72:
               obj = new ESHTMLQuoteElement((HTMLQuoteElement)node);
               break;
            case 75:
               obj = new ESHTMLScriptElement((HTMLScriptElement)node);
               break;
            case 76:
               obj = new ESHTMLSelectElement((HTMLSelectElement)node);
               break;
            case 81:
               obj = new ESHTMLStyleElement((HTMLStyleElement)node);
               break;
            case 84:
               obj = new ESHTMLTableElement((HTMLTableElement)node);
               break;
            case 85:
            case 88:
            case 90:
               obj = new ESHTMLTableSectionElement((HTMLTableSectionElement)node);
               break;
            case 86:
            case 89:
               obj = new ESHTMLTableCellElement((HTMLTableCellElement)node);
               break;
            case 87:
               obj = new ESHTMLTextAreaElement((HTMLTextAreaElement)node);
               break;
            case 91:
               obj = new ESHTMLTitleElement((HTMLTitleElement)node);
               break;
            case 92:
               obj = new ESHTMLTableRowElement((HTMLTableRowElement)node);
               break;
            case 95:
               obj = new ESHTMLUListElement((HTMLUListElement)node);
         }
      }

      if (obj == null) {
         if (obj instanceof HTMLElement) {
            obj = new ESHTMLElement((HTMLElement)node);
         } else {
            obj = new ESNode(node);
         }
      }

      if (obj != null) {
         this._esObjectTable.put(node, obj);
         return obj;
      } else {
         return null;
      }
   }

   final long lookupElementToESObjectLong(Node node) {
      if (node == null) {
         return Value.NULL;
      }

      ESObject obj = this.lookupElementToESObject(node);
      return obj != null ? Value.makeObjectValue(obj) : Value.UNDEFINED;
   }

   public final synchronized void openPrintStream() {
      if (this._javascriptEnabled) {
         try {
            this._bytesOut = (ByteArrayOutputStream)(new Object());
            this._outputWriter = (OutputStreamWriter)(new Object(this._bytesOut, "utf-8"));
         } finally {
            this._bytesOut = null;
            return;
         }
      }
   }

   public final synchronized void closePrintStream() {
      if (this._javascriptEnabled && this._outputWriter != null) {
         try {
            this._outputWriter.close();
         } finally {
            return;
         }
      }
   }

   final long lookupElementToESObjectLong(Object objList) {
      if (objList == null) {
         return Value.NULL;
      }

      if (objList instanceof Object) {
         return this.lookupElementToESObjectLong((Node)objList);
      }

      NodeList nodes = (NodeList)objList;
      int length = nodes.getLength();
      ESObject obj = this.lookupElementToESObject(nodes.item(0));
      if (obj == null) {
         return Value.UNDEFINED;
      }

      if (length == 1) {
         return Value.makeObjectValue(obj);
      }

      if (!(obj instanceof ESHTMLSelectElement)) {
         if (obj instanceof ESHTMLInputElement) {
            ESHTMLInputElement input = (ESHTMLInputElement)obj;
            input.setElementArray(nodes);
         }
      } else {
         ESHTMLSelectElement select = (ESHTMLSelectElement)obj;
         select.setElementArray(nodes);
      }

      return Value.makeObjectValue(obj);
   }

   public final synchronized OutputStreamWriter getCurrentStream() {
      return this._outputWriter;
   }

   final void resetSuppressPopups() {
      this._suppressPopups = !this._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 13, false);
   }

   final void setSuppressPopups(boolean suppressPopups) {
      this._suppressPopups = suppressPopups;
   }

   final boolean suppressPopups() {
      return this._suppressPopups;
   }

   public final synchronized String addFunction(String action) {
      if (!this._javascriptEnabled) {
         return null;
      }

      if (this._actionsToFunctions.containsKey(action)) {
         return (String)this._actionsToFunctions.get(action);
      }

      if (this._compileBuffer == null) {
         this._compileBuffer = (StringBuffer)(new Object());
      }

      String functionName = Misc.stringIntern(((StringBuffer)(new Object())).append(SPECIAL_METHOD).append(this._functionCount).toString());
      this._functionCount++;
      this._compileBuffer.append("function ").append(functionName).append("() {\nwith(this) {\n").append(action).append("\n}\n}\n");
      this._actionsToFunctions.put(action, functionName);
      return functionName;
   }

   final long getClickID() {
      return this._clickID;
   }

   public final void setUserBasedAction(boolean userBasedAction) {
      this._userBasedAction = userBasedAction;
   }

   final synchronized boolean executeMethodInternal(Object param1, Object param2, Object[] param3, boolean param4, long param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._window Lnet/rim/device/apps/internal/browser/javascript/ESWindow;
      // 004: invokevirtual net/rim/device/apps/internal/browser/javascript/ESWindow.getSyncObject ()Ljava/lang/Object;
      // 007: dup
      // 008: astore 7
      // 00a: monitorenter
      // 00b: aload 0
      // 00c: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._javascriptEnabled Z
      // 00f: ifeq 019
      // 012: aload 0
      // 013: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 016: ifnonnull 01e
      // 019: bipush 1
      // 01a: aload 7
      // 01c: monitorexit
      // 01d: ireturn
      // 01e: aload 0
      // 01f: iload 4
      // 021: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._userBasedAction Z
      // 024: aload 0
      // 025: lload 5
      // 027: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._clickID J
      // 02a: aload 0
      // 02b: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.setInstance ()V
      // 02e: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 031: ifeq 040
      // 034: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 037: bipush 7
      // 039: aload 2
      // 03a: invokevirtual java/lang/Object.hashCode ()I
      // 03d: invokevirtual net/rim/device/internal/browser/util/TimeLogger.startTimer (II)V
      // 040: iload 4
      // 042: ifeq 04d
      // 045: aload 0
      // 046: bipush 0
      // 047: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._suppressPopups Z
      // 04a: goto 051
      // 04d: aload 0
      // 04e: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.resetSuppressPopups ()V
      // 051: aconst_null
      // 052: astore 8
      // 054: aload 2
      // 055: instanceof java/lang/Object
      // 058: ifne 05e
      // 05b: goto 149
      // 05e: aload 0
      // 05f: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._actionsToFunctions Ljava/util/Hashtable;
      // 062: aload 2
      // 063: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 066: checkcast java/lang/Object
      // 069: astore 8
      // 06b: aload 8
      // 06d: ifnonnull 07a
      // 070: aload 0
      // 071: aload 2
      // 072: checkcast java/lang/Object
      // 075: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.addFunction (Ljava/lang/String;)Ljava/lang/String;
      // 078: astore 8
      // 07a: aload 0
      // 07b: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._compileBuffer Ljava/lang/StringBuffer;
      // 07e: ifnonnull 084
      // 081: goto 149
      // 084: aconst_null
      // 085: astore 9
      // 087: new java/lang/Object
      // 08a: dup
      // 08b: aload 0
      // 08c: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._compileBuffer Ljava/lang/StringBuffer;
      // 08f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 092: invokespecial net/rim/ecmascript/compiler/Compiler.<init> (Ljava/lang/String;)V
      // 095: astore 10
      // 097: aload 10
      // 099: invokevirtual net/rim/ecmascript/compiler/Compiler.compile ()Lnet/rim/ecmascript/runtime/CompiledScript;
      // 09c: astore 9
      // 09e: aload 0
      // 09f: aconst_null
      // 0a0: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._compileBuffer Ljava/lang/StringBuffer;
      // 0a3: goto 0d5
      // 0a6: astore 10
      // 0a8: getstatic net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.DEBUG Z
      // 0ab: ifeq 0c9
      // 0ae: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0b1: new java/lang/Object
      // 0b4: dup
      // 0b5: ldc_w "CompileError in ExecuteMethod "
      // 0b8: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0bb: aload 10
      // 0bd: invokevirtual net/rim/ecmascript/compiler/CompileError.toString ()Ljava/lang/String;
      // 0c0: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c3: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0c6: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0c9: aload 0
      // 0ca: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy ()V
      // 0cd: new java/lang/Object
      // 0d0: dup
      // 0d1: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0d4: athrow
      // 0d5: aload 9
      // 0d7: ifnonnull 0dd
      // 0da: goto 149
      // 0dd: aload 0
      // 0de: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 0e1: aload 9
      // 0e3: invokevirtual net/rim/ecmascript/runtime/GlobalObject.run (Lnet/rim/ecmascript/runtime/CompiledScript;)J
      // 0e6: pop2
      // 0e7: goto 149
      // 0ea: astore 10
      // 0ec: getstatic net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.DEBUG Z
      // 0ef: ifeq 10d
      // 0f2: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0f5: new java/lang/Object
      // 0f8: dup
      // 0f9: ldc_w "ThrownValue in ExecuteMethod "
      // 0fc: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0ff: aload 10
      // 101: invokevirtual net/rim/ecmascript/runtime/ThrownValue.toString ()Ljava/lang/String;
      // 104: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 107: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 10a: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 10d: ldc2_w 1907089860548946979
      // 110: new java/lang/Object
      // 113: dup
      // 114: ldc_w "Unsupported JavaScript Report: "
      // 117: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 11a: aload 10
      // 11c: invokevirtual net/rim/ecmascript/runtime/ThrownValue.toString ()Ljava/lang/String;
      // 11f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 122: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 125: invokevirtual java/lang/String.getBytes ()[B
      // 128: bipush 0
      // 129: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 12c: pop
      // 12d: aload 0
      // 12e: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy ()V
      // 131: new java/lang/Object
      // 134: dup
      // 135: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 138: athrow
      // 139: astore 10
      // 13b: aload 0
      // 13c: aload 10
      // 13e: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy (Ljava/lang/Throwable;)V
      // 141: new java/lang/Object
      // 144: dup
      // 145: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 148: athrow
      // 149: aload 0
      // 14a: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.setWatchdog ()V
      // 14d: aconst_null
      // 14e: astore 9
      // 150: aload 2
      // 151: dup
      // 152: instanceof java/lang/Object
      // 155: ifne 15c
      // 158: pop
      // 159: goto 164
      // 15c: checkcast java/lang/Object
      // 15f: astore 9
      // 161: goto 177
      // 164: aload 8
      // 166: ifnull 177
      // 169: aload 0
      // 16a: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 16d: aload 8
      // 16f: invokevirtual net/rim/ecmascript/runtime/RedirectedObject.getField (Ljava/lang/String;)J
      // 172: invokestatic net/rim/ecmascript/runtime/Value.checkIfFunctionValue (J)Lnet/rim/ecmascript/runtime/ESFunction;
      // 175: astore 9
      // 177: aload 9
      // 179: ifnonnull 17f
      // 17c: goto 260
      // 17f: aconst_null
      // 180: astore 10
      // 182: aload 1
      // 183: instanceof java/lang/Object
      // 186: ifeq 193
      // 189: aload 0
      // 18a: aload 1
      // 18b: checkcast java/lang/Object
      // 18e: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.lookupElementToESObject (Lorg/w3c/dom/Node;)Lnet/rim/ecmascript/runtime/ESObject;
      // 191: astore 10
      // 193: aload 10
      // 195: ifnull 1cf
      // 198: aload 0
      // 199: aload 0
      // 19a: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 19d: aload 9
      // 19f: aload 10
      // 1a1: bipush 0
      // 1a2: newarray 11
      // 1a4: invokevirtual net/rim/ecmascript/runtime/GlobalObject.callFunction (Lnet/rim/ecmascript/runtime/ESFunction;Lnet/rim/ecmascript/runtime/ESObject;[J)J
      // 1a7: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.toReturnValue (J)Z
      // 1aa: istore 11
      // 1ac: aload 0
      // 1ad: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 1b0: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 1b3: ifeq 1c2
      // 1b6: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 1b9: bipush 7
      // 1bb: aload 2
      // 1bc: invokevirtual java/lang/Object.hashCode ()I
      // 1bf: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 1c2: aload 0
      // 1c3: bipush -1
      // 1c5: i2l
      // 1c6: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._clickID J
      // 1c9: aload 7
      // 1cb: monitorexit
      // 1cc: iload 11
      // 1ce: ireturn
      // 1cf: aload 0
      // 1d0: aload 0
      // 1d1: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 1d4: aload 9
      // 1d6: invokevirtual net/rim/ecmascript/runtime/GlobalObject.callFunction (Lnet/rim/ecmascript/runtime/ESFunction;)J
      // 1d9: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.toReturnValue (J)Z
      // 1dc: istore 11
      // 1de: aload 0
      // 1df: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 1e2: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 1e5: ifeq 1f4
      // 1e8: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 1eb: bipush 7
      // 1ed: aload 2
      // 1ee: invokevirtual java/lang/Object.hashCode ()I
      // 1f1: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 1f4: aload 0
      // 1f5: bipush -1
      // 1f7: i2l
      // 1f8: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._clickID J
      // 1fb: aload 7
      // 1fd: monitorexit
      // 1fe: iload 11
      // 200: ireturn
      // 201: astore 9
      // 203: getstatic net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.DEBUG Z
      // 206: ifeq 224
      // 209: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 20c: new java/lang/Object
      // 20f: dup
      // 210: ldc_w "ThrownValue in ExecuteMethod "
      // 213: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 216: aload 9
      // 218: invokevirtual net/rim/ecmascript/runtime/ThrownValue.toString ()Ljava/lang/String;
      // 21b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 21e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 221: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 224: ldc2_w 1907089860548946979
      // 227: new java/lang/Object
      // 22a: dup
      // 22b: ldc_w "Unsupported JavaScript Report: "
      // 22e: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 231: aload 9
      // 233: invokevirtual net/rim/ecmascript/runtime/ThrownValue.toString ()Ljava/lang/String;
      // 236: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 239: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 23c: invokevirtual java/lang/String.getBytes ()[B
      // 23f: bipush 0
      // 240: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 243: pop
      // 244: aload 0
      // 245: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy ()V
      // 248: new java/lang/Object
      // 24b: dup
      // 24c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 24f: athrow
      // 250: astore 9
      // 252: aload 0
      // 253: aload 9
      // 255: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy (Ljava/lang/Throwable;)V
      // 258: new java/lang/Object
      // 25b: dup
      // 25c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 25f: athrow
      // 260: aload 0
      // 261: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 264: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 267: ifeq 276
      // 26a: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 26d: bipush 7
      // 26f: aload 2
      // 270: invokevirtual java/lang/Object.hashCode ()I
      // 273: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 276: aload 0
      // 277: bipush -1
      // 279: i2l
      // 27a: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._clickID J
      // 27d: goto 2a2
      // 280: astore 12
      // 282: aload 0
      // 283: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 286: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 289: ifeq 298
      // 28c: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 28f: bipush 7
      // 291: aload 2
      // 292: invokevirtual java/lang/Object.hashCode ()I
      // 295: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 298: aload 0
      // 299: bipush -1
      // 29b: i2l
      // 29c: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._clickID J
      // 29f: aload 12
      // 2a1: athrow
      // 2a2: bipush 1
      // 2a3: aload 7
      // 2a5: monitorexit
      // 2a6: ireturn
      // 2a7: astore 13
      // 2a9: aload 7
      // 2ab: monitorexit
      // 2ac: aload 13
      // 2ae: athrow
      // try (64 -> 77): 78 null
      // try (100 -> 105): 106 null
      // try (100 -> 105): 138 null
      // try (146 -> 192): 233 null
      // try (209 -> 216): 233 null
      // try (146 -> 192): 265 null
      // try (209 -> 216): 265 null
      // try (22 -> 192): 287 null
      // try (209 -> 216): 287 null
      // try (233 -> 273): 287 null
      // try (287 -> 288): 287 null
      // try (6 -> 15): 307 null
      // try (16 -> 207): 307 null
      // try (209 -> 231): 307 null
      // try (233 -> 306): 307 null
      // try (307 -> 310): 307 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final ESFunction getHostFunction(String action) {
      String functionStr = (String)this._actionsToFunctions.get(action);
      if (functionStr == null) {
         functionStr = this.addFunction(action);
      }

      if (this._compileBuffer != null) {
         CompiledScript script = null;
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            Compiler ce = new Object(this._compileBuffer.toString());
            script = ((Compiler)ce).compile();
            this._compileBuffer = null;
            var6 = false;
         } finally {
            if (var6) {
               throw new Object(Value.NULL);
            }
         }

         if (script != null) {
            this._globalObject.runRecursive(script);
         }
      }

      return Value.checkIfFunctionValue(this._globalObject.getField(functionStr));
   }

   final boolean isUserBasedAction() {
      return this._userBasedAction;
   }

   @Override
   public final synchronized boolean executeCompiledScript(byte[] data, int offset, int length) {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   @Override
   public final synchronized boolean executeMethod(Object thiz, String action, Object[] parms, boolean userBasedAction, long clickID) {
      return this.executeMethodInternal(thiz, action, parms, userBasedAction, clickID);
   }

   @Override
   public final synchronized boolean executeMethod(Object thiz, String action, Object[] parms, boolean userBasedAction) {
      return this.executeMethod(thiz, action, parms, userBasedAction, -1);
   }

   @Override
   public final synchronized boolean executeScript(String param1, String param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._window Lnet/rim/device/apps/internal/browser/javascript/ESWindow;
      // 004: invokevirtual net/rim/device/apps/internal/browser/javascript/ESWindow.getSyncObject ()Ljava/lang/Object;
      // 007: dup
      // 008: astore 3
      // 009: monitorenter
      // 00a: aload 0
      // 00b: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._javascriptEnabled Z
      // 00e: ifeq 018
      // 011: aload 0
      // 012: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 015: ifnonnull 01c
      // 018: bipush 1
      // 019: aload 3
      // 01a: monitorexit
      // 01b: ireturn
      // 01c: aload 0
      // 01d: bipush 0
      // 01e: putfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._userBasedAction Z
      // 021: aload 0
      // 022: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.openPrintStream ()V
      // 025: aload 0
      // 026: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.setInstance ()V
      // 029: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 02c: ifeq 03b
      // 02f: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 032: bipush 7
      // 034: aload 1
      // 035: invokevirtual java/lang/String.hashCode ()I
      // 038: invokevirtual net/rim/device/internal/browser/util/TimeLogger.startTimer (II)V
      // 03b: aload 0
      // 03c: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.resetSuppressPopups ()V
      // 03f: bipush 0
      // 040: istore 4
      // 042: aload 2
      // 043: ifnull 0db
      // 046: aload 2
      // 047: invokevirtual java/lang/String.length ()I
      // 04a: ifle 0db
      // 04d: aload 2
      // 04e: bipush 46
      // 050: invokevirtual java/lang/String.indexOf (I)I
      // 053: istore 5
      // 055: iload 5
      // 057: ifle 0db
      // 05a: aload 2
      // 05b: bipush 0
      // 05c: iload 5
      // 05e: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 061: astore 6
      // 063: aload 2
      // 064: iload 5
      // 066: bipush 1
      // 067: iadd
      // 068: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 06b: astore 7
      // 06d: aload 6
      // 06f: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 072: istore 8
      // 074: aload 7
      // 076: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 079: istore 9
      // 07b: iload 8
      // 07d: bipush 1
      // 07e: if_icmpne 0db
      // 081: iload 9
      // 083: tableswitch 41 -1 5 88 41 48 55 62 70 78
      // 0ac: bipush 100
      // 0ae: istore 4
      // 0b0: goto 0db
      // 0b3: bipush 110
      // 0b5: istore 4
      // 0b7: goto 0db
      // 0ba: bipush 120
      // 0bc: istore 4
      // 0be: goto 0db
      // 0c1: sipush 130
      // 0c4: istore 4
      // 0c6: goto 0db
      // 0c9: sipush 140
      // 0cc: istore 4
      // 0ce: goto 0db
      // 0d1: sipush 150
      // 0d4: istore 4
      // 0d6: goto 0db
      // 0d9: astore 8
      // 0db: aconst_null
      // 0dc: astore 5
      // 0de: new java/lang/Object
      // 0e1: dup
      // 0e2: aload 1
      // 0e3: iload 4
      // 0e5: invokespecial net/rim/ecmascript/compiler/Compiler.<init> (Ljava/lang/String;I)V
      // 0e8: astore 6
      // 0ea: aload 6
      // 0ec: invokevirtual net/rim/ecmascript/compiler/Compiler.compile ()Lnet/rim/ecmascript/runtime/CompiledScript;
      // 0ef: astore 5
      // 0f1: goto 12a
      // 0f4: astore 6
      // 0f6: getstatic net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.DEBUG Z
      // 0f9: ifeq 11e
      // 0fc: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0ff: aload 1
      // 100: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 103: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 106: new java/lang/Object
      // 109: dup
      // 10a: ldc_w "CompileError in ExecuteScript "
      // 10d: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 110: aload 6
      // 112: invokevirtual net/rim/ecmascript/compiler/CompileError.toString ()Ljava/lang/String;
      // 115: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 118: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 11b: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 11e: aload 0
      // 11f: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy ()V
      // 122: new java/lang/Object
      // 125: dup
      // 126: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 129: athrow
      // 12a: aload 5
      // 12c: ifnonnull 132
      // 12f: goto 1bf
      // 132: aload 0
      // 133: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.setWatchdog ()V
      // 136: aload 0
      // 137: aload 0
      // 138: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 13b: aload 5
      // 13d: invokevirtual net/rim/ecmascript/runtime/GlobalObject.run (Lnet/rim/ecmascript/runtime/CompiledScript;)J
      // 140: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.toReturnValue (J)Z
      // 143: istore 6
      // 145: aload 0
      // 146: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 149: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 14c: ifeq 15b
      // 14f: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 152: bipush 7
      // 154: aload 1
      // 155: invokevirtual java/lang/String.hashCode ()I
      // 158: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 15b: aload 3
      // 15c: monitorexit
      // 15d: iload 6
      // 15f: ireturn
      // 160: astore 6
      // 162: getstatic net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.DEBUG Z
      // 165: ifeq 183
      // 168: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 16b: new java/lang/Object
      // 16e: dup
      // 16f: ldc_w "ThrownValue in ExecuteScript "
      // 172: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 175: aload 6
      // 177: invokevirtual net/rim/ecmascript/runtime/ThrownValue.toString ()Ljava/lang/String;
      // 17a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 17d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 180: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 183: ldc2_w 1907089860548946979
      // 186: new java/lang/Object
      // 189: dup
      // 18a: ldc_w "Unsupported JavaScript Report: "
      // 18d: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 190: aload 6
      // 192: invokevirtual net/rim/ecmascript/runtime/ThrownValue.toString ()Ljava/lang/String;
      // 195: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 198: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 19b: invokevirtual java/lang/String.getBytes ()[B
      // 19e: bipush 0
      // 19f: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 1a2: pop
      // 1a3: aload 0
      // 1a4: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy ()V
      // 1a7: new java/lang/Object
      // 1aa: dup
      // 1ab: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1ae: athrow
      // 1af: astore 6
      // 1b1: aload 0
      // 1b2: aload 6
      // 1b4: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.handleQuincy (Ljava/lang/Throwable;)V
      // 1b7: new java/lang/Object
      // 1ba: dup
      // 1bb: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1be: athrow
      // 1bf: aload 0
      // 1c0: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 1c3: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 1c6: ifeq 1f3
      // 1c9: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 1cc: bipush 7
      // 1ce: aload 1
      // 1cf: invokevirtual java/lang/String.hashCode ()I
      // 1d2: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 1d5: goto 1f3
      // 1d8: astore 10
      // 1da: aload 0
      // 1db: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 1de: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 1e1: ifeq 1f0
      // 1e4: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 1e7: bipush 7
      // 1e9: aload 1
      // 1ea: invokevirtual java/lang/String.hashCode ()I
      // 1ed: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 1f0: aload 10
      // 1f2: athrow
      // 1f3: bipush 1
      // 1f4: aload 3
      // 1f5: monitorexit
      // 1f6: ireturn
      // 1f7: astore 11
      // 1f9: aload 3
      // 1fa: monitorexit
      // 1fb: aload 11
      // 1fd: athrow
      // try (56 -> 84): 85 null
      // try (88 -> 97): 98 null
      // try (125 -> 132): 145 null
      // try (125 -> 132): 177 null
      // try (21 -> 132): 195 null
      // try (145 -> 185): 195 null
      // try (195 -> 196): 195 null
      // try (6 -> 15): 211 null
      // try (16 -> 143): 211 null
      // try (145 -> 210): 211 null
      // try (211 -> 214): 211 null
   }

   @Override
   public final void documentHidden(HTMLDocument doc) {
      if (this._javascriptEnabled) {
         GlobalObject obj = this._globalObject;
         if (obj != null) {
            obj.stop();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void documentClosed(HTMLDocument doc) {
      if (this._javascriptEnabled) {
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            this.setInstance();
            GlobalObject obj = this._globalObject;
            if (obj != null) {
               obj.stop();
            }

            synchronized (this) {
               if (this._blackberry != null) {
                  this._blackberry.documentClosed();
               }

               if (this._globalObject != null) {
                  this._globalObject.clearInstance();
                  this._globalObject = null;
               }

               if (this._window != null) {
                  this._window.cleanup();
               }

               var8 = false;
            }
         } finally {
            if (var8) {
               this.clearInstance();
            }
         }

         this.clearInstance();
      }
   }

   @Override
   public final synchronized byte[] getStreamOutput() {
      if (this._javascriptEnabled && this._bytesOut != null) {
         byte[] bytes = this._bytesOut.toByteArray();
         this._bytesOut = null;
         this._outputWriter = null;
         return bytes;
      } else {
         return null;
      }
   }

   @Override
   public final synchronized void documentLoaded(HTMLDocument param1, String param2) {
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
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._javascriptEnabled Z
      // 04: ifeq 0e
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 0b: ifnonnull 0f
      // 0e: return
      // 0f: aload 0
      // 10: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.setInstance ()V
      // 13: aload 2
      // 14: astore 3
      // 15: aload 3
      // 16: ifnonnull 2c
      // 19: aload 0
      // 1a: getfield net/rim/device/apps/internal/browser/javascript/JavaScriptEngine._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 1d: getstatic net/rim/device/apps/internal/browser/javascript/Names.onLoad Ljava/lang/String;
      // 20: invokevirtual net/rim/ecmascript/runtime/RedirectedObject.getField (Ljava/lang/String;)J
      // 23: invokestatic net/rim/ecmascript/runtime/Value.checkIfFunctionValue (J)Lnet/rim/ecmascript/runtime/ESFunction;
      // 26: astore 3
      // 27: goto 2c
      // 2a: astore 4
      // 2c: aload 3
      // 2d: ifnull 41
      // 30: aload 0
      // 31: aconst_null
      // 32: aload 3
      // 33: aconst_null
      // 34: bipush 0
      // 35: bipush -1
      // 37: i2l
      // 38: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.executeMethodInternal (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;ZJ)Z
      // 3b: pop
      // 3c: goto 41
      // 3f: astore 4
      // 41: aload 0
      // 42: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 45: return
      // 46: astore 5
      // 48: aload 0
      // 49: invokespecial net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.clearInstance ()V
      // 4c: aload 5
      // 4e: athrow
      // try (13 -> 19): 20 null
      // try (23 -> 32): 33 null
      // try (7 -> 34): 37 null
      // try (37 -> 38): 37 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void documentCreated(HTMLDocument doc, BrowserContent content, RenderingSession renderingSession, Frame frame) {
      boolean var7 = false /* VF: Semaphore variable */;

      label107: {
         label108: {
            try {
               var7 = true;
               this.setInstance();
               this._quicySent = false;
               if (TimeLogger._loggingEnabled) {
                  TimeLogger.getInstance().startTimer(9, doc.hashCode());
               }

               if (renderingSession != null) {
                  if (content != null) {
                     this._browserContent = content;
                     this._renderingOptions = renderingSession != null ? renderingSession.getRenderingOptions() : null;
                     this._javascriptEnabled = this._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false);
                     if (this._javascriptEnabled) {
                        this._javascriptEnabled = !ITPolicy.getBoolean(30, 2, false);
                     }

                     if (!this._javascriptEnabled) {
                        var7 = false;
                        break label107;
                     }

                     this._watchDogTimeout = this._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 46, 0);
                     this._actionsToFunctions = (Hashtable)(new Object());
                     this._esObjectTable = (Hashtable)(new Object());
                     GlobalObject.disableLiveConnect();
                     this._globalObject = GlobalObject.newInstance();
                     this._globalObject.setAllowDelayedCompilation(true);
                     this._nodePrototype = new ESNodePrototype();
                     this._elementPrototype = new ESElementPrototype(this._nodePrototype);
                     this._textPrototype = new ESTextPrototype(this._nodePrototype);
                     this._htmlAnchorElementPrototype = new ESHTMLAnchorElementPrototype(this._elementPrototype);
                     this._htmlTableElementPrototype = new ESHTMLTableElementPrototype(this._elementPrototype);
                     this._htmlTableSectionElementPrototype = new ESHTMLTableSectionElementPrototype(this._elementPrototype);
                     this._htmlTableRowElementPrototype = new ESHTMLTableRowElementPrototype(this._elementPrototype);
                     this._optionPrototype = new ESHTMLOptionElementPrototype(this._elementPrototype);
                     this._htmlImagePrototype = new ESHTMLImageElementPrototype(this._elementPrototype);
                     this._selectPrototype = new ESHTMLSelectElementPrototype(this._elementPrototype);
                     this._windowPrototype = new ESWindowPrototype();
                     this._inputPrototype = new ESHTMLInputElementPrototype(this._elementPrototype);
                     this._htmlTextAreaPrototype = new ESHTMLTextAreaPrototype(this._elementPrototype);
                     this._documentPrototype = new ESHTMLDocumentPrototype();
                     this._formPrototype = new ESHTMLFormElementPrototype(this._elementPrototype);
                     this._historyPrototype = new ESHistoryPrototype();
                     this._xmlHttpRequestPrototype = new ESXMLHttpRequestPrototype();
                     this._domExceptionPrototype = new ESDOMExceptionPrototype();
                     this._namedNodeMapPrototype = new ESNamedNodeMapPrototype();
                     this._characterDataPrototype = new ESCharacterDataPrototype(this._nodePrototype);
                     this._nodeListPrototype = new ESNodeListPrototype();
                     this._blackberry = new ESBlackberry(this);
                     this._window = new ESWindow(this, doc, this._globalObject, renderingSession, frame);
                     var7 = false;
                     break label108;
                  }

                  var7 = false;
               } else {
                  var7 = false;
               }
            } finally {
               if (var7) {
                  this.clearInstance();
                  if (TimeLogger._loggingEnabled) {
                     TimeLogger.getInstance().stopTimer(9, doc.hashCode());
                  }
               }
            }

            this.clearInstance();
            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().stopTimer(9, doc.hashCode());
            }

            return;
         }

         this.clearInstance();
         if (TimeLogger._loggingEnabled) {
            TimeLogger.getInstance().stopTimer(9, doc.hashCode());
            return;
         }

         return;
      }

      this.clearInstance();
      if (TimeLogger._loggingEnabled) {
         TimeLogger.getInstance().stopTimer(9, doc.hashCode());
      }
   }

   private final void setInstance() {
      ThreadSpecificData.set(Thread.currentThread(), 2, this);
   }

   private final void setWatchdog() {
      if (this._watchDogTimeout > 0) {
         this._watchDogId = Application.getApplication().invokeLater(this._watchDog, this._watchDogTimeout, false);
      }
   }

   public static final long makeStringValue(Object str) {
      return !(str instanceof Object) ? Value.makeStringValue("") : Value.makeStringValue((String)str);
   }

   private final void handleQuincy(Throwable t) {
      if (JavaScriptRegistry._sendQuincy && !this._quicySent) {
         RIMGlobalMessagePoster.postGlobalEvent(-2269441167196113981L, 0, 0, t, null);
         this._quicySent = true;
      }
   }

   private final void handleQuincy() {
      if (JavaScriptRegistry._sendQuincy && !this._quicySent) {
         RIMGlobalMessagePoster.postGlobalEvent(2610733293414932871L, 0, 0, "Browser:Javascript", null);
         this._quicySent = true;
      }
   }

   private final boolean toReturnValue(long result) {
      switch (Value.getType(result)) {
         case 4:
            return Value.getBooleanValue(result);
         default:
            return true;
      }
   }

   private static final int isScriptVerified(byte[] hash) {
      int key = (hash[0] & 255) << 24 | (hash[1] & 255) << 16 | (hash[2] & 255) << 8 | hash[3] & 255;
      synchronized (_verifiedScriptsCache) {
         for (int i = 0; i < _cacheSize; i++) {
            if (_verifiedScriptsCache[i] == key && Arrays.equals((byte[])_verifiedScriptsHash[i], hash)) {
               return i;
            }
         }

         return -1;
      }
   }

   private static final void setScriptVerified(byte[] hash) {
      int key = (hash[0] & 255) << 24 | (hash[1] & 255) << 16 | (hash[2] & 255) << 8 | hash[3] & 255;
      synchronized (_verifiedScriptsCache) {
         if (_cacheSize < 50) {
            _cacheSize++;
         }

         int index = _cacheSize - 1;
         _verifiedScriptsCache[index] = key;
         _verifiedScriptsHash[index] = (byte[][])hash;
         moveToFront(index);
      }
   }

   private static final void moveToFront(int index) {
      if (index != 0) {
         int key = _verifiedScriptsCache[index];
         byte[] hash = (byte[])_verifiedScriptsHash[index];
         System.arraycopy(_verifiedScriptsCache, 0, _verifiedScriptsCache, 1, index);
         System.arraycopy(_verifiedScriptsHash, 0, _verifiedScriptsHash, 1, index);
         _verifiedScriptsCache[0] = key;
         _verifiedScriptsHash[0] = (byte[][])hash;
      }
   }

   private final void clearInstance() {
      ThreadSpecificData.set(Thread.currentThread(), 2, null);
      if (this._watchDogId != -1) {
         Application.getApplication().cancelInvokeLater(this._watchDogId);
         this._watchDogId = -1;
      }
   }

   static final JavaScriptEngine getInstance() {
      return (JavaScriptEngine)ThreadSpecificData.get(Thread.currentThread(), 2);
   }
}
