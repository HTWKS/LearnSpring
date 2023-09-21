resource "aws_ecs_cluster" "ecs" {
  name = local.resource_name_prefix
}

resource "aws_ecs_cluster_capacity_providers" "ecs_provider" {
  cluster_name = aws_ecs_cluster.ecs.name
  capacity_providers = ["FARGATE"]
}

data "aws_iam_policy_document" "ecs_assume_role_policy" {
  statement {
    sid     = ""
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name               = "${local.resource_name_prefix}-ecs-task-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "ecs-task-execution-role-policy-attachment" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
