package main.domain;

public class Part
{
    private static Integer currentId = 0;

    public final String id;
    public final String name;

    public final String manufacturer;
    public final String series;
    public final String launch_date;
    public final String msrp;
    public final String monetary_unit;

    public Part(String name, String manufacturer, String series, String launch_date, String msrp, String monetary_unit)
    {
        this.name = name;
        this.manufacturer = manufacturer;
        this.series = series;
        this.launch_date = launch_date;
        this.msrp = msrp;
        this.monetary_unit = monetary_unit;
        this.id = "i" + currentId.toString();

        currentId++;
    }

    public static void resetCurrentId()
    {
        currentId = 0;
    }


}
