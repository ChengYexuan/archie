package adlparser;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.profile.AomPropertyMapping;
import com.nedap.archie.query.AOMPathQuery;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ADLParserTest {

    @Test
    public void testGetDescription() throws ADLParseException, IOException {
        ADLParser parser = new ADLParser();
        InputStream inputStream = Files.newInputStream(Paths.get("/home/chengyexuan/IdeaProjects/archie/archie-all/src/test/java/adlparser/openEHR-EHR-COMPOSITION.encounter.v1.0.7.adls"));
        Archetype archetype = parser.parse(inputStream);

        // AOMPathQuery query = new AOMPathQuery("/content[at0001]/item[at0001]/value");
        // ArchetypeModelObject object = query.find(archetype.getDefinition());
        // CComplexObject def = archetype.getDefinition();
        // ArchetypeModelObject sameObject = archetype.getDefinition().itemAtPath("/context[id9002]/other_context[id2]/items[id3]");
        // CAttribute attribute = archetype.getDefinition().getAttribute("context").getChild("id1").getAttribute("items");
        // System.out.println(archetype.getArchetypeId());

    }
}
