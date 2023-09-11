provider "aws" {
  alias = "us-east-1"
  region = "us-east-1"
}

provider "aws" {
  alias = "ap-southeast-1"
  region = "ap-southeast-1"
}


resource "aws_ecrpublic_repository" "ecr_repo" {
  provider = aws.us-east-1
  repository_name = "docker_spring_demo"
}