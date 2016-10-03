package vthai.swype;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SwypeTest {
    private Swype swype;
    
    private static final int minimum = 4;
    
    public void printAll(List<String> ngrams) {
        for (String ngram : ngrams) {
            System.out.print(ngram + " ");
        }
        System.out.println();
    }
    
    @Before
    public void setUp() {
        File resourcesDirectory = new File("src/test/resources");
        swype = new Swype(resourcesDirectory.getAbsolutePath() + "/enable1.txt", 4);
    }
    
    @Test
    public void testSimpleSwipe1() {
        String userInput = "resdft";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        
        boolean exist = false;
        for (String ngram : ngrams) {
            if (ngram.equals("rest")) {
                exist = true;
            }
        }
        assertTrue(exist);
    }
    
    @Test
    public void testSimpleSwipe2() {
        String userInput = "resert";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        
        boolean exist = false;
        for (String ngram : ngrams) {
            if (ngram.equals("rest")) {
                exist = true;
            }
        }
        assertTrue(exist);
    }
    
    @Test
    public void testSwipeDoubleLetter() {
        String userInput = "polkjuytrews";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        
        boolean exist = false;
        for (String ngram : ngrams) {
            if (ngram.equals("polls")) {
                exist = true;
            }
        }
        assertTrue(exist);
    }
    
    @Test
    public void testSwipeResultSize() {
        String userInput = "resdft";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        for (String ngram : ngrams) {
            assertTrue(ngram.length() >= minimum);
        }
    }
    
    @Test
    public void testComlexSwipe1() {
        String userInput = "qwertyuytrewertyuijn";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        String[] expected = {"quern", "queen", "quin"};
        
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
    
    @Test
    public void testComlexSwipe2() {
        String userInput = "qwertyuytresdftyuioknn";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        String[] expected = {"question", "queen", "quin"};
        
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
    
    @Test
    public void testComlexSwipe3() {
        swype.setMinimum(5);
        String userInput = "gijakjthoijerjidsdfnokg";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        
        String[] expected = {"going", "gieing", "gating", "geeing", "garring", "goring", "gathering", "gaeing"}; 
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
    
    @Test
    public void testComplexIncreaseCache() {
        File resourcesDirectory = new File("src/test/resources");
        swype = new Swype(resourcesDirectory.getAbsolutePath() + "/enable1.txt", 4, 18);
        swype.setMinimum(5);
        String userInput = "gijakjthoijerjidsdfnokg";
        List<String> ngrams = swype.singleInput(userInput.toCharArray());
        
        String[] expected = {"going", "gieing", "gating", "geeing", "garring", "goring", "gathering", "gaeing"}; 
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
}
