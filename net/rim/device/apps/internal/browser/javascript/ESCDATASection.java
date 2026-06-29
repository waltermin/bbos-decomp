package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.CDATASection;

final class ESCDATASection extends ESText {
   ESCDATASection(CDATASection cDataSection) {
      super(cDataSection, Names.CDATASection);
   }
}
