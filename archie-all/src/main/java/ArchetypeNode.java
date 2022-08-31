import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import extractor.NotNullExtractor;
import extractor.TextExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"name", "concept", "parent", "purpose", "type", "use", "misuse", "keywords", "dataElements"})
public class ArchetypeNode {

    private String name;
    private String concept;
    private String parent;
    private String purpose;
    private String type;
    private String use;
    private String misuse;
    private List<String> keywords;
    private List<DataElement> dataElements = new ArrayList<>();
    private TextExtractor textExtractor = new NotNullExtractor();

    public ArchetypeNode(String name, String concept, String parent, String type) {
        this.setName(name);
        this.setConcept(concept);
        this.setParent(parent);
        this.setType(type);
    }

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

    @JsonProperty("rmType")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty
    public void setPurpose(String purpose) {
        this.purpose = textExtractor.extract(purpose);
    }

    @JsonProperty
    public void setUse(String use) {
        this.use = textExtractor.extract(use);
    }

    @JsonProperty
    public void setMisuse(String misuse) {
        this.misuse = textExtractor.extract(misuse);
    }

    @JsonProperty("keywords")
    public void setKeywords(List<String> keywords) {
        if (keywords == null || Objects.equals(keywords.get(0), "@ internal @")) {
            this.keywords = null;
        } else {
            this.keywords = keywords;
        }
    }

    @JsonProperty("dataElements")
    public void setDataElements(List<DataElement> dataElements) {
        this.dataElements = dataElements;
    }

    public String getName() {
        return name;
    }

    public String getConcept() {
        return concept;
    }

    public String getParent() {
        return parent;
    }

    public String getType() {
        return type;
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

    public List<String> getKeywords() {
        return keywords;
    }

    public List<DataElement> getDataElements() {
        return dataElements;
    }

    public void setNull() {
        this.purpose = null;
        this.use = null;
        this.misuse = null;
        this.keywords = null;
    }

    public String kwString() {
        if (this.getKeywords() == null) return "null";
        return String.join(",", keywords);
    }

    private String deString() {
        String content = null;
        if (dataElements == null) return "null";
        for (DataElement i : dataElements
        ) {
            content = String.join(",", content, i.getComment(), i.getDescription(), i.getText());
        }
        return content;
    }

    public String toString() {
        String content;
        content = String.join("\n", "name: " + getName(), "misuse: " + getMisuse(), "use: " + getUse(),
                "purpose: " + getPurpose(), "keywords: " + kwString(), "data elements: " + deString());
        return content;
    }

    public void addDataElement(String text, String description, String comment) {
        RM type = RM.valueOf(this.type);
        switch (type) {
            case OBSERVATION:
                if (text.equalsIgnoreCase("Any event") || text.equalsIgnoreCase("Any point in time event")
                        || text.equalsIgnoreCase("Event series") || text.equalsIgnoreCase("Protocol")) {
                    break;
                }
            case INSTRUCTION:
                if (text.equalsIgnoreCase("Activity") || text.equalsIgnoreCase("Current activity")) {
                    break;
                }
            default: {
                if (text.equalsIgnoreCase("Tree") || text.equalsIgnoreCase("Item tree")
                        || text.equalsIgnoreCase("Extension") || text.equalsIgnoreCase("Itemtree")) {
                    break;
                }
                this.dataElements.add(new DataElement(text, description, comment));
            }
        }
    }

    enum RM {
        CLUSTER, OBSERVATION, EVALUATION, INSTRUCTION, ACTION, SECTION, COMPOSITION, ADMIN_ENTRY
    }
}
