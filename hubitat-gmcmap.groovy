/*
 * gmcmap.com Geiger Counter Query
 */
import groovy.json.JsonSlurper

metadata {
    definition(name: "gmcmap.com Geiger Counter", namespace: "staze", author: "Ryan Stasel", importUrl: "https://raw.githubusercontent.com/staze/hubitat-gmcmap/master/hubitat-gmcmap.groovy") {
        capability "Sensor"
        capability "Polling"
        attribute "CPM", "NUMBER"
        attribute "ACPM", "NUMBER"
        attribute "uSv", "NUMBER"
    }
}

preferences {
    section("URIs") {
        input "GeigerID", "text", title: "Geiger ID", required: true
        input "Timezone", "text", title: "Timezone", required: false
        input name: 'updateMins', type: 'enum', description: "Select the update frequency", title: "Update frequency (minutes)\n0 is disabled", defaultValue: '5', options: ['0', '1', '2', '5','10','15','30'], required: true
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
}

def logsOff() {
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

def updated() {
    unschedule()
    log.info "gmcmap updated..."
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
    if(updateMins != "0") {
        schedule("0 */${updateMins} * ? * *", poll)
    }
    
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

def poll() {
    if (logEnable) log.debug "gmcmap polling..."
    def url = "http://www.gmcmap.com/historyData-plain.asp?Param_ID=${GeigerID}&timezone=${Timezone}"
    try {
        httpGet(url) { resp -> 
            if (logEnable) log.debug resp.getData()
            def respValues = new JsonSlurper().parseText(resp.data.toString().trim())
            sendEvent(name: "CPM", value: respValues.CPM)
            sendEvent(name: "ACPM", value: respValues.ACPM)
            sendEvent(name: "uSv", value: respValues.uSv)
	    log.info "respValues.CPM CPM, respValues.ACPM ACPM, respValues.uSv uSv"
	}
    } catch(Exception e) {
    	log.debug "error occured calling httpget ${e}"
    }
}
