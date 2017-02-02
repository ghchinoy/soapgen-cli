package com.chinoy.soapgen

/**
 * Created by hussain.chinoy on 2/1/17.
 */
class QuestionmarkSubstitutor {
    String ReplaceWithNameSlug(String xml) {
        def slurper = new XmlSlurper().parseText(xml)
        println slurper.findAll { it.text() == "?" }
            .each {
                println it.node().name
            }

        return xml;
    }
}
