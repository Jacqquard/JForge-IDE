import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Main extends JFrame {

    private JTextPane editorPane;
    private JTextArea consoleArea;
    private File currentFile;
    private UndoManager undoManager;

    public static final Color DARK_BG_PRIMARY = new Color(43, 43, 43);
    public static final Color DARK_BG_SECONDARY = new Color(50, 50, 50);
    public static final Color DARK_EDITOR_BG = new Color(30, 30, 30);
    public static final Color DARK_CONSOLE_BG = new Color(35, 35, 35);
    public static final Color DARK_TEXT_FG = new Color(210, 210, 210);
    public static final Color DARK_LINE_NUMBER_BG = new Color(45, 45, 45);
    public static final Color DARK_LINE_NUMBER_FG = new Color(100, 100, 100);
    public static final Color DARK_LINE_NUMBER_BORDER = new Color(60, 60, 60);
    public static final Color DARK_SELECTION_BG = new Color(75, 110, 175);
    public static final Color DARK_SELECTION_FG = DARK_TEXT_FG;
    public static final Color DARK_CARET_FG = new Color(220, 220, 170);

    private static final Color KEYWORD_COLOR = new Color(204, 120, 50);
    private static final Color COMMENT_COLOR = new Color(128, 128, 128);
    private static final Color STRING_COLOR = new Color(106, 135, 89);
    private static final Color DEFAULT_COLOR = DARK_TEXT_FG;

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
            "int", "interface", "long", "native", "new", "package", "private", "protected",
            "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
            "true", "false", "null"
    ));


    public Main() {
        applyDarkUIManagerSettings();
        setTitle("JFORGE");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setupMenu();
        setupWindowListener();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyDarkUIManagerSettings() {
        UIManager.put("Panel.background", DARK_BG_PRIMARY);
        UIManager.put("Viewport.background", DARK_EDITOR_BG);
        UIManager.put("Frame.background", DARK_BG_PRIMARY);
        UIManager.put("Window.background", DARK_BG_PRIMARY);

        UIManager.put("control", DARK_BG_SECONDARY);
        UIManager.put("text", DARK_TEXT_FG);
        UIManager.put("info", DARK_TEXT_FG);

        UIManager.put("Button.background", new Color(70, 73, 75));
        UIManager.put("Button.foreground", DARK_TEXT_FG);
        UIManager.put("Button.select", new Color(90, 93, 95));
        UIManager.put("Button.focus", new Color(DARK_SELECTION_BG.getRed(), DARK_SELECTION_BG.getGreen(), DARK_SELECTION_BG.getBlue(), 50));
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(80,80,80)));

        UIManager.put("TextPane.background", DARK_EDITOR_BG);
        UIManager.put("TextPane.foreground", DARK_TEXT_FG);
        UIManager.put("TextPane.caretForeground", DARK_CARET_FG);
        UIManager.put("TextPane.selectionBackground", DARK_SELECTION_BG);
        UIManager.put("TextPane.selectedTextColor", DARK_SELECTION_FG);

        UIManager.put("TextArea.background", DARK_CONSOLE_BG);
        UIManager.put("TextArea.foreground", DARK_TEXT_FG);
        UIManager.put("TextArea.caretForeground", DARK_CARET_FG);
        UIManager.put("TextArea.selectionBackground", DARK_SELECTION_BG);
        UIManager.put("TextArea.selectedTextColor", DARK_SELECTION_FG);
        
        UIManager.put("TextField.background", DARK_BG_SECONDARY);
        UIManager.put("TextField.foreground", DARK_TEXT_FG);
        UIManager.put("TextField.caretForeground", DARK_CARET_FG);
        UIManager.put("TextField.selectionBackground", DARK_SELECTION_BG);
        UIManager.put("TextField.selectedTextColor", DARK_SELECTION_FG);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(DARK_LINE_NUMBER_BORDER));
        
        UIManager.put("MenuBar.background", DARK_BG_PRIMARY);
        UIManager.put("MenuBar.foreground", DARK_TEXT_FG);
        UIManager.put("MenuBar.border", BorderFactory.createLineBorder(DARK_LINE_NUMBER_BORDER));
        UIManager.put("Menu.background", DARK_BG_PRIMARY);
        UIManager.put("Menu.foreground", DARK_TEXT_FG);
        UIManager.put("Menu.selectionBackground", DARK_SELECTION_BG);
        UIManager.put("Menu.selectionForeground", DARK_SELECTION_FG);
        UIManager.put("Menu.acceleratorForeground", new Color(170, 170, 170));
        UIManager.put("Menu.border", BorderFactory.createEmptyBorder(2,5,2,5));
        UIManager.put("MenuItem.background", DARK_BG_PRIMARY);
        UIManager.put("MenuItem.foreground", DARK_TEXT_FG);
        UIManager.put("MenuItem.selectionBackground", DARK_SELECTION_BG);
        UIManager.put("MenuItem.selectionForeground", DARK_SELECTION_FG);
        UIManager.put("MenuItem.acceleratorForeground", new Color(170, 170, 170));
        UIManager.put("MenuItem.acceleratorSelectionForeground", DARK_SELECTION_FG);
        UIManager.put("MenuItem.border", BorderFactory.createEmptyBorder(2,5,2,5));
        UIManager.put("PopupMenu.background", DARK_BG_PRIMARY);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(DARK_LINE_NUMBER_BORDER));

        Color scrollBarTrackColor = DARK_BG_SECONDARY;
        Color scrollBarThumbColor = new Color(100, 100, 100);
        Color scrollBarThumbShadow = new Color(70, 70, 70);
        Color scrollBarThumbHighlight = new Color(130, 130, 130);

        UIManager.put("ScrollBar.background", scrollBarTrackColor);
        UIManager.put("ScrollBar.foreground", DARK_TEXT_FG);
        UIManager.put("ScrollBar.track", scrollBarTrackColor);
        UIManager.put("ScrollBar.thumb", scrollBarThumbColor); 
        
        UIManager.put("ScrollBar.thumbShadow", scrollBarThumbShadow);
        UIManager.put("ScrollBar.thumbDarkShadow", scrollBarThumbShadow.darker());
        UIManager.put("ScrollBar.thumbHighlight", scrollBarThumbHighlight);
        
        UIManager.put("ScrollBar.trackHighlight", scrollBarTrackColor); 
        
        UIManager.put("ScrollBar.width", 15);
        UIManager.put("ScrollBar.border", BorderFactory.createEmptyBorder());


        UIManager.put("SplitPane.background", DARK_BG_PRIMARY);
        UIManager.put("SplitPane.dividerSize", 7);
        UIManager.put("SplitPaneDivider.background", DARK_BG_SECONDARY);
        UIManager.put("SplitPane.border", BorderFactory.createEmptyBorder());

        UIManager.put("OptionPane.background", DARK_BG_PRIMARY);
        UIManager.put("OptionPane.messageForeground", DARK_TEXT_FG);
        UIManager.put("FileChooser.background", DARK_BG_PRIMARY);
        UIManager.put("List.background", DARK_EDITOR_BG);
        UIManager.put("List.foreground", DARK_TEXT_FG);
        UIManager.put("List.selectionBackground", DARK_SELECTION_BG);
        UIManager.put("List.selectionForeground", DARK_SELECTION_FG);
        UIManager.put("Label.foreground", DARK_TEXT_FG);

        UIManager.put("ScrollPane.background", DARK_BG_PRIMARY);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    private void initComponents() {
        editorPane = new JTextPane();
        editorPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editorPane.setBackground(DARK_EDITOR_BG);
        editorPane.setForeground(DARK_TEXT_FG);
        editorPane.setCaretColor(DARK_CARET_FG);
        editorPane.setSelectionColor(DARK_SELECTION_BG);
        editorPane.setSelectedTextColor(DARK_SELECTION_FG);
        editorPane.setMargin(new Insets(5,5,5,5));

        undoManager = new UndoManager();
        editorPane.getDocument().addUndoableEditListener(undoManager);

        editorPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyHighlighting(); }
            public void removeUpdate(DocumentEvent e) { applyHighlighting(); }
            public void changedUpdate(DocumentEvent e) { /* Plain text components do not fire these */ }
        });

        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setBorder(BorderFactory.createEmptyBorder());

        TextLineNumber tln = new TextLineNumber(editorPane, this);
        editorScrollPane.setRowHeaderView(tln);

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consoleArea.setBackground(DARK_CONSOLE_BG);
        consoleArea.setForeground(DARK_TEXT_FG);
        consoleArea.setCaretColor(DARK_CARET_FG);
        consoleArea.setSelectionColor(DARK_SELECTION_BG);
        consoleArea.setSelectedTextColor(DARK_SELECTION_FG);
        consoleArea.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane consoleScrollPane = new JScrollPane(consoleArea);
        consoleScrollPane.setBorder(BorderFactory.createMatteBorder(1,0,0,0, DARK_LINE_NUMBER_BORDER));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScrollPane, consoleScrollPane);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this);
                divider.setBackground(DARK_BG_SECONDARY);
                divider.setBorder(BorderFactory.createEmptyBorder());
                return divider;
            }
        });
        splitPane.setDividerLocation(getHeight() * 3/4);
        add(splitPane, BorderLayout.CENTER);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> newFile());
        fileMenu.add(newItem);
        JMenuItem openItem = new JMenuItem("Open...");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());
        fileMenu.add(openItem);
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveFile());
        fileMenu.add(saveItem);
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        saveAsItem.addActionListener(e -> saveFileAs());
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> { if (undoManager.canUndo()) undoManager.undo(); });
        editMenu.add(undoItem);
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> { if (undoManager.canRedo()) undoManager.redo(); });
        editMenu.add(redoItem);
        menuBar.add(editMenu);
        JMenu buildMenu = new JMenu("Build");
        JMenuItem compileRunItem = new JMenuItem("Compile and Run");
        compileRunItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        compileRunItem.addActionListener(e -> compileAndRun());
        buildMenu.add(compileRunItem);
        menuBar.add(buildMenu);
        setJMenuBar(menuBar);
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    private boolean isEditorModified() {
        if (currentFile == null) { return !editorPane.getText().isEmpty(); }
        try {
            String fileContent = new String(Files.readAllBytes(currentFile.toPath()));
            return !editorPane.getText().equals(fileContent);
        } catch (IOException e) { return !editorPane.getText().isEmpty(); }
    }

    private boolean confirmClose() {
        if (isEditorModified()) {
             int result = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Do you want to save before closing?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                saveFile(); return !isEditorModified();
            } else if (result == JOptionPane.CANCEL_OPTION) { return false; }
        }
        return true;
    }

    private void newFile() {
        if (!confirmClose()) return;
        editorPanesetTextWithNoUndo("");
        currentFile = null;
        setTitle("JFORGE - New File");
        applyHighlighting();
    }

    private void openFile() {
        if (!confirmClose()) return;
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(currentFile.toPath()));
                editorPanesetTextWithNoUndo(content);
                setTitle("JFORGE - " + currentFile.getName());
                applyHighlighting();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                currentFile = null; setTitle("JFORGE");
            }
        }
    }
    
    private void editorPanesetTextWithNoUndo(String text) {
        editorPane.getDocument().removeUndoableEditListener(undoManager);
        editorPane.setText(text);
        editorPane.getDocument().addUndoableEditListener(undoManager);
        undoManager.discardAllEdits();
        editorPane.setCaretPosition(0);
    }

    private boolean saveFileLogic(File fileToSave) {
        try {
            Files.write(fileToSave.toPath(), editorPane.getText().getBytes());
            currentFile = fileToSave; setTitle("JFORGE - " + currentFile.getName()); return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean saveFile() {
        if (currentFile == null) { return saveFileAs(); } else { return saveFileLogic(currentFile); }
    }

    private boolean saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        if (currentFile != null) { fileChooser.setSelectedFile(currentFile); }
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".java")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".java");
            }
            return saveFileLogic(fileToSave);
        }
        return false;
    }

    private void exitApplication() { if (confirmClose()) { System.exit(0); } }

    private void compileAndRun() {
        consoleArea.setText("");
        File fileToCompile; String classToRun; String sourceFileName; Path sourcePath;
        if (currentFile != null && currentFile.getName().toLowerCase().endsWith(".java")) {
            if (isEditorModified()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "The current file '" + currentFile.getName() + "' has unsaved changes. Save before compiling?",
                        "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    if (!saveFile()) { consoleArea.append("Save failed or cancelled. Compilation aborted.\n"); return; }
                } else if (choice == JOptionPane.CANCEL_OPTION) { consoleArea.append("Compilation aborted by user.\n"); return; }
            }
             try { Files.write(currentFile.toPath(), editorPane.getText().getBytes());
            } catch (IOException e) { consoleArea.append("Error saving to " + currentFile.getName() + " before compilation: " + e.getMessage() + "\n"); return; }
            fileToCompile = currentFile; sourcePath = fileToCompile.toPath(); sourceFileName = fileToCompile.getName();
            classToRun = sourceFileName.substring(0, sourceFileName.lastIndexOf('.'));
            consoleArea.append("Preparing to compile: " + sourceFileName + " (Class: " + classToRun + ")\n");
        } else {
            sourceFileName = "TempClass.java"; Path tempDir = Paths.get(System.getProperty("user.dir"));
            sourcePath = tempDir.resolve(sourceFileName); fileToCompile = sourcePath.toFile(); classToRun = "TempClass";
            consoleArea.append("No saved Java file context. Using temporary file: " + sourcePath.toAbsolutePath() + "\n");
            consoleArea.append("IMPORTANT: Your public class in the editor MUST be named 'TempClass' for this to compile and run.\n");
            try { Files.write(sourcePath, editorPane.getText().getBytes());
            } catch (IOException e) { consoleArea.append("Error saving temporary file '" + sourceFileName + "': " + e.getMessage() + "\n"); return; }
        }
        consoleArea.append("Compiling " + sourceFileName + "...\n");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) { consoleArea.append("JDK not found. Please ensure a JDK is installed and on the system PATH (not just a JRE).\n"); return; }
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        int compilationResult = compiler.run(null, null, errStream, sourcePath.toAbsolutePath().toString());
        String compileErrors = errStream.toString();
        if (!compileErrors.isEmpty()) { consoleArea.append("Compilation Output (Errors/Warnings):\n" + compileErrors + "\n"); }
        if (compilationResult == 0) {
            consoleArea.append("Compilation successful.\nRunning " + classToRun + "...\n\n");
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", sourcePath.getParent().toString(), classToRun);
                pb.redirectErrorStream(true); Process process = pb.start();
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line; while ((line = reader.readLine()) != null) {
                            final String outputLine = line; SwingUtilities.invokeLater(() -> consoleArea.append(outputLine + "\n"));
                        }
                    } catch (IOException ex) { SwingUtilities.invokeLater(() -> consoleArea.append("Error reading program output: " + ex.getMessage() + "\n")); }
                    try { int exitCode = process.waitFor(); SwingUtilities.invokeLater(() -> consoleArea.append("\nProgram finished with exit code: " + exitCode + "\n"));
                    } catch (InterruptedException ex) { Thread.currentThread().interrupt(); SwingUtilities.invokeLater(() -> consoleArea.append("Program execution interrupted.\n")); }
                }).start();
            } catch (IOException e) { consoleArea.append("Error running class '" + classToRun + "': " + e.getMessage() + "\n"); }
        } else { consoleArea.append("Compilation failed.\n"); }
    }

    public void triggerSyntaxHighlighting() { applyHighlighting(); }

    private void applyHighlighting() {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = editorPane.getStyledDocument(); String text;
            try { text = doc.getText(0, doc.getLength()); } catch (BadLocationException e) { return; }
            SimpleAttributeSet defaultAttrs = new SimpleAttributeSet();
            StyleConstants.setForeground(defaultAttrs, DEFAULT_COLOR);
            StyleConstants.setBold(defaultAttrs, false); StyleConstants.setItalic(defaultAttrs, false);
            doc.setCharacterAttributes(0, text.length(), defaultAttrs, true);
            Style keywordStyle = doc.addStyle("KeywordStyle", null);
            StyleConstants.setForeground(keywordStyle, KEYWORD_COLOR); StyleConstants.setBold(keywordStyle, true);
            Pattern keywordPattern = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");
            Matcher keywordMatcher = keywordPattern.matcher(text);
            while (keywordMatcher.find()) { doc.setCharacterAttributes(keywordMatcher.start(), keywordMatcher.end() - keywordMatcher.start(), keywordStyle, false); }
            Style commentStyle = doc.addStyle("CommentStyle", null);
            StyleConstants.setForeground(commentStyle, COMMENT_COLOR); StyleConstants.setItalic(commentStyle, true);
            Pattern singleLineCommentPattern = Pattern.compile("//.*");
            Matcher singleLineCommentMatcher = singleLineCommentPattern.matcher(text);
            while (singleLineCommentMatcher.find()) { doc.setCharacterAttributes(singleLineCommentMatcher.start(), singleLineCommentMatcher.end() - singleLineCommentMatcher.start(), commentStyle, false); }
            Pattern multiLineCommentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
            Matcher multiLineCommentMatcher = multiLineCommentPattern.matcher(text);
            while (multiLineCommentMatcher.find()) { doc.setCharacterAttributes(multiLineCommentMatcher.start(), multiLineCommentMatcher.end() - multiLineCommentMatcher.start(), commentStyle, false); }
            Style stringStyle = doc.addStyle("StringStyle", null);
            StyleConstants.setForeground(stringStyle, STRING_COLOR);
            Pattern stringPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
            Matcher stringMatcher = stringPattern.matcher(text);
            while (stringMatcher.find()) { doc.setCharacterAttributes(stringMatcher.start(), stringMatcher.end() - stringMatcher.start(), stringStyle, false); }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { Main ide = new Main(); ide.setVisible(true); });
    }
}

class TextLineNumber extends JComponent implements DocumentListener, ComponentListener, PropertyChangeListener {
    private final JTextComponent component;
    private final Main ideInstance;
    private int lastDigits;
    private int lastLine;
    private static final int PADDING = 5;

    public TextLineNumber(JTextComponent component, Main ideInstance) {
        this.component = component;
        this.ideInstance = ideInstance;
        setFont(component.getFont());
        setBackground(Main.DARK_LINE_NUMBER_BG);
        setForeground(Main.DARK_LINE_NUMBER_FG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Main.DARK_LINE_NUMBER_BORDER));
        setOpaque(true);
        lastDigits = 0; lastLine = 0;
        component.getDocument().addDocumentListener(this);
        component.addComponentListener(this);
        component.addPropertyChangeListener("font", this);
        documentChanged();
    }

    private int calculateWidthForDigits(int digits) {
        FontMetrics currentTlnFontMetrics = getFontMetrics(getFont());
        int charWidth = currentTlnFontMetrics.charWidth('0');
        Insets currentTlnInsets = getInsets();
        return currentTlnInsets.left + (charWidth * digits) + PADDING + currentTlnInsets.right;
    }
    
    private void updatePreferredWidth() {
        int lines = component.getDocument().getDefaultRootElement().getElementCount();
        int newDigits = Math.max(String.valueOf(lines).length(), 2);
        if (lastDigits != newDigits) {
            lastDigits = newDigits; int newWidth = calculateWidthForDigits(newDigits);
            Dimension currentPreferredSize = getPreferredSize();
            if (currentPreferredSize == null || currentPreferredSize.width != newWidth) {
                setPreferredSize(new Dimension(newWidth, 0));
                 if (getParent() != null) { getParent().revalidate(); }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension ps = super.getPreferredSize();
        if (ps == null || ps.width == 0) { updatePreferredWidth(); ps = super.getPreferredSize(); }
        return new Dimension(ps.width, component.isVisible() ? component.getHeight() : 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics painterFontMetrics = g.getFontMetrics();
        if (component.getDocument().getLength() == 0 || painterFontMetrics == null) { return; }
        g.setColor(getForeground());
        Rectangle clip = g.getClipBounds(); if (clip == null) return;
        int startOffset = component.viewToModel2D(new Point(0, clip.y));
        int endOffset = component.viewToModel2D(new Point(0, clip.y + clip.height));
        Element map = component.getDocument().getDefaultRootElement();
        Insets tlnInsets = getInsets();
        int firstLineToDraw = map.getElementIndex(startOffset);
        int lastLineToDraw = map.getElementIndex(endOffset);

        for (int i = firstLineToDraw; i <= lastLineToDraw; i++) {
            try {
                Element lineElement = map.getElement(i); if (lineElement == null) continue;
                Shape shape = component.modelToView2D(lineElement.getStartOffset());
                if (shape == null) continue;
                Rectangle lineBounds = shape.getBounds();

                if (lineBounds.y + lineBounds.height >= clip.y && lineBounds.y <= clip.y + clip.height) {
                    String lineNumber = String.valueOf(i + 1);
                    int stringWidth = painterFontMetrics.stringWidth(lineNumber);
                    int x = getWidth() - tlnInsets.right - PADDING - stringWidth;
                    int y = lineBounds.y + painterFontMetrics.getAscent(); 
                    g.drawString(lineNumber, x, y);
                }
            } catch (BadLocationException e) { break; }
        }
    }

    private void documentChanged() {
        SwingUtilities.invokeLater(() -> {
            if (component.getDocument() == null) return;
            updatePreferredWidth();
            int newLines = component.getDocument().getDefaultRootElement().getElementCount();
            if (lastLine != newLines) { lastLine = newLines; }
            repaint();
        });
    }

    @Override public void insertUpdate(DocumentEvent e) { documentChanged(); if (ideInstance != null) ideInstance.triggerSyntaxHighlighting(); }
    @Override public void removeUpdate(DocumentEvent e) { documentChanged(); if (ideInstance != null) ideInstance.triggerSyntaxHighlighting(); }
    @Override public void changedUpdate(DocumentEvent e) { documentChanged(); }
    @Override public void componentResized(ComponentEvent e) { repaint(); }
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) { documentChanged(); }
    @Override public void componentHidden(ComponentEvent e) {}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("font".equals(evt.getPropertyName())) {
            if (component != null) {
                 setFont(component.getFont());
            }
            documentChanged();
        }
    }
}
