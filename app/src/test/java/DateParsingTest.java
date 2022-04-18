import front.controllers.VisualizeToolSceneController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arnaud MOREAU
 */
public class DateParsingTest {
    final String d0 = "";
    final String d1 = "a";
    final String d2 = "--";
    final String d3 = "0000-00-00";
    final String d4 = "0000.00.00";
    final String d5 = "2022-03-17";
    final String d6 = "2020-01-02";
    final String d7 = "23-07-2002";

    @Test
    @DisplayName("Day Parsing Test")
    public void testDayParsing() {
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayFromString(d0));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayFromString(d1));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayFromString(d2));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayFromString(d3));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayFromString(d4));
        assertEquals(17, VisualizeToolSceneController.StringDateParser.getDayFromString(d5));
        assertEquals(2, VisualizeToolSceneController.StringDateParser.getDayFromString(d6));
        assertEquals(23, VisualizeToolSceneController.StringDateParser.getDayFromString(d7));
    }

    @Test
    @DisplayName("Month Parsing Test")
    public void monthParsingTest() {
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getMonthFromString(d0));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getMonthFromString(d1));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getMonthFromString(d2));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getMonthFromString(d3));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getMonthFromString(d4));
        assertEquals(3, VisualizeToolSceneController.StringDateParser.getMonthFromString(d5));
        assertEquals(1, VisualizeToolSceneController.StringDateParser.getMonthFromString(d6));
        assertEquals(7, VisualizeToolSceneController.StringDateParser.getMonthFromString(d7));
    }

    @Test
    @DisplayName("Year Parsing Test")
    public void yearParsingTest() {
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearFromString(d0));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearFromString(d1));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearFromString(d2));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearFromString(d3));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearFromString(d4));
        assertEquals(2022, VisualizeToolSceneController.StringDateParser.getYearFromString(d5));
        assertEquals(2020, VisualizeToolSceneController.StringDateParser.getYearFromString(d6));
        assertEquals(2002, VisualizeToolSceneController.StringDateParser.getYearFromString(d7));
    }

    @Test
    @DisplayName("Get Year Week Test")
    public void getYearWeekTest() {
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d0));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d1));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d2));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d3));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d4));
        assertEquals(10, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d5));
        assertEquals(0, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d6));
        assertEquals(29, VisualizeToolSceneController.StringDateParser.getYearWeekFromString(d7));
    }

    @Test
    @DisplayName("Get Day of Year Test")
    public void getDayOfYearTest() {
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d0));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d1));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d2));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d3));
        assertEquals(-1, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d4));
        assertEquals(76, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d5));
        assertEquals(2, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d6));
        assertEquals(204, VisualizeToolSceneController.StringDateParser.getDayOfYearFromString(d7));
    }
}
