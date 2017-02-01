# soapgen

A tool to produce XML Request messages from a given WSDL.


```
usage: soapgen [OPTIONS] <WSDL>
 -b,--binding <BINDING>   explicit choice of binding localpart name to use
 -dir,--outdir <OUTDIR>   directory for output
 -e,--examples            Generate example content
 -h,--headers             Add headers
 -help                    print this message
 -o,--optionals           Generate optional elements
 -s,--skipcomments        Skip comments
```


Using [reficio/soap-ws](https://github.com/reficio/soap-ws) and [Apache Commons CLI 1.3.1](https://mvnrepository.com/artifact/commons-cli/commons-cli/1.3.1)

### Build

To build an executable jar, use maven:

	mvn clean compile assembly:single

The output will show up in the `target` directory.

### Example run

After building, this will output request XMLs, with example/dummy values, to the dir `test`

    java -jar target/soapgen-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar http://graphical.weather.gov/xml/DWMLgen/wsdl/ndfdXML.wsdl -dir test -e