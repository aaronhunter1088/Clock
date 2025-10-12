package clock.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import static clock.util.Constants.EMPTY;

/**
 * The ButtonColumn class provides a renderer and an editor that looks like a
 * JButton. The renderer and editor will then be used for a specified column
 * in the table. The TableModel will contain the String to be displayed on
 * the button.
 * The button can be invoked by a mouse click or by pressing the space bar
 * when the cell has focus. Optionally a mnemonic can be set to invoke the
 * button. When the button is invoked, the provided Action is invoked. The
 * source of the Action will be the table. The action command will contain
 * the model row number of the button that was clicked.
 *
 * @author Michael Ball
 * @version since 2.9
 */
public class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener
{
    private final JTable table;
    private final Action action;
    private int mnemonic;
    private final Border originalBorder;
    private Border focusBorder;

    private final JButton renderButton;
    private final JButton editButton;
    private Object editorValue;

    /**
     *  Create the ButtonColumn to be used as a renderer and editor. The
     *  renderer and editor will automatically be installed on the table
     *  of the specified column.
     *  @param table the table containing the button renderer/editor
     *  @param action the Action to be invoked when the button is invoked
     *  @param column the column to which the button renderer/editor is
     *                added, 0 indexed
     */
    public ButtonColumn(JTable table, Action action, int column)
    {
        this.table = table;
        this.action = action;

        renderButton = new JButton();
        editButton = new JButton();
        renderButton.setOpaque(true);
        editButton.setOpaque(true);
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);
        originalBorder = editButton.getBorder();
        setFocusBorder(new LineBorder(Color.BLUE));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    /**
     *  Get foreground color of the button
     *  when the cell has focus.
     *  @return the foreground color
     */
    public Border getFocusBorder()
    {
        return focusBorder;
    }

    /**
     *  The foreground color of the button when
     *  the cell has focus.
     *  @param focusBorder the foreground color
     */
    public void setFocusBorder(Border focusBorder)
    {
        this.focusBorder = focusBorder;
        editButton.setBorder(focusBorder);
    }

    /**
     * Get the mnemonic to activate the button
     * @return the mnemonic
     */
    public int getMnemonic()
    {
        return mnemonic;
    }

    /**
     *  The mnemonic to activate the button
     *  when the cell has focus.
     *  @param mnemonic the mnemonic
     */
    public void setMnemonic(int mnemonic)
    {
        this.mnemonic = mnemonic;
        renderButton.setMnemonic(mnemonic);
        editButton.setMnemonic(mnemonic);
    }

    /**
     * This method is called when a cell in the table
     * is edited by the user.
     * @param table the <code>JTable</code> that is asking the
     *              editor to edit.
     * @param value the value of the cell to be edited; it is
     *              up to the specific editor to interpret
     *              and draw the value.  For example, if value is
     *              the string "true", it could be rendered as a
     *              string or it could be rendered as a check
     *              box that is checked.  <code>null</code>
     *              is a valid value
     * @param isSelected true if the cell is to be rendered with
     *                   highlighting
     * @param row the row of the cell being edited
     * @param column the column of the cell being edited
     * @return the <code>Component</code> that should be used for editing
     */
    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column)
    {
        if (value == null)
        {
            editButton.setText(EMPTY);
            editButton.setIcon(null);
        }
        else if (value instanceof Icon)
        {
            editButton.setText(EMPTY);
            editButton.setIcon((Icon)value);
        }
        else
        {
            editButton.setText(value.toString());
            editButton.setIcon(null);
        }

        this.editorValue = value;
        editButton.setForeground(Color.WHITE);
        editButton.setBackground(Color.BLUE);
        return editButton;
    }

    /**
     * This method is called when the cell editing is stopped.
     * @return the value to be stored in the cell.
     */
    @Override
    public Object getCellEditorValue()
    {
        return editorValue;
    }

    /**
     * This method is called when a cell value is edited by the user.
     * @param table the <code>JTable</code> that is asking the
     *              renderer to draw.
     * @param value the value of the cell to be rendered.  It is
     *              up to the specific renderer to interpret
     *              and draw the value.  For example, if
     *              <code>value</code>
     *              is the string "true", it could be rendered as a
     *              string or it could be rendered as a check
     *              box that is checked.  <code>null</code> is a
     *              valid value
     * @param isSelected true if the cell is to be rendered with the
     *                   selection highlighted; otherwise false
     * @param hasFocus if true, render cell appropriately.  For
     *                 example, put a special border on the cell, if
     *                 the cell can be edited, render in the color used
     *                 to indicate editing
     * @param row the row index of the cell being drawn.  When
     *            drawing the header, the value of
     *            <code>row</code> is -1
     * @param column the column index of the cell being drawn
     *
     * @return the <code>Component</code> used for drawing the cell.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            renderButton.setForeground(Color.BLACK);
            renderButton.setBackground(Color.WHITE);
        }
        else
        {
            renderButton.setForeground(Color.BLACK);
            renderButton.setBackground(Color.WHITE);
        }

        if (hasFocus)
        {
            renderButton.setBorder(focusBorder);
        }
        else
        {
            renderButton.setBorder(originalBorder);
        }

        if (value == null)
        {
            renderButton.setText(EMPTY);
            renderButton.setIcon(null);
        }
        else if (value instanceof Icon)
        {
            renderButton.setText(EMPTY);
            renderButton.setIcon((Icon)value);
        }
        else
        {
            renderButton.setText(value.toString());
            renderButton.setIcon(null);
        }

        return renderButton;
    }

    /**
     * The button has been pressed. Stop editing
     * and invoke the custom Action.
     */
    public void actionPerformed(ActionEvent e)
    {
        int row = table.convertRowIndexToModel(table.getEditingRow());
        fireEditingStopped();

        //  Invoke the Action
        ActionEvent event = new ActionEvent(
                table,
                ActionEvent.ACTION_PERFORMED,
                EMPTY + row);
        action.actionPerformed(event);
    }
}
