package sky.housecommon;

public class WasherInstantaneousConsumption
{
    private final long time;
    private final PricingPeriod pricingPeriod;
    private final int consumption;

    public WasherInstantaneousConsumption(long time,PricingPeriod pricingPeriod,int consumption)
    {
        this.time=time;
        this.pricingPeriod=pricingPeriod;
        this.consumption=consumption;
    }

    public long getTime()
    {
        return time;
    }

    public PricingPeriod getPricingPeriod()
    {
        return pricingPeriod;
    }

    public int getConsumption()
    {
        return consumption;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName()
               + "\r\n{"
               + "\r\n\ttime="+time+","
               + "\r\n\tpricingPeriod="+pricingPeriod+","
               + "\r\n\t\"L&V\"="+consumption+" W"
               + "\r\n}";
    }
}
