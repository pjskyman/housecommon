package sky.housecommon;

public class OffPeakHourPeriodEfficiency
{
    private final double consumptionEfficiency;
    private final double priceEfficiency;
    private final PricingPeriod pricingPeriod;
    private final double offPeakConsumption;

    public OffPeakHourPeriodEfficiency(double consumptionEfficiency,double priceEfficiency,PricingPeriod pricingPeriod,double offPeakConsumption)
    {
        this.consumptionEfficiency=consumptionEfficiency;
        this.priceEfficiency=priceEfficiency;
        this.pricingPeriod=pricingPeriod;
        this.offPeakConsumption=offPeakConsumption;
    }

    public double getConsumptionEfficiency()
    {
        return consumptionEfficiency;
    }

    public double getPriceEfficiency()
    {
        return priceEfficiency;
    }

    public PricingPeriod getPricingPeriod()
    {
        return pricingPeriod;
    }

    public double getOffPeakConsumption()
    {
        return offPeakConsumption;
    }
}
