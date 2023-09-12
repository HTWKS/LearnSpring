data "aws_subnet" "ap-southeast-1a" {
  provider = aws.ap-southeast-1
  id       = var.subnet-id-24-ap-southeast-1a
}

resource "aws_security_group" "localhost-web-traffic-inbound" {
  provider    = aws.ap-southeast-1
  description = "Allow port 8080 for local host of spring"
  egress      = []
  ingress     = [
    {
      cidr_blocks = [
        "0.0.0.0/0",
      ]
      description      = "Localhost Spring fw"
      from_port        = 8080
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      protocol         = "tcp"
      security_groups  = []
      self             = false
      to_port          = 8080
    },
    {
      cidr_blocks      = []
      description      = "Localhost Spring fw"
      from_port        = 8080
      ipv6_cidr_blocks = [
        "::/0",
      ]
      prefix_list_ids = []
      protocol        = "tcp"
      security_groups = []
      self            = false
      to_port         = 8080
    },
  ]
  name   = "webLocal"
  vpc_id = aws_default_vpc.my_vpc.id
  revoke_rules_on_delete = false
}

resource "aws_default_vpc" "my_vpc" {
  provider             = aws.ap-southeast-1
  enable_dns_hostnames = false
  force_destroy        = false
  tags                 = {
    "Name" = "my-vpc"
  }
}

resource "aws_ecrpublic_repository" "ecr_repo" {
  provider        = aws.us-east-1
  repository_name = "docker_spring_demo"
}

resource "aws_iam_role" "aws_ecs_task_execution_role" {
  provider           = aws.ap-southeast-1
  name               = "ecsTaskExecutionRole"
  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action    = "sts:AssumeRole"
          Effect    = "Allow"
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          }
          Sid = ""
        },
      ]
      Version = "2008-10-17"
    }
  )
}