package clock.panel;

import clock.entity.Clock;
import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import static clock.entity.Panel.PANEL_STOPWATCH;
import static clock.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link DisplayLapsPanel} class.
 * DisplayLapsPanel renders stopwatch lap data in a JTable inside a larger stopwatch panel.
 *
 * @author michael ball
 * @version since 3.0
 */
class DisplayLapsPanelTest
{
    private static final Logger logger = LogManager.getLogger(DisplayLapsPanelTest.class);

    private Clock clock;
    private StopwatchPanel stopwatchPanel;
    private DisplayLapsPanel displayLapsPanel;

    @BeforeAll
    static void beforeAll()
    {
        logger.info("Starting DisplayLapsPanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        Stopwatch.stopwatchCounter = 0L;
        stopwatchPanel = new StopwatchPanel(new ClockFrame(clock));
        stopwatchPanel.getClockFrame().changePanels(PANEL_STOPWATCH);
        displayLapsPanel = stopwatchPanel.getDisplayLapsPanel();
    }

    @AfterEach
    void afterEach()
    {
        stopwatchPanel.getClockFrame().stop();
    }

    @AfterAll
    static void afterAll()
    {
        logger.info("Concluding DisplayLapsPanelTest.");
    }

    // ───────────────────────────────────────────────────
    // Constructor
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("Panel background is BLACK after construction")
    void testConstructorSetsBlackBackground()
    {
        assertEquals(Color.BLACK, displayLapsPanel.getBackground(),
                "Background should be BLACK");
    }

    @Test
    @DisplayName("Panel preferred size matches ClockFrame.analogueSize")
    void testConstructorSetsPreferredSize()
    {
        assertEquals(ClockFrame.analogueSize, displayLapsPanel.getPreferredSize(),
                "Preferred size should match ClockFrame.analogueSize");
    }

    @Test
    @DisplayName("Constructor calls setDefaultLayout — panel is non-empty on creation")
    void testConstructorCallsSetDefaultLayout()
    {
        assertTrue(displayLapsPanel.getComponentCount() > 0,
                "Panel should contain components after construction");
    }

    @Test
    @DisplayName("isLapsReversed defaults to false")
    void testIsLapsReversedDefaultIsFalse()
    {
        assertFalse(displayLapsPanel.isLapsReversed,
                "isLapsReversed should be false by default");
    }

    // ───────────────────────────────────────────────────
    // resetPanel
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("resetPanel removes all components from the panel")
    void testResetPanelClearsAllComponents()
    {
        displayLapsPanel.resetPanel();
        assertEquals(0, displayLapsPanel.getComponentCount(),
                "After resetPanel the component count should be 0");
    }

    // ───────────────────────────────────────────────────
    // setDefaultLayout
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("setDefaultLayout adds components to a previously empty panel")
    void testSetDefaultLayoutAddsComponents()
    {
        displayLapsPanel.resetPanel();
        displayLapsPanel.setDefaultLayout();
        assertTrue(displayLapsPanel.getComponentCount() > 0,
                "setDefaultLayout should add components to the panel");
    }

    @Test
    @DisplayName("setDefaultLayout adds a JScrollPane containing the initial empty laps table")
    void testSetDefaultLayoutContainsScrollPane()
    {
        displayLapsPanel.resetPanel();
        displayLapsPanel.setDefaultLayout();
        final JScrollPane scrollPane = findScrollPane(displayLapsPanel);
        assertNotNull(scrollPane, "setDefaultLayout should add a JScrollPane to the panel");
        assertInstanceOf(JTable.class, scrollPane.getViewport().getView(),
                "The scroll pane should wrap a JTable");
    }

    @Test
    @DisplayName("setDefaultLayout creates an empty laps table")
    void testSetDefaultLayoutCreatesEmptyLapsTable()
    {
        displayLapsPanel.resetPanel();
        displayLapsPanel.setDefaultLayout();
        final JTable table = findTableInScrollPane(displayLapsPanel);
        assertNotNull(table);
        assertEquals(0, table.getModel().getRowCount(),
                "Default laps table should have no rows");
    }

    // ───────────────────────────────────────────────────
    // addComponent
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("addComponent increases the panel's component count")
    void testAddComponentIncreasesCount()
    {
        displayLapsPanel.resetPanel();
        final int before = displayLapsPanel.getComponentCount();
        final JLabel extra = new JLabel("test");
        displayLapsPanel.addComponent(extra, 0, 0, 1, 1, 0, 0, 1, 0,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0));
        assertEquals(before + 1, displayLapsPanel.getComponentCount(),
                "addComponent should increase the component count by one");
    }

    // ───────────────────────────────────────────────────
    // updateLabelsAndStopwatchTable
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("updateLabelsAndStopwatchTable adds components when a current stopwatch is set")
    void testUpdateLabelsAndStopwatchTableAddsComponents()
    {
        final Stopwatch sw = addStopwatchToPanel("Sw1");
        displayLapsPanel.updateLabelsAndStopwatchTable();
        assertTrue(displayLapsPanel.getComponentCount() > 0,
                "Panel should have components after updateLabelsAndStopwatchTable");
    }

    @Test
    @DisplayName("updateLabelsAndStopwatchTable shows a View All button")
    void testUpdateLabelsAndStopwatchTableShowsViewAllButton()
    {
        addStopwatchToPanel("Sw1");
        displayLapsPanel.updateLabelsAndStopwatchTable();
        final JButton viewAll = findButtonByText(displayLapsPanel, VIEW_ALL);
        assertNotNull(viewAll, "A 'View All' button should be present after update");
    }

    @Test
    @DisplayName("updateLabelsAndStopwatchTable shows the current stopwatch name as a label")
    void testUpdateLabelsAndStopwatchTableShowsCurrentStopwatchName()
    {
        addStopwatchToPanel("MySw");
        displayLapsPanel.updateLabelsAndStopwatchTable();
        final boolean nameFound = Arrays.stream(displayLapsPanel.getComponents())
                .filter(c -> c instanceof JLabel)
                .map(c -> ((JLabel) c).getText())
                .anyMatch("MySw"::equals);
        assertTrue(nameFound, "Panel should show the current stopwatch name");
    }

    @Test
    @DisplayName("updateLabelsAndStopwatchTable shows 0 rows when no laps recorded")
    void testUpdateLabelsAndStopwatchTableWithNoLaps()
    {
        addStopwatchToPanel("Sw1");
        displayLapsPanel.updateLabelsAndStopwatchTable();
        final JTable table = findTableInScrollPane(displayLapsPanel);
        assertNotNull(table);
        assertEquals(0, table.getModel().getRowCount(),
                "Laps table should have 0 rows when no laps have been recorded");
    }

    @Test
    @DisplayName("updateLabelsAndStopwatchTable shows correct row count when laps are recorded")
    void testUpdateLabelsAndStopwatchTableWithLapsShowsCorrectRowCount()
    {
        final Stopwatch sw = addStopwatchToPanel("Sw1");
        sw.recordLap();
        sw.recordLap();
        displayLapsPanel.updateLabelsAndStopwatchTable();
        final JTable table = findTableInScrollPane(displayLapsPanel);
        assertNotNull(table);
        assertEquals(2, table.getModel().getRowCount(),
                "Laps table should have a row for every recorded lap");
    }

    @Test
    @DisplayName("Laps appear in ascending order when isLapsReversed is false")
    void testLapsDisplayedInNaturalOrderByDefault()
    {
        final Stopwatch sw = addStopwatchToPanel("Sw1");
        sw.recordLap();
        sw.recordLap();
        displayLapsPanel.isLapsReversed = false;
        displayLapsPanel.updateLabelsAndStopwatchTable();
        final TableModel model = findTableInScrollPane(displayLapsPanel).getModel();
        assertEquals(sw.getLaps().get(0).getLapNumber(), model.getValueAt(0, 0),
                "Row 0 should contain lap 1 when not reversed");
        assertEquals(sw.getLaps().get(1).getLapNumber(), model.getValueAt(1, 0),
                "Row 1 should contain lap 2 when not reversed");
    }

    @Test
    @DisplayName("Laps appear in descending order when isLapsReversed is true")
    void testLapsDisplayedInReverseOrderWhenFlagSet()
    {
        final Stopwatch sw = addStopwatchToPanel("Sw1");
        sw.recordLap();
        sw.recordLap();
        displayLapsPanel.isLapsReversed = true;
        displayLapsPanel.updateLabelsAndStopwatchTable();
        final TableModel model = findTableInScrollPane(displayLapsPanel).getModel();
        assertEquals(sw.getLaps().get(1).getLapNumber(), model.getValueAt(0, 0),
                "Row 0 should contain the last lap when reversed");
        assertEquals(sw.getLaps().get(0).getLapNumber(), model.getValueAt(1, 0),
                "Row 1 should contain lap 1 when reversed");
    }

    // ───────────────────────────────────────────────────
    // buttonAction — SELECT
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("buttonAction returns a non-null Action for any column index")
    void testButtonActionReturnsNonNullAction()
    {
        assertNotNull(displayLapsPanel.buttonAction(2), "buttonAction(2) should return a non-null Action");
        assertNotNull(displayLapsPanel.buttonAction(3), "buttonAction(3) should return a non-null Action");
    }

    @Test
    @DisplayName("buttonAction SELECT sets the current stopwatch on the panel")
    void testButtonActionSelectSetsCurrentStopwatch()
    {
        final Stopwatch sw1 = addNamedStopwatch("Alpha");
        final Stopwatch sw2 = addNamedStopwatch("Beta");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        // Invoke SELECT action for row 1 (sw2)
        final Action selectAction = displayLapsPanel.buttonAction(2);
        selectAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "1"));

        assertSame(sw2, stopwatchPanel.getCurrentStopwatch(),
                "SELECT should switch the current stopwatch to the one at the selected row");
    }

    @Test
    @DisplayName("buttonAction SELECT updates the stopwatch name text field")
    void testButtonActionSelectUpdatesNameField()
    {
        final Stopwatch sw1 = addNamedStopwatch("Alpha");
        final Stopwatch sw2 = addNamedStopwatch("Beta");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        displayLapsPanel.buttonAction(2)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "1"));

        assertEquals("Beta", stopwatchPanel.getStopwatchNameField().getText(),
                "SELECT should update the stopwatch name field to the selected stopwatch");
    }

    @Test
    @DisplayName("buttonAction SELECT sets start button text to RESUME")
    void testButtonActionSelectSetsStartButtonToResume()
    {
        final Stopwatch sw1 = addNamedStopwatch("Alpha");
        addNamedStopwatch("Beta");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        displayLapsPanel.buttonAction(2)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "1"));

        assertEquals(RESUME, stopwatchPanel.getStartButton().getText(),
                "SELECT should change the Start button to RESUME");
    }

    // ───────────────────────────────────────────────────
    // buttonAction — REMOVE
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("buttonAction REMOVE removes the stopwatch at the given row from the clock list")
    void testButtonActionRemoveDeletesStopwatch()
    {
        final Stopwatch sw1 = addNamedStopwatch("Alpha");
        final Stopwatch sw2 = addNamedStopwatch("Beta");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        displayLapsPanel.buttonAction(3)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "1"));

        assertFalse(clock.getListOfStopwatches().contains(sw2),
                "REMOVE should remove the stopwatch at the given row");
        assertEquals(1, clock.getListOfStopwatches().size(),
                "Only one stopwatch should remain after removal");
    }

    @Test
    @DisplayName("buttonAction REMOVE sets currentStopwatch to null when the last one is removed")
    void testButtonActionRemoveLastStopwatchSetsCurrentToNull()
    {
        final Stopwatch sw1 = addNamedStopwatch("Only");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        displayLapsPanel.buttonAction(3)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "0"));

        assertNull(stopwatchPanel.getCurrentStopwatch(),
                "currentStopwatch should be null after the last stopwatch is removed");
        assertTrue(clock.getListOfStopwatches().isEmpty(),
                "Clock stopwatch list should be empty after removal");
    }

    @Test
    @DisplayName("buttonAction REMOVE sets currentStopwatch to the last remaining when not empty")
    void testButtonActionRemoveNonLastSetsCurrentToLast()
    {
        final Stopwatch sw1 = addNamedStopwatch("Alpha");
        final Stopwatch sw2 = addNamedStopwatch("Beta");
        final Stopwatch sw3 = addNamedStopwatch("Gamma");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        // Remove row 0 (sw1)
        displayLapsPanel.buttonAction(3)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "0"));

        assertSame(sw3, stopwatchPanel.getCurrentStopwatch(),
                "After removing a non-last stopwatch, currentStopwatch should be the last in the list");
    }

    @Test
    @DisplayName("buttonAction REMOVE sets start button text to START when list becomes empty")
    void testButtonActionRemoveLastSetsStartButtonToStart()
    {
        final Stopwatch sw1 = addNamedStopwatch("Only");
        stopwatchPanel.setCurrentStopwatch(sw1);

        displayLapsPanel.updateLabelsAndStopwatchTable();
        triggerViewAllButton();

        displayLapsPanel.buttonAction(3)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "0"));

        assertEquals(START, stopwatchPanel.getStartButton().getText(),
                "Start button should revert to START when the last stopwatch is removed");
    }

    // ───────────────────────────────────────────────────
    // Helpers
    // ───────────────────────────────────────────────────

    /** Creates a stopwatch, registers it with the clock and makes it the current stopwatch. */
    private Stopwatch addStopwatchToPanel(String name)
    {
        final Stopwatch sw = new Stopwatch(name, false, false, clock);
        clock.getListOfStopwatches().add(sw);
        stopwatchPanel.setCurrentStopwatch(sw);
        return sw;
    }

    /** Creates a stopwatch and registers it with the clock without changing the current stopwatch. */
    private Stopwatch addNamedStopwatch(String name)
    {
        final Stopwatch sw = new Stopwatch(name, false, false, clock);
        clock.getListOfStopwatches().add(sw);
        return sw;
    }

    /** Fires the action listeners of the "View All" button, making stopwatchTable available. */
    private void triggerViewAllButton()
    {
        Arrays.stream(displayLapsPanel.getComponents())
                .filter(c -> c instanceof JButton && VIEW_ALL.equals(((JButton) c).getText()))
                .findFirst()
                .ifPresent(c ->
                {
                    final ActionEvent e = new ActionEvent(c, ActionEvent.ACTION_PERFORMED, VIEW_ALL);
                    for (final ActionListener al : ((JButton) c).getActionListeners())
                    {
                        al.actionPerformed(e);
                    }
                });
    }

    /** Returns the first JScrollPane found among the panel's direct children, or null. */
    private JScrollPane findScrollPane(JPanel panel)
    {
        return Arrays.stream(panel.getComponents())
                .filter(c -> c instanceof JScrollPane)
                .map(c -> (JScrollPane) c)
                .findFirst()
                .orElse(null);
    }

    /** Returns the JTable inside the first JScrollPane of the panel, or null. */
    private JTable findTableInScrollPane(JPanel panel)
    {
        final JScrollPane scrollPane = findScrollPane(panel);
        if (scrollPane == null) return null;
        final Component view = scrollPane.getViewport().getView();
        return view instanceof JTable ? (JTable) view : null;
    }

    /** Returns the first JButton with the given text from the panel's direct children, or null. */
    private JButton findButtonByText(JPanel panel, String text)
    {
        return Arrays.stream(panel.getComponents())
                .filter(c -> c instanceof JButton && text.equals(((JButton) c).getText()))
                .map(c -> (JButton) c)
                .findFirst()
                .orElse(null);
    }
}

