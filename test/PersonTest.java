import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import models.Person;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Henrichs on 10/27/2017.
 * Tests constructor and operations of Person class
 */
public class PersonTest extends UnitTest {

    Person person;
    Person person2;
    String excerpt;

    @Before
    public void createPerson() {
        HashMap<String, String> handles = new HashMap<>();
        handles.put("wiki", "Lil_Uzi_Vert");
        handles.put("insta", "liluzivert");
        handles.put("twitter", "liluzivert");
        handles.put("youtube", "LILUZIVERT");
        HashMap<String, String> handles2 = new HashMap<>();
        handles2.put("wiki", "Future_(rapper)");
        handles2.put("insta", "liluzivert");
        handles2.put("twitter", "liluzivert");
        handles2.put("youtube", "LILUZIVERT");
        person = new Person(1, "Lil Uzi Vert", "imgUrl", "excerpt", handles);
        person2 = new Person(2, "future", "imgUrl", "excerpt", handles2);
        excerpt = "";
    }

    @Test
    public void retreiveWikiInfoTest() {
        System.out.println("Before: " + person2.getWikiExcerpt());
        person2.retreiveWikiInfo();
        System.out.println("\n");
        System.out.println("After: " + person2.getWikiExcerpt());
        excerpt = person2.getWikiExcerpt();
    }

}
