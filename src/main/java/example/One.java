package example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class One
{
    String message = "foo";
    
    List<String> list = readOutFile();
    
    Stack<String[]> stack = new Stack<String[]>();
    List<String[]> arrList = new ArrayList<String[]>();
    int a = 3;
    
    public String foo()
    {
        
        int n = 3;
        int count = list.size() / n;
        int leave = list.size() % n;
        System.out.println("count=" + count + "  leave=" + leave);
        if(leave!=0) 
        {
            count+=1;
        }
        for (int i = 0; i <n-1 ; i++)
        {
            if(leave!=0) 
            {
                new Thread(new RunTask(i*count, (i+1)*count-1, count)).start();            
            }
        }
        new Thread(new RunTask((n-1)*count, list.size()-1 , list.size()-(n-1)*count)).start();  
        
        while(a>0) 
        {
        }
        while (!stack.isEmpty()&&stack.size()>1)
        {
             String[] list1 =  stack.pop();
             String[] list2 =  stack.pop();
             String[] sorted = null;

            sorted = new String[list1.length + list2.length];
            int list1Pos = 0;
            int list2Pos = 0;

            for (int i = 0; i < sorted.length; i++) {
                if (list1Pos >= list1.length) {
                    sorted[i] = list2[list2Pos++];
                } else if (list2Pos >= list2.length) {
                    sorted[i] = list1[list1Pos++];
                } else if (list1[list1Pos].compareTo(list2[list2Pos]) < 0) {
                    sorted[i] = list1[list1Pos++];
                } else {
                    sorted[i] = list2[list2Pos++];
                }
            }
            
            stack.push(sorted);
        }
        writeWordsToOutput("out.txt");
        
        
        return message;
    }
    public void writeWordsToOutput(String outputFile)
    {

       String words[] = stack.pop();
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
        }

    }
    public static void main(String[] args)
    {
        One one = new One();
        one.foo();
        //Sort.test();
        
    }
    
    private List<String> readOutFile()
    {
        File file = new File("in.txt");
        FileInputStream is;
        List<String> list = new ArrayList<String>();
        try
        {
            if (file.length() != 0)
            {
                is = new FileInputStream(file);
                InputStreamReader streamReader = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                while ((line = reader.readLine()) != null)
                {
                    try
                    {
                        int a = Integer.parseInt(line);                        
                    }
                    catch (Exception e)
                    {
                        list.add(line);
                    }
                   
                }
                reader.close();
                is.close();
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
        
    }
    
    class RunTask implements Runnable
    {
        int start;
        
        int end;
        
        String arr[];
        
        public RunTask(int start, int end, int n)
        {
            super();
            //System.out.println("start=" + start + "  leave=" + end+" n ="+n);
            arr = new String[n];
            for (int i = 0; i < n; i++)
            {
                arr[i] = list.get(start + i);
            }
        }
        
        public void run()
        {
            for (int i = 0; i < arr.length-1; i++)
            {
                for (int j = i+1; j < arr.length; j++)
                {
                    if (arr[i].compareTo(arr[j])>0)
                    {
                        String temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                    }
                }
            }
            System.out.print("[");
            for (int i = 0; i < arr.length; i++)
            {
                System.out.print(arr[i]+",");
            }
            System.out.println("]");
            arrList.add(arr);
            stack.push(arr);
            a--;
        }
        
    }
    
   
    
}