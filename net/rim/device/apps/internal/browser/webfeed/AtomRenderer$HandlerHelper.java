package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.browser.field.BrowserContentChangedEvent;
import net.rim.device.apps.api.utility.general.URI;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

final class AtomRenderer$HandlerHelper extends DefaultHandler {
   private final AtomRenderer this$0;

   private AtomRenderer$HandlerHelper(AtomRenderer _1) {
      this.this$0 = _1;
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      int topId = this.this$0.top();
      if (topId != 8 && topId != 4) {
         switch (this.this$0.getTag(localName)) {
            case 0:
               break;
            case 1:
            default:
               this.this$0._currentItem = new WebFeedItem();
               if (this.this$0._contentRead != null) {
                  this.this$0._contentRead.setItemsRead(this.this$0._pipePosition._numRead);
                  AtomRenderer.access$700(this.this$0).eventOccurred(this.this$0._contentRead);
               }

               String baseUrl = attributes.getValue("xml:base");
               if (baseUrl == null) {
                  baseUrl = this.this$0._browserContent.getURL();
               } else {
                  baseUrl = URI.getAbsoluteURL(baseUrl, this.this$0._browserContent.getURL());
               }

               this.this$0._currentItem.setBaseUrl(baseUrl);
               break;
            case 2:
               if (topId == 1) {
                  this.this$0._append = true;
               } else if (topId == 0) {
                  this.this$0._append = true;
               }
               break;
            case 3:
               if (topId == 1) {
                  String href = attributes.getValue("href");
                  if (this.this$0._currentItem != null && href != null) {
                     String rel = attributes.getValue("rel");
                     if (rel != null && rel.equals("enclosure")) {
                        String type = attributes.getValue("type");
                        if (type != null) {
                           String sizeStr = attributes.getValue("length");
                           if (sizeStr != null) {
                              label104:
                              try {
                                 int size = Integer.parseInt(sizeStr);
                                 if (size > 0) {
                                    this.this$0._currentItem.setEnclosure(href, size, type);
                                 }
                              } finally {
                                 break label104;
                              }
                           }
                        }
                     } else {
                        this.this$0._currentItem.setLink(href);
                     }
                  }
               }
               break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
               if (topId == 1) {
                  this.this$0._append = true;
               }
         }

         this.this$0._itemStack.push(localName);
      } else {
         this.this$0._buffer.append('<');
         this.this$0._buffer.append(localName);
         int numAttributes = attributes.getLength();

         for (int i = 0; i < numAttributes; i++) {
            this.this$0._buffer.append(' ');
            this.this$0._buffer.append(attributes.getLocalName(i));
            this.this$0._buffer.append('=');
            this.this$0._buffer.append('"');
            StringBuffer buffer = new StringBuffer(attributes.getValue(i));

            for (int j = 0; j < buffer.length(); j++) {
               if (buffer.charAt(j) == '"') {
                  buffer.insert(j, '\\');
                  j++;
               }
            }

            this.this$0._buffer.append(buffer.toString());
            this.this$0._buffer.append('"');
         }

         this.this$0._buffer.append('>');
      }
   }

   @Override
   public final void ignorableWhitespace(char[] ch, int start, int length) {
      int topId = this.this$0.top();
      if (this.this$0._append && (topId == 8 || topId == 4)) {
         this.this$0._buffer.append(ch, start, length);
      }
   }

   @Override
   public final void characters(char[] ch, int start, int length) {
      if (this.this$0._append) {
         this.this$0._buffer.append(ch, start, length);
      }
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
      int tagId = this.this$0.getTag(localName);
      int topId = this.this$0.top();
      if (tagId == 8 || tagId == 4 || topId != 8 && topId != 4) {
         this.this$0._itemStack.pop();
         switch (tagId) {
            case 0:
            case 3:
               break;
            case 1:
            default:
               if (this.this$0._currentItem != null) {
                  this.this$0._webFeedField.addItem(this.this$0._currentItem);
                  this.this$0._currentItem = null;
                  return;
               }
               break;
            case 2:
               int top = this.this$0.top();
               if (top == 1) {
                  if (this.this$0._currentItem != null) {
                     String title = this.this$0.getCurrentString();
                     this.this$0._charEntities.parse(title.toCharArray(), 0, title.length());
                     this.this$0._currentItem.setTitle(this.this$0._charEntities.getResult());
                  }

                  this.this$0._append = false;
                  return;
               }

               if (top == 0) {
                  String title = this.this$0.getCurrentString();
                  this.this$0._browserContent.setTitle(title);
                  if (AtomRenderer.access$1400(this.this$0) != null) {
                     BrowserContentChangedEvent event = new BrowserContentChangedEvent(this.this$0._browserContent);
                     AtomRenderer.access$1500(this.this$0).eventOccurred(event);
                  }

                  this.this$0._append = false;
                  return;
               }
               break;
            case 4:
               if (this.this$0.top() == 1) {
                  this.this$0._append = false;
                  if (this.this$0._currentItem != null) {
                     this.this$0._currentItem.setDescription(this.this$0.getCurrentString());
                     return;
                  }
               }
               break;
            case 5:
            case 7:
               if (this.this$0.top() == 1) {
                  this.this$0._append = false;
                  if (this.this$0._currentItem != null) {
                     this.this$0._currentItem.setPubDate(this.this$0.getCurrentString());
                     return;
                  }
               }
               break;
            case 6:
               if (this.this$0.top() == 1) {
                  this.this$0._append = false;
                  if (this.this$0._currentItem != null) {
                     this.this$0._currentItem.setGuid(this.this$0.getCurrentString());
                  }
               }
               break;
            case 8:
               if (this.this$0.top() == 1) {
                  this.this$0._append = false;
                  if (this.this$0._currentItem != null) {
                     String summary = this.this$0.getCurrentString();
                     if (this.this$0._currentItem.getDescription() == null) {
                        this.this$0._currentItem.setDescription(summary);
                        return;
                     }
                  }
               }
         }
      } else {
         this.this$0._buffer.append('<');
         this.this$0._buffer.append('/');
         this.this$0._buffer.append(localName);
         this.this$0._buffer.append('>');
      }
   }

   AtomRenderer$HandlerHelper(AtomRenderer x0, AtomRenderer$1 x1) {
      this(x0);
   }
}
