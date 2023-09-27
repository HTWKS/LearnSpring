module "some_service" {
  source = "../../../service_module"

  alb_listener_arn            = data.terraform_remote_state.base_environment.outputs.alb_listener_arn
  ecs_cluster_id              = data.terraform_remote_state.base_environment.outputs.ecs_cluster_id
  ecs_task_execution_role_arn = data.terraform_remote_state.base_environment.outputs.ecs_task_execution_role_arn

  vpc_id          = data.terraform_remote_state.base_environment.outputs.vpc_id
  private_subnets = data.terraform_remote_state.base_environment.outputs.private_subnets

  alb_rule_priority = 42
  path_pattern      = "/some_service/*"
  service_name      = "${terraform.workspace}-some-service"
  container_port    = 80
  container_image   = "nginx:stable"
}

