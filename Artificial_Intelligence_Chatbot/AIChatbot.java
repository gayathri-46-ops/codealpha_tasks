import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.Normalizer;
import java.util.*;
import java.util.List;

/**
 * Simple AI-style chatbot built with Java Swing.
 * NLP features: text normalization, tokenization, stop-word filtering,
 * keyword/intent scoring, and rule-based responses.
 *
 * Run:
 *   javac -encoding UTF-8 src/AIChatbot.java
 *   java -cp src AIChatbot
 */
public class AIChatbot {
    private final JFrame frame = new JFrame("AI Chatbot");
    private final JTextArea conversation = new JTextArea();
    private final JTextField input = new JTextField();
    private final Map<String, Intent> intents = new LinkedHashMap<>();

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "is", "am", "are", "was", "were", "be", "been",
        "being", "i", "me", "my", "you", "your", "we", "our", "it", "this",
        "that", "to", "of", "for", "in", "on", "at", "and", "or", "but",
        "do", "does", "did", "can", "could", "would", "should", "please",
        "tell", "me", "about", "what", "how", "who", "when", "where"
    ));

    private static class Intent {
        final String name;
        final Set<String> keywords;
        final String response;

        Intent(String name, String response, String... keywords) {
            this.name = name;
            this.response = response;
            this.keywords = new HashSet<>(Arrays.asList(keywords));
        }
    }

    public AIChatbot() {
        loadKnowledgeBase();
        buildGui();
    }

    private void loadKnowledgeBase() {
        add("greeting", "Hello! 😊 How can I help you today?",
            "hello", "hi", "hey", "greetings", "good morning", "good evening");
        add("name", "I'm JavaBot, a simple AI-style chatbot built with Java Swing.",
            "name", "who are you", "yourself", "java bot");
        add("purpose", "I can answer common questions about Java, NLP, AI, and this project.",
            "help", "purpose", "what can you do", "features", "capabilities");
        add("java", "Java is a general-purpose, object-oriented programming language that runs on the JVM.",
            "java", "jvm", "programming language");
        add("ai", "Artificial Intelligence (AI) is the field of building systems that perform tasks associated with human intelligence.",
            "ai", "artificial intelligence", "intelligence");
        add("nlp", "Natural Language Processing (NLP) helps computers process human language. This bot uses normalization, tokenization, and keyword matching.",
            "nlp", "natural language processing", "language processing");
        add("machine learning", "This demo uses transparent rule-based intent matching rather than a trained ML model. You can extend it with a classifier later.",
            "machine learning", "ml", "training", "model");
        add("project", "Project: Java-based chatbot with a Swing GUI, an editable rule-based knowledge base, and lightweight NLP.",
            "project", "assignment", "source code", "how it works");
        add("thanks", "You're welcome! Happy to help. 🙂",
            "thanks", "thank you", "thx");
        add("goodbye", "Goodbye! Have a great day. 👋",
            "bye", "goodbye", "see you", "exit", "quit");
        add("time", "I don't access your system clock in this demo, but you can add a Java date/time response easily.",
            "time", "date", "clock");
    }

    private void add(String name, String response, String... keywords) {
        intents.put(name, new Intent(name, response, keywords));
    }

    private void buildGui() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(620, 480));
        frame.setSize(760, 560);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel("Java AI Chatbot");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Rule-based intent matching + lightweight NLP");
        subtitle.setForeground(new Color(90, 100, 115));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 3));
        header.setOpaque(false);
        header.add(heading);
        header.add(subtitle);

        conversation.setEditable(false);
        conversation.setLineWrap(true);
        conversation.setWrapStyleWord(true);
        conversation.setFont(new Font("SansSerif", Font.PLAIN, 14));
        conversation.setBackground(Color.WHITE);
        conversation.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(conversation);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 232)));

        input.setFont(new Font("SansSerif", Font.PLAIN, 14));
        input.setToolTipText("Type a message and press Enter");
        JButton send = new JButton("Send");
        send.setFont(new Font("SansSerif", Font.BOLD, 14));
        send.addActionListener(this::sendMessage);
        input.addActionListener(this::sendMessage);

        JPanel composer = new JPanel(new BorderLayout(8, 0));
        composer.setOpaque(false);
        composer.add(input, BorderLayout.CENTER);
        composer.add(send, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(composer, BorderLayout.SOUTH);
        frame.setContentPane(root);

        append("JavaBot", "Hi! I'm JavaBot. Ask me about Java, AI, NLP, or this project.");
        frame.setVisible(true);
        input.requestFocusInWindow();
    }

    private void sendMessage(ActionEvent event) {
        String message = input.getText().trim();
        if (message.isEmpty()) return;

        append("You", message);
        input.setText("");
        String response = generateResponse(message);
        append("JavaBot", response);
    }

    private void append(String speaker, String message) {
        conversation.append(speaker + ": " + message + "\n\n");
        conversation.setCaretPosition(conversation.getDocument().getLength());
    }

    private String generateResponse(String message) {
        String normalized = normalize(message);
        if (normalized.isEmpty()) {
            return "I didn't catch that. Could you try rephrasing?";
        }

        Set<String> tokens = tokenize(normalized);
        Intent bestIntent = null;
        double bestScore = 0.0;

        for (Intent intent : intents.values()) {
            double score = scoreIntent(normalized, tokens, intent);
            if (score > bestScore) {
                bestScore = score;
                bestIntent = intent;
            }
        }

        if (bestIntent != null && bestScore >= 1.0) {
            return bestIntent.response;
        }

        return "I'm not sure how to answer that yet. Try asking about Java, AI, NLP, machine learning, or type 'help'.";
    }

    private String normalize(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        String noAccents = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return noAccents.replaceAll("[^a-z0-9\\s]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
    }

    private Set<String> tokenize(String normalizedText) {
        Set<String> tokens = new LinkedHashSet<>();
        if (normalizedText.isEmpty()) return tokens;
        for (String token : normalizedText.split("\\s+")) {
            if (token.length() > 1 && !STOP_WORDS.contains(token)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private double scoreIntent(String normalized, Set<String> tokens, Intent intent) {
        double score = 0.0;

        // Exact phrase matches are more meaningful than isolated keyword matches.
        for (String keyword : intent.keywords) {
            String normalizedKeyword = normalize(keyword);
            if (normalizedKeyword.contains(" ")) {
                if (normalized.contains(normalizedKeyword)) score += 2.5;
            } else if (tokens.contains(normalizedKeyword)) {
                score += 1.0;
            }
        }

        // Small bonus when a token overlaps the intent name.
        for (String part : intent.name.split("\\s+")) {
            if (tokens.contains(part)) score += 0.5;
        }
        return score;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AIChatbot::new);
    }
}
