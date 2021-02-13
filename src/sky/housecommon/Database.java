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
    private Database()
    {
    }

    public static boolean insertInstantaneousConsumption(InstantaneousConsumption instantaneousConsumption) throws NotAvailableDatabaseException
    {
        boolean success=false;
        try(Connection connection=Database.getEcocompteurConnection())
        {
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

    public static InstantaneousConsumption getLastInstantaneousConsumption()
    {
        List<InstantaneousConsumption> instantaneousConsumptions=getLastInstantaneousConsumptions(1);
        if(instantaneousConsumptions.isEmpty())
            return null;
        else
            return instantaneousConsumptions.get(0);
    }

    public static List<InstantaneousConsumption> getLastInstantaneousConsumptions(int count)
    {
        try(Connection connection=Database.getEcocompteurConnection())
        {
            long startTime=System.currentTimeMillis();
            List<InstantaneousConsumption> instantaneousConsumptions=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM instantaneous_consumption ORDER BY time DESC LIMIT "+count+";"))
                {
                    while(resultSet.next())
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
                }
            }
            Collections.reverse(instantaneousConsumptions);
            Logger.LOGGER.info(instantaneousConsumptions.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return instantaneousConsumptions;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptions(int year)
    {
        return getInstantaneousConsumptions(1,1,year,31,12,year);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptions(int month,int year)
    {
        return getInstantaneousConsumptions(1,month,year,new GregorianCalendar(year,month-1,1).getActualMaximum(Calendar.DAY_OF_MONTH),month,year);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptions(int day,int month,int year)
    {
        return getInstantaneousConsumptions(day,month,year,day,month,year);
    }

    public static List<InstantaneousConsumption> getInstantaneousConsumptions(int day1,int month1,int year1,int day2,int month2,int year2)
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
        try(Connection connection=Database.getEcocompteurConnection())
        {
            long startTime=System.currentTimeMillis();
            List<InstantaneousConsumption> instantaneousConsumptions=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM instantaneous_consumption WHERE time>="+startCalendar.getTimeInMillis()+" AND time<"+endCalendar.getTimeInMillis()+" ORDER BY time;"))
                {
                    while(resultSet.next())
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
            Logger.LOGGER.info(instantaneousConsumptions.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return instantaneousConsumptions;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    public static List<WasherInstantaneousConsumption> getWasherInstantaneousConsumptions(long startTimeRequested,long stopTimeRequested)
    {
        try(Connection connection=Database.getEcocompteurConnection())
        {
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
            Logger.LOGGER.info(washerInstantaneousConsumptions.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
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
        try(Connection connection=Database.getToilettesConnection())
        {
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

    public static Temperature getLastTemperature()
    {
        List<Temperature> temperatures=getLastTemperatures(1);
        if(temperatures.isEmpty())
            return null;
        else
            return temperatures.get(0);
    }

    public static List<Temperature> getLastTemperatures(int count)
    {
        try(Connection connection=Database.getToilettesConnection())
        {
            long startTime=System.currentTimeMillis();
            List<Temperature> temperatures=new ArrayList<>();
            try(Statement statement=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY))
            {
                try(ResultSet resultSet=statement.executeQuery("SELECT * FROM temperature ORDER BY time DESC LIMIT "+count+";"))
                {
                    while(resultSet.next())
                    {
                        long time=resultSet.getLong("time");
                        double temperature=resultSet.getDouble("temperature");
                        double setPoint=resultSet.getDouble("setPoint");
                        double ratio=resultSet.getDouble("ratio");
                        boolean heaterOn=resultSet.getInt("heaterOn")==1;
                        temperatures.add(new Temperature(time,temperature,setPoint,ratio,heaterOn));
                    }
                }
            }
            Collections.reverse(temperatures);
            Logger.LOGGER.info(temperatures.size()+" rows fetched in "+(System.currentTimeMillis()-startTime)+" ms");
            return temperatures;
        }
        catch(NotAvailableDatabaseException|SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    private static Connection getEcocompteurConnection() throws NotAvailableDatabaseException
    {
        return getConnectionImpl("database1.ini");
    }

    private static Connection getToilettesConnection() throws NotAvailableDatabaseException
    {
        return getConnectionImpl("database2.ini");
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
}
