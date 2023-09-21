// not really testing the module itself but just proof that we can register services under the base path as well
resource "aws_lb_listener_rule" "some_service_under_base_path" {
  priority     = 9999
  listener_arn = data.terraform_remote_state.base_environment.outputs.alb_listener_arn
  action {
    fixed_response {
      content_type = "text/plain"
      message_body = "hello from some service under base path"
      status_code  = "200"
    }
    type = "fixed-response"
  }
  condition {
    path_pattern {
      values = ["/*"]
    }
  }
}
