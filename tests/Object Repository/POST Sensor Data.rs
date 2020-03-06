<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>POST Sensor Data</name>
   <tag></tag>
   <elementGuidId>f08cac7e-468f-4398-88ea-47fcdba5faea</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n\t\&quot;fields\&quot;: {\n\t\t\&quot;Humidade\&quot;: {\n\t\t\t\&quot;integerValue\&quot;: \&quot;55\&quot;\n\t\t},\n\t\t\&quot;Temperatura\&quot;: {\n\t\t\t\&quot;integerValue\&quot;: \&quot;20\&quot;\n\t\t},\n\t\t\&quot;Timestamp\&quot;: {\n\t\t\t\&quot;timestampValue\&quot;: \&quot;2019-12-10T00:30:00Z\&quot;\n\t\t}\n\t}\n}&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
   </httpHeaderProperties>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://firestore.googleapis.com/v1/projects/taes-sentinel/databases/(default)/documents/Escolas/ESTG/Edificios/A/Salas/A.S.1.2B/Historico?key=AIzaSyDRxvNjCqhhLiEFEITSerPtfIy9BIm1ov0</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceFunction></soapServiceFunction>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
