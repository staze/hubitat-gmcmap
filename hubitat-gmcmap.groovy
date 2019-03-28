/*
 * gmcmap.com Geiger Counter Query
 */
metadata {
    definition(name: "gmcmap.com Geiger Counter", namespace: "staze", author: "Ryan Stasel", importUrl: "https://raw.githubusercontent.com/hubitat/HubitatPublic/master/examples/drivers/httpGetSwitch.groovy") {
        capability "Sensor"
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

def getParams = [
    uri: "http://www.gmcmap.com/historyData-plain.asp?Param_ID={GeigerID}&timezone={Timezone}",
        	contentType: "application/json",
]

def poll() {
	try {
		httpget(getParams) { resp -> log.debug resp.json }
	} catch(Exception e) {
		log.debug "error occured calling httpPost ${e}"
	}
}
