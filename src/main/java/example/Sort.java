package example;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

final public class Sort
{
    
    private static Sort instance = null;
    
    private int numberOfWords = 0;
    
    private String[] words;
    
    public static void test()
    {
        int threadCount = 6;
        
        Sort sort = Sort.getInstance();
        Date start = new Date();
        //输入
        if (!sort.getWordsFromInput("in.txt"))
        {
            return;
        }
        
        if (!sort.sortWords(threadCount))
        {
            return;
        }
        
        if (!sort.writeWordsToOutput("out.txt"))
        {
            return;
        }
        
        Date end = new Date();
        long timeToComplete = end.getTime() - start.getTime();
        
        System.out.println();
        System.out.println("Using " + threadCount + " threads, " + sort.getWords().length + " words was sorted in "
            + timeToComplete + " milliseconds.");
    }
    
   
    private Sort()
    {
    } 
    
    private static Sort getInstance()
    {
        if (instance == null)
        {
            instance = new Sort();
        }
        
        return instance;
    } // Returner singleton objekt
    
    public boolean getWordsFromInput(String inputFile)
    {
        System.out.print("Loading contents of " + inputFile + "... ");
        Date start = new Date();
        
        StringBuilder firstLine = new StringBuilder(); // F酶rste linja, som inneholder antall ord
        StringBuilder lines = new StringBuilder(); // Ordene for sortering
        
        try
        {
            BufferedReader input = new BufferedReader(new FileReader(inputFile));
            boolean readFirstLine = false; // Har vi lest f酶rste linje?
            
            for (int charByte = input.read(); charByte >= 0; charByte = input.read())
            {
                char readChar = (char)charByte;
                
                if (readChar == '\r')
                { // ignorer \r tegn
                }
                else if (readFirstLine)
                { // Vi har lest f酶rste linje
                    lines.append(readChar);
                }
                else
                { // Vi har ikke lest f酶rste linje, les frem til linjeskift
                    if (readChar == '\n')
                    {
                        readFirstLine = true;
                        continue;
                    }
                    
                    firstLine.append(readChar);
                }
            }
            
            input.close();
        }
        catch (IOException e)
        {
            return false;
        }
        
        words = lines.toString().split("\n");
        
        try
        {
            numberOfWords = Integer.parseInt(firstLine.toString());
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        
        Date end = new Date();
        long timeDiff = end.getTime() - start.getTime();
        
        System.out.println(timeDiff + "ms");
        
        return true;
    }
    
    public boolean writeWordsToOutput(String outputFile)
    {
        System.out.print("Writing results to " + outputFile + "... ");
        Date start = new Date();
        
        if (words.length != numberOfWords)
        { // Sjekker om vi har sortert riktig antall ord
            System.out.println("Sorted list does not contain expected number of words!");
            return false;
        }
        
        try
        {
            FileWriter output = new FileWriter(outputFile);
            
            for (int i = 0; i < words.length; i++)
            {
                String outputWord = (i == words.length - 1) ? words[i] : words[i] + "\n";
                System.out.print(outputWord);
                output.write(outputWord);
            }
            
            output.close();
        }
        catch (IOException e)
        {
            return false;
        }
        
        Date end = new Date();
        long timeDiff = end.getTime() - start.getTime();
        System.out.println(timeDiff + "ms");
        
        return true;
    }
    
    public boolean sortWords(int threadCount)
    {
        System.out.print("Sorting... ");
        Date start = new Date();
        //排序过程
        //线程集合
        LinkedList<WordHandler> wordHandlers = new LinkedList<WordHandler>();
        //读取之后分组后排序
        initSortThreads(threadCount, wordHandlers); // Start sortering
        //分组排序之后，将获取到的排序列表进行排序融合
         //这个过程也是多线程的  ---如果前面的排序线程还没执行完毕，这边已经开始使用分组排序的时候的结果咋办
        boolean sortResult = interleaveThreads(wordHandlers); 
        
        Date end = new Date();
        long timeDiff = end.getTime() - start.getTime();
        System.out.println(timeDiff + "ms");
        
        return sortResult;
    }
    
    private void initSortThreads(int threadCount, LinkedList<WordHandler> wordHandlers)
    {
        int wordsPerThread = words.length / threadCount;
        int additionalWordsPerThread = words.length % threadCount;
        
        int currentOffset = 0;
        
        for (int i = 0; i < threadCount; i++)
        {
            int wordsForThread = wordsPerThread;
            
            if (additionalWordsPerThread > 0)
            {
                wordsForThread++;
                additionalWordsPerThread--;
            }
            
            WordSorter sorter = new WordSorter(words, currentOffset, currentOffset + wordsForThread);
            wordHandlers.add(sorter);
            
            currentOffset += wordsForThread;
        }
    }
    
    private boolean interleaveThreads(LinkedList<WordHandler> wordHandlers)
    {
        WordHandler buffer = null;
        
        while (wordHandlers.size() > 0)
        {
            try
            {
                wordHandlers.peek().join();
                
                if (buffer == null && wordHandlers.size() == 1)
                {
                    words = wordHandlers.poll().getWords();
                }
                else if (buffer == null)
                {
                    buffer = wordHandlers.poll();
                }
                else
                {
                    Interleaver merge = new Interleaver(buffer.getWords(), wordHandlers.poll().getWords());
                    wordHandlers.add(merge);
                    buffer = null;
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("Main sort thread was interupted!");
                return false;
            }
        }
        
        return true;
    }
    
    public String[] getWords()
    {
        return words;
    }
}
