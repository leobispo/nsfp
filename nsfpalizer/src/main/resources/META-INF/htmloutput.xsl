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
        <xsl:if test="/NSFPResult/samples">
          <p>Samples:</p>
          <ul> 
          <xsl:for-each select="NSFPResult/samples/*">
              <li><xsl:value-of select="."/></li>
          </xsl:for-each>
          </ul>
        </xsl:if>
        <xsl:if test="/NSFPResult/snvFilters">"
          <ul> 
          <xsl:for-each select="NSFPResult/snvFilters/*">
              <li>Total SNVs before <b><xsl:value-of select="filterName"/></b> filtering <xsl:value-of select="elementsProcessed"/></li>
              <li>Total SNVs after <b><xsl:value-of select="filterName"/></b> filtering <xsl:value-of select="elementsFiltered"/></li>
          </xsl:for-each>
          </ul> 
        </xsl:if>
        <table>
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
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
