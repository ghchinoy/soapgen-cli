package com.chinoy.soapgen;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.reficio.ws.SoapContext;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class App
{
    public static void main(String[] args) {

        BasicConfigurator.configure();
        final Options options = new Options();

        Option optionHelp = new Option(null, "help", false,"prints this message");

        Option optionDebug = Option.builder(null)
                .longOpt( "debug")
                .desc( "Shows debug information"  )
                .hasArg(false)
                .argName( "DEBUG" )
                .build();

        // SOAP headers, -h, --headers
        Option optionIncludeHeaders = Option.builder("h")
                .longOpt("headers")
                .desc("Add headers")
                .hasArg(false)
                .argName("HEADERS")
                .build();

        // Skip comments, -s, --skipcomments
        Option optionSkipComments = Option.builder("s")
                .longOpt("skipcomments")
                .desc("Skip comments")
                .hasArg(false)
                .argName("SKIPCOMMENTS")
                .build();

        Option optionGenerateExamples = Option.builder("e")
                .longOpt("examples")
                .desc("Generate example content")
                .hasArg(false)
                .argName("EXAMPLES")
                .build();

        Option optionOptionals = Option.builder("o")
                .longOpt("optionals")
                .desc("Generate optional elements")
                .hasArg(false)
                .argName("OPTIONALS")
                .build();

        Option optionOutDir = Option.builder("d")
                .longOpt("outdir")
                .desc("directory for output")
                .hasArg()
                .argName("OUTDIR")
                .build();

        Option optionBindingChoice = Option.builder("b")
                .longOpt("binding")
                .desc("explicit choice of binding localpart name to use")
                .hasArg()
                .argName("BINDING")
                .build();

        options.addOption(optionBindingChoice);
        options.addOption(optionOutDir);
        options.addOption(optionOptionals);
        options.addOption(optionGenerateExamples);
        options.addOption(optionSkipComments);
        options.addOption(optionIncludeHeaders);
        options.addOption(optionDebug);
        options.addOption(optionHelp);

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            // Check for help flag
            if (cmd.hasOption("help")) {
                showHelp(options);
                return;
            }

            boolean debug, headers, skipcomments, examples, optionals;
            debug = headers = skipcomments = examples = optionals = false;
            String outputdir = ".";
            // debug
            if (cmd.hasOption("debug")) {
                debug = true;
            }
            if (cmd.hasOption("h")) {
                headers = true;
            }
            if (cmd.hasOption("s")) {
                skipcomments = true;
            }
            if (cmd.hasOption("e")) {
                examples = true;
            }
            if (cmd.hasOption("o")) {
                optionals = true;
            }
            if (cmd.hasOption("d")) {
                outputdir = cmd.getOptionValue("d");
            }

            String[] remainingArguments = cmd.getArgs();
            if (remainingArguments.length < 1) {
                showHelp(options);
                return;
            }
            String wsdlUrl = remainingArguments[0];

            Wsdl wsdl = Wsdl.parse(wsdlUrl);  // (1)

            List<QName> bindings = wsdl.getBindings();
            System.out.println("Bindings available:");
            wsdl.printBindings(); // (4)

            // pick a binding and create a builder
            String chosenBinding = "";
            if (bindings.size() >= 1) {
                if (cmd.hasOption("b")) {
                    chosenBinding = cmd.getOptionValue("b");
                } else { // get the first binding
                    chosenBinding = bindings.get(0).getLocalPart();
                }
            } else {
                System.out.println("No bindings to choose from!");
                System.exit(1);
            }
            SoapBuilder builder = wsdl.binding().localPart(chosenBinding).find(); // (3)
            System.out.format("Using Binding: %s\n", chosenBinding);

            SoapContext context = SoapContext.builder()
                    .alwaysBuildHeaders(headers)
                    .buildOptional(optionals)
                    .exampleContent(examples)
                    .build();

            QuestionmarkSubstitutor sub = new QuestionmarkSubstitutor();

            List<SoapOperation> operations = builder.getOperations(); // (5)
            System.out.println("Operations available:");
            for (int i = 0; i < operations.size(); i++) {
                String name = operations.get(i).getOperationName();
                SoapOperation operation = builder.operation().name(name).find();

                String requestxml = builder.buildInputMessage(operation, context);

                if (!cmd.hasOption("e")) { // no example content
                    requestxml = sub.ReplaceWithNameSlug(requestxml, debug);
                }

                // Request
                Path outdirpath = Paths.get(outputdir).toAbsolutePath();
                Files.createDirectories(outdirpath);

                // Requests
                String request = name + "_Request.xml";
                System.out.println("Writing " + request + " ...");
                Path requestout = outdirpath.resolve(request);
                Files.write(requestout, requestxml.getBytes());

                // Responses
                /*
                String response = name + "_Response.xml";
                System.out.println("Writing " + response + " ...");
                Path responseout = outdirpath.resolve(response);
                Files.write(responseout, builder.buildOutputMessage(operation, context).getBytes());
                */

                //PrintWriter writer = new PrintWriter(filename, "UTF-8");
                //writer.println(builder.buildInputMessage(operation, context));
                //writer.close();
            }

        } catch (ParseException e) {
            showHelp(options);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't write file");
            System.err.println(e.toString());
            System.exit(1);
        } catch (org.reficio.ws.SoapBuilderException e) {
            // unable to create xml
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    public static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("soapgen [OPTIONS] <WSDL>", options);
    }
}