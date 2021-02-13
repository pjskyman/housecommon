package sky.housecommon;

import java.util.List;
import java.util.Random;

public class ElectricityUtils
{
    private static final Random RANDOM=new Random();

    private ElectricityUtils()
    {
    }

    public static int calculatePowerBarHeight(int power)
    {
        if(power==0)
            return 0;
        double ordinate1=13.105178503466455d
                +.8935371553157391d*(double)power
                +.0011767571668344792d*Math.pow((double)power,2d)
                -1.2087136238229091e-4d*Math.pow((double)power,3d)
                +1.1938931060353606e-6d*Math.pow((double)power,4d)
                -5.366042379604714e-9d*Math.pow((double)power,5d)
                +1.2375520523981775e-11d*Math.pow((double)power,6d)
                -1.5252113325589502e-14d*Math.pow((double)power,7d)
                +1.0111118973594798e-17d*Math.pow((double)power,8d)
                -3.379100166542495e-21d*Math.pow((double)power,9d)
                +4.117422344411683e-25d*Math.pow((double)power,10d)
                +2.951303006401026e-29d*Math.pow((double)power,11d)
                -4.740442092946457e-33d*Math.pow((double)power,12d)
                -8.818731061778005e-37d*Math.pow((double)power,13d)
                -3.5639045982917994e-41d*Math.pow((double)power,14d)
                +1.8920748927329792e-44d*Math.pow((double)power,15d);
        double ordinate2=23.462972022832d+11.798263558634d*Math.log((double)power);
        if(power<180)
            return (int)Math.max(13d,ordinate1)-13+1;
        else
            if(power>200)
                return (int)Math.min(128d,ordinate2)-13+1;
            else
            {
                double factor=((double)power-180d)/20d;
                return (int)((1d-factor)*ordinate1+factor*ordinate2)-13+1;
            }
    }

    public static EnergyConsumption calculateEnergyConsumption(int year)
    {
        return calculateEnergyConsumption(Database.getInstantaneousConsumptions(year));
    }

    public static EnergyConsumption calculateEnergyConsumption(int month,int year)
    {
        return calculateEnergyConsumption(Database.getInstantaneousConsumptions(month,year));
    }

    public static EnergyConsumption calculateEnergyConsumption(int day,int month,int year)
    {
        return calculateEnergyConsumption(Database.getInstantaneousConsumptions(day,month,year));
    }

    public static EnergyConsumption calculateEnergyConsumption(int day1,int month1,int year1,int day2,int month2,int year2)
    {
        return calculateEnergyConsumption(Database.getInstantaneousConsumptions(day1,month1,year1,day2,month2,year2));
    }

    public static EnergyConsumption calculateEnergyConsumption(List<InstantaneousConsumption> instantaneousConsumptions)
    {
        double[] accumulations=new double[10];
        double[] prices=new double[10];
        for(int i=0;i<instantaneousConsumptions.size();i++)
        {
            double timeOffset;//en secondes
            if(i==0)
                timeOffset=(double)(instantaneousConsumptions.get(1).getTime()-instantaneousConsumptions.get(0).getTime())/1000d;
            else
                if(i==instantaneousConsumptions.size()-1)
                    timeOffset=(double)(instantaneousConsumptions.get(instantaneousConsumptions.size()-1).getTime()-instantaneousConsumptions.get(instantaneousConsumptions.size()-2).getTime())/1000d;
                else
                {
                    InstantaneousConsumption previousInstantaneousConsumption=instantaneousConsumptions.get(i-1);
                    InstantaneousConsumption nextInstantaneousConsumption=instantaneousConsumptions.get(i+1);
                    timeOffset=(double)(nextInstantaneousConsumption.getTime()-previousInstantaneousConsumption.getTime())/2d/1000d;
                }
            for(int j=0;j<10;j++)
            {
                double increment=(double)instantaneousConsumptions.get(i).getConsumerConsumption(j+1)*timeOffset;
                accumulations[j]+=increment;
                prices[j]+=increment*instantaneousConsumptions.get(i).getPricingPeriod().getPrice();
            }
        }
        InstantaneousConsumption instantaneousConsumption=instantaneousConsumptions.isEmpty()?Database.getLastInstantaneousConsumption():instantaneousConsumptions.get(0);
        return new EnergyConsumption(instantaneousConsumption.getConsumer1Name(),accumulations[0]/3600d/1000d,prices[0]/3600d/1000d,
                                     instantaneousConsumption.getConsumer2Name(),accumulations[1]/3600d/1000d,prices[1]/3600d/1000d,
                                     instantaneousConsumption.getConsumer3Name(),accumulations[2]/3600d/1000d,prices[2]/3600d/1000d,
                                     instantaneousConsumption.getConsumer4Name(),accumulations[3]/3600d/1000d,prices[3]/3600d/1000d,
                                     instantaneousConsumption.getConsumer5Name(),accumulations[4]/3600d/1000d,prices[4]/3600d/1000d,
                                     instantaneousConsumption.getConsumer6Name(),accumulations[5]/3600d/1000d,prices[5]/3600d/1000d,
                                     instantaneousConsumption.getConsumer7Name(),accumulations[6]/3600d/1000d,prices[6]/3600d/1000d,
                                     instantaneousConsumption.getConsumer8Name(),accumulations[7]/3600d/1000d,prices[7]/3600d/1000d,
                                     instantaneousConsumption.getConsumer9Name(),accumulations[8]/3600d/1000d,prices[8]/3600d/1000d,
                                     instantaneousConsumption.getConsumer10Name(),accumulations[9]/3600d/1000d,prices[9]/3600d/1000d);
    }

    public static OffPeakHourPeriodEfficiency calculateOffPeakHourPeriodEfficiency(int year)
    {
        return calculateOffPeakHourPeriodEfficiency(Database.getInstantaneousConsumptions(year));
    }

    public static OffPeakHourPeriodEfficiency calculateOffPeakHourPeriodEfficiency(int month,int year)
    {
        return calculateOffPeakHourPeriodEfficiency(Database.getInstantaneousConsumptions(month,year));
    }

    public static OffPeakHourPeriodEfficiency calculateOffPeakHourPeriodEfficiency(int day,int month,int year)
    {
        return calculateOffPeakHourPeriodEfficiency(Database.getInstantaneousConsumptions(day,month,year));
    }

    public static OffPeakHourPeriodEfficiency calculateOffPeakHourPeriodEfficiency(int day1,int month1,int year1,int day2,int month2,int year2)
    {
        return calculateOffPeakHourPeriodEfficiency(Database.getInstantaneousConsumptions(day1,month1,year1,day2,month2,year2));
    }

    public static OffPeakHourPeriodEfficiency calculateOffPeakHourPeriodEfficiency(List<InstantaneousConsumption> instantaneousConsumptions)
    {
        double accumulation=0d;
        double price=0d;
        double offPeakAccumulation=0d;
        double offPeakPrice=0d;
        for(int i=0;i<instantaneousConsumptions.size();i++)
        {
            double timeOffset;//en secondes
            if(i==0)
                timeOffset=(double)(instantaneousConsumptions.get(1).getTime()-instantaneousConsumptions.get(0).getTime())/1000d;
            else
                if(i==instantaneousConsumptions.size()-1)
                    timeOffset=(double)(instantaneousConsumptions.get(instantaneousConsumptions.size()-1).getTime()-instantaneousConsumptions.get(instantaneousConsumptions.size()-2).getTime())/1000d;
                else
                {
                    InstantaneousConsumption previousInstantaneousConsumption=instantaneousConsumptions.get(i-1);
                    InstantaneousConsumption nextInstantaneousConsumption=instantaneousConsumptions.get(i+1);
                    timeOffset=(double)(nextInstantaneousConsumption.getTime()-previousInstantaneousConsumption.getTime())/2d/1000d;
                }
            InstantaneousConsumption instantaneousConsumption=instantaneousConsumptions.get(i);
            double increment=(double)instantaneousConsumption.getTotalOfConsumptions()*timeOffset;
            accumulation+=increment;
            double priceIncrement=increment*instantaneousConsumption.getPricingPeriod().getPrice();
            price+=priceIncrement;
            if(instantaneousConsumption.getPricingPeriod().isOffPeakHourPeriod())
            {
                offPeakAccumulation+=increment;
                offPeakPrice+=priceIncrement;
            }
        }
        double consumptionEfficiency=offPeakAccumulation/accumulation;
        if(Double.isNaN(consumptionEfficiency)||Double.isInfinite(consumptionEfficiency))
            consumptionEfficiency=0d;
        double priceEfficiency=offPeakPrice/price;
        if(Double.isNaN(priceEfficiency)||Double.isInfinite(priceEfficiency))
            priceEfficiency=0d;
        PricingPeriod pricingPeriod;
        if(instantaneousConsumptions.size()>1000)
            pricingPeriod=instantaneousConsumptions.get(1000).getPricingPeriod();
        else
            if(!instantaneousConsumptions.isEmpty())
                pricingPeriod=instantaneousConsumptions.get(RANDOM.nextInt(instantaneousConsumptions.size())).getPricingPeriod();
            else
                pricingPeriod=PricingPeriod.BLUE_DAY_PEAK_HOUR;
        return new OffPeakHourPeriodEfficiency(consumptionEfficiency*100d,priceEfficiency*100d,pricingPeriod,offPeakAccumulation/3600d/1000d);
    }

//    public static void main(String[] args)
//    {
//        for(int day=1;day<=10;day++)
//        {
//            System.out.println("day="+day);
//            EnergyConsumption energyConsumption=calculateEnergyConsumption(day,2,2020);
//            System.out.println(AbstractPage.DECIMAL_000_FORMAT.format(energyConsumption.getTotalOfConsumptions())+" kWh");
//            System.out.println(AbstractPage.DECIMAL_00_FORMAT.format(energyConsumption.getTotalOfPrices())+" €");
//            OffPeakHourPeriodEfficiency offPeakHourPeriodEfficiency=calculateOffPeakHourPeriodEfficiency(day,2,2020);
//            System.out.println(AbstractPage.DECIMAL_0_FORMAT.format(offPeakHourPeriodEfficiency.getConsumptionEfficiency())+" %");
//            System.out.println(AbstractPage.DECIMAL_0_FORMAT.format(offPeakHourPeriodEfficiency.getPriceEfficiency())+" %");
//            PricingPeriod pricingPeriod=offPeakHourPeriodEfficiency.getPricingPeriod();
//            double savedMoney;
//            if(pricingPeriod.isBlueDay())
//                savedMoney=offPeakHourPeriodEfficiency.getOffPeakConsumption()*(PricingPeriod.BLUE_DAY_PEAK_HOUR.getPrice()-PricingPeriod.BLUE_DAY_OFF_PEAK_HOUR.getPrice());
//            else
//                if(pricingPeriod.isWhiteDay())
//                    savedMoney=offPeakHourPeriodEfficiency.getOffPeakConsumption()*(PricingPeriod.WHITE_DAY_PEAK_HOUR.getPrice()-PricingPeriod.WHITE_DAY_OFF_PEAK_HOUR.getPrice());
//                else
//                    savedMoney=offPeakHourPeriodEfficiency.getOffPeakConsumption()*(PricingPeriod.RED_DAY_PEAK_HOUR.getPrice()-PricingPeriod.RED_DAY_OFF_PEAK_HOUR.getPrice());
//            System.out.println(AbstractPage.DECIMAL_00_FORMAT.format(savedMoney)+" €");
//            System.out.println();
//        }
//    }

//    @Deprecated
//    public static void main2(String[] args)
//    {
//        long startTime=System.currentTimeMillis();
//        //Lave-vaisselle 45-65 29/12/2018 1546099386831L 1546108347421L
//        //Lave-linge 40 30/12/2018 1546193633099L 1546201293588L
//        //Lave-vaisselle 70 01/01/2019 1546356684103L 1546364874626L
//        //Lave-vaisselle 50 02/01/2019 1546479018996L 1546489909748L
//        //Lave-linge 60 30&31/01/2019 1548904134106L 1548912624649L
//
//        List<InstantaneousConsumption> list=getInstantaneousConsumptions(30,1,2019,31,1,2019);
//
////        for(InstantaneousConsumption instantaneousConsumption:list)
////            System.out.println(instantaneousConsumption.getTime()+" "+instantaneousConsumption.getConsumer7Consumption());
////        if(2==2)
////            return;
//
//        List<InstantaneousConsumption> list2=new ArrayList<>();
//        list.stream()
//                .filter(instantaneousConsumption->instantaneousConsumption.getTime()>=1548904134106L)
//                .filter(instantaneousConsumption->instantaneousConsumption.getTime()<=1548912624649L)
//                .forEach(list2::add);
//        try(DataOutputStream outputStream=new DataOutputStream(new FileOutputStream(new File("profile.profile"))))
//        {
//            outputStream.writeInt(list2.size());
//            for(InstantaneousConsumption instantaneousConsumption:list2)
//            {
//                outputStream.writeLong(instantaneousConsumption.getTime());
//                outputStream.writeInt(instantaneousConsumption.getConsumer7Consumption());
//                System.out.println(instantaneousConsumption.getTime()+" "+instantaneousConsumption.getConsumer7Consumption());
//            }
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//        ConsumptionProfileCalculator consumptionProfile=ConsumptionProfileCalculator.construct(list2);
//        List<PricingPeriodZone> pricingPeriodZones=new ArrayList<>();
//        pricingPeriodZones.add(new PricingPeriodZone(0,0,6,2,PricingPeriod.RED_DAY_OFF_PEAK_HOUR));
//        pricingPeriodZones.add(new PricingPeriodZone(6,2,7,32,PricingPeriod.BLUE_DAY_OFF_PEAK_HOUR));
//        pricingPeriodZones.add(new PricingPeriodZone(7,32,20,00,PricingPeriod.BLUE_DAY_PEAK_HOUR));
//        BufferedImage image=new BufferedImage(840,900,BufferedImage.TYPE_INT_ARGB_PRE);
//        Graphics2D g2d=image.createGraphics();
//        g2d.setColor(Color.WHITE);
//        g2d.fillRect(0,0,840,900);
//        for(int hour=2;hour<=8;hour++)
//        {
//            g2d.setColor(Color.BLACK);
//            g2d.drawLine((hour-2)*60*2,0,(hour-2)*60*2,900);
//            g2d.setColor(Color.RED);
//            g2d.drawString(""+hour,(hour-2)*60*2,15);
//            g2d.setColor(Color.BLACK);
//            for(int minute=0;minute<=59;minute++)
//            {
//                if(minute>0&&minute%10==0)
//                {
//                    g2d.setColor(Color.LIGHT_GRAY);
//                    g2d.drawLine((hour-2)*60*2+minute*2,0,(hour-2)*60*2+minute*2,900);
//                    g2d.drawLine((hour-2)*60*2+minute*2+1,0,(hour-2)*60*2+minute*2+1,900);
//                    g2d.setColor(Color.BLACK);
//                }
//                double price=consumptionProfile.getTotalPricing(hour,minute,pricingPeriodZones);
//                System.out.println(hour+":"+minute+" "+price);
//                g2d.drawLine((hour-2)*60*2+minute*2,900,(hour-2)*60*2+minute*2,900-(int)(price/.55d*900d));
//                g2d.drawLine((hour-2)*60*2+minute*2+1,900,(hour-2)*60*2+minute*2+1,900-(int)(price/.55d*900d));
//            }
//        }
//        g2d.dispose();
//        try
//        {
//            ImageIO.write(image,"png",new File("price.png"));
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//        System.out.println("Total "+(System.currentTimeMillis()-startTime)+" ms");
//    }
}
