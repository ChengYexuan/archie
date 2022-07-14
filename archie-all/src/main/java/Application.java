import com.nedap.archie.adl14.*;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Application {

    public static void main(String[] args) throws IOException {
        ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);

        List<Archetype> archetypes = new ArrayList<>();
        ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());

        File fileParent = new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/test/resources/adls");
        File[] fileNames = fileParent.listFiles();

        for(File file: Objects.requireNonNull(fileNames)) {
            try(InputStream stream = Files.newInputStream(Paths.get(file.getPath()))) {
                Archetype archetype = parser.parse(stream, conversionConfiguration);
                if(parser.getErrors().hasNoErrors()) {
                    archetypes.add(archetype);
                } else {
                    //handle parse error
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ADL2ConversionResultList resultList = converter.convert(archetypes);
        for(ADL2ConversionResult adl2ConversionResult:resultList.getConversionResults()) {
            if(adl2ConversionResult.getException() == null) {
                // convertedArchetype is the ADL 2 conversion result. Additional warning messages in adl2ConversionResult.getLog()
                Archetype convertedArchetype = adl2ConversionResult.getArchetype();
                ResourceDescriptionItem des = convertedArchetype.getDescription().getDetails().get("en");

                String concept = convertedArchetype.getArchetypeId().getConceptId();
                String purpose = des.getPurpose();
                String use = des.getUse();
                String misuse = des.getMisuse();
                List<String> keywords = des.getKeywords();

                Map<String, ArchetypeTerm> items = convertedArchetype.getTerminology().getTermDefinitions().get("en");
                for(ArchetypeTerm item: items.values()){
                    String text = item.get("text");
                    String description = item.get("description");
                    String comment = item.get("comment");
                }
                String parent = convertedArchetype.getParentArchetypeId();
            }
        }
    }
}
