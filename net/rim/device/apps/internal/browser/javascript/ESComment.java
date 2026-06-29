package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.Comment;

final class ESComment extends ESCharacterData {
   ESComment(Comment comment) {
      super(comment, Names.Comment);
   }
}
