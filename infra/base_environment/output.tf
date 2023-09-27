output "alb_base_url" {
  value = "https://${local.host}"
}

output "alb_listener_arn" {
  value = aws_alb_listener.https.arn
}

output "ecs_task_execution_role_arn" {
  value = aws_iam_role.ecs_task_execution_role.arn
}

output "ecs_cluster_id" {
  value = aws_ecs_cluster.ecs.id
}

output "private_subnets" {
  value = module.vpc.private_subnets
}

output "vpc_id" {
  value = module.vpc.vpc_id
}

output "hostname" {
  value = local.host
}