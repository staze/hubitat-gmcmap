/*
 * alphavantage ticker device
 */

metadata {
	definition(name: "alphavantage.stockticker", namespace: "alphavantage", author: "Leonard Sperry", importUrl: "https://raw.githubusercontent.com/leosperry/hubitat/main/alphavantage") 
    {
		capability "Sensor"
		capability "Polling"
		attribute "Symbol", "STRING"
		attribute "Open", "NUMBER"
		attribute "High", "NUMBER"
		attribute "Low", "NUMBER"
		attribute "Price", "NUMBER"
		attribute "Volume", "NUMBER"
		attribute "LatestTradingDay", "DATE"
		attribute "PreviousClose", "NUMBER"
		attribute "Change", "NUMBER"
		attribute "ChangePercent", "NUMBER"
	}
}

preferences {
	section("URIs") {
		input name: "ApiKey", type: "text", title: "API Key", required: true
        input name: "Symbol", type: "text", title: "Symbol", required: true
        input name: 'updateMins', type: 'enum', description: "Select the update frequency", title: "Update frequency (minutes)\n0 is disabled", defaultValue: '30', options: ['0','5','10','15','30'], required: true
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
	if (logEnable) log.debug "alphabantage polling..."
    
    def url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${Symbol}&apikey=${ApiKey}"
    
	try {
		httpGet(url) { resp -> 
			if (logEnable) log.debug resp.getData()
			
            def respValues = resp.data["Global Quote"]
            sendEvent(name: "Symbol", value: respValues["01. symbol"])
            sendEvent(name: "Open", value: respValues["02. open"])
            sendEvent(name: "High", value: respValues["03. high"])
			sendEvent(name: "Low", value: respValues["04. low"])
			sendEvent(name: "Price", value: respValues["05. price"])
            sendEvent(name: "Volume", value: respValues["06. volume"])
            sendEvent(name: "LatestTradingDay", value: respValues["07. latest trading day"])
            sendEvent(name: "PreviousClose", value: respValues["08. previous close"])
            sendEvent(name: "Change", value: respValues["09. change"])
			sendEvent(name: "ChangePercent", value: respValues["10. change percent"])
		}
	} catch(Exception e) {
		log.debug "error occured calling httpget ${e}"
	}
}
