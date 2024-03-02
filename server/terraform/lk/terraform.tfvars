deployment_location = "us-west-2"
debug               = true
ip_prefix           = "10.0"
domain_name_zone    = "cs.lightningkite.com"
domain_name         = "rocktemplateapi.cs.lightningkite.com"
cors                = {
  allowedDomains = ["*"]
  allowedHeaders = ["*"]
}
display_name = "Rock Template"
sms          = { "url" : "console", "from" : null }
logging      = {
  "default" : { "filePattern" : null, "toConsole" : true, "level" : "INFO", "additive" : false }, "logger" : null
}
database_org_id   = "6323a65c43d66b56a2ea5aea"
files_expiry      = "P1D"
metrics_tracked   = ["Execution Time", "Health Checks Run"]
metrics_namespace = "ilusso"
exceptions        = {
  "url" : "sentry://https://ac54afbd919076c39a7b5d3f3cafe951@sentry24.lightningkite.com/6"
}
reporting_email                        = "joseph@lightningkite.com"
emergencyInvocationsPerMinuteThreshold = 100
emergencyComputePerMinuteThreshold     = 13800  // $10/month
panicInvocationsPerMinuteThreshold     = 500
panicComputePerMinuteThreshold         = 138000  // $100/month
emergencyContact                       = "joseph@lightningkite.com"
lambda_memory_size                     = 1024
lambda_timeout                         = 500
lambda_snapstart                       = true

serveApp   = null
frontend = "https://rocktemplate.cs.lightningkite.com"