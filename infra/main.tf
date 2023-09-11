data "aws_iam_role" "ecs_task_execution_role" {
  provider = aws.ap-southeast-1
  name     = "ecsTaskExecutionRole"
}

resource "aws_default_vpc" "my_vpc" {
  provider = aws.ap-southeast-1
}

resource "aws_default_security_group" "sec_group" {
  provider = aws.ap-southeast-1
}

resource "aws_ecrpublic_repository" "ecr_repo" {
  provider        = aws.us-east-1
  repository_name = "docker_spring_demo"
}

resource "aws_ecs_cluster" "ecs_cluster" {
  provider = aws.ap-southeast-1
  name     = "spring-demo-cluster"
}

resource "aws_ecs_service" "web_ecs_service" {
  name                  = "spring_demo_task"
  provider              = aws.ap-southeast-1
  desired_count         = 1
  task_definition       = aws_ecs_task_definition.ecr_task.arn
  wait_for_steady_state = "false"
  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }
}
resource "aws_ecs_task_definition" "ecr_task" {
  family   = "docker_spring_demo_task"
  provider = aws.ap-southeast-1
  runtime_platform {
    cpu_architecture        = "ARM64"
    operating_system_family = "LINUX"
  }
  cpu                      = "256"
  memory                   = "512"
  skip_destroy             = "false"
  requires_compatibilities = [
    "FARGATE"
  ]
  network_mode          = "awsvpc"
  execution_role_arn    = data.aws_iam_role.ecs_task_execution_role.arn
  container_definitions = jsonencode([
    {
      cpu : 256,
      environment : [],
      environmentFiles : [],
      essential : true,
      image : aws_ecrpublic_repository.ecr_repo.repository_uri,
      logConfiguration : {
        logDriver : "awslogs",
        options : {
          "awslogs-create-group" : "true",
          "awslogs-group" : "/ecs/docker_spring_demo_task",
          "awslogs-region" : var.singapore-aws-provider,
          "awslogs-stream-prefix" : "ecs"
        },
        secretOptions : []
      },
      memory : 512,
      memoryReservation : 256,
      mountPoints : [],
      name : aws_ecrpublic_repository.ecr_repo.repository_name,
      portMappings : [
        {
          appProtocol : "http",
          containerPort : 8080,
          hostPort : 8080,
          name : "docker_spring_demo-8080-tcp",
          protocol : "tcp"
        }
      ],
      ulimits : [],
      volumesFrom : []
    }
  ])
}