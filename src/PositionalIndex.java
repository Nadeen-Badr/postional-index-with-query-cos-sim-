// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

public class PositionalIndex {


    ArrayList<String> termList;
    ArrayList<ArrayList<DocId>> docLists;
    int distance = 1;

    public PositionalIndex(ArrayList<String> docs) {
        termList = new ArrayList<String>();
        docLists = new ArrayList<ArrayList<DocId>>();

        for (int i = 0; i < (docs.size()); i++) {
            String all_lines = docs.get(i); // بجيب الفايل جواه ايه
            String[] tokens = all_lines.split("[ .,?!:;$%&*+()%#!/\\-\\^\"]+"); // بيشيل الي بيوقف الكلام ويخلي كل كلمه
            // لوحدها
            for (int m = 0; m < tokens.length; m++) {
                tokens[m] = tokens[m].toLowerCase();
            }
            for (int j = 0; j < (tokens.length); j++) // بيمشي علي كلمه كلمه
            {
                if (tokens[j].equals("a") || tokens[j].equals("an") || tokens[j].equals("the")
                        || tokens[j].equals("and") || tokens[j].equals("or") || tokens[j].equals("not")
                        || tokens[j].equals("to") || tokens[j].equals("at"))
                    continue;
                if (!termList.contains(tokens[j])) // لو كلمه دي مش موجودهه
                {
                    termList.add(tokens[j]); // ضيفها
                    DocId doid = new DocId(i, j); // هضيف رقم الفايل والاندكس بتاع كلمه
                    ArrayList<DocId> ListOfDoc = new ArrayList<DocId>(); // ارقام الفايلات الي بتحتوي علي الكلمه
                    ListOfDoc.add(doid);
                    docLists.add(ListOfDoc);
                } else {
                    int index = termList.indexOf(tokens[j]);
                    ArrayList<DocId> docList = docLists.get(index);
                    boolean match = false;
                    int k = 0;
                    // old term same document also seen before
                    //insert postion
                    for (DocId doid : docList) {
                        if (doid.docId == i) {
                            doid.insertPosition(j);
                            match = true;
                        }
                        k++;
                    }
                    // old term new document
                    if (!match) {
                        DocId doid = new DocId(i, j);
                        //index ,doc and index word
                      
                        docLists.get(index).add(doid);
                          //index of the token
                    }
                }
            }
        }
    }
//just formatting
    public String toString() {
        String matrixString = new String();
        ArrayList<DocId> docList;
        for (int i = 0; i < termList.size(); i++) {
            matrixString += String.format("%-15s", termList.get(i));
            docList = docLists.get(i);
            for (int j = 0; j < docList.size(); j++) {
                matrixString += docList.get(j) + "\t";
            }
            matrixString += "\n";
        }
        return matrixString;
    }

    public ArrayList<DocId> intersect(ArrayList<DocId> list1, ArrayList<DocId> list2) {
        if (list1 == null)
            return list2;
        else if (list2 == null)
            return list1;
        ArrayList<DocId> mergedList = new ArrayList<DocId>();
        int index_list1 = 0, index_list2 = 0;

        while (index_list1 < list1.size() && index_list2 < list2.size()) // كمل لحد ما تعدى على فايل فايل
        {
            if (list1.get(index_list1).docId == list2.get(index_list2).docId) // لو فايلين شبه بعض
            {

                ArrayList<Integer> pp1 = list1.get(index_list1).positionList; // بنجيب كل فايل جواه ايش
                ArrayList<Integer> pp2 = list2.get(index_list2).positionList;//
                int pid1 = 0, pid2 = 0;
                boolean match = false;
                while (pid1 < pp1.size()) {
                    pid2 = 0;
                    while (pid2 < pp2.size()) {
                        if (pp2.get(pid2) - pp1.get(pid1) == distance) {
                            if (!match) // لو ماتشينج
                            {//بشيل التكرار يعني 
                                DocId docList = new DocId(list1.get(index_list1).docId);
                                mergedList.add(docList);
                                match = true;
                            }

                        }
                        pid2++;
                    }
                    pid1++;
                }
                index_list1++;
                index_list2++;
            } else if (list1.get(index_list1).docId > list2.get(index_list2).docId) // الاصغر هو الي بيتحرك
                index_list2++;
            else
                index_list1++;
        }
        return mergedList;
    }

    //
    public ArrayList<DocId> phraseQuery(String[] query) {
        distance = 1;
        ArrayList<DocId> docList1 = new ArrayList<DocId>();
        ArrayList<DocId> docList2 = new ArrayList<DocId>();
        ArrayList<DocId> docList = new ArrayList<DocId>();
        // ArrayList<ArrayList<DocId>> result = new ArrayList<ArrayList<DocId>>();

        if (query.length == 0)
            return null;
            //1 word
        else if (query.length == 1) {
            if (termList.contains(query[0])) {
                int index = termList.indexOf(query[0]);
                return docLists.get(index);
            } else {
                return null; // Word not found
            }
        } else // لز اكتر من كلمه
        {
            ArrayList<DocId> result = new ArrayList<DocId>();
            if (termList.contains(query[0]))
                docList1 = docLists.get(termList.indexOf(query[0]));
            else
                docList1 = null;
            if (termList.contains(query[0]))
                docList2 = docLists.get(termList.indexOf(query[1]));
            else
                docList2 = null;
            result = intersect(docList1, docList2); // جاب كل فايلات الي فيهم كلمتين دول ورا بعض وبدا يقارن بيها مع
            // الكلمه الي بعدها
            distance++; // ??
            for (int i = 2; i < query.length; i++) {
                if (termList.contains(query[i]))
                    docList = docLists.get(termList.indexOf(query[i]));
                else
                    docList = null;
                result = intersect(result, docList);
                distance++;
            }
            return result;

        }
    }

    // فانكشن بترجع لينا con بكام
    public static double calculateCossallarlty(String Doc, String Doc2) {
        int firstDoccounter = 0, secondDoccounter2 = 0;
        int numerator = 0;
        int sum_numerator = 0;
        double sqr_sumDoc1 = 0, sqr_sumDoc2 = 0;
        double cosineSimilarlty;

        ArrayList<String> Doc1_words = new ArrayList<>(); // files System.out.println(result);
        ArrayList<String> Doc2_words = new ArrayList<>();// query

        // Split the sentence into words
        String[] words = Doc.split(" ");
        for (String word : words) {
            Doc1_words.add(word);
        }
        HashSet<String> allwords = new HashSet<>(Doc1_words);
        String[] words2 = Doc2.split(" ");
        for (String word : words2) {
            Doc2_words.add(word);
            allwords.add(word);
        }

        for (String word : allwords) // همشي علي كل كلمه
        {
            for (String w : Doc1_words) // هشوف كلمه موجوده كام مره فى الفايل
            {
                if (word.equals(w)) {
                    firstDoccounter++;
                }
            }
            for (String w : Doc2_words) // موجوده كام مره فى الكويرؤ
            {
                if (word.equals(w)) {
                    secondDoccounter2++;
                }
            }
            sqr_sumDoc1 += Math.pow(firstDoccounter, 2.0); // بتجمع الي تحت الكسر
            sqr_sumDoc2 += Math.pow(secondDoccounter2, 2.0);// نفس الشيء
            numerator = firstDoccounter * secondDoccounter2;// ؟
            sum_numerator += numerator;//
            firstDoccounter = secondDoccounter2 = 0;
        }

        sqr_sumDoc1 = Math.sqrt(sqr_sumDoc1);// بيجيب الجذر

        sqr_sumDoc2 = Math.sqrt(sqr_sumDoc2);// نفس شيء
        cosineSimilarlty = sum_numerator / (sqr_sumDoc1 * sqr_sumDoc2);//
        return cosineSimilarlty;
    }

    public Map<String, Map<Integer, Double>> calculateTFIDF() {
        Map<String, Map<Integer, Double>> tfidfMap = new HashMap<>();

        int totalDocuments = docLists.size();
        for (int i = 0; i < termList.size(); i++) {
            String term = termList.get(i);
            ArrayList<DocId> docList = docLists.get(i);
            int documentFrequency = docList.size();

            Map<Integer, Double> tfidfValues = new HashMap<>();
            for (DocId doc : docList) {
                int docId = doc.docId;
                int termFrequency = doc.positionList.size();
                double tf = (double) termFrequency / docLists.get(docId).size();
                double idf = Math.log((double) totalDocuments / documentFrequency);
                double tfidf = tf * idf;
                tfidfValues.put(docId, tfidf);
            }

            tfidfMap.put(term, tfidfValues);
        }

        return tfidfMap;
    }

    // فانكشن بتجيب كل فايل جواه ايه وتحوله استرينج وتبعته
    private static String readFile(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }
        sc.close();
        return sb.toString();
    }

    public static void PrintPositionalIndex(PositionalIndex pi) {
        System.out.print(pi);
    }

    public static String GetDocumnets(PositionalIndex pi) {
        System.out.println("Enter a phrase query ");
        Scanner scanner = new Scanner(System.in);
        String phraseQuery = scanner.nextLine().toLowerCase();
        String[] tokens = phraseQuery.split("[ .,?!:;$%&*+()%#!/\\-\\^\"]+");
        ArrayList<DocId> result = pi.phraseQuery(tokens);
        if (result == null) {
            System.out.println("Not found");
        } else {
            System.out.print("this documents  ");
            for (DocId res : result) {
                System.out.print(res.docId + 1 + " ");
            }
            System.out.println("Satytisy the query");
        }
        return phraseQuery;
    }

    public static void GetTf_IDF(PositionalIndex pi) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Map<Integer, Double>> tfidfMap = pi.calculateTFIDF();
        System.out.println("Enter a Term ");
        String term = scanner.nextLine().toLowerCase();
        Map<Integer, Double> termTFIDF = tfidfMap.get(term);
        if (termTFIDF != null) {
            for (Map.Entry<Integer, Double> entry : termTFIDF.entrySet()) {
                int docId = entry.getKey();
                double tfidf = entry.getValue();
                System.out.println("Term: " + term + ", Document: " + (docId + 1) + ", TF-IDF: " + tfidf);
            }
        } else {
            System.out.println("Term not found.");
        }
    }

    public static TreeMap<Double, ArrayList<Integer>> GetcalculateCossallarlty(String phraseQuery, ArrayList<String> docs) {
        double result_cos = 0;
        TreeMap<Double, ArrayList<Integer>> map = new TreeMap<>();
        for (int i = 0; i < docs.size(); i++) {
            result_cos = calculateCossallarlty(docs.get(i), phraseQuery);
            System.out.println("File"+i+"cos_samilarity is:"+ result_cos );
            if (map.containsKey(result_cos))
                map.get(result_cos).add(i + 1);
            else {
                ArrayList<Integer> documents = new ArrayList<>();
                documents.add(i + 1);
                map.put(result_cos, documents);

            }

        }
        return map;
    }

    public static void RankFiles(TreeMap<Double, ArrayList<Integer>> map) {
        for (double key : map.descendingKeySet()) {
            ArrayList<Integer> value = map.get(key);

            if (key != 0)
                System.out.println("File" + value + " With value: " + key);

        }
    }
//   public static HashSet<String>  Get_Content_Web() throws IOException
//   {
//       ImprovedWebCrawler crawler = new ImprovedWebCrawler();
//       String URL = "https://www.bbc.com/news";
//       crawler.getPageLinks("https://www.bbc.com/news", 0);
//       HashSet<String>  links = crawler.getLinks();
//       return links ;
//   }

    // public static  ArrayList<String> Web_Links( HashSet<String> links ) throws IOException {



    //     ArrayList<String> docs = new ArrayList<>();
    //     for (String link : links) {
    //         Document document = Jsoup.connect(link).userAgent("Mozilla").get();
    //         String text = document.body().text();
    //         docs.add(text);
    //     }
    //     return docs;
    // }

    public static void SatisyQuery(PositionalIndex pi, HashSet<String> links) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a phrase query ");
        String phraseQuery = scanner.nextLine().toLowerCase();
        String[] tokens = phraseQuery.split("[ .,?!:;$%&*+()%#!/\\-\\^\"]+");
        ArrayList<DocId> result = pi.phraseQuery(tokens);
        String[] Geeks = links.toArray(new String[links.size()]);
        if (result == null) {
            System.out.println("Not found");
        } else {

            for (DocId res : result) {
                String m = Geeks[res.docId + 1];
                System.out.println("this URL:::");
                System.out.println(m);

            }
            System.out.println("Satytisy the query");
        }
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println("if you want  print-Index choose 1");
        String[] filenames = {"file1.txt", "file2.txt", "file3.txt", "file4.txt", "file5.txt", "file6.txt",
                "file7.txt", "file8.txt", "file9.txt", "file10.txt"};

        ArrayList<String> docs = new ArrayList<>();
        String phraseQuery = null;

        for (int i = 0; i < 10; i++)
        {
            String content_file = readFile(filenames[i]);
            docs.add(content_file);
        }

        Scanner scanner = new Scanner(System.in);
        PositionalIndex pi = new PositionalIndex(docs);
        TreeMap<Double, ArrayList<Integer>> map = new TreeMap<>();
        while (true) {
            System.out.println("if you want  print-Index choose 1");
            System.out.println("if you want  Qet Query choose 2");
            System.out.println("if you want  get Coisn sallmarilty choose 3");
            System.out.println("if you want  Rank  Coisn sallmarilty choose 4");
            System.out.println("if you want TF-IDF choose 5");
            System.out.println("if you want Search in Web Crawler choose 6 ");
            System.out.println("if you want Exit  choose 7 ");
            int choose = scanner.nextInt();
            if (choose == 1) {
                PrintPositionalIndex(pi);
                System.out.println("--------------------------------------------------------");
            } else if (choose == 2) {
                phraseQuery = GetDocumnets(pi);
                System.out.println("--------------------------------------------------------");
            } else if (choose == 3) {
                map = GetcalculateCossallarlty(phraseQuery, docs);
                System.out.println("--------------------------------------------------------");
            } else if (choose == 4) {
                RankFiles(map);
                System.out.println("--------------------------------------------------------");
            } else if (choose == 5) {
                GetTf_IDF(pi);
                System.out.println("--------------------------------------------------------");
            }
            // else if (choose == 6)
            // {
            //     HashSet<String> links = Get_Content_Web();
            //     ArrayList<String> docs2;
            //     docs2 = Web_Links(links);
            //     PositionalIndex pi2= new PositionalIndex(docs2);
            //     SatisyQuery(pi2,links );
            //     System.out.println("--------------------------------------------------------");
            // }
            else
            {
               break ;
            }


        }


    }

}
