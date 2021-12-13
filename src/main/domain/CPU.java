package main.domain;

public class CPU extends Part
{
    public final String core_num;
    public final String frequency;
    public final String power_consumption;
    public final String integrated_graphics;

    public CPU(String name,
               String manufacturer,
               String series,
               String launch_date,
               String msrp,
               String monetary_unit,
               String core_num,
               String frequency,
               String power_consumption,
               String integrated_graphics)
    {
        super(name, manufacturer, series, launch_date, msrp, monetary_unit);

        this.core_num = core_num;
        this.frequency = frequency;
        this.power_consumption = power_consumption;
        this.integrated_graphics = integrated_graphics;
    }
}
