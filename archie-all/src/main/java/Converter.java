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

    /**
     * Convert parsed archetypes to formatted nodes using {@link #adl2node(Archetype)}} on each archetype.
     * @param archetypes parsed archetypes
     * @return List of formatted nodes of Class-ArchetypeNode.
     */
    public List<ArchetypeNode> clean(List<Archetype> archetypes) {
        List<ArchetypeNode> res = new ArrayList<>();
        for (Archetype archetype : archetypes) {
            res.add(adl2node(archetype));
        }
        return res;
    }

    /**
     * Clean the parsed archetypes and output as JSON.
     * @param archetypes to be used in {@link #clean(List)}
     * @param fileName output file name
     * @param FLAG 0 for es bulk import, 1 for general use
     */
    public void adl2json(List<Archetype> archetypes, String fileName, int FLAG) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            List<ArchetypeNode> nodes = clean(archetypes);
            for (ArchetypeNode node : nodes) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(node);
                switch (FLAG){
                    case 0:{
                        fileWriter.write("{ \"create\": { } }\n");
                        fileWriter.flush();
                    }
                    case 1: {
                        fileWriter.write(json);
                        fileWriter.flush();
                        fileWriter.write("\n");
                        fileWriter.flush();
                    }
                }
            }
        }
    }


    private ArchetypeNode adl2node(Archetype archetype) {
        // TODO: implement iterator
        ArchetypeNode node = new ArchetypeNode(archetype.getArchetypeId().getFullId(),
                archetype.getArchetypeId().getConceptId(),
                archetype.getParentArchetypeId(),
                archetype.getDefinition().getRmTypeName());

        // dealing with description
        try {
            // either getDescription() or getDetails().get("en") is null
            ResourceDescriptionItem des = archetype.getDescription().getDetails().get("en");
            node.setPurpose(des.getPurpose());
            node.setUse(des.getUse());
            node.setMisuse(des.getMisuse());
            node.setKeywords(des.getKeywords());
        } catch (NullPointerException e) {
            node.setNull();
        }

        // dealing with data elements
        if (archetype.getTerminology().getTermDefinitions().get("en") != null) {
            Map<String, ArchetypeTerm> items = archetype.getTerminology().getTermDefinitions().get("en");
            for (Map.Entry<String, ArchetypeTerm> entry : items.entrySet()
            ) {
                if (entry.getKey().contains("id")) {
                    ArchetypeTerm item = entry.getValue();
                    node.addDataElement(item.getText(), item.getDescription(), item.get("comment"));
                }
            }
        } else {
//            System.out.println(node.getName());
            node.setDataElements(null);
        }

        return node;
    }
}
