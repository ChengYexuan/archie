import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nedap.archie.aom.ArchetypeHRID;

import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"name", "concept", "parent", "purpose", "use", "misuse", "keywords", "dataElements"})
public class ArchetypeNode {

    public String name;
    public String concept;
    public String parent;

    public String purpose;
    public String use;
    public String misuse;
    public List<String> keywords;

    public List<DataElement> dataElements;

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public void setConcept(String concept) {
        this.concept = concept;
    }

    @JsonProperty
    public void setParent(String parent) {
        this.parent = parent;
    }

    @JsonProperty
    public void setPurpose(String purpose) {
        if(!Objects.equals(purpose, "@ internal @")){
            this.purpose = purpose;
        } else {
            this.purpose = null;
        }
    }

    @JsonProperty
    public void setUse(String use) {
        if(!Objects.equals(use, "@ internal @")){
            this.use = use;
        } else {
            this.use = null;
        }
    }

    @JsonProperty
    public void setMisuse(String misuse) {
        if(!Objects.equals(misuse, "@ internal @")){
            this.misuse = misuse;
        } else {
            this.misuse = null;
        }
    }

    @JsonProperty
    public void setKeywords(List<String> keywords) {
        if (keywords == null || Objects.equals(keywords.get(0), "@ internal @")){
            this.keywords = null;
        } else {
            this.keywords = keywords;
        }
    }

    @JsonProperty
    public void setDataElements(List<DataElement> dataElements) {
        this.dataElements = dataElements;
    }

    public String getName() {
        return name;
    }

    public String getConcept() {
        return concept;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String kwString() {
        if (this.getKeywords() == null) return "null";
        return String.join(",", keywords);
}

    public List<DataElement> getDataElements() {
        return dataElements;
    }

    private String deString() {
        String content = null;
        if(dataElements == null) return "null";
        for (DataElement i : dataElements
             ) {
            content = String.join(",", content, i.getComment(), i.getDescription(), i.getText());
        }
        return content;
    }

    public String getParent() {
        return parent;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getUse() {
        return use;
    }

    public String getMisuse() {
        return misuse;
    }

    public void setNull() {
        this.purpose = null;
        this.use = null;
        this.misuse = null;
        this.keywords = null;
    }

    public String toString(){
        String content;
        content= String.join(",", getMisuse(), getUse(), getPurpose(), kwString(), deString());
        return content;
    }
}
