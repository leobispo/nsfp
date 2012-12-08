<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <html>
      <body>
        <h1>The Exomizer: Annotate and Filter Variants</h1>
        <p>This is a java jdbc program that uses data from <a href="http://www.ncbi.nlm.nih.gov/pubmed/21520341">dbNSFP</a>
        and other sources and filters to visualize and annotate VCF data. Apply flags from the command line or 
        Galaxy to control behaviour: path (pathogenicity filter), AR, AR, or X for inheritance filters, 
        TG=0.05 (or similar) for Thousand Genomes frequency filter, 
        and Q=30 for minimum genotype quality of 30 (or similar).</p>
        <p>Brought to you by AG Robinson, be sure to visit our <a href="http://compbio.charite.de">homepage</a>.</p>
        <p>Filtering methods used:</p>
        <ul>
          <xsl:if test="/NSFPResult/snvFilters"> 
            <xsl:for-each select="NSFPResult/snvFilters/*">
              <li><xsl:value-of select="filterName"/>: <xsl:value-of select="description"/></li>
            </xsl:for-each>
          </xsl:if>
          <xsl:if test="/NSFPResult/nsfpFilters"> 
            <xsl:for-each select="NSFPResult/nsfpFilters/*">
              <li><xsl:value-of select="filterName"/>: <xsl:value-of select="description"/></li>
            </xsl:for-each>
          </xsl:if>
          <xsl:if test="/NSFPResult/inheritanceFilters"> 
            <xsl:for-each select="NSFPResult/inheritanceFilters/*">
              <li><xsl:value-of select="filterName"/>: <xsl:value-of select="description"/></li>
            </xsl:for-each>
          </xsl:if>
        </ul>
        <p>VCF File: <xsl:value-of select="NSFPResult/vcfFile"/>.</p>
        <xsl:choose>
          <xsl:when test="count(/NSFPResult/inheritanceFilters/*)  &gt; 1">
            <p>Warning: Multiple inheritance filters were set. Not a valid analysis!</p>
          </xsl:when>
        </xsl:choose>
        <xsl:if test="/NSFPResult/samples">
          <p>Samples:</p>
          <ul> 
          <xsl:for-each select="NSFPResult/samples/*">
              <li><xsl:value-of select="."/></li>
          </xsl:for-each>
          </ul>
        </xsl:if>
        <ul>
          <xsl:if test="/NSFPResult/snvFilters">
            <li>Total SNVs before filtering <xsl:value-of select="NSFPResult/snvFilters/Filter[1]/elementsProcessed"/></li>
            <xsl:for-each select="NSFPResult/snvFilters/*">
              <li>Total SNVs after <b><xsl:value-of select="filterName"/></b> filtering <xsl:value-of select="elementsDiff"/></li>
            </xsl:for-each>
          </xsl:if>
          <xsl:if test="/NSFPResult/nsfpFilters">
            <xsl:for-each select="NSFPResult/nsfpFilters/*">
              <li>Total SNVs after <b><xsl:value-of select="filterName"/></b> filtering <xsl:value-of select="elementsDiff"/></li>
            </xsl:for-each>
          </xsl:if>
        </ul>
        <table border="1" cellpaddding="1" cellspacing="0">
          <tbody align="center" style="font-family:verdana;color:black;background-color:#00FFFF">
            <tr>
              <xsl:for-each select="NSFPResult/NSFPs/NSFP[1]/*">
                <th><xsl:value-of select="name(.)"/></th>
              </xsl:for-each>
            </tr>
            <xsl:for-each select="NSFPResult/NSFPs/NSFP">
            <tr>
              <xsl:for-each select="*">
                <th><xsl:value-of select="."/></th>
              </xsl:for-each>
            </tr>
          </xsl:for-each>
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>

