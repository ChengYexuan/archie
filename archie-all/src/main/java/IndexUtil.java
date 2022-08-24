import com.nedap.archie.aom.Archetype;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.lucene.index.IndexOptions.DOCS_AND_FREQS;

public class IndexUtil {

    static Converter converter = new Converter();
    static IndexReader indexReader;
    static IndexSearcher indexSearcher;
    static Directory dir;
    static int DOC = 771;
    static Map<String, Map<String, Double>> data_element_map = new HashMap<>();
    static Map<String, Map<String, Double>> key_word_map = new HashMap<>();
    static long Mt;
    static long Mk;

    static {
        try {
            dir = FSDirectory.open(Paths.get("/home/chengyexuan/IdeaProjects/archie/archie-all/src/main/resources/index"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static TopDocs termSearch (String fieldName, String term, int n) throws IOException {
        indexReader = DirectoryReader.open(dir);
        indexSearcher = new IndexSearcher(indexReader);
        TermQuery tq = new TermQuery(new Term(fieldName, term));
        try{
            TopDocs hits = indexSearcher.search(tq, n);
            for(ScoreDoc scoreDoc: hits.scoreDocs) {
                System.out.println("Doc #" + scoreDoc.doc + " contains " + "\"" + term  + "\"" + " in the field " + fieldName);
            }
            return hits;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int searchByName (String name) throws IOException {
        indexReader = DirectoryReader.open(dir);
        indexSearcher = new IndexSearcher(indexReader);
        TermQuery tq = new TermQuery(new Term("name", name));
        try{
            TopDocs hits = indexSearcher.search(tq, 1);
            return hits.scoreDocs[0].doc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeIndex(List<Archetype> archetypes) throws IOException {
        IndexWriter indexWriter;
        try {
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(dir, iwc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArchetypeNode node;
        Document document;

        FieldType kwType = new FieldType();
        kwType.setIndexOptions(DOCS_AND_FREQS);
        kwType.setStored(true);
        kwType.setTokenized(false);
        kwType.setStoreTermVectors(true);

        FieldType txType = new FieldType();
        txType.setIndexOptions(DOCS_AND_FREQS);
        txType.setStored(true);
        txType.setTokenized(true);
        txType.setStoreTermVectors(true);

        for (Archetype archetype : archetypes) {
            node = converter.adl2node(archetype);
            document = new Document();

            document.add(new TextField("purpose", avoidNull(node.getPurpose()), Field.Store.YES));
            document.add(new TextField("use", avoidNull(node.getUse()), Field.Store.YES));
            document.add(new TextField("misuse", avoidNull(node.getMisuse()), Field.Store.YES));
            document.add(new Field("keywords", avoidNull(node.kwString()), txType));
            document.add(new StringField("name", node.getName(), Field.Store.YES));
            data_element_map.put(node.getName(), new HashMap<>());
            document.add(new TextField("concept", node.getConcept(), Field.Store.YES));

            List<DataElement> a = node.getDataElements();
            try{
                for (DataElement item : a) {
                    document.add(new Field("elements", avoidNull(item.getText()), kwType));
                    document.add(new Field("term", avoidNull(item.getText()), txType));
                    document.add(new TextField("description", avoidNull(item.getDescription()), Field.Store.YES));
                    document.add(new TextField("comment", avoidNull(item.getComment()), Field.Store.YES));
                }
            } catch (NullPointerException e){
//                System.out.println(node.getName());
            }

            document.add(new Field("source", node.toString(), txType));
            indexWriter.addDocument(document);
        }

        indexWriter.close();

        try
        {
            FileOutputStream fos =
                    new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data_element_map);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in hashmap.ser\n");
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private static String avoidNull(String text) {
        if(text == null) {
            return "null";
        } else {
            return text;
        }
    }

    public static void readIndex() throws IOException {
        try {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            data_element_map = (Map<String, Map<String, Double>>) ois.readObject();
            key_word_map.putAll(data_element_map);
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        indexReader = DirectoryReader.open(dir);
        Mt = wij(data_element_map, "term");
        Mk = wij(key_word_map, "keywords");
        
        nearestConcept();
        
        indexReader.close();
    }

    private static void nearestConcept() {
//        for (String archetype : key_word_map.keySet()
//             ) {
//            for (:
//                 ) {
//
//            }
//        }
    }

    private static long wij(Map<String, Map<String, Double>> map, String fieldName) {
        // sum the number M (Mt or Mk)
        Set<String > index = new HashSet<>();

        for (String archetype : map.keySet()
            ) {
            HashMap<String, Double> tfMap = new HashMap<>();
            try {
                // put the terms of fieldName into the tfMap as key
                Terms termVector = indexReader.getTermVector(searchByName(archetype), fieldName);
                TermsEnum itr = termVector.iterator();
                BytesRef term;
                while ((term = itr.next()) != null) {
                    String key = term.utf8ToString();
                    tfMap.put(key, 0.0);
                }

                // calculate the weight
                termVector = indexReader.getTermVector(searchByName(archetype), "source");
                itr = termVector.iterator();
                double sum = 0.0;
                while ((term = itr.next()) != null) {
                    String key = term.utf8ToString();
                    if(tfMap.containsKey(key)){
                        long tf = itr.totalTermFreq();
                        double itf = 1 + Math.log((double) DOC/itr.docFreq());
                        double mid = (double) tf * Math.pow(itf,2);
                        sum += mid;
                        tfMap.put(key, mid);
                        index.add(key);
                    }
                }

                for (String key : tfMap.keySet()
                    ) {
                    tfMap.put(key, tfMap.get(key)/sum);
                }
            } catch (NullPointerException | IOException e){
                System.out.println(archetype);
            }

            map.put(archetype, tfMap);
        }

        return index.size();
    }

    private static List<String> search(String query, long M) {
        Map<String,Double> scoreList = new HashMap<>();

        try(Analyzer analyzer = new StandardAnalyzer()){
            List<String> result = analyze(query, analyzer);
            for (String archetype : data_element_map.keySet()
                 ) {
                Map<String, Double> concept = data_element_map.get(archetype);
                double score = 0.0;
                for (String index : concept.keySet()
                     ) {
                    if (result.contains(index)) {
                        score += concept.get(index);
                    } else {
                        score += 1.0/M* concept.get(index);
                    }
                }
                scoreList.put(archetype, score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Map.Entry<String, Double>> list = new ArrayList<>(scoreList.entrySet());
//        list.sort(Map.Entry.comparingByValue());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//        System.out.println(list);
        List<String> result = new ArrayList<>();
        result.add(query);
        for(int i = 0; i < 10; i++){
            result.add(list.get(i).getKey());
        }

        return result;
    }

    public static void bulkSearch(List<String> queries) {
        try (FileWriter fileWriter = new FileWriter("result.txt")) {
            for (String query : queries
                ) {
                fileWriter.flush();
                String line = String.join(",", search(query, Mt));
                fileWriter.write(line);
                fileWriter.flush();
                fileWriter.write("\n");
                fileWriter.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> analyze(String text, Analyzer analyzer) throws IOException{
        List<String> result = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream("", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }
}
