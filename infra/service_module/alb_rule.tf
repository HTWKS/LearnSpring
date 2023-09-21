resource "aws_alb_target_group" "service" {
  port        = var.container_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  deregistration_delay = 10

  health_check {
    healthy_threshold   = "3"
    interval            = "30"
    protocol            = "HTTP"
    matcher             = "200"
    timeout             = "3"
    path                = "/"
    unhealthy_threshold = "2"
  }

  tags = {
    Service = var.service_name
  }
}


resource "aws_lb_listener_rule" "service" {
  priority     = var.alb_rule_priority
  listener_arn = var.alb_listener_arn
  action {
    target_group_arn = aws_alb_target_group.service.arn
    type             = "forward"
  }
  condition {
    path_pattern {
      values = [var.path_pattern]
    }
  }
}
