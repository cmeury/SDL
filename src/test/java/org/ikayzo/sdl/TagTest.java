/*
 * Simple Declarative Language (SDL) for Java
 * Copyright 2005 Ikayzo, inc.
 *
 * This program is free software. You can distribute or modify it under the 
 * terms of the GNU Lesser General Public License version 2.1 as published by  
 * the Free Software Foundation.
 *
 * This program is distributed AS IS and WITHOUT WARRANTY. OF ANY KIND,
 * INCLUDING MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, contact the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.ikayzo.sdl;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import org.ikayzo.codec.Base64;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * SDL unit tests. 
 * 
 * @author Daniel Leuck
 * @author Cedric Meury
 */
@SuppressWarnings("unchecked")
public class TagTest {

	// Tag datastructure tests
	private static final String TAG = "Tag";
	private static final String TAG_WRITE_PARSE = "Tag Write Parse";
	
	// Basic Types Tests
	private static final String STRING_DECLARATIONS = "String Declarations";	
	private static final String CHARACTER_DECLARATIONS = "Character Declarations";
	private static final String NUMBER_DECLARATIONS = "Number Declarations";	
	private static final String BOOLEAN_DECLARATIONS = "Boolean Declarations";
	private static final String NULL_DECLARATION = "Null Declaration";	
	private static final String DATE_DECLARATIONS = "Date Declarations";		
	private static final String TIME_SPAN_DECLARATIONS = "Time Span Declarations";	
	private static final String DATE_TIME_DECLARATIONS = "Date Time Declarations";	

	// Structure Tests
	private static final String EMPTY_TAG = "Empty Tag";
	private static final String VALUES = "Values";		
	private static final String ATTRIBUTES = "Attributes";	
	private static final String VALUES_AND_ATTRIBUTES = "Values and Attributes";		
	private static final String CHILDREN = "Children";	
	private static final String NAMESPACES = "Namespaces";

	// parsed objects
	private Tag basicStructuresRoot;
	private Tag basicTypesRoot;

	@Before
	public void setUp() throws IOException, SDLParseException {
		basicStructuresRoot = loadSdl("test_structures.sdl");
		basicTypesRoot = loadSdl("test_basic_types.sdl");
	}

	private Tag loadSdl(String fileName) throws IOException, SDLParseException {
		InputStream stream = this.getClass().getResourceAsStream(fileName);
		InputStreamReader reader = new InputStreamReader(stream, "UTF8");
		return new Tag("root").read(reader);
	}

	////////////////////////////////////////////////////////////////////////////
	// Tag Tests
	////////////////////////////////////////////////////////////////////////////
	@Test
	public void testTag() {
		// Doing basic Tag tests...
		
		// TagTest to make sure Tag ignores the order in which attributes are
		// added.
		//     Making sure attributes are consistently ordered...
		Tag t1 = new Tag("test");
		t1.setAttribute("foo", "bar");
		t1.setAttribute("john", "doe");
		
		Tag t2 = new Tag("test");
		t2.setAttribute("john", "doe");	
		t2.setAttribute("foo", "bar");
		
		assertEquals(TAG, t1, t2);
		
		// Making sure tags with different structures return false from .equals...
		
		t2.setValue("item");
		assertNotEquals(TAG, t1, t2);
		
		t2.removeValue("item");
		t2.setAttribute("another", "attribute");
		assertNotEquals(TAG, t1, t2);
		
		// Checking attributes namespaces...
		
		t2.setAttribute("name", "bill");
		t2.setAttribute("private", "smoker", true);
		t2.setAttribute("public", "hobby", "hiking");
		t2.setAttribute("private", "nickname", "tubby");
		
		assertEquals(TAG, t2.getAttributesForNamespace("private"),
				new TreeMap<String,Object>(
						map("smoker",true,"nickname","tubby")
						));
	}	

	@Test
	public void testTagWriteParse()
		throws SDLParseException {
		
		// Doing Tag write/parse tests for file ...

		// Write out the contents of a tag, read the output back in and
		// test for equality.  This is a very rigorous test for any non-trivial
		// file.  It tests the parsing, output, and .equals implementation.
		//     Write out the tag and read it back in...
		
		assertEquals(TAG_WRITE_PARSE, basicStructuresRoot, new Tag("test")
				.read(basicStructuresRoot.toString()).getChild("root"));
		
		
	}
	
	////////////////////////////////////////////////////////////////////////////
	// Basic Types Tests
	////////////////////////////////////////////////////////////////////////////

	@Test
	public void testStrings() {
		// Doing String tests...
		//     Doing basic tests including new line handling...
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string1").getValue(), "hello");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string2").getValue(), "hi");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string3").getValue(), "aloha");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string4").getValue(), "hi there");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string5").getValue(), "hi there joe");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string6").getValue(), "line1\nline2");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string7").getValue(), "line1\nline2");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string8").getValue(), "line1\nline2\nline3");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string9").getValue(),
				"Anything should go in this line without escapes \\ \\\\ \\n " +
				"\\t \" \"\" ' ''");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("string10").getValue(), "escapes \"\\\n\t");
		
		//     Checking unicode strings...
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("japanese").getValue(), "\u65e5\u672c\u8a9e");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("korean").getValue(), "\uc5ec\ubcf4\uc138\uc694");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("russian").getValue(),
				"\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u043b\u0442\u0435");
		
		//     More new line tests...
		assertContains(STRING_DECLARATIONS, (String) basicTypesRoot.getChild("xml").getValue(),
				"<text>Hi there!</text>");
		assertEquals(STRING_DECLARATIONS, basicTypesRoot.getChild("line_test").getValue(),
				"\nnew line above and below\n");
	}

	@Test
	public void testCharacters() {
		// Doing character tests...
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char1").getValue(), 'a');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char2").getValue(), 'A');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char3").getValue(), '\\');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char4").getValue(), '\n');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char5").getValue(), '\t');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char6").getValue(), '\'');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char7").getValue(), '"');
		
		//     Doing unicode character tests...
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char8").getValue(), '\u65e5');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char9").getValue(), '\uc5ec');
		assertEquals(CHARACTER_DECLARATIONS, basicTypesRoot.getChild("char10").getValue(), '\u0437');
	}

	@Test
	public void testNumbers() {
		// Doing number tests...
		
		//     Testing ints...
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("int1").getValue(), 0);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("int2").getValue(), 5);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("int3").getValue(), -100);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("int4").getValue(), 234253532);
		
		//     Testing longs...
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("long1").getValue(), 0L);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("long2").getValue(), 5L);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("long3").getValue(), 5L);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("long4").getValue(), 3904857398753453453L);
		
		//     Testing floats...
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("float1").getValue(), 1F);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("float2").getValue(), .23F);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("float3").getValue(), -.34F);

		//     Testing doubles...
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("double1").getValue(), 2D);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("double2").getValue(), -.234D);
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("double3").getValue(), 2.34D);
		
		//     Testing decimals (BigDouble in Java)...
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("decimal1").getValue(),
				new BigDecimal("0"));
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("decimal2").getValue(),
				new BigDecimal("11.111111"));
		assertEquals(NUMBER_DECLARATIONS, basicTypesRoot.getChild("decimal3").getValue(),
				new BigDecimal("234535.3453453453454345345341242343"));		
	}

	@Test
	public void testBooleans() {
		// Doing boolean tests...

		assertEquals(BOOLEAN_DECLARATIONS, basicTypesRoot.getChild("light-on").getValue(), true);
		assertEquals(BOOLEAN_DECLARATIONS, basicTypesRoot.getChild("light-off").getValue(), false);
		assertEquals(BOOLEAN_DECLARATIONS, basicTypesRoot.getChild("light1").getValue(), true);
		assertEquals(BOOLEAN_DECLARATIONS, basicTypesRoot.getChild("light2").getValue(), false);
	}

	@Test
	public void testNull() {
		// Doing null test...

		assertEquals(NULL_DECLARATION, basicTypesRoot.getChild("nothing").getValue(), null);
	}

	@Test
	public void testDates() {
		// Doing date tests...

		assertEquals(DATE_DECLARATIONS, basicTypesRoot.getChild("date1").getValue(),
				getDate(2005,12,31));
		assertEquals(DATE_DECLARATIONS, basicTypesRoot.getChild("date2").getValue(),
				getDate(1882,5,2));
		assertEquals(DATE_DECLARATIONS, basicTypesRoot.getChild("date3").getValue(),
				getDate(1882,5,2));		
		assertEquals(DATE_DECLARATIONS, basicTypesRoot.getChild("_way_back").getValue(),
				getDate(582,9,16));			
	}

	@Test
	public void testTimeSpans() {
		// Doing time span tests...

		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time1").getValue(),
				new SDLTimeSpan(0,12,30,0,0));
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time2").getValue(),
				new SDLTimeSpan(0,24,0,0,0));
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time3").getValue(),
				new SDLTimeSpan(0,1,0,0,0));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time4").getValue(),
				new SDLTimeSpan(0,1,0,0,0));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time5").getValue(),
				new SDLTimeSpan(0,12,30,2,0));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time6").getValue(),
				new SDLTimeSpan(0,12,30,23,0));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time7").getValue(),
				new SDLTimeSpan(0,12,30,23,100));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time8").getValue(),
				new SDLTimeSpan(0,12,30,23,120));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time9").getValue(),
				new SDLTimeSpan(0,12,30,23,123));
		
		//     Checking time spans with days...
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time10").getValue(),
				new SDLTimeSpan(34,12,30,23,100));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time11").getValue(),
				new SDLTimeSpan(1,12,30,0,0));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time12").getValue(),
				new SDLTimeSpan(5,12,30,23,123));
		
		//     Checking negative time spans...
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time13").getValue(),
				new SDLTimeSpan(0,-12,-30,-23,-123));	
		assertEquals(TIME_SPAN_DECLARATIONS, basicTypesRoot.getChild("time14").getValue(),
				new SDLTimeSpan(-5,-12,-30,-23,-123));
	}

	@Test
	public void testDateTimes() {
		// Doing date time tests...

		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time1").getValue(),
				getDateTime(2005,12,31,12,30,0,0,null));	
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time2").getValue(),
				getDateTime(1882,5,2,12,30,0,0,null));	
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time3").getValue(),
				getDateTime(2005,12,31,1,0,0,0,null));	
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time4").getValue(),
				getDateTime(1882,5,2,1,0,0,0,null));	
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time5").getValue(),
				getDateTime(2005,12,31,12,30,23,120,null));	
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time6").getValue(),
				getDateTime(1882,5,2,12,30,23,123,null));	
		
		//     Checking timezones...
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time7").getValue(),
				getDateTime(1882,5,2,12,30,23,123,"JST"));	
		assertEquals(DATE_TIME_DECLARATIONS, basicTypesRoot.getChild("date_time8").getValue(),
				getDateTime(985, 4,11,12,30,23,123,"PST"));	
	}

	@Test
	public void testBinaries() throws Exception {
		// Doing binary tests...
		assertArrayEquals((byte[]) basicTypesRoot.getChild("hi").getValue(),
			"hi".getBytes("UTF8"));	
		assertArrayEquals((byte[]) basicTypesRoot.getChild("png").getValue(),
			Base64.decode(
				"iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAKnRFWHRDcmVhdGlvbiBUaW1l" +
				"AERpIDQgTXJ6IDIwMDMgMDA6MjQ6MDQgKzAxMDDdSQ6OAAAAB3RJTUUH0wMEAAcllPlrJgAA" +
				"AAlwSFlzAAAK8AAACvABQqw0mAAAAARnQU1BAACxjwv8YQUAAADQSURBVHjaY2CgEDCCyZn/" +
				"3YHkDhL1ejCkM+5kgXJ2zDQmXueShwwMh9+ALWSEGcCQfhZIvHlDnAk8PAwMHBxgJtyAa7bX" +
				"UdT8/cvA8Ps3hP7zB4FBYn/+vGbweqyJaoCmpiaKASDFv35BNMBoZMzwGKKOidJYoNgAuBdm" +
				"naXQgHRKDfgagxD89w8S+iAaFICwGIHFAgjrHUczAByySAaAMEgDLBphhv7/D8EYLgDZhAxA" +
				"mkAKYYbAMMwwDAOQXYDuDXRXgDC6AR7SW8jITNQAACjZgdj4VjlqAAAAAElFTkSuQmCC"						
			));		
	}
	
	////////////////////////////////////////////////////////////////////////////
	// Structure Tests (values, attributes, children)
	////////////////////////////////////////////////////////////////////////////

	@Test
	public void testEmptyTag() {
		// Doing empty tag test...
		
		assertEquals(EMPTY_TAG, basicStructuresRoot.getChild("empty_tag"), new Tag("empty_tag"));
	}

	@Test
	public void testValues() {
		// Doing values tests...

		assertEquals(VALUES, basicStructuresRoot.getChild("values1").getValues(), list("hi"));
		assertEquals(VALUES, basicStructuresRoot.getChild("values2").getValues(), list("hi","ho"));
		assertEquals(VALUES, basicStructuresRoot.getChild("values3").getValues(), list(1, "ho"));
		assertEquals(VALUES, basicStructuresRoot.getChild("values4").getValues(), list("hi",5));
		assertEquals(VALUES, basicStructuresRoot.getChild("values5").getValues(), list(1,2));
		assertEquals(VALUES, basicStructuresRoot.getChild("values6").getValues(), list(1,2,3));
		assertEquals(VALUES, basicStructuresRoot.getChild("values7").getValues(),
				list(null,"foo",false,getDate(1980,12,5)));		
		assertEquals(VALUES, basicStructuresRoot.getChild("values8").getValues(),
				list(null, "foo", false, getDateTime(1980,12,5,12,30,0,0,null),
						"there", new SDLTimeSpan(0,15,23,12,234)));
		assertEquals(VALUES, basicStructuresRoot.getChild("values9").getValues(),
				list(null, "foo", false, getDateTime(1980,12,5,12,30,0,0,null),
						"there", getDateTime(1989,8,12,15,23,12,234,"JST")));
		assertEquals(VALUES, basicStructuresRoot.getChild("values10").getValues(),
				list(null, "foo", false, getDateTime(1980,12,5,12,30,0,0,null),
						"there", new SDLTimeSpan(0,15,23,12,234), "more stuff"));
		assertEquals(VALUES, basicStructuresRoot.getChild("values11").getValues(),
				list(null, "foo", false, getDateTime(1980,12,5,12,30,0,0,null),
						"there", new SDLTimeSpan(123,15,23,12,234),
						"more stuff here"));		
		assertEquals(VALUES, basicStructuresRoot.getChild("values12").getValues(), list(1,3));
		assertEquals(VALUES, basicStructuresRoot.getChild("values13").getValues(), list(1,3));
		assertEquals(VALUES, basicStructuresRoot.getChild("values14").getValues(), list(1,3));
		assertEquals(VALUES, basicStructuresRoot.getChild("values15").getValues(), list(1,2,4,5,6));
		assertEquals(VALUES, basicStructuresRoot.getChild("values16").getValues(), list(1,2,5));
		assertEquals(VALUES, basicStructuresRoot.getChild("values17").getValues(), list(1,2,5));
		assertEquals(VALUES, basicStructuresRoot.getChild("values18").getValues(), list(1,2,7));
		assertEquals(VALUES, basicStructuresRoot.getChild("values19").getValues(),
				list(1,3,5,7));	
		assertEquals(VALUES, basicStructuresRoot.getChild("values20").getValues(),
				list(1,3,5));		
		assertEquals(VALUES, basicStructuresRoot.getChild("values21").getValues(),
				list(1,3,5));			 
		assertEquals(VALUES, basicStructuresRoot.getChild("values22").getValues(),
				list("hi","ho","ho",5,"hi"));			
	}

	@Test
	public void testAttributes() throws Exception {
		// Doing attribute tests...

		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts1").getAttributes(),
				map("name","joe"));
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts2").getAttributes(),
				map("size",5));	
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts3").getAttributes(),
				map("name","joe","size",5));	
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts4").getAttributes(),
				map("name","joe","size",5,"smoker",false));
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts5").getAttributes(),
				map("name","joe","smoker",false));
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts6").getAttributes(),
				map("name","joe","smoker",false));	
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts7").getAttributes(),
				map("name","joe"));
		assertEquals(ATTRIBUTES, basicStructuresRoot.getChild("atts8").getAttributes(),
				map("name","joe","size",5,"smoker",false,"text","hi","birthday",
						getDate(1972,5,23)));
		assertArrayEquals((byte[]) basicStructuresRoot.getChild("atts9").getAttribute("key"),
				"mykey".getBytes("utf8"));
	}

	@Test
	public void testValuesAndAttributes() {
		// Doing values and attributes tests...

		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts1")
				.getValues(), list("joe"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts1")
				.getAttributes(), map("size", 5));		
		
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts2")
				.getValues(), list("joe"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts2")
				.getAttributes(), map("size", 5));			
		
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts3")
				.getValues(), list("joe"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts3")
				.getAttributes(), map("size", 5));

		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts4")
				.getValues(), list("joe"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts4")
				.getAttributes(), map("size", 5, "weight", 160, "hat", "big"));
		
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts5")
				.getValues(), list("joe", "is a\n nice guy"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts5")
				.getAttributes(), map("size", 5, "smoker", false));		

		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts6")
				.getValues(), list("joe", "is a\n nice guy"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts6")
				.getAttributes(), map("size", 5, "house", "big and\n blue"));
		
		//////////
		
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts7")
				.getValues(), list("joe", "is a\n nice guy"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts7")
				.getAttributes(), map("size", 5, "smoker", false));
		
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts8")
				.getValues(), list("joe", "is a\n nice guy"));
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts8")
				.getAttributes(), map("size", 5, "smoker", false));
		
		assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts9")
				.getValues(),list("joe", "is a\n nice guy"));
			assertEquals(VALUES_AND_ATTRIBUTES, basicStructuresRoot.getChild("valatts9")
					.getAttributes(), map("size", 5, "smoker", false));			
	}

	@Test
	public void testChildren() {
		// Doing children tests...

		Tag parent = basicStructuresRoot.getChild("parent");
		
		assertEquals(CHILDREN, parent.getChildren().size(), 2);
		assertEquals(CHILDREN, parent.getChildren().get(1).getName(),
				"daughter");
		
		
		Tag grandparent = basicStructuresRoot.getChild("grandparent");
		
		assertEquals(CHILDREN, grandparent.getChildren().size(), 2);
		// recursive fetch of children
		assertEquals(CHILDREN, grandparent.getChildren(true).size(), 6);		
		assertEquals(CHILDREN, grandparent.getChildren("son", true).size(), 2);	
		
		Tag grandparent2 = basicStructuresRoot.getChild("grandparent2");
		assertEquals(CHILDREN, grandparent2.getChildren("child", true)
				.size(), 5);
		assertEquals(CHILDREN, grandparent2.getChild("daughter", true)
				.getAttribute("birthday"),getDate(1976, 4, 18));
		
		Tag files = basicStructuresRoot.getChild("files");
		
		assertEquals(CHILDREN, files.getChildrenValues("content"),
				list("c:/file1.txt", "c:/file2.txt", "c:/folder"));
		
		Tag matrix = basicStructuresRoot.getChild("matrix");
		
		assertEquals(CHILDREN, matrix.getChildrenValues("content"),
				list(list(1,2,3),list(4,5,6)));		
	}

	@Test
	public void testNamespaces() {
		// Doing namespaces tests...
		
		assertEquals(NAMESPACES, basicStructuresRoot.getChildrenForNamespace("person", true)
				.size(), 8);
		
		Tag grandparent2 = basicStructuresRoot.getChild("grandparent3");
		
		// get only the attributes for Akiko in the public namespace
		assertEquals(NAMESPACES, grandparent2.getChild("daughter", true)
				.getAttributesForNamespace("public"), map("name", "Akiko",
						"birthday", getDate(1976, 4, 18)));
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	// Utility methods
	////////////////////////////////////////////////////////////////////////////

	private static void assertContains(String testName, String o1, String o2) {
		assertNotNull(testName, o1);
		assertNotNull(testName, o2);
		assertTrue(testName, o1.contains(o2));
	}

	private static Calendar getDate(int year, int month, int day) {	
		GregorianCalendar gc = new GregorianCalendar(year, month-1, day);
		
		// force calculations
		gc.get(Calendar.YEAR);
		gc.get(Calendar.MONTH);
		gc.get(Calendar.DAY_OF_MONTH);
		
		gc.clear(Calendar.HOUR_OF_DAY);
		gc.clear(Calendar.MINUTE);
		gc.clear(Calendar.SECOND);
		gc.clear(Calendar.MILLISECOND);
	
		return gc;
	}
	
	private static Calendar getDateTime(int year, int month, int day, int hour,
			int minute, int second, int millisecond, String timeZone) {
	
		TimeZone tz = (timeZone==null) ? TimeZone.getDefault() :
			TimeZone.getTimeZone(timeZone);
		
		GregorianCalendar gc = new GregorianCalendar(tz);
		
		gc.set(Calendar.YEAR, year);
		gc.set(Calendar.MONTH, month-1);
		gc.set(Calendar.DAY_OF_MONTH, day);
		
		gc.set(Calendar.HOUR_OF_DAY, hour);
		gc.set(Calendar.MINUTE, minute);
		gc.set(Calendar.SECOND, second);
		gc.set(Calendar.MILLISECOND, millisecond);
		
		// force calculations
		gc.get(Calendar.YEAR);
		gc.get(Calendar.MONTH);
		gc.get(Calendar.DAY_OF_MONTH);
		
		gc.get(Calendar.HOUR_OF_DAY);
		gc.get(Calendar.MINUTE);
		gc.get(Calendar.SECOND);
		gc.get(Calendar.MILLISECOND);
	
		return gc;
	}
	
	private static List list(Object... obs) {
		ArrayList list = new ArrayList();
		Collections.addAll(list, obs);
		return list;
	}
	
	/**
	 * Make a map from alternating key/value pairs
	 */
	private static Map map(Object... obs) {
		TreeMap map = new TreeMap();
		for(int i=0; i<obs.length;)
			map.put(obs[i++], obs[i++]);		
		return map;
	}

}
