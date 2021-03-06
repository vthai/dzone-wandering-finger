package vthai.wfinger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class WanderingFinger {
    private Set<String> dictionary;
    
    private Scanner inputReader;
    
    private Set<String> ngramsFound;
    
    private Map<Integer, Set<String>> prefixes;
    
    private int minimum;
    
    private int cachedLevel;
    
    public WanderingFinger(String dictionaryPath, int minimum, int cacheLevel) {
        this.minimum = minimum;
        this.cachedLevel = cacheLevel;
        
        buildDictionary(dictionaryPath);
        ngramsFound = new HashSet<>();
    }
    
    public WanderingFinger(String dictionaryPath, int minimum) {
        this(dictionaryPath, minimum, 12);
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
    
    private void displayResult(List<String> ngrams) {
        System.out.println("\nThis is the list of ngrams that have 5+ characters:");
        for (String ngram : ngrams) {
            System.out.println(ngram);
        }
        System.out.println();
    }
    
    private boolean isTrulyPotential(String candidate) {
        return prefixes.getOrDefault(candidate.length(), Collections.emptySet())
                .contains(candidate);
    }
    
    public List<String> singleInput(char[] userInput) {
        ngramsFound.clear();
        
        char lastChar = userInput[userInput.length - 1];
        Set<String> cachedWords = new HashSet<>();
        
        cachedWords.add(String.valueOf(userInput[0]));
        
        for (int index = 1; index < userInput.length; index++) {
            char character = userInput[index];
            
            Set<String> newCachedWords = new HashSet<>();
            
            for (String cachedWord : cachedWords) {
                String potentialNGram = cachedWord + character;
                
                if (potentialNGram.length() >= minimum
                        && dictionary.contains(potentialNGram)
                        && character == lastChar) {
                    ngramsFound.add(potentialNGram);
                }
                
                String potentialNGramDoubleLetters = cachedWord + character + character;
                
                if (potentialNGramDoubleLetters.length() >= minimum
                        && dictionary.contains(potentialNGramDoubleLetters) 
                        && character == lastChar) {
                    ngramsFound.add(potentialNGramDoubleLetters);
                }
                
                if (isTrulyPotential(potentialNGram)) {
                    newCachedWords.add(potentialNGram);
                }
                
                if (isTrulyPotential(potentialNGramDoubleLetters)) {
                    newCachedWords.add(potentialNGramDoubleLetters);
                }
            }
            cachedWords.addAll(newCachedWords);
        }
        
        return new ArrayList<>(ngramsFound);
    }
    
    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }
    
    public void interactiveInput() {
        inputReader = new Scanner(System.in);
        
        Optional<char[]> userInput = readUserInput();
        
        while (userInput.isPresent()) {
            List<String> ngrams = singleInput(userInput.get());
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
        WanderingFinger swype = new WanderingFinger(args[0], 5);
        swype.interactiveInput();
    }
}
