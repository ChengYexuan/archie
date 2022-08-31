import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import extractor.NotNullExtractor;
import extractor.TextExtractor;
import extractor.SimpleExtractor;

@JsonPropertyOrder({"text", "description", "comment"})
public class DataElement {

    private String text;
    private String description;
    private String comment;

    private TextExtractor nameExtractor = new SimpleExtractor();
    private TextExtractor textExtractor = new NotNullExtractor();

    void setExtractor(TextExtractor name, TextExtractor text) {
        this.nameExtractor = name;
        this.textExtractor = text;
    }

    @JsonProperty
    public void setText(String text) {
        this.text = nameExtractor.extract(text);
    }

    @JsonProperty
    public void setDescription(String description) {
        this.description = textExtractor.extract(description);
    }

    @JsonProperty
    public void setComment(String comment) {
        this.comment = textExtractor.extract(comment);
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public String getComment() {
        return comment;
    }

    public DataElement(String text, String description, String comment) {
        this.setText(text);
        this.setComment(comment);
        this.setDescription(description);
    }
}
