import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by Ilya Evlampiev on 09.12.2015.
 */
public class ApachePastvuOrchestrator {
    static int counter=404000;//140000;

    static Queue<Integer> forgotten=new ArrayDeque<Integer>(){};
    public static void main(String [] args)
    {
        for (int i=0;i<100;i++) {
            (new ApachePastvuLoader()).start();
        }
    }

    synchronized static int getNext()
    {
        if (!forgotten.isEmpty())
        {
            return forgotten.poll();
        }
       counter++;
       return counter;
    }

    synchronized static void addFailed(int failnum)
    {
        forgotten.add(failnum);
    }
}
