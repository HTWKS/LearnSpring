resource "aws_ecs_task_definition" "service" {
  network_mode             = "awsvpc"
  family                   = var.service_name
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = var.ecs_task_execution_role_arn
  container_definitions = jsonencode([
    {
      name      = var.service_name
      image     = var.container_image
      essential = true
      portMappings = [
        {
          protocol      = "tcp"
          containerPort = var.container_port
          hostPort      = var.container_port
      }]
      environment = var.environment_variables
      logConfiguration = {
        logDriver = "awslogs",
        options = {
          awslogs-group         = aws_cloudwatch_log_group.server.name,
          awslogs-region        = data.aws_region.current.name,
          awslogs-stream-prefix = var.service_name
        }
      }
  }])
}
