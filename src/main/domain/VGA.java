package main.domain;

public class VGA extends Part
{
    public final String core_num;
    public final String memory_size;
    public final String memory_type;
    public final String power_consumption;
    public final String slot_size;

    public VGA(String name,
               String manufacturer,
               String series,
               String launch_date,
               String msrp,
               String monetary_unit,
               String core_num,
               String memory_size,
               String memory_type,
               String power_consumption,
               String slot_size)
    {
        super(name, manufacturer, series, launch_date, msrp, monetary_unit);

        this.core_num = core_num;
        this.memory_size = memory_size;
        this.memory_type = memory_type;
        this.power_consumption = power_consumption;
        this.slot_size = slot_size;
    }
}
