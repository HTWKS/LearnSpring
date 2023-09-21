resource "aws_ecrpublic_repository" "ecr_repository" {
  provider        = aws.us-east-1
  repository_name = "docker_spring_demo"
}