package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;

@RunWith(ParameterizedTB3Runner.class)
public class TreeGridBasicFeaturesTest extends MultiBrowserTest {

    private TreeGridElement grid;

    public void setDataProvider(String dataProviderString) {
        selectMenuPath("Component", "Features", "Set data provider",
                dataProviderString);
    }

    @Parameters
    public static Collection<String> getDataProviders() {
        return Arrays.asList("LazyHierarchicalDataProvider",
                "InMemoryHierarchicalDataProvider");
    }

    @Before
    public void before() {
        setDebug(true);
        openTestURL();
        grid = $(TreeGridElement.class).first();
    }

    @Test
    public void toggle_collapse_server_side() {
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        selectMenuPath("Component", "Features", "Server-side expand",
                "Expand 0 | 0");
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        // expanding already expanded item should have no effect
        selectMenuPath("Component", "Features", "Server-side expand",
                "Expand 0 | 0");
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        selectMenuPath("Component", "Features", "Server-side collapse",
                "Collapse 0 | 0");
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        // collapsing the same item twice should have no effect
        selectMenuPath("Component", "Features", "Server-side collapse",
                "Collapse 0 | 0");
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        selectMenuPath("Component", "Features", "Server-side expand",
                "Expand 1 | 1");
        // 1 | 1 not yet visible, shouldn't immediately expand anything
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        selectMenuPath("Component", "Features", "Server-side expand",
                "Expand 0 | 0");
        // 1 | 1 becomes visible and is also expanded
        assertEquals(9, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "2 | 0", "2 | 1",
                "2 | 2", "1 | 2" });

        // collapsing a leaf should have no effect
        selectMenuPath("Component", "Features", "Server-side collapse",
                "Collapse 2 | 1");
        assertEquals(9, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "2 | 0", "2 | 1",
                "2 | 2", "1 | 2" });

        // collapsing 0 | 0 should collapse the expanded 1 | 1
        selectMenuPath("Component", "Features", "Server-side collapse",
                "Collapse 0 | 0");
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        assertNoSystemNotifications();
        assertNoErrorNotifications();
    }

    @Test
    public void pending_expands_cleared_when_data_provider_set() {
        selectMenuPath("Component", "Features", "Server-side expand",
                "Expand 1 | 1");
        selectMenuPath("Component", "Features", "Set data provider",
                "LazyHierarchicalDataProvider");
        grid.expandWithClick(0);
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });
    }

    @Test
    public void non_leaf_collapse_on_click() {
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        // Should expand "0 | 0"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-treegrid-expander")).click();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        // Should collapse "0 | 0"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-treegrid-expander")).click();
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
    }

    @Test
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();

        // Should expand "0 | 0" without moving focus
        new Actions(getDriver()).sendKeys(Keys.RIGHT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });
        assertTrue(
                grid.getRow(0).hasClassName("v-treegrid-rowmode-row-focused"));
        assertFalse(
                grid.getRow(1).hasClassName("v-treegrid-rowmode-row-focused"));

        // Should navigate 2 times down to "1 | 1"
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });
        assertFalse(
                grid.getRow(0).hasClassName("v-treegrid-rowmode-row-focused"));
        assertFalse(
                grid.getRow(1).hasClassName("v-treegrid-rowmode-row-focused"));
        assertTrue(
                grid.getRow(2).hasClassName("v-treegrid-rowmode-row-focused"));

        // Should expand "1 | 1" without moving focus
        new Actions(getDriver()).sendKeys(Keys.RIGHT).perform();
        assertEquals(9, grid.getRowCount());
        assertCellTexts(2, 0,
                new String[] { "1 | 1", "2 | 0", "2 | 1", "2 | 2", "1 | 2" });
        assertTrue(
                grid.getRow(2).hasClassName("v-treegrid-rowmode-row-focused"));

        // Should collapse "1 | 1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(2, 0, new String[] { "1 | 1", "1 | 2", "0 | 1" });
        assertTrue(
                grid.getRow(2).hasClassName("v-treegrid-rowmode-row-focused"));

        // Should navigate to "0 | 0"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(0, 0,
                new String[] { "0 | 0", "1 | 0", "1 | 1", "1 | 2", "0 | 1" });
        assertTrue(
                grid.getRow(0).hasClassName("v-treegrid-rowmode-row-focused"));

        // Should collapse "0 | 0"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
        assertTrue(
                grid.getRow(0).hasClassName("v-treegrid-rowmode-row-focused"));

        // Nothing should happen
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
        assertTrue(
                grid.getRow(0).hasClassName("v-treegrid-rowmode-row-focused"));

        assertNoErrorNotifications();
    }

    @Test
    public void changing_hierarchy_column() {
        assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-treegrid-expander")));
        assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-treegrid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "depth");

        assertFalse(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-treegrid-expander")));
        assertTrue(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-treegrid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "string");

        assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-treegrid-expander")));
        assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-treegrid-expander")));
    }

    @Override
    protected boolean useNativeEventsForIE() {
        // Fixes IE11 selectMenuPath troubles
        return false;
    }

    @Test
    public void expand_and_collapse_listeners() {
        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");

        assertFalse(logContainsText(
                "Item expanded (user originated: true): 0 | 0"));
        assertFalse(logContainsText(
                "Item collapsed (user originated: true): 0 | 0"));

        grid.expandWithClick(0);

        assertTrue(logContainsText(
                "Item expanded (user originated: true): 0 | 0"));
        assertFalse(logContainsText(
                "Item collapsed (user originated: true): 0 | 0"));

        grid.collapseWithClick(0);

        assertTrue(logContainsText(
                "Item expanded (user originated: true): 0 | 0"));
        assertTrue(logContainsText(
                "Item collapsed (user originated: true): 0 | 0"));

        selectMenuPath("Component", "Features", "Server-side expand",
                "Expand 0 | 0");

        assertTrue(logContainsText(
                "Item expanded (user originated: false): 0 | 0"));
        assertFalse(logContainsText(
                "Item collapsed (user originated: false): 0 | 0"));

        selectMenuPath("Component", "Features", "Server-side collapse",
                "Collapse 0 | 0");

        assertTrue(logContainsText(
                "Item expanded (user originated: false): 0 | 0"));
        assertTrue(logContainsText(
                "Item collapsed (user originated: false): 0 | 0"));

        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");

        grid.expandWithClick(1);
        grid.collapseWithClick(1);

        assertFalse(logContainsText(
                "Item expanded (user originated: true): 0 | 1"));
        assertFalse(logContainsText(
                "Item collapsed (user originated: true): 0 | 1"));
    }

    @Test
    @Ignore
    public void no_race_condition_with_multiple_collapse_or_expand() {
        testBench().disableWaitForVaadin();

        // toggle expand of two rows simultaneously
        // only the first of the expands should occur
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        waitUntilRowCountEquals(6);
        assertCellTexts(0, 0,
                new String[] { "0 | 0", "1 | 0", "1 | 1", "1 | 2", "0 | 1" });

        // toggle collapse of the expanded first row and immediately expand the
        // last row
        // only the collapse should occur
        grid.collapseWithClick(0);
        grid.expandWithClick(5);
        waitUntilRowCountEquals(3);
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
    }

    @Test
    public void expanded_nodes_stay_expanded_when_parent_expand_state_is_toggled() {
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.collapseWithClick(0);
        grid.expandWithClick(0);
        assertCellTexts(0, 0, new String[] { "0 | 0", "1 | 0", "2 | 0", "2 | 1",
                "2 | 2", "1 | 1", "1 | 2", "0 | 1", "0 | 2" });
        assertEquals(9, grid.getRowCount());

        grid.expandWithClick(7);
        grid.expandWithClick(8);
        grid.collapseWithClick(7);
        grid.collapseWithClick(0);
        grid.expandWithClick(1);
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "1 | 0", "2 | 0",
                "2 | 1", "2 | 2", "1 | 1", "1 | 2", "0 | 2" });
        assertEquals(9, grid.getRowCount());
    }

    private void assertCellTexts(int startRowIndex, int cellIndex,
            String[] cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }

    private void waitUntilRowCountEquals(int expectedCount) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return grid.getRowCount() == expectedCount;
            }
        });
    }
}
