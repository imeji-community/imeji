<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
xmlns:generic-metadata="http://purl.org/escidoc/schemas/generic-metadata/metadata/0.1" xmlns:generic-metadata-records="http://purl.org/escidoc/schemas/generic-metadata/records/0.1">
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:template match="escidocMetadataRecords:md-record">
		<xsl:element name="generic-metadata-records:md-records">
			<xsl:element name="generic-metadata-records:md-record">
				<xsl:attribute name="name">escidoc</xsl:attribute>
				<xsl:for-each select="*">
					<!-- The very first element defines the type of the description -->
					<xsl:element name="generic-metadata:description">
						<!-- The resource class attribute is the type of the description -->
						<xsl:attribute name="resource-class">
							<xsl:value-of select="local-name()"/>	
						</xsl:attribute>
						<!-- The screen-id is after the transformation set to default-->
						<xsl:attribute name="screen-id">default</xsl:attribute>
						<!-- The configuration is either formatted or default. By definition, after the transformation it's formatted-->
						<xsl:attribute name="configuration">formatted</xsl:attribute>
						<xsl:call-template name="statement"></xsl:call-template>
					</xsl:element>
				</xsl:for-each>		
		
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="statement">
		<xsl:for-each select="*">
			<xsl:element name="generic-metadata:statement">
				<!-- The id of the statement corresponds to the name of the original element -->
				<xsl:attribute name="id">
					<xsl:value-of select="local-name()"/>
				</xsl:attribute>
				<!-- The label of the statement is by default equal to the id -->
				<xsl:attribute name="label">
					<xsl:value-of select="local-name()"/>
				</xsl:attribute>
				<!-- If the original element has a Namespace, the ns is keeped as an attribute -->
				<xsl:if test="namespace-uri(.) != ''">
					<xsl:attribute name="namespace">
						<xsl:value-of select="namespace-uri(.)"/>
					</xsl:attribute>
				</xsl:if>
				<!-- If the element has no child element -->
				<xsl:if test="not(*!='')">
					<xsl:element name="generic-metadata:value">
						<xsl:attribute name="xml:lang">en-US</xsl:attribute>
						<xsl:value-of select="current()"/>
					</xsl:element>
				</xsl:if>
				<!-- If statement has a child, do again-->
				<xsl:if test="local-name() != ''">
					<xsl:call-template name="statement"/>
				</xsl:if>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
