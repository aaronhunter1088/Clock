package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;

import static clock.util.Constants.EMPTY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link ButtonColumn} class.
 * ButtonColumn provides a JButton renderer and editor for a JTable column.
 *
 * @author michael ball
 * @version since 3.0
 */
class ButtonColumnTest
{
    private static final Logger logger = LogManager.getLogger(ButtonColumnTest.class);
    private static AutoCloseable mocks;

    private static final int COLUMN = 0;

    private JTable table;
    private Action mockAction;
    private ButtonColumn buttonColumn;

    @BeforeAll
    static void beforeAll()
    {
        mocks = MockitoAnnotations.openMocks(ButtonColumnTest.class);
        logger.info("Starting ButtonColumnTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        final DefaultTableModel model = new DefaultTableModel(
                new Object[][]{{"Click Me"}},
                new Object[]{"Action"});
        table = new JTable(model);
        mockAction = mock(Action.class);
        buttonColumn = new ButtonColumn(table, mockAction, COLUMN);
    }

    @AfterAll
    static void afterAll() throws Exception
    {
        mocks.close();
        logger.info("Concluding ButtonColumnTest.");
    }

    // ───────────────────────────────────────────────────
    // Constructor
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("Constructor installs ButtonColumn as renderer on the specified column")
    void testConstructorInstallsRenderer()
    {
        assertSame(buttonColumn, table.getColumnModel().getColumn(COLUMN).getCellRenderer(),
                "ButtonColumn should be the cell renderer for the specified column");
    }

    @Test
    @DisplayName("Constructor installs ButtonColumn as editor on the specified column")
    void testConstructorInstallsEditor()
    {
        assertSame(buttonColumn, table.getColumnModel().getColumn(COLUMN).getCellEditor(),
                "ButtonColumn should be the cell editor for the specified column");
    }

    // ───────────────────────────────────────────────────
    // Focus border
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("Default focus border is LineBorder with Color.BLUE")
    void testDefaultFocusBorderIsBlueLineBorder()
    {
        final Border border = buttonColumn.getFocusBorder();
        assertInstanceOf(LineBorder.class, border, "Default focus border should be a LineBorder");
        assertEquals(Color.BLUE, ((LineBorder) border).getLineColor(),
                "Default focus border colour should be BLUE");
    }

    @Test
    @DisplayName("setFocusBorder updates getFocusBorder")
    void testSetFocusBorderUpdatesGetter()
    {
        final Border newBorder = new LineBorder(Color.RED);
        buttonColumn.setFocusBorder(newBorder);
        assertSame(newBorder, buttonColumn.getFocusBorder(),
                "getFocusBorder should return the border that was set");
    }

    // ───────────────────────────────────────────────────
    // Mnemonic
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("getMnemonic returns 0 before any value is set")
    void testGetMnemonicDefaultIsZero()
    {
        assertEquals(0, buttonColumn.getMnemonic(),
                "Mnemonic should default to 0");
    }

    @Test
    @DisplayName("setMnemonic / getMnemonic round-trip")
    void testSetMnemonicRoundTrip()
    {
        buttonColumn.setMnemonic(KeyEvent.VK_B);
        assertEquals(KeyEvent.VK_B, buttonColumn.getMnemonic(),
                "getMnemonic should return the value that was set");
    }

    // ───────────────────────────────────────────────────
    // getTableCellEditorComponent
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("getTableCellEditorComponent with null → empty text and null icon")
    void testEditorComponentNullValue()
    {
        final Component c = buttonColumn.getTableCellEditorComponent(table, null, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        final var btn = (JButton) c;
        assertEquals(EMPTY, btn.getText(), "Text should be empty for null value");
        assertNull(btn.getIcon(), "Icon should be null for null value");
    }

    @Test
    @DisplayName("getTableCellEditorComponent with String → text set, null icon")
    void testEditorComponentStringValue()
    {
        final Component c = buttonColumn.getTableCellEditorComponent(table, "Delete", false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        final var btn = (JButton) c;
        assertEquals("Delete", btn.getText(), "Button text should match the provided string");
        assertNull(btn.getIcon(), "Icon should be null when value is a string");
    }

    @Test
    @DisplayName("getTableCellEditorComponent with Icon → empty text, icon set")
    void testEditorComponentIconValue()
    {
        final Icon icon = mock(Icon.class);
        final Component c = buttonColumn.getTableCellEditorComponent(table, icon, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        final var btn = (JButton) c;
        assertEquals(EMPTY, btn.getText(), "Text should be empty when value is an Icon");
        assertSame(icon, btn.getIcon(), "Button icon should be the provided Icon");
    }

    @Test
    @DisplayName("getTableCellEditorComponent sets WHITE foreground and BLUE background")
    void testEditorComponentColors()
    {
        final Component c = buttonColumn.getTableCellEditorComponent(table, "X", false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        assertEquals(Color.WHITE, ((JButton) c).getForeground(), "Editor button foreground should be WHITE");
        assertEquals(Color.BLUE, ((JButton) c).getBackground(), "Editor button background should be BLUE");
    }

    // ───────────────────────────────────────────────────
    // getCellEditorValue
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("getCellEditorValue returns the value supplied to the last editor render")
    void testGetCellEditorValueReturnsLastValue()
    {
        buttonColumn.getTableCellEditorComponent(table, "Pause", false, 0, COLUMN);
        assertEquals("Pause", buttonColumn.getCellEditorValue(),
                "getCellEditorValue should return the last value passed to the editor");
    }

    @Test
    @DisplayName("getCellEditorValue returns null when editor was rendered with null value")
    void testGetCellEditorValueReturnsNull()
    {
        buttonColumn.getTableCellEditorComponent(table, null, false, 0, COLUMN);
        assertNull(buttonColumn.getCellEditorValue(),
                "getCellEditorValue should return null when null was given to the editor");
    }

    // ───────────────────────────────────────────────────
    // getTableCellRendererComponent
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("getTableCellRendererComponent with null → empty text and null icon")
    void testRendererComponentNullValue()
    {
        final Component c = buttonColumn.getTableCellRendererComponent(table, null, false, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        final var btn = (JButton) c;
        assertEquals(EMPTY, btn.getText(), "Text should be empty for null value");
        assertNull(btn.getIcon(), "Icon should be null for null value");
    }

    @Test
    @DisplayName("getTableCellRendererComponent with String → text set, null icon")
    void testRendererComponentStringValue()
    {
        final Component c = buttonColumn.getTableCellRendererComponent(table, "Pause", false, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        final var btn = (JButton) c;
        assertEquals("Pause", btn.getText(), "Button text should match the provided string");
        assertNull(btn.getIcon(), "Icon should be null when value is a string");
    }

    @Test
    @DisplayName("getTableCellRendererComponent with Icon → empty text, icon set")
    void testRendererComponentIconValue()
    {
        final Icon icon = mock(Icon.class);
        final Component c = buttonColumn.getTableCellRendererComponent(table, icon, false, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        final var btn = (JButton) c;
        assertEquals(EMPTY, btn.getText(), "Text should be empty when value is an Icon");
        assertSame(icon, btn.getIcon(), "Button icon should be the provided Icon");
    }

    @Test
    @DisplayName("getTableCellRendererComponent with hasFocus=true applies the focus border")
    void testRendererComponentHasFocusAppliesFocusBorder()
    {
        final Border focusBorder = buttonColumn.getFocusBorder();
        final Component c = buttonColumn.getTableCellRendererComponent(table, "X", false, true, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        assertSame(focusBorder, ((JButton) c).getBorder(),
                "Render button should use focusBorder when hasFocus is true");
    }

    @Test
    @DisplayName("getTableCellRendererComponent with hasFocus=false restores original border")
    void testRendererComponentNoFocusRestoresOriginalBorder()
    {
        // Apply focus, then remove it — border should revert to original
        buttonColumn.getTableCellRendererComponent(table, "X", false, true, 0, COLUMN);
        final Component c = buttonColumn.getTableCellRendererComponent(table, "X", false, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        assertNotSame(buttonColumn.getFocusBorder(), ((JButton) c).getBorder(),
                "Render button should not use focusBorder when hasFocus is false");
    }

    @Test
    @DisplayName("getTableCellRendererComponent sets BLACK foreground and WHITE background")
    void testRendererComponentColors()
    {
        final Component c = buttonColumn.getTableCellRendererComponent(table, "X", false, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        assertEquals(Color.BLACK, ((JButton) c).getForeground(), "Render button foreground should be BLACK");
        assertEquals(Color.WHITE, ((JButton) c).getBackground(), "Render button background should be WHITE");
    }

    @Test
    @DisplayName("getTableCellRendererComponent with isSelected=true still uses BLACK foreground and WHITE background")
    void testRendererComponentSelectedColors()
    {
        final Component c = buttonColumn.getTableCellRendererComponent(table, "X", true, false, 0, COLUMN);
        assertInstanceOf(JButton.class, c);
        assertEquals(Color.BLACK, ((JButton) c).getForeground(), "Selected render button foreground should be BLACK");
        assertEquals(Color.WHITE, ((JButton) c).getBackground(), "Selected render button background should be WHITE");
    }

    // ────────────���──────────────────────────────────────
    // actionPerformed
    // ───────────────────────────────────────────────────

    @Test
    @DisplayName("actionPerformed invokes the provided Action")
    void testActionPerformedInvokesAction()
    {
        final AtomicReference<ActionEvent> captured = new AtomicReference<>();
        final Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) { captured.set(e); }
        };
        final DefaultTableModel model = new DefaultTableModel(
                new Object[][]{{"Row0"}}, new Object[]{"Action"});
        final JTable t = new JTable(model);
        final ButtonColumn bc = new ButtonColumn(t, action, 0);

        bc.actionPerformed(new ActionEvent(t, ActionEvent.ACTION_PERFORMED, EMPTY));

        assertNotNull(captured.get(), "Action should have been invoked");
    }

    @Test
    @DisplayName("actionPerformed passes the table as the action source")
    void testActionPerformedSourceIsTable()
    {
        final AtomicReference<ActionEvent> captured = new AtomicReference<>();
        final Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) { captured.set(e); }
        };
        final DefaultTableModel model = new DefaultTableModel(
                new Object[][]{{"Row0"}}, new Object[]{"Action"});
        final JTable t = new JTable(model);
        final ButtonColumn bc = new ButtonColumn(t, action, 0);

        bc.actionPerformed(new ActionEvent(t, ActionEvent.ACTION_PERFORMED, EMPTY));

        assertSame(t, captured.get().getSource(),
                "The action source should be the JTable");
    }
}

