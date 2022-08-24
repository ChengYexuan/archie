import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> keywords = new ArrayList<>();
        keywords.add("Blood pressure");
        keywords.add("blood");
        keywords.add("pressure");
        System.out.println(keywords);
        String word = String.join(",", keywords);
        System.out.println(word.toLowerCase());
    }
}
