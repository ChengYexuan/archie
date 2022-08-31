import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.adl14.*;
import com.nedap.archie.aom.Archetype;
import org.checkerframework.checker.units.qual.C;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
public class Application {
    public static void main(String[] args) throws IOException {
        List<Archetype> archetypes = preprocess();
        Converter converter = new Converter();
        try {
//            converter.adl2json(archetypes,"for_es.json",0);
            converter.adl2json(archetypes,"for_python.json",1);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        IndexUtil.writeIndex(archetypes);
//        List<String> queries = new ArrayList<>();
//        try (Scanner scanner = new Scanner(new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/main/resources/queries.txt"))) {
//            while (scanner.hasNextLine()) {
//                queries.add(scanner.nextLine());
//            }
//        }
//
//        IndexUtil.readIndex();
//
//
//        IndexUtil.termSearch("item", "comment", 5);
//        IndexUtil.bulkSearch(queries);
    }

    /**
     * Preprocess the ADL1.4 files to object in RAM.
     * @return List of Archetype objects.
     */
    static public List<Archetype> preprocess() {
        // configure a parser for ADL1.4 and a converter to ADL2
        ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
        ADL14Converter adlConverter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
        ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());

//        File fileParent = new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/test/resources/failed test");
//        File fileParent = new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/test/resources");
        File fileParent = new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/main/resources/adls");
        File[] fileNames = fileParent.listFiles();

        // parse all ADL1.4 files into a list of archetypes
        List<Archetype> archetypes14 = new ArrayList<>();
        for (File file : Objects.requireNonNull(fileNames)) {
            try (InputStream stream = Files.newInputStream(Paths.get(file.getPath()))) {
                Archetype archetype = parser.parse(stream, conversionConfiguration);
                if (parser.getErrors().hasNoErrors()) {
                    archetypes14.add(archetype);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(archetypes14.size() + " archetypes were parsed!");

        // convert all archetypes to ADL2 archetypes
        List<Archetype> archetypes = new ArrayList<>();
        ADL2ConversionResultList resultList = adlConverter.convert(archetypes14);
        for (ADL2ConversionResult adl2ConversionResult : resultList.getConversionResults()) {
            if (adl2ConversionResult.getException() == null) {
                // convertedArchetype is the ADL 2 conversion result. Additional warning messages in adl2ConversionResult.getLog()
                Archetype convertedArchetype = adl2ConversionResult.getArchetype();
                archetypes.add(convertedArchetype);
            } else {
                System.out.println(adl2ConversionResult.getArchetypeId());
            }
        }
        System.out.println(archetypes.size() + " archetypes were converted!");

//        if(archetypes.size() == 0) return archetypes14;
        return archetypes;
//        return archetypes14;
    }

    /*long getFrequency(int docNum, String term) throws IOException {
        long cp = 0;
        TokenStream tokenStream = null;
        try {
            IndexReader reader = DirectoryReader.open(dir);
            Document doc = reader.document(docNum);
            Analyzer analyzer = new StandardAnalyzer();
            tokenStream = analyzer.tokenStream("T", new StringReader(doc.get("T")));
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                if(charTermAttribute.toString().contains(term)){
                    cp++;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }finally {
            assert tokenStream != null;
            tokenStream.close();
        }

        return cp;
    }*/
}
