package extractor;

public class SimpleExtractor implements TextExtractor {
    /**
     * Remove all the punctuations and trim the phrase.
     * For example: "Exam cancelled?" --> "exam cancelled"
     */
    @Override
    public String extract(String text) {
        return text.replaceAll("\\p{Punct}", " ").toLowerCase().trim();
    }

}
