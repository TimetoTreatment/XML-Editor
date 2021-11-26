<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="uri:xsl">
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE>Graphics List</TITLE>
      </HEAD>
      <BODY>
        <H1 style="text-align:center">List of Parts</H1>
        <br/>
        <!--=================-->
        <!--== VGA: NVIDIA ==-->
        <!--=================-->
        <div>
          <TABLE border="1" style="empty-cells:hide">
            <CAPTION style="text-align: center; color: green; font-size:26pt">GPU: NVIDIA</CAPTION>
            <TR>
              <TH></TH>
              <TH colspan="4" style="font-size: 16pt">General Infomation</TH>
              <TH colspan="4" style="font-size: 16pt">Specs</TH>
            </TR>
            <TR>
              <TH style="width: 1em">#</TH>
              <TH>Series</TH>
              <TH>Name</TH>
              <TH>Launch Date</TH>
              <TH>msrp</TH>
              <TH>Cores</TH>
              <TH>Memory</TH>
              <TH>Power Consumption</TH>
            </TR>
            <xsl:for-each select="Parts/VGA:VGA" order-by="-number(VGA:specs/VGA:core_num)">
              <xsl:if test="part:manufacturer[.$eq$'NVIDIA']">
                <TR>
                  <!--GENERAL INFORMATION-->
                  <!--Price Rank-->
                  <TD>
                    <xsl:eval>fRank()</xsl:eval>
                  </TD>
                  <!--Series-->
                  <TD>
                    <xsl:value-of select="part:series" />
                  </TD>
                  <!--Name-->
                  <TD>
                    <xsl:value-of select="part:name" />
                  </TD>
                  <!--Launch Date-->
                  <TD>
                    <xsl:value-of select="part:launch_date" />
                  </TD>
                  <!--part:msrp (Manufacturer's Suggested Retail Price)-->
                  <xsl:choose>
                    <xsl:when test="part:msrp[(. $lt$ 2000 $and$ . $ge$ 750) $or$ (. $lt$ 2000000 $and$ . $ge$ 750000)]">
                      <TD style="color: red">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 750 $and$ . $ge$ 500) $or$ (. $lt$ 750000 $and$ . $ge$ 500000)]">
                      <TD style="color: blue">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 500 $and$ . $ge$ 250) $or$ (. $lt$ 500000 $and$ . $ge$ 250000)]">
                      <TD style="color: green">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 250 $and$ . $ge$ 10) $or$ (. $lt$ 250000 $and$ . $ge$ 10000)]">
                      <TD style="color: brown">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">\</xsl:if>
                      </TD>
                    </xsl:when>
                  </xsl:choose>
                  <!--SPEC INFORMATION-->
                  <!--Cores-->
                  <TD>
                    <xsl:value-of select="VGA:specs/VGA:core_num" />
                  </TD>
                  <!--Memory-->
                  <TD>
                    <xsl:value-of select="VGA:specs/VGA:memory_size" /> GB
                  </TD>
                  <!--Power Consumption-->
                  <TD>
                    <xsl:value-of select="VGA:specs/VGA:power_consumption" /> Watt
                  </TD>
                </TR>
              </xsl:if>
            </xsl:for-each>
          </TABLE>
          <!--Image: NVIDIA-->
          <div style="display:inline-block; margin:15px 0px 30px 0px; padding:2px; border:2px solid black">
            <a target="_blank" href="https://www.nvidia.com/en-us/geforce/graphics-cards/30-series/compare/?section=compare-specs">
              <img src="resource/tableNVIDIA.png" width="800"></img>
            </a>
          </div>
        </div>
        <xsl:eval>fRankReset()</xsl:eval>
        <!--==============-->
        <!--== VGA: AMD ==-->
        <!--==============-->
        <div>
          <TABLE border="1" style="empty-cells:hide">
            <CAPTION style="text-align: center; color: red; font-size:26pt">GPU: AMD</CAPTION>
            <TR>
              <TH></TH>
              <TH colspan="4" style="font-size: 16pt">General Infomation</TH>
              <TH colspan="4" style="font-size: 16pt">Specs</TH>
            </TR>
            <TR>
              <TH style="width: 1em">#</TH>
              <TH>Series</TH>
              <TH>Name</TH>
              <TH>Launch Date</TH>
              <TH>msrp</TH>
              <TH>Cores</TH>
              <TH>Memory</TH>
              <TH>Power Consumption</TH>
            </TR>
            <xsl:for-each select="Parts/VGA:VGA" order-by="-number(VGA:specs/VGA:core_num)">
              <xsl:if test="part:manufacturer[.$eq$'AMD']">
                <TR>
                  <!--GENERAL INFORMATION-->
                  <!--Price Rank-->
                  <TD>
                    <xsl:eval>fRank()</xsl:eval>
                  </TD>
                  <!--Series-->
                  <TD>
                    <xsl:value-of select="part:series" />
                  </TD>
                  <!--Name-->
                  <TD>
                    <xsl:value-of select="part:name" />
                  </TD>
                  <!--Launch Date-->
                  <TD>
                    <xsl:value-of select="part:launch_date" />
                  </TD>
                  <!--part:msrp (Manufacturer's Suggested Retail Price)-->
                  <xsl:choose>
                    <xsl:when test="part:msrp[(. $lt$ 2000 $and$ . $ge$ 750) $or$ (. $lt$ 2000000 $and$ . $ge$ 750000)]">
                      <TD style="color: red">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 750 $and$ . $ge$ 500) $or$ (. $lt$ 750000 $and$ . $ge$ 500000)]">
                      <TD style="color: blue">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 500 $and$ . $ge$ 250) $or$ (. $lt$ 500000 $and$ . $ge$ 250000)]">
                      <TD style="color: green">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 250 $and$ . $ge$ 10) $or$ (. $lt$ 250000 $and$ . $ge$ 10000)]">
                      <TD style="color: brown">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">\</xsl:if>
                      </TD>
                    </xsl:when>
                  </xsl:choose>
                  <!--SPEC INFORMATION-->
                  <!--Cores-->
                  <TD>
                    <xsl:value-of select="VGA:specs/VGA:core_num" />
                  </TD>
                  <!--Memory-->
                  <TD>
                    <xsl:value-of select="VGA:specs/VGA:memory_size" /> GB
                  </TD>
                  <!--Power Consumption-->
                  <TD>
                    <xsl:value-of select="VGA:specs/VGA:power_consumption" /> Watt
                  </TD>
                </TR>
              </xsl:if>
            </xsl:for-each>
          </TABLE>
          <!--Image: AMD-->
          <div style="display:inline-block; margin:15px 0px 30px 0px; padding:2px; border:2px solid black">
            <a target="_blank" href="https://www.amd.com/en/graphics/amd-radeon-rx-6000-series">
              <img src="resource/tableAMD.png" width="800" style="border:1px solid black"></img>
            </a>
          </div>
        </div>
        <xsl:eval>fRankReset()</xsl:eval>
        <!--================-->
        <!--== CPU: INTEL ==-->
        <!--================-->
        <div>
          <TABLE border="1" style="empty-cells:hide">
            <CAPTION style="text-align: center; color: blue; font-size:26pt">CPU: INTEL</CAPTION>
            <TR>
              <TH></TH>
              <TH colspan="4" style="font-size: 16pt">General Infomation</TH>
              <TH colspan="4" style="font-size: 16pt">Specs</TH>
            </TR>
            <TR>
              <TH style="width: 1em">#</TH>
              <TH>Series</TH>
              <TH>Name</TH>
              <TH>Launch Date</TH>
              <TH>msrp</TH>
              <TH>Cores</TH>
              <TH>Memory</TH>
              <TH>Power Consumption</TH>
            </TR>
            <xsl:for-each select="Parts/CPU:CPU" order-by="-number(part:msrp)">
              <xsl:if test="part:manufacturer[.$eq$'Intel']">
                <TR>
                  <!--GENERAL INFORMATION-->
                  <!--Price Rank-->
                  <TD>
                    <xsl:eval>fRank()</xsl:eval>
                  </TD>
                  <!--Series-->
                  <TD>
                    <xsl:value-of select="part:series" />
                  </TD>
                  <!--Name-->
                  <TD>
                    <xsl:value-of select="part:name" />
                  </TD>
                  <!--Launch Date-->
                  <TD>
                    <xsl:value-of select="part:launch_date" />
                  </TD>
                  <!--part:msrp (Manufacturer's Suggested Retail Price)-->
                  <xsl:choose>
                    <xsl:when test="part:msrp[(. $lt$ 2000 $and$ . $ge$ 750) $or$ (. $lt$ 2000000 $and$ . $ge$ 750000)]">
                      <TD style="color: red">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 750 $and$ . $ge$ 500) $or$ (. $lt$ 750000 $and$ . $ge$ 500000)]">
                      <TD style="color: blue">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 500 $and$ . $ge$ 250) $or$ (. $lt$ 500000 $and$ . $ge$ 250000)]">
                      <TD style="color: green">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 250 $and$ . $ge$ 10) $or$ (. $lt$ 250000 $and$ . $ge$ 10000)]">
                      <TD style="color: brown">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">\</xsl:if>
                      </TD>
                    </xsl:when>
                  </xsl:choose>
                  <!--SPEC INFORMATION-->
                  <!--Cores-->
                  <TD>
                    <xsl:value-of select="CPU:specs/CPU:core_num" />
                  </TD>
                  <!--Memory-->
                  <TD>
                    <xsl:value-of select="CPU:specs/CPU:frequency" /> GB
                  </TD>
                  <!--Power Consumption-->
                  <TD>
                    <xsl:value-of select="CPU:specs/CPU:power_consumption" /> Watt
                  </TD>
                </TR>
              </xsl:if>
            </xsl:for-each>
          </TABLE>
        </div>
        <div style="display:inline-block; margin:15px 0px 30px 0px;" />
        <xsl:eval>fRankReset()</xsl:eval>
        <!--==============-->
        <!--== CPU: AMD ==-->
        <!--==============-->
        <div>
          <TABLE border="1" style="empty-cells:hide">
            <CAPTION style="text-align: center; color: red; font-size:26pt">CPU: AMD</CAPTION>
            <TR>
              <TH></TH>
              <TH colspan="4" style="font-size: 16pt">General Infomation</TH>
              <TH colspan="4" style="font-size: 16pt">Specs</TH>
            </TR>
            <TR>
              <TH style="width: 1em">#</TH>
              <TH>Series</TH>
              <TH>Name</TH>
              <TH>Launch Date</TH>
              <TH>msrp</TH>
              <TH>Cores</TH>
              <TH>Memory</TH>
              <TH>Power Consumption</TH>
            </TR>
            <xsl:for-each select="Parts/CPU:CPU" order-by="-number(part:msrp)">
              <xsl:if test="part:manufacturer[.$eq$'AMD']">
                <TR>
                  <!--GENERAL INFORMATION-->
                  <!--Price Rank-->
                  <TD>
                    <xsl:eval>fRank()</xsl:eval>
                  </TD>
                  <!--Series-->
                  <TD>
                    <xsl:value-of select="part:series" />
                  </TD>
                  <!--Name-->
                  <TD>
                    <xsl:value-of select="part:name" />
                  </TD>
                  <!--Launch Date-->
                  <TD>
                    <xsl:value-of select="part:launch_date" />
                  </TD>
                  <!--part:msrp (Manufacturer's Suggested Retail Price)-->
                  <xsl:choose>
                    <xsl:when test="part:msrp[(. $lt$ 2000 $and$ . $ge$ 750) $or$ (. $lt$ 2000000 $and$ . $ge$ 750000)]">
                      <TD style="color: red">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 750 $and$ . $ge$ 500) $or$ (. $lt$ 750000 $and$ . $ge$ 500000)]">
                      <TD style="color: blue">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 500 $and$ . $ge$ 250) $or$ (. $lt$ 500000 $and$ . $ge$ 250000)]">
                      <TD style="color: green">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">KRW</xsl:if>
                      </TD>
                    </xsl:when>
                    <xsl:when test="part:msrp[(. $lt$ 250 $and$ . $ge$ 10) $or$ (. $lt$ 250000 $and$ . $ge$ 10000)]">
                      <TD style="color: brown">
                        <xsl:value-of select="part:msrp" />
                        <xsl:if test="part:msrp[./@unit $eq$ 'USD']">$</xsl:if>
                        <xsl:if test="part:msrp[./@unit $eq$ 'KRW']">\</xsl:if>
                      </TD>
                    </xsl:when>
                  </xsl:choose>
                  <!--SPEC INFORMATION-->
                  <!--Cores-->
                  <TD>
                    <xsl:value-of select="CPU:specs/CPU:core_num" />
                  </TD>
                  <!--Memory-->
                  <TD>
                    <xsl:value-of select="CPU:specs/CPU:frequency" /> GB
                  </TD>
                  <!--Power Consumption-->
                  <TD>
                    <xsl:value-of select="CPU:specs/CPU:power_consumption" /> Watt
                  </TD>
                </TR>
              </xsl:if>
            </xsl:for-each>
          </TABLE>
        </div>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:script>
    <![CDATA[
    var rank = 0;
    function fRank() {
      rank++;
      return rank;
    }
    function fRankReset() {
      rank = 0;
    }
    ]]>
  </xsl:script>
</xsl:stylesheet>
