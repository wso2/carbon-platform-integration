<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
  ~  Copyright (c) 2005-2008, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~  WSO2 Inc. licenses this file to you under the Apache License,
  ~  Version 2.0 (the "License"); you may not use this file except
  ~  in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing,
  ~  software distributed under the License is distributed on an
  ~  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~  KIND, either express or implied.  See the License for the
  ~  specific language governing permissions and limitations
  ~  under the License.
  ~
  -->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
        xmlns:ns="http://samples.esb.wso2.org"
        xmlns:ax21="http://samples.esb.wso2.org/xsd"
        exclude-result-prefixes="ns fn">
<xsl:param name="amount"/>
<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="//ns:getResponse" /> 
</xsl:template>
  
<xsl:template match="ns:getResponse" xmlns:ns="http://samples.esb.wso2.org">
<sam:credit xmlns:sam="http://samples.esb.wso2.org" xmlns:xsd="http://samples.esb.wso2.org/xsd">
    <sam:info>
        <xsd:amount><xsl:value-of select="$amount"/></xsd:amount>
        <xsd:personInfo>
            <xsd:address><xsl:value-of select="ns:return/ax21:address"/></xsd:address>
            <xsd:id><xsl:value-of select="ns:return/ax21:id"/></xsd:id>
            <xsd:name><xsl:value-of select="ns:return/ax21:name"/></xsd:name>
        </xsd:personInfo>
    </sam:info>
</sam:credit>
</xsl:template>
</xsl:stylesheet>
