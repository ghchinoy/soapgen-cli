# Development Notes

Random notes as I'm creating this tool.


## Thoughts / References

* Use [SoapUI](https://www.soapui.org/developers-corner/contribute-to-soapui.html) - http://stackoverflow.com/questions/20486743/java-get-sample-request-xml-from-wsdl ?
* Is the SoapUI license too restrictive? Note, reficio/soap-ws uses some of this, as well.
* [SoapUI API Docs](https://www.soapui.org/apidocs/overview-summary.html)

## Process Notes

Creation of a pom.xml including dependencies for
[refico/soap-ws](https://github.com/reficio/soap-ws) and commons-cli.

App, added log4j support, as reficio/soap-ws required it, with a properties file in `src/main/resources`.

Using Java7 Files and Paths :)

Added Groovy to substitute `?` with element name FreeMarker Template slug; had to modify pom to include Groovy and structure for groovy file location

## Examples WSDLs for testing

* Account Manager http://demo.se.akana-dev.net:8080/sample_axis/services/AccountManagerDocLiteralWrapped?wsdl
* Weather.gov http://graphical.weather.gov/xml/DWMLgen/wsdl/ndfdXML.wsdl
* Currency converter http://www.webservicex.net/CurrencyConvertor.asmx?WSDL