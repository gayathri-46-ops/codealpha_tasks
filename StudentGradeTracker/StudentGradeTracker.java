import java.util.*;

public class StudentGradeTracker {
    static Scanner in = new Scanner(System.in);
    static List<Student> students = new ArrayList<>();

    static class Student {
        String name;
        List<Double> grades = new ArrayList<>();
        Student(String name) { this.name = name; }
        double average() { return grades.stream().mapToDouble(Double::doubleValue).average().orElse(0); }
        double highest() { return grades.stream().mapToDouble(Double::doubleValue).max().orElse(0); }
        double lowest() { return grades.stream().mapToDouble(Double::doubleValue).min().orElse(0); }
    }

    public static void main(String[] args) {
        System.out.println("=== STUDENT GRADE TRACKER ===");
        boolean running = true;
        while (running) {
            System.out.println("\\n1 Add student\\n2 Add grade\\n3 Student report\\n4 Class summary\\n5 Exit");
            int choice = readInt("Choose: ", 1, 5);
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> addGrade();
                case 3 -> report();
                case 4 -> summary();
                case 5 -> { running = false; System.out.println("Goodbye!"); }
            }
        }
    }

    static void addStudent() {
        System.out.print("Student name: ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        for (Student s : students) if (s.name.equalsIgnoreCase(name)) {
            System.out.println("Student already exists."); return;
        }
        students.add(new Student(name));
        System.out.println("Student added.");
    }

    static Student chooseStudent() {
        if (students.isEmpty()) { System.out.println("Add a student first."); return null; }
        for (int i = 0; i < students.size(); i++) System.out.println((i + 1) + ". " + students.get(i).name);
        return students.get(readInt("Student number: ", 1, students.size()) - 1);
    }

    static void addGrade() {
        Student s = chooseStudent();
        if (s == null) return;
        double grade;
        while (true) {
            System.out.print("Grade (0-100): ");
            try {
                grade = Double.parseDouble(in.nextLine().trim());
                if (grade >= 0 && grade <= 100 && Double.isFinite(grade)) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a number from 0 to 100.");
        }
        s.grades.add(grade);
        System.out.println("Grade added.");
    }

    static void report() {
        Student s = chooseStudent();
        if (s == null) return;
        System.out.println("\\nName: " + s.name + "\\nGrades: " + s.grades);
        if (s.grades.isEmpty()) System.out.println("No grades entered.");
        else System.out.printf("Average: %.2f | Highest: %.2f | Lowest: %.2f%n",
                s.average(), s.highest(), s.lowest());
    }

    static void summary() {
        if (students.isEmpty()) { System.out.println("No students yet."); return; }
        double sum = 0, high = Double.NEGATIVE_INFINITY, low = Double.POSITIVE_INFINITY;
        int count = 0;
        System.out.println("\\n=== CLASS SUMMARY ===");
        for (Student s : students) {
            System.out.print(s.name + " | grades: " + s.grades);
            if (s.grades.isEmpty()) System.out.println(" | no grades");
            else {
                System.out.printf(" | average %.2f%n", s.average());
                for (double g : s.grades) { sum += g; count++; high = Math.max(high, g); low = Math.min(low, g); }
            }
        }
        if (count > 0) System.out.printf("Overall average: %.2f | Highest: %.2f | Lowest: %.2f%n", sum/count, high, low);
        else System.out.println("No grades entered yet.");
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
