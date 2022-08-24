import com.nedap.archie.adl14.*;
import com.nedap.archie.aom.Archetype;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.File;
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

//        boolean FLAG = false;
        boolean FLAG = true;

        if(FLAG){
            ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
            ADL14Converter adlConverter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());

//            File fileParent = new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/test/resources");
            File fileParent = new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/main/resources/adls");
            File[] fileNames = fileParent.listFiles();

            List<Archetype> archetypes14 = new ArrayList<>();
            // parse all archetypes into list
            for(File file: Objects.requireNonNull(fileNames)) {
                try(InputStream stream = Files.newInputStream(Paths.get(file.getPath()))) {
                    Archetype archetype = parser.parse(stream, conversionConfiguration);
                    if(parser.getErrors().hasNoErrors()) {
                        archetypes14.add(archetype);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<Archetype> archetypes = new ArrayList<>();
            ADL2ConversionResultList resultList = adlConverter.convert(archetypes14);
            for(ADL2ConversionResult adl2ConversionResult:resultList.getConversionResults()) {
                if(adl2ConversionResult.getException() == null) {
                    // convertedArchetype is the ADL 2 conversion result. Additional warning messages in adl2ConversionResult.getLog()
                    Archetype convertedArchetype = adl2ConversionResult.getArchetype();
                    archetypes.add(convertedArchetype);
                }
            }

            System.out.println(archetypes.size() + " archetypes were parsed!");
            /*if(archetypes.size() == fileNames.length){
                docNum = fileNames.length;
                System.out.println("All of " + docNum + " archetypes were parsed!");
            }*/

            // 清洗并导出成json

//            IndexUtil.converter.adl2json(archetypes);

            IndexUtil.writeIndex(archetypes);
        }

        List<String> queries = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("/home/chengyexuan/IdeaProjects/archie/archie-all/src/main/resources/queries.txt"))) {
            while (scanner.hasNextLine()) {
                queries.add(scanner.nextLine());
            }
        }

        IndexUtil.readIndex();


//        IndexUtil.termSearch("item", "comment", 5);
        IndexUtil.bulkSearch(queries);
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
