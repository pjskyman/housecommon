package sky.housecommon;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EnergyConsumption
{
    private final String consumer1Name;
    private final double consumer1Consumption;
    private final double consumer1Price;
    private final String consumer2Name;
    private final double consumer2Consumption;
    private final double consumer2Price;
    private final String consumer3Name;
    private final double consumer3Consumption;
    private final double consumer3Price;
    private final String consumer4Name;
    private final double consumer4Consumption;
    private final double consumer4Price;
    private final String consumer5Name;
    private final double consumer5Consumption;
    private final double consumer5Price;
    private final String consumer6Name;
    private final double consumer6Consumption;
    private final double consumer6Price;
    private final String consumer7Name;
    private final double consumer7Consumption;
    private final double consumer7Price;
    private final String consumer8Name;
    private final double consumer8Consumption;
    private final double consumer8Price;
    private final String consumer9Name;
    private final double consumer9Consumption;
    private final double consumer9Price;
    private final String consumer10Name;
    private final double consumer10Consumption;
    private final double consumer10Price;
    private static final NumberFormat CONSUMPTION_FORMAT=new DecimalFormat("########0.000");

    public EnergyConsumption(String consumer1Name,double consumer1Consumption,double consumer1Price,String consumer2Name,double consumer2Consumption,double consumer2Price,String consumer3Name,double consumer3Consumption,double consumer3Price,String consumer4Name,double consumer4Consumption,double consumer4Price,String consumer5Name,double consumer5Consumption,double consumer5Price,String consumer6Name,double consumer6Consumption,double consumer6Price,String consumer7Name,double consumer7Consumption,double consumer7Price,String consumer8Name,double consumer8Consumption,double consumer8Price,String consumer9Name,double consumer9Consumption,double consumer9Price,String consumer10Name,double consumer10Consumption,double consumer10Price)
    {
        this.consumer1Name=consumer1Name;
        this.consumer1Consumption=consumer1Consumption;
        this.consumer1Price=consumer1Price;
        this.consumer2Name=consumer2Name;
        this.consumer2Consumption=consumer2Consumption;
        this.consumer2Price=consumer2Price;
        this.consumer3Name=consumer3Name;
        this.consumer3Consumption=consumer3Consumption;
        this.consumer3Price=consumer3Price;
        this.consumer4Name=consumer4Name;
        this.consumer4Consumption=consumer4Consumption;
        this.consumer4Price=consumer4Price;
        this.consumer5Name=consumer5Name;
        this.consumer5Consumption=consumer5Consumption;
        this.consumer5Price=consumer5Price;
        this.consumer6Name=consumer6Name;
        this.consumer6Consumption=consumer6Consumption;
        this.consumer6Price=consumer6Price;
        this.consumer7Name=consumer7Name;
        this.consumer7Consumption=consumer7Consumption;
        this.consumer7Price=consumer7Price;
        this.consumer8Name=consumer8Name;
        this.consumer8Consumption=consumer8Consumption;
        this.consumer8Price=consumer8Price;
        this.consumer9Name=consumer9Name;
        this.consumer9Consumption=consumer9Consumption;
        this.consumer9Price=consumer9Price;
        this.consumer10Name=consumer10Name;
        this.consumer10Consumption=consumer10Consumption;
        this.consumer10Price=consumer10Price;
    }

    public String getConsumer1Name()
    {
        return consumer1Name;
    }

    public double getConsumer1Consumption()
    {
        return consumer1Consumption;
    }

    public double getConsumer1Price()
    {
        return consumer1Price;
    }

    public String getConsumer2Name()
    {
        return consumer2Name;
    }

    public double getConsumer2Consumption()
    {
        return consumer2Consumption;
    }

    public double getConsumer2Price()
    {
        return consumer2Price;
    }

    public String getConsumer3Name()
    {
        return consumer3Name;
    }

    public double getConsumer3Consumption()
    {
        return consumer3Consumption;
    }

    public double getConsumer3Price()
    {
        return consumer3Price;
    }

    public String getConsumer4Name()
    {
        return consumer4Name;
    }

    public double getConsumer4Consumption()
    {
        return consumer4Consumption;
    }

    public double getConsumer4Price()
    {
        return consumer4Price;
    }

    public String getConsumer5Name()
    {
        return consumer5Name;
    }

    public double getConsumer5Consumption()
    {
        return consumer5Consumption;
    }

    public double getConsumer5Price()
    {
        return consumer5Price;
    }

    public String getConsumer6Name()
    {
        return consumer6Name;
    }

    public double getConsumer6Consumption()
    {
        return consumer6Consumption;
    }

    public double getConsumer6Price()
    {
        return consumer6Price;
    }

    public String getConsumer7Name()
    {
        return consumer7Name;
    }

    public double getConsumer7Consumption()
    {
        return consumer7Consumption;
    }

    public double getConsumer7Price()
    {
        return consumer7Price;
    }

    public String getConsumer8Name()
    {
        return consumer8Name;
    }

    public double getConsumer8Consumption()
    {
        return consumer8Consumption;
    }

    public double getConsumer8Price()
    {
        return consumer8Price;
    }

    public String getConsumer9Name()
    {
        return consumer9Name;
    }

    public double getConsumer9Consumption()
    {
        return consumer9Consumption;
    }

    public double getConsumer9Price()
    {
        return consumer9Price;
    }

    public String getConsumer10Name()
    {
        return consumer10Name;
    }

    public double getConsumer10Consumption()
    {
        return consumer10Consumption;
    }

    public double getConsumer10Price()
    {
        return consumer10Price;
    }

    public String getConsumerName(int rank)
    {
        try
        {
            return (String)getClass().getMethod("getConsumer"+rank+"Name").invoke(this);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public double getConsumerConsumption(int rank)
    {
        try
        {
            return ((Double)getClass().getMethod("getConsumer"+rank+"Consumption").invoke(this)).doubleValue();
        }
        catch(Exception e)
        {
            return -1d;
        }
    }

    public double getConsumerPrice(int rank)
    {
        try
        {
            return ((Double)getClass().getMethod("getConsumer"+rank+"Price").invoke(this)).doubleValue();
        }
        catch(Exception e)
        {
            return -1d;
        }
    }

    public double getTotalOfConsumptions()
    {
        double total=0d;
        for(int rank=1;rank<=10;rank++)
            total+=getConsumerConsumption(rank);
        return total;
    }

    public double getTotalOfPrices()
    {
        double total=0d;
        for(int rank=1;rank<=10;rank++)
            total+=getConsumerPrice(rank);
        return total;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName()
               + "\r\n{"
               + "\r\n\t\""+consumer1Name+"\"="+CONSUMPTION_FORMAT.format(consumer1Consumption)+" kWh,"
               + "\r\n\t\""+consumer2Name+"\"="+CONSUMPTION_FORMAT.format(consumer2Consumption)+" kWh,"
               + "\r\n\t\""+consumer3Name+"\"="+CONSUMPTION_FORMAT.format(consumer3Consumption)+" kWh,"
               + "\r\n\t\""+consumer4Name+"\"="+CONSUMPTION_FORMAT.format(consumer4Consumption)+" kWh,"
               + "\r\n\t\""+consumer5Name+"\"="+CONSUMPTION_FORMAT.format(consumer5Consumption)+" kWh,"
               + "\r\n\t\""+consumer6Name+"\"="+CONSUMPTION_FORMAT.format(consumer6Consumption)+" kWh,"
               + "\r\n\t\""+consumer7Name+"\"="+CONSUMPTION_FORMAT.format(consumer7Consumption)+" kWh,"
               + "\r\n\t\""+consumer8Name+"\"="+CONSUMPTION_FORMAT.format(consumer8Consumption)+" kWh,"
               + "\r\n\t\""+consumer9Name+"\"="+CONSUMPTION_FORMAT.format(consumer9Consumption)+" kWh,"
               + "\r\n\t\""+consumer10Name+"\"="+CONSUMPTION_FORMAT.format(consumer10Consumption)+" kWh,"
               + "\r\n\ttotalOfConsumptions="+CONSUMPTION_FORMAT.format(getTotalOfConsumptions())+" kWh"
               + "\r\n}";
    }
}
