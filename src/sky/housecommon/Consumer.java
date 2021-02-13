package sky.housecommon;

import java.awt.Color;

public enum Consumer
{
    CONSUMER_1
    {
        public int getRank()
        {
            return 1;
        }

        public String getName()
        {
            return "Eclairages 1";//pas d'accent pour rester compatible avec toutes les polices
        }

        public String getShortName()
        {
            return "E1";
        }

        public Color getColor()
        {
            return new Color(128,0,0);//teinte 0, moins de luminance
        }
    },
    CONSUMER_2
    {
        public int getRank()
        {
            return 2;
        }

        public String getName()
        {
            return "Eclairages 2";//pas d'accent pour rester compatible avec toutes les polices
        }

        public String getShortName()
        {
            return "E2";
        }

        public Color getColor()
        {
            return new Color(255,0,0);//teinte 0
        }
    },
    CONSUMER_3
    {
        public int getRank()
        {
            return 3;
        }

        public String getName()
        {
            return "Prises 1";
        }

        public String getShortName()
        {
            return "P1";
        }

        public Color getColor()
        {
            return new Color(255,96,0);//teinte 15
        }
    },
    CONSUMER_4
    {
        public int getRank()
        {
            return 4;
        }

        public String getName()
        {
            return "Prises 2";
        }

        public String getShortName()
        {
            return "P2";
        }

        public Color getColor()
        {
            return new Color(255,191,0);//teinte 30
        }
    },
    CONSUMER_5
    {
        public int getRank()
        {
            return 5;
        }

        public String getName()
        {
            return "Ancien";
        }

        public String getShortName()
        {
            return "A";
        }

        public Color getColor()
        {
            return new Color(255,255,0);//teinte 40
        }
    },
    CONSUMER_6
    {
        public int getRank()
        {
            return 6;
        }

        public String getName()
        {
            return "Cuisson";
        }

        public String getShortName()
        {
            return "Cu";
        }

        public Color getColor()
        {
            return new Color(128,255,0);//teinte 60
        }
    },
    CONSUMER_7
    {
        public int getRank()
        {
            return 7;
        }

        public String getName()
        {
            return "Linge & vaisselle";
        }

        public String getShortName()
        {
            return "L&V";
        }

        public Color getColor()
        {
            return new Color(0,255,255);//teinte 120
        }
    },
    CONSUMER_8
    {
        public int getRank()
        {
            return 8;
        }

        public String getName()
        {
            return "Froid";
        }

        public String getShortName()
        {
            return "F";
        }

        public Color getColor()
        {
            return new Color(0,128,255);//teinte 140
        }
    },
    CONSUMER_9
    {
        public int getRank()
        {
            return 9;
        }

        public String getName()
        {
            return "Chauffage";
        }

        public String getShortName()
        {
            return "Ch";
        }

        public Color getColor()
        {
            return new Color(0,0,255);//teinte 160
        }
    },
    CONSUMER_10
    {
        public int getRank()
        {
            return 10;
        }

        public String getName()
        {
            return "Divers";
        }

        public String getShortName()
        {
            return "D";
        }

        public Color getColor()
        {
            return new Color(128,0,255);//teinte 180
        }
    },
    ;

    public abstract int getRank();

    public abstract String getName();

    public abstract String getShortName();

    public abstract Color getColor();

    public Color getForegroundColor()
    {
        float luminance=(float)(.2126d*(double)getColor().getRed()/255d+.7152d*(double)getColor().getGreen()/255d+.0722d*(double)getColor().getBlue()/255d);
        return luminance>.5f?Color.BLACK:Color.WHITE;
    }

    public static Consumer getConsumer(int rank)
    {
        return Consumer.values()[rank-1];
    }
}
