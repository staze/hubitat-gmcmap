/*
 * gmcmap.com Geiger Counter Query
 */
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

metadata {
    definition(name: "gmcmap.com Geiger Counter", namespace: "staze", author: "Ryan Stasel", importUrl: "https://raw.githubusercontent.com/staze/hubitat-gmcmap/master/hubitat-gmcmap.groovy") {
        capability "Sensor"
	capability "Refresh"
    }
}

preferences {
    section("URIs") {
        input "GeigerID", "text", title: "Geiger ID", required: true
        input "Timezone", "text", title: "Timezone", required: false
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
}

def logsOff() {
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

def updated() {
    log.info "updated..."
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

def getParams() { "http://www.gmcmap.com/historyData-plain.asp?Param_ID=${GeigerID}&timezone=${Timezone}" }

def refresh() {
def responseBody

try {
	if (logEnable) log.debug "Params:  ${getParams()}"
	httpGet(getParams()) { resp -> 
		if (logEnable) log.debug resp.getData()
		responseBody = resp.getData()
		state.geiger = jsonSlurper.parseText(resp.data.toString())
	}
		//responseBody = resp.getData()}
} catch(Exception e) {
	log.debug "error occured calling httpget ${e}"
}
if (logEnable) log.info responseBody
}
