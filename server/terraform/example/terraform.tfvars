deployment_location                    = "us-west-2"
debug                                  = false
ip_prefix                              = "10.0"
domain_name_zone                       = null
domain_name                            = null
cors                                   = null
display_name                           = "hammerprice"
database_min_capacity                  = 0.5
database_max_capacity                  = 2
database_auto_pause                    = true
sms                                    = { "url" : "console", "from" : null }
logging                                = {
  "default" : { "filePattern" : null, "toConsole" : true, "level" : "INFO", "additive" : false }, "logger" : null
}
files_expiry                           = "P1D"
metrics_tracked                        = ["Health Checks Run", "Execution Time"]
metrics_namespace                      = "hammerprice"
exchangeRateApi                        = { "fixerKey" : null }
exceptions                             = { "url" : "none", "sentryDsn" : null }
reporting_email                        = null
notifications                          = { "implementation" : "console", "credentials" : null }
emergencyInvocationsPerMinuteThreshold = 100
emergencyComputePerMinuteThreshold     = 10000
panicInvocationsPerMinuteThreshold     = 500
panicComputePerMinuteThreshold         = 50000
emergencyContact                       = null
lambda_memory_size                     = 1024
lambda_timeout                         = 30
lambda_snapstart                       = false