<!-- This file is used to transform an existing jboss properties-service-xml to include sprecial pubman properties -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="yes" standalone="yes" indent="yes" />
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="server">
		<xsl:copy>
			<attribute name="Properties">
				${jboss.property.1}
				${jboss.property.2}
			</attribute>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="attribute" />
</xsl:stylesheet>
