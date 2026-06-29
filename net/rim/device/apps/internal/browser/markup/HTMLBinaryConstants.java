package net.rim.device.apps.internal.browser.markup;

import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.util.Asserts;

public class HTMLBinaryConstants implements MarkupBinaryConstants {
   public static final int END = 0;
   public static final int STRING = 1;
   public static final int STRING_REF = 2;
   public static final int LITERAL = 3;
   public static final int UNKNOWN = 4;
   public static String CHARSET_IS8859_1 = "ISO-8859-1";
   public static String CHARSET_UTF_8 = "UTF-8";
   public static final int CHARSET_IS8859_CODE = 0;
   public static final int CHARSET_UTF_8_CODE = 1;
   public static final int THRESHOLD_FOR_TAG_NAMES = 5;
   public static final int THRESHOLD_FOR_ATTRIBUTE_NAMES_AND_VALUES = 5;
   public static final int THRESHOLD_FOR_ATTRIBUTE_NAMES = 80;
   public static final int THRESHOLD_FOR_INSTRUCTIONS = 255;
   public static final int FLAG_TAG_HAS_ATTRIBUTES = 1;
   public static final int FLAG_TAG_HAS_CONTENT = 2;
   public static final int FLAG_TAG_IN_ANCHOR = 4;
   public static final int FLAG_ATTRIBUTE_HAS_VALUE = 1;
   public static final int FLAG_ATTRIBUTE_VALUE_IS_TOKENIZED_ONE_BYTE = 2;
   public static final int FLAG_ATTRIBUTE_VALUE_IS_TOKENIZED_TWO_BYTES = 4;
   public static final int FLAG_ATTRIBUTE_VALUE_IS_TOKENIZED_THREE_BYTES = 6;
   public static final int FLAG_ATTRIBUTE_VALUE_IS_TOKENIZED_FOUR_BYTES = 8;
   public static final int FLAG_ATTRIBUTE_VALUE_IS_TOKENIZED_MASK = 14;
   public static final int T_A = 5;
   public static final int T_ABBR = 6;
   public static final int T_ACRONYM = 7;
   public static final int T_ADDRESS = 8;
   public static final int T_APPLET = 9;
   public static final int T_AREA = 10;
   public static final int T_B = 11;
   public static final int T_BASE = 12;
   public static final int T_BASEFONT = 13;
   public static final int T_BDO = 14;
   public static final int T_BIG = 15;
   public static final int T_BLOCKQUOTE = 16;
   public static final int T_BODY = 17;
   public static final int T_BR = 18;
   public static final int T_BUTTON = 19;
   public static final int T_CAPTION = 20;
   public static final int T_CENTER = 21;
   public static final int T_CITE = 22;
   public static final int T_CODE = 23;
   public static final int T_COL = 24;
   public static final int T_COLGROUP = 25;
   public static final int T_DD = 26;
   public static final int T_DEL = 27;
   public static final int T_DFN = 28;
   public static final int T_DIR = 29;
   public static final int T_DIV = 30;
   public static final int T_DL = 31;
   public static final int T_DT = 32;
   public static final int T_EM = 33;
   public static final int T_FIELDSET = 34;
   public static final int T_FONT = 35;
   public static final int T_FORM = 36;
   public static final int T_FRAME = 37;
   public static final int T_FRAMESET = 38;
   public static final int T_H1 = 39;
   public static final int T_H2 = 40;
   public static final int T_H3 = 41;
   public static final int T_H4 = 42;
   public static final int T_H5 = 43;
   public static final int T_H6 = 44;
   public static final int T_HEAD = 45;
   public static final int T_HR = 46;
   public static final int T_HTML = 47;
   public static final int T_I = 48;
   public static final int T_IFRAME = 49;
   public static final int T_IMG = 50;
   public static final int T_INPUT = 51;
   public static final int T_INS = 52;
   public static final int T_ISINDEX = 53;
   public static final int T_KBD = 54;
   public static final int T_LABEL = 55;
   public static final int T_LEGEND = 56;
   public static final int T_LI = 57;
   public static final int T_LINK = 58;
   public static final int T_MAP = 59;
   public static final int T_MENU = 60;
   public static final int T_META = 61;
   public static final int T_NOFRAMES = 62;
   public static final int T_NOSCRIPT = 63;
   public static final int T_OBJECT = 64;
   public static final int T_OL = 65;
   public static final int T_OPTGROUP = 66;
   public static final int T_OPTION = 67;
   public static final int T_P = 68;
   public static final int T_PARAM = 69;
   public static final int T_PLAINTEXT = 70;
   public static final int T_PRE = 71;
   public static final int T_Q = 72;
   public static final int T_S = 73;
   public static final int T_SAMP = 74;
   public static final int T_SCRIPT = 75;
   public static final int T_SELECT = 76;
   public static final int T_SMALL = 77;
   public static final int T_SPAN = 78;
   public static final int T_STRIKE = 79;
   public static final int T_STRONG = 80;
   public static final int T_STYLE = 81;
   public static final int T_SUB = 82;
   public static final int T_SUP = 83;
   public static final int T_TABLE = 84;
   public static final int T_TBODY = 85;
   public static final int T_TD = 86;
   public static final int T_TEXTAREA = 87;
   public static final int T_TFOOT = 88;
   public static final int T_TH = 89;
   public static final int T_THEAD = 90;
   public static final int T_TITLE = 91;
   public static final int T_TR = 92;
   public static final int T_TT = 93;
   public static final int T_U = 94;
   public static final int T_UL = 95;
   public static final int T_VAR = 96;
   public static final int T_BLINK = 97;
   public static final int T_MARQUEE = 98;
   public static final int T_EMBED = 99;
   public static final int T_NOEMBED = 100;
   public static final int THRESHOLD_MAX_TAG = 100;
   public static final int AV_ALIGN_TOP = 5;
   public static final int AV_ALIGN_MIDDLE = 6;
   public static final int AV_ALIGN_BOTTOM = 7;
   public static final int AV_ALIGN_LEFT = 8;
   public static final int AV_ALIGN_CENTER = 9;
   public static final int AV_ALIGN_RIGHT = 10;
   public static final int AV_ALIGN_JUSTIFY = 11;
   public static final int AV_ALIGN_CHAR = 12;
   public static final int AV_CLEAR_LEFT = 13;
   public static final int AV_CLEAR_ALL = 14;
   public static final int AV_CLEAR_RIGHT = 15;
   public static final int AV_CLEAR_NONE = 16;
   public static final int AV_DIR_LTR = 17;
   public static final int AV_DIR_RTL = 18;
   public static final int AV_FRAMEBORDER_1 = 19;
   public static final int AV_FRAMEBORDER_0 = 20;
   public static final int AV_METHOD_GET = 21;
   public static final int AV_METHOD_POST = 22;
   public static final int AV_SCROLLING_YES = 23;
   public static final int AV_SCROLLING_NO = 24;
   public static final int AV_SCROLLING_AUTO = 25;
   public static final int AV_TYPE_PASSWORD = 26;
   public static final int AV_TYPE_CHECKBOX = 27;
   public static final int AV_TYPE_RADIO = 28;
   public static final int AV_TYPE_SUBMIT = 29;
   public static final int AV_TYPE_RESET = 30;
   public static final int AV_TYPE_FILE = 31;
   public static final int AV_TYPE_HIDDEN = 32;
   public static final int AV_TYPE_IMAGE = 33;
   public static final int AV_TYPE_BUTTON = 34;
   public static final int AV_TYPE_TEXT = 35;
   public static final int AV_VALIGN_TOP = 36;
   public static final int AV_VALIGN_MIDDLE = 37;
   public static final int AV_VALIGN_BOTTOM = 38;
   public static final int AV_VALIGN_BASELINE = 39;
   public static final int AV_RULES_NONE = 40;
   public static final int AV_RULES_GROUPS = 41;
   public static final int AV_RULES_ROWS = 42;
   public static final int AV_RULES_COLS = 43;
   public static final int AV_RULES_ALL = 44;
   public static final int AV_FRAME_VOID = 45;
   public static final int AV_FRAME_ABOVE = 46;
   public static final int AV_FRAME_BELOW = 47;
   public static final int AV_FRAME_HSIDES = 48;
   public static final int AV_FRAME_LHS = 49;
   public static final int AV_FRAME_RHS = 50;
   public static final int AV_FRAME_VSIDES = 51;
   public static final int AV_FRAME_BOX = 52;
   public static final int AV_FRAME_BORDER = 53;
   public static final int AV_BEHAVIOR_SCROLL = 54;
   public static final int AV_BEHAVIOR_SLIDE = 55;
   public static final int AV_BEHAVIOR_ALTERNATE = 56;
   public static final int AV_DIRECTION_RIGHT = 57;
   public static final int AV_DIRECTION_LEFT = 58;
   public static final int AV_DIRECTION_UP = 59;
   public static final int AV_DIRECTION_DOWN = 60;
   public static final int AV_BGPROPERTIES_FIXED = 61;
   public static final int AV_HIDDEN_TRUE = 62;
   public static final int AV_HIDDEN_FALSE = 63;
   public static final int A_ABBR = 80;
   public static final int A_ACCEPT = 81;
   public static final int A_ACCEPT_CHARSET = 82;
   public static final int A_ACCESSKEY = 83;
   public static final int A_ACTION = 84;
   public static final int A_ALIGN = 85;
   public static final int A_ALINK = 86;
   public static final int A_ALT = 87;
   public static final int A_ARCHIVE = 88;
   public static final int A_AXIS = 89;
   public static final int A_BACKGROUND = 90;
   public static final int A_BGCOLOR = 91;
   public static final int A_BORDER = 92;
   public static final int A_CELLPADDING = 93;
   public static final int A_CELLSPACING = 94;
   public static final int A_CHAR = 95;
   public static final int A_CHAROFF = 96;
   public static final int A_CHARSET = 97;
   public static final int A_CHECKED = 98;
   public static final int A_CITE = 99;
   public static final int A_CLASS = 100;
   public static final int A_CLASSID = 101;
   public static final int A_CLEAR = 102;
   public static final int A_CODE = 103;
   public static final int A_CODEBASE = 104;
   public static final int A_CODETYPE = 105;
   public static final int A_COLOR = 106;
   public static final int A_COLS = 107;
   public static final int A_COLSPAN = 108;
   public static final int A_COMPACT = 109;
   public static final int A_CONTENT = 110;
   public static final int A_COORDS = 111;
   public static final int A_DATA = 112;
   public static final int A_DATETIME = 113;
   public static final int A_DECLARE = 114;
   public static final int A_DEFER = 115;
   public static final int A_DIR = 116;
   public static final int A_DISABLED = 117;
   public static final int A_ENCTYPE = 118;
   public static final int A_FACE = 119;
   public static final int A_FOR = 120;
   public static final int A_FRAME = 121;
   public static final int A_FRAMEBORDER = 122;
   public static final int A_HEADERS = 123;
   public static final int A_HEIGHT = 124;
   public static final int A_HREF = 125;
   public static final int A_HREFLANG = 126;
   public static final int A_HSPACE = 127;
   public static final int A_HTTP_EQUIV = 128;
   public static final int A_ID = 129;
   public static final int A_ISMAP = 130;
   public static final int A_LABEL = 131;
   public static final int A_LANG = 132;
   public static final int A_LANGUAGE = 133;
   public static final int A_LINK = 134;
   public static final int A_LONGDESC = 135;
   public static final int A_MARGINHEIGHT = 136;
   public static final int A_MARGINWIDTH = 137;
   public static final int A_MAXLENGTH = 138;
   public static final int A_MEDIA = 139;
   public static final int A_METHOD = 140;
   public static final int A_MULTIPLE = 141;
   public static final int A_NAME = 142;
   public static final int A_NOHREF = 143;
   public static final int A_NORESIZE = 144;
   public static final int A_NOSHADE = 145;
   public static final int A_NOWRAP = 146;
   public static final int A_OBJECT = 147;
   public static final int A_ONBLUR = 148;
   public static final int A_ONCHANGE = 149;
   public static final int A_ONCLICK = 150;
   public static final int A_ONDBLCLICK = 151;
   public static final int A_ONFOCUS = 152;
   public static final int A_ONKEYDOWN = 153;
   public static final int A_ONKEYPRESS = 154;
   public static final int A_ONKEYUP = 155;
   public static final int A_ONLOAD = 156;
   public static final int A_ONMOUSEDOWN = 157;
   public static final int A_ONMOUSEMOVE = 158;
   public static final int A_ONMOUSEOUT = 159;
   public static final int A_ONMOUSEOVER = 160;
   public static final int A_ONMOUSEUP = 161;
   public static final int A_ONRESET = 162;
   public static final int A_ONSELECT = 163;
   public static final int A_ONSUBMIT = 164;
   public static final int A_ONUNLOAD = 165;
   public static final int A_PROFILE = 166;
   public static final int A_PROMPT = 167;
   public static final int A_READONLY = 168;
   public static final int A_REL = 169;
   public static final int A_REV = 170;
   public static final int A_ROWS = 171;
   public static final int A_ROWSPAN = 172;
   public static final int A_RULES = 173;
   public static final int A_SCHEME = 174;
   public static final int A_SCOPE = 175;
   public static final int A_SCROLLING = 176;
   public static final int A_SELECTED = 177;
   public static final int A_SHAPE = 178;
   public static final int A_SIZE = 179;
   public static final int A_SPAN = 180;
   public static final int A_SRC = 181;
   public static final int A_STANDBY = 182;
   public static final int A_START = 183;
   public static final int A_STYLE = 184;
   public static final int A_SUMMARY = 185;
   public static final int A_TABINDEX = 186;
   public static final int A_TARGET = 187;
   public static final int A_TEXT = 188;
   public static final int A_TITLE = 189;
   public static final int A_TYPE = 190;
   public static final int A_USEMAP = 191;
   public static final int A_VALIGN = 192;
   public static final int A_VALUE = 193;
   public static final int A_VALUETYPE = 194;
   public static final int A_VERSION = 195;
   public static final int A_VLINK = 196;
   public static final int A_VSPACE = 197;
   public static final int A_WIDTH = 198;
   public static final int A_CTI = 199;
   public static final int A_BEHAVIOR = 200;
   public static final int A_DIRECTION = 201;
   public static final int A_LOOP = 202;
   public static final int A_SCROLLAMOUNT = 203;
   public static final int A_SCROLLDELAY = 204;
   public static final int A_BGPROPERTIES = 205;
   public static final int A_HIDDEN = 206;
   public static final int A_LOWSRC = 207;
   private static final String[] _COLOR_NAMES = new String[]{
      "aliceblue",
      "antiquewhite",
      "aqua",
      "aquamarine",
      "azure",
      "beige",
      "bisque",
      "black",
      "blanchedalmond",
      "blue",
      "blueviolet",
      "brown",
      "burlywood",
      "cadetblue",
      "chartreuse",
      "chocolate",
      "coral",
      "cornflowerblue",
      "cornsilk",
      "crimson",
      "cyan",
      "darkblue",
      "darkcyan",
      "darkgoldenrod",
      "darkgray",
      "darkgreen",
      "darkgrey",
      "darkkhaki",
      "darkmagenta",
      "darkolivegreen",
      "darkorange",
      "darkorchid",
      "darkred",
      "darksalmon",
      "darkseagreen",
      "darkslateblue",
      "darkslategray",
      "darkslategrey",
      "darkturquoise",
      "darkviolet",
      "deeppink",
      "deepskyblue",
      "dimgray",
      "dimgrey",
      "dodgerblue",
      "firebrick",
      "floralwhite",
      "forestgreen",
      "fuchsia",
      "gainsboro",
      "ghostwhite",
      "gold",
      "goldenrod",
      "gray",
      "green",
      "greenyellow",
      "grey",
      "honeydew",
      "hotpink",
      "indianred",
      "indigo",
      "ivory",
      "khaki",
      "lavender",
      "lavenderblush",
      "lawngreen",
      "lemonchiffon",
      "lightblue",
      "lightcoral",
      "lightcyan",
      "lightgoldenrodyellow",
      "lightgray",
      "lightgreen",
      "lightgrey",
      "lightpink",
      "lightsalmon",
      "lightseagreen",
      "lightskyblue",
      "lightslategray",
      "lightslategrey",
      "lightsteelblue",
      "lightyellow",
      "lime",
      "limegreen",
      "linen",
      "magenta",
      "maroon",
      "mediumaquamarine",
      "mediumblue",
      "mediumorchid",
      "mediumpurple",
      "mediumseagreen",
      "mediumslateblue",
      "mediumspringgreen",
      "mediumturquoise",
      "mediumvioletred",
      "midnightblue",
      "mintcream",
      "mistyrose",
      "moccasin",
      "navajowhite",
      "navy",
      "oldlace",
      "olive",
      "olivedrab",
      "orange",
      "orangered",
      "orchid",
      "palegoldenrod",
      "palegreen",
      "paleturquoise",
      "palevioletred",
      "papayawhip",
      "peachpuff",
      "peru",
      "pink",
      "plum",
      "powderblue",
      "purple",
      "red",
      "rosybrown",
      "royalblue",
      "saddlebrown",
      "salmon",
      "sandybrown",
      "seagreen",
      "seashell",
      "sienna",
      "silver",
      "skyblue",
      "slateblue",
      "slategray",
      "slategrey",
      "snow",
      "springgreen",
      "steelblue",
      "tan",
      "teal",
      "thistle",
      "tomato",
      "turquoise",
      "violet",
      "wheat",
      "white",
      "whitesmoke",
      "yellow",
      "yellowgreen"
   };
   private static final int[] _COLOR_VALUES = new int[]{
      15792383,
      16444375,
      65535,
      8388564,
      15794175,
      16119260,
      16770244,
      0,
      16772045,
      255,
      9055202,
      10824234,
      14596231,
      6266528,
      8388352,
      13789470,
      16744272,
      6591981,
      16775388,
      14423100,
      65535,
      139,
      35723,
      12092939,
      11119017,
      25600,
      11119017,
      12433259,
      9109643,
      5597999,
      16747520,
      10040012,
      9109504,
      15308410,
      9419919,
      4734347,
      3100495,
      3100495,
      52945,
      9699539,
      16716947,
      49151,
      6908265,
      6908265,
      2003199,
      11674146,
      16775920,
      2263842,
      16711935,
      14474460,
      16316671,
      16766720,
      14329120,
      8421504,
      32768,
      11403055,
      8421504,
      15794160,
      16738740,
      13458524,
      4915330,
      16777200,
      15787660,
      15132410,
      16773365,
      8190976,
      16775885,
      11393254,
      15761536,
      14745599,
      16448210,
      13882323,
      9498256,
      13882323,
      16758465,
      16752762,
      2142890,
      8900346,
      7833753,
      7833753,
      11584734,
      16777184,
      65280,
      3329330,
      16445670,
      16711935,
      8388608,
      6737322,
      205,
      12211667,
      9662683,
      3978097,
      8087790,
      64154,
      4772300,
      13047173,
      1644912,
      16121850,
      16770273,
      16770229,
      16768685,
      128,
      16643558,
      8421376,
      7048739,
      16753920,
      16729344,
      14315734,
      15657130,
      10025880,
      11529966,
      14381203,
      16773077,
      16767673,
      13468991,
      16761035,
      14524637,
      11591910,
      8388736,
      16711680,
      12357519,
      4286945,
      9127187,
      16416882,
      16032864,
      3050327,
      16774638,
      10506797,
      12632256,
      8900331,
      6970061,
      7372944,
      7372944,
      16775930,
      65407,
      4620980,
      13808780,
      32896,
      14204888,
      16737095,
      4251856,
      15631086,
      16113331,
      16777215,
      16119285,
      16776960,
      10145074,
      16777472,
      671511817,
      1631839568,
      16803222,
      1688366946,
      23855360,
      -445822619,
      599328672,
      23855360,
      1721794405,
      599328672,
      712179968,
      527827200,
      16810638,
      -2104615050,
      1979777215,
      6646639,
      1802466817,
      1952661861,
      1979777052,
      1097165679,
      1633117294,
      1979777027,
      1097165679,
      -1026723728,
      1644910241,
      1870004480,
      21390699,
      1979777124,
      1281715055,
      16780049,
      1701539702,
      257191503,
      1979777035,
      1382378351,
      1650552482,
      -1258225538,
      1761870021,
      100723743,
      7938075,
      2032147206,
      724820,
      416518,
      1694657542,
      134219776,
      1080303910,
      -1549372572,
      1948346732,
      106655301,
      -445822620,
      1076234240,
      13980276,
      1950361096,
      134245542,
      -982237146,
      134225510,
      8288550,
      555427848,
      671612935,
      272842608,
      1885038,
      1064314888,
      671613171,
      1190346608,
      -1136431840,
      1533953418,
      134251620,
      1929777707,
      1450576976,
      134242827,
      1869832769,
      1432698732,
      134237266,
      -1183554751,
      1697402469,
      1665206272,
      1415952756,
      6634344,
      1953251592,
      1816201216,
      1085809524,
      1849755648,
      1867520406,
      134282611,
      -848269247,
      1091043522,
      -1771998092,
      7572077,
      1953775880,
      1063414130,
      1091043429,
      1655138688,
      1756490110,
      1107820660,
      1600771,
      1828930056,
      16471832,
      1828930056,
      1869848600,
      134219694,
      409797442,
      1953018345,
      423757824,
      1953448678,
      134245808,
      1290148162,
      7628389,
      -434552312,
      1953018194,
      423757824,
      14570726,
      1998602760,
      -1136456845,
      1107820682,
      192116512,
      1695574096,
      1768296515,
      12742002,
      1998602760,
      -447739021,
      14138435,
      1953448456,
      134245808,
      1688366914,
      1107820659,
      1917806990,
      1107820774,
      8283298,
      1823097352,
      10561652,
      1806516744,
      1688346727,
      1666214,
      2042446344,
      -1052637184,
      540756,
      1980777224,
      134220586,
      1342324803,
      16610159,
      -412335352,
      1681944418,
      1816332288,
      -2054917913,
      1124597876,
      1953369262,
      1867568980,
      134282611,
      427011651,
      -42766512,
      -1136457728,
      -954118774,
      1124597771,
      2035583676,
      134243696,
      1468709955,
      1124598011,
      7572156,
      -1967373560,
      1124597929,
      -1310094660,
      134247528,
      14138435,
      -675527928,
      7672653,
      -675527928,
      1131746125,
      134272956,
      -1552997820,
      743312347,
      1141375077,
      -1992012465,
      134253199,
      1634100548,
      7629941,
      1717912584,
      1953264993,
      3896393,
      1717912584,
      1953264993,
      600017747,
      1698957312,
      1819631974,
      1644910196,
      1139174997,
      1668014,
      1717912584,
      1953264993,
      1650561111,
      1745965182,
      1229789510,
      2035565901,
      134243696,
      1634100548,
      -1502319499,
      1701407599,
      1698957312,
      8546174,
      8537096,
      1889158152,
      743340455,
      1141375077,
      2041016474,
      1132356193,
      134253244,
      -983273915,
      134225510,
      426930757,
      1917126656,
      111548786,
      21366784,
      1644910180,
      424019968,
      134222189,
      292362566,
      12219457,
      745686536,
      1174929509,
      1823655029,
      6630419,
      12338696,
      1712080904,
      1397229568,
      134219762,
      1886680136,
      1225261248,
      134221923,
      15035721,
      -445822712,
      7107157,
      -445822712,
      1634684019,
      134243334,
      1944415561,
      1867280212,
      1275592741,
      1344211715,
      2020173324,
      6581829,
      1650543624,
      1275592843,
      16218465,
      2036419592,
      -1025751049,
      1699481600,
      1472161377,
      13920259,
      1717914632,
      1309147252,
      -551588583,
      1886999587,
      1309147237,
      134243628,
      1729257806,
      1309147330,
      1832931471,
      1309147251,
      1943360143,
      -1890711552,
      7564454,
      108154632,
      1325924539,
      520204658,
      9074245,
      8933128,
      101929480,
      1886405051,
      134267597,
      -1157229742,
      1936172870,
      324143104,
      -2008040698,
      1376256115,
      1959880995,
      5001813,
      2002866696,
      1819636640,
      1376256116,
      1950361250,
      -1571682304,
      6581829,
      1756451336,
      1376256116,
      561009621,
      324208640,
      1393033446,
      1633947682,
      1280464173,
      197599565,
      1632831488,
      134226806,
      319578963,
      1750272000,
      1180961889,
      7563175,
      1149915912,
      1393033346,
      1227080587,
      134276461,
      600017747,
      1393033402,
      1393033452,
      12806893,
      2114147336,
      1079248896,
      1409810440,
      1097153384,
      1769108596,
      1399144290,
      1409810434,
      1097153384,
      1225548151,
      134276461,
      1698392148,
      409797442,
      1750337536,
      1409810586,
      6647929,
      1151554568,
      -682330162,
      2900048,
      1321686024,
      1948804396,
      -699136000,
      6555227,
      14111752,
      -1445506040,
      -564918272,
      -564918272,
      1697411654,
      197599565,
      1381304320,
      1426587724,
      56052818,
      1230174056,
      742412365,
      1426587763,
      1886404969,
      134267597,
      1850042709,
      6619495,
      527324424,
      1085809524,
      1918175232,
      1426587756,
      1631808626,
      134243734,
      1091269461,
      134253159,
      1935805270,
      1398147072,
      134219762,
      1651087958,
      16471934,
      1771722248,
      -1310097822,
      134247528,
      1443260759,
      1867372905,
      1460142086,
      1948781456,
      2059787,
      194008840,
      1846244392,
      1885684767,
      2298321,
      16471816,
      -973383928,
      453405015,
      134252623,
      1745049468,
      1971745832,
      2080899173,
      1114112855,
      561934191,
      6649222,
      134250248,
      -1972770939,
      -1811414925,
      477303668,
      1298997318,
      1413827913,
      6647929,
      -1972071416,
      1738541056,
      1280464156,
      1738541056,
      1802479132,
      134243689,
      -1508087904,
      1701407599,
      134235209,
      -1368427616,
      -1610088441,
      128872307,
      2120524354,
      1939867648,
      1124576879,
      134228391,
      275805088,
      435841,
      1970511880,
      134247532,
      1666214,
      1399629320,
      134226288,
      1768648614,
      -1509425051,
      1701407599,
      134235209,
      1768648614,
      1745049445,
      -1509424940,
      -1975817053,
      1085812065,
      -1475870605,
      134247384,
      -1308098391,
      1697387372,
      1974929408,
      -1207435163,
      2321186,
      -1388267512,
      1850018676,
      8676443,
      134265352,
      -1089994561,
      -2123404221,
      1282364443,
      134267529,
      1745049535,
      -1089994540,
      7643814,
      134266888,
      -831372864,
      -1073217415,
      192155984,
      134247278,
      1992389,
      1818674440,
      1968268545
   };
   private static final int[] _COLOR_INDICES = new int[]{
      0,
      5,
      13,
      21,
      45,
      45,
      49,
      57,
      59,
      62,
      62,
      63,
      85,
      100,
      102,
      108,
      119,
      119,
      122,
      136,
      141,
      141,
      142,
      145,
      145,
      147,
      -804650982,
      0,
      6,
      16,
      22,
      29,
      31,
      36,
      36,
      45,
      51,
      51,
      52,
      56,
      60,
      63,
      67,
      71,
      72,
      72,
      83,
      93,
      95,
      96,
      96,
      96,
      96,
      -804650982,
      0,
      8,
      12,
      16,
      22,
      22,
      33,
      33,
      35,
      35,
      35,
      35,
      35,
      37,
      37,
      37,
      37,
      37,
      42,
      45,
      55,
      55,
      59,
      59,
      59,
      59,
      -804650982,
      0,
      10,
      10,
      15,
      16,
      21,
      24,
      24,
      32,
      35,
      35,
      35,
      37,
      44,
      47,
      55,
      56,
      56,
      56,
      63,
      79,
      79,
      81
   };

   public static String resolveStringEncoding(int code) {
      switch (code) {
         case 1:
            return CHARSET_UTF_8;
         default:
            return CHARSET_IS8859_1;
      }
   }

   public static int resolveColor(String name) {
      return name == null ? -1 : resolveColor(name, 0, name.length());
   }

   public static int resolveColor(String name, int startIndex, int endIndex) {
      int length = endIndex - startIndex;
      Asserts.productionArgumentAssert(name != null && length > 0);
      if (name.charAt(startIndex) == '#') {
         label67:
         try {
            return NumberUtilities.parseInt(name, startIndex + 1, endIndex, 16);
         } finally {
            break label67;
         }
      } else {
         int color = resolveToken(name, startIndex, endIndex, _COLOR_NAMES, _COLOR_VALUES, _COLOR_INDICES);
         if (color != -1) {
            return color;
         }

         if (length == 6) {
            label63:
            try {
               return NumberUtilities.parseInt(name, startIndex, endIndex, 16);
            } finally {
               break label63;
            }
         }
      }

      Asserts.productionArgumentAssert(false);
      return -1;
   }

   protected static int resolveToken(String name, String[] names, int[] tokens, int[] indices) {
      return resolveToken(name, 0, name.length(), names, tokens, indices);
   }

   protected static int resolveToken(String name, int startIndex, int endIndex, String[] names, int[] tokens, int[] indices) {
      char item = name.charAt(startIndex);
      if (item >= 'A' && item <= 'Z') {
         item = (char)(item - 'A');
      } else {
         if (item < 'a' || item > 'z') {
            return -1;
         }

         item = (char)(item - 'a');
      }

      int index = indices[item];
      int count = names.length;
      int length = endIndex - startIndex;
      if (item < 25) {
         count = indices[item + 1];
      }

      while (index < count) {
         if (names[index].length() == length && StringUtilities.regionMatches(names[index], true, 0, name, startIndex, length, 1701707776)) {
            return tokens[index];
         }

         index++;
      }

      return -1;
   }
}
