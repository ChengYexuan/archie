import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"text", "description", "comment"})
public class DataElement{

    public String text;
    public String description;
    public String comment;

    @JsonProperty
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public void setComment(String comment) {
        this.comment = comment;
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
        text = text.replaceAll("\\p{Punct}", " ").toLowerCase().trim();
        this.setText(text);
        if(comment != null && !comment.contains("@ internal")){
            this.setComment(comment);
        } else {
            this.setComment(null);
        }
        if(description!=null && !description.contains("@ internal")){
            this.setDescription(description);
        } else {
            this.setDescription(null);
        }
    }
}
