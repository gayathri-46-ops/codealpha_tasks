import java.util.*;

public class StockTradingPlatform {
    static Scanner in = new Scanner(System.in);
    static Map<String, Stock> market = new LinkedHashMap<>();
    static Map<String, Integer> holdings = new LinkedHashMap<>();
    static Map<String, Double> averageCost = new LinkedHashMap<>();
    static List<String> history = new ArrayList<>();
    static double cash = 10000.0;
    static final double STARTING_CASH = 10000.0;

    static class Stock {
        String symbol, company;
        double price;
        Stock(String s, String c, double p) { symbol=s; company=c; price=p; }
    }

    public static void main(String[] args) {
        market.put("ALP", new Stock("ALP", "Alpha Tech", 125.50));
        market.put("BTA", new Stock("BTA", "Beta Motors", 82.75));
        market.put("GAM", new Stock("GAM", "Gamma Energy", 46.20));
        market.put("DLT", new Stock("DLT", "Delta Retail", 210.00));
        market.put("OMX", new Stock("OMX", "Omega Health", 94.35));
        System.out.println("=== STOCK TRADING PLATFORM (SIMULATION ONLY) ===");
        boolean running = true;
        while (running) {
            System.out.printf("\\nCash: $%.2f%n1 Market data\\n2 Buy shares\\n3 Sell shares\\n4 Portfolio\\n5 Simulate market movement\\n6 Transaction history\\n7 Exit%n", cash);
            int choice = readInt("Choose: ", 1, 7);
            switch (choice) {
                case 1 -> showMarket();
                case 2 -> trade(true);
                case 3 -> trade(false);
                case 4 -> portfolio();
                case 5 -> movePrices();
                case 6 -> showHistory();
                case 7 -> { running=false; System.out.println("Goodbye!"); }
            }
        }
    }

    static void showMarket() {
        System.out.println("\\nSYMBOL  COMPANY                 PRICE");
        for (Stock s : market.values()) System.out.printf("%-7s %-23s $%.2f%n", s.symbol, s.company, s.price);
    }

    static Stock chooseStock() {
        showMarket();
        System.out.print("Enter stock symbol: ");
        Stock s = market.get(in.nextLine().trim().toUpperCase(Locale.ROOT));
        if (s == null) System.out.println("Unknown stock symbol.");
        return s;
    }

    static void trade(boolean buy) {
        Stock s = chooseStock();
        if (s == null) return;
        int owned = holdings.getOrDefault(s.symbol, 0);
        int max = buy ? 1000000 : owned;
        if (max == 0) { System.out.println("You do not own shares of this stock."); return; }
        int quantity = readInt("Quantity: ", 1, max);
        double total = quantity * s.price;
        if (buy) {
            if (total > cash) { System.out.println("Not enough cash. Cost: $" + String.format("%.2f", total)); return; }
            double oldCost = averageCost.getOrDefault(s.symbol, s.price) * owned;
            holdings.put(s.symbol, owned + quantity);
            averageCost.put(s.symbol, (oldCost + total) / (owned + quantity));
            cash -= total;
            history.add("BUY " + quantity + " " + s.symbol + " at $" + String.format("%.2f", s.price));
            System.out.printf("Bought %d shares for $%.2f%n", quantity, total);
        } else {
            holdings.put(s.symbol, owned - quantity);
            if (owned == quantity) { holdings.remove(s.symbol); averageCost.remove(s.symbol); }
            cash += total;
            history.add("SELL " + quantity + " " + s.symbol + " at $" + String.format("%.2f", s.price));
            System.out.printf("Sold %d shares for $%.2f%n", quantity, total);
        }
    }

    static void portfolio() {
        double value = 0, cost = 0;
        System.out.println("\\n=== PORTFOLIO ===");
        if (holdings.isEmpty()) System.out.println("No shares owned yet.");
        for (String symbol : holdings.keySet()) {
            int q = holdings.get(symbol);
            Stock s = market.get(symbol);
            double marketValue = q * s.price, purchaseValue = q * averageCost.get(symbol);
            value += marketValue; cost += purchaseValue;
            System.out.printf("%s | shares: %d | value: $%.2f | unrealized P/L: $%.2f%n",
                    symbol, q, marketValue, marketValue - purchaseValue);
        }
        System.out.printf("Cash: $%.2f | Holdings: $%.2f | Total account: $%.2f | Overall P/L: $%.2f%n",
                cash, value, cash + value, cash + value - STARTING_CASH);
    }

    static void movePrices() {
        System.out.println("Simulated price movement (fictional prices):");
        for (Stock s : market.values()) {
            s.price = Math.max(0.01, s.price * (1 + (Math.random() * 0.10 - 0.05)));
            System.out.printf("%s: $%.2f%n", s.symbol, s.price);
        }
        history.add("Simulated market price update");
    }

    static void showHistory() {
        System.out.println("\\n=== TRANSACTION HISTORY ===");
        if (history.isEmpty()) System.out.println("No transactions yet.");
        else for (int i = 0; i < history.size(); i++) System.out.println((i+1) + ". " + history.get(i));
    }

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int n = Integer.parseInt(in.nextLine().trim());
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Enter a whole number from %d to %d.%n", min, max);
        }
    }
}
