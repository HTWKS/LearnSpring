output "some_service_url" {
  value = "${data.terraform_remote_state.base_environment.outputs.alb_base_url}/some_service/"
}
output "base_url" {
  value = data.terraform_remote_state.base_environment.outputs.alb_base_url
}
output "some_service_log_group_name" {
  value = module.some_service.log_group_name
}