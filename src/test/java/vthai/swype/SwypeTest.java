package vthai.swype;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vthai.swype.Swype.NGram;

public class SwypeTest {
    private Swype swype;
    
    private static final int minimum = 4;
    
    @Before
    public void setUp() {
        File resourcesDirectory = new File("src/test/resources");
        swype = new Swype(resourcesDirectory.getAbsolutePath() + "/enable1.txt", 4);
    }
    
    @Test
    public void testSimpleSwipe1() {
        String userInput = "resdft";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        
        boolean exist = false;
        for (NGram ngram : ngrams) {
            if (ngram.toString().equals("rest")) {
                exist = true;
            }
        }
        assertTrue(exist);
    }
    
    @Test
    public void testSimpleSwipe2() {
        String userInput = "resert";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        
        boolean exist = false;
        for (NGram ngram : ngrams) {
            if (ngram.toString().equals("rest")) {
                exist = true;
            }
        }
        assertTrue(exist);
    }
    
    @Test
    public void testSwipeDoubleLetter() {
        String userInput = "polkjuytrews";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        
        boolean exist = false;
        for (NGram ngram : ngrams) {
            if (ngram.toString().equals("polls")) {
                exist = true;
            }
        }
        assertTrue(exist);
    }
    
    @Test
    public void testSwipeResultSize() {
        String userInput = "resdft";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        for (NGram ngram : ngrams) {
            assertTrue(ngram.toString().length() >= minimum);
        }
    }
    
    @Test
    public void testComlexSwipe1() {
        String userInput = "qwertyuytrewertyuijn";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        String[] expected = {"qwerty", "queer", "quern", "queen", "query", "quin", "quey"};
        
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
    
    @Test
    public void testComlexSwipe2() {
        String userInput = "qwertyuytresdftyuioknn";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        String[] expected = {"question", "qwertys", "qwerty", "queen", "quest", "quin", "quey"};
        
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
    
    @Test
    public void testComlexSwipe3() {
        swype.setMinimum(5);
        String userInput = "gijakjthoijerjidsdfnokg";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        
        String[] expected = {"gathering", "garring", "gittern", "gathers", "gieing", "gators", "goring", "gating", "gather", "gaijin", "geeing", "gittin", "gooier", "gaeing", "girds", "goods", "grids", "ghees", "gated", "goers", "going", "gesso", "gates", "gator", "griff"}; 
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
    
    @Test
    public void testComplexIncreaseCache() {
        File resourcesDirectory = new File("src/test/resources");
        swype = new Swype(resourcesDirectory.getAbsolutePath() + "/enable1.txt", 4, 100);
        swype.setMinimum(5);
        String userInput = "gijakjthoijerjidsdfnokg";
        List<NGram> ngrams = swype.singleInput(userInput.toCharArray());
        
        String[] expected = {"gathering", "garring", "gittern", "gathers", "gieing", "gators", "goring", "gating", "gather", "gaijin", "geeing", "gittin", "gooier", "gaeing", "girds", "goods", "grids", "ghees", "gated", "goers", "going", "gesso", "gates", "gator", "griff"}; 
        for (int index = 0; index < ngrams.size(); index++) {
            assertThat(ngrams.get(index).toString(), equalTo(expected[index]));
        }
    }
}
