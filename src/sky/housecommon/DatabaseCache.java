package sky.housecommon;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import sky.program.Duration;

public class DatabaseCache<T extends Timed&Comparable<T>>
{
    private final List<T> objects;
    private final Map<T,Long> lastAccessTimes;
    private final Object lockObject;
    private long lastCleanTime;
    private static final long CLEAN_DELAY=Duration.of(1).hour();
    private static final long OLD_DELAY=Duration.of(2).hourPlus(1).minute();

    public DatabaseCache()
    {
        objects=new ArrayList<>();
        lastAccessTimes=new HashMap<>();
        lockObject=new Object();
        lastCleanTime=System.currentTimeMillis();
    }

    public void add(T object)
    {
        synchronized(lockObject)
        {
            clean();
            boolean changed=false;
            if(!objects.contains(object))
            {
                objects.add(object);
                lastAccessTimes.put(object,Long.valueOf(System.currentTimeMillis()));
                changed=true;
            }
            objects.sort(null);
            if(changed)
                if(objects.isEmpty())
                    Logger.LOGGER.debug("Cache is empty");
                else
                    Logger.LOGGER.debug("Cache now contains "+objects.size()+" "+objects.get(0).getClass().getSimpleName()+" object(s)");
        }
    }

    public void addAll(List<T> objects)
    {
        synchronized(lockObject)
        {
            clean();
            AtomicBoolean changed=new AtomicBoolean();
            objects.stream()
                    .filter(object->!this.objects.contains(object))
                    .forEach(object->
                    {
                        this.objects.add(object);
                        lastAccessTimes.put(object,Long.valueOf(System.currentTimeMillis()));
                        changed.set(true);
                    });
            this.objects.sort(null);
            if(changed.get())
                if(objects.isEmpty())
                    Logger.LOGGER.debug("Cache is empty");
                else
                    Logger.LOGGER.debug("Cache now contains "+this.objects.size()+" "+this.objects.get(0).getClass().getSimpleName()+" object(s)");
        }
    }

    public T getFirst()
    {
        synchronized(lockObject)
        {
            T first=objects.get(0);
            lastAccessTimes.put(first,Long.valueOf(System.currentTimeMillis()));
            return first;
        }
    }

    public T getLast()
    {
        synchronized(lockObject)
        {
            T last=objects.get(objects.size()-1);
            lastAccessTimes.put(last,Long.valueOf(System.currentTimeMillis()));
            return last;
        }
    }

    public List<T> getAllBetween(long time1,long time2)
    {
        synchronized(lockObject)
        {
            List<T> result=new ArrayList<>();
            Long now=Long.valueOf(System.currentTimeMillis());
            objects.stream()
                    .filter(object->object.getTime()>=time1&&object.getTime()<=time2)
                    .forEach(object->
                    {
                        result.add(object);
                        lastAccessTimes.put(object,now);
                    });
            return result;
        }
    }

    public T get(long time)
    {
        synchronized(lockObject)
        {
            return getImpl2(time);
        }
    }

    private T getImpl1(long time)
    {
        return objects.stream()
                .filter(object->object.getTime()==time)
                .findFirst()
                .orElse(null);
    }

    private T getImpl2(long time)
    {
        for(int i=objects.size()-1;i>=0;i--)
            if(objects.get(i).getTime()==time)
                return objects.get(i);
        return null;
    }

    private void clean()
    {
        if(System.currentTimeMillis()-lastCleanTime>CLEAN_DELAY)
        {
            cleanImpl();
            lastCleanTime=System.currentTimeMillis();//à nouveau System.currentTimeMillis() car le cleanImpl peut être long
        }
    }

    private void cleanImpl()
    {
        if(objects.isEmpty())
            return;
        T[] array=objects.stream()
                .toArray(size->(T[])Array.newInstance(objects.get(0).getClass(),size));
        long now=System.currentTimeMillis();
        int removed=0;
        for(T object:array)
            if(now-lastAccessTimes.get(object).longValue()>OLD_DELAY)
            {
                objects.remove(object);
                lastAccessTimes.remove(object);
                removed++;
            }
        if(removed>0)
            if(objects.isEmpty())
                Logger.LOGGER.debug("Cache completely cleaned");
            else
                Logger.LOGGER.debug("Cache removed "+removed+" "+objects.get(0).getClass().getSimpleName()+" object(s)");
    }
}
