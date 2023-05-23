
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class ImprovedWebCrawler {
    private static final int MAX_PAGES_TO_SEARCH = 3;
    private static final int MAX_LINKS_TO_COLLECT =6;
    private HashSet<String> links;
    private final HashMap<String, Integer> linkToIdMap;
    private int currentDocId = 1;

    public ImprovedWebCrawler() {
        this.links = new HashSet<>();
        this.linkToIdMap = new HashMap<>();
    }

    // public void getPageLinks(String URL, int depth) {
    //     if ((!links.contains(URL) && (depth < MAX_PAGES_TO_SEARCH) && (links.size() < MAX_LINKS_TO_COLLECT))) {
    //         try {
    //             System.out.println("Depth: " + depth + " [" + URL + "]");
    //             links.add(URL);
    //             linkToIdMap.put(URL, currentDocId);
    //             currentDocId++;

    //             Document document = Jsoup.connect(URL).get();
    //             Elements otherLinks = document.select("a[href]");

    //             depth++;
    //             for (Element page : otherLinks) {
    //                 if (links.size() >= MAX_LINKS_TO_COLLECT) {
    //                     break;
    //                 }
    //                 getPageLinks(page.attr("abs:href"), depth);
    //             }
    //         } catch (IOException e) {
    //             System.err.println(e.getMessage());
    //         }
    //     }
    // }

    public HashSet<String> getLinks() {
        return links;
    }

    public int getDocId(String URL) {
        return linkToIdMap.getOrDefault(URL, -1);
    }
}
