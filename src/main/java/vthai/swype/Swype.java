package vthai.swype;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Swype {
    private Set<String> dictionary;
    
    private Scanner inputReader;
    
    private Set<NGram> dictionaryWords;
    
    private Map<Integer, Set<String>> prefixes;
    
    private int minimum;
    
    private int cachedLevel;
    
    public class NGram {
        private String word;
        
        public NGram(String word) {
            this.word = word;
        }
        
        @Override
        public int hashCode() {
            return word.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof NGram)) {
                return false;
            }
            NGram ngram = (NGram)obj;
            
            return this.word.equals(ngram.word);
        }
        
        public int length() {
            return word.length();
        }
        
        @Override
        public String toString() {
            return word;
        }
    }
    
    public Swype(String dictionaryPath, int minimum, int cacheLevel) {
        this.minimum = minimum;
        this.cachedLevel = cacheLevel;
        
        buildDictionary(dictionaryPath);
        dictionaryWords = new HashSet<>();
    }
    
    public Swype(String dictionaryPath, int minimum) {
        this(dictionaryPath, minimum, 30);
    }
    
    private void buildDictionary(String dictionaryPath) {
        dictionary = new HashSet<>();
        prefixes = new HashMap<>();
        
        for (int i = 2; i <= cachedLevel + 1; i++) {
            prefixes.put(i, new HashSet<>());
        }
        
        try (Scanner scanner = new Scanner(new File(dictionaryPath))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                dictionary.add(line);
                
                prefixes.get(2).add(line.substring(0, 2));
                
                for (int i = 2; i <= cachedLevel; i++) {
                    if (line.length() > i) {
                        prefixes.get(i+1).add(line.substring(0, i+1));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
    
    private Optional<char[]> readUserInput() {
        System.out.print("Enter the swiped text: ");
        String userInput = inputReader.nextLine();
        
        if (userInput.length() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(userInput.toCharArray());
        }
    }
    
    private void displayResult(List<NGram> ngrams) {
        System.out.println("\nThis is the list of ngrams that have 5+ characters:");
        for (NGram ngram : ngrams) {
            System.out.println(ngram.word);
        }
        System.out.println();
    }
    
    private void cleanUpImpossibleCacheWords(Set<String> cachedWords) {
        Iterator<String> iterator = cachedWords.iterator();
        while (iterator.hasNext()) {
            String cachedWord = iterator.next();
            
            Set<String> prefixList = prefixes.get(cachedWord.length());
            if (prefixList != null && !prefixList.contains(cachedWord)) {
                //System.out.println("Remove  " + cachedWord.length() + ": " + cachedWord);
                iterator.remove();
            }
        }
    }
    
    public List<NGram> singleInput(char[] userInput) {
        dictionaryWords.clear();
        
        StringBuilder accumulatedString = new StringBuilder();
        Set<String> cachedWords = new HashSet<>();
        
        for (char character : userInput) {
            accumulatedString.append(character);
            cachedWords.add(accumulatedString.toString());
            
            Set<String> newCachedWords = new HashSet<>();
            
            for (String cachedWord : cachedWords) {
                String potentialNGram = cachedWord + character;
                if (dictionary.contains(potentialNGram)) {
                    dictionaryWords.add(new NGram(potentialNGram));
                }
                
                String potentialNGramDoubleLetters = cachedWord + character + character;
                if (dictionary.contains(potentialNGramDoubleLetters)) {
                    dictionaryWords.add(new NGram(potentialNGramDoubleLetters));
                }
                
                newCachedWords.add(potentialNGram);
                newCachedWords.add(potentialNGramDoubleLetters);
            }
            cachedWords.addAll(newCachedWords);
            cleanUpImpossibleCacheWords(cachedWords);
        }
        
        List<NGram> ngrams = new ArrayList<>(dictionaryWords);
        char lastChar = userInput[userInput.length - 1];
        List<NGram> reducedngrams = ngrams.stream()
                .filter(ngram -> ngram.length() >= minimum)
                .filter(ngram -> ngram.word.charAt(ngram.length()-1) == lastChar)
                .collect(Collectors.toList());
        
        //reducedngrams.sort((NGram ngram1, NGram ngram2) -> ngram2.length() - ngram1.length());
        return reducedngrams;
    }
    
    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }
    
    public void interactiveInput() {
        inputReader = new Scanner(System.in);
        
        Optional<char[]> userInput = readUserInput();
        
        while (userInput.isPresent()) {
            List<NGram> ngrams = singleInput(userInput.get());
            displayResult(ngrams);
            
            userInput = readUserInput();
        }
        
        inputReader.close();
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the dictionary file");
            return;
        }
        Swype swype = new Swype(args[0], 5);
        swype.interactiveInput();
    }
}
