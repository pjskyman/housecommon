package sky.housecommon;

public class Temperature implements Timed,Comparable<Temperature>
{
    private final long time;
    private final double temperature;
    private final double setPoint;
    private final double ratio;
    private final boolean heaterOn;

    public Temperature(long time,double temperature,double setPoint,double ratio,boolean heaterOn)
    {
        this.time=time;
        this.temperature=temperature;
        this.setPoint=setPoint;
        this.ratio=ratio;
        this.heaterOn=heaterOn;
    }

    public long getTime()
    {
        return time;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public double getSetPoint()
    {
        return setPoint;
    }

    public double getRatio()
    {
        return ratio;
    }

    public boolean isHeaterOn()
    {
        return heaterOn;
    }

    public int compareTo(Temperature o)
    {
        return Long.compare(time,o.time);
    }

    @Override
    public int hashCode()
    {
        int hash=3;
        hash=23*hash+(int)(this.time^(this.time>>>32));
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this==obj)
            return true;
        if(obj==null)
            return false;
        if(getClass()!=obj.getClass())
            return false;
        final Temperature other=(Temperature)obj;
        if(this.time!=other.time)
            return false;
        return true;
    }
}
