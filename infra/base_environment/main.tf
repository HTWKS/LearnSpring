terraform {
  required_version = ">= 1.5.7"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.16.1"
    }
  }

  backend "s3" {
    key            = "base_environment"
    bucket         = "learn-spring-160071257600"
    dynamodb_table = "learn-spring-terraform-lock"
    region         = "ap-southeast-1"
  }
}