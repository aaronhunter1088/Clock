package org.example.clock;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/** This is the Timer panel.
 * The timer panel is used to set a timer.
 * Giving the panel some Hours, some Minutes,
 * and some Seconds, it will display the panel
 * when the timer reaches '00:00:00'.
 *
 * @author michael ball
 * @version 2.6
 */
public class TimerPanel extends JPanel implements ClockConstants, IClockPanel {
    private static final Logger logger = LogManager.getLogger(TimerPanel.class);
    GridBagLayout layout;
    GridBagConstraints constraints;
    JTextField textField1, /*Hour textField*/ textField2, /*Min textField*/ textField3; /*Second textField*/
    JButton timerButton;   // Set Timer button
    JButton resetButton;   // Reset Timer button
    Clock clock;           // Reference to the clock
    Thread countdownThread = new Thread(this::performCountDown);
    boolean timerIsGoingOff;
    AdvancedPlayer musicPlayer;
    PanelType panelType;


    TimerPanel(Clock clock) {
        super();
        setClock(clock);
        setPanelType(PanelType.TIMER);
        setSize(Clock.panelSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK); // Color.BLACK
        setForeground(Color.WHITE);
        setBorder(new LineBorder(Color.BLACK));
        setupTimerPanel();
        setupTimerButton();
        setupResetButton();
        updateLabels();
        addComponentsToPanel();
        setupMusicPlayer();
        setupSettingsMenu();
        // allows the user to click around the options and if timer values
        // are valid, enables the Set button
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
                    getTimerButton().setEnabled(true);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        logger.info("Finished creating the Timer Panel");
    }

    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JTextField getJTextField1() { return this.textField1; }
    public JTextField getJTextField2() { return this.textField2; }
    public JTextField getJTextField3() { return this.textField3; }
    public JButton getTimerButton() { return this.timerButton; }
    public JButton getResetButton() { return this.resetButton; }
    public boolean isTimerGoingOff() { return timerIsGoingOff; }
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }

    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setJTextField1(JTextField textField1) { this.textField1 = textField1; }
    protected void setJTextField2(JTextField textField2) { this.textField2 = textField2; }
    protected void setJTextField3(JTextField textField3) { this.textField3 = textField3; }
    protected void setTimerButton(JButton timerButton) { this.timerButton = timerButton; }
    protected void setResetButton(JButton resetButton) { this.resetButton = resetButton; }
    protected void setIsTimerGoingOff(boolean timerIsGoingOff) { this.timerIsGoingOff = timerIsGoingOff; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    @Override
    public void setClock(Clock clock) { this.clock = clock; }
    @Override
    public void setPanelType(PanelType panelType) { this.panelType = panelType; }
    // Helper methods
    public void setupTimerPanel() {
        logger.info("setupTimerPanel");
        setJTextField1(new JTextField("Hour", 4));
        setJTextField2(new JTextField("Min", 4));
        setJTextField3(new JTextField("Sec", 4));
        getJTextField1().setSize(new Dimension(50, 50));
        getJTextField2().setSize(new Dimension(50, 50));
        getJTextField3().setSize(new Dimension(50, 50));
        getJTextField1().addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            {
                //if (!validateFirstTextField())
                //{
                    getJTextField1().setText("");
                //}
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (StringUtils.isBlank(getJTextField1().getText()) ||
                    StringUtils.isEmpty(getJTextField1().getText()))
                {
                    getJTextField1().setText("Hour");
                }
                if (NumberUtils.isNumber(getJTextField1().getText()))
                {
                    if (Integer.parseInt(getJTextField1().getText()) < 24 &&
                        Integer.parseInt(getJTextField1().getText()) >= 0)
                    {
                        getTimerButton().setText("Set");
                        getTimerButton().repaint();
                        getTimerButton().updateUI();
                    }
                    if (Integer.parseInt(getJTextField1().getText()) >= 24 ||
                        Integer.parseInt(getJTextField1().getText()) < 0)
                    {
                        getTimerButton().setText("Hour between 0 and 24");
                        getTimerButton().repaint();
                        getTimerButton().updateUI();
                        getJTextField1().grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
                {
                    getTimerButton().setEnabled(true);
                }
                else if (validateFirstTextField() && !validateSecondTextField())
                {
                    getJTextField2().grabFocus();
                    getTimerButton().setEnabled(false);
                }
                else if (validateFirstTextField() && validateSecondTextField() && !validateThirdTextField())
                {
                    getJTextField3().grabFocus();
                    getTimerButton().setEnabled(false);
                }
        }
        });
        getJTextField2().addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            {
                //if (!validateSecondTextField())
                //{
                    getJTextField2().setText("");
                //}
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (StringUtils.isBlank(getJTextField2().getText()) ||
                    StringUtils.isEmpty(getJTextField2().getText()))
                {
                    getJTextField2().setText("Min");
                }
                if (NumberUtils.isNumber(getJTextField2().getText()))
                {
                    if (Integer.parseInt(getJTextField2().getText()) < 60 &&
                        Integer.parseInt(getJTextField2().getText()) >= 0)
                    {
                        getTimerButton().setText("Set");
                        getTimerButton().repaint();
                        getTimerButton().updateUI();
                    }
                    if (Integer.parseInt(getJTextField2().getText()) >= 60 ||
                        Integer.parseInt(getJTextField2().getText()) < 0)
                    {
                        getTimerButton().setText("Min between 0 and 60");
                        getTimerButton().repaint();
                        getTimerButton().updateUI();
                        getJTextField2().grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
                {
                    getTimerButton().setEnabled(true);
                }
                else if (!validateFirstTextField())
                {
                    getJTextField1().grabFocus();
                    getTimerButton().setEnabled(false);
                }
            }
        });
        getJTextField3().addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            {
                //if (!validateThirdTextField())
                //{
                    getJTextField3().setText("");
                //}
                if (validateThirdTextField()) getTimerButton().setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (StringUtils.isBlank(getJTextField3().getText()) ||
                    StringUtils.isEmpty(getJTextField3().getText()))
                {
                    getJTextField3().setText("Sec");
                }
                if (NumberUtils.isNumber(getJTextField3().getText()))
                {
                    if (Integer.parseInt(getJTextField3().getText()) < 60 &&
                        Integer.parseInt(getJTextField3().getText()) >= 0)
                    {
                        getTimerButton().setText("Set");
                        getTimerButton().repaint();
                        getTimerButton().updateUI();
                    }
                    if (Integer.parseInt(getJTextField3().getText()) >= 60 ||
                        Integer.parseInt(getJTextField3().getText()) < 0)
                    {
                        getTimerButton().setText("Sec between 0 and 60");
                        getTimerButton().repaint();
                        getTimerButton().updateUI();
                        getJTextField3().grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
                {
                    getTimerButton().setEnabled(true);
                }
                else if (!validateFirstTextField())
                {
                    getJTextField1().grabFocus();
                    getTimerButton().setEnabled(false);
                }
                else if (!validateSecondTextField())
                {
                    getJTextField2().grabFocus();
                    getTimerButton().setEnabled(false);
                }
            }

        });
        getJTextField1().setBorder(new LineBorder(Color.WHITE));
        getJTextField2().setBorder(new LineBorder(Color.WHITE));
        getJTextField3().setBorder(new LineBorder(Color.WHITE));
        getJTextField1().setHorizontalAlignment(JTextField.CENTER);
        getJTextField2().setHorizontalAlignment(JTextField.CENTER);
        getJTextField3().setHorizontalAlignment(JTextField.CENTER);
        setTimerButton(new JButton(SET));
        getTimerButton().setFont(Clock.font20);
        getTimerButton().setOpaque(true);
        getTimerButton().setBackground(Color.BLACK);
        getTimerButton().setForeground(Color.WHITE);
        getTimerButton().setBorder(new LineBorder(Color.WHITE));
        getTimerButton().setEnabled(false);
        setResetButton(new JButton(RESET));
        getResetButton().setFont(Clock.font20);
        getResetButton().setOpaque(true);
        getResetButton().setBackground(Color.BLACK);
        getResetButton().setForeground(Color.WHITE);
        getResetButton().setBorder(new LineBorder(Color.WHITE));
        setBackground(Color.BLACK);
    }
    protected void setupTimerButton() { getTimerButton().addActionListener(this::run); }
    protected void setupResetButton() { getResetButton().addActionListener(this::resetTimerFields); }
    public boolean validateFirstTextField() {
        logger.info("validateFirstTextField");
        if (StringUtils.equals(getJTextField1().getText(), "Hour")) { return true; }
        if (!NumberUtils.isNumber(getJTextField1().getText())) { return false; }
        // cannot be more than 23 hours or less than 0
        return Integer.parseInt(getJTextField1().getText()) < 24 &&
                Integer.parseInt(getJTextField1().getText()) >= 0;
    }
    public boolean validateSecondTextField() {
        logger.info("validateSecondTextField");
        if (StringUtils.equals(getJTextField2().getText(), "Min")) { return true; }
        if (!NumberUtils.isNumber(getJTextField2().getText())) { return false; }
        // 70 minutes given
        return Integer.parseInt(getJTextField2().getText()) < 60 &&
                Integer.parseInt(getJTextField2().getText()) >= 0;
    }
    public boolean validateThirdTextField() {
        logger.info("validateThirdTextField");
        if (StringUtils.equals(getJTextField3().getText(), "Sec")) { return true;}
        if (!NumberUtils.isNumber(getJTextField3().getText())) { return false; }
        // 70 seconds given
        return Integer.parseInt(getJTextField3().getText()) < 60 &&
                Integer.parseInt(getJTextField3().getText()) >= 0;
    }
    public void startTimer() {
        logger.info("startTimer");
        getTimerButton().setText(PAUSE_TIMER);
        getTimerButton().repaint();
        getTimerButton().updateUI();
        //logger.error(countdownThread.getName() + " is in state: " + countdownThread.getState());
        if (countdownThread.getState() == Thread.State.NEW) {
            countdownThread.start(); // calls the run method of this class
            //setTimerHasConcluded(false);
            getClock().setIsTimerGoingOff(false);
        }
        else if (countdownThread.getState() == Thread.State.TERMINATED) {
            countdownThread = new Thread(this::performCountDown);
            countdownThread.start();
        }
        else if (countdownThread.getState() == Thread.State.TIMED_WAITING) {
            countdownThread.notify();
        }
    }
    public void pauseTimer() throws InterruptedException {
        logger.info("pauseTimer");
        getTimerButton().setText(RESUME_TIMER);
        getTimerButton().repaint();
        getTimerButton().updateUI();
        countdownThread.interrupt();
    }
    public void performCountDown() {
        logger.info("performCountDown");
        try {
            // get total number of seconds to count down
            //int totalSeconds = Integer.parseInt(getJTextField3().getText());
            //totalSeconds += (Integer.parseInt(getJTextField2().getText()) * 60);
            //totalSeconds += (Integer.parseInt(getJTextField1().getText()) * (60*60));
            // reduce the total, keeping hours, minutes, and seconds current
            if (StringUtils.equals(getJTextField1().getText(), "Hour")) getJTextField1().setText("0");
            if (StringUtils.equals(getJTextField2().getText(), "Min")) getJTextField2().setText("0");
            if (StringUtils.equals(getJTextField3().getText(), "Sec")) getJTextField3().setText("0");

            while (Integer.parseInt(getJTextField3().getText()) > 0 ||
                    Integer.parseInt(getJTextField2().getText()) > 0 ||
                    Integer.parseInt(getJTextField1().getText()) > 0 ) {
                if (Integer.parseInt(getJTextField3().getText()) >= 0) {
                    int seconds = Integer.parseInt(getJTextField3().getText()) - 1;
                    getJTextField3().setText(Integer.toString(seconds));
                    // check hours and minutes, to see if they now need to be decreased
                    if (Integer.parseInt(getJTextField3().getText()) < 0 && Integer.parseInt(getJTextField2().getText()) >= 0) {
                        getJTextField3().setText(Integer.toString(59));
                        int minutes = Integer.parseInt(getJTextField2().getText()) - 1;
                        getJTextField2().setText(Integer.toString(minutes));
                        if (Integer.parseInt(getJTextField2().getText()) < 0 && Integer.parseInt(getJTextField1().getText()) > 0) {
                            getJTextField2().setText(Integer.toString(59));
                            int hours = Integer.parseInt(getJTextField1().getText()) - 1;
                            getJTextField1().setText(Integer.toString(hours));
                        }
                    }
                }
                sleep(1000);
            }
            // when done counting, change the Set button back to say Set
            getTimerButton().setText(SET);
            getTimerButton().setEnabled(false);
            //setTimerHasConcluded(true);
            getClock().setIsTimerGoingOff(true);
        }
        catch (Exception e) { printStackTrace(e, null); }
    }
    public void resetTimerFields(ActionEvent action) {
        logger.info("resetTimerFields");
        getJTextField1().setText("Hour");
        getJTextField2().setText("Min");
        getJTextField3().setText("Sec");
        getTimerButton().setText(SET);
        getTimerButton().setEnabled(false);
        //countdownThread = new Thread(this::performCountDown);
    }
    public void printStackTrace(Exception e, String message) {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace()) { logger.error(ste.toString()); }
    }
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill) {
        logger.debug("addComponent");
        setGridBagConstraints(new GridBagConstraints());
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().fill = fill;
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().fill = fill;
        getGridBagConstraints().weightx = 0;
        getGridBagConstraints().weighty = 0;
        getGridBagConstraints().insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }
    public void checkIfTimerHasConcluded() {
        logger.info("checkIfTimerHasConcluded");
        ExecutorService executor = Executors.newCachedThreadPool();
        if (getClock().isTimerGoingOff()) {
            triggerSound(executor);
            getClock().setIsTimerGoingOff(false);
        }
    }
    public void triggerSound(ExecutorService executor) {
        logger.info("triggering timer sound");
        setIsTimerGoingOff(true);
        // play sound
        Callable<String> c = () -> {
            try {
                setupMusicPlayer();
                getMusicPlayer().play(50);
                stopSound();
                //logger.error("Alarm is going off.");
                return "Timer triggered";
            }
            catch (Exception e) {
                logger.error(e.getCause().getClass().getName() + ": " + e.getMessage());
                printStackTrace(e, null);
                setupMusicPlayer();
                getMusicPlayer().play(50);
                stopSound();
                return "Reset music player req'd";
            }
        };
        executor.submit(c);
    }
    public void stopSound() {
        logger.info("stopSound");
        setMusicPlayer(null);
        setIsTimerGoingOff(false);
        logger.info("timer turned off");
    }

    @Override
    public void addComponentsToPanel() {
        logger.info("addComponentsToPanel");
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        addComponent(getJTextField1(), 0,0,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 1
        addComponent(getJTextField2(), 0,1,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 2
        addComponent(getJTextField3(), 0,2,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 3
        addComponent(getResetButton(), 1, 0, 3, 1, 0, 0, GridBagConstraints.HORIZONTAL); // Reset Button
        addComponent(getTimerButton(), 2,0,3,1,0,0, GridBagConstraints.HORIZONTAL); // Set Timer Button
    }

    public void updateLabels() {
        logger.info("updateLabels");
        getJTextField1().grabFocus();
        getClock().repaint();
    }

    public void run(ActionEvent action) { // called when we hit the Start Timer button
        logger.info("run");
        try {
            if (StringUtils.equals(getTimerButton().getText(), "Set") ||
                StringUtils.equals(getTimerButton().getText(), "Resume Timer")) {
                logger.info("starting timer");
                startTimer();
            }
            else {
                logger.info("pausing timer");
                pauseTimer();
            }
        }
        catch (InterruptedException e) { printStackTrace(e, null); }
    }

    public void setupMusicPlayer() {
        logger.info("setupAlarmButton");
        InputStream inputStream;
        try {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { setMusicPlayer(new AdvancedPlayer(inputStream)); }
            else throw new NullPointerException();
        }
        catch (NullPointerException | JavaLayerException e) {
            if (e instanceof NullPointerException)
                printStackTrace(e, "An issue occurred while reading the alarm file.");
            else
                printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
        }
        logger.info("Alarm button set!");
    }

    protected void setupSettingsMenu() {
        clock.getClockMenuBar().getSettingsMenu().removeAll();
        logger.info("No settings set for the Timer Panel");
    }
}
