/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/** */
public class HtmlTools {
	public static class IndexPair {
		public boolean mIsAlreadyAppended = false;
		public boolean mIsTag;
		public int originalEnd;
		public int originalStart;
		public int replacedEnd;
		public int replacedStart;

		/**
		 * @param pIsTag
		 */
		public IndexPair(final int pOriginalStart, final int pOriginalEnd, final int pReplacedStart,
		                 final int pReplacedEnd, final boolean pIsTag) {
			super();
			originalStart = pOriginalStart;
			originalEnd = pOriginalEnd;
			replacedStart = pReplacedStart;
			replacedEnd = pReplacedEnd;
			mIsTag = pIsTag;
		}

		/**
		 * generated by CodeSugar http:
		 */
		@Override
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			buffer.append("[IndexPair:");
			buffer.append(" originalStart: ");
			buffer.append(originalStart);
			buffer.append(" originalEnd: ");
			buffer.append(originalEnd);
			buffer.append(" replacedStart: ");
			buffer.append(replacedStart);
			buffer.append(" replacedEnd: ");
			buffer.append(replacedEnd);
			buffer.append(" is a tag: ");
			buffer.append(mIsTag);
			buffer.append("]");
			return buffer.toString();
		}
	}

	private static final Pattern FIND_TAGS_PATTERN = Pattern.compile("([^<]*)(<[^>]+>)");
	private static final Pattern HTML_PATTERN = Pattern.compile("(?s)^\\s*<\\s*html.*?>.*");
	private static Pattern[] PATTERNS;
	private static HtmlTools sInstance = new HtmlTools();
	private static final Pattern SLASHED_TAGS_PATTERN = Pattern.compile("<((" + "br|area|base|basefont|"
	        + "bgsound|button|col|colgroup|embed|hr" + "|img|input|isindex|keygen|link|meta"
	        + "|object|plaintext|spacer|wbr" + ")(\\s[^>]*)?)/>");
	private static final Pattern TAGS_PATTERN = Pattern.compile("(?s)<[^><]*>");

	public static HtmlTools getInstance() {
		return HtmlTools.sInstance;
	}

	public static String htmlToPlain(final String text) {
		return HtmlTools.htmlToPlain(text, /* strictHTMLOnly= */true);
	}

	public static String htmlToPlain(final String text, final boolean strictHTMLOnly) {
		if (strictHTMLOnly && !HtmlTools.isHtmlNode(text)) {
			return text;
		}
		if (PATTERNS == null) {
			PATTERNS = new Pattern[] { Pattern.compile("(?ims)[\n\t]"), Pattern.compile("(?ims) +"),
			        Pattern.compile("(?ims)<br.*?>"), Pattern.compile("(?ims)<p.*?>"),
			        Pattern.compile("(?ims)<div.*?>"), Pattern.compile("(?ims)<tr.*?>"),
			        Pattern.compile("(?ims)<dt.*?>"), Pattern.compile("(?ims)<dd.*?>"),
			        Pattern.compile("(?ims)<td.*?>"), Pattern.compile("(?ims)<[uo]l.*?>"),
			        Pattern.compile("(?ims)<li.*?>"), Pattern.compile("(?ims) *</[^>]*>"),
			        Pattern.compile("(?ims)<[^/][^>]*> *"), Pattern.compile("^\n+"), Pattern.compile("(?ims)&lt;"),
			        Pattern.compile("(?ims)&gt;"), Pattern.compile("(?ims)&quot;"), Pattern.compile("(?ims)&nbsp;"),
			        Pattern.compile("(?ims)&amp;") };
		}
		String intermediate = text;
		intermediate = PATTERNS[0].matcher(intermediate).replaceAll("");
		intermediate = PATTERNS[1].matcher(intermediate).replaceAll(" ");
		intermediate = PATTERNS[2].matcher(intermediate).replaceAll("\n");
		intermediate = PATTERNS[3].matcher(intermediate).replaceAll("\n\n");
		intermediate = PATTERNS[4].matcher(intermediate).replaceAll("\n");
		intermediate = PATTERNS[5].matcher(intermediate).replaceAll("\n");
		intermediate = PATTERNS[6].matcher(intermediate).replaceAll("\n");
		intermediate = PATTERNS[7].matcher(intermediate).replaceAll("\n   ");
		intermediate = PATTERNS[8].matcher(intermediate).replaceAll(" ");
		intermediate = PATTERNS[9].matcher(intermediate).replaceAll("\n");
		intermediate = PATTERNS[10].matcher(intermediate).replaceAll("\n   * ");
		intermediate = PATTERNS[11].matcher(intermediate).replaceAll("");
		intermediate = PATTERNS[12].matcher(intermediate).replaceAll("");
		intermediate = PATTERNS[13].matcher(intermediate).replaceAll("");
		intermediate = intermediate.trim();
		intermediate = HtmlTools.unescapeHTMLUnicodeEntity(intermediate);
		intermediate = PATTERNS[14].matcher(intermediate).replaceAll("<");
		intermediate = PATTERNS[15].matcher(intermediate).replaceAll(">");
		intermediate = PATTERNS[16].matcher(intermediate).replaceAll("\"");
		intermediate = PATTERNS[17].matcher(intermediate).replaceAll(" ");
		intermediate = PATTERNS[18].matcher(intermediate).replaceAll("&");
		return intermediate;
	}

	/**
	 */
	public static boolean isHtmlNode(final String text) {
		for (int i = 0; i < text.length(); i++) {
			final char ch = text.charAt(i);
			if (ch == '<') {
				break;
			}
			if (!Character.isWhitespace(ch) || i == text.length()) {
				return false;
			}
		}
		return HtmlTools.HTML_PATTERN.matcher(text.toLowerCase(Locale.ENGLISH)).matches();
	}

	public static String plainToHTML(final String text) {
		char myChar;
		final String textTabsExpanded = text.replaceAll("\t", "         ");
		final StringBuilder result = new StringBuilder(textTabsExpanded.length());
		final int lengthMinus1 = textTabsExpanded.length() - 1;
		result.append("<html><body><p>");
		for (int i = 0; i < textTabsExpanded.length(); ++i) {
			myChar = textTabsExpanded.charAt(i);
			switch (myChar) {
				case '&':
					result.append("&amp;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case ' ':
					if (i > 0 && i < lengthMinus1 && textTabsExpanded.charAt(i - 1) > 32
					        && textTabsExpanded.charAt(i + 1) > 32) {
						result.append(' ');
					}
					else {
						result.append("&nbsp;");
					}
					break;
				case '\n':
					result.append("<br>");
					break;
				default:
					result.append(myChar);
			}
		}
		return result.toString();
	}

	public static String removeAllTagsFromString(final String text) {
		return HtmlTools.TAGS_PATTERN.matcher(text).replaceAll("");
	}

	/**
	 * Removes all tags (<..>) from a string if it starts with "<html>..." to
	 * make it compareable.
	 */
	public static String removeHtmlTagsFromString(final String text) {
		if (HtmlTools.isHtmlNode(text)) {
			return HtmlTools.removeAllTagsFromString(text);
		}
		else {
			return text;
		}
	}

	public static String toXMLEscapedText(final String text) {
		final int len = text.length();
		final StringBuilder result = new StringBuilder(len);
		char myChar;
		for (int i = 0; i < len; ++i) {
			myChar = text.charAt(i);
			switch (myChar) {
				case '&':
					result.append("&amp;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '"':
					result.append("&quot;");
					break;
				default:
					result.append(myChar);
			}
		}
		return result.toString();
	}

	public static String toXMLEscapedTextExpandingWhitespace(String text) {
		text = text.replaceAll("\t", "         ");
		final int len = text.length();
		final StringBuilder result = new StringBuilder(len);
		char myChar;
		for (int i = 0; i < len; ++i) {
			myChar = text.charAt(i);
			switch (myChar) {
				case '&':
					result.append("&amp;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '"':
					result.append("&quot;");
					break;
				case ' ':
					if (i > 0 && i < len - 1 && text.charAt(i - 1) > 32 && text.charAt(i + 1) > 32) {
						result.append(' ');
					}
					else {
						result.append("&nbsp;");
					}
					break;
				default:
					result.append(myChar);
			}
		}
		return result.toString();
	}

	public static String toXMLUnescapedText(final String text) {
		return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
	}

	public static String unescapeHTMLUnicodeEntity(final String text) {
		final StringBuilder result = new StringBuilder(text.length());
		final StringBuilder entity = new StringBuilder();
		boolean readingEntity = false;
		char myChar;
		for (int i = 0; i < text.length(); ++i) {
			myChar = text.charAt(i);
			if (readingEntity) {
				if (myChar == ';') {
					if (entity.charAt(0) == '#') {
						try {
							if (entity.charAt(1) == 'x') {
								result.append((char) Integer.parseInt(entity.substring(2), 16));
							}
							else {
								result.append((char) Integer.parseInt(entity.substring(1), 10));
							}
						}
						catch (final NumberFormatException e) {
							result.append('&').append(entity).append(';');
						}
					}
					else {
						result.append('&').append(entity).append(';');
					}
					entity.setLength(0);
					readingEntity = false;
				}
				else {
					entity.append(myChar);
				}
			}
			else {
				if (myChar == '&') {
					readingEntity = true;
				}
				else {
					result.append(myChar);
				}
			}
		}
		if (entity.length() > 0) {
			result.append('&').append(entity);
		}
		return result.toString();
	}

	public static String unicodeToHTMLUnicodeEntity(final String text) {
		/*
		 * Heuristic reserve for expansion : factor 1.2
		 */
		final StringBuilder result = new StringBuilder((int) (text.length() * 1.2));
		int intValue;
		char myChar;
		for (int i = 0; i < text.length(); ++i) {
			myChar = text.charAt(i);
			intValue = text.charAt(i);
			if (intValue < 32 || intValue > 126) {
				result.append("&#x").append(Integer.toString(intValue, 16)).append(';');
			}
			else {
				result.append(myChar);
			}
		}
		return result.toString();
	}

	/**
	 *
	 */
	private HtmlTools() {
		super();
	}

	/**
	 * @return the maximal index i such that pI is mapped to i by removing all
	 *         tags from the original input.
	 */
	public int getMaximalOriginalPosition(final int pI, final ArrayList pListOfIndices) {
		for (int i = pListOfIndices.size() - 1; i >= 0; --i) {
			final IndexPair pair = (IndexPair) pListOfIndices.get(i);
			if (pI >= pair.replacedStart) {
				if (!pair.mIsTag) {
					return pair.originalStart + pI - pair.replacedStart;
				}
				else {
					return pair.originalEnd;
				}
			}
		}
		throw new IllegalArgumentException("Position " + pI + " not found.");
	}

	public int getMinimalOriginalPosition(final int pI, final ArrayList pListOfIndices) {
		for (final Iterator iter = pListOfIndices.iterator(); iter.hasNext();) {
			final IndexPair pair = (IndexPair) iter.next();
			if (pI >= pair.replacedStart && pI <= pair.replacedEnd) {
				return pair.originalStart + pI - pair.replacedStart;
			}
		}
		throw new IllegalArgumentException("Position " + pI + " not found.");
	}

	/**
	 * Replaces text in node content without replacing tags. fc, 19.12.06: This
	 * method is very difficult. If you have a simplier method, please supply
	 * it. But look that it complies with FindTextTests!!!
	 */
	public String getReplaceResult(final Pattern pattern, final String replacement, final String text) {
		final ArrayList splittedStringList = new ArrayList();
		String stringWithoutTags = null;
		{
			final StringBuffer sb = new StringBuffer();
			final Matcher matcher = HtmlTools.FIND_TAGS_PATTERN.matcher(text);
			int lastMatchEnd = 0;
			while (matcher.find()) {
				final String textWithoutTag = matcher.group(1);
				int replStart = sb.length();
				matcher.appendReplacement(sb, "$1");
				IndexPair indexPair;
				if (textWithoutTag.length() > 0) {
					indexPair = new IndexPair(lastMatchEnd, matcher.end(1), replStart, sb.length(), false);
					lastMatchEnd = matcher.end(1);
					splittedStringList.add(indexPair);
				}
				replStart = sb.length();
				indexPair = new IndexPair(lastMatchEnd, matcher.end(2), replStart, sb.length(), true);
				lastMatchEnd = matcher.end(2);
				splittedStringList.add(indexPair);
			}
			final int replStart = sb.length();
			matcher.appendTail(sb);
			if (sb.length() != replStart) {
				final IndexPair indexPair = new IndexPair(lastMatchEnd, text.length(), replStart, sb.length(), false);
				splittedStringList.add(indexPair);
			}
			stringWithoutTags = sb.toString();
		}
		final Matcher matcher = pattern.matcher(stringWithoutTags);
		final StringBuilder sbResult = new StringBuilder();
		if (matcher.find()) {
			/*
			 * now, take all from 0 to m.start() from original. append the
			 * replaced text, append all removed tags from the original that
			 * stays in between and append the rest.
			 */
			final int mStart = matcher.start();
			final int mEnd = matcher.end();
			int state = 0;
			for (final Iterator iter = splittedStringList.iterator(); iter.hasNext();) {
				final IndexPair pair = (IndexPair) iter.next();
				switch (state) {
					case 0:
						if (!pair.mIsTag && pair.replacedStart <= mStart && pair.replacedEnd > mStart) {
							state = 1;
							sbResult.append(text.substring(pair.originalStart, pair.originalStart + mStart
							        - pair.replacedStart));
							sbResult.append(replacement);
						}
						else {
							sbResult.append(text.substring(pair.originalStart, pair.originalEnd));
							break;
						}
					case 1:
						if (!pair.mIsTag && pair.replacedStart <= mEnd && pair.replacedEnd > mEnd) {
							state = 2;
							sbResult.append(text.substring(pair.originalStart + mEnd - pair.replacedStart,
							    pair.originalEnd));
						}
						else {
							if (pair.mIsTag) {
								sbResult.append(text.substring(pair.originalStart, pair.originalEnd));
							}
						}
						break;
					case 2:
						sbResult.append(text.substring(pair.originalStart, pair.originalEnd));
						break;
				}
			}
		}
		else {
			sbResult.append(text);
		}
		return sbResult.toString();
	}

	/**
	 * @return true, if well formed XML.
	 */
	public boolean isWellformedXml(final String xml) {
		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.newSAXParser().parse(new InputSource(new StringReader(xml)), new DefaultHandler());
			return true;
		}
		catch (final SAXParseException e) {
			LogTool.severe("XmlParseError on line " + e.getLineNumber() + " of " + xml, e);
		}
		catch (final Exception e) {
			LogTool.severe("XmlParseError", e);
		}
		return false;
	}

	public String toHtml(final String xhtmlText) {
		return HtmlTools.SLASHED_TAGS_PATTERN.matcher(xhtmlText).replaceAll("<$1>");
	}

	public String toXhtml(String htmlText) {
		if (!HtmlTools.isHtmlNode(htmlText)) {
			return null;
		}
		final StringReader reader = new StringReader(htmlText);
		final StringWriter writer = new StringWriter();
		try {
			XHTMLWriter.html2xhtml(reader, writer);
			final String resultXml = writer.toString();
			if (!isWellformedXml(resultXml)) {
				return HtmlTools.toXMLEscapedText(htmlText);
			}
			return resultXml;
		}
		catch (final IOException e) {
			LogTool.severe(e);
		}
		catch (final BadLocationException e) {
			LogTool.severe(e);
		}
		htmlText = htmlText.replaceAll("<", "&gt;");
		htmlText = htmlText.replaceAll(">", "&lt;");
		return htmlText;
	}

	static public int endOfText(final String html) {
        int bodyEndPos = html.lastIndexOf("</body>");
        if(bodyEndPos == -1){
        	bodyEndPos = html.lastIndexOf("</BODY>");
        }
        if(bodyEndPos == -1){
        	bodyEndPos = html.lastIndexOf("</html>");
        }
        if(bodyEndPos == -1){
        	bodyEndPos = html.lastIndexOf("</HTML>");
        }
        if(bodyEndPos == -1){
        	bodyEndPos = html.length();
        }
        return bodyEndPos;
    }
}
