package com.chinoy.soapgen

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

class QuestionmarkSubstitutor {
    String ReplaceWithNameSlug(String xml, boolean debug) {
        def slurper = new XmlSlurper().parseText(xml)
        def counter = 0
        slurper.depthFirst().findAll { it.text() == '?' }
            .each {
                el ->
                    counter++
                    if (debug) {
                        println "$counter ${el.name()}"
                    }
                    // ftl format
                    el.replaceBody "\${${el.name()}}"
            }
        return toPrettyXml(slurper);
    }

    def static toPrettyXml(xml) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml })
    }
}
