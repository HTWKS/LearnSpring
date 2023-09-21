resource "aws_cloudwatch_log_group" "server" {
  name              = var.service_name
  retention_in_days = 14
}
