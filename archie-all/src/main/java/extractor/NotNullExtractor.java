package extractor;

public class NotNullExtractor implements TextExtractor {
    /**
     * Remove general useless node.
     */
    @Override
    public String extract(String text) {
        if (text != null && !text.contains("@ internal") && !text.equals("*") && !text.equals("@ internal @")) {
            return text;
        } else {
            return null;
        }
    }
}
