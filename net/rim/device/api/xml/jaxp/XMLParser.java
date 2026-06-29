package net.rim.device.api.xml.jaxp;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.xml.parsers.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends SAXParser implements Locator, XMLReader {
   XMLParser$InputReader _in;
   private DefaultHandler _defaultHandler = (DefaultHandler)(new Object());
   private ContentHandler _contentHandler = this._defaultHandler;
   private DTDHandler _dtdHandler = this._defaultHandler;
   private ErrorHandler _errorHandler = this._defaultHandler;
   private EntityResolver _entityResolver = this._defaultHandler;
   private RIMExtendedHandler _extHandler;
   private Hashtable _internalEntities;
   private Hashtable _entitiesDeclaredInExternalSubset;
   private Hashtable _externalEntities;
   private Hashtable _externalParameterEntities;
   private Hashtable _parameterEntities;
   private Hashtable _nameSpaceLookup;
   private Hashtable _attributeDeclarations;
   private String _defaultURI = "";
   private String _undefinedURI = (String)(new Object());
   private boolean _allowUndefinedNamespaces;
   private boolean _isNamespaceAware = true;
   private String XMLNS = "xmlns";
   private boolean _inDTD;
   private boolean _inEntityValue;
   private boolean _inAttValue;
   private boolean _processPERefs;
   private boolean _inInternalSubset;
   private StringBuffer _dtdBody;
   private boolean _isDeclSep;
   private boolean _standalone;
   private boolean _foundToBeStandalone;
   private boolean _parsing;
   private XMLParser$InputReader _tokenStartInput;
   private XMLParser$InputReader _lastReadInput;
   private boolean _EOFFatal = true;
   private char[][] _nameBuffers;
   private int[] _nameLengths;
   private int[] _firstNonSpaces;
   private int[] _lastNonSpaces;
   private int _nameStackIndex;
   private char[] _nameBuffer;
   private int _nameLength;
   private int _firstNonSpace;
   private int _lastNonSpace;
   private boolean _trimLeadingSpace = true;
   private String _prefixName;
   private String _localName;
   private String _qName;
   private String _uri;
   private String _systemId;
   private String _publicId;
   private int _pushedBackFirstChar = -1;
   private int _tokenType;
   private String[] _kwTable = new String[]{
      "ID",
      "no",
      "1.0",
      "ANY",
      "yes",
      "xml",
      "CDATA",
      "EMPTY",
      "FIXED",
      "IDREF",
      "NDATA",
      "xmlns",
      "ENTITY",
      "IDREFS",
      "IGNORE",
      "PCDATA",
      "PUBLIC",
      "SYSTEM",
      "version",
      "ATTLIST",
      "DOCTYPE",
      "ELEMENT",
      "IMPLIED",
      "INCLUDE",
      "NMTOKEN",
      "encoding",
      "ENTITIES",
      "NMTOKENS",
      "NOTATION",
      "REQUIRED",
      "standalone"
   };
   final SAXAttributesImpl _nullAttributes = new SAXAttributesImpl();
   private Hashtable _duplicateAttributeCheck = (Hashtable)(new Object());
   private boolean _havePeek;
   private int _peek;
   static String XMLURL = "http://www.w3.org/XML/1998/namespace";
   static String XML = "xml";
   private static final int NAME_BUFFER_INITIAL_SIZE = 64;
   private static final int NAME_BUFFER_GROWTH_INCREMENT = 64;
   private static final int NAMESTACK_INITIAL_SIZE = 64;
   private static final int NAMESTACK_GROWTH_INCREMENT = 64;
   private static final int TOKEN_SPACE = 1;
   private static final int TOKEN_COMMENT = 2;
   private static final int TOKEN_PI = 3;
   private static final int TOKEN_START_TAG = 4;
   private static final int TOKEN_END_TAG = 5;
   private static final int TOKEN_DTD = 7;
   private static final int TOKEN_EOF = 8;
   private static final int TOKEN_UNKNOWN = 9;
   private static final int TOKEN_INCLUDE = 10;
   private static final int TOKEN_IGNORE = 11;
   private static final int TOKEN_CDSECT = 12;
   private static final int KEYWORD_ID = 0;
   private static final int KEYWORD_no = 1;
   private static final int KEYWORD_1p0 = 2;
   private static final int KEYWORD_ANY = 3;
   private static final int KEYWORD_yes = 4;
   private static final int KEYWORD_xml = 5;
   private static final int KEYWORD_CDATA = 6;
   private static final int KEYWORD_EMPTY = 7;
   private static final int KEYWORD_FIXED = 8;
   private static final int KEYWORD_IDREF = 9;
   private static final int KEYWORD_NDATA = 10;
   private static final int KEYWORD_xmlns = 11;
   private static final int KEYWORD_ENTITY = 12;
   private static final int KEYWORD_IDREFS = 13;
   private static final int KEYWORD_IGNORE = 14;
   private static final int KEYWORD_PCDATA = 15;
   private static final int KEYWORD_PUBLIC = 16;
   private static final int KEYWORD_SYSTEM = 17;
   private static final int KEYWORD_version = 18;
   private static final int KEYWORD_ATTLIST = 19;
   private static final int KEYWORD_DOCTYPE = 20;
   private static final int KEYWORD_ELEMENT = 21;
   private static final int KEYWORD_IMPLIED = 22;
   private static final int KEYWORD_INCLUDE = 23;
   private static final int KEYWORD_NMTOKEN = 24;
   private static final int KEYWORD_encoding = 25;
   private static final int KEYWORD_ENTITIES = 26;
   private static final int KEYWORD_NMTOKENS = 27;
   private static final int KEYWORD_NOTATION = 28;
   private static final int KEYWORD_REQUIRED = 29;
   private static final int KEYWORD_standalone = 30;
   private static final String ERROR_RECURSIVE_ENTITY = "Entity '%1' may not reference itself, either directly or indirectly.";
   private static final String ERROR_NO_LT_IN_ATTVALUE = "An attribute value may not contain '<'.";
   private static final String ERROR_EXTERNAL_REF_IN_ATTVALUE = "An external entity reference is not allowed in an attribute value.";
   private static final String ERROR_CANT_FIND_DTD = "Cannot resolve external DTD subset '%1'.";
   private static final String ERROR_CANT_RESOLVE = "Cannot resolve '%1'.";
   private static final String ERROR_BAD_END_CDATA = "Character data may not contain ']]>'.";
   private static final String ERROR_AINT_STANDALONE = "A standalone document may not reference entities declared in the external DTD subset.";
   private static final String ERROR_DUPLICATE_ATTRIBUTE = "Duplicate attribute '%1'.";
   private static final String ERROR_NEED_ATTVALUE = "Expecting an attribute value.";
   private static final String ERROR_NEED_ENTITY_VALUE = "Expecting an entity replacement value.";
   private static final String ERROR_NEED = "Expecting '%1'.";
   private static final String ERROR_NEED_NAME = "Expecting a name.";
   private static final String ERROR_NEED_ELEMENT = "Expecting an element.";
   private static final String ERROR_NEED_ENCODING = "Expecting an encoding name.";
   private static final String ERROR_NEED_EOF = "Expecting end of file.";
   private static final String ERROR_NEED_PUBID = "Expecting a public identifer.";
   private static final String ERROR_NEED_SYSID = "Expecting a system identifier.";
   private static final String ERROR_NEED_SPACE = "Expecting white space.";
   private static final String ERROR_NEED_EXT_OR_PUBID = "Expecting an external or public identifier.";
   private static final String ERROR_BAD_CHAR = "Invalid character '%1' encountered.";
   private static final String ERROR_BAD_CHARREF = "Invalid character reference '%1'.";
   private static final String ERROR_PE_NOT_HERE = "A parameter entity reference may only appear between markup in the internal DTD subset.";
   private static final String ERROR_PI_NOT_XML = "Processing instruction target may not be 'XML'.";
   private static final String ERROR_BAD_URI = "Invalid URI '%1'.";
   private static final String ERROR_NEED_ANY_OR_EMPTY = "Expecting 'EMPTY' or 'ANY'.";
   private static final String ERROR_NEED_ATTTYPE = "Expecting an attribute type.";
   private static final String ERROR_NEED_ATTVALUEDECL = "Expecting a default attribute value declaration.";
   private static final String ERROR_NEED_MARKUPDECL = "Expecting a markup declaration.";
   private static final String ERROR_NEED_MARKUP_OR_END = "Expecting '<' or ']'.";
   private static final String ERROR_NEED_NMTOKEN = "Expecting a name token.";
   private static final String ERROR_NEED_NUMBER = "Expecting a number.";
   private static final String ERROR_BAD_UTF8 = "Malformed UTF-8.";
   private static final String ERROR_BAD_XMLDECL = "Invalid xml declaration.";
   private static final String ERROR_TAG_MISMATCH = "End tag does not match start tag '%1'.";
   private static final String ERROR_BAD_NESTING = "Parsing unit must begin and end in the same entity.";
   private static final String ERROR_UNDEEFINED_NAMESPACE = "Undefined namespace '%1'.";
   private static final String ERROR_UNDEFINED_ENTITY = "Undefined entity reference '%1'.";
   private static final String ERROR_UNDEFINED_NAMESPACE_PREFIX = "Undefined namespace prefix '%1'.";
   private static final String ERROR_UNDEFINED_PE = "Undefined parameter entity reference.";
   private static final String ERROR_UNEXPECTED_EOF = "Unexpected end of file.";
   private static final String ERROR_UNRECOGNIZED_MARKUP = "Unrecognized markup.";
   private static final String ERROR_CANT_DECODE = "Unable to determine document encoding.";
   private static final String ERROR_BAD_VERSION = "XML version must be 1.0.";
   static final int LETTER = 0;
   static final int DIGIT = 1;
   static final int COMBINING = 2;
   static final int EXTENDER = 3;
   static final int UNKNOWN = 4;
   private static final char[] _letters = new char[]{
      '0',
      '9',
      '\u0001',
      'A',
      'Z',
      '\u0000',
      'a',
      'z',
      '\u0000',
      '·',
      '·',
      '\u0003',
      'À',
      'Ö',
      '\u0000',
      'Ø',
      'ö',
      '\u0000',
      'ø',
      'ÿ',
      '\u0000',
      'Ā',
      'ı',
      '\u0000',
      'Ĵ',
      'ľ',
      '\u0000',
      'Ł',
      'ň',
      '\u0000',
      'Ŋ',
      'ž',
      '\u0000',
      'ƀ',
      'ǃ',
      '\u0000',
      'Ǎ',
      'ǰ',
      '\u0000',
      'Ǵ',
      'ǵ',
      '\u0000',
      'Ǻ',
      'ȗ',
      '\u0000',
      'ɐ',
      'ʨ',
      '\u0000',
      'ʻ',
      'ˁ',
      '\u0000',
      'ː',
      'ː',
      '\u0003',
      'ˑ',
      'ˑ',
      '\u0003',
      '̀',
      'ͅ',
      '\u0002',
      '͠',
      '͡',
      '\u0002',
      'Ά',
      'Ά',
      '\u0000',
      '·',
      '·',
      '\u0003',
      'Έ',
      'Ί',
      '\u0000',
      'Ό',
      'Ό',
      '\u0000',
      'Ύ',
      'Ρ',
      '\u0000',
      'Σ',
      'ώ',
      '\u0000',
      'ϐ',
      'ϖ',
      '\u0000',
      'Ϛ',
      'Ϛ',
      '\u0000',
      'Ϝ',
      'Ϝ',
      '\u0000',
      'Ϟ',
      'Ϟ',
      '\u0000',
      'Ϡ',
      'Ϡ',
      '\u0000',
      'Ϣ',
      'ϳ',
      '\u0000',
      'Ё',
      'Ќ',
      '\u0000',
      'Ў',
      'я',
      '\u0000',
      'ё',
      'ќ',
      '\u0000',
      'ў',
      'ҁ',
      '\u0000',
      '҃',
      '҆',
      '\u0002',
      'Ґ',
      'ӄ',
      '\u0000',
      'Ӈ',
      'ӈ',
      '\u0000',
      'Ӌ',
      'ӌ',
      '\u0000',
      'Ӑ',
      'ӫ',
      '\u0000',
      'Ӯ',
      'ӵ',
      '\u0000',
      'Ӹ',
      'ӹ',
      '\u0000',
      'Ա',
      'Ֆ',
      '\u0000',
      'ՙ',
      'ՙ',
      '\u0000',
      'ա',
      'ֆ',
      '\u0000',
      '֑',
      '֡',
      '\u0002',
      '֣',
      'ֹ',
      '\u0002',
      'ֻ',
      'ֽ',
      '\u0002',
      'ֿ',
      'ֿ',
      '\u0002',
      'ׁ',
      'ׂ',
      '\u0002',
      'ׄ',
      'ׄ',
      '\u0002',
      'א',
      'ת',
      '\u0000',
      'װ',
      'ײ',
      '\u0000',
      'ء',
      'غ',
      '\u0000',
      'ـ',
      'ـ',
      '\u0003',
      'ف',
      'ي',
      '\u0000',
      'ً',
      'ْ',
      '\u0002',
      '٠',
      '٩',
      '\u0001',
      'ٰ',
      'ٰ',
      '\u0002',
      'ٱ',
      'ڷ',
      '\u0000',
      'ں',
      'ھ',
      '\u0000',
      'ۀ',
      'ێ',
      '\u0000',
      'ې',
      'ۓ',
      '\u0000',
      'ە',
      'ە',
      '\u0000',
      'ۖ',
      'ۜ',
      '\u0002',
      '\u06dd',
      '۟',
      '\u0002',
      '۠',
      'ۤ',
      '\u0002',
      'ۥ',
      'ۦ',
      '\u0000',
      'ۧ',
      'ۨ',
      '\u0002',
      '۪',
      'ۭ',
      '\u0002',
      '۰',
      '۹',
      '\u0001',
      'ँ',
      'ः',
      '\u0002',
      'अ',
      'ह',
      '\u0000',
      '़',
      '़',
      '\u0002',
      'ऽ',
      'ऽ',
      '\u0000',
      'ा',
      'ौ',
      '\u0002',
      '्',
      '्',
      '\u0002',
      '॑',
      '॔',
      '\u0002',
      'क़',
      'ॡ',
      '\u0000',
      'ॢ',
      'ॣ',
      '\u0002',
      '०',
      '९',
      '\u0001',
      'ঁ',
      'ঃ',
      '\u0002',
      'অ',
      'ঌ',
      '\u0000',
      'এ',
      'ঐ',
      '\u0000',
      'ও',
      'ন',
      '\u0000',
      'প',
      'র',
      '\u0000',
      'ল',
      'ল',
      '\u0000',
      'শ',
      'হ',
      '\u0000',
      '়',
      '়',
      '\u0002',
      'া',
      'া',
      '\u0002',
      'ি',
      'ি',
      '\u0002',
      'ী',
      'ৄ',
      '\u0002',
      'ে',
      'ৈ',
      '\u0002',
      'ো',
      '্',
      '\u0002',
      'ৗ',
      'ৗ',
      '\u0002',
      'ড়',
      'ঢ়',
      '\u0000',
      'য়',
      'ৡ',
      '\u0000',
      'ৢ',
      'ৣ',
      '\u0002',
      '০',
      '৯',
      '\u0001',
      'ৰ',
      'ৱ',
      '\u0000',
      'ਂ',
      'ਂ',
      '\u0002',
      'ਅ',
      'ਊ',
      '\u0000',
      'ਏ',
      'ਐ',
      '\u0000',
      'ਓ',
      'ਨ',
      '\u0000',
      'ਪ',
      'ਰ',
      '\u0000',
      'ਲ',
      'ਲ਼',
      '\u0000',
      'ਵ',
      'ਸ਼',
      '\u0000',
      'ਸ',
      'ਹ',
      '\u0000',
      '਼',
      '਼',
      '\u0002',
      'ਾ',
      'ਾ',
      '\u0002',
      'ਿ',
      'ਿ',
      '\u0002',
      'ੀ',
      'ੂ',
      '\u0002',
      'ੇ',
      'ੈ',
      '\u0002',
      'ੋ',
      '੍',
      '\u0002',
      'ਖ਼',
      'ੜ',
      '\u0000',
      'ਫ਼',
      'ਫ਼',
      '\u0000',
      '੦',
      '੯',
      '\u0001',
      'ੰ',
      'ੱ',
      '\u0002',
      'ੲ',
      'ੴ',
      '\u0000',
      'ઁ',
      'ઃ',
      '\u0002',
      'અ',
      'ઋ',
      '\u0000',
      'ઍ',
      'ઍ',
      '\u0000',
      'એ',
      'ઑ',
      '\u0000',
      'ઓ',
      'ન',
      '\u0000',
      'પ',
      'ર',
      '\u0000',
      'લ',
      'ળ',
      '\u0000',
      'વ',
      'હ',
      '\u0000',
      '઼',
      '઼',
      '\u0002',
      'ઽ',
      'ઽ',
      '\u0000',
      'ા',
      'ૅ',
      '\u0002',
      'ે',
      'ૉ',
      '\u0002',
      'ો',
      '્',
      '\u0002',
      'ૠ',
      'ૠ',
      '\u0000',
      '૦',
      '૯',
      '\u0001',
      'ଁ',
      'ଃ',
      '\u0002',
      'ଅ',
      'ଌ',
      '\u0000',
      'ଏ',
      'ଐ',
      '\u0000',
      'ଓ',
      'ନ',
      '\u0000',
      'ପ',
      'ର',
      '\u0000',
      'ଲ',
      'ଳ',
      '\u0000',
      'ଶ',
      'ହ',
      '\u0000',
      '଼',
      '଼',
      '\u0002',
      'ଽ',
      'ଽ',
      '\u0000',
      'ା',
      'ୃ',
      '\u0002',
      'େ',
      'ୈ',
      '\u0002',
      'ୋ',
      '୍',
      '\u0002',
      'ୖ',
      'ୗ',
      '\u0002',
      'ଡ଼',
      'ଢ଼',
      '\u0000',
      'ୟ',
      'ୡ',
      '\u0000',
      '୦',
      '୯',
      '\u0001',
      'ஂ',
      'ஃ',
      '\u0002',
      'அ',
      'ஊ',
      '\u0000',
      'எ',
      'ஐ',
      '\u0000',
      'ஒ',
      'க',
      '\u0000',
      'ங',
      'ச',
      '\u0000',
      'ஜ',
      'ஜ',
      '\u0000',
      'ஞ',
      'ட',
      '\u0000',
      'ண',
      'த',
      '\u0000',
      'ந',
      'ப',
      '\u0000',
      'ம',
      'வ',
      '\u0000',
      'ஷ',
      'ஹ',
      '\u0000',
      'ா',
      'ூ',
      '\u0002',
      'ெ',
      'ை',
      '\u0002',
      'ொ',
      '்',
      '\u0002',
      'ௗ',
      'ௗ',
      '\u0002',
      '௧',
      '௯',
      '\u0001',
      'ఁ',
      'ః',
      '\u0002',
      'అ',
      'ఌ',
      '\u0000',
      'ఎ',
      'ఐ',
      '\u0000',
      'ఒ',
      'న',
      '\u0000',
      'ప',
      'ళ',
      '\u0000',
      'వ',
      'హ',
      '\u0000',
      'ా',
      'ౄ',
      '\u0002',
      'ె',
      'ై',
      '\u0002',
      'ొ',
      '్',
      '\u0002',
      'ౕ',
      'ౖ',
      '\u0002',
      'ౠ',
      'ౡ',
      '\u0000',
      '౦',
      '౯',
      '\u0001',
      'ಂ',
      'ಃ',
      '\u0002',
      'ಅ',
      'ಌ',
      '\u0000',
      'ಎ',
      'ಐ',
      '\u0000',
      'ಒ',
      'ನ',
      '\u0000',
      'ಪ',
      'ಳ',
      '\u0000',
      'ವ',
      'ಹ',
      '\u0000',
      'ಾ',
      'ೄ',
      '\u0002',
      'ೆ',
      'ೈ',
      '\u0002',
      'ೊ',
      '್',
      '\u0002',
      'ೕ',
      'ೖ',
      '\u0002',
      'ೞ',
      'ೞ',
      '\u0000',
      'ೠ',
      'ೡ',
      '\u0000',
      '೦',
      '೯',
      '\u0001',
      'ം',
      'ഃ',
      '\u0002',
      'അ',
      'ഌ',
      '\u0000',
      'എ',
      'ഐ',
      '\u0000',
      'ഒ',
      'ന',
      '\u0000',
      'പ',
      'ഹ',
      '\u0000',
      'ാ',
      'ൃ',
      '\u0002',
      'െ',
      'ൈ',
      '\u0002',
      'ൊ',
      '്',
      '\u0002',
      'ൗ',
      'ൗ',
      '\u0002',
      'ൠ',
      'ൡ',
      '\u0000',
      '൦',
      '൯',
      '\u0001',
      'ก',
      'ฮ',
      '\u0000',
      'ะ',
      'ะ',
      '\u0000',
      'ั',
      'ั',
      '\u0002',
      'า',
      'ำ',
      '\u0000',
      'ิ',
      'ฺ',
      '\u0002',
      'เ',
      'ๅ',
      '\u0000',
      'ๆ',
      'ๆ',
      '\u0003',
      '็',
      '๎',
      '\u0002',
      '๐',
      '๙',
      '\u0001',
      'ກ',
      'ຂ',
      '\u0000',
      'ຄ',
      'ຄ',
      '\u0000',
      'ງ',
      'ຈ',
      '\u0000',
      'ຊ',
      'ຊ',
      '\u0000',
      'ຍ',
      'ຍ',
      '\u0000',
      'ດ',
      'ທ',
      '\u0000',
      'ນ',
      'ຟ',
      '\u0000',
      'ມ',
      'ຣ',
      '\u0000',
      'ລ',
      'ລ',
      '\u0000',
      'ວ',
      'ວ',
      '\u0000',
      'ສ',
      'ຫ',
      '\u0000',
      'ອ',
      'ຮ',
      '\u0000',
      'ະ',
      'ະ',
      '\u0000',
      'ັ',
      'ັ',
      '\u0002',
      'າ',
      'ຳ',
      '\u0000',
      'ິ',
      'ູ',
      '\u0002',
      'ົ',
      'ຼ',
      '\u0002',
      'ຽ',
      'ຽ',
      '\u0000',
      'ເ',
      'ໄ',
      '\u0000',
      'ໆ',
      'ໆ',
      '\u0003',
      '່',
      'ໍ',
      '\u0002',
      '໐',
      '໙',
      '\u0001',
      '༘',
      '༙',
      '\u0002',
      '༠',
      '༩',
      '\u0001',
      '༵',
      '༵',
      '\u0002',
      '༷',
      '༷',
      '\u0002',
      '༹',
      '༹',
      '\u0002',
      '༾',
      '༾',
      '\u0002',
      '༿',
      '༿',
      '\u0002',
      'ཀ',
      'ཇ',
      '\u0000',
      'ཉ',
      'ཀྵ',
      '\u0000',
      'ཱ',
      '྄',
      '\u0002',
      '྆',
      'ྋ',
      '\u0002',
      'ྐ',
      'ྕ',
      '\u0002',
      'ྗ',
      'ྗ',
      '\u0002',
      'ྙ',
      'ྭ',
      '\u0002',
      'ྱ',
      'ྷ',
      '\u0002',
      'ྐྵ',
      'ྐྵ',
      '\u0002',
      'Ⴀ',
      'Ⴥ',
      '\u0000',
      'ა',
      'ჶ',
      '\u0000',
      'ᄀ',
      'ᄀ',
      '\u0000',
      'ᄂ',
      'ᄃ',
      '\u0000',
      'ᄅ',
      'ᄇ',
      '\u0000',
      'ᄉ',
      'ᄉ',
      '\u0000',
      'ᄋ',
      'ᄌ',
      '\u0000',
      'ᄎ',
      'ᄒ',
      '\u0000',
      'ᄼ',
      'ᄼ',
      '\u0000',
      'ᄾ',
      'ᄾ',
      '\u0000',
      'ᅀ',
      'ᅀ',
      '\u0000',
      'ᅌ',
      'ᅌ',
      '\u0000',
      'ᅎ',
      'ᅎ',
      '\u0000',
      'ᅐ',
      'ᅐ',
      '\u0000',
      'ᅔ',
      'ᅕ',
      '\u0000',
      'ᅙ',
      'ᅙ',
      '\u0000',
      'ᅟ',
      'ᅡ',
      '\u0000',
      'ᅣ',
      'ᅣ',
      '\u0000',
      'ᅥ',
      'ᅥ',
      '\u0000',
      'ᅧ',
      'ᅧ',
      '\u0000',
      'ᅩ',
      'ᅩ',
      '\u0000',
      'ᅭ',
      'ᅮ',
      '\u0000',
      'ᅲ',
      'ᅳ',
      '\u0000',
      'ᅵ',
      'ᅵ',
      '\u0000',
      'ᆞ',
      'ᆞ',
      '\u0000',
      'ᆨ',
      'ᆨ',
      '\u0000',
      'ᆫ',
      'ᆫ',
      '\u0000',
      'ᆮ',
      'ᆯ',
      '\u0000',
      'ᆷ',
      'ᆸ',
      '\u0000',
      'ᆺ',
      'ᆺ',
      '\u0000',
      'ᆼ',
      'ᇂ',
      '\u0000',
      'ᇫ',
      'ᇫ',
      '\u0000',
      'ᇰ',
      'ᇰ',
      '\u0000',
      'ᇹ',
      'ᇹ',
      '\u0000',
      'Ḁ',
      'ẛ',
      '\u0000',
      'Ạ',
      'ỹ',
      '\u0000',
      'ἀ',
      'ἕ',
      '\u0000',
      'Ἐ',
      'Ἕ',
      '\u0000',
      'ἠ',
      'ὅ',
      '\u0000',
      'Ὀ',
      'Ὅ',
      '\u0000',
      'ὐ',
      'ὗ',
      '\u0000',
      'Ὑ',
      'Ὑ',
      '\u0000',
      'Ὓ',
      'Ὓ',
      '\u0000',
      'Ὕ',
      'Ὕ',
      '\u0000',
      'Ὗ',
      'ώ',
      '\u0000',
      'ᾀ',
      'ᾴ',
      '\u0000',
      'ᾶ',
      'ᾼ',
      '\u0000',
      'ι',
      'ι',
      '\u0000',
      'ῂ',
      'ῄ',
      '\u0000',
      'ῆ',
      'ῌ',
      '\u0000',
      'ῐ',
      'ΐ',
      '\u0000',
      'ῖ',
      'Ί',
      '\u0000',
      'ῠ',
      'Ῥ',
      '\u0000',
      'ῲ',
      'ῴ',
      '\u0000',
      'ῶ',
      'ῼ',
      '\u0000',
      '⃐',
      '⃜',
      '\u0002',
      '⃡',
      '⃡',
      '\u0002',
      'Ω',
      'Ω',
      '\u0000',
      'K',
      'Å',
      '\u0000',
      '℮',
      '℮',
      '\u0000',
      'ↀ',
      'ↂ',
      '\u0000',
      '々',
      '々',
      '\u0003',
      '〇',
      '〇',
      '\u0000',
      '〡',
      '〩',
      '\u0000',
      '〪',
      '〯',
      '\u0002',
      '〱',
      '〵',
      '\u0003',
      'ぁ',
      'ゔ',
      '\u0000',
      '゙',
      '゙',
      '\u0002',
      '゚',
      '゚',
      '\u0002',
      'ゝ',
      'ゞ',
      '\u0003',
      'ァ',
      'ヺ',
      '\u0000',
      'ー',
      'ヾ',
      '\u0003',
      'ㄅ',
      'ㄬ',
      '\u0000',
      '一',
      '龥',
      '\u0000',
      '가',
      '힣',
      '\u0000',
      '\ud7a4',
      '\uffff',
      '\u0004',
      '\u0000',
      '\u0002',
      '퀊',
      '3',
      '\u0000',
      '3',
      '\u0000',
      'Ā',
      '甉',
      '猆',
      'ⱎ',
      '獥',
      '߲',
      's',
      'ँ',
      'ٵ',
      '鑳',
      '쵰',
      '倃',
      '树',
      's',
      '猁',
      '䄪',
      '瑦',
      '\u000b',
      '猁',
      '䄪',
      '瑴',
      '楲',
      '㽢',
      'e',
      '猁',
      '䈪',
      '晥',
      '౯',
      'Ā',
      '⩳',
      '흔',
      '\u007f',
      '猁',
      '缪',
      'Ā',
      '⩳',
      '\udbb8',
      'd',
      '딁',
      'Å',
      '㬃',
      '\u0600',
      '慦',
      '汵',
      '䅴',
      '瑴',
      '楲',
      '㽢',
      'e',
      '昆',
      '畡',
      '瑬',
      '疆',
      'e',
      '氆',
      '攂',
      '\u0600',
      'ɬ',
      '呥',
      '翗',
      '\u0600',
      'ɬ',
      '罥',
      '\u0600',
      'ɬ',
      '롥',
      '䅀',
      't',
      '\b',
      '⠈',
      'ℛ',
      '\u0007',
      '⠈',
      '呄',
      'D',
      '⠈',
      '୴',
      'Ὦ',
      '\ued53',
      '\u001e',
      '⠈',
      '柘',
      '\u000b',
      '⠈',
      '柘',
      '䄋',
      't',
      '䄈',
      '칲',
      'y',
      '䄈',
      '瑴',
      '♲',
      '瑀',
      '慔',
      '繢',
      'ࠀ',
      '瑁',
      '牴',
      '扩',
      '政',
      'ࠀ',
      '瑁',
      '牴',
      '扩',
      '政',
      '獉',
      '灓',
      '椢',
      '楦',
      '#',
      '䄈',
      '瑴',
      '楲',
      '㽢',
      '䱥',
      'ᾉ',
      'ⱎ',
      'e',
      '䄈',
      '瑴',
      '楲',
      '㽢',
      '乥',
      'S',
      '䄈',
      '瑴',
      '楲',
      '㽢',
      '健',
      '昌',
      '硩',
      'ࠀ',
      '瑁',
      '牴',
      '扩',
      '政',
      '乑',
      '攬',
      'ࠀ',
      '瑁',
      '牴',
      '扩',
      '政',
      '祔',
      '数',
      'ࠀ',
      '瑁',
      '牴',
      '扩',
      '政',
      '剕',
      'I',
      '䄈',
      '瑴',
      '楲',
      '㽢',
      '獥',
      'ࠀ',
      '瑁',
      '牴',
      '扩',
      '政',
      '疆',
      'e',
      '䄈',
      '瑴',
      '楲',
      '㽢',
      '\ue865',
      '\u0006',
      '䄈',
      '瑴',
      '楲',
      '㽢',
      '\ue865',
      '丆',
      'S',
      '䄈',
      '瑴',
      '虲',
      '敵',
      '慔',
      '繢',
      'ࠀ',
      '쑁',
      '汅',
      '註',
      's',
      '䄈',
      '헄',
      '湕',
      '昆',
      '⌁',
      'ⱎ',
      '獥',
      '߲',
      's',
      '䈈',
      '潯',
      'ⅾ',
      'ࠀ',
      '潂',
      '繯',
      '䄡',
      't',
      '䌈',
      '誼',
      'ⅈ',
      '汤',
      '\u000b',
      '䐈',
      '䵏',
      'ꍉ',
      '㭬',
      '슊',
      'ࠀ',
      '呄',
      '䡄',
      '搡',
      '୬',
      'ࠀ',
      '襄',
      '祴',
      '数',
      'ࠀ',
      '襄',
      '誏',
      'ࠀ',
      '襄',
      '誏',
      '汅',
      '註',
      'ࠀ',
      '襄',
      '誏',
      '祔',
      '数',
      'ࠀ',
      '襄',
      '誏',
      '祔',
      '数',
      '뵂',
      'y',
      '䐈',
      '辉',
      '咊',
      '灹',
      '乥',
      '攬',
      'ࠀ',
      '襄',
      '誏',
      '祔',
      '数',
      '\ued50',
      '\uf1cd',
      'ࠀ',
      '襄',
      '誏',
      '祔',
      '数',
      '\uf1e3',
      'ࠀ',
      '汅',
      '註',
      '瑁',
      '牴',
      '扩',
      '政',
      's',
      '䔈',
      '㭬',
      '䲊',
      'ᾉ',
      'ⱎ',
      'e',
      '䔈',
      '㭬',
      '亊',
      '攬',
      '\uf273',
      '猇',
      'ࠀ',
      '汅',
      '註',
      '\u0c50',
      '楦',
      'x',
      '䔈',
      '㭬',
      '冊',
      'ⱎ',
      'e',
      '䔈',
      '㭬',
      '喊',
      '䥒',
      'ࠀ',
      '汅',
      '註',
      'ꡳ',
      '읔',
      'ⱎ',
      'e',
      '䔈',
      '㭬',
      '玊',
      '咨',
      '仇',
      '攬',
      '华',
      'ࠀ',
      '汅',
      '註',
      '\uf1a8',
      'ࠀ',
      '湅',
      'ʹ',
      '敩',
      's',
      '䔈',
      '瑮',
      '礃',
      '慈',
      '桳',
      'ࠀ',
      '湅',
      'ʹ',
      '乹',
      '攬',
      'ࠀ',
      '湅',
      'ʹ',
      '偹',
      '췭',
      'ñ',
      '䔈',
      '瑮',
      '礃',
      '疆',
      'e',
      '䔈',
      '瑮',
      '礃',
      '暠',
      'ጋ',
      '万',
      '攬',
      'ࠀ',
      '湅',
      'ʹ',
      'ꁹ',
      '୦',
      'ܓ',
      '\ued50',
      '\uf1cd',
      'ࠀ',
      '湅',
      'ʹ',
      'ꁹ',
      '୦',
      'ܓ',
      '\uf1e3',
      'ࠀ',
      '湅',
      'ʹ',
      'ꁹ',
      '潳',
      '癬',
      '\u000b',
      '䔈',
      '瑮',
      '礃',
      '\uf1e3',
      'ࠀ',
      '湅',
      'ʹ',
      '\ue879',
      '쉴',
      'ⱎ',
      'e',
      '䔈',
      '牲',
      '䠙',
      '搡',
      '୬',
      'ࠀ',
      '敆',
      '甑',
      '\f',
      '䤈',
      'R',
      '䤈',
      '沣',
      '註',
      'Â',
      '䰈',
      '攁',
      '\udd4e',
      '\u000b',
      '䰈',
      '᭡',
      '\udbb8',
      'd',
      '䰈',
      'ᾉ',
      'ⱎ',
      'e',
      '丈',
      '⌬',
      '瑉',
      ';',
      '丈',
      '⌬',
      '瑉',
      '主',
      'S',
      '丈',
      '攬',
      'ࠀ',
      'ⱎ',
      '獥',
      '߲',
      '흎',
      'ࠀ',
      'ⱎ',
      '獥',
      '߲',
      '\u0c50',
      '楦',
      'x',
      '丈',
      '攬',
      '\uf273',
      '唇',
      '䥒',
      'ࠀ',
      '轎',
      '疆',
      '獥',
      'ࠀ',
      '轎',
      '玡',
      'ࠀ',
      '轎',
      'ۊ',
      'd',
      '丈',
      '×',
      '丈',
      '䛗',
      '攌',
      'ࠀ',
      '흎',
      '楓',
      '汢',
      '\u0084',
      '丈',
      '룗',
      '擛',
      'ࠀ',
      '睏',
      '୮',
      '襄',
      '誏',
      'ࠀ',
      '睏',
      '୮',
      '汅',
      '註',
      'ࠀ',
      '\uf24f',
      '畱',
      '罥',
      'ࠀ',
      '乑',
      '攬',
      'ࠀ',
      '灓',
      '椢',
      '楦',
      '#',
      '匈',
      '᳃',
      '\uec53',
      'ࠀ',
      '\uec53',
      'ۊ',
      'd',
      '合',
      'ࡀ',
      'ࠀ',
      '祔',
      '数',
      'ࠀ',
      '읔',
      'ⱎ',
      'e',
      '合',
      '哇',
      '扡',
      '~',
      '合',
      '䣗',
      '搡',
      '~',
      '合',
      '受',
      'ᯭ',
      '»',
      '合',
      '꧗',
      'ࠀ',
      '흔',
      'À',
      '唈',
      '䥒',
      'ࠀ',
      'ୖ',
      'ᱳ',
      'ࠀ',
      '䵘',
      'ꁌ',
      '\ue661',
      'ࠀ',
      '|',
      '缈',
      'ࠀ',
      '疆',
      'e',
      '鐈',
      'ⅰ',
      '搆',
      '흔',
      '牁',
      '秎',
      'ࠀ',
      '炔',
      '搡',
      '䖄',
      '瑮',
      '椃',
      '獥',
      'ࠀ',
      '玠',
      'ၰ',
      'ꚁ',
      '\u0006',
      'ꘈ',
      '䈆',
      '潯',
      '䡫',
      '獡',
      'h',
      'ꘈ',
      '䈆',
      '潯',
      '䥫',
      'D',
      'ꘈ',
      '攟',
      '捳',
      '\u0084',
      'ꘈ',
      '转',
      '乮',
      '\u0bdd',
      'ࠀ',
      '\ud8a8',
      's',
      'ꠈ',
      '\uf3d8',
      'ࠀ',
      '©',
      '눈',
      '᭲',
      'ె',
      'e',
      '눈',
      '᭲',
      '\udbb8',
      'd',
      '레',
      'Ṁ',
      'ࠀ',
      '䂸',
      's',
      '레',
      '굀',
      '୴',
      'ó',
      '레',
      '擛',
      'ۨ',
      's',
      '먈',
      'ࠀ',
      'À',
      '쀈',
      '瑁',
      'ࠀ',
      '\uf1e3',
      'ࠀ',
      'ۨ',
      'ࠀ',
      'ۨ',
      'ⱎ',
      'e',
      '\ue808',
      '吆',
      '灹',
      'e',
      '\ue808',
      '蘆',
      '敵',
      'ࠀ',
      '瓨',
      '䣂',
      '獡',
      'h',
      '\ue808',
      '쉴',
      'ⱎ',
      'e',
      '\ue808',
      '쉴',
      '\ued50',
      '\uf1cd',
      'ࠀ',
      '瓨',
      '珂',
      'ࠀ',
      '瓨',
      '\ue3c2',
      'ñ',
      '\ue908',
      '\ue661',
      '¿',
      '\uf308',
      '憠',
      'æ',
      'ဉ',
      '\ue865',
      '\u0006',
      '愉',
      '椩',
      '祦',
      'ऀ',
      '䁥',
      'ऀ',
      '䁥',
      '瑁',
      '牴',
      '扩',
      '政',
      '獉',
      '敄',
      '慦',
      '汵',
      't',
      '漉',
      '\u0081',
      '\n',
      '爋',
      '\u0019',
      '洌',
      '癯',
      'e',
      '洌',
      '癯',
      '䅥',
      '瑴',
      '楲',
      '㽢',
      'e',
      '洌',
      '癯',
      '䅥',
      '瑴',
      '楲',
      '㽢',
      '乥',
      'S',
      '洌',
      '癯',
      '䅥',
      '瑴',
      '楲',
      '㽢',
      '\ue865',
      '\u0006',
      '洌',
      '癯',
      '䕥',
      '㭬',
      '䆊',
      't',
      '洌',
      '癯',
      '乥',
      '⌬',
      '瑉',
      ';',
      '洌',
      '癯',
      '乥',
      '⌬',
      '瑉',
      '主',
      'S',
      '洌',
      '癯',
      '롥',
      '擛',
      'ఀ',
      'ꝰ',
      '䄇',
      '瑴',
      '楲',
      '㽢',
      'e',
      '瀌',
      'ާ',
      '흔',
      '\u007f',
      '瀌',
      'ާ',
      '\u007f',
      '瀌',
      'ާ',
      '\udbb8',
      'd',
      '焌',
      'ⱎ',
      'e',
      '焌',
      '济',
      '殰',
      '\u0013',
      '焌',
      '罎',
      '敄',
      '\t',
      '焌',
      '璔',
      '渋',
      '伟',
      '偲',
      '췭',
      '䑉',
      'ఀ',
      '潳',
      '癬',
      '䕥',
      '瑮',
      '礃',
      'ఀ',
      '\uec73',
      '\u0e00',
      'ༀ',
      '\ue8fa',
      '\u0006',
      '琑',
      '楲',
      '㽢',
      '䑥',
      '晥',
      'ᰄ',
      's',
      '琑',
      '楲',
      '㽢',
      '獥',
      'ሀ',
      'ͳ',
      '#',
      '⸖',
      '⸕',
      '⸗',
      '猌',
      '깯',
      '猇',
      'ᘀ',
      'ᔮ',
      'ᜮ',
      'ᨮ',
      '㼮',
      'Û',
      '⸖',
      '⸕',
      '⸗',
      '⸚',
      '浸',
      'l',
      '⸖',
      '⸕',
      '⸗',
      '⸚',
      '浸',
      '\u2e6c',
      '慪',
      '灸',
      'ᘀ',
      'ᔮ',
      'ᜮ',
      'ᨮ',
      '砮',
      '汭',
      '瀮',
      '獀',
      '猋',
      'ᘀ',
      'ᔮ',
      '瘮',
      'm',
      '弖',
      '引',
      '浸',
      '剬',
      '䵉',
      '玠',
      '깯',
      '猇',
      '᠀',
      '쥰',
      '᠀',
      '쥰',
      '흔',
      '\u007f',
      '瀘',
      '埉',
      '栃',
      '畎',
      'Ä',
      '瀘',
      '翉',
      '᠀',
      '쥰',
      '\udbb8',
      'd',
      '朙',
      '眮',
      '挳',
      '搮',
      '浯',
      'ᤀ',
      '\u2e67',
      '浸',
      '\u2e6c',
      '慳',
      'x',
      '朙',
      '砮',
      '汭',
      '献',
      '硡',
      '栮',
      '炋',
      '猋',
      'ᬀ',
      '瑀',
      '湁',
      '䕤',
      '摮',
      '汅',
      '註',
      'ᬀ',
      '瑀',
      '呄',
      'D',
      '䀛',
      '䑴',
      '辉',
      '\u008a',
      '䀛',
      '䕴',
      '㭬',
      '\u008a',
      '䀛',
      '䕴',
      '瑮',
      '礃',
      '暠',
      'ጋ',
      '\u0007',
      '䀛',
      '側',
      '昌',
      '硩',
      'ᡍ',
      '葰',
      'ᬀ',
      '瑀',
      '坳',
      '栃',
      'ᬀ',
      '䆻',
      '칲',
      'ꁹ',
      '\uec73',
      'Ḁ',
      'Ḁ',
      '䐨',
      '䑔',
      'Ḁ',
      '瑁',
      '牴',
      '䀦',
      '呴',
      '扡',
      '~',
      '䄞',
      '瑴',
      '楲',
      '㽢',
      'e',
      '䄞',
      '瑴',
      '楲',
      '㽢',
      '䥥',
      '䑳',
      '晥',
      '畡',
      '瑬',
      'Ḁ',
      '瑁',
      '牴',
      '扩',
      '政',
      '华',
      'Ḁ',
      '瑁',
      '牴',
      '扩',
      '政',
      '乑',
      '攬',
      'Ḁ',
      '瑁',
      '牴',
      '扩',
      '政',
      '疆',
      'e',
      '䄞',
      '瑴',
      '楲',
      '㽢',
      '\ue865',
      '\u0006',
      '䄞',
      '瑴',
      '楲',
      '㽢',
      '\ue865',
      '丆',
      'S',
      '䄞',
      '瑴',
      '虲',
      '敵',
      '慔',
      '繢',
      'Ḁ',
      '쑁',
      '嗕',
      'ٮ',
      'Ŧ',
      '丣',
      '攬',
      '\uf273'
   };
   private static final String FEATURES = "http://xml.org/sax/features/";
   private static final String PROPERTIES = "http://xml.org/sax/properties/";

   @Override
   public XMLReader getXMLReader() {
      return this;
   }

   void illegalCharacter(int ch) {
      this.fatalError("Invalid character '%1' encountered.", ((StringBuffer)(new Object("&#x"))).append(Integer.toHexString(ch)).toString());
   }

   void fatalError(String error, String parm) {
      int index = error.indexOf("%1");
      if (index == -1) {
         this.fatalError(error);
      }

      this.fatalError(((StringBuffer)(new Object())).append(error.substring(0, index)).append(parm).append(error.substring(index + 2)).toString());
   }

   void fatalError(String msg) throws SAXParseException {
      SAXParseException x = (SAXParseException)(new Object(msg, this));
      this._errorHandler.fatalError(x);
      throw x;
   }

   void nonFatalError(String error, String parm) {
      int index = error.indexOf("%1");
      if (index == -1) {
         this.nonFatalError(error);
      }

      this.nonFatalError(((StringBuffer)(new Object())).append(error.substring(0, index)).append(parm).append(error.substring(index + 2)).toString());
   }

   void nonFatalError(String msg) {
      this._errorHandler.error((SAXParseException)(new Object(msg, this)));
   }

   void warning(String error, String parm) {
      int index = error.indexOf("%1");
      if (index == -1) {
         this.warning(error);
      }

      this.warning(((StringBuffer)(new Object())).append(error.substring(0, index)).append(parm).append(error.substring(index + 2)).toString());
   }

   void warning(String msg) {
      this._errorHandler.warning((SAXParseException)(new Object(msg, this)));
   }

   int kwLookup() {
      int kw;
      kw = -1;
      label70:
      switch (this._nameLength) {
         case 1:
         case 4:
         case 9:
            return -1;
         case 2:
         default:
            switch (this._nameBuffer[0]) {
               case 'I':
                  kw = 0;
                  break label70;
               case 'n':
                  kw = 1;
                  break label70;
               default:
                  return -1;
            }
         case 3:
            switch (this._nameBuffer[0]) {
               case '1':
                  kw = 2;
                  break label70;
               case 'A':
                  kw = 3;
                  break label70;
               case 'x':
                  kw = 5;
                  break label70;
               case 'y':
                  kw = 4;
                  break label70;
               default:
                  return -1;
            }
         case 5:
            switch (this._nameBuffer[0]) {
               case 'C':
                  kw = 6;
                  break label70;
               case 'E':
                  kw = 7;
                  break label70;
               case 'F':
                  kw = 8;
                  break label70;
               case 'I':
                  kw = 9;
                  break label70;
               case 'N':
                  kw = 10;
                  break label70;
               case 'x':
                  kw = 11;
                  break label70;
               default:
                  return -1;
            }
         case 6:
            switch (this._nameBuffer[1]) {
               case 'C':
                  kw = 15;
                  break label70;
               case 'D':
                  kw = 13;
                  break label70;
               case 'G':
                  kw = 14;
                  break label70;
               case 'N':
                  kw = 12;
                  break label70;
               case 'U':
                  kw = 16;
                  break label70;
               case 'Y':
                  kw = 17;
                  break label70;
               default:
                  return -1;
            }
         case 7:
            switch (this._nameBuffer[0]) {
               case 'A':
                  kw = 19;
                  break label70;
               case 'D':
                  kw = 20;
                  break label70;
               case 'E':
                  kw = 21;
                  break label70;
               case 'I':
                  switch (this._nameBuffer[1]) {
                     case 'L':
                        return -1;
                     case 'M':
                     default:
                        kw = 22;
                        break label70;
                     case 'N':
                        kw = 23;
                        break label70;
                  }
               case 'N':
                  kw = 24;
                  break label70;
               case 'v':
                  kw = 18;
                  break label70;
               default:
                  return -1;
            }
         case 8:
            switch (this._nameBuffer[3]) {
               case 'A':
                  kw = 28;
                  break label70;
               case 'I':
                  kw = 26;
                  break label70;
               case 'O':
                  kw = 27;
                  break label70;
               case 'U':
                  kw = 29;
                  break label70;
               case 'o':
                  kw = 25;
                  break label70;
               default:
                  return -1;
            }
         case 10:
            kw = 30;
      }

      String kwStr = this._kwTable[kw];

      for (int i = 0; i < this._nameLength; i++) {
         if (this._nameBuffer[i] != kwStr.charAt(i)) {
            return -1;
         }
      }

      return kw;
   }

   private String nameToString() {
      return (String)(new Object(this._nameBuffer, 0, this._nameLength));
   }

   private void lastReadInputMustBeTokenStartInput() {
      this.lastReadInputMustBe(this._tokenStartInput);
   }

   private void lastReadInputMustBe(XMLParser$InputReader start) {
   }

   private void eof() {
      if (this._EOFFatal) {
         this.fatalError("Unexpected end of file.");
      }
   }

   private void require(int ch1, int ch2) {
      if (ch1 != ch2) {
         this.illegalCharacter(ch1);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void pushExternalParsedEntity(String entityName, boolean isPE, String publicId, String systemId, InputSource is) {
      boolean var16 = false /* VF: Semaphore variable */;

      label314: {
         label313: {
            label312: {
               label311: {
                  try {
                     var16 = true;
                     this.pushName();
                     boolean isMainDocument = this._in == null;
                     Reader reader = is.getCharacterStream();
                     InputStream byteStream = is.getByteStream();
                     if (reader == null && byteStream == null) {
                        if (is.getSystemId() == null) {
                           this.fatalError("Cannot resolve '%1'.", "null");
                        }

                        InputSource newIs = this._entityResolver.resolveEntity(is.getPublicId(), is.getSystemId());
                        if (newIs == null) {
                           this.fatalError("Cannot resolve '%1'.", is.getSystemId());
                        }

                        reader = is.getCharacterStream();
                        byteStream = is.getByteStream();
                        if (reader == null && byteStream == null) {
                           this.fatalError("Cannot resolve '%1'.", is.getSystemId());
                        }
                     }

                     XMLParser$InputReader inStream = null;
                     if (reader != null) {
                        inStream = new XMLParser$ReaderReader(this, reader);
                        if (this._pushedBackFirstChar != -1) {
                           new XMLParser$PushBackReader(this, ((StringBuffer)(new Object(""))).append(this._pushedBackFirstChar).toString());
                           this._pushedBackFirstChar = -1;
                        }
                     } else if (byteStream == null) {
                        this.fatalError("Cannot resolve '%1'.", "null");
                     } else {
                        int ch;
                        if (this._pushedBackFirstChar != -1) {
                           ch = this._pushedBackFirstChar;
                           this._pushedBackFirstChar = -1;
                        } else {
                           ch = byteStream.read();
                        }

                        label257:
                        switch (ch) {
                           case -1:
                              inStream = new XMLParser$UTF8InputStreamReader(this, byteStream);
                              break;
                           case 0:
                              switch (byteStream.read()) {
                                 case 0:
                                    switch (byteStream.read()) {
                                       case 0:
                                          this.addToName(byteStream.read());
                                          inStream = new XMLParser$UCS4BigEndianInputStreamReader(this, byteStream);
                                          break label257;
                                       case 254:
                                          this.require(byteStream.read(), 255);
                                          inStream = new XMLParser$UCS4BigEndianInputStreamReader(this, byteStream);
                                          break label257;
                                       default:
                                          this.fatalError("Unable to determine document encoding.");
                                          break label257;
                                    }
                                 case 60:
                                    this.require(byteStream.read(), 0);
                                    this.require(byteStream.read(), 63);
                                    this.addToName(60);
                                    this.addToName(63);
                                    inStream = new XMLParser$UTF16BigEndianInputStreamReader(this, byteStream);
                                    break label257;
                                 default:
                                    this.fatalError("Unable to determine document encoding.");
                                    break label257;
                              }
                           case 60:
                              ch = byteStream.read();
                              switch (ch) {
                                 case 0:
                                    switch (byteStream.read()) {
                                       case 0:
                                          this.require(byteStream.read(), 0);
                                          this.addToName(60);
                                          inStream = new XMLParser$UCS4SmallEndianInputStreamReader(this, byteStream);
                                          break label257;
                                       case 63:
                                          this.require(byteStream.read(), 0);
                                          this.addToName(60);
                                          this.addToName(63);
                                          inStream = new XMLParser$UTF16SmallEndianInputStreamReader(this, byteStream);
                                          break label257;
                                       default:
                                          this.fatalError("Unable to determine document encoding.");
                                          break label257;
                                    }
                                 default:
                                    this.addToName(60);
                                    this.addToName(ch);
                                    inStream = new XMLParser$UTF8InputStreamReader(this, byteStream);
                                    break label257;
                              }
                           case 239:
                              this.require(byteStream.read(), 187);
                              this.require(byteStream.read(), 191);
                              inStream = new XMLParser$UTF8InputStreamReader(this, byteStream);
                              break;
                           case 254:
                              switch (byteStream.read()) {
                                 case 255:
                                    inStream = new XMLParser$UTF16BigEndianInputStreamReader(this, byteStream);
                                    break label257;
                                 default:
                                    this.fatalError("Unable to determine document encoding.");
                                    break label257;
                              }
                           case 255:
                              switch (byteStream.read()) {
                                 case 254:
                                    ch = byteStream.read();
                                    switch (ch) {
                                       case -2:
                                          this.addToName(ch + (byteStream.read() << 8));
                                       case -1:
                                          inStream = new XMLParser$UTF16SmallEndianInputStreamReader(this, byteStream);
                                          break label257;
                                       case 0:
                                       default:
                                          this.require(byteStream.read(), 0);
                                          inStream = new XMLParser$UCS4SmallEndianInputStreamReader(this, byteStream);
                                          break label257;
                                    }
                                 default:
                                    this.fatalError("Unable to determine document encoding.");
                                    break label257;
                              }
                           default:
                              this.addToName(ch);
                              inStream = new XMLParser$UTF8InputStreamReader(this, byteStream);
                        }

                        if (this._nameLength != 0) {
                           new XMLParser$PushBackReader(this, this.nameToString());
                           inStream.setColumnNumber(this._nameLength);
                        }
                     }

                     inStream.setReturnEndOfEntity(true);
                     inStream.setEntityId(publicId, systemId);
                     inStream.setEntityName(entityName, isPE);
                     boolean var19 = false /* VF: Semaphore variable */;

                     label307: {
                        label306: {
                           label305: {
                              label304: {
                                 try {
                                    var19 = true;
                                    this.clearName();
                                    int ch = this.read();
                                    if (ch == -1) {
                                       var19 = false;
                                       break label306;
                                    }

                                    this.addToName(ch);
                                    if (ch != 60) {
                                       new XMLParser$PushBackReader(this, this.nameToString());
                                       var19 = false;
                                       break label305;
                                    }

                                    ch = this.read();
                                    if (ch == -1) {
                                       this.eof();
                                    }

                                    this.addToName(ch);
                                    if (ch != 63) {
                                       new XMLParser$PushBackReader(this, this.nameToString());
                                       var19 = false;
                                       break label304;
                                    }

                                    this.reqName();
                                    if (this.kwLookup() == 5) {
                                       if (isMainDocument) {
                                          this.reqXMLDecl();
                                          var19 = false;
                                       } else {
                                          this.reqTextDecl();
                                          var19 = false;
                                       }
                                       break label307;
                                    }

                                    if (this._havePeek) {
                                       new XMLParser$PushBackReader(this, ((StringBuffer)(new Object(""))).append((char)this._peek).toString());
                                       this._havePeek = false;
                                    }

                                    new XMLParser$PushBackReader(this, this.nameToString());
                                    new XMLParser$PushBackReader(this, "<?");
                                    var19 = false;
                                 } finally {
                                    if (var19) {
                                       inStream.setReturnEndOfEntity(false);
                                    }
                                 }

                                 inStream.setReturnEndOfEntity(false);
                                 var16 = false;
                                 break label311;
                              }

                              inStream.setReturnEndOfEntity(false);
                              var16 = false;
                              break label312;
                           }

                           inStream.setReturnEndOfEntity(false);
                           var16 = false;
                           break label313;
                        }

                        inStream.setReturnEndOfEntity(false);
                        var16 = false;
                        break label314;
                     }

                     inStream.setReturnEndOfEntity(false);
                     var16 = false;
                  } finally {
                     if (var16) {
                        this.popName();
                     }
                  }

                  this.popName();
                  return;
               }

               this.popName();
               return;
            }

            this.popName();
            return;
         }

         this.popName();
         return;
      }

      this.popName();
   }

   @Override
   public boolean isValidating() {
      return false;
   }

   @Override
   public boolean isNamespaceAware() {
      return this._isNamespaceAware;
   }

   @Override
   public void setNamespaceAware(boolean aware) {
      this._isNamespaceAware = aware;
   }

   void pushBackFirstChar(int ch) {
      this._pushedBackFirstChar = ch;
   }

   @Override
   public void parse(InputStream is, DefaultHandler handler) {
      this.parse((InputSource)(new Object(is)), handler);
   }

   @Override
   public void parse(InputSource is, DefaultHandler handler) {
      this._contentHandler = handler;
      this._dtdHandler = handler;
      this._errorHandler = handler;
      this._entityResolver = handler;
      this.parse(is);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void doParse(InputSource is) {
      if (this._contentHandler instanceof RIMExtendedHandler) {
         this._extHandler = (RIMExtendedHandler)this._contentHandler;
      }

      this._in = null;
      this.initNameBuffers();
      this.pushExternalParsedEntity(null, false, is.getPublicId(), is.getSystemId(), is);
      this._foundToBeStandalone = true;
      this._parameterEntities = (Hashtable)(new Object());
      this._entitiesDeclaredInExternalSubset = (Hashtable)(new Object());
      this._internalEntities = (Hashtable)(new Object());
      this._externalEntities = (Hashtable)(new Object());
      this._externalParameterEntities = (Hashtable)(new Object());
      this._internalEntities.put("lt", "&#60;");
      this._internalEntities.put("gt", "&#62;");
      this._internalEntities.put("amp", "&#38;");
      this._internalEntities.put("apos", "&#39;");
      this._internalEntities.put("quot", "&#34;");
      this._nameSpaceLookup = (Hashtable)(new Object());
      this._nameSpaceLookup.put("", "");
      this._nameSpaceLookup.put(XML, XMLURL);
      this._attributeDeclarations = (Hashtable)(new Object());
      this._contentHandler.setDocumentLocator(this);
      this._contentHandler.startDocument();
      this._parsing = true;
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (this._isNamespaceAware) {
            this._contentHandler.startPrefixMapping(XML, XMLURL);
         }

         this.document();
         if (this._isNamespaceAware) {
            this._contentHandler.endPrefixMapping(XML);
         }

         this._contentHandler.endDocument();
         var4 = false;
      } finally {
         if (var4) {
            this._parsing = false;
         }
      }

      this._parsing = false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void classifyToken() {
      this._EOFFatal = false;
      int ch = this.read();
      this._tokenStartInput = this._lastReadInput;
      this._EOFFatal = true;
      switch (ch) {
         case -1:
            this._tokenType = 8;
            return;
         case 9:
         case 10:
         case 13:
         case 32:
            while (true) {
               this._EOFFatal = false;
               ch = this.peek();
               this._EOFFatal = true;
               switch (ch) {
                  case -1:
                     this._tokenType = 8;
                     return;
                  case 9:
                  case 10:
                  case 13:
                  case 32:
                     this.advance();
                     break;
                  default:
                     this._tokenType = 1;
                     return;
               }
            }
         case 60:
            switch (this.peek()) {
               case 33:
                  this.advance();
                  switch (this.peek()) {
                     case 45:
                        this.advance();
                        if (this.read() != 45) {
                           this.expect('-');
                        }

                        this.clearName();
                        boolean saved = this._processPERefs;
                        boolean var6 = false /* VF: Semaphore variable */;

                        try {
                           var6 = true;
                           this._processPERefs = false;

                           while (true) {
                              ch = this.read();
                              if (ch == 45) {
                                 ch = this.read();
                                 if (ch == 45) {
                                    this.expect('>');
                                    this.lastReadInputMustBeTokenStartInput();
                                    this._tokenType = 2;
                                    if (this._extHandler != null) {
                                       this._extHandler.comment(this._nameBuffer, 0, this._nameLength);
                                       var6 = false;
                                    } else {
                                       var6 = false;
                                    }
                                    break;
                                 }

                                 this.addToName(ch);
                              } else {
                                 this.addToName(ch);
                              }
                           }
                        } finally {
                           if (var6) {
                              this._processPERefs = saved;
                           }
                        }

                        this._processPERefs = saved;
                        return;
                     case 91:
                        this.advance();
                        int cdataToken = this.optionalSpace() ? 9 : 12;
                        this.reqName();
                        switch (this.kwLookup()) {
                           case 6:
                              this._tokenType = cdataToken;
                              return;
                           case 14:
                              this._tokenType = 11;
                              return;
                           case 23:
                              this._tokenType = 10;
                              return;
                           default:
                              this._tokenType = 9;
                              return;
                        }
                     default:
                        this.reqName();
                        this._tokenType = 7;
                        return;
                  }
               case 47:
                  this.advance();
                  this.reqQName(true, this._defaultURI, false);
                  this._tokenType = 5;
                  return;
               case 63:
                  this.advance();
                  this.reqName();
                  this._tokenType = 3;
                  return;
               default:
                  this.reqQName(true, this._defaultURI, true);
                  this._tokenType = 4;
                  return;
            }
         default:
            this._tokenType = 9;
      }
   }

   static int classify(int ch) {
      for (int i = 0; i < _letters.length; i += 3) {
         if (ch < _letters[i]) {
            return 4;
         }

         if (ch <= _letters[i + 1]) {
            return _letters[i + 2];
         }
      }

      return 4;
   }

   static boolean isNameStart(int ch) {
      switch (ch) {
         case 57:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 91:
         case 92:
         case 93:
         case 94:
         case 96:
            if (classify(ch) == 0) {
               return true;
            }

            return false;
         case 58:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 95:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         default:
            return true;
      }
   }

   static boolean isNameChar(int ch) {
      switch (ch) {
         case 44:
         case 47:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 91:
         case 92:
         case 93:
         case 94:
         case 96:
            if (classify(ch) != 4) {
               return true;
            }

            return false;
         case 45:
         case 46:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 95:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         default:
            return true;
      }
   }

   private boolean nameIs(String str) {
      if (this._nameLength != str.length()) {
         return false;
      }

      for (int i = this._nameLength - 1; i >= 0; i--) {
         if (this._nameBuffer[i] != str.charAt(i)) {
            return false;
         }
      }

      return true;
   }

   private void initNameBuffers() {
      this._nameBuffers = new char[64][];
      this._nameLengths = new int[64];
      this._firstNonSpaces = new int[64];
      this._lastNonSpaces = new int[64];
      this._nameBuffers[0] = this._nameBuffer = new char[64];
   }

   private void pushName() {
      this.moveNameStack(1);
      this.clearName();
   }

   private void popName() {
      this.moveNameStack(-1);
   }

   private void moveNameStack(int by) {
      this._firstNonSpaces[this._nameStackIndex] = this._firstNonSpace;
      this._lastNonSpaces[this._nameStackIndex] = this._lastNonSpace;
      this._nameLengths[this._nameStackIndex] = this._nameLength;
      this._nameBuffers[this._nameStackIndex] = this._nameBuffer;
      this._nameStackIndex += by;
      if (this._nameStackIndex >= this._nameBuffers.length) {
         this._nameBuffers = ArrayResize.charArrayArrayResize(this._nameBuffers, this._nameStackIndex + 64);
         this._nameLengths = ArrayResize.intArrayResize(this._nameLengths, this._nameStackIndex + 64);
         this._firstNonSpaces = ArrayResize.intArrayResize(this._firstNonSpaces, this._nameStackIndex + 64);
         this._lastNonSpaces = ArrayResize.intArrayResize(this._lastNonSpaces, this._nameStackIndex + 64);
      }

      this._nameBuffer = this._nameBuffers[this._nameStackIndex];
      if (this._nameBuffer == null) {
         this._nameBuffer = this._nameBuffers[this._nameStackIndex] = new char[64];
      }

      this._nameLength = this._nameLengths[this._nameStackIndex];
      this._firstNonSpace = this._firstNonSpaces[this._nameStackIndex];
      this._lastNonSpace = this._lastNonSpaces[this._nameStackIndex];
   }

   private void clearName() {
      this._nameLength = 0;
      this._firstNonSpace = -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void addToName(int ch) {
      boolean var5 = false /* VF: Semaphore variable */;

      label27:
      try {
         var5 = true;
         this._nameBuffer[this._nameLength] = (char)ch;
         var5 = false;
      } finally {
         if (var5) {
            int length = this._nameBuffer.length;
            this._nameBuffer = ArrayResize.charArrayResize(this._nameBuffer, length + 64);
            this._nameBuffer[this._nameLength] = (char)ch;
            break label27;
         }
      }

      switch (ch) {
         default:
            if (this._firstNonSpace == -1) {
               this._firstNonSpace = this._nameLength;
            }

            this._lastNonSpace = this._nameLength;
         case 9:
         case 10:
         case 13:
         case 32:
            this._nameLength++;
      }
   }

   private void expectKeyword(int kw) {
      this.reqName();
      if (this.kwLookup() != kw) {
         this.fatalError("Expecting '%1'.", this._kwTable[kw]);
      }
   }

   private void expect(char ch) {
      if (this.read() != ch) {
         this.fatalError("Expecting '%1'.", ((StringBuffer)(new Object(""))).append(ch).toString());
      }
   }

   private void reqName() {
      int ch = this.read();
      if (!isNameStart(ch)) {
         this.fatalError("Expecting a name.");
      }

      this.clearName();
      this.addToName(ch);

      while (true) {
         ch = this.peek();
         if (!isNameChar(ch)) {
            return;
         }

         this.advance();
         this.addToName(ch);
      }
   }

   private void reqNCName() {
      int ch = this.read();
      if (ch == 58 || !isNameStart(ch)) {
         this.fatalError("Expecting a name.");
      }

      int ncSeparator = this._isNamespaceAware ? 58 : -1;
      this.addToName(ch);

      while (true) {
         ch = this.peek();
         if (ch == ncSeparator) {
            return;
         }

         if (!isNameChar(ch)) {
            return;
         }

         this.advance();
         this.addToName(ch);
      }
   }

   static boolean isValidName(String name, boolean allowColon) {
      int notAllowedChar;
      if (allowColon) {
         notAllowedChar = -1;
      } else {
         notAllowedChar = 58;
      }

      int length = name.length();
      if (length == 0) {
         return false;
      }

      int ch = name.charAt(0);
      if (ch != notAllowedChar && isNameStart(ch)) {
         for (int i = 1; i < length; i++) {
            int var6 = name.charAt(i);
            if (var6 == notAllowedChar) {
               return false;
            }

            if (!isNameChar(var6)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void reqQName(boolean parse, String defaultURI, boolean allowDelayedNSDefinition) {
      this.clearName();
      this.reqNCName();
      if (this.peek() == 58) {
         this.advance();
         this.addToName(58);
         int localStart = this._nameLength;
         this._prefixName = (String)(new Object(this._nameBuffer, 0, localStart - 1));
         this.reqNCName();
         if (parse) {
            this._localName = (String)(new Object(this._nameBuffer, localStart, this._nameLength - localStart));
            this._qName = this.nameToString();
            this._uri = (String)this._nameSpaceLookup.get(this._prefixName);
            if (this._uri == null && !this._prefixName.equals(this.XMLNS)) {
               if (!allowDelayedNSDefinition && !this._allowUndefinedNamespaces) {
                  this.fatalError("Undefined namespace '%1'.", this._prefixName);
                  return;
               }

               this._uri = this._undefinedURI;
               return;
            }
         }
      } else if (parse) {
         this._prefixName = "";
         this._qName = this._localName = this.nameToString();
         this._uri = defaultURI;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void reqSystemLiteral() {
      boolean saved = this._processPERefs;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         this._processPERefs = false;
         int quote = this.read();
         XMLParser$InputReader literalStartInput = this._lastReadInput;
         label38:
         switch (quote) {
            case 34:
            case 39:
               this.clearName();

               while (true) {
                  int ch = this.read();
                  if (ch == quote) {
                     this.lastReadInputMustBe(literalStartInput);
                     var7 = false;
                     break label38;
                  }

                  this.addToName(ch);
               }
            default:
               this.fatalError("Expecting a system identifier.");
               var7 = false;
         }
      } finally {
         if (var7) {
            this._processPERefs = saved;
         }
      }

      this._processPERefs = saved;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void reqPubidLiteral() {
      boolean saved = this._processPERefs;
      boolean var7 = false /* VF: Semaphore variable */;

      label52: {
         try {
            var7 = true;
            this._processPERefs = false;
            int quote = this.read();
            XMLParser$InputReader literalStartInput = this._lastReadInput;
            switch (quote) {
               case 34:
               case 39:
                  this.clearName();

                  label46:
                  while (true) {
                     int ch = this.read();
                     if (ch == quote) {
                        this.lastReadInputMustBe(literalStartInput);
                        var7 = false;
                        break label52;
                     }

                     switch (ch) {
                        case 9:
                        case 11:
                        case 12:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                        case 29:
                        case 30:
                        case 31:
                        case 34:
                        case 38:
                        case 60:
                        case 62:
                        case 91:
                        case 92:
                        case 93:
                        case 94:
                        case 96:
                           break label46;
                        case 10:
                        case 13:
                        case 32:
                        case 33:
                        case 35:
                        case 36:
                        case 37:
                        case 39:
                        case 40:
                        case 41:
                        case 42:
                        case 43:
                        case 44:
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                        case 58:
                        case 59:
                        case 61:
                        case 63:
                        case 64:
                        case 65:
                        case 66:
                        case 67:
                        case 68:
                        case 69:
                        case 70:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 76:
                        case 77:
                        case 78:
                        case 79:
                        case 80:
                        case 81:
                        case 82:
                        case 83:
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                        case 90:
                        case 95:
                        case 97:
                        case 98:
                        case 99:
                        case 100:
                        case 101:
                        case 102:
                        case 103:
                        case 104:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 120:
                        case 121:
                        case 122:
                        default:
                           this.addToName(ch);
                     }
                  }
            }

            this.fatalError("Expecting a public identifer.");
            var7 = false;
         } finally {
            if (var7) {
               this._processPERefs = saved;
            }
         }

         this._processPERefs = saved;
         return;
      }

      this._processPERefs = saved;
   }

   private boolean optionalSpace() {
      boolean haveSpace = false;

      while (true) {
         switch (this.peek()) {
            case 9:
            case 10:
            case 13:
            case 32:
               haveSpace = true;
               this.advance();
               break;
            default:
               return haveSpace;
         }
      }
   }

   private void reqSpace() {
      if (!this.optionalSpace()) {
         this.fatalError("Expecting white space.");
      }
   }

   private void reqEq() {
      this.optionalSpace();
      this.expect('=');
      this.optionalSpace();
   }

   private void versionInfo() {
      this.expectKeyword(18);
      this.reqEq();
      this.reqSystemLiteral();
      if (this.kwLookup() != 2) {
         this.fatalError("XML version must be 1.0.");
      }
   }

   private void encodingDecl() {
      XMLParser$InputReader literalStartInput;
      this.expectKeyword(25);
      this.reqEq();
      int quote = this.read();
      literalStartInput = this._lastReadInput;
      label62:
      switch (quote) {
         case 34:
         case 39:
            this.clearName();
            int ch = this.read();
            this.addToName(ch);
            if (ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90) {
               do {
                  ch = this.read();
                  if (ch == quote) {
                     break label62;
                  }

                  this.addToName(ch);
               } while (ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90 || ch >= 48 && ch <= 57 || ch == 46 || ch == 95 || ch == 45);
            }
         default:
            this.fatalError("Expecting an encoding name.");
      }

      this._in.setEncoding(this.nameToString());
      this.lastReadInputMustBe(literalStartInput);
   }

   private void reqTextDecl() {
      this.reqSpace();
      if (this.peek() == 101) {
         this.encodingDecl();
      } else {
         this.versionInfo();
         this.reqSpace();
         this.encodingDecl();
      }

      this.optionalSpace();
      this.expect('?');
      this.expect('>');
   }

   private void reqXMLDecl() {
      this.reqSpace();
      this.versionInfo();
      boolean space = this.optionalSpace();
      if (this.peek() == 63) {
         this.advance();
         this.expect('>');
      } else {
         if (space && this.peek() == 101) {
            this.encodingDecl();
            space = this.optionalSpace();
         }

         if (this.peek() == 63) {
            this.advance();
            this.expect('>');
         } else {
            if (space) {
               this.reqName();
               if (this.kwLookup() == 30) {
                  this.reqEq();
                  this.reqSystemLiteral();
                  switch (this.kwLookup()) {
                     case 1:
                        this._standalone = false;
                        break;
                     case 4:
                        this._standalone = true;
                        break;
                     default:
                        this.fatalError("Invalid xml declaration.");
                  }
               } else {
                  this.fatalError("Invalid xml declaration.");
               }
            }

            this.optionalSpace();
            if (this.peek() == 63) {
               this.advance();
               this.expect('>');
            } else {
               this.fatalError("Invalid xml declaration.");
            }
         }
      }
   }

   private void reqPI() {
      if (this._nameLength == 3) {
         switch (this._nameBuffer[0]) {
            case 'X':
            case 'x':
               switch (this._nameBuffer[1]) {
                  case 'M':
                  case 'm':
                     switch (this._nameBuffer[2]) {
                        case 'L':
                        case 'l':
                           this.fatalError("Processing instruction target may not be 'XML'.");
                     }
               }
         }
      }

      String target = this.nameToString();
      if (this.peek() != 63) {
         this.reqSpace();
      }

      this.clearName();

      label38:
      while (true) {
         int ch = this.read();
         if (ch == 63) {
            while (true) {
               ch = this.read();
               switch (ch) {
                  case 61:
                     this.addToName(63);
                     this.addToName(ch);
                     continue label38;
                  case 62:
                     this.lastReadInputMustBeTokenStartInput();
                     this._contentHandler.processingInstruction(target, this.nameToString());
                     return;
                  case 63:
                  default:
                     this.addToName(63);
               }
            }
         } else {
            this.addToName(ch);
         }
      }
   }

   private boolean optionalMisc() {
      switch (this._tokenType) {
         case 0:
            return false;
         case 1:
         case 2:
         default:
            return true;
         case 3:
            this.reqPI();
            return true;
      }
   }

   boolean reqNDataDecl() {
      if (!this.optionalSpace()) {
         return false;
      }

      if (this.peek() != 78) {
         return false;
      }

      this.expectKeyword(10);
      this.reqSpace();
      this.reqName();
      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   boolean optionalExternalOrPublicID(boolean allowPublicId) {
      boolean var10 = false /* VF: Semaphore variable */;

      boolean haveSpace;
      label279: {
         boolean var14;
         label280: {
            label281: {
               try {
                  var10 = true;
                  switch (this.peek()) {
                     case 80:
                        this.expectKeyword(16);
                        this.reqSpace();
                        this.reqPubidLiteral();
                        this._publicId = this.nameToString();
                        haveSpace = this.optionalSpace();
                        int ch = this.peek();
                        if (!haveSpace || ch != 39 && ch != 34) {
                           this._systemId = null;
                           if (!allowPublicId) {
                              this.fatalError("Expecting a system identifier.");
                           }

                           var14 = true;
                           var10 = false;
                           break label280;
                        }

                        this.reqSystemLiteral();
                        this._systemId = this.nameToString();
                        var14 = true;
                        var10 = false;
                        break label281;
                     case 83:
                        this.expectKeyword(17);
                        this.reqSpace();
                        this.reqSystemLiteral();
                        this._systemId = this.nameToString();
                        this._publicId = null;
                        haveSpace = true;
                        var10 = false;
                        break label279;
                  }

                  var14 = false;
                  var10 = false;
               } finally {
                  if (var10) {
                     if (this._systemId != null) {
                        for (int i = this._systemId.length() - 1; i >= 0; i--) {
                           int ch = this._systemId.charAt(i);
                           if (ch >= 128 || ch <= 32) {
                              this.fatalError("Invalid URI '%1'.", this._systemId);
                           }

                           switch (this._systemId.charAt(i)) {
                              case '"':
                              case '#':
                              case '<':
                              case '>':
                              case '[':
                              case ']':
                              case '^':
                              case '`':
                              case '{':
                              case '|':
                              case '}':
                                 this.fatalError("Invalid URI '%1'.", this._systemId);
                                 break;
                           }
                        }
                     }
                  }
               }

               if (this._systemId != null) {
                  for (int i = this._systemId.length() - 1; i >= 0; i--) {
                     int ch = this._systemId.charAt(i);
                     if (ch >= 128 || ch <= 32) {
                        this.fatalError("Invalid URI '%1'.", this._systemId);
                     }

                     switch (this._systemId.charAt(i)) {
                        case '"':
                        case '#':
                        case '<':
                        case '>':
                        case '[':
                        case ']':
                        case '^':
                        case '`':
                        case '{':
                        case '|':
                        case '}':
                           this.fatalError("Invalid URI '%1'.", this._systemId);
                           break;
                     }
                  }
               }

               return var14;
            }

            if (this._systemId != null) {
               for (int i = this._systemId.length() - 1; i >= 0; i--) {
                  int ch = this._systemId.charAt(i);
                  if (ch >= 128 || ch <= 32) {
                     this.fatalError("Invalid URI '%1'.", this._systemId);
                  }

                  switch (this._systemId.charAt(i)) {
                     case '"':
                     case '#':
                     case '<':
                     case '>':
                     case '[':
                     case ']':
                     case '^':
                     case '`':
                     case '{':
                     case '|':
                     case '}':
                        this.fatalError("Invalid URI '%1'.", this._systemId);
                        break;
                  }
               }
            }

            return var14;
         }

         if (this._systemId != null) {
            for (int i = this._systemId.length() - 1; i >= 0; i--) {
               int ch = this._systemId.charAt(i);
               if (ch >= 128 || ch <= 32) {
                  this.fatalError("Invalid URI '%1'.", this._systemId);
               }

               switch (this._systemId.charAt(i)) {
                  case '"':
                  case '#':
                  case '<':
                  case '>':
                  case '[':
                  case ']':
                  case '^':
                  case '`':
                  case '{':
                  case '|':
                  case '}':
                     this.fatalError("Invalid URI '%1'.", this._systemId);
                     break;
               }
            }
         }

         return var14;
      }

      if (this._systemId != null) {
         for (int i = this._systemId.length() - 1; i >= 0; i--) {
            int ch = this._systemId.charAt(i);
            if (ch >= 128 || ch <= 32) {
               this.fatalError("Invalid URI '%1'.", this._systemId);
            }

            switch (this._systemId.charAt(i)) {
               case '"':
               case '#':
               case '<':
               case '>':
               case '[':
               case ']':
               case '^':
               case '`':
               case '{':
               case '|':
               case '}':
                  this.fatalError("Invalid URI '%1'.", this._systemId);
                  break;
            }
         }
      }

      return haveSpace;
   }

   boolean optionalExternalID() {
      return this.optionalExternalOrPublicID(false);
   }

   void reqExternalOrPublicID() {
      if (!this.optionalExternalOrPublicID(true)) {
         this.fatalError("Expecting an external or public identifier.");
      }
   }

   private void cp() {
      if (this.peek() == 40) {
         XMLParser$InputReader parenStart = this._lastReadInput;
         this.advance();
         this.choiceOrSeq();
         this.lastReadInputMustBe(parenStart);
      } else {
         this.reqName();
      }

      switch (this.peek()) {
         case 42:
         case 43:
         case 63:
            this.advance();
      }
   }

   private void choiceOrSeq() {
      this.optionalSpace();
      this.cp();
      this.optionalSpace();
      label15:
      switch (this.peek()) {
         case 44:
            while (true) {
               this.advance();
               this.optionalSpace();
               this.cp();
               this.optionalSpace();
               if (this.peek() != 44) {
                  break label15;
               }
            }
         case 124:
            do {
               this.advance();
               this.optionalSpace();
               this.cp();
               this.optionalSpace();
            } while (this.peek() == 124);
      }

      this.expect(')');
   }

   private void reqContentSpec() {
      switch (this.peek()) {
         case 40:
            XMLParser$InputReader parenStart = this._lastReadInput;
            this.advance();
            this.optionalSpace();
            if (this.peek() != 35) {
               this.choiceOrSeq();
               this.lastReadInputMustBe(parenStart);
               switch (this.peek()) {
                  case 42:
                  case 43:
                  case 63:
                     this.advance();
                     return;
                  default:
                     return;
               }
            }

            this.advance();
            this.reqName();
            if (this.kwLookup() != 15) {
               this.fatalError("Expecting '%1'.", this._kwTable[15]);
            }

            this.optionalSpace();
            boolean needStar = false;

            while (this.peek() == 124) {
               needStar = true;
               this.advance();
               this.optionalSpace();
               this.reqName();
               this.optionalSpace();
            }

            this.expect(')');
            this.lastReadInputMustBe(parenStart);
            if (this.peek() == 42 || needStar) {
               this.expect('*');
               return;
            }
            break;
         default:
            this.reqName();
            switch (this.kwLookup()) {
               case 3:
               case 7:
                  break;
               default:
                  this.fatalError("Expecting 'EMPTY' or 'ANY'.");
            }
      }
   }

   private void reqElementDecl() {
      this.reqSpace();
      this.reqQName(false, "", true);
      this.reqSpace();
      this.reqContentSpec();
      this.optionalSpace();
      this.expect('>');
      this.lastReadInputMustBeTokenStartInput();
   }

   void reqNmtoken() {
      int ch = this.read();
      if (!isNameChar(ch)) {
         this.fatalError("Expecting a name token.");
      }

      this.clearName();

      while (true) {
         this.addToName(ch);
         ch = this.peek();
         if (!isNameChar(ch)) {
            return;
         }

         this.advance();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private String reqAttType() {
      boolean var5 = false /* VF: Semaphore variable */;

      String attType;
      label78: {
         String var2;
         label77: {
            try {
               var5 = true;
               this.pushName();
               if (this.peek() == 40) {
                  this.advance();
                  this.optionalSpace();
                  this.reqNmtoken();
                  this.optionalSpace();

                  while (this.peek() == 124) {
                     this.advance();
                     this.optionalSpace();
                     this.reqNmtoken();
                     this.optionalSpace();
                  }

                  this.expect(')');
                  attType = "Enumeration";
                  var5 = false;
                  break label78;
               }

               this.reqName();
               attType = this.nameToString();
               switch (this.kwLookup()) {
                  case 0:
                  case 6:
                  case 9:
                  case 12:
                  case 13:
                  case 24:
                  case 26:
                  case 27:
                     break;
                  case 28:
                     this.reqSpace();
                     this.expect('(');
                     this.optionalSpace();
                     this.reqName();
                     this.optionalSpace();

                     while (this.peek() == 124) {
                        this.advance();
                        this.optionalSpace();
                        this.reqName();
                        this.optionalSpace();
                     }

                     this.expect(')');
                     break;
                  default:
                     this.fatalError("Expecting an attribute type.");
                     var2 = null;
                     var5 = false;
                     break label77;
               }

               var2 = attType;
               var5 = false;
            } finally {
               if (var5) {
                  this.popName();
               }
            }

            this.popName();
            return var2;
         }

         this.popName();
         return var2;
      }

      this.popName();
      return attType;
   }

   private int reqDecimalNumber() {
      int numDigits = 0;
      int sum = 0;

      while (true) {
         int ch = this.peek();
         switch (ch) {
            case 47:
               if (numDigits == 0) {
                  this.fatalError("Expecting a number.");
               }

               return sum;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            default:
               numDigits++;
               this.advance();
               sum = sum * 10 + (ch - 48);
         }
      }
   }

   private int reqHexNumber() {
      int numDigits = 0;
      int sum = 0;

      while (true) {
         int ch = this.peek();
         switch (ch) {
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               numDigits++;
               this.advance();
               sum = sum * 16 + (ch - 48);
               break;
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
               numDigits++;
               this.advance();
               sum = sum * 16 + ch - 65 + 10;
               break;
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
               numDigits++;
               this.advance();
               sum = sum * 16 + ch - 97 + 10;
               break;
            default:
               if (numDigits == 0) {
                  this.fatalError("Expecting a number.");
               }

               return sum;
         }
      }
   }

   private void processCharReference() {
      this.advance();
      int ch;
      if (this.peek() == 120) {
         this.advance();
         ch = this.reqHexNumber();
      } else {
         ch = this.reqDecimalNumber();
      }

      this.expect(';');
      if (ch <= 65533) {
         this.addToName(ch);
         if (ch < 32) {
            switch (ch) {
               case 9:
               case 10:
               case 13:
                  return;
            }
         } else {
            if (ch < 55296) {
               return;
            }

            if (ch >= 57344) {
               return;
            }
         }
      } else if (ch >= 65536 && ch <= 1114111) {
         ch -= 65536;
         this.addToName((char)((ch >>> 10) + 55296));
         this.addToName((char)((ch & 1023) + 56320));
         return;
      }

      this.fatalError("Invalid character reference '%1'.", ((StringBuffer)(new Object("&#x"))).append(Integer.toHexString(ch)).toString());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void processReference() {
      XMLParser$InputReader refStart = this._lastReadInput;
      if (this.peek() == 35) {
         this.processCharReference();
         this.lastReadInputMustBe(refStart);
      } else if (this._inEntityValue) {
         this.addToName(38);
         boolean var20 = false /* VF: Semaphore variable */;

         int var24;
         char[] var26;
         try {
            var20 = true;
            this.pushName();
            this.reqName();
            var24 = this._nameLength;
            var26 = this._nameBuffer;
            var20 = false;
         } finally {
            if (var20) {
               this.popName();
            }
         }

         this.popName();

         for (int var27 = 0; var27 < var24; var27++) {
            this.addToName(var26[var27]);
         }

         this.expect(';');
         this.lastReadInputMustBe(refStart);
         this.addToName(59);
      } else {
         boolean informHandler = this._extHandler != null && !this._inAttValue && !this._inEntityValue;
         boolean var16 = false /* VF: Semaphore variable */;

         String entityName;
         try {
            var16 = true;
            this.pushName();
            this.reqName();
            entityName = this.nameToString();
            var16 = false;
         } finally {
            if (var16) {
               this.popName();
            }
         }

         this.popName();
         this.expect(';');
         this.lastReadInputMustBe(refStart);
         String entity = (String)this._internalEntities.get(entityName);
         new XMLParser$EndOfEntity(this);
         if (this._standalone && !this._inDTD && this._entitiesDeclaredInExternalSubset.get(entityName) != null) {
            this.fatalError("A standalone document may not reference entities declared in the external DTD subset.");
         }

         if (entity != null) {
            int len = entity.length();
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               this.pushName();
               if (this._inAttValue) {
                  for (int i = 0; i < len; i++) {
                     char ch = entity.charAt(i);
                     switch (ch) {
                        case '\t':
                        case '\n':
                        case '\r':
                           ch = ' ';
                        default:
                           this.addToName(ch);
                     }
                  }
               } else {
                  for (int i = 0; i < len; i++) {
                     this.addToName(entity.charAt(i));
                  }
               }

               entity = this.nameToString();
               var12 = false;
            } finally {
               if (var12) {
                  this.popName();
               }
            }

            this.popName();
            if (informHandler) {
               this.sendChars(false);
               this._extHandler.startEntityReference(entityName, null, null);
            }

            new XMLParser$EntityReader(this, entityName, entity, false);
         } else if (this._inAttValue) {
            this.fatalError("An external entity reference is not allowed in an attribute value.");
         } else {
            XMLParser$ExternalEntity extEnt = (XMLParser$ExternalEntity)this._externalEntities.get(entityName);
            InputSource is = null;
            if (extEnt == null) {
               if (this._standalone || this._foundToBeStandalone) {
                  this.fatalError("Undefined entity reference '%1'.", entityName);
               }
            } else {
               is = this._entityResolver.resolveEntity(extEnt.publicId, extEnt.systemId);
            }

            if (is == null) {
               this.sendChars(false);
               this._contentHandler.skippedEntity(entityName);
               this._in.pop();
               return;
            }

            if (informHandler) {
               this.sendChars(false);
               this._extHandler.startEntityReference(entityName, extEnt.publicId, extEnt.systemId);
            }

            this.pushExternalParsedEntity(entityName, false, extEnt.publicId, extEnt.systemId, is);
         }

         this.reqContent();
         if (informHandler) {
            this.sendChars(false);
            this._extHandler.endEntityReference(entityName);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void reqAttValue() {
      boolean saved = this._processPERefs;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         this._inAttValue = true;
         this._processPERefs = false;
         int quote = this.read();
         XMLParser$InputReader literalStartInput = this._lastReadInput;
         switch (quote) {
            default:
               this.fatalError("Expecting an attribute value.");
            case 34:
            case 39:
               this.clearName();
         }

         while (true) {
            int ch = this.read();
            if (ch == quote && (this._lastReadInput == literalStartInput || !this._lastReadInput.isEntity())) {
               this.lastReadInputMustBe(literalStartInput);
               var7 = false;
               break;
            }

            if (ch == 60) {
               this.fatalError("An attribute value may not contain '<'.");
            } else if (ch == 38) {
               this.processReference();
            } else {
               switch (ch) {
                  case 9:
                  case 10:
                  case 13:
                     this.addToName(32);
                     break;
                  default:
                     this.addToName(ch);
               }
            }
         }
      } finally {
         if (var7) {
            this._processPERefs = saved;
            this._inAttValue = false;
         }
      }

      this._processPERefs = saved;
      this._inAttValue = false;
   }

   private boolean putIgnoreDuplicates(Hashtable ht, Object key, Object value) {
      Object old = ht.put(key, value);
      if (old == null) {
         return true;
      }

      ht.put(key, old);
      return false;
   }

   private String reqDefaultDecl(boolean normalize) {
      if (this.peek() == 35) {
         this.advance();
         this.reqName();
         switch (this.kwLookup()) {
            case 8:
               this.reqSpace();
               break;
            case 22:
               return null;
            case 29:
               return null;
            default:
               this.fatalError("Expecting a default attribute value declaration.");
         }
      }

      this.reqAttValue();
      if (normalize) {
         this.normalizeAttributeValue();
      }

      return this.nameToString();
   }

   private void reqAttListDecl() {
      XMLParser$AttributeDefinitions attributeDefinitions = null;
      this.reqSpace();
      this.reqQName(false, "", true);
      String tagName = this.nameToString();
      this.optionalSpace();

      while (this.peek() != 62) {
         this.reqQName(true, "", true);
         this.reqSpace();
         String type = this.reqAttType();
         boolean isCData = "CDATA".equals(type);
         this.reqSpace();
         String defaultValue = this.reqDefaultDecl(!isCData);
         this.optionalSpace();
         if (attributeDefinitions == null) {
            attributeDefinitions = (XMLParser$AttributeDefinitions)this._attributeDeclarations.get(tagName);
         }

         if (attributeDefinitions == null) {
            attributeDefinitions = new XMLParser$AttributeDefinitions(this);
         }

         attributeDefinitions.addAttribute(this._qName, this._localName, this._prefixName, type, isCData, defaultValue);
         if (defaultValue != null && this._extHandler != null) {
            this._extHandler.defaultAttribute(tagName, this._qName, defaultValue);
         }
      }

      this.lastReadInputMustBeTokenStartInput();
      if (attributeDefinitions != null) {
         this._attributeDeclarations.put(tagName, attributeDefinitions);
      }

      this.advance();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void reqEntityValue() {
      this._inEntityValue = true;
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         int quote = this.read();
         XMLParser$InputReader literalStartInput = this._lastReadInput;
         switch (quote) {
            default:
               this.fatalError("Expecting an entity replacement value.");
            case 34:
            case 39:
               this.clearName();
         }

         while (true) {
            int ch = this.peek();
            if (ch == quote && (this._lastReadInput == literalStartInput || !this._lastReadInput.isEntity())) {
               this.advance();
               this.lastReadInputMustBe(literalStartInput);
               var6 = false;
               break;
            }

            if (ch == 38) {
               this.advance();
               this.processReference();
            } else {
               this.advance();
               this.addToName(ch);
            }
         }
      } finally {
         if (var6) {
            this._inEntityValue = false;
         }
      }

      this._inEntityValue = false;
   }

   private void reqEntityDecl() {
      this._processPERefs = false;
      this.reqSpace();
      boolean peDecl = false;
      if (this.peek() == 37) {
         this.advance();
         peDecl = true;
         this.reqSpace();
      }

      this._processPERefs = true;
      this.reqName();
      this.reqSpace();
      String name = this.nameToString();
      if (this.optionalExternalID()) {
         if (!peDecl) {
            if (this.reqNDataDecl()) {
               this._dtdHandler.unparsedEntityDecl(name, this._publicId, this._systemId, this.nameToString());
            } else {
               this._foundToBeStandalone = false;
               this.putIgnoreDuplicates(this._externalEntities, name, new XMLParser$ExternalEntity(this._publicId, this._systemId));
            }
         } else {
            this._foundToBeStandalone = false;
            this.putIgnoreDuplicates(this._externalParameterEntities, name, new XMLParser$ExternalEntity(this._publicId, this._systemId));
         }
      } else {
         this.reqEntityValue();
         String value = this.nameToString();
         if (peDecl) {
            this.putIgnoreDuplicates(this._parameterEntities, name, value);
         } else if (this.putIgnoreDuplicates(this._internalEntities, name, value)) {
            if (!this._inInternalSubset) {
               this._entitiesDeclaredInExternalSubset.put(name, name);
            }

            if (this._extHandler != null) {
               this._extHandler.entityDecl(name, value);
            }
         }
      }

      this.optionalSpace();
      this.expect('>');
      this.lastReadInputMustBeTokenStartInput();
   }

   private void reqNotationDecl() {
      this.optionalSpace();
      this.reqName();
      String name = this.nameToString();
      this.optionalSpace();
      this.reqExternalOrPublicID();
      this.optionalSpace();
      this.expect('>');
      this.lastReadInputMustBeTokenStartInput();
      if (this._publicId != null) {
         this.normalizeToName(this._publicId);
         this._publicId = this.nameToString();
      }

      this._dtdHandler.notationDecl(name, this._publicId, this._systemId);
   }

   private void ignoreSect() {
      XMLParser$InputReader sectionStartInput = this._tokenStartInput;
      this.optionalSpace();
      this.expect('[');
      int nested = 1;

      label38:
      while (true) {
         switch (this.read()) {
            case 60:
               if (this.read() == 33 && this.read() == 91) {
                  nested++;
               }
               break;
            case 93:
               if (this.read() == 93) {
                  while (true) {
                     switch (this.read()) {
                        case 62:
                           if (--nested == 0) {
                              this.lastReadInputMustBe(sectionStartInput);
                              return;
                           }
                           continue label38;
                        case 93:
                           break;
                        default:
                           continue label38;
                     }
                  }
               }
         }
      }
   }

   private void includeSect() {
      XMLParser$InputReader sectionStartInput = this._tokenStartInput;
      this.optionalSpace();
      this.expect('[');
      this.extSubsetDecl();
      this.expect(']');
      this.expect(']');
      this.expect('>');
      this.lastReadInputMustBe(sectionStartInput);
   }

   private void reqMarkupdecl(boolean allowConditional) {
      switch (this._tokenType) {
         case 3:
            this.reqPI();
            return;
         case 7:
            switch (this.kwLookup()) {
               case 12:
                  this.reqEntityDecl();
                  return;
               case 19:
                  this.reqAttListDecl();
                  return;
               case 21:
                  this.reqElementDecl();
                  return;
               case 28:
                  this.reqNotationDecl();
                  return;
               default:
                  this.fatalError("Expecting a markup declaration.");
                  return;
            }
         case 10:
            if (allowConditional) {
               this.includeSect();
               return;
            }
         case 11:
            if (allowConditional) {
               this.ignoreSect();
               return;
            }
         default:
            this.fatalError("Expecting a markup declaration.");
         case 2:
      }
   }

   private void extSubsetDecl() {
      while (true) {
         this._isDeclSep = true;
         this.optionalSpace();
         switch (this.peek()) {
            case -1:
               this.advance();
               return;
            case 60:
               this._isDeclSep = false;
               this.classifyToken();
               this.reqMarkupdecl(true);
               this._isDeclSep = false;
               break;
            default:
               return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean optionalDocTypeDecl() {
      if (this._tokenType != 7) {
         return false;
      }

      this._inDTD = true;
      this._processPERefs = true;
      this._inInternalSubset = true;
      boolean var9 = false /* VF: Semaphore variable */;

      boolean var11;
      label136: {
         boolean var12;
         try {
            var9 = true;
            if (this.kwLookup() != 20) {
               this.fatalError("Expecting '%1'.", this._kwTable[20]);
            }

            XMLParser$InputReader docTypeStart = this._tokenStartInput;
            this.optionalSpace();
            this.reqQName(false, "", true);
            String dtdName = this.nameToString();
            this.optionalSpace();
            if (this.peek() == 62) {
               this.advance();
               this.lastReadInputMustBe(docTypeStart);
               var11 = true;
               var9 = false;
               break label136;
            }

            String extSubsetSystemId = null;
            String extSubsetPublicId = null;
            if (this.optionalExternalID()) {
               extSubsetSystemId = this._systemId;
               extSubsetPublicId = this._publicId;
            }

            this.optionalSpace();
            String dtd = "";
            if (this._extHandler != null) {
               this._extHandler.startDTD();
            }

            if (this.peek() == 91) {
               this.advance();
               this._dtdBody = (StringBuffer)(new Object());

               label120:
               while (true) {
                  this._isDeclSep = true;
                  this.optionalSpace();
                  switch (this.peek()) {
                     case 60:
                        this._isDeclSep = false;
                        this.classifyToken();
                        this.reqMarkupdecl(false);
                        break;
                     case 93:
                        this.advance();
                        if (this._dtdBody.length() != 0) {
                           this._dtdBody.deleteCharAt(this._dtdBody.length() - 1);
                        }

                        dtd = this._dtdBody.toString();
                        this._dtdBody = null;
                        this._isDeclSep = false;
                        break label120;
                     default:
                        this.fatalError("Expecting '<' or ']'.");
                  }
               }
            }

            this.optionalSpace();
            this.expect('>');
            this.lastReadInputMustBe(docTypeStart);
            this._inInternalSubset = false;
            if (extSubsetSystemId != null) {
               if (this._extHandler != null) {
                  this._extHandler.endDTD(dtdName, extSubsetPublicId, extSubsetSystemId, dtd);
               }

               InputSource is = this._entityResolver.resolveEntity(extSubsetPublicId, extSubsetSystemId);
               if (is == null) {
                  this.nonFatalError("Cannot resolve external DTD subset '%1'.", extSubsetSystemId);
               } else {
                  new XMLParser$EndOfEntity(this);
                  this.pushExternalParsedEntity(null, false, extSubsetPublicId, extSubsetSystemId, is);
                  this.extSubsetDecl();
               }
            } else if (this._extHandler != null) {
               this._extHandler.endDTD(dtdName, null, null, dtd);
            }

            this._processPERefs = false;

            do {
               this.classifyToken();
            } while (this._tokenType == 1 || this._tokenType == 8);

            var12 = true;
            var9 = false;
         } finally {
            if (var9) {
               this._inDTD = false;
               this._processPERefs = false;
               this._inInternalSubset = false;
            }
         }

         this._inDTD = false;
         this._processPERefs = false;
         this._inInternalSubset = false;
         return var12;
      }

      this._inDTD = false;
      this._processPERefs = false;
      this._inInternalSubset = false;
      return var11;
   }

   private void reqProlog() {
      this.classifyToken();

      while (this.optionalMisc()) {
         this.classifyToken();
      }

      if (this.optionalDocTypeDecl()) {
         while (this.optionalMisc()) {
            this.classifyToken();
         }
      }
   }

   private void reqCDSect() {
      if (this.kwLookup() != 6) {
         this.fatalError("Expecting '%1'.", this._kwTable[6]);
      }

      this.expect('[');
      this.clearName();

      label58:
      while (true) {
         int ch = this.read();
         if (ch == 93) {
            ch = this.read();
            if (ch == 93) {
               int brackets = 2;

               while (true) {
                  ch = this.read();
                  switch (ch) {
                     case 62:
                        for (int i = 2; i < brackets; i++) {
                           this.addToName(93);
                        }

                        this.lastReadInputMustBeTokenStartInput();
                        if (this._extHandler != null) {
                           this._extHandler.cdataSection(this._nameBuffer, 0, this._nameLength);
                        } else {
                           this._contentHandler.characters(this._nameBuffer, 0, this._nameLength);
                        }

                        this.clearName();
                        this._trimLeadingSpace = false;
                        return;
                     case 93:
                        brackets++;
                        break;
                     default:
                        for (int i = 0; i < brackets; i++) {
                           this.addToName(93);
                        }

                        this.addToName(ch);
                        continue label58;
                  }
               }
            } else {
               this.addToName(93);
               this.addToName(ch);
            }
         } else {
            this.addToName(ch);
         }
      }
   }

   private void sendChars(boolean ignorableWhiteSpace) {
      if (this._nameLength != 0) {
         switch (this._firstNonSpace) {
            case -2:
               if (this._trimLeadingSpace) {
                  this._contentHandler.ignorableWhitespace(this._nameBuffer, 0, this._firstNonSpace);
               } else {
                  this._firstNonSpace = 0;
               }
            case 0:
               if (ignorableWhiteSpace) {
                  this._lastNonSpace++;
                  this._contentHandler.characters(this._nameBuffer, this._firstNonSpace, this._lastNonSpace - this._firstNonSpace);
                  if (this._lastNonSpace < this._nameLength) {
                     this._contentHandler.ignorableWhitespace(this._nameBuffer, this._lastNonSpace, this._nameLength - this._lastNonSpace);
                  }
               } else {
                  this._contentHandler.characters(this._nameBuffer, this._firstNonSpace, this._nameLength - this._firstNonSpace);
               }
               break;
            case -1:
            default:
               if (!ignorableWhiteSpace && !this._trimLeadingSpace) {
                  this._contentHandler.characters(this._nameBuffer, 0, this._nameLength);
               } else {
                  this._contentHandler.ignorableWhitespace(this._nameBuffer, 0, this._nameLength);
               }
         }

         this.clearName();
         this._trimLeadingSpace = ignorableWhiteSpace;
      }
   }

   private void reqContent() {
      while (true) {
         int ch = this.peek();
         switch (ch) {
            case -1:
               this.advance();
               return;
            case 38:
               this.advance();
               this.processReference();
               break;
            case 60:
               this.sendChars(this._tokenType != 12);
               this.classifyToken();
               switch (this._tokenType) {
                  case 2:
                     break;
                  case 3:
                     this.reqPI();
                     break;
                  case 4:
                     this.reqElement();
                     break;
                  case 5:
                     return;
                  case 8:
                     this.fatalError("Unexpected end of file.");
                  default:
                     this.fatalError("Unrecognized markup.");
                     break;
                  case 12:
                     this.reqCDSect();
               }

               this.clearName();
               break;
            case 93:
               this.addToName(ch);
               this.advance();
               ch = this.peek();
               if (ch != 93) {
                  break;
               }

               do {
                  this.addToName(ch);
                  this.advance();
                  ch = this.peek();
               } while (ch == 93);

               if (ch == 62) {
                  this.fatalError("Character data may not contain ']]>'.");
               }
               break;
            default:
               this.addToName(ch);
               this.advance();
         }
      }
   }

   private void normalizeToName(String value) {
      this.clearName();
      int len = value.length();

      int i;
      for (i = 0; i < len; i++) {
         char ch = value.charAt(i);
         if (ch != ' ' && ch != '\n') {
            break;
         }
      }

      label36:
      while (i < len) {
         char ch = value.charAt(i);
         i++;
         if (ch != ' ' && ch != '\n') {
            this.addToName(ch);
         } else {
            while (i != len) {
               ch = value.charAt(i);
               if (ch != ' ' && ch != '\n') {
                  this.addToName(32);
                  continue label36;
               }

               i++;
            }

            return;
         }
      }
   }

   private void normalizeAttributeValue() {
      this.normalizeToName(this.nameToString());
   }

   private SAXAttributesImpl addAttribute(SAXAttributesImpl attributes, XMLParser$AttributeDefinitions defs) {
      this.reqEq();
      this.reqAttValue();
      if (attributes == null) {
         attributes = new SAXAttributesImpl();
      }

      String type = "CDATA";
      if (defs != null) {
         XMLParser$AttributeDefinitions$Value value = defs.getValue(this._qName);
         if (value != null) {
            type = value.type;
            if (!value.isCData) {
               this.normalizeAttributeValue();
            }

            value.visited = true;
         }
      }

      attributes.addAttribute(this._uri, this._localName, this._qName, type, this.nameToString(), false);
      return attributes;
   }

   private Hashtable handleNamespace(Hashtable savedNameSpaces, String ns, String nsValue) {
      String oldUri = (String)this._nameSpaceLookup.get(ns);
      if (oldUri == null) {
         oldUri = "";
      }

      if (savedNameSpaces == null) {
         savedNameSpaces = (Hashtable)(new Object());
      }

      savedNameSpaces.put(ns, oldUri);
      this._contentHandler.startPrefixMapping(ns, nsValue);
      this._nameSpaceLookup.put(ns, nsValue);
      return savedNameSpaces;
   }

   private void addDefaultAttributes(XMLParser$AddDefaultAttributeParms parms) {
      SAXAttributesImpl attributes = parms.attributes;
      String thisElementURI = parms.uri;
      Hashtable savedNameSpaces = parms.savedNameSpaces;
      XMLParser$AttributeDefinitions attributeDefinitions = parms.attributeDefinitions;
      if (attributeDefinitions != null) {
         Enumeration e = attributeDefinitions.values();

         while (e.hasMoreElements()) {
            XMLParser$AttributeDefinitions$Value value = (XMLParser$AttributeDefinitions$Value)e.nextElement();
            if (!value.visited) {
               if (value.isDefaultNamespace) {
                  value.visited = true;
                  this._defaultURI = value.defaultValue;
                  if (value.prefixName.length() == 0) {
                     thisElementURI = this._defaultURI;
                  }
               } else if (value.isNamespace) {
                  value.visited = true;
                  savedNameSpaces = this.handleNamespace(savedNameSpaces, value.localName, value.defaultValue);
               }
            }
         }

         e = attributeDefinitions.values();

         while (e.hasMoreElements()) {
            XMLParser$AttributeDefinitions$Value value = (XMLParser$AttributeDefinitions$Value)e.nextElement();
            boolean visited = value.visited;
            value.visited = false;
            if (!visited && value.defaultValue != null) {
               String uri;
               if (value.prefixName.length() == 0) {
                  uri = "";
               } else {
                  uri = (String)this._nameSpaceLookup.get(value.prefixName);
                  if (uri == null) {
                     this.fatalError("Undefined namespace '%1'.", value.prefixName);
                  }
               }

               if (attributes == null) {
                  attributes = new SAXAttributesImpl();
               }

               attributes.addAttribute(uri, value.localName, value.qName, value.type, value.defaultValue, true);
            }
         }
      }

      if (attributes == null) {
         attributes = this._nullAttributes;
      }

      parms.attributes = attributes;
      parms.uri = thisElementURI;
      parms.savedNameSpaces = savedNameSpaces;
   }

   @Override
   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }

   @Override
   public void setAllowUndefinedNamespaces(boolean allowUndefinedNamespaces) {
      this._allowUndefinedNamespaces = allowUndefinedNamespaces;
   }

   private String lookupURI(String prefix, String uri) {
      if (uri != this._undefinedURI) {
         return uri;
      }

      uri = (String)this._nameSpaceLookup.get(prefix);
      if (uri != null) {
         return uri;
      }

      if (this._allowUndefinedNamespaces) {
         return this._undefinedURI;
      }

      this.fatalError("Undefined namespace '%1'.", prefix);
      return null;
   }

   private void resolveAttributes(SAXAttributesImpl attributes) {
      if (this._isNamespaceAware) {
         for (int i = attributes.getLength() - 1; i >= 0; i--) {
            String uri = attributes.getURI(i);
            if (uri == this._undefinedURI) {
               String prefix = attributes.getQName(i);
               prefix = prefix.substring(0, prefix.indexOf(58));
               attributes.setURI(i, this.lookupURI(prefix, uri));
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void reqElement() {
      if (this._tokenType != 4) {
         this.fatalError("Expecting an element.");
      }

      XMLParser$InputReader elementStartInput = this._tokenStartInput;
      String defaultURI = this._defaultURI;
      Hashtable savedNameSpaces = null;
      SAXAttributesImpl attributes = null;
      XMLParser$AttributeDefinitions attributeDefinitions = (XMLParser$AttributeDefinitions)this._attributeDeclarations.get(this._qName);
      this._duplicateAttributeCheck.clear();
      XMLParser$AddDefaultAttributeParms parms = new XMLParser$AddDefaultAttributeParms(null);
      boolean var20 = false /* VF: Semaphore variable */;

      label218: {
         try {
            var20 = true;
            String var7 = this._prefixName;
            String qName = this._qName;
            String localName = this._localName;
            String var10 = this._uri;
            if (this._uri == null) {
               this.fatalError("Undefined namespace prefix '%1'.", var7);
            }

            label208:
            while (true) {
               boolean hadSpace = this.optionalSpace();
               switch (this.peek()) {
                  case 47:
                     parms.attributes = attributes;
                     parms.uri = var10;
                     parms.savedNameSpaces = savedNameSpaces;
                     parms.attributeDefinitions = attributeDefinitions;
                     this.addDefaultAttributes(parms);
                     attributes = parms.attributes;
                     var10 = parms.uri;
                     savedNameSpaces = parms.savedNameSpaces;
                     var10 = this.lookupURI(var7, var10);
                     this.resolveAttributes(attributes);
                     if (this._extHandler != null) {
                        this._extHandler.startAndEndElement(var10, localName, qName, attributes);
                     } else {
                        this._contentHandler.startElement(var10, localName, qName, attributes);
                        this._contentHandler.endElement(var10, localName, qName);
                     }

                     this.advance();
                     this.expect('>');
                     this.lastReadInputMustBe(elementStartInput);
                     var20 = false;
                     break label208;
                  case 62:
                     parms.attributes = attributes;
                     parms.uri = var10;
                     parms.savedNameSpaces = savedNameSpaces;
                     parms.attributeDefinitions = attributeDefinitions;
                     this.addDefaultAttributes(parms);
                     attributes = parms.attributes;
                     var10 = parms.uri;
                     savedNameSpaces = parms.savedNameSpaces;
                     var10 = this.lookupURI(var7, var10);
                     this.resolveAttributes(attributes);
                     this._contentHandler.startElement(var10, localName, qName, attributes);
                     this.advance();
                     this.clearName();
                     this.reqContent();
                     if (!this.nameIs(qName)) {
                        this.fatalError("End tag does not match start tag '%1'.", qName);
                     }

                     this.optionalSpace();
                     this.expect('>');
                     this.lastReadInputMustBe(elementStartInput);
                     this._contentHandler.endElement(var10, localName, qName);
                     var20 = false;
                     break label218;
                  default:
                     if (!this._allowUndefinedNamespaces && !hadSpace) {
                        this.reqSpace();
                     }

                     this.reqQName(true, "", true);
                     if (this._duplicateAttributeCheck.put(this._qName, this._qName) != null) {
                        this.fatalError("Duplicate attribute '%1'.", this._qName);
                     }

                     String nsValue = null;
                     String ns = null;
                     if (this._prefixName.length() == 0) {
                        if (this._isNamespaceAware && this._localName.equals(this.XMLNS)) {
                           this.reqEq();
                           this.reqAttValue();
                           nsValue = this._defaultURI = this.nameToString();
                           ns = "";
                           if (var7.length() == 0) {
                              var10 = this._defaultURI;
                           }

                           if (attributeDefinitions != null) {
                              XMLParser$AttributeDefinitions$Value value = attributeDefinitions.getValue(this._qName);
                              if (value != null) {
                                 value.visited = true;
                              }
                           }
                        } else {
                           attributes = this.addAttribute(attributes, attributeDefinitions);
                        }
                     } else if (this._isNamespaceAware && this._prefixName.equals(this.XMLNS)) {
                        ns = this._localName;
                        this.reqEq();
                        this.reqAttValue();
                        nsValue = this.nameToString();
                        if (attributeDefinitions != null) {
                           XMLParser$AttributeDefinitions$Value value = attributeDefinitions.getValue(this._qName);
                           if (value != null) {
                              value.visited = true;
                           }
                        }
                     } else {
                        attributes = this.addAttribute(attributes, attributeDefinitions);
                     }

                     if (ns != null) {
                        savedNameSpaces = this.handleNamespace(savedNameSpaces, ns, nsValue);
                     }
               }
            }
         } finally {
            if (var20) {
               this._defaultURI = defaultURI;
               if (savedNameSpaces != null) {
                  Enumeration e = savedNameSpaces.keys();

                  while (e.hasMoreElements()) {
                     String prefix = (String)e.nextElement();
                     this._contentHandler.endPrefixMapping(prefix);
                     String uri = (String)savedNameSpaces.get(prefix);
                     if (uri.length() == 0) {
                        this._nameSpaceLookup.remove(prefix);
                     } else {
                        this._nameSpaceLookup.put(prefix, uri);
                     }
                  }
               }
            }
         }

         this._defaultURI = defaultURI;
         if (savedNameSpaces != null) {
            Enumeration e = savedNameSpaces.keys();

            while (e.hasMoreElements()) {
               String prefix = (String)e.nextElement();
               this._contentHandler.endPrefixMapping(prefix);
               String uri = (String)savedNameSpaces.get(prefix);
               if (uri.length() == 0) {
                  this._nameSpaceLookup.remove(prefix);
               } else {
                  this._nameSpaceLookup.put(prefix, uri);
               }
            }
         }

         return;
      }

      this._defaultURI = defaultURI;
      if (savedNameSpaces != null) {
         Enumeration e = savedNameSpaces.keys();

         while (e.hasMoreElements()) {
            String prefix = (String)e.nextElement();
            this._contentHandler.endPrefixMapping(prefix);
            String uri = (String)savedNameSpaces.get(prefix);
            if (uri.length() == 0) {
               this._nameSpaceLookup.remove(prefix);
            } else {
               this._nameSpaceLookup.put(prefix, uri);
            }
         }
      }
   }

   private void document() {
      this.reqProlog();
      this.reqElement();

      do {
         this.classifyToken();
      } while (this.optionalMisc());

      if (this._tokenType != 8) {
         this.fatalError("Expecting end of file.");
      }
   }

   int peek() {
      if (!this._havePeek) {
         this._peek = this._in.read();
         if (this._dtdBody != null) {
            this._dtdBody.append((char)this._peek);
         }

         this._havePeek = true;
      }

      return this._peek;
   }

   int read() {
      if (this._havePeek) {
         this._havePeek = false;
         return this._peek;
      }

      int ch = this._in.read();
      if (this._dtdBody != null) {
         this._dtdBody.append((char)ch);
      }

      return ch;
   }

   void advance() {
      this._havePeek = false;
   }

   private XMLParser$InputReader getLocator() {
      for (XMLParser$InputReader curr = this._in; curr != null; curr = curr.getNext()) {
         if (curr.getSystemId() != null) {
            return curr;
         }
      }

      return null;
   }

   @Override
   public String getPublicId() {
      XMLParser$InputReader curr = this.getLocator();
      return curr == null ? null : curr.getPublicId();
   }

   @Override
   public String getSystemId() {
      XMLParser$InputReader curr = this.getLocator();
      return curr == null ? null : curr.getSystemId();
   }

   @Override
   public int getLineNumber() {
      XMLParser$InputReader curr = this.getLocator();
      return curr == null ? -1 : curr.getLineNumber();
   }

   @Override
   public int getColumnNumber() {
      XMLParser$InputReader curr = this.getLocator();
      return curr == null ? -1 : curr.getColumnNumber();
   }

   private String getId(String uriStart, String name) {
      if (!name.startsWith(uriStart)) {
         throw new Object();
      } else {
         return name.substring(uriStart.length());
      }
   }

   @Override
   public boolean getFeature(String name) {
      name = this.getId("http://xml.org/sax/features/", name);
      if (name.equals("external-general-entities")) {
         return true;
      }

      if (name.equals("external-parameter-entities")) {
         return true;
      }

      if (name.equals("is-standalone")) {
         if (!this._parsing) {
            throw new Object();
         } else {
            return this._standalone;
         }
      } else if (name.equals("lexical-handler/parameter-entities")) {
         return false;
      } else if (name.equals("namespaces")) {
         return true;
      } else if (name.equals("namespace-prefixes")) {
         return true;
      } else if (name.equals("resolve-dtd-uris")) {
         return false;
      } else if (name.equals("string-interning")) {
         return false;
      } else if (name.equals("unicode-normalization-checking")) {
         return false;
      } else if (name.equals("use-attributes2")) {
         return false;
      } else if (name.equals("use-locator2")) {
         return false;
      } else if (name.equals("use-entity-resolver2")) {
         return false;
      } else if (name.equals("validation")) {
         return false;
      } else if (name.equals("xmlns-uris")) {
         return false;
      } else if (name.equals("xml-1.1")) {
         return false;
      } else {
         throw new Object();
      }
   }

   @Override
   public void setFeature(String name, boolean value) {
      name = this.getId("http://xml.org/sax/features/", name);
      if (name.equals("external-general-entities")) {
         throw new Object();
      } else if (name.equals("external-parameter-entities")) {
         throw new Object();
      } else if (name.equals("is-standalone")) {
         throw new Object();
      } else if (name.equals("lexical-handler/parameter-entities")) {
         throw new Object();
      } else if (name.equals("namespaces")) {
         throw new Object();
      } else if (name.equals("namespace-prefixes")) {
         throw new Object();
      } else if (name.equals("resolve-dtd-uris")) {
         throw new Object();
      } else if (name.equals("string-interning")) {
         throw new Object();
      } else if (name.equals("unicode-normalization-checking")) {
         throw new Object();
      } else if (name.equals("use-attributes2")) {
         throw new Object();
      } else if (name.equals("use-locator2")) {
         throw new Object();
      } else if (name.equals("use-entity-resolver2")) {
         throw new Object();
      } else if (name.equals("validation")) {
         throw new Object();
      } else if (name.equals("xmlns-uris")) {
         throw new Object();
      } else if (name.equals("xml-1.1")) {
         throw new Object();
      } else {
         throw new Object();
      }
   }

   private void getSetProperty(String name) {
      name = this.getId("http://xml.org/sax/properties/", name);
      if (name.equals("declaration-handler")) {
         throw new Object();
      } else if (name.equals("document-xml-version")) {
         throw new Object();
      } else if (name.equals("dom-node")) {
         throw new Object();
      } else if (name.equals("lexical-handler")) {
         throw new Object();
      } else if (name.equals("xml-string")) {
         throw new Object();
      } else {
         throw new Object();
      }
   }

   @Override
   public Object getProperty(String name) {
      this.getSetProperty(name);
      return null;
   }

   @Override
   public void setProperty(String name, Object value) {
      this.getSetProperty(name);
   }

   @Override
   public void setEntityResolver(EntityResolver resolver) {
      if (resolver == null) {
         throw new Object();
      }

      this._entityResolver = resolver;
   }

   @Override
   public EntityResolver getEntityResolver() {
      return this._entityResolver;
   }

   @Override
   public void setDTDHandler(DTDHandler handler) {
      if (handler == null) {
         throw new Object();
      }

      this._dtdHandler = handler;
   }

   @Override
   public DTDHandler getDTDHandler() {
      return this._dtdHandler;
   }

   @Override
   public void setContentHandler(ContentHandler handler) {
      if (handler == null) {
         throw new Object();
      }

      this._contentHandler = handler;
   }

   @Override
   public ContentHandler getContentHandler() {
      return this._contentHandler;
   }

   @Override
   public void setErrorHandler(ErrorHandler handler) {
      if (handler == null) {
         throw new Object();
      }

      this._errorHandler = handler;
   }

   @Override
   public ErrorHandler getErrorHandler() {
      return this._errorHandler;
   }

   @Override
   public void parse(InputSource input) {
      this.doParse(input);
   }

   @Override
   public void parse(String systemId) {
      this.parse((InputSource)(new Object(systemId)));
   }
}
