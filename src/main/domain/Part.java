package main.domain;

public class Part
{
    private static Integer currentId = 0;

    public String id;
    public String name;

    public String manufacturer;
    public String series;
    public String launch_date;
    public String msrp;
    public String monetary_unit;

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



}
