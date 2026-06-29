package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.DocumentFragment;

final class ESDocumentFragment extends ESNode {
   ESDocumentFragment(DocumentFragment fragment) {
      super(fragment, Names.DocumentFragment);
   }
}
