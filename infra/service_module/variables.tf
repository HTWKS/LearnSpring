variable "alb_listener_arn" { type = string }
variable "path_pattern" { type = string }
variable "service_name" { type = string }
variable "ecs_task_execution_role_arn" { type = string }
variable "container_port" { type = number } // TODO: does it make sense for this to be configurable? I think only 80 is open on the ALB
variable "ecs_cluster_id" { type = string }
variable "private_subnets" { type = list(string) }
variable "vpc_id" { type = string }
variable "container_image" { type = string }
variable "alb_rule_priority" { type = number }
variable "environment_variables" {
  type    = list(map(string))
  default = []
}
