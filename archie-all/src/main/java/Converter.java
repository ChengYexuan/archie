import com.fasterxml.jackson.databind.ObjectMapper;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.terminology.ArchetypeTerm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Converter {

    public void adl2json(List<Archetype> archetypes) throws IOException {
        // json file
        try(FileWriter fileWriter = new FileWriter("archetype.json")){
            for(Archetype archetype:archetypes) {

                ArchetypeNode node = adl2node(archetype);

                // to json
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(node);

                // to file
                fileWriter.write("{ \"create\": { } }\n");
                fileWriter.flush();
                fileWriter.write(json);
                fileWriter.flush();
                fileWriter.write("\n");
                fileWriter.flush();
            }
        }
    }

    public ArchetypeNode adl2node(Archetype archetype) {
        ArchetypeNode node = new ArchetypeNode();

        // set attributes
        node.setName(archetype.getArchetypeId().getFullId());
        node.setParent(archetype.getParentArchetypeId());
        node.setConcept(archetype.getArchetypeId().getConceptId());

        // dealing with description
        try {
            // either desc or "en" is null
            ResourceDescriptionItem des = archetype.getDescription().getDetails().get("en");
            node.setPurpose(des.getPurpose());
            node.setUse(des.getUse());
            node.setMisuse(des.getMisuse());
            node.setKeywords(des.getKeywords());
        } catch (NullPointerException e){
            node.setNull();
        }

        // dealing with data elements
        if(archetype.getTerminology().getTermDefinitions().get("en") != null){
            Map<String, ArchetypeTerm> items = archetype.getTerminology().getTermDefinitions().get("en");
            List<DataElement> dataElements = new ArrayList<>();
            for(ArchetypeTerm item: items.values()){
                DataElement dataElement = new DataElement(item.getText(), item.getDescription(), item.get("comment"));
                dataElements.add(dataElement);
            }
            node.setDataElements(dataElements);
        } else {
            node.setDataElements(null);
        }

        return node;
    }
}
