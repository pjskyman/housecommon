package sky.housecommon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import sky.program.Duration;

public final class Database
{
    private static Connection ecocompteurConnection=null;
    private static Connection toilettesConnection=null;
    private static final Object LOCK_OBJECT=new Object();
    public static final DatabaseCache<InstantaneousConsumption> INSTANTANEOUS_CONSUMPTION_CACHE=new DatabaseCache<>();
    private static final DatabaseCache<Temperature> TEMPERATURE_CACHE=new DatabaseCache<>();

    private Database()
    {
    }

    public static boolean insertInstantaneousConsumption(InstantaneousConsumption instantaneousConsumption) throws NotAvailableDatabaseException
    {
        boolean success=false;
        try
        {
            Connection connection=getEcocompteurConnection();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                int result=statement.executeUpdate("INSERT INTO instantaneous_consumption VALUES ("
                        + instantaneousConsumption.getTime()+","
                        + instantaneousConsumption.getPricingPeriod().getCode()+","
                        + instantaneousConsumption.getBlueDayOffPeakHourTotal()+","
                        + instantaneousConsumption.getBlueDayPeakHourTotal()+","
                        + instantaneousConsumption.getWhiteDayOffPeakHourTotal()+","
                        + instantaneousConsumption.getWhiteDayPeakHourTotal()+","
                        + instantaneousConsumption.getRedDayOffPeakHourTotal()+","
                        + instantaneousConsumption.getRedDayPeakHourTotal()+","
                        + "'"+instantaneousConsumption.getConsumer1Name()+"',"
                        + instantaneousConsumption.getConsumer1Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer2Name()+"',"
                        + instantaneousConsumption.getConsumer2Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer3Name()+"',"
                        + instantaneousConsumption.getConsumer3Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer4Name()+"',"
                        + instantaneousConsumption.getConsumer4Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer5Name()+"',"
                        + instantaneousConsumption.getConsumer5Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer6Name()+"',"
                        + instantaneousConsumption.getConsumer6Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer7Name()+"',"
                        + instantaneousConsumption.getConsumer7Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer8Name()+"',"
                        + instantaneousConsumption.getConsumer8Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer9Name()+"',"
                        + instantaneousConsumption.getConsumer9Consumption()+","
                        + "'"+instantaneousConsumption.getConsumer10Name()+"',"
                        + instantaneousConsumption.getConsumer10Consumption()+");");
                if(result==1)
                    success=true;
            }
        }
        catch(SQLException ex)
        {
            Logger.LOGGER.error(ex.toString());
            throw new NotAvailableDatabaseException(ex);
        }
        return success;
    }

    public static InstantaneousConsumption getLastCachedInstantaneousConsumption()
    {
        return INSTANTANEOUS_CONSUMPTION_CACHE.getLast();
    }

    public static InstantaneousConsumption getLastInstantaneousConsumption()//fait appel à la BDD en permanence, pour toujours savoir si on a la plus fraîche information ou pas
    {
        List<InstantaneousConsumption> instantaneousConsumptions=getLastInstantaneousConsumptions(1);
        if(instantaneousConsumptions.isEmpty())
            return null;
        else
        {
            INSTANTANEOUS_CONSUMPTION_CACHE.add(instantaneousConsumptions.get(0));
            return instantaneousConsumptions.get(0);
        }
    }

    private static List<InstantaneousConsumption> getLastInstantaneousConsumptions(int count)//réservé à des usages en interne
    {
        try
        {
            Connection connection=getEcocompteurConnection();
            long startTime=System.currentTimeMillis();
            List<InstantaneousConsumption> instantaneousConsumptions=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM instantaneous_consumption ORDER BY time DESC LIMIT "+count+";"))
                {
                    while(resultSet.next())
                        readResultSetInstantaneousConsumptionRow(resultSet,instantaneousConsumptions);
                }
            }
            Collections.reverse(instantaneousConsumptions);
//            Logger.LOGGER.debug(instantaneousConsumptions.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return instantaneousConsumptions;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    public static List<InstantaneousConsumption> getLastInstantaneousConsumptionsWhile(long time)
    {
        long now=System.currentTimeMillis();
        long time1=now-time;
        long time2=now+Duration.of(5).second();
        return getInstantaneousConsumptionsBetween(time1,time2);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptionsFor(int year)
    {
        return getInstantaneousConsumptionsBetween(1,1,year,31,12,year);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptionsFor(int month,int year)
    {
        return getInstantaneousConsumptionsBetween(1,month,year,new GregorianCalendar(year,month-1,1).getActualMaximum(Calendar.DAY_OF_MONTH),month,year);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptionsFor(int day,int month,int year)
    {
        return getInstantaneousConsumptionsBetween(day,month,year,day,month,year);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptionsBetween(int day1,int month1,int year1,int day2,int month2,int year2)
    {
        GregorianCalendar startCalendar=new GregorianCalendar();
        startCalendar.clear();
        startCalendar.set(Calendar.YEAR,year1);
        startCalendar.set(Calendar.MONTH,month1-1);
        startCalendar.set(Calendar.DAY_OF_MONTH,day1);
        startCalendar.set(Calendar.HOUR_OF_DAY,6);
        GregorianCalendar endCalendar=new GregorianCalendar();
        endCalendar.clear();
        endCalendar.set(Calendar.YEAR,year2);
        endCalendar.set(Calendar.MONTH,month2-1);
        endCalendar.set(Calendar.DAY_OF_MONTH,day2);
        endCalendar.set(Calendar.HOUR_OF_DAY,6);
        endCalendar.setTimeInMillis(endCalendar.getTimeInMillis()+Duration.of(1).day());
        int endYear=endCalendar.get(Calendar.YEAR);
        int endMonth=endCalendar.get(Calendar.MONTH);
        int endDay=endCalendar.get(Calendar.DAY_OF_MONTH);
        endCalendar.clear();
        endCalendar.set(Calendar.YEAR,endYear);
        endCalendar.set(Calendar.MONTH,endMonth);
        endCalendar.set(Calendar.DAY_OF_MONTH,endDay);
        endCalendar.set(Calendar.HOUR_OF_DAY,6);
        return getInstantaneousConsumptionsBetween(startCalendar.getTimeInMillis(),endCalendar.getTimeInMillis());
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptionsBetween(long time1,long time2)
    {
        List<InstantaneousConsumption> list=INSTANTANEOUS_CONSUMPTION_CACHE.getAllBetween(time1,time2);
        if(list.isEmpty())
        {
            List<InstantaneousConsumption> instantaneousConsumptions=getInstantaneousConsumptionsImpl(time1,time2);
            INSTANTANEOUS_CONSUMPTION_CACHE.addAll(instantaneousConsumptions);
            list.addAll(instantaneousConsumptions);
        }
        else
        {
            if(list.get(0).getTime()-time1>Duration.of(7).secondPlus(500).millisecond())
            {
                List<InstantaneousConsumption> instantaneousConsumptions=getInstantaneousConsumptionsImpl(time1,list.get(0).getTime());
                INSTANTANEOUS_CONSUMPTION_CACHE.addAll(instantaneousConsumptions);
                instantaneousConsumptions.stream()
                        .filter(instantaneousConsumption->!list.contains(instantaneousConsumption))
                        .forEach(instantaneousConsumption->list.add(0,instantaneousConsumption));
                list.sort(null);
            }
            if(time2-list.get(list.size()-1).getTime()>Duration.of(7).secondPlus(500).millisecond())
            {
                List<InstantaneousConsumption> instantaneousConsumptions=getInstantaneousConsumptionsImpl(list.get(list.size()-1).getTime(),time2);
                INSTANTANEOUS_CONSUMPTION_CACHE.addAll(instantaneousConsumptions);
                instantaneousConsumptions.stream()
                        .filter(instantaneousConsumption->!list.contains(instantaneousConsumption))
                        .forEach(list::add);
                list.sort(null);//par précaution, la liste est en théorie déjà triée à ce stade
            }
        }
        List<InstantaneousConsumption> list2=new ArrayList<>(list);//on fait une copie, et on va faire évoluer la copie uniquement
        boolean modified=false;
        for(int i=1;i<list.size();i++)
        {
            long previousTime=list.get(i-1).getTime();
            long currentTime=list.get(i).getTime();
            if(currentTime-previousTime>Duration.of(7).secondPlus(500).millisecond())
            {
                List<InstantaneousConsumption> instantaneousConsumptions=getInstantaneousConsumptionsImpl(previousTime,currentTime);
                INSTANTANEOUS_CONSUMPTION_CACHE.addAll(instantaneousConsumptions);
                instantaneousConsumptions.stream()
                        .filter(instantaneousConsumption->!list2.contains(instantaneousConsumption))
                        .forEach(list2::add);
                modified=true;
            }
        }
        if(modified)
            list2.sort(null);
        return list2;
    }

    private static List<InstantaneousConsumption> getInstantaneousConsumptionsImpl(long time1,long time2)
    {
        try
        {
            Connection connection=getEcocompteurConnection();
            long startTime=System.currentTimeMillis();
            List<InstantaneousConsumption> instantaneousConsumptions=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM instantaneous_consumption WHERE time>="+time1+" AND time<"+time2+" ORDER BY time;"))
                {
                    while(resultSet.next())
                        readResultSetInstantaneousConsumptionRow(resultSet,instantaneousConsumptions);
                }
            }
//            List<InstantaneousConsumption> list=new ArrayList<>();
//            instantaneousConsumptions.stream().filter(t->
//            {
//                if(t.getPricingPeriod().isBlueDay())
//                    return instantaneousConsumptions.get(instantaneousConsumptions.size()/2).getPricingPeriod().isBlueDay();
//                else
//                    if(t.getPricingPeriod().isWhiteDay())
//                        return instantaneousConsumptions.get(instantaneousConsumptions.size()/2).getPricingPeriod().isWhiteDay();
//                    else
//                        return instantaneousConsumptions.get(instantaneousConsumptions.size()/2).getPricingPeriod().isRedDay();
//            }).forEach(list::add);
//            Logger.LOGGER.debug(instantaneousConsumptions.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return instantaneousConsumptions;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    private static void readResultSetInstantaneousConsumptionRow(ResultSet resultSet,List<InstantaneousConsumption> instantaneousConsumptions) throws SQLException
    {
        long time=resultSet.getLong("time");
        PricingPeriod pricingPeriod=PricingPeriod.getPricingPeriodForCode(resultSet.getInt("pricingPeriod"));
        double blueDayOffPeakHourTotal=resultSet.getDouble("blueDayOffPeakHourTotal");
        double blueDayPeakHourTotal=resultSet.getDouble("blueDayPeakHourTotal");
        double whiteDayOffPeakHourTotal=resultSet.getDouble("whiteDayOffPeakHourTotal");
        double whiteDayPeakHourTotal=resultSet.getDouble("whiteDayPeakHourTotal");
        double redDayOffPeakHourTotal=resultSet.getDouble("redDayOffPeakHourTotal");
        double redDayPeakHourTotal=resultSet.getDouble("redDayPeakHourTotal");
        String consumer1Name=resultSet.getString("consumer1Name");
        int consumer1Consumption=resultSet.getInt("consumer1Consumption");
        String consumer2Name=resultSet.getString("consumer2Name");
        int consumer2Consumption=resultSet.getInt("consumer2Consumption");
        String consumer3Name=resultSet.getString("consumer3Name");
        int consumer3Consumption=resultSet.getInt("consumer3Consumption");
        String consumer4Name=resultSet.getString("consumer4Name");
        int consumer4Consumption=resultSet.getInt("consumer4Consumption");
        String consumer5Name=resultSet.getString("consumer5Name");
        int consumer5Consumption=resultSet.getInt("consumer5Consumption");
        String consumer6Name=resultSet.getString("consumer6Name");
        int consumer6Consumption=resultSet.getInt("consumer6Consumption");
        String consumer7Name=resultSet.getString("consumer7Name");
        int consumer7Consumption=resultSet.getInt("consumer7Consumption");
        String consumer8Name=resultSet.getString("consumer8Name");
        int consumer8Consumption=resultSet.getInt("consumer8Consumption");
        String consumer9Name=resultSet.getString("consumer9Name");
        int consumer9Consumption=resultSet.getInt("consumer9Consumption");
        String consumer10Name=resultSet.getString("consumer10Name");
        int consumer10Consumption=resultSet.getInt("consumer10Consumption");
        instantaneousConsumptions.add(new InstantaneousConsumption(time,
                pricingPeriod,
                blueDayOffPeakHourTotal,
                blueDayPeakHourTotal,
                whiteDayOffPeakHourTotal,
                whiteDayPeakHourTotal,
                redDayOffPeakHourTotal,
                redDayPeakHourTotal,
                consumer1Name,
                consumer1Consumption,
                consumer2Name,
                consumer2Consumption,
                consumer3Name,
                consumer3Consumption,
                consumer4Name,
                consumer4Consumption,
                consumer5Name,
                consumer5Consumption,
                consumer6Name,
                consumer6Consumption,
                consumer7Name,
                consumer7Consumption,
                consumer8Name,
                consumer8Consumption,
                consumer9Name,
                consumer9Consumption,
                consumer10Name,
                consumer10Consumption));
    }

    public static InstantaneousConsumption getInstantaneousConsumption(long time)
    {
        return INSTANTANEOUS_CONSUMPTION_CACHE.get(time);
    }

    public static List<WasherInstantaneousConsumption> getWasherInstantaneousConsumptions(long startTimeRequested,long stopTimeRequested)
    {
        try
        {
            Connection connection=getEcocompteurConnection();
            long startTime=System.currentTimeMillis();
            List<WasherInstantaneousConsumption> washerInstantaneousConsumptions=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT time,pricingPeriod,consumer7Consumption as consumption FROM instantaneous_consumption WHERE time>="+startTimeRequested+" AND time<"+stopTimeRequested+" ORDER BY time;"))
                {
                    while(resultSet.next())
                    {
                        long time=resultSet.getLong("time");
                        PricingPeriod pricingPeriod=PricingPeriod.getPricingPeriodForCode(resultSet.getInt("pricingPeriod"));
                        int consumption=resultSet.getInt("consumption");
                        washerInstantaneousConsumptions.add(new WasherInstantaneousConsumption(time,pricingPeriod,consumption));
                    }
                }
            }
//            Logger.LOGGER.debug(washerInstantaneousConsumptions.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return washerInstantaneousConsumptions;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    public static boolean insertTemperature(Temperature temperature) throws NotAvailableDatabaseException
    {
        boolean success=false;
        try
        {
            Connection connection=getToilettesConnection();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                int result=statement.executeUpdate("INSERT INTO temperature VALUES ("
                        + temperature.getTime()+","
                        + temperature.getTemperature()+","
                        + temperature.getSetPoint()+","
                        + temperature.getRatio()+","
                        + (temperature.isHeaterOn()?1:0)+");");
                if(result==1)
                    success=true;
            }
        }
        catch(SQLException ex)
        {
            Logger.LOGGER.error(ex.toString());
            throw new NotAvailableDatabaseException(ex);
        }
        return success;
    }

    public static Temperature getLastCachedTemperature()
    {
        return TEMPERATURE_CACHE.getLast();
    }

    public static Temperature getLastTemperature()//fait appel à la BDD en permanence, pour toujours savoir si on a la plus fraîche information ou pas
    {
        List<Temperature> temperatures=getLastTemperatures(1);
        if(temperatures.isEmpty())
            return null;
        else
        {
            TEMPERATURE_CACHE.add(temperatures.get(0));
            return temperatures.get(0);
        }
    }

    private static List<Temperature> getLastTemperatures(int count)//réservé à des usages en interne
    {
        try
        {
            Connection connection=getToilettesConnection();
            long startTime=System.currentTimeMillis();
            List<Temperature> temperatures=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM temperature ORDER BY time DESC LIMIT "+count+";"))
                {
                    while(resultSet.next())
                        readResultSetTemperatureRow(resultSet,temperatures);
                }
            }
            Collections.reverse(temperatures);
//            Logger.LOGGER.debug(temperatures.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return temperatures;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    public static List<Temperature> getLastTemperaturesWhile(long time)
    {
        long now=System.currentTimeMillis();
        long time1=now-time;
        long time2=now+Duration.of(5).second();
        return getTemperaturesBetween(time1,time2);
    }

    public static List<Temperature> getTemperaturesBetween(long time1,long time2)
    {
        List<Temperature> list=TEMPERATURE_CACHE.getAllBetween(time1,time2);
        if(list.isEmpty())
        {
            List<Temperature> temperatures=getTemperaturesImpl(time1,time2);
            TEMPERATURE_CACHE.addAll(temperatures);
            list.addAll(temperatures);
        }
        else
        {
            if(list.get(0).getTime()-time1>Duration.of(7).secondPlus(500).millisecond())
            {
                List<Temperature> temperatures=getTemperaturesImpl(time1,list.get(0).getTime());
                TEMPERATURE_CACHE.addAll(temperatures);
                temperatures.stream()
                        .filter(temperature->!list.contains(temperature))
                        .forEach(temperature->list.add(0,temperature));
                list.sort(null);
            }
            if(time2-list.get(list.size()-1).getTime()>Duration.of(7).secondPlus(500).millisecond())
            {
                List<Temperature> temperatures=getTemperaturesImpl(list.get(list.size()-1).getTime(),time2);
                TEMPERATURE_CACHE.addAll(temperatures);
                temperatures.stream()
                        .filter(temperature->!list.contains(temperature))
                        .forEach(list::add);
                list.sort(null);//par précaution, la liste est en théorie déjà triée à ce stade
            }
        }
        List<Temperature> list2=new ArrayList<>(list);//on fait une copie, et on va faire évoluer la copie uniquement
        boolean modified=false;
        for(int i=1;i<list.size();i++)
        {
            long previousTime=list.get(i-1).getTime();
            long currentTime=list.get(i).getTime();
            if(currentTime-previousTime>Duration.of(7).secondPlus(500).millisecond())
            {
                List<Temperature> temperatures=getTemperaturesImpl(previousTime,currentTime);
                TEMPERATURE_CACHE.addAll(temperatures);
                temperatures.stream()
                        .filter(temperature->!list2.contains(temperature))
                        .forEach(list2::add);
                modified=true;
            }
        }
        if(modified)
            list2.sort(null);
        return list2;
    }

    private static List<Temperature> getTemperaturesImpl(long time1,long time2)
    {
        try
        {
            Connection connection=getToilettesConnection();
            long startTime=System.currentTimeMillis();
            List<Temperature> temperatures=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM temperature WHERE time>="+time1+" AND time<"+time2+" ORDER BY time;"))
                {
                    while(resultSet.next())
                        readResultSetTemperatureRow(resultSet,temperatures);
                }
            }
//            Logger.LOGGER.debug(temperatures.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return temperatures;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    private static void readResultSetTemperatureRow(ResultSet resultSet,List<Temperature> temperatures) throws SQLException
    {
        long time=resultSet.getLong("time");
        double temperature=resultSet.getDouble("temperature");
        double setPoint=resultSet.getDouble("setPoint");
        double ratio=resultSet.getDouble("ratio");
        boolean heaterOn=resultSet.getInt("heaterOn")==1;
        temperatures.add(new Temperature(time,temperature,setPoint,ratio,heaterOn));
    }

    private static Connection getEcocompteurConnection() throws NotAvailableDatabaseException
    {
        synchronized(LOCK_OBJECT)
        {
            if(ecocompteurConnection==null)
                ecocompteurConnection=getConnectionImpl("database1.ini");
            else
            {
                boolean valid;
                try
                {
                    valid=ecocompteurConnection.isValid(0);
                }
                catch(SQLException e)//n'arrive jamais tant que l'argument de isValid est positif ou nul
                {
                    valid=false;
                }
                if(!valid)
                {
                    try
                    {
                        ecocompteurConnection.close();
                    }
                    catch(SQLException e)
                    {
                    }
                    ecocompteurConnection=null;
                    ecocompteurConnection=getConnectionImpl("database1.ini");
                }
            }
            return ecocompteurConnection;
        }
    }

    private static Connection getToilettesConnection() throws NotAvailableDatabaseException
    {
        synchronized(LOCK_OBJECT)
        {
            if(toilettesConnection==null)
                toilettesConnection=getConnectionImpl("database2.ini");
            else
            {
                boolean valid;
                try
                {
                    valid=toilettesConnection.isValid(0);
                }
                catch(SQLException e)//n'arrive jamais tant que l'argument de isValid est positif ou nul
                {
                    valid=false;
                }
                if(!valid)
                {
                    try
                    {
                        toilettesConnection.close();
                    }
                    catch(SQLException e)
                    {
                    }
                    toilettesConnection=null;
                    toilettesConnection=getConnectionImpl("database2.ini");
                }
            }
            return toilettesConnection;
        }
    }

    private static Connection getConnectionImpl(String iniFileName) throws NotAvailableDatabaseException
    {
        String serverAddress="";
        String serverPort="";
        String databaseName="";
        String user="";
        String password="";
        try(BufferedReader reader=new BufferedReader(new FileReader(new File(iniFileName))))
        {
            serverAddress=reader.readLine();
            serverPort=reader.readLine();
            databaseName=reader.readLine();
            user=reader.readLine();
            password=reader.readLine();
        }
        catch(IOException e)
        {
            Logger.LOGGER.error("Unable to read database connection infos from the config file ("+e.toString()+")");
            e.printStackTrace();
        }
        Connection connection=null;
        try
        {
            connection=DriverManager.getConnection("jdbc:mariadb://"+serverAddress+":"+serverPort+"/"+databaseName+"?user="+user+"&password="+password);
//            Logger.LOGGER.info("Connection to SQLite has been established.");
        }
        catch(SQLException e)
        {
            try
            {
                if(connection!=null)
                    connection.close();
            }
            catch(SQLException ex)
            {
            }
            Logger.LOGGER.error(e.toString());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnections()
    {
        synchronized(LOCK_OBJECT)
        {
            if(ecocompteurConnection!=null)
                try
                {
                    ecocompteurConnection.close();
                }
                catch(SQLException e)
                {
                }
            if(toilettesConnection!=null)
                try
                {
                    toilettesConnection.close();
                }
                catch(SQLException e)
                {
                }
        }
    }
}
